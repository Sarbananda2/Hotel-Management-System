-- seed core data
INSERT INTO users (name, email, password_hash, role) VALUES
('Admin User','admin@example.com','$2a$10$J4lrMBMyew3fFamqVp0YgeqLDOMZ47QZXMcywZMh.rKIqVAE1KmX2','ADMIN'),
('Front Desk','frontdesk@example.com','$2a$10$eieHWvSqqNUyfLZa/W3rZupzngKsI1D7WJp.WhWcg0H0O0Hanw8YG','FRONTDESK'),
('Housekeeper','housekeeping@example.com','$2a$10$G2hF/Wq9T3p8thYPPyv1kuNAHBsxzBXr.IEfttyeFqiXX0yYJxupu','HOUSEKEEPING');

INSERT INTO room_types (name, capacity, base_rate, description) VALUES
('Standard', 2, 3000.00, 'Standard double room'),
('Deluxe', 3, 5000.00, 'Deluxe room with view');

INSERT INTO rooms (room_number, room_type_id, status) VALUES
('101', 1, 'VACANT'),
('102', 1, 'VACANT'),
('201', 2, 'VACANT'),
('202', 2, 'VACANT');

