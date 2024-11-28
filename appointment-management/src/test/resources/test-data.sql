CREATE TABLE IF NOT EXISTS appointments (
                                            appointment_id BIGINT AUTO_INCREMENT PRIMARY KEY,
                                            provider_id BIGINT NOT NULL,
                                            user_id BIGINT,
                                            start_date_time DATETIME NOT NULL,
                                            end_date_time DATETIME NOT NULL,
                                            status VARCHAR(50),
    service_type VARCHAR(100),
    comments VARCHAR(100)
    );

DELETE FROM appointments;
INSERT INTO appointments (
    appointment_id, provider_id, user_id, start_date_time, end_date_time, status, service_type, comments
) VALUES
      (1, 1, 2, TIMESTAMP '2024-01-01 09:00:00', TIMESTAMP '2024-01-01 10:00:00', 'SCHEDULED', 'Medical', 'Initial test appointment'),
      (2, 1, 3, TIMESTAMP '2024-01-02 09:00:00', TIMESTAMP '2024-01-02 10:00:00', 'CANCELLED', 'Consultation', 'Cancelled appointment');
