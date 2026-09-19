package com.railway.payment.service;

import com.railway.payment.dto.RefundCalculateRequest;
import com.railway.payment.dto.RefundCalculateResponse;
import com.railway.payment.dto.RefundRequest;
import com.railway.payment.dto.RefundResponse;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import java.util.List;

public interface RefundService {
    RefundCalculateResponse calculateRefund(RefundCalculateRequest request);
    RefundResponse processRefund(RefundRequest request);
    RefundResponse getRefundById(Long id);
    RefundResponse getRefundByReference(String refundReference);
    RefundResponse getRefundByReservationId(Long reservationId);
    RefundResponse getRefundByPnr(String pnr);
    Page<RefundResponse> getAllRefunds(Pageable pageable);
}
