-- Este archivo se ejecuta automáticamente al iniciar la aplicación.

-- Insertar Roles
INSERT INTO rol (rol_id, nombre, permisos) VALUES (1, 'ADMINISTRADOR', 'ALL_PERMISSIONS') ON CONFLICT (rol_id) DO NOTHING;
INSERT INTO rol (rol_id, nombre, permisos) VALUES (2, 'COORDINADOR', 'MANAGE_SCHEDULES') ON CONFLICT (rol_id) DO NOTHING;
INSERT INTO rol (rol_id, nombre, permisos) VALUES (3, 'MEDICO', 'VIEW_SCHEDULE') ON CONFLICT (rol_id) DO NOTHING;

-- Insertar Especialidades
INSERT INTO especialidad (especialidad_id, especialidad, descripcion) VALUES (1, 'Cardiología', 'Especialidad del corazón') ON CONFLICT (especialidad_id) DO NOTHING;
INSERT INTO especialidad (especialidad_id, especialidad, descripcion) VALUES (2, 'Dermatología', 'Especialidad de la piel') ON CONFLICT (especialidad_id) DO NOTHING;
INSERT INTO especialidad (especialidad_id, especialidad, descripcion) VALUES (3, 'Pediatría', 'Especialidad infantil') ON CONFLICT (especialidad_id) DO NOTHING;

-- Insertar un usuario ADMINISTRADOR
-- Contraseña en texto plano: "admin1234"
INSERT INTO medico (medico_id, nombre, apellido, email, password, tipo_documento, numero_documento, especialidad_id, rol_id)
VALUES (99, 'Admin', 'Principal', 'admin@citasalud.com', 'PEGA_AQUI_EL_NUEVO_HASH_DEL_ADMIN', 'NIT', '999999999', 1, 1) ON CONFLICT (medico_id) DO NOTHING;

-- Insertar un usuario MEDICO
-- Contraseña en texto plano: "medico1234"
INSERT INTO medico (medico_id, nombre, apellido, email, password, tipo_documento, numero_documento, especialidad_id, rol_id)
VALUES (100, 'Carlos', 'Santana', 'carlos.santana@example.com', 'PEGA_AQUI_EL_NUEVO_HASH_DEL_MEDICO', 'CC', '12345678', 1, 3) ON CONFLICT (medico_id) DO NOTHING;