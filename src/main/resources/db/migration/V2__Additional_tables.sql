-- Additional tables for the vehicle OEM server

-- Create key_sharing_invitations table
CREATE TABLE IF NOT EXISTS key_sharing_invitations (
    id BIGSERIAL PRIMARY KEY,
    invitation_code VARCHAR(50) UNIQUE NOT NULL,
    friend_email VARCHAR(100) NOT NULL,
    friend_name VARCHAR(100) NOT NULL,
    friend_phone VARCHAR(20),
    shared_by VARCHAR(100) NOT NULL,
    permission_level VARCHAR(255) CHECK (permission_level IN ('FULL_ACCESS','DRIVE_ONLY','UNLOCK_ONLY','TRUNK_ONLY','EMERGENCY_ONLY','VALET')),
    time_restrictions VARCHAR(255),
    location_restrictions VARCHAR(255),
    max_usage_count BIGINT,
    status VARCHAR(255) CHECK (status IN ('PENDING','ACCEPTED','DECLINED','EXPIRED','REVOKED')) DEFAULT 'PENDING',
    created_at TIMESTAMP(6) DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP(6) DEFAULT CURRENT_TIMESTAMP,
    sent_at TIMESTAMP(6),
    accepted_at TIMESTAMP(6),
    expires_at TIMESTAMP(6),
    invitation_expires_at TIMESTAMP(6),
    revoked_at TIMESTAMP(6),
    revoked_by VARCHAR(100),
    revocation_reason VARCHAR(255),
    vehicle_id BIGINT,
    digital_key_id BIGINT UNIQUE,
    CONSTRAINT fk_key_sharing_invitations_vehicle FOREIGN KEY (vehicle_id) REFERENCES vehicles(id),
    CONSTRAINT fk_key_sharing_invitations_digital_key FOREIGN KEY (digital_key_id) REFERENCES digital_keys(id)
);

-- Create pairing_sessions table
CREATE TABLE IF NOT EXISTS pairing_sessions (
    id BIGSERIAL PRIMARY KEY,
    session_id VARCHAR(50) UNIQUE NOT NULL,
    device_id VARCHAR(100) NOT NULL,
    device_oem VARCHAR(50) NOT NULL,
    initiated_by VARCHAR(100) NOT NULL,
    status VARCHAR(255) CHECK (status IN ('INITIATED','ACTIVE','COMPLETED','FAILED','EXPIRED','REVOKED')) DEFAULT 'INITIATED',
    pairing_password VARCHAR(100),
    pairing_verifier VARCHAR(255),
    device_public_key TEXT,
    vehicle_public_key TEXT,
    vehicle_private_key TEXT,
    device_certificate TEXT,
    cross_signed_certificate TEXT,
    failed_attempts INTEGER DEFAULT 0,
    created_at TIMESTAMP(6) DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP(6) DEFAULT CURRENT_TIMESTAMP,
    initiated_at TIMESTAMP(6),
    completed_at TIMESTAMP(6),
    expires_at TIMESTAMP(6),
    revoked_at TIMESTAMP(6),
    revoked_by VARCHAR(100),
    revocation_reason VARCHAR(255),
    vehicle_id BIGINT,
    CONSTRAINT fk_pairing_sessions_vehicle FOREIGN KEY (vehicle_id) REFERENCES vehicles(id)
);

-- Create subscriptions table
CREATE TABLE IF NOT EXISTS subscriptions (
    id BIGSERIAL PRIMARY KEY,
    tier VARCHAR(255) CHECK (tier IN ('BASIC','PREMIUM','ENTERPRISE')) DEFAULT 'BASIC',
    status VARCHAR(255) CHECK (status IN ('ACTIVE','INACTIVE','SUSPENDED','CANCELLED','EXPIRED','GRACE_PERIOD','PENDING_PAYMENT')) DEFAULT 'ACTIVE',
    billing_cycle VARCHAR(255) CHECK (billing_cycle IN ('MONTHLY','YEARLY')) DEFAULT 'MONTHLY',
    monthly_price NUMERIC(10,2),
    auto_renew BOOLEAN DEFAULT TRUE,
    discount_percentage INTEGER DEFAULT 0,
    promo_code VARCHAR(50),
    started_at TIMESTAMP(6),
    expires_at TIMESTAMP(6),
    last_billing_date TIMESTAMP(6),
    next_billing_date TIMESTAMP(6),
    grace_period_ends_at TIMESTAMP(6),
    cancelled_at TIMESTAMP(6),
    cancellation_requested_at TIMESTAMP(6),
    cancellation_reason VARCHAR(255),
    created_at TIMESTAMP(6) DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP(6) DEFAULT CURRENT_TIMESTAMP,
    vehicle_id BIGINT UNIQUE,
    CONSTRAINT fk_subscriptions_vehicle FOREIGN KEY (vehicle_id) REFERENCES vehicles(id)
);

-- Create key_usage_logs table
CREATE TABLE IF NOT EXISTS key_usage_logs (
    id BIGSERIAL PRIMARY KEY,
    key_id VARCHAR(100) NOT NULL,
    device_id VARCHAR(100) NOT NULL,
    vehicle_vin VARCHAR(17) NOT NULL,
    usage_type VARCHAR(255) CHECK (usage_type IN ('UNLOCK','LOCK','START_ENGINE','STOP_ENGINE','TRUNK_ACCESS','PANIC_BUTTON','REMOTE_START','CLIMATE_CONTROL','HORN_LIGHTS','VALET_MODE','EMERGENCY_ACCESS')),
    timestamp TIMESTAMP(6) DEFAULT CURRENT_TIMESTAMP,
    success BOOLEAN DEFAULT TRUE,
    error_message VARCHAR(255),
    location_latitude DOUBLE PRECISION,
    location_longitude DOUBLE PRECISION,
    location_address VARCHAR(255),
    session_duration_minutes INTEGER,
    battery_level_start INTEGER,
    battery_level_end INTEGER,
    fuel_consumed_liters DOUBLE PRECISION,
    distance_traveled_km DOUBLE PRECISION,
    max_speed_kmh DOUBLE PRECISION,
    additional_data TEXT
);

-- Create audit_logs table
CREATE TABLE IF NOT EXISTS audit_logs (
    id BIGSERIAL PRIMARY KEY,
    timestamp TIMESTAMP(6) DEFAULT CURRENT_TIMESTAMP,
    performed_by VARCHAR(100),
    session_id VARCHAR(100),
    ip_address VARCHAR(45),
    user_agent VARCHAR(255),
    action VARCHAR(50),
    entity_type VARCHAR(50),
    entity_id VARCHAR(100),
    old_values TEXT,
    new_values TEXT,
    reason VARCHAR(255),
    severity VARCHAR(255) CHECK (severity IN ('INFO','WARNING','ERROR','CRITICAL')) DEFAULT 'INFO'
);
