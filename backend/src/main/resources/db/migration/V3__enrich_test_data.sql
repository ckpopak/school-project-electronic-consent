INSERT INTO school (id, name, code) VALUES
  (2, 'Harbour View Kindergarten', 'HVK'),
  (3, 'Summit Secondary School', 'SSS');

INSERT INTO app_user (id, email, password, display_name, role, school_id) VALUES
  (4, 'parent3@demo.local', 'demo123', 'Morgan Ng', 'PARENT', NULL),
  (5, 'parent4@demo.local', 'demo123', 'Casey Lam', 'PARENT', NULL),
  (6, 'parent5@demo.local', 'demo123', 'Riley Cheung', 'PARENT', NULL),
  (7, 'school@harbour.demo', 'demo123', 'Mr. Ho (KG Admin)', 'SCHOOL', 2),
  (8, 'school@summit.demo', 'demo123', 'Dr. Fong (Office)', 'SCHOOL', 3);

INSERT INTO student (id, guardian_id, full_name, class_name, class_number) VALUES
  (4, 3, 'Jamie Wong', '4A', '03'),
  (5, 4, 'Avery Ng', 'K2', '08'),
  (6, 4, 'Quinn Ng', 'K1', '14'),
  (7, 5, 'Blake Lam', '3C', '22'),
  (8, 5, 'Sky Lam', '1A', '07'),
  (9, 6, 'River Cheung', '6B', '01'),
  (10, 2, 'Parker Wong', '2A', '11');

INSERT INTO campaign (id, school_id, title, description, qr_token, starts_at, deadline_at) VALUES
  (
    2,
    1,
    'Sports Day 2027 — photo & video',
    'Optional consent for photographs and short video clips taken during Sports Day to be used in school newsletters and social media.',
    'riverside-sports-2027',
    TIMESTAMP '2026-09-01 00:00:00',
    TIMESTAMP '2027-03-31 23:59:59'
  ),
  (
    3,
    2,
    'KG Open Day 2027 publicity',
    'Consent for children''s images during Open Day activities to appear in kindergarten promotional leaflets and the school Facebook page.',
    'harbour-openday-2027',
    TIMESTAMP '2026-08-15 00:00:00',
    TIMESTAMP '2026-11-30 23:59:59'
  ),
  (
    4,
    3,
    'Graduation ceremony album',
    'Form 6 graduation photos for the yearbook and alumni archive. Declining will not affect participation in the ceremony.',
    'summit-grad-2027',
    TIMESTAMP '2026-01-01 00:00:00',
    TIMESTAMP '2026-10-15 23:59:59'
  ),
  (
    5,
    1,
    '2025 archive campaign (closed)',
    'Demo only: this campaign deadline has passed — submissions should be rejected.',
    'riverside-closed-demo',
    TIMESTAMP '2024-01-01 00:00:00',
    TIMESTAMP '2025-01-01 00:00:00'
  );

INSERT INTO consent_submission (id, campaign_id, student_id, decision, scope_note, status, submitted_at) VALUES
  (1, 1, 3, 'YES', NULL, 'SUBMITTED', TIMESTAMP '2026-09-12 09:15:00'),
  (2, 1, 1, 'LIMITED', 'School website only, not social media', 'SUBMITTED', TIMESTAMP '2026-09-10 14:22:00'),
  (3, 1, 4, 'NO', NULL, 'SUBMITTED', TIMESTAMP '2026-09-11 18:05:00'),
  (4, 2, 2, 'YES', NULL, 'SUBMITTED', TIMESTAMP '2026-10-01 11:30:00'),
  (5, 3, 5, 'LIMITED', 'Printed leaflet only', 'SUBMITTED', TIMESTAMP '2026-09-20 08:45:00'),
  (6, 3, 6, 'YES', NULL, 'SUBMITTED', TIMESTAMP '2026-09-21 16:10:00'),
  (7, 4, 9, 'NO', NULL, 'SUBMITTED', TIMESTAMP '2026-08-05 10:00:00'),
  (8, 1, 10, 'YES', NULL, 'WITHDRAWN', TIMESTAMP '2026-09-08 12:00:00');

ALTER TABLE consent_submission ALTER COLUMN id RESTART WITH 100;
ALTER TABLE student ALTER COLUMN id RESTART WITH 100;
ALTER TABLE app_user ALTER COLUMN id RESTART WITH 100;
ALTER TABLE campaign ALTER COLUMN id RESTART WITH 100;
ALTER TABLE school ALTER COLUMN id RESTART WITH 100;
