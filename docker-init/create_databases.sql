-- Script này sẽ chạy 1 lần duy nhất khi khởi tạo container

CREATE DATABASE cinemesh_auth_db;
CREATE DATABASE cinemesh_movie_db;
CREATE DATABASE cinemesh_theater_db;
CREATE DATABASE cinemesh_booking_db;
CREATE DATABASE cinemesh_payment_db;
CREATE DATABASE cinemesh_notification_db;

-- Cấp quyền (Optional - vì user cinemesh thường là owner rồi)
GRANT ALL PRIVILEGES ON DATABASE cinemesh_auth_db TO cinemesh;
GRANT ALL PRIVILEGES ON DATABASE cinemesh_movie_db TO cinemesh;
GRANT ALL PRIVILEGES ON DATABASE cinemesh_theater_db TO cinemesh;
GRANT ALL PRIVILEGES ON DATABASE cinemesh_booking_db TO cinemesh;
GRANT ALL PRIVILEGES ON DATABASE cinemesh_payment_db TO cinemesh;
GRANT ALL PRIVILEGES ON DATABASE cinemesh_notification_db TO cinemesh;

-- master data: ROLES
insert into roles (id, name)values (gen_random_uuid(), 'ROLE_CUSTOMER');
insert into roles (id, name)values (gen_random_uuid(), 'ROLE_ADMIN');
