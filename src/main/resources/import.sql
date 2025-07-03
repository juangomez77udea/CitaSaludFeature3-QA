-- Roles
INSERT INTO rol (rol_id, nombre, permisos) VALUES (1, 'ADMINISTRADOR', 'ALL_PERMISSIONS');
INSERT INTO rol (rol_id, nombre, permisos) VALUES (2, 'COORDINADOR', 'MANAGE_SCHEDULES');
INSERT INTO rol (rol_id, nombre, permisos) VALUES (3, 'MEDICO', 'VIEW_SCHEDULE');

-- Especialidades
INSERT INTO especialidad (especialidad_id, especialidad, descripcion) VALUES (1, 'Cardiología', 'Especialidad del corazón');
INSERT INTO especialidad (especialidad_id, especialidad, descripcion) VALUES (2, 'Dermatología', 'Especialidad de la piel');
INSERT INTO especialidad (especialidad_id, especialidad, descripcion) VALUES (3, 'Pediatría', 'Especialidad infantil');

-- Admin (contraseña: admin1234)
INSERT INTO medico (medico_id, nombre, apellido, email, password, tipo_documento, numero_documento, especialidad_id, rol_id) VALUES (99, 'Admin', 'Principal', 'admin@citasalud.com', '$2a$10$wfSBuDeFQoqK3NMilMH.JeFLBaJcyDGYmAKzByXXNozyTns82aiNK', 'NIT', '999999999', 1, 1);

-- Medico (contraseña: medico1234)
INSERT INTO medico (medico_id, nombre, apellido, email, password, tipo_documento, numero_documento, especialidad_id, rol_id) VALUES (100, 'Carlos', 'Santana', 'carlos.santana@example.com', '$2a$10$gnJf1bVhNtv38B9wcXJOy.dblo4Evqjv6EGRps9lE5J8vDNINr9Rm', 'CC', '12345678', 1, 3);