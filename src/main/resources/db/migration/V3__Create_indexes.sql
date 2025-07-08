-- Create indexes for better query performance

-- Owner accounts indexes
CREATE INDEX IF NOT EXISTS idx_owner_accounts_email ON owner_accounts(email);
CREATE INDEX IF NOT EXISTS idx_owner_accounts_account_id ON owner_accounts(account_id);
CREATE INDEX IF NOT EXISTS idx_owner_accounts_status ON owner_accounts(account_status);
CREATE INDEX IF NOT EXISTS idx_owner_accounts_created_at ON owner_accounts(created_at);

-- Vehicles indexes
CREATE INDEX IF NOT EXISTS idx_vehicles_vin ON vehicles(vin);
CREATE INDEX IF NOT EXISTS idx_vehicles_owner_id ON vehicles(owner_id);
CREATE INDEX IF NOT EXISTS idx_vehicles_status ON vehicles(vehicle_status);
CREATE INDEX IF NOT EXISTS idx_vehicles_subscription_active ON vehicles(subscription_active);
CREATE INDEX IF NOT EXISTS idx_vehicles_make_model ON vehicles(make, model);
CREATE INDEX IF NOT EXISTS idx_vehicles_created_at ON vehicles(created_at);

-- Digital keys indexes
CREATE INDEX IF NOT EXISTS idx_digital_keys_key_id ON digital_keys(key_id);
CREATE INDEX IF NOT EXISTS idx_digital_keys_device_id ON digital_keys(device_id);
CREATE INDEX IF NOT EXISTS idx_digital_keys_vehicle_id ON digital_keys(vehicle_id);
CREATE INDEX IF NOT EXISTS idx_digital_keys_status ON digital_keys(status);
CREATE INDEX IF NOT EXISTS idx_digital_keys_key_type ON digital_keys(key_type);
CREATE INDEX IF NOT EXISTS idx_digital_keys_friend_email ON digital_keys(friend_email);
CREATE INDEX IF NOT EXISTS idx_digital_keys_expires_at ON digital_keys(expires_at);
CREATE INDEX IF NOT EXISTS idx_digital_keys_created_at ON digital_keys(created_at);

-- Key sharing invitations indexes
CREATE INDEX IF NOT EXISTS idx_key_sharing_invitations_invitation_code ON key_sharing_invitations(invitation_code);
CREATE INDEX IF NOT EXISTS idx_key_sharing_invitations_friend_email ON key_sharing_invitations(friend_email);
CREATE INDEX IF NOT EXISTS idx_key_sharing_invitations_vehicle_id ON key_sharing_invitations(vehicle_id);
CREATE INDEX IF NOT EXISTS idx_key_sharing_invitations_status ON key_sharing_invitations(status);
CREATE INDEX IF NOT EXISTS idx_key_sharing_invitations_expires_at ON key_sharing_invitations(expires_at);
CREATE INDEX IF NOT EXISTS idx_key_sharing_invitations_created_at ON key_sharing_invitations(created_at);

-- Pairing sessions indexes
CREATE INDEX IF NOT EXISTS idx_pairing_sessions_session_id ON pairing_sessions(session_id);
CREATE INDEX IF NOT EXISTS idx_pairing_sessions_device_id ON pairing_sessions(device_id);
CREATE INDEX IF NOT EXISTS idx_pairing_sessions_vehicle_id ON pairing_sessions(vehicle_id);
CREATE INDEX IF NOT EXISTS idx_pairing_sessions_status ON pairing_sessions(status);
CREATE INDEX IF NOT EXISTS idx_pairing_sessions_expires_at ON pairing_sessions(expires_at);
CREATE INDEX IF NOT EXISTS idx_pairing_sessions_created_at ON pairing_sessions(created_at);

-- Subscriptions indexes
CREATE INDEX IF NOT EXISTS idx_subscriptions_vehicle_id ON subscriptions(vehicle_id);
CREATE INDEX IF NOT EXISTS idx_subscriptions_status ON subscriptions(status);
CREATE INDEX IF NOT EXISTS idx_subscriptions_tier ON subscriptions(tier);
CREATE INDEX IF NOT EXISTS idx_subscriptions_expires_at ON subscriptions(expires_at);
CREATE INDEX IF NOT EXISTS idx_subscriptions_next_billing_date ON subscriptions(next_billing_date);

-- Key usage logs indexes
CREATE INDEX IF NOT EXISTS idx_key_usage_logs_key_id ON key_usage_logs(key_id);
CREATE INDEX IF NOT EXISTS idx_key_usage_logs_device_id ON key_usage_logs(device_id);
CREATE INDEX IF NOT EXISTS idx_key_usage_logs_vehicle_vin ON key_usage_logs(vehicle_vin);
CREATE INDEX IF NOT EXISTS idx_key_usage_logs_usage_type ON key_usage_logs(usage_type);
CREATE INDEX IF NOT EXISTS idx_key_usage_logs_timestamp ON key_usage_logs(timestamp);
CREATE INDEX IF NOT EXISTS idx_key_usage_logs_success ON key_usage_logs(success);

-- Audit logs indexes
CREATE INDEX IF NOT EXISTS idx_audit_logs_timestamp ON audit_logs(timestamp);
CREATE INDEX IF NOT EXISTS idx_audit_logs_performed_by ON audit_logs(performed_by);
CREATE INDEX IF NOT EXISTS idx_audit_logs_action ON audit_logs(action);
CREATE INDEX IF NOT EXISTS idx_audit_logs_entity_type ON audit_logs(entity_type);
CREATE INDEX IF NOT EXISTS idx_audit_logs_entity_id ON audit_logs(entity_id);
CREATE INDEX IF NOT EXISTS idx_audit_logs_severity ON audit_logs(severity);

-- Composite indexes for common query patterns
CREATE INDEX IF NOT EXISTS idx_digital_keys_vehicle_status ON digital_keys(vehicle_id, status);
CREATE INDEX IF NOT EXISTS idx_digital_keys_device_status ON digital_keys(device_id, status);
CREATE INDEX IF NOT EXISTS idx_key_usage_logs_key_timestamp ON key_usage_logs(key_id, timestamp);
CREATE INDEX IF NOT EXISTS idx_vehicles_owner_status ON vehicles(owner_id, vehicle_status);
CREATE INDEX IF NOT EXISTS idx_audit_logs_entity_timestamp ON audit_logs(entity_type, entity_id, timestamp);
