package com.railway.search;

import com.railway.search.dto.TrainSearchResult;
import com.railway.search.service.SearchService;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import java.time.LocalDate;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

@SpringBootTest
class SearchServiceApplicationTests {

    @Autowired
    private SearchService searchService;

    @Test
    void testTrainSearchAggregation() {
        List<TrainSearchResult> results = searchService.searchTrains("MAS", "CBE", LocalDate.now().plusDays(3), "SL", "GENERAL");
        assertNotNull(results);
        assertFalse(results.isEmpty());

        TrainSearchResult res = results.get(0);
        assertEquals("MAS", res.getSource());
        assertEquals("CBE", res.getDestination());
        assertTrue(res.getFare() > 0);
        assertTrue(res.getAvailableSeats() > 0);
    }

    @Test
    void testTatkalSearchAggregation() {
        List<TrainSearchResult> results = searchService.searchTrains("KHI", "LHE", LocalDate.now().plusDays(1), "3A", "TATKAL");
        assertNotNull(results);
        assertFalse(results.isEmpty());

        TrainSearchResult res = results.get(0);
        assertEquals("TATKAL", res.getQuota());
        assertTrue(res.isTatkalAvailable());
        assertEquals("10:00 AM", res.getTatkalOpeningTime());
        assertTrue(res.getFare() > 0);
    }

    @Test
    void testDepartedTrainCutoffAndNextDay() {
        // Querying yesterday's date guarantees train has already departed
        LocalDate pastDate = LocalDate.now().minusDays(1);
        List<TrainSearchResult> results = searchService.searchTrains("KHI", "LHE", pastDate, "SL", "GENERAL");
        assertNotNull(results);
        assertFalse(results.isEmpty());

        TrainSearchResult res = results.get(0);
        assertTrue(res.isDeparted());
        assertFalse(res.isBookingOpen());
        assertEquals("DEPARTED", res.getStatus());
        assertEquals(0L, res.getAvailableSeats());
        assertNotNull(res.getNextAvailableDate());
        assertEquals(pastDate.plusDays(1).toString(), res.getNextAvailableDate());
        assertNotNull(res.getNextDayAvailableSeats());
    }
}