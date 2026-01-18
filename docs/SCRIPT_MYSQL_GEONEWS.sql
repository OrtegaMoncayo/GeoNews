-- =====================================================
-- Script de Base de Datos MySQL - GeoNews
-- Versión: 0.1.0 (Sin módulo de Eventos)
-- Fecha: Enero 2026
-- =====================================================

-- Crear base de datos
CREATE DATABASE IF NOT EXISTS geonews_db
CHARACTER SET utf8mb4
COLLATE utf8mb4_unicode_ci;

USE geonews_db;

-- =====================================================
-- TABLA: categorias
-- Descripción: Categorías de noticias
-- =====================================================
CREATE TABLE IF NOT EXISTS categorias (
    id INT PRIMARY KEY AUTO_INCREMENT,
    nombre VARCHAR(50) NOT NULL UNIQUE,
    descripcion TEXT,
    icono VARCHAR(50),
    color VARCHAR(7) COMMENT 'Color en formato hex (#RRGGBB)',
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
    INDEX idx_nombre (nombre)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;

-- =====================================================
-- TABLA: parroquias
-- Descripción: Parroquias de Ibarra con geolocalización
-- =====================================================
CREATE TABLE IF NOT EXISTS parroquias (
    id INT PRIMARY KEY AUTO_INCREMENT,
    nombre VARCHAR(100) NOT NULL UNIQUE,
    tipo ENUM('urbana', 'rural') NOT NULL DEFAULT 'urbana',
    descripcion TEXT,
    latitud DECIMAL(10, 8) NOT NULL COMMENT 'Latitud en grados decimales',
    longitud DECIMAL(11, 8) NOT NULL COMMENT 'Longitud en grados decimales',
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
    INDEX idx_nombre (nombre),
    INDEX idx_tipo (tipo),
    INDEX idx_coordenadas (latitud, longitud)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;

-- =====================================================
-- TABLA: usuarios
-- Descripción: Usuarios registrados en la plataforma
-- =====================================================
CREATE TABLE IF NOT EXISTS usuarios (
    id INT PRIMARY KEY AUTO_INCREMENT,
    firebase_uid VARCHAR(128) UNIQUE COMMENT 'UID de Firebase Auth',
    nombre VARCHAR(100) NOT NULL,
    apellido VARCHAR(100),
    email VARCHAR(255) NOT NULL UNIQUE,
    password_hash VARCHAR(255) COMMENT 'Hash de contraseña (si no usa Firebase Auth)',
    foto_perfil VARCHAR(500),
    bio TEXT,
    telefonocelular VARCHAR(20),
    ubicacion VARCHAR(200),
    fecha_registro BIGINT COMMENT 'Timestamp en milisegundos',
    ultima_conexion BIGINT COMMENT 'Timestamp en milisegundos',
    noticias_publicadas INT DEFAULT 0,
    noticias_leidas INT DEFAULT 0,
    verificado BOOLEAN DEFAULT FALSE,
    tipo_usuario ENUM('usuario', 'reportero', 'admin') DEFAULT 'usuario',
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
    INDEX idx_email (email),
    INDEX idx_tipo_usuario (tipo_usuario),
    INDEX idx_firebase_uid (firebase_uid)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;

-- =====================================================
-- TABLA: noticias
-- Descripción: Noticias publicadas con geolocalización
-- =====================================================
CREATE TABLE IF NOT EXISTS noticias (
    id INT PRIMARY KEY AUTO_INCREMENT,
    firestore_id VARCHAR(100) UNIQUE COMMENT 'ID del documento en Firestore',
    titulo VARCHAR(255) NOT NULL,
    descripcion TEXT NOT NULL,
    contenido LONGTEXT NOT NULL,
    imagen_url VARCHAR(500),
    cita_destacada TEXT COMMENT 'Cita o frase destacada de la noticia',
    hashtags VARCHAR(500) COMMENT 'Hashtags separados por espacios',
    impacto_comunitario TEXT COMMENT 'Descripción del impacto en la comunidad',
    autor_id INT NOT NULL,
    autor_nombre VARCHAR(200) COMMENT 'Desnormalizado para performance',
    categoria_id INT NOT NULL,
    categoria_nombre VARCHAR(50) COMMENT 'Desnormalizado para performance',
    parroquia_id INT,
    fecha_creacion BIGINT NOT NULL COMMENT 'Timestamp en milisegundos',
    fecha_actualizacion BIGINT COMMENT 'Timestamp en milisegundos',
    visualizaciones INT DEFAULT 0,
    latitud DECIMAL(10, 8) NOT NULL,
    longitud DECIMAL(11, 8) NOT NULL,
    ubicacion_texto VARCHAR(200),
    destacada BOOLEAN DEFAULT FALSE,
    estado ENUM('draft', 'published', 'archived') DEFAULT 'published',
    distancia DECIMAL(10, 2) COMMENT 'Distancia calculada del usuario (km)',
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
    FOREIGN KEY (autor_id) REFERENCES usuarios(id) ON DELETE CASCADE,
    FOREIGN KEY (categoria_id) REFERENCES categorias(id) ON DELETE RESTRICT,
    FOREIGN KEY (parroquia_id) REFERENCES parroquias(id) ON DELETE SET NULL,
    INDEX idx_autor (autor_id),
    INDEX idx_categoria (categoria_id),
    INDEX idx_fecha_creacion (fecha_creacion DESC),
    INDEX idx_estado (estado),
    INDEX idx_destacada (destacada),
    INDEX idx_coordenadas (latitud, longitud),
    INDEX idx_categoria_fecha (categoria_id, fecha_creacion DESC),
    INDEX idx_destacada_estado_fecha (destacada, estado, fecha_creacion DESC),
    FULLTEXT INDEX idx_fulltext_titulo (titulo),
    FULLTEXT INDEX idx_fulltext_contenido (contenido)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;

-- =====================================================
-- TABLA: notificaciones
-- Descripción: Notificaciones push enviadas a usuarios
-- =====================================================
CREATE TABLE IF NOT EXISTS notificaciones (
    id INT PRIMARY KEY AUTO_INCREMENT,
    usuario_id INT NOT NULL,
    titulo VARCHAR(255) NOT NULL,
    mensaje TEXT NOT NULL,
    tipo ENUM('noticia_destacada', 'noticia_cercana', 'sistema') DEFAULT 'sistema',
    noticia_id INT COMMENT 'ID de la noticia relacionada',
    categoria_id INT COMMENT 'ID de la categoría relacionada',
    leida BOOLEAN DEFAULT FALSE,
    fecha_envio BIGINT NOT NULL COMMENT 'Timestamp en milisegundos',
    fecha_leida BIGINT COMMENT 'Timestamp en milisegundos',
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    FOREIGN KEY (usuario_id) REFERENCES usuarios(id) ON DELETE CASCADE,
    FOREIGN KEY (noticia_id) REFERENCES noticias(id) ON DELETE CASCADE,
    FOREIGN KEY (categoria_id) REFERENCES categorias(id) ON DELETE SET NULL,
    INDEX idx_usuario_fecha (usuario_id, fecha_envio DESC),
    INDEX idx_usuario_leida (usuario_id, leida)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;

-- =====================================================
-- TABLA: noticias_guardadas
-- Descripción: Relación muchos a muchos entre usuarios y noticias guardadas
-- =====================================================
CREATE TABLE IF NOT EXISTS noticias_guardadas (
    id INT PRIMARY KEY AUTO_INCREMENT,
    usuario_id INT NOT NULL,
    noticia_id INT NOT NULL,
    fecha_guardado BIGINT NOT NULL COMMENT 'Timestamp en milisegundos',
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    FOREIGN KEY (usuario_id) REFERENCES usuarios(id) ON DELETE CASCADE,
    FOREIGN KEY (noticia_id) REFERENCES noticias(id) ON DELETE CASCADE,
    UNIQUE KEY unique_usuario_noticia (usuario_id, noticia_id),
    INDEX idx_usuario (usuario_id),
    INDEX idx_noticia (noticia_id),
    INDEX idx_fecha_guardado (fecha_guardado DESC)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;

-- =====================================================
-- TABLA: intereses_usuarios
-- Descripción: Categorías de interés de cada usuario
-- =====================================================
CREATE TABLE IF NOT EXISTS intereses_usuarios (
    id INT PRIMARY KEY AUTO_INCREMENT,
    usuario_id INT NOT NULL,
    categoria_id INT NOT NULL,
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    FOREIGN KEY (usuario_id) REFERENCES usuarios(id) ON DELETE CASCADE,
    FOREIGN KEY (categoria_id) REFERENCES categorias(id) ON DELETE CASCADE,
    UNIQUE KEY unique_usuario_categoria (usuario_id, categoria_id),
    INDEX idx_usuario (usuario_id),
    INDEX idx_categoria (categoria_id)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;

-- =====================================================
-- INSERCIÓN DE DATOS INICIALES
-- =====================================================

-- Insertar categorías predefinidas
INSERT INTO categorias (id, nombre, descripcion, icono, color) VALUES
(1, 'Política', 'Noticias políticas locales y nacionales', 'ic_politics', '#FF6B35'),
(2, 'Economía', 'Economía y finanzas locales', 'ic_economy', '#004E89'),
(3, 'Cultura', 'Eventos y actividades culturales', 'ic_culture', '#9B59B6'),
(4, 'Deportes', 'Deportes y actividades deportivas locales', 'ic_sports', '#27AE60'),
(5, 'Educación', 'Educación y ciencia', 'ic_education', '#F39C12'),
(6, 'Salud', 'Salud y bienestar', 'ic_health', '#E74C3C'),
(7, 'Seguridad', 'Seguridad ciudadana', 'ic_security', '#34495E'),
(8, 'Medio Ambiente', 'Ecología y medio ambiente', 'ic_environment', '#16A085'),
(9, 'Turismo', 'Turismo local', 'ic_tourism', '#8E44AD'),
(10, 'Tecnología', 'Tecnología e innovación', 'ic_tech', '#3498DB');

-- Insertar parroquias urbanas de Ibarra
INSERT INTO parroquias (id, nombre, tipo, descripcion, latitud, longitud) VALUES
(1, 'Ibarra Centro', 'urbana', 'Centro histórico de la ciudad de Ibarra', 0.35140000, -78.12670000),
(2, 'San Francisco', 'urbana', 'Parroquia urbana San Francisco', 0.34950000, -78.12890000),
(3, 'Caranqui', 'urbana', 'Parroquia urbana Caranqui', 0.36120000, -78.11980000),
(4, 'Alpachaca', 'urbana', 'Parroquia urbana Alpachaca', 0.34230000, -78.13120000),
(5, 'La Dolorosa del Priorato', 'urbana', 'Parroquia urbana La Dolorosa', 0.35670000, -78.12340000);

-- Insertar parroquias rurales de Ibarra
INSERT INTO parroquias (id, nombre, tipo, descripcion, latitud, longitud) VALUES
(6, 'San Antonio', 'rural', 'Parroquia rural San Antonio', 0.33470000, -78.21450000),
(7, 'La Esperanza', 'rural', 'Parroquia rural La Esperanza', 0.28910000, -78.09230000),
(8, 'Angochagua', 'rural', 'Parroquia rural Angochagua', 0.24560000, -78.07120000),
(9, 'Ambuquí', 'rural', 'Parroquia rural Ambuquí', 0.42340000, -78.23450000),
(10, 'Salinas', 'rural', 'Parroquia rural Salinas', 0.52340000, -77.98760000),
(11, 'La Carolina', 'rural', 'Parroquia rural La Carolina', 0.67890000, -78.45670000),
(12, 'Lita', 'rural', 'Parroquia rural Lita', 0.89120000, -78.56780000);

-- =====================================================
-- DATOS DE PRUEBA (OPCIONAL - COMENTADO)
-- =====================================================

-- Usuario de prueba (administrador)
-- INSERT INTO usuarios (firebase_uid, nombre, apellido, email, tipo_usuario, ubicacion, verificado, fecha_registro, ultima_conexion) VALUES
-- ('test_admin_001', 'Juan', 'Pérez', 'admin@geonews.ec', 'admin', 'Ibarra Centro', TRUE, UNIX_TIMESTAMP() * 1000, UNIX_TIMESTAMP() * 1000);

-- Usuario de prueba (reportero)
-- INSERT INTO usuarios (firebase_uid, nombre, apellido, email, tipo_usuario, ubicacion, verificado, fecha_registro, ultima_conexion) VALUES
-- ('test_reportero_001', 'María', 'González', 'reportero@geonews.ec', 'reportero', 'San Francisco', TRUE, UNIX_TIMESTAMP() * 1000, UNIX_TIMESTAMP() * 1000);

-- Noticia de prueba
-- INSERT INTO noticias (titulo, descripcion, contenido, autor_id, autor_nombre, categoria_id, categoria_nombre, parroquia_id, latitud, longitud, ubicacion_texto, destacada, estado, fecha_creacion, fecha_actualizacion) VALUES
-- ('Inauguración de ciclovía en Ibarra Centro',
--  'Se inauguró 2km de ciclovía ecológica',
--  'La alcaldía de Ibarra inauguró hoy una moderna ciclovía de 2 kilómetros en el centro histórico...',
--  1, 'Juan Pérez', 8, 'Medio Ambiente', 1, 0.35140000, -78.12670000, 'Ibarra Centro', TRUE, 'published',
--  UNIX_TIMESTAMP() * 1000, UNIX_TIMESTAMP() * 1000);

-- =====================================================
-- PROCEDIMIENTOS ALMACENADOS ÚTILES
-- =====================================================

DELIMITER //

-- Procedimiento para obtener noticias cercanas a una ubicación
CREATE PROCEDURE IF NOT EXISTS ObtenerNoticiasCercanas(
    IN p_latitud DECIMAL(10,8),
    IN p_longitud DECIMAL(11,8),
    IN p_radio_km DECIMAL(10,2)
)
BEGIN
    SELECT
        n.*,
        (
            6371 * ACOS(
                COS(RADIANS(p_latitud)) *
                COS(RADIANS(n.latitud)) *
                COS(RADIANS(n.longitud) - RADIANS(p_longitud)) +
                SIN(RADIANS(p_latitud)) *
                SIN(RADIANS(n.latitud))
            )
        ) AS distancia
    FROM noticias n
    WHERE n.estado = 'published'
    HAVING distancia <= p_radio_km
    ORDER BY distancia ASC;
END //

-- Procedimiento para incrementar visualizaciones
CREATE PROCEDURE IF NOT EXISTS IncrementarVisualizaciones(
    IN p_noticia_id INT
)
BEGIN
    UPDATE noticias
    SET visualizaciones = visualizaciones + 1,
        fecha_actualizacion = UNIX_TIMESTAMP() * 1000
    WHERE id = p_noticia_id;
END //

-- Procedimiento para obtener estadísticas de usuario
CREATE PROCEDURE IF NOT EXISTS ObtenerEstadisticasUsuario(
    IN p_usuario_id INT
)
BEGIN
    SELECT
        u.noticias_publicadas,
        u.noticias_leidas,
        COUNT(DISTINCT ng.id) AS noticias_guardadas,
        COUNT(DISTINCT iu.id) AS categorias_interes,
        DATEDIFF(NOW(), FROM_UNIXTIME(u.fecha_registro/1000)) AS dias_activo
    FROM usuarios u
    LEFT JOIN noticias_guardadas ng ON u.id = ng.usuario_id
    LEFT JOIN intereses_usuarios iu ON u.id = iu.usuario_id
    WHERE u.id = p_usuario_id
    GROUP BY u.id;
END //

DELIMITER ;

-- =====================================================
-- VISTAS ÚTILES
-- =====================================================

-- Vista de noticias con información completa
CREATE OR REPLACE VIEW v_noticias_completas AS
SELECT
    n.id,
    n.firestore_id,
    n.titulo,
    n.descripcion,
    n.contenido,
    n.imagen_url,
    n.cita_destacada,
    n.hashtags,
    n.impacto_comunitario,
    n.autor_id,
    n.autor_nombre,
    u.email AS autor_email,
    u.foto_perfil AS autor_foto,
    n.categoria_id,
    n.categoria_nombre,
    c.color AS categoria_color,
    c.icono AS categoria_icono,
    n.parroquia_id,
    p.nombre AS parroquia_nombre,
    p.tipo AS parroquia_tipo,
    n.latitud,
    n.longitud,
    n.ubicacion_texto,
    n.fecha_creacion,
    n.fecha_actualizacion,
    n.visualizaciones,
    n.destacada,
    n.estado,
    n.created_at,
    n.updated_at
FROM noticias n
LEFT JOIN usuarios u ON n.autor_id = u.id
LEFT JOIN categorias c ON n.categoria_id = c.id
LEFT JOIN parroquias p ON n.parroquia_id = p.id;

-- Vista de noticias destacadas publicadas
CREATE OR REPLACE VIEW v_noticias_destacadas AS
SELECT * FROM v_noticias_completas
WHERE estado = 'published' AND destacada = TRUE
ORDER BY fecha_creacion DESC;

-- Vista de estadísticas por categoría
CREATE OR REPLACE VIEW v_estadisticas_categorias AS
SELECT
    c.id,
    c.nombre,
    c.color,
    COUNT(n.id) AS total_noticias,
    SUM(n.visualizaciones) AS total_visualizaciones,
    AVG(n.visualizaciones) AS promedio_visualizaciones
FROM categorias c
LEFT JOIN noticias n ON c.id = n.categoria_id AND n.estado = 'published'
GROUP BY c.id, c.nombre, c.color
ORDER BY total_noticias DESC;

-- =====================================================
-- TRIGGERS
-- =====================================================

DELIMITER //

-- Trigger para actualizar contador de noticias publicadas del usuario
CREATE TRIGGER IF NOT EXISTS after_noticia_insert
AFTER INSERT ON noticias
FOR EACH ROW
BEGIN
    IF NEW.estado = 'published' THEN
        UPDATE usuarios
        SET noticias_publicadas = noticias_publicadas + 1
        WHERE id = NEW.autor_id;
    END IF;
END //

-- Trigger para actualizar última conexión al leer una noticia
CREATE TRIGGER IF NOT EXISTS after_visualizacion_update
AFTER UPDATE ON noticias
FOR EACH ROW
BEGIN
    IF NEW.visualizaciones > OLD.visualizaciones THEN
        -- Aquí podrías actualizar última conexión del usuario si tuvieras esa info
        -- Por ahora solo registramos el cambio
        SET @last_view = NOW();
    END IF;
END //

DELIMITER ;

-- =====================================================
-- NOTAS IMPORTANTES
-- =====================================================
-- 1. Esta base de datos NO incluye el módulo de Eventos (eliminado)
-- 2. Las coordenadas usan DECIMAL(10,8) y DECIMAL(11,8) para precisión GPS
-- 3. Los timestamps se almacenan en milisegundos (BIGINT) para compatibilidad con Firebase
-- 4. Se usan índices compuestos para optimizar consultas frecuentes
-- 5. Las relaciones foreign key aseguran integridad referencial
-- 6. Se incluyen vistas y procedimientos para operaciones comunes
-- 7. Compatible con MySQL 5.7+ y MariaDB 10.2+

-- =====================================================
-- FIN DEL SCRIPT
-- =====================================================
