package com.railway.inventoryquota;

import com.railway.inventoryquota.dto.*;
import com.railway.inventoryquota.service.InventoryService;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import java.time.LocalDate;

import static org.junit.jupiter.api.Assertions.*;

@SpringBootTest
class InventoryQuotaServiceApplicationTests {

    @Autowired
    private InventoryService inventoryService;

    @Test
    void testSeatLifecycleHoldConfirmRelease() {
        LocalDate date = LocalDate.now().plusDays(5);

        AvailabilityResponse avail = inventoryService.getAvailability(1L, date, "SL", "GENERAL");
        assertNotNull(avail);
        assertTrue(avail.getAvailableSeats() > 0);
        long initialSeats = avail.getAvailableSeats();

        // Hold 2 seats
        SeatHoldRequest holdReq = new SeatHoldRequest();
        holdReq.setTrainId(1L);
        holdReq.setJourneyDate(date);
        holdReq.setClassType("SL");
        holdReq.setQuota("GENERAL");
        holdReq.setSeatCount(2);

        SeatHoldResponse holdResp = inventoryService.holdSeats(holdReq);
        assertNotNull(holdResp);
        assertTrue(holdResp.isSuccess());
        assertEquals(2, holdResp.getHeldSeatIds().size());

        // Check availability reduced by 2
        AvailabilityResponse afterHold = inventoryService.getAvailability(1L, date, "SL", "GENERAL");
        assertEquals(initialSeats - 2, afterHold.getAvailableSeats());

        // Confirm seats
        SeatConfirmRequest confReq = new SeatConfirmRequest(holdResp.getHoldReference(), 1001L, "4527819365");
        SeatConfirmResponse confResp = inventoryService.confirmSeats(confReq);
        assertNotNull(confResp);
        assertTrue(confResp.isSuccess());
        assertEquals(2, confResp.getConfirmedSeats().size());

        // Release seats by PNR (e.g. cancellation)
        SeatReleaseRequest relReq = new SeatReleaseRequest(null, "4527819365", null);
        SeatReleaseResponse relResp = inventoryService.releaseSeats(relReq);
        assertNotNull(relResp);
        assertEquals(2, relResp.getReleasedCount());

        // Verify seats returned to available
        AvailabilityResponse afterRelease = inventoryService.getAvailability(1L, date, "SL", "GENERAL");
        assertEquals(initialSeats, afterRelease.getAvailableSeats());
    }

    @Test
    void testDynamicLegBasedSeatReleaseAndReuse() {
        LocalDate date = LocalDate.now().plusDays(8);

        // Check initial availability for Train 2 in 3A
        AvailabilityResponse initialLeg1 = inventoryService.getAvailability(2L, date, "3A", "GENERAL", "KHI", "HYD", 1, 2);
        AvailabilityResponse initialLeg2 = inventoryService.getAvailability(2L, date, "3A", "GENERAL", "HYD", "LHE", 2, 4);

        long totalSeatsLeg1 = initialLeg1.getAvailableSeats();
        long totalSeatsLeg2 = initialLeg2.getAvailableSeats();
        assertTrue(totalSeatsLeg1 > 0);
        assertEquals(totalSeatsLeg1, totalSeatsLeg2);

        // 1. Passenger 1 books for Station 1 to 2 (Leg 1: 1 -> 2)
        SeatHoldRequest holdLeg1 = new SeatHoldRequest();
        holdLeg1.setTrainId(2L);
        holdLeg1.setJourneyDate(date);
        holdLeg1.setClassType("3A");
        holdLeg1.setQuota("GENERAL");
        holdLeg1.setSeatCount(1);
        holdLeg1.setFromStationCode("KHI");
        holdLeg1.setToStationCode("HYD");
        holdLeg1.setFromStopSeq(1);
        holdLeg1.setToStopSeq(2);

        SeatHoldResponse resp1 = inventoryService.holdSeats(holdLeg1);
        assertTrue(resp1.isSuccess());
        String allocatedSeatP1 = resp1.getSeatNumbers().get(0);

        inventoryService.confirmSeats(new SeatConfirmRequest(resp1.getHoldReference(), 201L, "PNR1111111111"));

        // 2. Verify Leg 1 (1->2) available seats decreased by 1
        AvailabilityResponse afterP1Leg1 = inventoryService.getAvailability(2L, date, "3A", "GENERAL", "KHI", "HYD", 1, 2);
        assertEquals(totalSeatsLeg1 - 1, afterP1Leg1.getAvailableSeats());

        // 3. CRITICAL REQUIREMENT VERIFICATION:
        // After Station 2 (HYD), that seat is RELEASED and available for passengers from Station 2 to 4 (Leg 2: 2 -> 4)
        AvailabilityResponse afterP1Leg2 = inventoryService.getAvailability(2L, date, "3A", "GENERAL", "HYD", "LHE", 2, 4);
        assertEquals(totalSeatsLeg2, afterP1Leg2.getAvailableSeats(), "Seat must be released and available for customers boarding at station 2 onwards");

        // 4. Passenger 2 books for Station 2 to 4 (Leg 2: 2 -> 4)
        SeatHoldRequest holdLeg2 = new SeatHoldRequest();
        holdLeg2.setTrainId(2L);
        holdLeg2.setJourneyDate(date);
        holdLeg2.setClassType("3A");
        holdLeg2.setQuota("GENERAL");
        holdLeg2.setSeatCount(1);
        holdLeg2.setFromStationCode("HYD");
        holdLeg2.setToStationCode("LHE");
        holdLeg2.setFromStopSeq(2);
        holdLeg2.setToStopSeq(4);

        SeatHoldResponse resp2 = inventoryService.holdSeats(holdLeg2);
        assertTrue(resp2.isSuccess());
        inventoryService.confirmSeats(new SeatConfirmRequest(resp2.getHoldReference(), 202L, "PNR2222222222"));

        // Now Leg 2 is also booked
        AvailabilityResponse afterP2Leg2 = inventoryService.getAvailability(2L, date, "3A", "GENERAL", "HYD", "LHE", 2, 4);
        assertEquals(totalSeatsLeg2 - 1, afterP2Leg2.getAvailableSeats());
    }
}