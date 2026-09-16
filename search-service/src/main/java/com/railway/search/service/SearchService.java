package com.railway.search.service;

import com.railway.search.dto.TrainSearchResult;
import java.time.LocalDate;
import java.util.List;

public interface SearchService {
    List<TrainSearchResult> searchTrains(String source, String destination, LocalDate journeyDate, String classType, String quota);
}