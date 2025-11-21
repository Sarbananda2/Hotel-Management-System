-- create core tables
CREATE TABLE IF NOT EXISTS users (
  id SERIAL PRIMARY KEY,
  name TEXT NOT NULL,
  email TEXT UNIQUE NOT NULL,
  password_hash TEXT NOT NULL,
  role TEXT NOT NULL,
  created_at TIMESTAMP WITH TIME ZONE DEFAULT now()
);

CREATE TABLE IF NOT EXISTS room_types (
  id SERIAL PRIMARY KEY,
  name TEXT NOT NULL,
  capacity INT NOT NULL DEFAULT 1,
  base_rate NUMERIC(10,2) NOT NULL DEFAULT 0,
  description TEXT
);

CREATE TABLE IF NOT EXISTS rooms (
  id SERIAL PRIMARY KEY,
  room_number TEXT NOT NULL UNIQUE,
  room_type_id INT NOT NULL REFERENCES room_types(id) ON DELETE RESTRICT,
  status TEXT NOT NULL DEFAULT 'VACANT',
  current_reservation_id INT NULL
);

CREATE TABLE IF NOT EXISTS reservations (
  id SERIAL PRIMARY KEY,
  guest_name TEXT NOT NULL,
  guest_email TEXT,
  phone TEXT,
  room_type_id INT NOT NULL REFERENCES room_types(id),
  check_in_date DATE NOT NULL,
  check_out_date DATE NOT NULL,
  status TEXT NOT NULL DEFAULT 'BOOKED',
  created_by INT REFERENCES users(id),
  created_at TIMESTAMP WITH TIME ZONE DEFAULT now()
);

CREATE TABLE IF NOT EXISTS stays (
  id SERIAL PRIMARY KEY,
  reservation_id INT REFERENCES reservations(id) ON DELETE SET NULL,
  room_id INT REFERENCES rooms(id) ON DELETE SET NULL,
  actual_checkin TIMESTAMP WITH TIME ZONE,
  actual_checkout TIMESTAMP WITH TIME ZONE,
  folio_id INT
);

CREATE TABLE IF NOT EXISTS folios (
  id SERIAL PRIMARY KEY,
  stay_id INT REFERENCES stays(id) ON DELETE SET NULL,
  reservation_id INT REFERENCES reservations(id) ON DELETE SET NULL,
  currency TEXT NOT NULL DEFAULT 'INR',
  created_at TIMESTAMP WITH TIME ZONE DEFAULT now()
);

CREATE TABLE IF NOT EXISTS folio_line_items (
  id SERIAL PRIMARY KEY,
  folio_id INT NOT NULL REFERENCES folios(id) ON DELETE CASCADE,
  type TEXT NOT NULL,
  description TEXT,
  amount NUMERIC(10,2) NOT NULL,
  posted_at TIMESTAMP WITH TIME ZONE DEFAULT now()
);

CREATE TABLE IF NOT EXISTS payments (
  id SERIAL PRIMARY KEY,
  folio_id INT NOT NULL REFERENCES folios(id) ON DELETE CASCADE,
  amount NUMERIC(10,2) NOT NULL,
  method TEXT NOT NULL,
  reference TEXT,
  created_at TIMESTAMP WITH TIME ZONE DEFAULT now()
);

CREATE TABLE IF NOT EXISTS housekeeping_tasks (
  id SERIAL PRIMARY KEY,
  room_id INT REFERENCES rooms(id),
  status TEXT NOT NULL DEFAULT 'OPEN',
  assigned_to INT REFERENCES users(id),
  notes TEXT,
  created_at TIMESTAMP WITH TIME ZONE DEFAULT now()
);

CREATE TABLE IF NOT EXISTS audit_logs (
  id SERIAL PRIMARY KEY,
  user_id INT REFERENCES users(id),
  action TEXT NOT NULL,
  entity_type TEXT,
  entity_id INT,
  timestamp TIMESTAMP WITH TIME ZONE DEFAULT now(),
  meta JSONB
);

CREATE INDEX IF NOT EXISTS idx_reservations_roomtype_dates ON reservations(room_type_id, check_in_date, check_out_date);
CREATE INDEX IF NOT EXISTS idx_reservations_status ON reservations(status);
CREATE INDEX IF NOT EXISTS idx_rooms_status ON rooms(status);
CREATE INDEX IF NOT EXISTS idx_stays_reservation ON stays(reservation_id);
CREATE INDEX IF NOT EXISTS idx_folios_stay ON folios(stay_id);
CREATE INDEX IF NOT EXISTS idx_payments_folio ON payments(folio_id);
CREATE INDEX IF NOT EXISTS idx_payments_created_at ON payments(created_at);
CREATE INDEX IF NOT EXISTS idx_audit_logs_user ON audit_logs(user_id);
CREATE INDEX IF NOT EXISTS idx_audit_logs_timestamp ON audit_logs(timestamp);

