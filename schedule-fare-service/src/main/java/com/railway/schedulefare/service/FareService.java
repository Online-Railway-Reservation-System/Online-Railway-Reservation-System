package com.railway.schedulefare.service;

import com.railway.schedulefare.dto.FareCalculateRequest;
import com.railway.schedulefare.dto.FareCalculateResponse;
import com.railway.schedulefare.entity.FareRule;
import com.railway.schedulefare.entity.TatkalConfig;

import java.util.List;

public interface FareService {
    FareCalculateResponse calculateFare(FareCalculateRequest request);
    List<FareRule> getFareRulesByTrainId(Long trainId);
    FareRule createFareRule(FareRule rule);
    TatkalConfig getTatkalConfig(Long trainId);
    TatkalConfig saveTatkalConfig(TatkalConfig config);
}