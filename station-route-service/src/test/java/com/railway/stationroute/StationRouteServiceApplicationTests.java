package com.railway.stationroute;

import com.railway.stationroute.dto.*;
import com.railway.stationroute.service.RouteService;
import com.railway.stationroute.service.StationService;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

@SpringBootTest
class StationRouteServiceApplicationTests {

    @Autowired
    private StationService stationService;

    @Autowired
    private RouteService routeService;

    @Test
    void testStationAndRouteLifecycle() {
        StationRequest stnReq = new StationRequest("SBC", "KSR Bengaluru", "Bengaluru", "Karnataka", true);
        StationResponse stn = stationService.createStation(stnReq);
        assertNotNull(stn);
        assertEquals("SBC", stn.getStationCode());

        RouteStationRequest stopReq = new RouteStationRequest();
        stopReq.setTrainId(99L);
        stopReq.setStationCode("SBC");
        stopReq.setStationName("KSR Bengaluru");
        stopReq.setStopSequence(1);
        stopReq.setDistanceFromOriginKm(0.0);
        stopReq.setSourceStation(true);

        RouteStationResponse stop = routeService.addRouteStop(stopReq);
        assertNotNull(stop);

        List<RouteStationResponse> route = routeService.getRouteByTrainId(99L);
        assertFalse(route.isEmpty());
        assertEquals("SBC", route.get(0).getStationCode());
    }
}