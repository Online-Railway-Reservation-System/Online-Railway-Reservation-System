package com.railway.train.service;

import com.railway.train.dto.TrainRequest;
import com.railway.train.dto.TrainResponse;
import com.railway.train.entity.Train;
import com.railway.train.exception.DuplicateResourceException;
import com.railway.train.exception.ResourceNotFoundException;
import com.railway.train.repository.TrainRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.stream.Collectors;

@Service
@Transactional
public class TrainServiceImpl implements TrainService {

    private final TrainRepository trainRepository;

    public TrainServiceImpl(TrainRepository trainRepository) {
        this.trainRepository = trainRepository;
    }

    @Override
    @Transactional(readOnly = true)
    public List<TrainResponse> getAllActiveTrains() {
        return trainRepository.findByActiveStatusTrue()
                .stream()
                .map(this::mapToDto)
                .collect(Collectors.toList());
    }

    @Override
    @Transactional(readOnly = true)
    public TrainResponse getTrainById(Long id) {
        Train train = trainRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Train not found with id: " + id));
        return mapToDto(train);
    }

    @Override
    @Transactional(readOnly = true)
    public TrainResponse getTrainByNumber(String trainNumber) {
        Train train = trainRepository.findByTrainNumber(trainNumber)
                .orElseThrow(() -> new ResourceNotFoundException("Train not found with number: " + trainNumber));
        return mapToDto(train);
    }

    @Override
    public TrainResponse createTrain(TrainRequest request) {
        if (trainRepository.existsByTrainNumber(request.getTrainNumber())) {
            throw new DuplicateResourceException("Train with number " + request.getTrainNumber() + " already exists");
        }

        Train train = new Train();
        train.setTrainNumber(request.getTrainNumber().trim());
        train.setTrainName(request.getTrainName().trim());
        train.setTrainType(request.getTrainType().toUpperCase().trim());
        train.setActiveStatus(request.isActiveStatus());

        Train saved = trainRepository.save(train);
        return mapToDto(saved);
    }

    @Override
    public TrainResponse updateTrain(Long id, TrainRequest request) {
        Train train = trainRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Train not found with id: " + id));

        if (!train.getTrainNumber().equalsIgnoreCase(request.getTrainNumber()) &&
                trainRepository.existsByTrainNumber(request.getTrainNumber())) {
            throw new DuplicateResourceException("Train with number " + request.getTrainNumber() + " already exists");
        }

        train.setTrainNumber(request.getTrainNumber().trim());
        train.setTrainName(request.getTrainName().trim());
        train.setTrainType(request.getTrainType().toUpperCase().trim());
        train.setActiveStatus(request.isActiveStatus());

        Train saved = trainRepository.save(train);
        return mapToDto(saved);
    }

    @Override
    public void deleteTrain(Long id) {
        Train train = trainRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Train not found with id: " + id));
        train.setActiveStatus(false);
        trainRepository.save(train);
    }

    private TrainResponse mapToDto(Train t) {
        return new TrainResponse(
                t.getId(),
                t.getTrainNumber(),
                t.getTrainName(),
                t.getTrainType(),
                t.isActiveStatus()
        );
    }
}