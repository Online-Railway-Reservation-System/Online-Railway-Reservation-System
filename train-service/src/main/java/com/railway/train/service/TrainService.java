package com.railway.train.service;

import com.railway.train.dto.TrainRequest;
import com.railway.train.dto.TrainResponse;

import java.util.List;

public interface TrainService {
    List<TrainResponse> getAllActiveTrains();
    TrainResponse getTrainById(Long id);
    TrainResponse getTrainByNumber(String trainNumber);
    TrainResponse createTrain(TrainRequest request);
    TrainResponse updateTrain(Long id, TrainRequest request);
    void deleteTrain(Long id);
}