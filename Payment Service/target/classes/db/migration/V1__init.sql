-- Payment Transactions Table
CREATE TABLE IF NOT EXISTS payment_transactions (
    id BIGINT AUTO_INCREMENT PRIMARY KEY,
    transaction_reference VARCHAR(64) NOT NULL UNIQUE,
    reservation_id BIGINT,
    pnr VARCHAR(20),
    customer_id BIGINT NOT NULL,
    amount DOUBLE NOT NULL,
    payment_method VARCHAR(50) NOT NULL,
    status VARCHAR(20) NOT NULL DEFAULT 'SUCCESS',
    idempotency_key VARCHAR(100) UNIQUE,
    gateway_reference VARCHAR(100),
    failure_reason VARCHAR(255),
    created_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
    INDEX idx_pay_res (reservation_id),
    INDEX idx_pay_pnr (pnr),
    INDEX idx_pay_idemp (idempotency_key)
);

-- Refund Transactions Table
CREATE TABLE IF NOT EXISTS refund_transactions (
    id BIGINT AUTO_INCREMENT PRIMARY KEY,
    refund_reference VARCHAR(64) NOT NULL UNIQUE,
    payment_transaction_id BIGINT,
    reservation_id BIGINT,
    pnr VARCHAR(20),
    original_amount DOUBLE NOT NULL,
    deduction_amount DOUBLE NOT NULL,
    refund_amount DOUBLE NOT NULL,
    reason VARCHAR(255),
    status VARCHAR(20) NOT NULL DEFAULT 'PROCESSED',
    processed_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
    INDEX idx_ref_pnr (pnr),
    INDEX idx_ref_res (reservation_id)
);
