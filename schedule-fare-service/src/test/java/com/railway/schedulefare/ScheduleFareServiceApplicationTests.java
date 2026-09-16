package com.railway.schedulefare;

import com.railway.schedulefare.dto.*;
import com.railway.schedulefare.service.FareService;
import com.railway.schedulefare.service.ScheduleService;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import static org.junit.jupiter.api.Assertions.*;

@SpringBootTest
class ScheduleFareServiceApplicationTests {

    @Autowired
    private ScheduleService scheduleService;

    @Autowired
    private FareService fareService;

    @Test
    void testFareCalculationAndTatkal() {
        FareCalculateRequest req = new FareCalculateRequest();
        req.setTrainId(1L);
        req.setSourceStationCode("MAS");
        req.setDestinationStationCode("CBE");
        req.setClassType("SL");
        req.setQuota("GENERAL");
        req.setPassengerCount(2);
        req.setDistanceKm(495.0);

        FareCalculateResponse response = fareService.calculateFare(req);
        assertNotNull(response);
        assertEquals("SL", response.getClassType());
        assertEquals(2, response.getPassengerCount());
        assertTrue(response.getFinalPayableFare() > 0);

        // Test Tatkal surcharge
        req.setQuota("TATKAL");
        FareCalculateResponse tatkalResponse = fareService.calculateFare(req);
        assertTrue(tatkalResponse.getQuotaSurcharge() > 0);
        assertTrue(tatkalResponse.getFinalPayableFare() > response.getFinalPayableFare());

        // Test Verified Concession discount
        req.setQuota("GENERAL");
        req.setConcessionType("DISABILITY");
        req.setConcessionVerified(true);
        FareCalculateResponse concessionalResponse = fareService.calculateFare(req);
        assertTrue(concessionalResponse.getConcessionDiscount() > 0);
        assertTrue(concessionalResponse.getFinalPayableFare() < response.getFinalPayableFare());
    }
}