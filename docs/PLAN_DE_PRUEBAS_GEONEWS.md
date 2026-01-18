# PLAN DE PRUEBAS - GEONEWS

**Proyecto:** GeoNews - Aplicación Móvil de Noticias Locales Geolocalizadas
**Versión:** 0.1.0
**Fecha:** Enero 2026
**Plataforma:** Android (API 21 - API 34)

---

## 1. INTRODUCCIÓN

Este plan de pruebas describe la estrategia y metodología que seguirá el equipo de QA para asegurar la calidad de la aplicación móvil Android **"GeoNews"**. Se utilizará una metodología ágil combinada con pruebas manuales y automatizadas, enfocándose en asegurar que todos los requerimientos funcionales y no funcionales sean validados antes de la entrega al usuario final.

GeoNews es una aplicación móvil Android que permite a los ciudadanos de Ibarra acceder a noticias locales geolocalizadas, visualizar noticias en un mapa interactivo, y gestionar su perfil de usuario. La aplicación utiliza Firebase como backend (Authentication, Firestore, Storage) y Google Maps para la visualización geoespacial.

---

## 1.1 Alcance

### 1.1.1 Dentro del Alcance

Las siguientes características y requerimientos funcionales y no funcionales del software **SÍ** serán probados:

#### Módulo de Autenticación
- Registro de nuevos usuarios con validación de datos
- Inicio de sesión con email y contraseña
- Autenticación con Firebase Authentication
- Cierre de sesión
- Validación de campos obligatorios
- Manejo de errores de autenticación

#### Módulo de Noticias
- Visualización de lista de noticias
- Filtrado de noticias por categorías (10 categorías)
- Filtrado de noticias cercanas por radio de distancia
- Visualización de noticias destacadas
- Detalle completo de noticia (título, descripción, contenido, imagen, cita destacada, hashtags, impacto comunitario)
- Guardado de noticias en favoritos
- Compartir noticias
- Incremento de visualizaciones
- Pull-to-refresh para actualizar noticias
- Carga de imágenes desde Firebase Storage

#### Módulo de Mapa
- Visualización de noticias en mapa con marcadores personalizados
- Marcadores diferenciados por categoría (10 tipos)
- InfoWindow al hacer clic en marcador
- Centrado en ubicación actual del usuario
- Filtrado de marcadores por categoría
- Solicitud de permisos de ubicación
- Integración con Google Maps SDK

#### Módulo de Perfil
- Visualización de datos del usuario
- Edición de perfil (nombre, apellido, bio, ubicación, teléfono)
- Cambio de foto de perfil (cámara o galería)
- Subida de imagen a Firebase Storage
- Visualización de estadísticas (noticias leídas, guardadas, días activo)
- Gestión de intereses por categorías
- Configuración de notificaciones push
- Modo oscuro (tema claro/oscuro)
- Cambio de contraseña
- Cierre de sesión

#### Módulo de Configuración (Ajustes)
- Editar perfil desde ajustes
- Configuración de privacidad y seguridad
- Gestión de categorías de interés
- Centro de ayuda
- Acerca de la aplicación

#### Funcionalidades Transversales
- Navegación entre secciones (Bottom Navigation: Noticias, Mapa, Perfil)
- Splash screen con verificación de sesión
- Persistencia de datos con SharedPreferences
- Validación de permisos (ubicación, cámara, almacenamiento)
- Cálculo de distancias con fórmula Haversine
- Manejo de estados de carga (ProgressBar)
- Manejo de estados vacíos (Empty States)
- Diálogos de confirmación y alertas

#### Pruebas No Funcionales
- Rendimiento de carga de noticias (tiempo de respuesta < 3 segundos)
- Rendimiento de carga de mapa (tiempo de carga < 5 segundos)
- Uso de memoria de la aplicación
- Consumo de batería
- Usabilidad (Material Design 3)
- Compatibilidad con Android 5.0 (API 21) hasta Android 14 (API 34)
- Compatibilidad con diferentes tamaños de pantalla
- Modo offline (datos en caché)
- Seguridad de Firebase (reglas de seguridad Firestore y Storage)

### 1.1.2 Fuera del Alcance

Las siguientes características o requerimientos del software **NO** serán probados:

- **Módulo de Eventos** (eliminado del proyecto)
- Integración con sistemas externos de gestión de contenido
- Pruebas en plataformas iOS
- Pruebas en tablets específicas (solo smartphones)
- Pruebas de monetización (ads, pagos)
- Pruebas de accesibilidad avanzada (TalkBack completo)
- Pruebas de localización en múltiples idiomas (solo español)
- Backend FastAPI (solo se prueba integración con Firebase)
- Migración de datos desde MySQL a Firestore
- Pruebas de estrés con más de 10,000 noticias
- Pruebas en redes 2G/3G (solo 4G, 5G y WiFi)

---

## 1.2 Objetivo de Calidad

Los objetivos de calidad que se planean alcanzar mediante las pruebas son:

1. **Funcionalidad:** Garantizar que la Aplicación Bajo Prueba (AUT) cumpla con el 100% de los requisitos funcionales definidos para los módulos de Autenticación, Noticias, Mapa y Perfil.

2. **Rendimiento:** Asegurar que la aplicación responda en menos de 3 segundos para cargar listas de noticias y menos de 5 segundos para cargar el mapa con marcadores.

3. **Usabilidad:** Validar que la interfaz siga los lineamientos de Material Design 3 y sea intuitiva para el usuario final.

4. **Fiabilidad:** Detectar y corregir todos los errores críticos y de alta prioridad antes de la puesta en producción.

5. **Compatibilidad:** Verificar el correcto funcionamiento en dispositivos Android desde API 21 (Android 5.0) hasta API 34 (Android 14).

6. **Seguridad:** Validar la correcta implementación de autenticación Firebase y reglas de seguridad de Firestore.

7. **Cobertura:** Alcanzar una cobertura de pruebas del 90% de los casos de uso identificados.

---

## 1.3 Roles y Responsabilidades

| Rol | Responsabilidad | Persona Asignada |
|-----|----------------|------------------|
| **QA Lead** | Coordinación del equipo de pruebas, definición de estrategia, reporte de avance a stakeholders | [Nombre del QA Lead] |
| **QA Analyst** | Diseño de casos de prueba, ejecución manual de pruebas, documentación de errores, validación de correcciones | [Nombre del QA Analyst] |
| **Automation Engineer** | Automatización de casos de prueba críticos, configuración de CI/CD para pruebas | [Nombre del Automation Engineer] |
| **Developers** | Corrección de errores reportados, implementación de mejoras, unit testing | [Equipo de Desarrollo] |
| **Product Owner** | Validación de requerimientos, priorización de correcciones, aprobación de UAT | [Nombre del PO] |
| **DevOps Engineer** | Configuración de ambientes de prueba, despliegue en Firebase, gestión de versiones | [Nombre del DevOps] |
| **UX/UI Designer** | Validación de diseño visual, revisión de usabilidad | [Nombre del Designer] |

---

## 2. METODOLOGÍA DE PRUEBAS

### 2.1 Descripción General

Se utilizará una **metodología ágil híbrida** combinando elementos de Scrum y pruebas continuas. La razón de adoptar esta metodología es:

- **Desarrollo iterativo:** El proyecto GeoNews se desarrolla en sprints de 2 semanas, requiriendo pruebas continuas en cada iteración.
- **Cambios frecuentes:** Los requerimientos pueden evolucionar basándose en feedback de usuarios piloto.
- **Integración continua:** Se utiliza Firebase que permite deploys frecuentes, requiriendo validación rápida.
- **Testing temprano:** Las pruebas comienzan desde el diseño de casos de uso, no al final del desarrollo.

### 2.2 Niveles de Pruebas

Se ejecutarán los siguientes niveles de pruebas:

#### 1. Pruebas Unitarias
- **Responsable:** Developers
- **Alcance:** Métodos individuales de clases (Utils, FirebaseManager, modelos)
- **Herramienta:** JUnit 4, Mockito
- **Cobertura esperada:** 70%
- **Ejemplos:**
  - Test de cálculo de distancia (UbicacionUtils.calcularDistancia)
  - Test de validación de email (LoginActivity.validarCampos)
  - Test de conversión Firestore a Modelo (FirebaseManager.documentToNoticia)

#### 2. Pruebas de Integración
- **Responsable:** QA Analyst + Developers
- **Alcance:** Integración entre componentes Android y Firebase
- **Herramienta:** Espresso, Firebase Test Lab
- **Ejemplos:**
  - Integración autenticación Firebase Auth
  - Lectura/escritura en Firestore
  - Subida de imágenes a Firebase Storage
  - Integración Google Maps SDK

#### 3. Pruebas de Sistema
- **Responsable:** QA Analyst
- **Alcance:** Flujos end-to-end completos
- **Herramienta:** Manual + Espresso (automatizado)
- **Ejemplos:**
  - Flujo completo de registro → login → ver noticias → detalle → guardar
  - Flujo de edición de perfil con cambio de foto
  - Flujo de visualización de mapa con filtros

#### 4. Pruebas de Aceptación de Usuario (UAT)
- **Responsable:** Product Owner + Usuarios Beta
- **Alcance:** Validación de que la app cumple expectativas del usuario final
- **Herramienta:** TestFlight (si se considera iOS futuro) / Firebase App Distribution
- **Ejemplos:**
  - Validación de usabilidad con 10 usuarios reales de Ibarra
  - Pruebas de flujos completos en escenarios reales
  - Feedback sobre diseño y navegación

#### 5. Pruebas de Regresión
- **Responsable:** Automation Engineer
- **Alcance:** Validar que nuevos cambios no rompan funcionalidad existente
- **Herramienta:** Suite automatizada Espresso
- **Frecuencia:** Cada build en desarrollo

#### 6. Pruebas de Rendimiento
- **Responsable:** QA Analyst + DevOps
- **Alcance:** Tiempo de respuesta, uso de memoria, batería
- **Herramienta:** Android Profiler, Firebase Performance Monitoring
- **Ejemplos:**
  - Carga de 100 noticias en RecyclerView
  - Renderizado de 50 marcadores en Google Maps
  - Tiempo de splash screen

#### 7. Pruebas de Seguridad
- **Responsable:** QA Lead + DevOps
- **Alcance:** Validación de reglas de seguridad Firebase
- **Herramienta:** Firebase Security Rules Simulator
- **Ejemplos:**
  - Intentar acceder a datos de otros usuarios sin autenticación
  - Validar que Storage solo permita imágenes jpg/png
  - Validar que Firestore no permita escritura sin autenticación

### 2.3 Triage de Errores (Bug Triage)

Los errores encontrados se gestionarán según el siguiente proceso:

#### Clasificación de Severidad

| Severidad | Descripción | Tiempo de Corrección | Ejemplo |
|-----------|-------------|---------------------|---------|
| **Crítico** | Bloquea funcionalidad principal, crash de la app | 24 horas | App crashea al abrir mapa, no se puede hacer login |
| **Alto** | Funcionalidad importante no funciona correctamente | 48 horas | Noticias no se cargan, no se puede guardar noticia |
| **Medio** | Funcionalidad secundaria afectada | 1 semana | Imagen de perfil no se actualiza visualmente, filtros lentos |
| **Bajo** | Problemas estéticos o de UX menores | 2 semanas | Texto desalineado, color incorrecto en chip |

#### Proceso de Triage

1. **Reporte:** QA Analyst documenta el error en Jira con:
   - Título descriptivo
   - Pasos para reproducir
   - Resultado esperado vs. obtenido
   - Screenshots/videos
   - Dispositivo y versión Android
   - Logs de error

2. **Clasificación:** QA Lead asigna severidad y prioridad

3. **Asignación:** Se asigna al developer correspondiente según el módulo

4. **Corrección:** Developer corrige y marca como "Ready for Testing"

5. **Validación:** QA Analyst verifica la corrección

6. **Cierre:** Si está corregido, se cierra el ticket

#### Criterios de Priorización

- **P1 (Crítico):** Errores que bloquean release
- **P2 (Alto):** Errores que afectan funcionalidad clave
- **P3 (Medio):** Errores que pueden esperar al siguiente sprint
- **P4 (Bajo):** Mejoras o bugs estéticos

### 2.4 Criterios de Suspensión y Reanudación

#### Criterios de Suspensión

Las pruebas se **suspenderán** cuando:

1. El ambiente de Firebase esté caído (Firestore, Auth, Storage no disponibles)
2. Existan más de 5 errores críticos sin resolver
3. El build de la aplicación no compile o crashee inmediatamente al abrir
4. Google Maps API Key esté inválida o suspendida
5. No haya conexión a internet en el ambiente de pruebas
6. Dispositivos de prueba no disponibles

#### Criterios de Reanudación

Las pruebas se **reanudarán** cuando:

1. Firebase esté operativo nuevamente (verificado con Firebase Status Dashboard)
2. Los errores críticos sean corregidos y validados
3. Nuevo build estable sea desplegado
4. Google Maps API Key sea restaurada
5. Conexión a internet esté disponible
6. Dispositivos de prueba estén disponibles y configurados

### 2.5 Criterios de Completitud de Pruebas

Las pruebas se considerarán **completas** cuando se cumplan TODOS los siguientes criterios:

#### Criterios de Cobertura

- ✅ Ejecución del 100% de los casos de prueba de alta prioridad
- ✅ Ejecución del 90% de los casos de prueba de media prioridad
- ✅ Ejecución del 70% de los casos de prueba de baja prioridad
- ✅ Cobertura del 100% de los requerimientos funcionales documentados
- ✅ Cobertura del 80% de los requerimientos no funcionales

#### Criterios de Calidad

- ✅ 0 errores críticos (Severidad: Crítico) abiertos
- ✅ 0 errores de alta severidad (Severidad: Alto) abiertos
- ✅ Máximo 5 errores de severidad media abiertos (con plan de corrección)
- ✅ Errores de baja severidad documentados (pueden corregirse post-release)

#### Criterios de Validación

- ✅ Todas las pruebas de regresión automatizadas pasando (100%)
- ✅ Validación exitosa en al menos 5 dispositivos físicos diferentes
- ✅ Validación exitosa en 3 versiones de Android (API 21, 29, 34)
- ✅ Performance dentro de los límites establecidos (tiempo respuesta < 3s)
- ✅ Pruebas UAT aprobadas por Product Owner
- ✅ Checklist ISO/IEC 25010 completado al 80%

#### Criterios de Documentación

- ✅ Matriz de trazabilidad actualizada al 100%
- ✅ Evidencias de pruebas documentadas (screenshots/videos)
- ✅ Reporte final de pruebas generado
- ✅ Casos de prueba actualizados en repositorio

---

## 3. ENTREGABLES DE PRUEBAS

Los siguientes artefactos serán entregados durante las distintas fases del ciclo de vida de pruebas:

### 3.1 Fase de Planificación

| Entregable | Descripción | Responsable | Formato |
|------------|-------------|-------------|---------|
| **Plan de Pruebas** | Este documento (estrategia, alcance, metodología) | QA Lead | Markdown |
| **Matriz de Trazabilidad** | Relación Requerimientos ↔ Casos de Prueba | QA Analyst | Excel/Google Sheets |
| **Estrategia de Automatización** | Definición de qué se automatizará | Automation Engineer | Documento |

### 3.2 Fase de Diseño

| Entregable | Descripción | Responsable | Formato |
|------------|-------------|-------------|---------|
| **Casos de Prueba** | Casos detallados con pasos, datos, resultados esperados | QA Analyst | Excel/TestRail |
| **Scripts de Automatización** | Scripts Espresso para casos críticos | Automation Engineer | Código Java |
| **Checklist ISO/IEC 25010** | Checklist de calidad del software | QA Lead | Excel |

### 3.3 Fase de Ejecución

| Entregable | Descripción | Responsable | Formato |
|------------|-------------|-------------|---------|
| **Reportes de Ejecución Diarios** | Resumen de pruebas ejecutadas (passed/failed) | QA Analyst | Email/Jira |
| **Reportes de Errores** | Tickets de bugs en Jira con detalles completos | QA Analyst | Jira |
| **Evidencias de Pruebas** | Screenshots, videos, logs de casos ejecutados | QA Analyst | Carpeta compartida |
| **Reporte de Cobertura** | % de casos ejecutados vs. planificados | QA Lead | Dashboard |

### 3.4 Fase de Cierre

| Entregable | Descripción | Responsable | Formato |
|------------|-------------|-------------|---------|
| **Reporte Final de Pruebas** | Resumen ejecutivo de todas las pruebas | QA Lead | PDF |
| **Métricas de Calidad** | Gráficas de bugs, severidad, tiempos | QA Lead | Excel/Dashboard |
| **Lecciones Aprendidas** | Documentación de problemas y mejoras | Todo el equipo | Documento |
| **Sign-off de UAT** | Aprobación formal de Product Owner | Product Owner | Email/Documento |

---

## 4. RECURSOS Y NECESIDADES DEL ENTORNO

### 4.1 Herramientas de Pruebas

#### Gestión de Pruebas y Errores

| Herramienta | Propósito | Licencia |
|-------------|-----------|----------|
| **Jira** | Seguimiento de requerimientos, errores, sprints | Atlassian Cloud |
| **TestRail** | Gestión de casos de prueba (opcional) | Cloud/On-Premise |
| **Confluence** | Documentación de pruebas | Atlassian Cloud |

#### Automatización de Pruebas

| Herramienta | Propósito | Tecnología |
|-------------|-----------|------------|
| **Espresso** | Automatización de UI en Android | Google/Android |
| **JUnit 4** | Pruebas unitarias | Java |
| **Mockito** | Mock de dependencias | Java |
| **Firebase Test Lab** | Ejecución en dispositivos reales en la nube | Firebase |

#### Pruebas de API (Opcional - si se usa backend propio)

| Herramienta | Propósito | Tecnología |
|-------------|-----------|------------|
| **Postman** | Pruebas manuales de API REST | Postman |
| **Newman** | Automatización de colecciones Postman | CLI |

#### Monitoreo y Performance

| Herramienta | Propósito | Tecnología |
|-------------|-----------|------------|
| **Android Profiler** | Análisis de CPU, memoria, red | Android Studio |
| **Firebase Performance Monitoring** | Monitoreo de performance en producción | Firebase |
| **Crashlytics** | Reporte de crashes | Firebase |
| **Firebase Analytics** | Análisis de uso | Firebase |

#### Control de Versiones

| Herramienta | Propósito | Tecnología |
|-------------|-----------|------------|
| **Git** | Control de versiones de código y scripts | GitHub/GitLab |
| **GitHub Actions** | CI/CD para builds y pruebas automatizadas | GitHub |

### 4.2 Entorno de Pruebas

#### Hardware Requerido

##### Dispositivos Físicos Mínimos

| Dispositivo | Android Version | Resolución | RAM | Propósito |
|-------------|----------------|------------|-----|-----------|
| **Samsung Galaxy A12** | Android 11 (API 30) | 720x1600 | 3GB | Gama baja |
| **Xiaomi Redmi Note 10** | Android 11 (API 30) | 1080x2400 | 4GB | Gama media |
| **Samsung Galaxy S21** | Android 13 (API 33) | 1080x2400 | 8GB | Gama alta |
| **Google Pixel 5** | Android 14 (API 34) | 1080x2340 | 8GB | Android stock |
| **Huawei P30 Lite** | Android 9 (API 28) | 1080x2312 | 4GB | Sin Google Services (edge case) |

##### Emuladores (Android Studio AVD)

| Emulador | Android Version | Resolución | RAM | Propósito |
|----------|----------------|------------|-----|-----------|
| **Pixel 4** | Android 5.0 (API 21) | 1080x2280 | 2GB | Versión mínima soportada |
| **Pixel 6 Pro** | Android 12 (API 31) | 1440x3120 | 4GB | Pantalla grande |
| **Nexus 5X** | Android 8.0 (API 26) | 1080x1920 | 2GB | Versión intermedia |

##### Requisitos de PC para Ejecución de Pruebas

- **Procesador:** Intel i7 9th Gen o superior / AMD Ryzen 7
- **RAM:** 16GB mínimo (recomendado 32GB)
- **Almacenamiento:** 256GB SSD (para emuladores y builds)
- **GPU:** Dedicada (para emuladores Android)
- **Sistema Operativo:** Windows 10/11, macOS 12+, Ubuntu 20.04+

#### Software Requerido

##### Desarrollo y Pruebas

| Software | Versión | Propósito |
|----------|---------|-----------|
| **Android Studio** | Hedgehog 2023.1.1+ | IDE principal |
| **JDK** | 11 o 17 | Compilación |
| **Gradle** | 8.0+ | Build system |
| **Git** | 2.40+ | Control de versiones |
| **Node.js** | 18+ | Scripts de automatización (opcional) |

##### Configuración Firebase

| Servicio | Proyecto | Propósito |
|----------|----------|-----------|
| **Firebase Authentication** | geonews-dev / geonews-prod | Autenticación de usuarios |
| **Cloud Firestore** | geonews-dev / geonews-prod | Base de datos NoSQL |
| **Firebase Storage** | geonews-dev / geonews-prod | Almacenamiento de imágenes |
| **Firebase Performance** | geonews-prod | Monitoreo |
| **Crashlytics** | geonews-prod | Reporte de crashes |

##### API Keys Requeridas

| API | Propósito | Restricciones |
|-----|-----------|--------------|
| **Google Maps API Key** | Visualización de mapas | Restringida a package name |
| **Firebase Config** | google-services.json | Separado dev/prod |

#### Ambientes

##### Ambiente de Desarrollo (DEV)

- **Firebase Project:** geonews-dev
- **Package Name:** com.tesistitulacion.noticiaslocales.dev
- **Base URL API (opcional):** https://dev-api.geonews.ec
- **Datos:** Datos de prueba sintéticos
- **Propósito:** Desarrollo diario y pruebas de QA

##### Ambiente de Staging (STG)

- **Firebase Project:** geonews-staging
- **Package Name:** com.tesistitulacion.noticiaslocales.stg
- **Base URL API (opcional):** https://stg-api.geonews.ec
- **Datos:** Copia de producción (anonimizada)
- **Propósito:** UAT y pruebas pre-release

##### Ambiente de Producción (PROD)

- **Firebase Project:** geonews-prod
- **Package Name:** com.tesistitulacion.noticiaslocales
- **Base URL API (opcional):** https://api.geonews.ec
- **Datos:** Datos reales de usuarios
- **Propósito:** Aplicación en vivo en Google Play Store

#### Conectividad

- **WiFi:** Disponible en sala de pruebas (100 Mbps mínimo)
- **Datos Móviles:** SIM cards con plan de datos 4G/5G
- **VPN:** (si se requiere para acceder a ambientes restringidos)

---

## 5. TÉRMINOS Y ACRÓNIMOS

| TÉRMINO/ACRÓNIMO | DEFINICIÓN |
|------------------|------------|
| **AUT** | Application Under Test (Aplicación Bajo Prueba) |
| **UAT** | User Acceptance Testing (Pruebas de Aceptación de Usuario) |
| **QA** | Quality Assurance (Aseguramiento de la Calidad) |
| **API** | Application Programming Interface (Interfaz de Programación de Aplicaciones) |
| **SDK** | Software Development Kit (Kit de Desarrollo de Software) |
| **CRUD** | Create, Read, Update, Delete (Operaciones básicas de datos) |
| **UI** | User Interface (Interfaz de Usuario) |
| **UX** | User Experience (Experiencia de Usuario) |
| **CI/CD** | Continuous Integration / Continuous Deployment |
| **AVD** | Android Virtual Device (Emulador Android) |
| **APK** | Android Package Kit (Paquete de instalación Android) |
| **AAB** | Android App Bundle (Formato de distribución Google Play) |
| **GPS** | Global Positioning System (Sistema de Posicionamiento Global) |
| **JSON** | JavaScript Object Notation (Formato de intercambio de datos) |
| **REST** | Representational State Transfer (Arquitectura de APIs) |
| **HTTP** | Hypertext Transfer Protocol (Protocolo de transferencia) |
| **HTTPS** | HTTP Secure (HTTP Seguro) |
| **FCM** | Firebase Cloud Messaging (Mensajería push de Firebase) |
| **MD3** | Material Design 3 (Sistema de diseño de Google) |
| **DAO** | Data Access Object (Objeto de Acceso a Datos) |
| **DTO** | Data Transfer Object (Objeto de Transferencia de Datos) |
| **NoSQL** | Not Only SQL (Base de datos no relacional) |
| **Firestore** | Firebase Cloud Firestore (Base de datos NoSQL de Firebase) |
| **Storage** | Firebase Cloud Storage (Almacenamiento de archivos) |
| **Auth** | Firebase Authentication (Servicio de autenticación) |
| **Crashlytics** | Firebase Crashlytics (Reporte de crashes) |
| **P0/P1/P2/P3/P4** | Niveles de prioridad de bugs (P0 = máxima prioridad) |
| **Regression** | Pruebas de regresión (validar que no se rompió funcionalidad existente) |
| **Smoke Test** | Pruebas rápidas de funcionalidad básica |
| **Sanity Test** | Pruebas rápidas de funcionalidad específica tras corrección |
| **E2E** | End-to-End (Pruebas de flujo completo) |
| **TDD** | Test-Driven Development (Desarrollo guiado por pruebas) |
| **BDD** | Behavior-Driven Development (Desarrollo guiado por comportamiento) |
| **ISO/IEC 25010** | Estándar de calidad de software (SQuaRE) |
| **Haversine** | Fórmula para calcular distancias entre coordenadas GPS |

---

## 6. CASOS DE PRUEBA CRÍTICOS (Resumen)

### 6.1 Autenticación

| ID | Caso de Prueba | Prioridad |
|----|----------------|-----------|
| **AUTH-001** | Registro exitoso con datos válidos | P1 |
| **AUTH-002** | Registro fallido con email duplicado | P1 |
| **AUTH-003** | Login exitoso con credenciales válidas | P1 |
| **AUTH-004** | Login fallido con credenciales inválidas | P1 |
| **AUTH-005** | Validación de formato de email | P2 |
| **AUTH-006** | Validación de contraseña (min 6 caracteres) | P2 |
| **AUTH-007** | Cierre de sesión exitoso | P1 |
| **AUTH-008** | Persistencia de sesión tras cerrar app | P1 |

### 6.2 Noticias

| ID | Caso de Prueba | Prioridad |
|----|----------------|-----------|
| **NEWS-001** | Cargar lista de noticias al abrir app | P1 |
| **NEWS-002** | Filtrar noticias por categoría | P1 |
| **NEWS-003** | Filtrar noticias cercanas (radio 5km) | P1 |
| **NEWS-004** | Ver detalle completo de noticia | P1 |
| **NEWS-005** | Guardar noticia en favoritos | P1 |
| **NEWS-006** | Eliminar noticia de favoritos | P2 |
| **NEWS-007** | Compartir noticia por WhatsApp/Facebook | P2 |
| **NEWS-008** | Pull-to-refresh actualiza noticias | P2 |
| **NEWS-009** | Incrementar visualizaciones al abrir detalle | P3 |
| **NEWS-010** | Mostrar empty state cuando no hay noticias | P2 |

### 6.3 Mapa

| ID | Caso de Prueba | Prioridad |
|----|----------------|-----------|
| **MAP-001** | Cargar mapa con marcadores de noticias | P1 |
| **MAP-002** | Marcadores diferenciados por categoría | P1 |
| **MAP-003** | Mostrar InfoWindow al clic en marcador | P1 |
| **MAP-004** | Centrar mapa en ubicación actual | P1 |
| **MAP-005** | Solicitar permisos de ubicación | P1 |
| **MAP-006** | Filtrar marcadores por categoría | P2 |
| **MAP-007** | Navegar desde InfoWindow a detalle | P2 |
| **MAP-008** | Manejo de error si no hay permisos GPS | P2 |

### 6.4 Perfil

| ID | Caso de Prueba | Prioridad |
|----|----------------|-----------|
| **PROF-001** | Visualizar datos del usuario logueado | P1 |
| **PROF-002** | Editar nombre y apellido | P1 |
| **PROF-003** | Cambiar foto de perfil desde galería | P1 |
| **PROF-004** | Cambiar foto de perfil desde cámara | P2 |
| **PROF-005** | Actualizar bio y ubicación | P2 |
| **PROF-006** | Cambiar contraseña | P1 |
| **PROF-007** | Activar/desactivar notificaciones | P2 |
| **PROF-008** | Cambiar a modo oscuro | P2 |
| **PROF-009** | Ver estadísticas (noticias leídas, guardadas) | P3 |
| **PROF-010** | Cerrar sesión y volver a login | P1 |

---

## 7. CRONOGRAMA DE PRUEBAS (Ejemplo)

| Fase | Actividad | Duración | Responsable |
|------|-----------|----------|-------------|
| **Semana 1** | Planificación y diseño de casos de prueba | 5 días | QA Lead + QA Analyst |
| **Semana 2** | Configuración de ambientes y herramientas | 3 días | DevOps + QA |
| **Semana 2-3** | Ejecución de pruebas de sistema (manual) | 10 días | QA Analyst |
| **Semana 3** | Desarrollo de scripts de automatización | 5 días | Automation Engineer |
| **Semana 4** | Ejecución de pruebas de regresión | 3 días | Automation + QA |
| **Semana 4** | Corrección de errores encontrados | 5 días | Developers |
| **Semana 5** | Re-testing y validación de correcciones | 3 días | QA Analyst |
| **Semana 5** | Pruebas de rendimiento y seguridad | 2 días | QA + DevOps |
| **Semana 6** | UAT con usuarios piloto | 5 días | Product Owner + Usuarios |
| **Semana 6** | Documentación y cierre | 2 días | QA Lead |

**Total: 6 semanas (1.5 meses)**

---

## 8. MÉTRICAS DE CALIDAD

Se rastrearán las siguientes métricas durante el proceso de pruebas:

### Métricas de Ejecución

- **% de casos ejecutados:** (Casos ejecutados / Total casos) × 100
- **% de casos pasados:** (Casos passed / Casos ejecutados) × 100
- **% de casos fallidos:** (Casos failed / Casos ejecutados) × 100

### Métricas de Defectos

- **Total de defectos encontrados:** Cantidad total de bugs
- **Defectos por severidad:** Crítico, Alto, Medio, Bajo
- **Defectos por módulo:** Autenticación, Noticias, Mapa, Perfil
- **Tasa de detección de defectos:** (Defectos encontrados / Total casos ejecutados)
- **Tasa de resolución de defectos:** (Defectos cerrados / Defectos totales) × 100
- **Tiempo promedio de corrección:** Días entre reporte y cierre

### Métricas de Cobertura

- **Cobertura de requerimientos:** (Requerimientos probados / Total requerimientos) × 100
- **Cobertura de código (unit tests):** % de líneas de código con tests unitarios

### Métricas de Rendimiento

- **Tiempo de carga de noticias:** Promedio en segundos
- **Tiempo de carga de mapa:** Promedio en segundos
- **Consumo de memoria:** Promedio en MB
- **Crashes por sesión:** Tasa de crashes

---

## 9. RIESGOS Y MITIGACIÓN

| Riesgo | Probabilidad | Impacto | Mitigación |
|--------|--------------|---------|------------|
| **Firebase caído durante pruebas** | Baja | Alto | Tener ambiente local de desarrollo, monitorear Firebase Status |
| **Cambios de requerimientos en mitad del sprint** | Media | Medio | Metodología ágil flexible, re-priorización de casos |
| **Dispositivos físicos no disponibles** | Media | Medio | Usar Firebase Test Lab con dispositivos cloud |
| **Google Maps API Key suspendida** | Baja | Crítico | Tener key de respaldo, monitorear cuotas |
| **Retrasos en corrección de bugs** | Media | Alto | Reuniones diarias de triage, priorización clara |
| **Falta de datos de prueba** | Baja | Medio | Scripts para generar datos sintéticos en Firestore |
| **Incompatibilidad con versiones antiguas Android** | Media | Medio | Pruebas tempranas en API 21, considerar deprecar si es necesario |

---

## 10. APROBACIONES

| Rol | Nombre | Firma | Fecha |
|-----|--------|-------|-------|
| **QA Lead** | [Nombre] | __________ | ____/____/____ |
| **Product Owner** | [Nombre] | __________ | ____/____/____ |
| **Tech Lead** | [Nombre] | __________ | ____/____/____ |
| **Project Manager** | [Nombre] | __________ | ____/____/____ |

---

**Fin del Plan de Pruebas - GeoNews v0.1.0**

*Este documento es de carácter confidencial y propiedad del equipo de desarrollo de GeoNews.*
