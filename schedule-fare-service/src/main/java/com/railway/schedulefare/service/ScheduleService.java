package com.railway.schedulefare.service;

import com.railway.schedulefare.dto.ScheduleRequest;
import com.railway.schedulefare.dto.ScheduleResponse;

import java.util.List;

public interface ScheduleService {
    List<ScheduleResponse> getSchedulesByTrainId(Long trainId);
    ScheduleResponse createSchedule(ScheduleRequest request);
    ScheduleResponse updateSchedule(Long id, ScheduleRequest request);
    void deleteSchedule(Long id);
}