package com.railway.inventoryquota.service;

import com.railway.inventoryquota.dto.*;
import com.railway.inventoryquota.entity.Coach;
import com.railway.inventoryquota.entity.SeatInventory;
import com.railway.inventoryquota.entity.SeatLegBooking;
import com.railway.inventoryquota.exception.BadRequestException;
import com.railway.inventoryquota.exception.SeatUnavailableException;
import com.railway.inventoryquota.repository.CoachRepository;
import com.railway.inventoryquota.repository.SeatInventoryRepository;
import com.railway.inventoryquota.repository.SeatLegBookingRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.*;
import java.util.stream.Collectors;

@Service
@Transactional
public class InventoryServiceImpl implements InventoryService {

    private static final Logger log = LoggerFactory.getLogger(InventoryServiceImpl.class);

    private final SeatInventoryRepository seatRepository;
    private final CoachRepository coachRepository;
    private final SeatLegBookingRepository legBookingRepository;

    public InventoryServiceImpl(SeatInventoryRepository seatRepository,
                                CoachRepository coachRepository,
                                SeatLegBookingRepository legBookingRepository) {
        this.seatRepository = seatRepository;
        this.coachRepository = coachRepository;
        this.legBookingRepository = legBookingRepository;
    }

    @Override
    public AvailabilityResponse getAvailability(Long trainId, LocalDate journeyDate, String classType, String quota) {
        return getAvailability(trainId, journeyDate, classType, quota, null, null, 1, 999);
    }

    @Override
    public AvailabilityResponse getAvailability(Long trainId, LocalDate journeyDate, String classType, String quota,
                                         String fromStationCode, String toStationCode, Integer fromStopSeq, Integer toStopSeq) {
        String cType = classType.toUpperCase().trim();
        String qType = quota != null ? quota.toUpperCase().trim() : "GENERAL";
        int fromSeq = fromStopSeq != null ? fromStopSeq : 1;
        int toSeq = toStopSeq != null ? toStopSeq : 999;

        ensureInventoryInitialized(trainId, journeyDate, cType);

        List<SeatInventory> totalSeats = resolveOrPartitionSeats(trainId, journeyDate, cType, qType);

        // Fetch occupied seat numbers for this exact train leg
        Set<String> occupiedSeatNums = legBookingRepository.findOccupiedSeatNumbers(
                trainId, journeyDate, cType, qType, fromSeq, toSeq, LocalDateTime.now()
        );

        long availableCount = totalSeats.stream()
                .filter(s -> !occupiedSeatNums.contains(s.getSeatNumber()))
                .count();

        String status = availableCount > 0 ? "AVAILABLE" : "WAITLISTED";

        return new AvailabilityResponse(
                trainId, journeyDate, cType, qType, availableCount, status,
                fromStationCode, toStationCode, fromSeq, toSeq
        );
    }

    @Override
    @Transactional(readOnly = true)
    public List<SeatInventory> getSeatsLayout(Long trainId, LocalDate journeyDate, String classType) {
        return seatRepository.findByTrainIdAndJourneyDateAndClassType(trainId, journeyDate, classType.toUpperCase());
    }

    @Override
    public synchronized SeatHoldResponse holdSeats(SeatHoldRequest request) {
        Long trainId = request.getTrainId();
        LocalDate journeyDate = request.getJourneyDate();
        String classType = request.getClassType().toUpperCase().trim();
        String quotaType = request.getQuota() != null ? request.getQuota().toUpperCase().trim() : "GENERAL";
        int seatCount = request.getSeatCount();
        int fromSeq = request.getFromStopSeq() != null ? request.getFromStopSeq() : 1;
        int toSeq = request.getToStopSeq() != null ? request.getToStopSeq() : 999;

        if (fromSeq >= toSeq) {
            throw new BadRequestException("Departure stop sequence (" + fromSeq + ") must be before arrival stop sequence (" + toSeq + ")");
        }

        ensureInventoryInitialized(trainId, journeyDate, classType);

        String holdRef = request.getHoldReference() != null && !request.getHoldReference().isBlank()
                ? request.getHoldReference()
                : "HOLD-" + UUID.randomUUID().toString().substring(0, 10).toUpperCase();

        LocalDateTime expiry = LocalDateTime.now().plusMinutes(request.getHoldDurationMinutes());

        // Find all physical seats for this class & quota
        List<SeatInventory> totalSeats = resolveOrPartitionSeats(trainId, journeyDate, classType, quotaType);

        // Find seats currently occupied on ANY overlapping leg
        Set<String> occupiedSeatNums = legBookingRepository.findOccupiedSeatNumbers(
                trainId, journeyDate, classType, quotaType, fromSeq, toSeq, LocalDateTime.now()
        );

        // Filter available candidate seats
        List<SeatInventory> availableSeats = totalSeats.stream()
                .filter(s -> !occupiedSeatNums.contains(s.getSeatNumber()))
                .limit(seatCount)
                .collect(Collectors.toList());

        if (availableSeats.size() < seatCount) {
            throw new SeatUnavailableException("Requested " + seatCount + " seats, but only " +
                    availableSeats.size() + " seats are available in " + classType + " (" + quotaType + ") between stop sequence " + fromSeq + " and " + toSeq);
        }

        List<Long> heldIds = new ArrayList<>();
        List<String> seatNums = new ArrayList<>();
        List<String> berthTypes = new ArrayList<>();
        List<SeatLegBooking> legBookingsToSave = new ArrayList<>();

        for (SeatInventory seat : availableSeats) {
            heldIds.add(seat.getId());
            seatNums.add(seat.getSeatNumber());
            berthTypes.add(seat.getBerthType());

            SeatLegBooking legBooking = new SeatLegBooking(
                    trainId,
                    journeyDate,
                    seat.getCoachNumber(),
                    seat.getSeatNumber(),
                    classType,
                    quotaType,
                    request.getFromStationCode(),
                    request.getToStationCode(),
                    fromSeq,
                    toSeq,
                    "HELD",
                    holdRef,
                    expiry
            );
            legBookingsToSave.add(legBooking);
        }

        legBookingRepository.saveAll(legBookingsToSave);

        return new SeatHoldResponse(
                true,
                holdRef,
                heldIds,
                seatNums,
                berthTypes,
                expiry,
                "Held " + seatCount + " seats successfully for segment " + fromSeq + " -> " + toSeq + " until " + expiry
        );
    }

    @Override
    public SeatConfirmResponse confirmSeats(SeatConfirmRequest request) {
        List<SeatLegBooking> heldLegs = legBookingRepository.findByHoldReference(request.getHoldReference());
        if (heldLegs.isEmpty()) {
            // Also check physical seat repository fallback
            List<SeatInventory> fallbackSeats = seatRepository.findByHoldReference(request.getHoldReference());
            if (fallbackSeats.isEmpty()) {
                throw new BadRequestException("No held seats found for reference: " + request.getHoldReference());
            }
            List<String> confirmedNums = new ArrayList<>();
            for (SeatInventory s : fallbackSeats) {
                s.setStatus("CONFIRMED");
                s.setReservationId(request.getReservationId());
                s.setPnr(request.getPnr());
                s.setHoldReference(null);
                s.setHoldExpiry(null);
                confirmedNums.add(s.getSeatNumber());
            }
            seatRepository.saveAll(fallbackSeats);
            return new SeatConfirmResponse(true, request.getPnr(), request.getReservationId(), confirmedNums, "Seats confirmed successfully");
        }

        List<String> confirmedNums = new ArrayList<>();
        for (SeatLegBooking leg : heldLegs) {
            leg.setStatus("CONFIRMED");
            leg.setReservationId(request.getReservationId());
            leg.setPnr(request.getPnr());
            leg.setHoldReference(null);
            leg.setHoldExpiry(null);
            confirmedNums.add(leg.getSeatNumber());
        }

        legBookingRepository.saveAll(heldLegs);

        return new SeatConfirmResponse(true, request.getPnr(), request.getReservationId(), confirmedNums, "Seats confirmed successfully");
    }

    @Override
    public SeatReleaseResponse releaseSeats(SeatReleaseRequest request) {
        int releasedCount = 0;

        if (request.getHoldReference() != null && !request.getHoldReference().isBlank()) {
            List<SeatLegBooking> legs = legBookingRepository.findByHoldReference(request.getHoldReference());
            releasedCount += legs.size();
            legBookingRepository.deleteAll(legs);

            List<SeatInventory> seats = seatRepository.findByHoldReference(request.getHoldReference());
            for (SeatInventory s : seats) {
                s.setStatus("AVAILABLE");
                s.setHoldReference(null);
                s.setHoldExpiry(null);
            }
            seatRepository.saveAll(seats);
        }

        if (request.getPnr() != null && !request.getPnr().isBlank()) {
            List<SeatLegBooking> legs = legBookingRepository.findByPnr(request.getPnr());
            releasedCount += legs.size();
            legBookingRepository.deleteAll(legs);

            List<SeatInventory> seats = seatRepository.findByPnr(request.getPnr());
            for (SeatInventory s : seats) {
                s.setStatus("AVAILABLE");
                s.setPnr(null);
                s.setReservationId(null);
            }
            seatRepository.saveAll(seats);
        }

        if (request.getSeatIds() != null && !request.getSeatIds().isEmpty()) {
            List<SeatInventory> seats = seatRepository.findAllById(request.getSeatIds());
            for (SeatInventory s : seats) {
                s.setStatus("AVAILABLE");
                s.setHoldReference(null);
                s.setHoldExpiry(null);
                s.setReservationId(null);
                s.setPnr(null);
            }
            seatRepository.saveAll(seats);
            releasedCount += seats.size();
        }

        return new SeatReleaseResponse(true, releasedCount, "Released " + releasedCount + " seats to available inventory");
    }

    @Override
    @Scheduled(fixedRate = 60000)
    public void releaseExpiredHolds() {
        LocalDateTime now = LocalDateTime.now();
        List<SeatLegBooking> expiredLegs = legBookingRepository.findExpiredHolds(now);
        if (!expiredLegs.isEmpty()) {
            log.info("Releasing {} expired seat leg holds", expiredLegs.size());
            legBookingRepository.deleteAll(expiredLegs);
        }

        List<SeatInventory> expiredSeats = seatRepository.findExpiredHolds(now);
        if (!expiredSeats.isEmpty()) {
            for (SeatInventory s : expiredSeats) {
                s.setStatus("AVAILABLE");
                s.setHoldReference(null);
                s.setHoldExpiry(null);
            }
            seatRepository.saveAll(expiredSeats);
        }
    }

    private synchronized void ensureInventoryInitialized(Long trainId, LocalDate journeyDate, String classType) {
        long existingCount = seatRepository.countByTrainIdAndJourneyDateAndClassTypeAndQuotaType(
                trainId, journeyDate, classType, "GENERAL"
        );

        if (existingCount > 0) {
            return;
        }

        // Initialize coach and seat layout
        List<Coach> coaches = coachRepository.findByTrainIdAndClassTypeAndActiveStatusTrue(trainId, classType);
        if (coaches.isEmpty()) {
            // Create default coach if not exists
            String cNum = switch (classType) {
                case "1A" -> "H1";
                case "2A" -> "A1";
                case "3A" -> "B1";
                case "CC" -> "C1";
                default   -> "S1";
            };
            Coach defaultCoach = new Coach(null, trainId, cNum, classType, 72, true);
            coaches = List.of(coachRepository.save(defaultCoach));
        }

        List<SeatInventory> seatsToCreate = new ArrayList<>();
        String[] berths = {"LOWER", "MIDDLE", "UPPER", "LOWER", "MIDDLE", "UPPER", "SIDE_LOWER", "SIDE_UPPER"};

        for (Coach c : coaches) {
            for (int i = 1; i <= c.getTotalSeats(); i++) {
                SeatInventory seat = new SeatInventory();
                seat.setTrainId(trainId);
                seat.setJourneyDate(journeyDate);
                seat.setCoachNumber(c.getCoachNumber());
                seat.setSeatNumber(c.getCoachNumber() + "-" + i);
                seat.setClassType(classType);
                seat.setBerthType(berths[(i - 1) % berths.length]);

                // Allocation: first 70% GENERAL, next 20% TATKAL, last 10% LADIES
                if (i <= c.getTotalSeats() * 0.70) {
                    seat.setQuotaType("GENERAL");
                } else if (i <= c.getTotalSeats() * 0.90) {
                    seat.setQuotaType("TATKAL");
                } else {
                    seat.setQuotaType("LADIES");
                }

                seat.setStatus("AVAILABLE");
                seatsToCreate.add(seat);
            }
        }

        seatRepository.saveAll(seatsToCreate);
    }

    private synchronized List<SeatInventory> resolveOrPartitionSeats(Long trainId, LocalDate journeyDate, String classType, String quotaType) {
        List<SeatInventory> seats = seatRepository
                .findByTrainIdAndJourneyDateAndClassTypeAndQuotaTypeOrderBySeatNumberAsc(trainId, journeyDate, classType, quotaType);
        if (seats.isEmpty() && !"GENERAL".equalsIgnoreCase(quotaType)) {
            List<SeatInventory> allSeats = seatRepository.findByTrainIdAndJourneyDateAndClassType(trainId, journeyDate, classType);
            if (!allSeats.isEmpty()) {
                // Partition seats: 70% GENERAL, 20% TATKAL, 10% LADIES
                for (int i = 0; i < allSeats.size(); i++) {
                    SeatInventory s = allSeats.get(i);
                    String assigned = (i < allSeats.size() * 0.70) ? "GENERAL" : ((i < allSeats.size() * 0.90) ? "TATKAL" : "LADIES");
                    s.setQuotaType(assigned);
                }
                seatRepository.saveAll(allSeats);
                seats = seatRepository.findByTrainIdAndJourneyDateAndClassTypeAndQuotaTypeOrderBySeatNumberAsc(trainId, journeyDate, classType, quotaType);
            }
        }
        return seats;
    }
}