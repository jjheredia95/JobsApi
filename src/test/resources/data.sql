INSERT INTO category (id, name, description) VALUES (1, 'Technology', 'Tech related jobs');
INSERT INTO category (id, name, description) VALUES (2, 'Healthcare', 'Healthcare related jobs');

INSERT INTO location (id, city, state) VALUES (1, 'New York', 'NY');
INSERT INTO location (id, city, state) VALUES (2, 'Los Angeles', 'CA');

INSERT INTO company (id, name, description, website, headquarters)
VALUES (1, 'TechCorp', 'A tech company', 'https://techcorp.com', 'New York');

INSERT INTO vacancy (id, name, description, category_id, company_id, published_date, open_date, status, featured, work_mode, employment_type)
VALUES (1, 'React Developer', 'Build UI using react.js', 1, 1, '2026-05-04', '2026-05-05', 'OPEN', true, 'REMOTE', 'FULL_TIME');

INSERT INTO vacancy (id, name, description, category_id, company_id, published_date, open_date, status, featured, work_mode, employment_type)
VALUES (2, 'Backend Developer', 'Develop backend services', 1, 1, '2026-05-04', '2026-05-05', 'OPEN', true, 'ONSITE', 'FULL_TIME');

INSERT INTO vacancy (id, name, description, category_id, company_id, published_date, open_date, status, featured, work_mode, employment_type)
VALUES (3, 'Registered Nurse', 'Provide patient care', 2, 1, '2026-05-04', '2026-05-05', 'OPEN', true, 'ONSITE', 'FULL_TIME');

INSERT INTO vacancy_location (vacancy_id, location_id) VALUES (1, 1);
INSERT INTO vacancy_location (vacancy_id, location_id) VALUES (2, 1);
INSERT INTO vacancy_location (vacancy_id, location_id) VALUES (3, 2);
