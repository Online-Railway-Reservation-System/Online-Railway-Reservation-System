-- Notification Logs Table
CREATE TABLE IF NOT EXISTS notification_logs (
    id BIGINT AUTO_INCREMENT PRIMARY KEY,
    recipient_email VARCHAR(100),
    recipient_phone VARCHAR(20),
    channel VARCHAR(20) NOT NULL DEFAULT 'EMAIL',
    notification_type VARCHAR(50) NOT NULL,
    pnr VARCHAR(20),
    customer_id BIGINT,
    subject VARCHAR(200) NOT NULL,
    content TEXT NOT NULL,
    status VARCHAR(20) NOT NULL DEFAULT 'SENT',
    sent_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
    INDEX idx_notif_pnr (pnr),
    INDEX idx_notif_cust (customer_id),
    INDEX idx_notif_channel (channel)
);
