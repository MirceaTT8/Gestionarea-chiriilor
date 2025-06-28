INSERT INTO roles (name) VALUES ('ADMIN'), ('LANDLORD'), ('TENANT');

INSERT INTO users (first_name, last_name, email, password, phone, is_active) VALUES
                                                                                 ('Marian', 'Mihai', 'marian.mihai@gmail.com', '$2a$10$N9qo8uLOickgx2ZrVzaGUeRQ5qsO.mvc5A8Vgz1lNaXqnqI1uekGC', '0724123456', true),
                                                                                 ('Maria', 'Ionescu', 'maria.ionescu@gmail.com', '$2a$10$N9qo8uLOickgx2ZrVzaGUeRQ5qsO.mvc5A8Vgz1lNaXqnqI1uekGC', '0734567890', true),
                                                                                 ('Gheorghe', 'Vasilescu', 'gheorghe.vasilescu@yahoo.com', '$2a$10$N9qo8uLOickgx2ZrVzaGUeRQ5qsO.mvc5A8Vgz1lNaXqnqI1uekGC', '0745123789', true),
                                                                                 ('Alexandru', 'Vidu', 'alex.vidu@gmail.com', '$2a$10$N9qo8uLOickgx2ZrVzaGUeRQ5qsO.mvc5A8Vgz1lNaXqnqI1uekGC', '0767890123', true),
                                                                                 ('Cristina', 'Marin', 'cristina.marin@yahoo.com', '$2a$10$N9qo8uLOickgx2ZrVzaGUeRQ5qsO.mvc5A8Vgz1lNaXqnqI1uekGC', '0778901234', true);

INSERT INTO user_roles (user_id, role_id) VALUES
                                              (1, 1),
                                              (2, 2), (3, 2),
                                              (4, 3), (5, 3);

INSERT INTO properties (name, address, type, rent_amount, latitude, longitude, owner_id, validation_status, property_status, created_at, is_flagged) VALUES
                                                                                                                                                         ('Apartament 2 camere Victoriei', 'Bd. Victoriei 15, Timișoara', 'APARTMENT', 1200.00, 45.7536, 21.2251, 2, 'APPROVED', 'AVAILABLE', CURRENT_TIMESTAMP, false),
                                                                                                                                                         ('Apartament 3 camere Iosefin', 'Str. Eminescu 42, Timișoara', 'APARTMENT', 1400.00, 45.7640, 21.2268, 2, 'APPROVED', 'RENTED', CURRENT_TIMESTAMP, false),
                                                                                                                                                         ('Studio Cetate', 'Str. Ungureanu 8, Timișoara', 'APARTMENT', 800.00, 45.7597, 21.2257, 3, 'APPROVED', 'RENTED', CURRENT_TIMESTAMP, false),
                                                                                                                                                         ('Penthouse Bălcescu', 'Bd. I.C. Brătianu 5, Timișoara', 'APARTMENT', 2500.00, 45.7580, 21.2245, 3, 'PENDING', 'AVAILABLE', CURRENT_TIMESTAMP, false);

INSERT INTO leases (tenant_id, property_id, start_date, end_date, monthly_rent, status, lease_termination_status, created_at) VALUES
                                                                                                                                  (4, 2, '2024-07-01', '2025-12-31', 1400.00, 'ACTIVE', 'NONE', '2024-06-25'),
                                                                                                                                  (5, 3, '2025-01-01', '2026-12-31', 800.00, 'ACTIVE', 'NONE', '2024-12-20'),
                                                                                                                                  (5, 1, '2025-07-04', '2026-06-30', 1200.00, 'PENDING', 'NONE', '2025-06-20');

INSERT INTO payments (lease_id, amount, payment_date, payment_method, status) VALUES
                                                                                  (1, 1400.00, '2025-01-01', 'BANK_TRANSFER', 'COMPLETED'),
                                                                                  (1, 1400.00, '2025-02-01', 'CREDIT_CARD', 'COMPLETED'),
                                                                                  (1, 1400.00, '2025-03-01', 'CREDIT_CARD', 'COMPLETED'),
                                                                                  (1, 1400.00, '2025-04-01', 'BANK_TRANSFER', 'COMPLETED'),
                                                                                  (1, 1400.00, '2025-05-01', 'CREDIT_CARD', 'COMPLETED'),
                                                                                  (1, 1400.00, '2025-06-01', 'CREDIT_CARD', 'COMPLETED'),
                                                                                  (2, 800.00, '2025-01-01', 'CREDIT_CARD', 'COMPLETED'),
                                                                                  (2, 800.00, '2025-02-01', 'CREDIT_CARD', 'COMPLETED'),
                                                                                  (2, 800.00, '2025-03-01', 'BANK_TRANSFER', 'COMPLETED'),
                                                                                  (2, 800.00, '2025-04-01', 'CREDIT_CARD', 'COMPLETED'),
                                                                                  (2, 800.00, '2025-05-01', 'CREDIT_CARD', 'COMPLETED'),
                                                                                  (2, 800.00, '2025-06-01', 'CREDIT_CARD', 'COMPLETED');

UPDATE landlord_stats SET version = 0 WHERE version IS NULL;

INSERT INTO maintenance_requests (lease_id, description, status, cost, created_at, updated_at, is_fixed) VALUES
                                                                                                             (1, 'Robinetul din baie picură continuu. Este nevoie de reparație urgentă.', 'PENDING', NULL, '2025-06-23 08:45:00', '2025-06-23 08:45:00', false),
                                                                                                             (1, 'Becul din dormitor nu funcționează, probabil întrerupătorul.', 'IN_PROGRESS', NULL, '2025-06-22 15:00:00', '2025-06-23 10:00:00', false),
                                                                                                             (2, 'Aerul condiționat face zgomot când pornește.', 'COMPLETED', 150.00, '2025-06-20 12:30:00', '2025-06-21 16:00:00', true),
                                                                                                             (2, 'Ușa de la balcon se blochează uneori.', 'PENDING', NULL, '2025-06-24 14:20:00', '2025-06-24 14:20:00', false),
                                                                                                             (1, 'Chiuveta din baie se înfundă des.', 'COMPLETED', 80.00, '2025-06-15 09:15:00', '2025-06-16 14:30:00', true),
                                                                                                             (2, 'Geamul din living nu se închide bine.', 'IN_PROGRESS', NULL, '2025-06-18 16:45:00', '2025-06-19 08:20:00', false),
                                                                                                             (1, 'Dulapul din bucătărie nu se închide bine.', 'COMPLETED', 120.00, '2025-05-28 11:30:00', '2025-05-30 14:00:00', true),
                                                                                                             (2, 'Priza din dormitor nu funcționează.', 'COMPLETED', 75.00, '2025-05-25 13:15:00', '2025-05-26 16:45:00', true);

INSERT INTO users (first_name, last_name, email, password, phone, is_active) VALUES
                                                                                 ('Andrei', 'Popescu', 'andrei.popescu@gmail.com', '$2a$10$N9qo8uLOickgx2ZrVzaGUeRQ5qsO.mvc5A8Vgz1lNaXqnqI1uekGC', '0721234567', true),
                                                                                 ('Elena', 'Radu', 'elena.radu@yahoo.com', '$2a$10$N9qo8uLOickgx2ZrVzaGUeRQ5qsO.mvc5A8Vgz1lNaXqnqI1uekGC', '0732345678', true),
                                                                                 ('Mihai', 'Dumitrescu', 'mihai.dumitrescu@gmail.com', '$2a$10$N9qo8uLOickgx2ZrVzaGUeRQ5qsO.mvc5A8Vgz1lNaXqnqI1uekGC', '0743456789', true),
                                                                                 ('Ana', 'Georgescu', 'ana.georgescu@yahoo.com', '$2a$10$N9qo8uLOickgx2ZrVzaGUeRQ5qsO.mvc5A8Vgz1lNaXqnqI1uekGC', '0754567890', true),
                                                                                 ('Bogdan', 'Mihalcea', 'bogdan.mihalcea@gmail.com', '$2a$10$N9qo8uLOickgx2ZrVzaGUeRQ5qsO.mvc5A8Vgz1lNaXqnqI1uekGC', '0765678901', true);

INSERT INTO users (first_name, last_name, email, password, phone, is_active) VALUES
                                                                                 ('Stefan', 'Constantinescu', 'stefan.constantinescu@gmail.com', '$2a$10$N9qo8uLOickgx2ZrVzaGUeRQ5qsO.mvc5A8Vgz1lNaXqnqI1uekGC', '0776789012', true),
                                                                                 ('Ioana', 'Popa', 'ioana.popa@yahoo.com', '$2a$10$N9qo8uLOickgx2ZrVzaGUeRQ5qsO.mvc5A8Vgz1lNaXqnqI1uekGC', '0787890123', true),
                                                                                 ('Florin', 'Stanescu', 'florin.stanescu@gmail.com', '$2a$10$N9qo8uLOickgx2ZrVzaGUeRQ5qsO.mvc5A8Vgz1lNaXqnqI1uekGC', '0798901234', true);

INSERT INTO user_roles (user_id, role_id) VALUES
                                              (6, 3), (7, 3), (8, 3), (9, 3), (10, 3),
                                              (11, 2), (12, 2), (13, 2);

INSERT INTO properties (name, address, type, rent_amount, latitude, longitude, owner_id, validation_status, property_status, created_at, is_flagged) VALUES
                                                                                                                                                         ('Garsonieră Fabric', 'Str. Popa Șapcă 12, Timișoara', 'APARTMENT', 700.00, 45.7489, 21.2087, 11, 'APPROVED', 'RENTED', '2024-03-15 10:00:00', false),
                                                                                                                                                         ('Apartament 2 camere Girocului', 'Bd. Liviu Rebreanu 85, Timișoara', 'APARTMENT', 950.00, 45.7300, 21.2500, 11, 'APPROVED', 'RENTED', '2024-04-20 14:30:00', true),
                                                                                                                                                         ('Studio Complex Studențesc', 'Aleea Studenților 7, Timișoara', 'APARTMENT', 600.00, 45.7450, 21.2300, 12, 'APPROVED', 'RENTED', '2024-05-10 09:15:00', false),
                                                                                                                                                         ('Apartament 3 camere Dacia', 'Str. Cluj 22, Timișoara', 'APARTMENT', 1600.00, 45.7600, 21.2400, 12, 'APPROVED', 'RENTED', '2024-02-28 16:45:00', false),
                                                                                                                                                         ('Penthouse Centru', 'Str. Mercy 1, Timișoara', 'APARTMENT', 2200.00, 45.7550, 21.2280, 13, 'APPROVED', 'RENTED', '2024-01-15 11:20:00', false),
                                                                                                                                                         ('Studio Modern Torontalului', 'Calea Torontalului 45, Timișoara', 'APARTMENT', 850.00, 45.7520, 21.2350, 13, 'APPROVED', 'RENTED', '2024-06-01 13:00:00', true);

INSERT INTO leases (tenant_id, property_id, start_date, end_date, monthly_rent, status, lease_termination_status, created_at) VALUES
                                                                                                                                  (4, 5, '2023-01-01', '2023-12-31', 700.00, 'TERMINATED', 'APPROVED', '2022-12-15'),
                                                                                                                                  (5, 6, '2023-03-01', '2024-02-29', 950.00, 'TERMINATED', 'APPROVED', '2023-02-20'),
                                                                                                                                  (6, 5, '2024-01-01', '2025-12-31', 700.00, 'ACTIVE', 'NONE', '2023-12-20'),
                                                                                                                                  (7, 6, '2024-03-01', '2026-02-28', 950.00, 'ACTIVE', 'NONE', '2024-02-25'),
                                                                                                                                  (8, 7, '2024-06-01', '2026-05-31', 600.00, 'ACTIVE', 'NONE', '2024-05-25'),
                                                                                                                                  (9, 8, '2024-04-01', '2026-03-31', 1600.00, 'ACTIVE', 'NONE', '2024-03-25'),
                                                                                                                                  (10, 9, '2024-02-01', '2026-01-31', 2200.00, 'ACTIVE', 'NONE', '2024-01-25'),
                                                                                                                                  (6, 10, '2024-07-01', '2026-06-30', 850.00, 'ACTIVE', 'NONE', '2024-06-25');

INSERT INTO payments (lease_id, amount, payment_date, payment_method, status, was_late) VALUES
                                                                                            (6, 700.00, '2024-01-01', 'BANK_TRANSFER', 'COMPLETED', false),
                                                                                            (6, 700.00, '2024-02-01', 'BANK_TRANSFER', 'COMPLETED', false),
                                                                                            (6, 700.00, '2024-03-01', 'BANK_TRANSFER', 'COMPLETED', false),
                                                                                            (6, 700.00, '2024-04-01', 'BANK_TRANSFER', 'COMPLETED', false),
                                                                                            (6, 700.00, '2024-05-01', 'BANK_TRANSFER', 'COMPLETED', false),
                                                                                            (6, 700.00, '2024-06-01', 'BANK_TRANSFER', 'COMPLETED', false),
                                                                                            (6, 700.00, '2024-07-01', 'BANK_TRANSFER', 'COMPLETED', false),
                                                                                            (6, 700.00, '2024-08-01', 'BANK_TRANSFER', 'COMPLETED', false),
                                                                                            (6, 700.00, '2024-09-01', 'BANK_TRANSFER', 'COMPLETED', false),
                                                                                            (6, 700.00, '2024-10-01', 'BANK_TRANSFER', 'COMPLETED', false),
                                                                                            (6, 700.00, '2024-11-01', 'BANK_TRANSFER', 'COMPLETED', false),
                                                                                            (6, 700.00, '2024-12-01', 'BANK_TRANSFER', 'COMPLETED', false),
                                                                                            (6, 700.00, '2025-01-01', 'BANK_TRANSFER', 'COMPLETED', false),
                                                                                            (6, 700.00, '2025-02-01', 'BANK_TRANSFER', 'COMPLETED', false),
                                                                                            (6, 700.00, '2025-03-01', 'BANK_TRANSFER', 'COMPLETED', false),
                                                                                            (6, 700.00, '2025-04-01', 'BANK_TRANSFER', 'COMPLETED', false),
                                                                                            (6, 700.00, '2025-05-01', 'BANK_TRANSFER', 'COMPLETED', false),
                                                                                            (6, 700.00, '2025-06-01', 'BANK_TRANSFER', 'COMPLETED', false),
                                                                                            (7, 950.00, '2024-03-01', 'CREDIT_CARD', 'COMPLETED', false),
                                                                                            (7, 950.00, '2024-04-05', 'CREDIT_CARD', 'COMPLETED', true),
                                                                                            (7, 950.00, '2024-05-01', 'CREDIT_CARD', 'COMPLETED', false),
                                                                                            (7, 950.00, '2024-06-01', 'CREDIT_CARD', 'COMPLETED', false),
                                                                                            (7, 950.00, '2024-07-08', 'CREDIT_CARD', 'COMPLETED', true),
                                                                                            (7, 950.00, '2024-08-01', 'CREDIT_CARD', 'COMPLETED', false),
                                                                                            (7, 950.00, '2024-09-01', 'CREDIT_CARD', 'COMPLETED', false),
                                                                                            (7, 950.00, '2024-10-01', 'CREDIT_CARD', 'COMPLETED', false),
                                                                                            (7, 950.00, '2024-11-01', 'CREDIT_CARD', 'COMPLETED', false),
                                                                                            (7, 950.00, '2024-12-01', 'CREDIT_CARD', 'COMPLETED', false),
                                                                                            (7, 950.00, '2025-01-01', 'CREDIT_CARD', 'COMPLETED', false),
                                                                                            (7, 950.00, '2025-02-10', 'CREDIT_CARD', 'COMPLETED', true),
                                                                                            (7, 950.00, '2025-03-01', 'CREDIT_CARD', 'COMPLETED', false),
                                                                                            (7, 950.00, '2025-04-01', 'CREDIT_CARD', 'COMPLETED', false),
                                                                                            (7, 950.00, '2025-05-01', 'CREDIT_CARD', 'COMPLETED', false),
                                                                                            (7, 950.00, '2025-06-01', 'CREDIT_CARD', 'COMPLETED', false),
                                                                                            (8, 600.00, '2024-06-01', 'BANK_TRANSFER', 'COMPLETED', false),
                                                                                            (8, 600.00, '2024-07-15', 'BANK_TRANSFER', 'COMPLETED', true),
                                                                                            (8, 600.00, '2024-08-10', 'BANK_TRANSFER', 'COMPLETED', true),
                                                                                            (8, 600.00, '2024-09-01', 'BANK_TRANSFER', 'COMPLETED', false),
                                                                                            (8, 600.00, '2024-10-12', 'BANK_TRANSFER', 'COMPLETED', true),
                                                                                            (8, 600.00, '2024-11-01', 'BANK_TRANSFER', 'COMPLETED', false),
                                                                                            (8, 600.00, '2024-12-20', 'BANK_TRANSFER', 'COMPLETED', true),
                                                                                            (8, 600.00, '2025-01-01', 'BANK_TRANSFER', 'COMPLETED', false),
                                                                                            (8, 600.00, '2025-02-08', 'BANK_TRANSFER', 'COMPLETED', true),
                                                                                            (8, 600.00, '2025-03-01', 'BANK_TRANSFER', 'COMPLETED', false),
                                                                                            (8, 600.00, '2025-04-18', 'BANK_TRANSFER', 'COMPLETED', true),
                                                                                            (8, 600.00, '2025-05-01', 'BANK_TRANSFER', 'COMPLETED', false),
                                                                                            (8, 600.00, '2025-06-01', 'BANK_TRANSFER', 'COMPLETED', false);

INSERT INTO properties (name, address, type, rent_amount, latitude, longitude, owner_id, validation_status, property_status, created_at, is_flagged) VALUES
    ('Apartament problematic Elisabetin', 'Str. Take Ionescu 33, Timișoara', 'APARTMENT', 1100.00, 45.7420, 21.2180, 11, 'APPROVED', 'AVAILABLE', '2024-05-15 12:00:00', true);

INSERT INTO reviews (tenant_id, property_id, landlord_id, lease_id, rating, comment, created_at, review_type) VALUES
                                                                                                                  (4, 5, 11, 4, 5, 'Stefan a fost un proprietar excelent! Întotdeauna receptiv și a rezolvat rapid toate problemele.', '2024-01-15 10:00:00', 'TENANT_TO_LANDLORD'),
                                                                                                                  (5, 6, 11, 5, 3, 'Apartamentul a avut multe probleme, dar Stefan a încercat să le rezolve. Comunicarea putea fi mai bună.', '2024-03-10 14:30:00', 'TENANT_TO_LANDLORD'),
                                                                                                                  (6, 1, 2, 1, 4, 'Maria este o proprietară bună, dar uneori răspunde greu la telefon.', '2024-06-20 09:00:00', 'TENANT_TO_LANDLORD'),
                                                                                                                  (7, 3, 3, 2, 5, 'Gheorghe este foarte profesionist și se ocupă prompt de toate cererile.', '2024-05-15 16:00:00', 'TENANT_TO_LANDLORD'),
                                                                                                                  (8, 4, 3, 3, 2, 'Penthouse-ul nu era în starea promisă. Proprietarul nu a fost foarte cooperant.', '2024-04-25 11:30:00', 'TENANT_TO_LANDLORD');

INSERT INTO reviews (tenant_id, property_id, landlord_id, lease_id, rating, comment, created_at, review_type) VALUES
                                                                                                                  (4, 5, 11, 4, 4, 'Alexandru a fost un chiriaș responsabil. A plătit la timp și a avut grijă de proprietate.', '2024-01-20 11:00:00', 'LANDLORD_TO_TENANT'),
                                                                                                                  (5, 6, 11, 5, 3, 'Cristina a plătit regulat, dar a făcut mult zgomot și au fost plângeri de la vecini.', '2024-03-15 15:00:00', 'LANDLORD_TO_TENANT'),
                                                                                                                  (6, 1, 2, 1, 5, 'Chiriaș excelent! Foarte curat și organizat, plăți întotdeauna la timp.', '2024-06-25 10:30:00', 'LANDLORD_TO_TENANT'),
                                                                                                                  (7, 3, 3, 2, 4, 'Elena este o chiriașă bună, deși uneori plătește cu câteva zile întârziere.', '2024-05-20 17:00:00', 'LANDLORD_TO_TENANT'),
                                                                                                                  (8, 4, 3, 3, 2, 'Mihai plătește des cu întârziere și nu întreține proprietatea corespunzător.', '2024-04-30 12:00:00', 'LANDLORD_TO_TENANT');

UPDATE maintenance_requests
SET updated_at = DATEADD('HOUR', 2, created_at), status = 'COMPLETED'
WHERE request_id IN (3, 5);

UPDATE maintenance_requests
SET updated_at = DATEADD('DAY', 5, created_at)
WHERE request_id IN (4, 6);

INSERT INTO maintenance_requests (lease_id, description, status, cost, created_at, updated_at, is_fixed) VALUES
                                                                                                             (6, 'Ușa de la intrare scârțâie tare.', 'COMPLETED', 50.00, '2025-05-10 09:00:00', '2025-05-10 14:00:00', true),
                                                                                                             (7, 'Centrala nu pornește.', 'COMPLETED', 200.00, '2025-05-15 07:30:00', '2025-05-17 16:00:00', true),
                                                                                                             (8, 'Robinet stricat în baie.', 'COMPLETED', 75.00, '2025-05-20 10:00:00', '2025-05-20 15:00:00', true),
                                                                                                             (9, 'Geam spart în dormitor.', 'COMPLETED', 150.00, '2025-05-25 11:00:00', '2025-05-25 17:00:00', true),
                                                                                                             (10, 'Infiltrații pe tavan.', 'IN_PROGRESS', NULL, '2025-06-10 08:00:00', '2025-06-15 10:00:00', false),
                                                                                                             (11, 'Prize defecte în bucătărie.', 'PENDING', NULL, '2025-06-20 14:00:00', '2025-06-20 14:00:00', false);

INSERT INTO landlord_stats (landlord_id, rating_score, maintenance_score, behavior_score, overall_score, completed_leases, avg_maintenance_response_time, flagged_properties, last_updated, version) VALUES
                                                                                                                                                                                                         (2, 4.5, 5.0, 5.0, 4.75, 2, 2, 0, CURRENT_TIMESTAMP, 0),
                                                                                                                                                                                                         (3, 3.5, 2.0, 4.0, 3.05, 3, 120, 1, CURRENT_TIMESTAMP, 0),
                                                                                                                                                                                                         (11, 4.0, 3.5, 3.0, 3.65, 2, 36, 1, CURRENT_TIMESTAMP, 0),
                                                                                                                                                                                                         (12, 5.0, 5.0, 5.0, 5.0, 0, 6, 0, CURRENT_TIMESTAMP, 0),
                                                                                                                                                                                                         (13, 3.5, 1.0, 3.0, 2.2, 0, 120, 1, CURRENT_TIMESTAMP, 0);

INSERT INTO tenant_stats (tenant_id, payment_score, feedback_score, overall_score, total_payments, late_payments, on_time_payments, payment_punctuality_ratio, active_leases, completed_leases, last_updated, version) VALUES
    (4, 5.0, 4.0, 4.7, 20, 0, 20, 1.0, 2, 1, CURRENT_TIMESTAMP, 0);