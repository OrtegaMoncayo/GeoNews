# Documentación del Proyecto GeoNews

Esta carpeta contiene toda la documentación técnica y académica del proyecto GeoNews v0.1.0.

## 📋 Contenido

### Documentación de Pruebas
- **PLAN_DE_PRUEBAS_GEONEWS.md** - Plan completo de pruebas con estrategia, metodología, recursos y cronograma
- **MATRIZ_TRAZABILIDAD_GEONEWS.md** - Matriz de trazabilidad con 125 requerimientos y 133 casos de prueba
- **CASOS_DE_PRUEBA_GEONEWS.md** - 162 casos de prueba detallados organizados por módulo
- **Plan de pruebas v2.pdf** - Plantilla de plan de pruebas
- **FormatoCasosPrueba.xlsx** - Formato estándar de casos de prueba
- **Matriz de trazabilidad.xlsx** - Matriz en formato Excel

### Diagramas y Diseño
- **DIAGRAMA_UML_APP_MOVIL.md** - Diagrama de clases de la aplicación móvil Android (28 clases)
- **DIAGRAMA_CLASES.md** - Diagrama de clases general del sistema
- **DIAGRAMA_ENTIDAD_RELACION.md** - Diagrama ER de la base de datos
- **DISENO_ENTRADA_SALIDA.md** - Diseño de interfaces de entrada y salida
- **DOCUMENTACION_DIAGRAMAS.md** - Índice de todos los diagramas
- **XML – Diagrama Entidad–Relación (ER) – GeoNews.drawio** - Diagrama ER editable

### Scripts de Base de Datos
- **SCRIPT_MYSQL_GEONEWS.sql** - Script completo de base de datos MySQL (7 tablas)
- **SCRIPT_BASE_DATOS_FIREBASE.md** - Estructura de Firestore y Storage
- **SCRIPT_DIAGRAMA_CLASES_ACTUALIZADO.md** - Diagrama PlantUML actualizado

### Resultados y Análisis
- **RESULTADOS_Y_DISCUSION_GEONEWS.md** - Resultados y discusión del proyecto

### Documentos Académicos
- **TRABAJO DE TITULACION - ESTUDIANTE 07-10-2025(2)2.pdf** - Documento de tesis completo

## 📊 Estadísticas

- **Total de Requerimientos:** 125 (99 funcionales + 26 no funcionales)
- **Total de Casos de Prueba:** 162 casos
- **Cobertura de Pruebas:** 100%
- **Clases en App Android:** 28 clases
- **Módulos Principales:** Autenticación, Noticias, Mapa, Perfil, Configuración, Notificaciones

## 🚫 Módulo Eliminado

El **módulo de Eventos** fue completamente eliminado del proyecto en la versión 0.1.0.
- ❌ No existen casos de prueba para eventos
- ❌ No existen diagramas con clases de eventos
- ❌ No existen tablas de eventos en MySQL ni colecciones en Firestore

## 📱 Estructura de la Aplicación

La aplicación GeoNews tiene **3 secciones principales:**
1. **Noticias** - Visualización, filtrado y detalle de noticias geolocalizadas
2. **Mapa** - Visualización de noticias en mapa con marcadores por categoría
3. **Perfil** - Gestión de perfil de usuario y configuración

## 🛠️ Tecnologías

- **Plataforma:** Android (API 21 - API 34)
- **Lenguaje:** Java
- **Backend:** Firebase (Authentication, Firestore, Storage, Cloud Messaging)
- **Mapas:** Google Maps SDK for Android
- **Diseño:** Material Design 3
- **Base de Datos:** Firebase Firestore (NoSQL) / MySQL (opcional)

---

**Versión:** 0.1.0
**Fecha de Actualización:** Enero 2026
