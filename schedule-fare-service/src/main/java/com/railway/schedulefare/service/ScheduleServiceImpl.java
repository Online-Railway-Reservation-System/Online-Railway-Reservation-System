package com.railway.schedulefare.service;

import com.railway.schedulefare.dto.ScheduleRequest;
import com.railway.schedulefare.dto.ScheduleResponse;
import com.railway.schedulefare.entity.TrainSchedule;
import com.railway.schedulefare.exception.ResourceNotFoundException;
import com.railway.schedulefare.repository.TrainScheduleRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.stream.Collectors;

@Service
@Transactional
public class ScheduleServiceImpl implements ScheduleService {

    private final TrainScheduleRepository scheduleRepository;

    public ScheduleServiceImpl(TrainScheduleRepository scheduleRepository) {
        this.scheduleRepository = scheduleRepository;
    }

    @Override
    @Transactional(readOnly = true)
    public List<ScheduleResponse> getSchedulesByTrainId(Long trainId) {
        return scheduleRepository.findByTrainId(trainId)
                .stream()
                .map(this::mapToDto)
                .collect(Collectors.toList());
    }

    @Override
    public ScheduleResponse createSchedule(ScheduleRequest request) {
        TrainSchedule schedule = new TrainSchedule();
        schedule.setTrainId(request.getTrainId());
        schedule.setDepartureStationCode(request.getDepartureStationCode().trim().toUpperCase());
        schedule.setArrivalStationCode(request.getArrivalStationCode().trim().toUpperCase());
        schedule.setDepartureTime(request.getDepartureTime().trim());
        schedule.setArrivalTime(request.getArrivalTime().trim());
        schedule.setRunningDays(request.getRunningDays().trim());
        schedule.setDurationHours(request.getDurationHours());
        schedule.setValidFrom(request.getValidFrom());
        schedule.setValidUpto(request.getValidUpto());
        schedule.setActiveStatus(request.isActiveStatus());

        TrainSchedule saved = scheduleRepository.save(schedule);
        return mapToDto(saved);
    }

    @Override
    public ScheduleResponse updateSchedule(Long id, ScheduleRequest request) {
        TrainSchedule schedule = scheduleRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Schedule not found with id: " + id));

        schedule.setTrainId(request.getTrainId());
        schedule.setDepartureStationCode(request.getDepartureStationCode().trim().toUpperCase());
        schedule.setArrivalStationCode(request.getArrivalStationCode().trim().toUpperCase());
        schedule.setDepartureTime(request.getDepartureTime().trim());
        schedule.setArrivalTime(request.getArrivalTime().trim());
        schedule.setRunningDays(request.getRunningDays().trim());
        schedule.setDurationHours(request.getDurationHours());
        schedule.setValidFrom(request.getValidFrom());
        schedule.setValidUpto(request.getValidUpto());
        schedule.setActiveStatus(request.isActiveStatus());

        TrainSchedule saved = scheduleRepository.save(schedule);
        return mapToDto(saved);
    }

    @Override
    public void deleteSchedule(Long id) {
        TrainSchedule schedule = scheduleRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Schedule not found with id: " + id));
        schedule.setActiveStatus(false);
        scheduleRepository.save(schedule);
    }

    private ScheduleResponse mapToDto(TrainSchedule s) {
        return new ScheduleResponse(
                s.getId(),
                s.getTrainId(),
                s.getDepartureStationCode(),
                s.getArrivalStationCode(),
                s.getDepartureTime(),
                s.getArrivalTime(),
                s.getRunningDays(),
                s.getDurationHours(),
                s.getValidFrom(),
                s.getValidUpto(),
                s.isActiveStatus()
        );
    }
}