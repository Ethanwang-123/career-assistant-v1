insert into app_user (email, password, created_at)
values ('demo@example.com', '$2a$10$7EqJtq98hPqEX7fNZaFWoOhiJ4w7jMLL3NoGp0xG7ypPrxqM0mLwS', '2026-06-18T10:00:00Z');

insert into job_application (company_name, role_title, location, status, application_date, deadline, notes, created_at, updated_at, app_user_id)
values ('Cambridge AI Lab', 'Junior Backend Developer', 'Cambridge', 'APPLIED', '2026-06-01', '2026-07-01', 'Cloud platform team using Java and Spring Boot.', '2026-06-18T10:00:00Z', '2026-06-18T10:00:00Z', 1);

insert into job_application (company_name, role_title, location, status, application_date, deadline, notes, created_at, updated_at, app_user_id)
values ('London Cloud Systems', 'Software Engineer', 'London', 'ONLINE_TEST', '2026-06-05', '2026-07-10', 'Online coding test for a cloud infrastructure role.', '2026-06-18T10:00:00Z', '2026-06-18T10:00:00Z', 1);

insert into job_application (company_name, role_title, location, status, application_date, deadline, notes, created_at, updated_at, app_user_id)
values ('Oxford Health Tech', 'Backend Engineer', 'Oxford', 'INTERVIEW', '2026-06-08', null, 'First interview booked with engineering manager.', '2026-06-18T10:00:00Z', '2026-06-18T10:00:00Z', 1);

insert into job_application (company_name, role_title, location, status, application_date, deadline, notes, created_at, updated_at, app_user_id)
values ('Bristol FinTech', 'Java Developer', 'Bristol', 'REJECTED', '2026-05-20', '2026-06-15', 'Rejected after resume screen.', '2026-06-18T10:00:00Z', '2026-06-18T10:00:00Z', 1);

insert into job_application (company_name, role_title, location, status, application_date, deadline, notes, created_at, updated_at, app_user_id)
values ('Remote Startup', 'Graduate Developer', 'Remote', 'NOT_APPLIED', null, '2026-07-20', 'Interesting role to apply for later.', '2026-06-18T10:00:00Z', '2026-06-18T10:00:00Z', 1);
