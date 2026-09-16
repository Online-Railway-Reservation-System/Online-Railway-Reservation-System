package com.railway.inventoryquota.service;

import com.railway.inventoryquota.entity.TrainQuota;
import com.railway.inventoryquota.repository.TrainQuotaRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@Transactional
public class QuotaServiceImpl implements QuotaService {

    private final TrainQuotaRepository quotaRepository;

    public QuotaServiceImpl(TrainQuotaRepository quotaRepository) {
        this.quotaRepository = quotaRepository;
    }

    @Override
    @Transactional(readOnly = true)
    public List<TrainQuota> getQuotasByTrainId(Long trainId) {
        return quotaRepository.findByTrainId(trainId);
    }

    @Override
    public TrainQuota saveQuota(TrainQuota quota) {
        return quotaRepository.save(quota);
    }

    @Override
    public void deleteQuota(Long quotaId) {
        quotaRepository.deleteById(quotaId);
    }
}