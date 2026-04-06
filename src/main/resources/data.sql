INSERT INTO users (email, password_hash, role, status, created_at) VALUES
('admin@finance.com', '$2a$10$mlmRSIRy2jZST5.AcPZuueDF3Hy5ItanvxnWBKSHnj.XIXEVtq.6q', 'ADMIN', 'ACTIVE', CURRENT_TIMESTAMP);

INSERT INTO users (email, password_hash, role, status, created_at) VALUES
('analyst@finance.com', '$2a$10$mlmRSIRy2jZST5.AcPZuueDF3Hy5ItanvxnWBKSHnj.XIXEVtq.6q', 'ANALYST', 'ACTIVE', CURRENT_TIMESTAMP);

INSERT INTO users (email, password_hash, role, status, created_at) VALUES
('viewer@finance.com', '$2a$10$mlmRSIRy2jZST5.AcPZuueDF3Hy5ItanvxnWBKSHnj.XIXEVtq.6q', 'VIEWER', 'ACTIVE', CURRENT_TIMESTAMP);