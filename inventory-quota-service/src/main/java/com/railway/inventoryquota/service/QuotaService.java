package com.railway.inventoryquota.service;

import com.railway.inventoryquota.entity.TrainQuota;
import java.util.List;

public interface QuotaService {
    List<TrainQuota> getQuotasByTrainId(Long trainId);
    TrainQuota saveQuota(TrainQuota quota);
    void deleteQuota(Long quotaId);
}