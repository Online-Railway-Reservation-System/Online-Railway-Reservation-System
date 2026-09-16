package com.railway.schedulefare.service;

import com.railway.schedulefare.dto.FareCalculateRequest;
import com.railway.schedulefare.dto.FareCalculateResponse;
import com.railway.schedulefare.entity.FareRule;
import com.railway.schedulefare.entity.TatkalConfig;
import com.railway.schedulefare.exception.ResourceNotFoundException;
import com.railway.schedulefare.repository.FareRuleRepository;
import com.railway.schedulefare.repository.TatkalConfigRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@Transactional
public class FareServiceImpl implements FareService {

    private final FareRuleRepository fareRuleRepository;
    private final TatkalConfigRepository tatkalConfigRepository;

    public FareServiceImpl(FareRuleRepository fareRuleRepository, TatkalConfigRepository tatkalConfigRepository) {
        this.fareRuleRepository = fareRuleRepository;
        this.tatkalConfigRepository = tatkalConfigRepository;
    }

    @Override
    @Transactional(readOnly = true)
    public FareCalculateResponse calculateFare(FareCalculateRequest request) {
        String classType = request.getClassType().toUpperCase().trim();
        String quota = request.getQuota() != null ? request.getQuota().toUpperCase().trim() : "GENERAL";
        double distance = request.getDistanceKm() != null && request.getDistanceKm() > 0 ? request.getDistanceKm() : 495.0;
        int count = request.getPassengerCount() != null && request.getPassengerCount() > 0 ? request.getPassengerCount() : 1;

        // Resolve fare rule for train and class, fallback to generic rule
        FareRule rule = fareRuleRepository.findByTrainIdAndClassType(request.getTrainId(), classType)
                .or(() -> fareRuleRepository.findTopByClassTypeAndTrainIdIsNull(classType))
                .orElseGet(() -> createDefaultRuleForClass(classType));

        double baseFare = distance * rule.getBaseFarePerKm();
        double reservationFee = rule.getReservationFee();
        double superfastCharge = rule.getSuperfastCharge();

        // Tatkal surcharge calculation
        double quotaSurcharge = 0.0;
        if ("TATKAL".equalsIgnoreCase(quota)) {
            TatkalConfig tatkalConfig = tatkalConfigRepository.findByTrainId(request.getTrainId())
                    .orElseGet(() -> defaultTatkalConfig(request.getTrainId()));
            if (tatkalConfig.isTatkalEnabled()) {
                double pct = tatkalConfig.getSurchargePercentage() / 100.0;
                quotaSurcharge = Math.max(100.0, Math.min(500.0, baseFare * pct));
            }
        }

        // Concession calculation (50% discount on base fare for all eligible concession categories)
        double concessionDiscount = 0.0;
        if (request.isConcessionVerified() && request.getConcessionType() != null) {
            String cType = request.getConcessionType().toUpperCase().trim();
            if ("DISABILITY".equals(cType) || "DISABLED".equals(cType)
                    || "SENIOR_CITIZEN".equals(cType)
                    || "STUDENT".equals(cType)
                    || "DEFENCE".equals(cType) || "GOVERNMENT_STAFF".equals(cType)) {
                concessionDiscount = baseFare * 0.50; // 50% discount
            }
        }

        double netBaseFare = Math.max(0.0, baseFare - concessionDiscount);
        double singleFare = Math.max(rule.getMinimumFare(), netBaseFare + reservationFee + superfastCharge + quotaSurcharge);
        // Round to whole rupee
        singleFare = Math.round(singleFare);
        double totalPayable = singleFare * count;

        String breakdown = String.format("Base: %.2f (Dist: %.1f km @ %.2f/km), Reservation: %.2f, Superfast: %.2f, Quota Surcharge: %.2f, Concession Discount: -%.2f => Per Passenger: %.2f x %d = %.2f",
                baseFare, distance, rule.getBaseFarePerKm(), reservationFee, superfastCharge, quotaSurcharge, concessionDiscount, singleFare, count, totalPayable);

        return new FareCalculateResponse(
                request.getTrainId(),
                classType,
                quota,
                baseFare,
                reservationFee,
                superfastCharge,
                quotaSurcharge,
                concessionDiscount,
                singleFare,
                count,
                totalPayable,
                breakdown
        );
    }

    @Override
    @Transactional(readOnly = true)
    public List<FareRule> getFareRulesByTrainId(Long trainId) {
        return fareRuleRepository.findByTrainId(trainId);
    }

    @Override
    public FareRule createFareRule(FareRule rule) {
        return fareRuleRepository.save(rule);
    }

    @Override
    @Transactional(readOnly = true)
    public TatkalConfig getTatkalConfig(Long trainId) {
        return tatkalConfigRepository.findByTrainId(trainId)
                .orElseGet(() -> defaultTatkalConfig(trainId));
    }

    @Override
    public TatkalConfig saveTatkalConfig(TatkalConfig config) {
        return tatkalConfigRepository.save(config);
    }

    private FareRule createDefaultRuleForClass(String classType) {
        FareRule r = new FareRule();
        r.setClassType(classType);
        switch (classType) {
            case "1A" -> { r.setBaseFarePerKm(3.0); r.setMinimumFare(700.0); r.setReservationFee(60.0); r.setSuperfastCharge(75.0); }
            case "2A" -> { r.setBaseFarePerKm(2.0); r.setMinimumFare(450.0); r.setReservationFee(50.0); r.setSuperfastCharge(45.0); }
            case "3A" -> { r.setBaseFarePerKm(1.4); r.setMinimumFare(300.0); r.setReservationFee(40.0); r.setSuperfastCharge(45.0); }
            case "CC" -> { r.setBaseFarePerKm(1.2); r.setMinimumFare(200.0); r.setReservationFee(40.0); r.setSuperfastCharge(45.0); }
            case "SL" -> { r.setBaseFarePerKm(0.8); r.setMinimumFare(140.0); r.setReservationFee(20.0); r.setSuperfastCharge(30.0); }
            default   -> { r.setBaseFarePerKm(0.5); r.setMinimumFare(60.0);  r.setReservationFee(15.0); r.setSuperfastCharge(15.0); }
        }
        return r;
    }

    private TatkalConfig defaultTatkalConfig(Long trainId) {
        TatkalConfig tc = new TatkalConfig();
        tc.setTrainId(trainId);
        tc.setTatkalEnabled(true);
        tc.setAdvanceDays(1);
        tc.setAcOpeningTime("10:00");
        tc.setNonAcOpeningTime("11:00");
        tc.setSurchargePercentage(30.0);
        return tc;
    }
}