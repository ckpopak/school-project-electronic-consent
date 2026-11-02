INSERT INTO school (id, name, code) VALUES (1, 'Riverside Primary School', 'RPS');

INSERT INTO app_user (id, email, password, display_name, role, school_id) VALUES
  (1, 'parent1@demo.local', 'demo123', 'Alex Parent', 'PARENT', NULL),
  (2, 'parent2@demo.local', 'demo123', 'Jordan Parent', 'PARENT', NULL),
  (3, 'school@riverside.demo', 'demo123', 'Ms. Lee (Admin)', 'SCHOOL', 1);

INSERT INTO student (id, guardian_id, full_name, class_name, class_number) VALUES
  (1, 1, 'Chris Chan', '4A', '12'),
  (2, 1, 'Sam Chan', '2B', '05'),
  (3, 2, 'Taylor Wong', '4A', '18');

INSERT INTO campaign (id, school_id, title, description, qr_token, starts_at, deadline_at) VALUES (
  1,
  1,
  'Photo use in 2027 promotional materials',
  'Consent for your child''s photo and first name to appear in the school website, yearbook, and open-day brochures. You may choose full consent, website only, or decline.',
  'riverside-photo-2027',
  TIMESTAMP '2026-08-01 00:00:00',
  TIMESTAMP '2026-12-31 23:59:59'
);
