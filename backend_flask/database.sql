-- =====================================================
-- Base de Datos: Noticias Locales Ibarra
-- =====================================================

CREATE DATABASE IF NOT EXISTS noticias_ibarra2
    CHARACTER SET utf8mb4
    COLLATE utf8mb4_unicode_ci;

USE noticias_ibarra2;

-- =====================================================
-- TABLA: parroquias (12 parroquias de Ibarra)
-- =====================================================

CREATE TABLE IF NOT EXISTS parroquias (
    id INT PRIMARY KEY AUTO_INCREMENT,
    nombre VARCHAR(100) NOT NULL,
    tipo ENUM('urbana', 'rural') NOT NULL,
    latitud DECIMAL(10, 8) NOT NULL,
    longitud DECIMAL(11, 8) NOT NULL,
    descripcion TEXT,
    poblacion INT,
    superficie_km2 DECIMAL(8, 2),
    INDEX idx_tipo (tipo)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;

-- Datos de las 12 parroquias de Ibarra
INSERT INTO parroquias (nombre, tipo, latitud, longitud, descripcion, poblacion) VALUES
-- Urbanas (5)
('El Sagrario', 'urbana', 0.3476, -78.1223, 'Centro histórico de Ibarra', 15000),
('San Francisco', 'urbana', 0.3490, -78.1290, 'Zona norte de la ciudad', 20000),
('La Dolorosa del Priorato', 'urbana', 0.3515, -78.1180, 'Zona sur de Ibarra', 18000),
('Caranqui', 'urbana', 0.3610, -78.1100, 'Zona oriental, zona arqueológica', 12000),
('Alpachaca', 'urbana', 0.3370, -78.1190, 'Zona occidental de la ciudad', 14000),

-- Rurales (7)
('La Esperanza', 'rural', 0.3923, -78.0927, 'Comunidad indígena, artesanías', 8000),
('Angochagua', 'rural', 0.4015, -78.0680, 'Zona rural este', 5000),
('Ambuquí', 'rural', 0.4500, -78.2100, 'Valle subtropical', 6000),
('Salinas', 'rural', 0.5100, -77.9300, 'Norte rural de Ibarra', 4000),
('La Carolina', 'rural', 0.6000, -78.1500, 'Noreste alejado', 3000),
('San Antonio', 'rural', 0.3300, -78.2000, 'Famosa por artesanías', 7000),
('Lita', 'rural', 0.8700, -78.4500, 'Zona tropical al norte', 2000);

-- =====================================================
-- TABLA: categorias (categorías de noticias)
-- =====================================================

CREATE TABLE IF NOT EXISTS categorias (
    id INT PRIMARY KEY AUTO_INCREMENT,
    nombre VARCHAR(100) NOT NULL UNIQUE,
    descripcion TEXT,
    icono VARCHAR(50),
    color VARCHAR(20),
    orden INT DEFAULT 0,
    activa BOOLEAN DEFAULT TRUE
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;

-- Datos de categorías
INSERT INTO categorias (nombre, descripcion, icono, color, orden) VALUES
('Deportes', 'Noticias deportivas locales', '⚽', '#FF6B35', 1),
('Política', 'Actualidad política de Ibarra', '🏛️', '#004E89', 2),
('Cultura', 'Eventos culturales y artísticos', '🎭', '#9B59B6', 3),
('Tecnología', 'Innovación y tecnología', '💻', '#1ABC9C', 4),
('Economía', 'Economía local y negocios', '💰', '#F39C12', 5),
('Educación', 'Educación y formación', '📚', '#3498DB', 6),
('Salud', 'Salud y bienestar', '🏥', '#E74C3C', 7),
('Turismo', 'Turismo local', '🗺️', '#16A085', 8),
('Seguridad', 'Seguridad ciudadana', '🚨', '#C0392B', 9),
('Ambiente', 'Medio ambiente', '🌿', '#27AE60', 10);

-- =====================================================
-- TABLA: usuarios
-- =====================================================

CREATE TABLE IF NOT EXISTS usuarios (
    id INT PRIMARY KEY AUTO_INCREMENT,
    nombre_completo VARCHAR(255) NOT NULL,
    email VARCHAR(255) NOT NULL UNIQUE,
    password_hash VARCHAR(255) NOT NULL,
    telefono VARCHAR(20),
    fecha_registro TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    fecha_ultima_conexion TIMESTAMP NULL,
    activo BOOLEAN DEFAULT TRUE,
    rol ENUM('usuario', 'admin', 'moderador') DEFAULT 'usuario',
    avatar_url VARCHAR(500),
    INDEX idx_email (email),
    INDEX idx_activo (activo)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;

-- Usuario de prueba
INSERT INTO usuarios (nombre_completo, email, password_hash, rol) VALUES
('Richard Ortega', 'richard.ortega778@ist17dejulio.edu.ec', '$2b$12$hash', 'admin');

-- =====================================================
-- TABLA: noticias
-- =====================================================

CREATE TABLE IF NOT EXISTS noticias (
    id INT PRIMARY KEY AUTO_INCREMENT,
    titulo VARCHAR(255) NOT NULL,
    descripcion TEXT NOT NULL,
    contenido TEXT,
    imagen_url VARCHAR(500),
    autor_id INT,
    categoria_id INT,
    parroquia_id INT,
    latitud DECIMAL(10, 8),
    longitud DECIMAL(11, 8),
    ubicacion VARCHAR(255),
    fecha_publicacion TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    fecha_actualizacion TIMESTAMP DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
    visualizaciones INT DEFAULT 0,
    destacada BOOLEAN DEFAULT FALSE,
    activa BOOLEAN DEFAULT TRUE,
    fuente VARCHAR(255),
    tags VARCHAR(500),
    FOREIGN KEY (autor_id) REFERENCES usuarios(id) ON DELETE SET NULL,
    FOREIGN KEY (categoria_id) REFERENCES categorias(id) ON DELETE SET NULL,
    FOREIGN KEY (parroquia_id) REFERENCES parroquias(id) ON DELETE SET NULL,
    INDEX idx_coordenadas (latitud, longitud),
    INDEX idx_fecha_categoria (fecha_publicacion, categoria_id),
    INDEX idx_activa (activa),
    FULLTEXT INDEX idx_fulltext (titulo, descripcion, contenido)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;

-- Noticias de prueba
INSERT INTO noticias (titulo, descripcion, contenido, categoria_id, parroquia_id, latitud, longitud, ubicacion, destacada) VALUES
('Inauguración de nuevo parque en San Francisco', 'La municipalidad inauguró un moderno parque recreativo', 'El alcalde de Ibarra inauguró hoy un nuevo parque...', 1, 2, 0.3490, -78.1290, 'San Francisco, Ibarra', TRUE),
('Clasificación al campeonato nacional', 'Equipo de Imbabura clasifica a la final', 'El equipo representante de Imbabura logró...', 1, 1, 0.3476, -78.1223, 'El Sagrario, Ibarra', TRUE),
('Descubrimiento arqueológico en Caranqui', 'Hallazgo importante en zona arqueológica', 'Arqueólogos encontraron vestigios de la cultura Caranqui...', 3, 4, 0.3610, -78.1100, 'Caranqui, Ibarra', TRUE),
('Nueva sede de la UTN', 'Universidad Técnica del Norte inaugura campus', 'La UTN amplía su infraestructura con un nuevo campus...', 6, 3, 0.3515, -78.1180, 'La Dolorosa, Ibarra', FALSE),
('Festival de la Cosecha en La Esperanza', 'Comunidad celebra tradición ancestral', 'La parroquia de La Esperanza celebra su festival anual...', 3, 6, 0.3923, -78.0927, 'La Esperanza, Ibarra', TRUE),
('Presupuesto municipal 2025', 'Alcaldía presenta plan de inversiones', 'El alcalde presentó el presupuesto para el próximo año...', 2, 1, 0.3476, -78.1223, 'El Sagrario, Ibarra', FALSE);

-- =====================================================
-- TABLA: eventos
-- =====================================================

CREATE TABLE IF NOT EXISTS eventos (
    id INT PRIMARY KEY AUTO_INCREMENT,
    descripcion TEXT NOT NULL,
    fecha DATETIME NOT NULL,
    ubicacion VARCHAR(255),
    creador_id INT,
    parroquia_id INT,
    latitud DECIMAL(10, 8),
    longitud DECIMAL(11, 8),
    categoria_evento ENUM('cultural', 'deportivo', 'educativo', 'comunitario', 'otro') DEFAULT 'otro',
    cupo_maximo INT,
    cupo_actual INT DEFAULT 0,
    costo DECIMAL(10, 2) DEFAULT 0.00,
    imagen_url VARCHAR(500),
    contacto_telefono VARCHAR(20),
    contacto_email VARCHAR(255),
    estado ENUM('programado', 'en_curso', 'finalizado', 'cancelado') DEFAULT 'programado',
    fecha_creacion TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    FOREIGN KEY (creador_id) REFERENCES usuarios(id) ON DELETE SET NULL,
    FOREIGN KEY (parroquia_id) REFERENCES parroquias(id) ON DELETE SET NULL,
    INDEX idx_fecha (fecha),
    INDEX idx_estado (estado)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;

-- Eventos de prueba
INSERT INTO eventos (descripcion, fecha, ubicacion, creador_id, parroquia_id, latitud, longitud, categoria_evento, cupo_maximo, costo, estado) VALUES
('Concierto de música andina', '2025-11-15 18:00:00', 'Parque Central de Ibarra', 1, 1, 0.3476, -78.1223, 'cultural', 100, 0.00, 'programado'),
('Maratón 10K Ciudad Blanca', '2025-11-20 07:00:00', 'Parque Ciudad Blanca', 1, 2, 0.3490, -78.1290, 'deportivo', 200, 5.00, 'programado'),
('Feria de emprendimientos', '2025-11-25 09:00:00', 'Plaza de los Ponchos', 1, 4, 0.3610, -78.1100, 'comunitario', 50, 0.00, 'programado'),
('Taller de cerámica ancestral', '2025-11-30 14:00:00', 'Centro Cultural La Esperanza', 1, 6, 0.3923, -78.0927, 'educativo', 30, 3.00, 'programado'),
('Festival gastronómico', '2025-12-05 11:00:00', 'Parque Abdón Calderón', 1, 1, 0.3476, -78.1223, 'cultural', 150, 0.00, 'programado');

-- =====================================================
-- CONSULTAS ÚTILES
-- =====================================================

-- Ver todas las parroquias
-- SELECT * FROM parroquias ORDER BY tipo, nombre;

-- Ver eventos próximos
-- SELECT * FROM eventos WHERE fecha >= NOW() AND estado = 'programado' ORDER BY fecha;

-- Ver noticias por parroquia
-- SELECT n.*, p.nombre as parroquia_nombre FROM noticias n LEFT JOIN parroquias p ON n.parroquia_id = p.id;

-- Búsqueda geográfica (ejemplo: noticias en radio de 5km desde el centro)
-- SELECT *, (6371 * acos(cos(radians(0.3476)) * cos(radians(latitud)) * cos(radians(longitud) - radians(-78.1223)) + sin(radians(0.3476)) * sin(radians(latitud)))) AS distancia_km FROM noticias HAVING distancia_km <= 5 ORDER BY distancia_km;
