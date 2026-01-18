# MATRIZ DE TRAZABILIDAD DE REQUERIMIENTOS - GEONEWS

**Proyecto:** GeoNews - Aplicación Móvil de Noticias Locales Geolocalizadas
**Versión:** 0.1.0
**Fecha:** Enero 2026
**Plataforma:** Android (API 21 - API 34)

---

## Descripción

Esta matriz de trazabilidad establece la relación entre los **Requerimientos Funcionales y No Funcionales** del sistema GeoNews y los **Casos de Prueba** diseñados para validarlos.

El objetivo es garantizar que:
1. Todos los requerimientos tienen al menos un caso de prueba asociado
2. Todos los casos de prueba están vinculados a un requerimiento
3. Se puede rastrear la cobertura de pruebas por módulo y prioridad

---

## Leyenda

### Prioridad de Requerimientos
- **P1 - Crítica:** Funcionalidad esencial, bloquea el release
- **P2 - Alta:** Funcionalidad importante, debe estar en release
- **P3 - Media:** Funcionalidad deseable, puede postponerse
- **P4 - Baja:** Mejora o característica opcional

### Estado de Pruebas
- ✅ **Pasado:** Caso ejecutado exitosamente
- ❌ **Fallido:** Caso ejecutado con errores
- 🔄 **En Progreso:** Caso en ejecución
- ⏸️ **Pendiente:** Caso no ejecutado aún
- 🚫 **Bloqueado:** Caso bloqueado por dependencias
- ⏭️ **Omitido:** Caso omitido intencionalmente

### Módulos del Sistema
- **AUTH:** Autenticación y Registro
- **NEWS:** Gestión de Noticias
- **MAP:** Mapa y Geolocalización
- **PROF:** Perfil de Usuario
- **CONF:** Configuración y Ajustes
- **NOTI:** Notificaciones
- **SYST:** Sistema y Navegación

---

## MÓDULO 1: AUTENTICACIÓN (AUTH)

### Requerimientos Funcionales - Autenticación

| ID Req | Descripción del Requerimiento | Prioridad | Casos de Prueba Asociados | Estado | Resultado |
|--------|-------------------------------|-----------|---------------------------|--------|-----------|
| **RF-AUTH-001** | El sistema debe permitir el registro de nuevos usuarios con nombre, apellido, email y contraseña | P1 | AUTH-001, AUTH-002, AUTH-005, AUTH-006 | ⏸️ | - |
| **RF-AUTH-002** | El sistema debe validar que el email no esté duplicado en Firebase | P1 | AUTH-002 | ⏸️ | - |
| **RF-AUTH-003** | El sistema debe validar formato de email (contiene @) | P2 | AUTH-005 | ⏸️ | - |
| **RF-AUTH-004** | El sistema debe validar que la contraseña tenga mínimo 6 caracteres | P2 | AUTH-006 | ⏸️ | - |
| **RF-AUTH-005** | El sistema debe crear documento de usuario en Firestore tras registro exitoso | P1 | AUTH-001 | ⏸️ | - |
| **RF-AUTH-006** | El sistema debe permitir login con email y contraseña | P1 | AUTH-003, AUTH-004 | ⏸️ | - |
| **RF-AUTH-007** | El sistema debe mostrar error descriptivo si login falla | P1 | AUTH-004 | ⏸️ | - |
| **RF-AUTH-008** | El sistema debe guardar sesión en SharedPreferences tras login | P1 | AUTH-008 | ⏸️ | - |
| **RF-AUTH-009** | El sistema debe mantener sesión al cerrar y abrir la app | P1 | AUTH-008 | ⏸️ | - |
| **RF-AUTH-010** | El sistema debe permitir cerrar sesión | P1 | AUTH-007 | ⏸️ | - |
| **RF-AUTH-011** | El sistema debe limpiar datos locales al cerrar sesión | P1 | AUTH-007 | ⏸️ | - |
| **RF-AUTH-012** | El sistema debe redirigir a LoginActivity tras cerrar sesión | P1 | AUTH-007 | ⏸️ | - |

### Requerimientos No Funcionales - Autenticación

| ID Req | Descripción del Requerimiento | Prioridad | Casos de Prueba Asociados | Estado | Resultado |
|--------|-------------------------------|-----------|---------------------------|--------|-----------|
| **RNF-AUTH-001** | El tiempo de registro debe ser menor a 3 segundos con conexión estable | P2 | PERF-AUTH-001 | ⏸️ | - |
| **RNF-AUTH-002** | El tiempo de login debe ser menor a 2 segundos con conexión estable | P2 | PERF-AUTH-002 | ⏸️ | - |
| **RNF-AUTH-003** | Las contraseñas deben almacenarse encriptadas en Firebase Auth | P1 | SEC-AUTH-001 | ⏸️ | - |
| **RNF-AUTH-004** | La interfaz de login debe seguir Material Design 3 | P3 | UX-AUTH-001 | ⏸️ | - |

---

## MÓDULO 2: NOTICIAS (NEWS)

### Requerimientos Funcionales - Noticias

| ID Req | Descripción del Requerimiento | Prioridad | Casos de Prueba Asociados | Estado | Resultado |
|--------|-------------------------------|-----------|---------------------------|--------|-----------|
| **RF-NEWS-001** | El sistema debe cargar lista de noticias desde Firestore al abrir la app | P1 | NEWS-001 | ⏸️ | - |
| **RF-NEWS-002** | El sistema debe mostrar título, descripción, imagen, autor y fecha de cada noticia | P1 | NEWS-001 | ⏸️ | - |
| **RF-NEWS-003** | El sistema debe permitir filtrar noticias por categoría | P1 | NEWS-002 | ⏸️ | - |
| **RF-NEWS-004** | El sistema debe mostrar 10 categorías: Política, Economía, Cultura, Deportes, Educación, Salud, Seguridad, Medio Ambiente, Turismo, Tecnología | P1 | NEWS-002, NEWS-020 | ⏸️ | - |
| **RF-NEWS-005** | El sistema debe permitir filtrar noticias cercanas por radio (5km, 10km, 20km) | P1 | NEWS-003, NEWS-021 | ⏸️ | - |
| **RF-NEWS-006** | El sistema debe calcular distancia entre ubicación del usuario y ubicación de la noticia usando fórmula Haversine | P1 | NEWS-003, NEWS-022 | ⏸️ | - |
| **RF-NEWS-007** | El sistema debe mostrar solo noticias destacadas al activar filtro "Destacadas" | P1 | NEWS-023 | ⏸️ | - |
| **RF-NEWS-008** | El sistema debe permitir ver detalle completo de una noticia al hacer clic | P1 | NEWS-004 | ⏸️ | - |
| **RF-NEWS-009** | El detalle debe mostrar: título, imagen, descripción, contenido completo, autor, fecha, ubicación, categoría, cita destacada, hashtags, impacto comunitario | P1 | NEWS-004, NEWS-024 | ⏸️ | - |
| **RF-NEWS-010** | El sistema debe permitir guardar noticia en favoritos | P1 | NEWS-005, NEWS-025 | ⏸️ | - |
| **RF-NEWS-011** | El sistema debe permitir eliminar noticia de favoritos | P2 | NEWS-006, NEWS-026 | ⏸️ | - |
| **RF-NEWS-012** | El sistema debe almacenar IDs de noticias guardadas en SharedPreferences | P1 | NEWS-005, NEWS-006 | ⏸️ | - |
| **RF-NEWS-013** | El sistema debe permitir compartir noticia (título + link) | P2 | NEWS-007, NEWS-027 | ⏸️ | - |
| **RF-NEWS-014** | El sistema debe incrementar contador de visualizaciones al abrir detalle | P3 | NEWS-009, NEWS-028 | ⏸️ | - |
| **RF-NEWS-015** | El sistema debe permitir actualizar lista con pull-to-refresh | P2 | NEWS-008, NEWS-029 | ⏸️ | - |
| **RF-NEWS-016** | El sistema debe mostrar empty state cuando no hay noticias | P2 | NEWS-010, NEWS-030 | ⏸️ | - |
| **RF-NEWS-017** | El sistema debe mostrar ProgressBar mientras carga noticias | P2 | NEWS-031 | ⏸️ | - |
| **RF-NEWS-018** | El sistema debe cargar imágenes de noticias desde Firebase Storage | P1 | NEWS-032 | ⏸️ | - |
| **RF-NEWS-019** | El sistema debe mostrar placeholder si imagen no carga | P2 | NEWS-033 | ⏸️ | - |

### Requerimientos No Funcionales - Noticias

| ID Req | Descripción del Requerimiento | Prioridad | Casos de Prueba Asociados | Estado | Resultado |
|--------|-------------------------------|-----------|---------------------------|--------|-----------|
| **RNF-NEWS-001** | El tiempo de carga de lista de noticias debe ser menor a 3 segundos | P1 | PERF-NEWS-001 | ⏸️ | - |
| **RNF-NEWS-002** | El sistema debe soportar carga de hasta 500 noticias sin degradación de performance | P2 | PERF-NEWS-002 | ⏸️ | - |
| **RNF-NEWS-003** | Las imágenes deben cargarse de forma lazy (solo visibles en viewport) | P2 | PERF-NEWS-003 | ⏸️ | - |
| **RNF-NEWS-004** | El RecyclerView debe usar ViewHolder pattern para optimización | P1 | PERF-NEWS-004 | ⏸️ | - |
| **RNF-NEWS-005** | La interfaz de noticias debe seguir Material Design 3 | P2 | UX-NEWS-001 | ⏸️ | - |

---

## MÓDULO 3: MAPA (MAP)

### Requerimientos Funcionales - Mapa

| ID Req | Descripción del Requerimiento | Prioridad | Casos de Prueba Asociados | Estado | Resultado |
|--------|-------------------------------|-----------|---------------------------|--------|-----------|
| **RF-MAP-001** | El sistema debe cargar Google Maps al abrir sección Mapa | P1 | MAP-001 | ⏸️ | - |
| **RF-MAP-002** | El sistema debe mostrar marcadores de noticias en el mapa | P1 | MAP-001, MAP-034 | ⏸️ | - |
| **RF-MAP-003** | Los marcadores deben diferenciarse visualmente por categoría (10 iconos) | P1 | MAP-002, MAP-035 | ⏸️ | - |
| **RF-MAP-004** | El sistema debe mostrar InfoWindow al hacer clic en marcador | P1 | MAP-003, MAP-036 | ⏸️ | - |
| **RF-MAP-005** | El InfoWindow debe mostrar: título, imagen thumbnail, categoría, distancia | P1 | MAP-003 | ⏸️ | - |
| **RF-MAP-006** | El sistema debe permitir navegar a detalle de noticia desde InfoWindow | P2 | MAP-007, MAP-037 | ⏸️ | - |
| **RF-MAP-007** | El sistema debe solicitar permisos de ubicación al abrir mapa | P1 | MAP-005, MAP-038 | ⏸️ | - |
| **RF-MAP-008** | El sistema debe centrar mapa en ubicación actual del usuario si hay permisos | P1 | MAP-004, MAP-039 | ⏸️ | - |
| **RF-MAP-009** | El sistema debe mostrar botón de "Mi Ubicación" en el mapa | P2 | MAP-040 | ⏸️ | - |
| **RF-MAP-010** | El sistema debe permitir filtrar marcadores por categoría | P2 | MAP-006, MAP-041 | ⏸️ | - |
| **RF-MAP-011** | El sistema debe manejar error si no hay permisos de ubicación | P2 | MAP-008, MAP-042 | ⏸️ | - |
| **RF-MAP-012** | El sistema debe mostrar marcador de ubicación actual del usuario | P2 | MAP-043 | ⏸️ | - |
| **RF-MAP-013** | El mapa debe permitir zoom, pan y rotación | P1 | MAP-044 | ⏸️ | - |
| **RF-MAP-014** | El mapa debe centrarse por defecto en Ibarra (-0.3514, -78.1267) | P1 | MAP-045 | ⏸️ | - |

### Requerimientos No Funcionales - Mapa

| ID Req | Descripción del Requerimiento | Prioridad | Casos de Prueba Asociados | Estado | Resultado |
|--------|-------------------------------|-----------|---------------------------|--------|-----------|
| **RNF-MAP-001** | El tiempo de carga inicial del mapa debe ser menor a 5 segundos | P1 | PERF-MAP-001 | ⏸️ | - |
| **RNF-MAP-002** | El mapa debe renderizar hasta 100 marcadores sin lag | P2 | PERF-MAP-002 | ⏸️ | - |
| **RNF-MAP-003** | El sistema debe usar clustering si hay más de 50 marcadores cercanos | P3 | PERF-MAP-003 | ⏸️ | - |
| **RNF-MAP-004** | La Google Maps API Key debe estar restringida al package name | P1 | SEC-MAP-001 | ⏸️ | - |
| **RNF-MAP-005** | El mapa debe funcionar offline con tiles en caché | P3 | PERF-MAP-004 | ⏸️ | - |

---

## MÓDULO 4: PERFIL (PROF)

### Requerimientos Funcionales - Perfil

| ID Req | Descripción del Requerimiento | Prioridad | Casos de Prueba Asociados | Estado | Resultado |
|--------|-------------------------------|-----------|---------------------------|--------|-----------|
| **RF-PROF-001** | El sistema debe mostrar datos del usuario logueado en sección Perfil | P1 | PROF-001, PROF-046 | ⏸️ | - |
| **RF-PROF-002** | El sistema debe cargar datos desde Firestore usando userId | P1 | PROF-001 | ⏸️ | - |
| **RF-PROF-003** | El perfil debe mostrar: avatar, nombre, apellido, email, bio, ubicación | P1 | PROF-001 | ⏸️ | - |
| **RF-PROF-004** | El sistema debe permitir editar nombre y apellido | P1 | PROF-002, PROF-047 | ⏸️ | - |
| **RF-PROF-005** | El sistema debe permitir cambiar foto de perfil desde galería | P1 | PROF-003, PROF-048 | ⏸️ | - |
| **RF-PROF-006** | El sistema debe permitir cambiar foto de perfil desde cámara | P2 | PROF-004, PROF-049 | ⏸️ | - |
| **RF-PROF-007** | El sistema debe subir foto a Firebase Storage en carpeta "fotos_perfil/{userId}" | P1 | PROF-003, PROF-004 | ⏸️ | - |
| **RF-PROF-008** | El sistema debe actualizar campo fotoPerfil en Firestore con URL de Storage | P1 | PROF-003, PROF-004 | ⏸️ | - |
| **RF-PROF-009** | El sistema debe permitir editar bio y ubicación | P2 | PROF-005, PROF-050 | ⏸️ | - |
| **RF-PROF-010** | El sistema debe permitir cambiar contraseña | P1 | PROF-006, PROF-051 | ⏸️ | - |
| **RF-PROF-011** | El sistema debe validar contraseña actual antes de cambiarla | P1 | PROF-006 | ⏸️ | - |
| **RF-PROF-012** | El sistema debe actualizar contraseña en Firebase Auth | P1 | PROF-006 | ⏸️ | - |
| **RF-PROF-013** | El sistema debe mostrar estadísticas: noticias leídas, noticias guardadas, días activo | P3 | PROF-009, PROF-052 | ⏸️ | - |
| **RF-PROF-014** | El sistema debe permitir activar/desactivar notificaciones push | P2 | PROF-007, PROF-053 | ⏸️ | - |
| **RF-PROF-015** | El sistema debe permitir cambiar tema (modo claro/oscuro) | P2 | PROF-008, PROF-054 | ⏸️ | - |
| **RF-PROF-016** | El sistema debe aplicar tema inmediatamente al cambiar switch | P2 | PROF-008 | ⏸️ | - |
| **RF-PROF-017** | El sistema debe guardar preferencias en SharedPreferences | P1 | PROF-007, PROF-008 | ⏸️ | - |
| **RF-PROF-018** | El sistema debe permitir cerrar sesión desde perfil | P1 | PROF-010, PROF-055 | ⏸️ | - |
| **RF-PROF-019** | El sistema debe mostrar diálogo de confirmación antes de cerrar sesión | P2 | PROF-010 | ⏸️ | - |
| **RF-PROF-020** | El sistema debe mostrar ChipGroup de intereses (categorías seleccionadas) | P3 | PROF-056 | ⏸️ | - |
| **RF-PROF-021** | El sistema debe permitir agregar/eliminar categorías de interés | P3 | PROF-057 | ⏸️ | - |

### Requerimientos No Funcionales - Perfil

| ID Req | Descripción del Requerimiento | Prioridad | Casos de Prueba Asociados | Estado | Resultado |
|--------|-------------------------------|-----------|---------------------------|--------|-----------|
| **RNF-PROF-001** | La carga de datos del perfil debe ser menor a 2 segundos | P2 | PERF-PROF-001 | ⏸️ | - |
| **RNF-PROF-002** | La subida de foto de perfil debe completarse en menos de 5 segundos (imagen < 2MB) | P2 | PERF-PROF-002 | ⏸️ | - |
| **RNF-PROF-003** | El sistema debe comprimir imágenes antes de subirlas (max 1MB) | P2 | PERF-PROF-003 | ⏸️ | - |
| **RNF-PROF-004** | Las imágenes de perfil deben ser solo JPG o PNG | P2 | SEC-PROF-001 | ⏸️ | - |

---

## MÓDULO 5: CONFIGURACIÓN (CONF)

### Requerimientos Funcionales - Configuración

| ID Req | Descripción del Requerimiento | Prioridad | Casos de Prueba Asociados | Estado | Resultado |
|--------|-------------------------------|-----------|---------------------------|--------|-----------|
| **RF-CONF-001** | El sistema debe tener pantalla de Ajustes accesible desde Perfil | P2 | CONF-058 | ⏸️ | - |
| **RF-CONF-002** | El sistema debe mostrar resumen de perfil en Ajustes (avatar, nombre, ubicación) | P2 | CONF-058 | ⏸️ | - |
| **RF-CONF-003** | El sistema debe permitir navegar a EditarPerfil desde Ajustes | P2 | CONF-059 | ⏸️ | - |
| **RF-CONF-004** | El sistema debe mostrar opción "Mis Ubicaciones" | P3 | CONF-060 | ⏸️ | - |
| **RF-CONF-005** | El sistema debe mostrar opción "Seguridad y Privacidad" | P2 | CONF-061 | ⏸️ | - |
| **RF-CONF-006** | El sistema debe mostrar opción "Categorías de Interés" | P3 | CONF-062 | ⏸️ | - |
| **RF-CONF-007** | El sistema debe mostrar opción "Idioma" (solo español por ahora) | P4 | CONF-063 | ⏸️ | - |
| **RF-CONF-008** | El sistema debe mostrar opción "Centro de Ayuda" con instrucciones | P3 | CONF-064 | ⏸️ | - |
| **RF-CONF-009** | El sistema debe mostrar opción "Acerca de" con info del proyecto | P3 | CONF-065 | ⏸️ | - |
| **RF-CONF-010** | El sistema debe permitir activar/desactivar notificaciones push | P2 | CONF-066 | ⏸️ | - |
| **RF-CONF-011** | El sistema debe permitir activar/desactivar digest por email | P3 | CONF-067 | ⏸️ | - |
| **RF-CONF-012** | El sistema debe permitir cerrar sesión desde Ajustes | P2 | CONF-068 | ⏸️ | - |

---

## MÓDULO 6: NOTIFICACIONES (NOTI)

### Requerimientos Funcionales - Notificaciones

| ID Req | Descripción del Requerimiento | Prioridad | Casos de Prueba Asociados | Estado | Resultado |
|--------|-------------------------------|-----------|---------------------------|--------|-----------|
| **RF-NOTI-001** | El sistema debe tener pantalla de Notificaciones | P3 | NOTI-069 | ⏸️ | - |
| **RF-NOTI-002** | El sistema debe mostrar switch para activar/desactivar notificaciones | P2 | NOTI-070 | ⏸️ | - |
| **RF-NOTI-003** | El sistema debe mostrar opciones de notificaciones cuando estén activadas | P3 | NOTI-071 | ⏸️ | - |
| **RF-NOTI-004** | El sistema debe permitir activar notificaciones de noticias nuevas | P3 | NOTI-072 | ⏸️ | - |
| **RF-NOTI-005** | El sistema debe permitir activar notificaciones de noticias destacadas | P3 | NOTI-073 | ⏸️ | - |
| **RF-NOTI-006** | El sistema debe guardar preferencias en SharedPreferences | P2 | NOTI-074 | ⏸️ | - |
| **RF-NOTI-007** | El sistema debe integrar Firebase Cloud Messaging (FCM) | P3 | NOTI-075 | ⏸️ | - |

---

## MÓDULO 7: SISTEMA Y NAVEGACIÓN (SYST)

### Requerimientos Funcionales - Sistema

| ID Req | Descripción del Requerimiento | Prioridad | Casos de Prueba Asociados | Estado | Resultado |
|--------|-------------------------------|-----------|---------------------------|--------|-----------|
| **RF-SYST-001** | El sistema debe mostrar Splash Screen al iniciar | P1 | SYST-076 | ⏸️ | - |
| **RF-SYST-002** | El Splash debe verificar si hay sesión activa (SharedPreferences) | P1 | SYST-076 | ⏸️ | - |
| **RF-SYST-003** | El sistema debe navegar a LoginActivity si no hay sesión | P1 | SYST-077 | ⏸️ | - |
| **RF-SYST-004** | El sistema debe navegar a ListaNoticiasActivity si hay sesión | P1 | SYST-078 | ⏸️ | - |
| **RF-SYST-005** | El sistema debe tener Bottom Navigation con 3 secciones: Noticias, Mapa, Perfil | P1 | SYST-079 | ⏸️ | - |
| **RF-SYST-006** | El Bottom Navigation debe resaltar la sección activa | P1 | SYST-079 | ⏸️ | - |
| **RF-SYST-007** | El sistema debe permitir navegación entre secciones con animaciones | P2 | SYST-080 | ⏸️ | - |
| **RF-SYST-008** | El sistema debe mantener estado de cada sección al navegar | P2 | SYST-081 | ⏸️ | - |
| **RF-SYST-009** | El sistema debe solicitar permisos de ubicación cuando sea necesario | P1 | SYST-082 | ⏸️ | - |
| **RF-SYST-010** | El sistema debe solicitar permisos de cámara cuando sea necesario | P2 | SYST-083 | ⏸️ | - |
| **RF-SYST-011** | El sistema debe solicitar permisos de almacenamiento cuando sea necesario | P2 | SYST-084 | ⏸️ | - |
| **RF-SYST-012** | El sistema debe manejar negación de permisos con mensajes claros | P2 | SYST-085 | ⏸️ | - |
| **RF-SYST-013** | El sistema debe funcionar en modo offline con datos en caché | P3 | SYST-086 | ⏸️ | - |
| **RF-SYST-014** | El sistema debe mostrar mensaje cuando no hay conexión | P2 | SYST-087 | ⏸️ | - |

### Requerimientos No Funcionales - Sistema

| ID Req | Descripción del Requerimiento | Prioridad | Casos de Prueba Asociados | Estado | Resultado |
|--------|-------------------------------|-----------|---------------------------|--------|-----------|
| **RNF-SYST-001** | El sistema debe ser compatible con Android 5.0 (API 21) a Android 14 (API 34) | P1 | COMP-001 | ⏸️ | - |
| **RNF-SYST-002** | El sistema debe funcionar en pantallas de 4" a 6.7" | P1 | COMP-002 | ⏸️ | - |
| **RNF-SYST-003** | El sistema debe seguir Material Design 3 | P2 | UX-SYST-001 | ⏸️ | - |
| **RNF-SYST-004** | El tamaño del APK debe ser menor a 50 MB | P2 | PERF-SYST-001 | ⏸️ | - |
| **RNF-SYST-005** | El uso de RAM debe ser menor a 200 MB en condiciones normales | P2 | PERF-SYST-002 | ⏸️ | - |
| **RNF-SYST-006** | El consumo de batería debe ser clasificado como "Bajo" por Android | P3 | PERF-SYST-003 | ⏸️ | - |
| **RNF-SYST-007** | La tasa de crashes debe ser menor a 0.5% | P1 | PERF-SYST-004 | ⏸️ | - |
| **RNF-SYST-008** | El tiempo de inicio de la app debe ser menor a 2 segundos | P2 | PERF-SYST-005 | ⏸️ | - |

---

## RESUMEN DE COBERTURA

### Cobertura por Módulo

| Módulo | Total Requerimientos | Casos de Prueba Asociados | Cobertura |
|--------|---------------------|---------------------------|-----------|
| **Autenticación (AUTH)** | 16 (12 RF + 4 RNF) | 12 casos | 100% |
| **Noticias (NEWS)** | 24 (19 RF + 5 RNF) | 33 casos | 100% |
| **Mapa (MAP)** | 19 (14 RF + 5 RNF) | 25 casos | 100% |
| **Perfil (PROF)** | 25 (21 RF + 4 RNF) | 28 casos | 100% |
| **Configuración (CONF)** | 12 (12 RF + 0 RNF) | 11 casos | 100% |
| **Notificaciones (NOTI)** | 7 (7 RF + 0 RNF) | 7 casos | 100% |
| **Sistema (SYST)** | 22 (14 RF + 8 RNF) | 17 casos | 100% |
| **TOTAL** | **125 requerimientos** | **133 casos** | **100%** |

### Cobertura por Prioridad

| Prioridad | Total Requerimientos | Casos Asociados | % Cobertura |
|-----------|---------------------|-----------------|-------------|
| **P1 - Crítica** | 68 requerimientos | 72 casos | 100% |
| **P2 - Alta** | 38 requerimientos | 42 casos | 100% |
| **P3 - Media** | 16 requerimientos | 16 casos | 100% |
| **P4 - Baja** | 3 requerimientos | 3 casos | 100% |

### Cobertura por Tipo

| Tipo | Total Requerimientos | % del Total |
|------|---------------------|-------------|
| **Funcionales (RF)** | 99 | 79.2% |
| **No Funcionales (RNF)** | 26 | 20.8% |

---

## CASOS DE PRUEBA DETALLADOS

### AUTENTICACIÓN (AUTH)

| ID Caso | Descripción | Req Asociados | Prioridad | Precondiciones | Pasos | Resultado Esperado | Estado |
|---------|-------------|---------------|-----------|----------------|-------|-------------------|--------|
| **AUTH-001** | Registro exitoso con datos válidos | RF-AUTH-001, RF-AUTH-005 | P1 | App instalada, no hay sesión | 1. Abrir app<br>2. Clic en "Registrarse"<br>3. Ingresar: nombre, apellido, email válido, password ≥6<br>4. Clic "Registrar" | Usuario creado en Firebase Auth y Firestore, navega a app | ⏸️ |
| **AUTH-002** | Registro fallido con email duplicado | RF-AUTH-001, RF-AUTH-002 | P1 | Usuario ya existe con email | 1-3 igual que AUTH-001<br>4. Clic "Registrar" | Error: "El email ya está registrado" | ⏸️ |
| **AUTH-003** | Login exitoso con credenciales válidas | RF-AUTH-006 | P1 | Usuario registrado | 1. Abrir app<br>2. Ingresar email y password correctos<br>3. Clic "Iniciar Sesión" | Sesión iniciada, navega a ListaNoticiasActivity | ⏸️ |
| **AUTH-004** | Login fallido con credenciales inválidas | RF-AUTH-006, RF-AUTH-007 | P1 | Usuario no existe o password incorrecta | 1-2 igual que AUTH-003 pero con datos erróneos<br>3. Clic "Iniciar Sesión" | Error: "Credenciales inválidas" | ⏸️ |
| **AUTH-005** | Validación de formato de email | RF-AUTH-003 | P2 | En pantalla de registro | 1. Ingresar email sin @<br>2. Intentar registrar | Error: "Email inválido" | ⏸️ |
| **AUTH-006** | Validación de longitud de contraseña | RF-AUTH-004 | P2 | En pantalla de registro | 1. Ingresar password con < 6 caracteres<br>2. Intentar registrar | Error: "La contraseña debe tener al menos 6 caracteres" | ⏸️ |
| **AUTH-007** | Cierre de sesión exitoso | RF-AUTH-010, RF-AUTH-011, RF-AUTH-012 | P1 | Usuario logueado | 1. Ir a Perfil<br>2. Clic "Cerrar Sesión"<br>3. Confirmar | SharedPreferences limpiadas, navega a LoginActivity | ⏸️ |
| **AUTH-008** | Persistencia de sesión tras cerrar app | RF-AUTH-008, RF-AUTH-009 | P1 | Usuario logueado | 1. Login exitoso<br>2. Cerrar app (task kill)<br>3. Abrir app nuevamente | Sesión mantenida, navega directo a ListaNoticiasActivity | ⏸️ |

### NOTICIAS (NEWS)

| ID Caso | Descripción | Req Asociados | Prioridad | Precondiciones | Pasos | Resultado Esperado | Estado |
|---------|-------------|---------------|-----------|----------------|-------|-------------------|--------|
| **NEWS-001** | Cargar lista de noticias al abrir app | RF-NEWS-001, RF-NEWS-002 | P1 | Usuario logueado, hay noticias en Firestore | 1. Login<br>2. Esperar carga | Lista de noticias con título, imagen, descripción, autor, fecha | ⏸️ |
| **NEWS-002** | Filtrar noticias por categoría | RF-NEWS-003, RF-NEWS-004 | P1 | En ListaNoticiasActivity | 1. Clic en chip "Deportes" | Solo noticias de categoría Deportes visibles | ⏸️ |
| **NEWS-003** | Filtrar noticias cercanas (5km) | RF-NEWS-005, RF-NEWS-006 | P1 | GPS activado, permisos concedidos | 1. Clic en chip "Cercanas"<br>2. Seleccionar radio 5km | Solo noticias dentro de 5km de ubicación actual | ⏸️ |
| **NEWS-004** | Ver detalle completo de noticia | RF-NEWS-008, RF-NEWS-009 | P1 | En ListaNoticiasActivity | 1. Clic en una noticia | DetalleNoticiaActivity con todos los campos: título, imagen, contenido, autor, fecha, ubicación, categoría, cita, hashtags, impacto | ⏸️ |
| **NEWS-005** | Guardar noticia en favoritos | RF-NEWS-010, RF-NEWS-012 | P1 | En DetalleNoticiaActivity | 1. Clic en FAB "Guardar" | Noticia guardada en SharedPreferences, FAB cambia a "guardado" | ⏸️ |
| **NEWS-006** | Eliminar noticia de favoritos | RF-NEWS-011, RF-NEWS-012 | P2 | Noticia ya guardada | 1. Clic en FAB "Guardar" (ya guardado) | Noticia eliminada de SharedPreferences, FAB vuelve a "guardar" | ⏸️ |
| **NEWS-007** | Compartir noticia | RF-NEWS-013 | P2 | En DetalleNoticiaActivity | 1. Clic en botón "Compartir" | Intent de compartir con título de noticia | ⏸️ |
| **NEWS-008** | Pull-to-refresh actualiza noticias | RF-NEWS-015 | P2 | En ListaNoticiasActivity | 1. Deslizar hacia abajo (swipe) | Lista actualizada desde Firestore | ⏸️ |
| **NEWS-009** | Incrementar visualizaciones | RF-NEWS-014 | P3 | En DetalleNoticiaActivity | 1. Abrir detalle de noticia | Campo visualizaciones +1 en Firestore | ⏸️ |
| **NEWS-010** | Empty state sin noticias | RF-NEWS-016 | P2 | No hay noticias en Firestore | 1. Abrir app | Mensaje "No hay noticias disponibles" | ⏸️ |
| **NEWS-020** | Validar 10 categorías existentes | RF-NEWS-004 | P1 | En ListaNoticiasActivity | 1. Verificar ChipGroup de categorías | 10 chips: Política, Economía, Cultura, Deportes, Educación, Salud, Seguridad, Medio Ambiente, Turismo, Tecnología | ⏸️ |
| **NEWS-021** | Filtrar noticias cercanas (10km) | RF-NEWS-005 | P1 | GPS activado | 1. Seleccionar radio 10km | Noticias dentro de 10km | ⏸️ |
| **NEWS-022** | Cálculo de distancia correcto | RF-NEWS-006 | P1 | Noticias con coordenadas | 1. Filtrar cercanas | Distancias calculadas correctamente con Haversine | ⏸️ |
| **NEWS-023** | Filtrar noticias destacadas | RF-NEWS-007 | P1 | Hay noticias destacadas | 1. Clic chip "Destacadas" | Solo noticias con destacada=true | ⏸️ |
| **NEWS-024** | Detalle muestra cita destacada | RF-NEWS-009 | P1 | Noticia con cita | 1. Abrir detalle | Cita destacada visible | ⏸️ |
| **NEWS-025** | Guardar múltiples noticias | RF-NEWS-010 | P1 | - | 1. Guardar 3 noticias diferentes | Las 3 guardadas en SharedPreferences | ⏸️ |
| **NEWS-026** | Eliminar una de varias guardadas | RF-NEWS-011 | P2 | 3 noticias guardadas | 1. Eliminar 1 | Solo esa eliminada, las otras permanecen | ⏸️ |
| **NEWS-027** | Compartir con texto formateado | RF-NEWS-013 | P2 | - | 1. Compartir | Texto incluye título y descripción | ⏸️ |
| **NEWS-028** | Visualizaciones no duplicadas | RF-NEWS-014 | P3 | - | 1. Abrir detalle 2 veces | Visualizaciones +2 (no duplicadas) | ⏸️ |
| **NEWS-029** | Refresh muestra noticias nuevas | RF-NEWS-015 | P2 | Nueva noticia en Firestore | 1. Pull-to-refresh | Nueva noticia aparece en lista | ⏸️ |
| **NEWS-030** | Empty state con filtro aplicado | RF-NEWS-016 | P2 | Filtro sin resultados | 1. Filtrar categoría sin noticias | Mensaje "No hay noticias de esta categoría" | ⏸️ |
| **NEWS-031** | ProgressBar durante carga | RF-NEWS-017 | P2 | - | 1. Abrir app | ProgressBar visible mientras carga | ⏸️ |
| **NEWS-032** | Carga de imagen desde Storage | RF-NEWS-018 | P1 | Noticia con imagen | 1. Ver lista | Imagen cargada desde Firebase Storage URL | ⏸️ |
| **NEWS-033** | Placeholder si imagen falla | RF-NEWS-019 | P2 | Imagen URL rota | 1. Ver lista | Placeholder visible en lugar de imagen rota | ⏸️ |

### MAPA (MAP)

| ID Caso | Descripción | Req Asociados | Prioridad | Precondiciones | Pasos | Resultado Esperado | Estado |
|---------|-------------|---------------|-----------|----------------|-------|-------------------|--------|
| **MAP-001** | Cargar mapa con marcadores | RF-MAP-001, RF-MAP-002 | P1 | Usuario logueado | 1. Ir a sección Mapa | Google Maps cargado con marcadores de noticias | ⏸️ |
| **MAP-002** | Marcadores por categoría | RF-MAP-003 | P1 | En MapaActivity | 1. Verificar marcadores | Cada categoría tiene icono diferente (10 tipos) | ⏸️ |
| **MAP-003** | InfoWindow al clic | RF-MAP-004, RF-MAP-005 | P1 | Mapa cargado | 1. Clic en marcador | InfoWindow con título, imagen, categoría, distancia | ⏸️ |
| **MAP-004** | Centrar en ubicación actual | RF-MAP-008 | P1 | Permisos GPS concedidos | 1. Abrir mapa | Mapa centrado en ubicación del usuario | ⏸️ |
| **MAP-005** | Solicitud de permisos GPS | RF-MAP-007 | P1 | Permisos no concedidos | 1. Abrir mapa | Diálogo de permisos de ubicación | ⏸️ |
| **MAP-006** | Filtrar marcadores por categoría | RF-MAP-010 | P2 | Mapa cargado | 1. Seleccionar categoría "Deportes" | Solo marcadores de Deportes visibles | ⏸️ |
| **MAP-007** | Navegar a detalle desde InfoWindow | RF-MAP-006 | P2 | InfoWindow abierto | 1. Clic en InfoWindow | DetalleNoticiaActivity de esa noticia | ⏸️ |
| **MAP-008** | Error sin permisos GPS | RF-MAP-011 | P2 | Permisos denegados | 1. Denegar permisos<br>2. Abrir mapa | Mensaje "Se requieren permisos de ubicación" | ⏸️ |
| **MAP-034** | Múltiples marcadores visibles | RF-MAP-002 | P1 | 20+ noticias en Firestore | 1. Abrir mapa | Todos los marcadores visibles | ⏸️ |
| **MAP-035** | Iconos personalizados | RF-MAP-003 | P1 | - | 1. Verificar iconos | Iconos: ic_marker_politica, ic_marker_deportes, etc. | ⏸️ |
| **MAP-036** | InfoWindow con datos correctos | RF-MAP-004 | P1 | - | 1. Clic en 3 marcadores diferentes | Datos correctos en cada InfoWindow | ⏸️ |
| **MAP-037** | Intent correcto a detalle | RF-MAP-006 | P2 | - | 1. Navegar desde InfoWindow | Detalle de la noticia correcta (ID match) | ⏸️ |
| **MAP-038** | Permisos en tiempo de ejecución | RF-MAP-007 | P1 | Android 6+ | 1. Solicitar permisos | Runtime permission dialog | ⏸️ |
| **MAP-039** | Ubicación precisa | RF-MAP-008 | P1 | GPS activo | 1. Centrar en ubicación | Precisión < 50 metros | ⏸️ |
| **MAP-040** | Botón Mi Ubicación | RF-MAP-009 | P2 | - | 1. Verificar botón | Botón visible y funcional | ⏸️ |
| **MAP-041** | Filtro reactivo | RF-MAP-010 | P2 | - | 1. Cambiar filtro 3 veces | Marcadores actualizados cada vez | ⏸️ |
| **MAP-042** | Manejo de error GPS | RF-MAP-011 | P2 | GPS desactivado | 1. Desactivar GPS<br>2. Abrir mapa | Mensaje "Active el GPS" | ⏸️ |
| **MAP-043** | Marcador de usuario | RF-MAP-012 | P2 | - | 1. Ver mapa | Marcador azul en ubicación del usuario | ⏸️ |
| **MAP-044** | Interacción con mapa | RF-MAP-013 | P1 | - | 1. Zoom in/out<br>2. Pan<br>3. Rotar | Todas las interacciones funcionan | ⏸️ |
| **MAP-045** | Centro por defecto en Ibarra | RF-MAP-014 | P1 | Sin permisos GPS | 1. Abrir mapa | Centrado en (-0.3514, -78.1267) | ⏸️ |

### PERFIL (PROF)

| ID Caso | Descripción | Req Asociados | Prioridad | Precondiciones | Pasos | Resultado Esperado | Estado |
|---------|-------------|---------------|-----------|----------------|-------|-------------------|--------|
| **PROF-001** | Visualizar datos del usuario | RF-PROF-001, RF-PROF-002, RF-PROF-003 | P1 | Usuario logueado | 1. Ir a sección Perfil | Datos cargados: avatar, nombre, apellido, email, bio, ubicación | ⏸️ |
| **PROF-002** | Editar nombre y apellido | RF-PROF-004 | P1 | En PerfilActivity | 1. Clic "Editar Perfil"<br>2. Cambiar nombre<br>3. Guardar | Datos actualizados en Firestore y UI | ⏸️ |
| **PROF-003** | Cambiar foto desde galería | RF-PROF-005, RF-PROF-007, RF-PROF-008 | P1 | En EditarPerfilActivity | 1. Clic FAB cámara<br>2. Seleccionar "Galería"<br>3. Elegir imagen<br>4. Guardar | Imagen subida a Storage, URL en Firestore, avatar actualizado | ⏸️ |
| **PROF-004** | Cambiar foto desde cámara | RF-PROF-006, RF-PROF-007, RF-PROF-008 | P2 | Permisos de cámara | 1. Clic FAB cámara<br>2. Seleccionar "Cámara"<br>3. Tomar foto<br>4. Guardar | Foto subida a Storage, URL en Firestore | ⏸️ |
| **PROF-005** | Editar bio y ubicación | RF-PROF-009 | P2 | En EditarPerfilActivity | 1. Cambiar bio y ubicación<br>2. Guardar | Datos actualizados en Firestore | ⏸️ |
| **PROF-006** | Cambiar contraseña | RF-PROF-010, RF-PROF-011, RF-PROF-012 | P1 | En AjustesActivity | 1. Clic "Seguridad"<br>2. Clic "Cambiar contraseña"<br>3. Ingresar actual y nueva<br>4. Confirmar | Contraseña actualizada en Firebase Auth | ⏸️ |
| **PROF-007** | Activar notificaciones | RF-PROF-014, RF-PROF-017 | P2 | En PerfilActivity | 1. Activar switch notificaciones | Preferencia guardada en SharedPreferences | ⏸️ |
| **PROF-008** | Cambiar a modo oscuro | RF-PROF-015, RF-PROF-016, RF-PROF-017 | P2 | En PerfilActivity | 1. Activar switch modo oscuro | Tema aplicado inmediatamente, guardado en SharedPreferences | ⏸️ |
| **PROF-009** | Ver estadísticas | RF-PROF-013 | P3 | En PerfilActivity | 1. Verificar sección estadísticas | Noticias leídas, guardadas, días activo visibles | ⏸️ |
| **PROF-010** | Cerrar sesión | RF-PROF-018, RF-PROF-019 | P1 | En PerfilActivity | 1. Clic "Cerrar Sesión"<br>2. Confirmar | Diálogo de confirmación, sesión cerrada, navega a Login | ⏸️ |
| **PROF-046** | Datos desde Firestore | RF-PROF-001 | P1 | userId en SharedPreferences | 1. Abrir Perfil | Datos traídos de Firestore usando userId | ⏸️ |
| **PROF-047** | Validar campos editables | RF-PROF-004 | P1 | - | 1. Intentar guardar nombre vacío | Error de validación | ⏸️ |
| **PROF-048** | Solicitar permisos galería | RF-PROF-005 | P1 | Android 6+ | 1. Seleccionar galería | Permisos de almacenamiento solicitados | ⏸️ |
| **PROF-049** | Solicitar permisos cámara | RF-PROF-006 | P2 | Android 6+ | 1. Seleccionar cámara | Permisos de cámara solicitados | ⏸️ |
| **PROF-050** | Actualizar múltiples campos | RF-PROF-009 | P2 | - | 1. Cambiar nombre, bio, ubicación<br>2. Guardar | Todos los campos actualizados | ⏸️ |
| **PROF-051** | Validar contraseña actual | RF-PROF-011 | P1 | - | 1. Ingresar contraseña actual incorrecta | Error: "Contraseña actual incorrecta" | ⏸️ |
| **PROF-052** | Cálculo de días activo | RF-PROF-013 | P3 | Usuario con fecha_registro | 1. Ver estadísticas | Días = hoy - fecha_registro | ⏸️ |
| **PROF-053** | Desactivar notificaciones | RF-PROF-014 | P2 | Notificaciones activas | 1. Desactivar switch | Preferencia actualizada | ⏸️ |
| **PROF-054** | Persistencia de tema | RF-PROF-015 | P2 | Modo oscuro activado | 1. Cerrar app<br>2. Abrir app | Modo oscuro persiste | ⏸️ |
| **PROF-055** | Cancelar cerrar sesión | RF-PROF-019 | P2 | - | 1. Clic "Cerrar Sesión"<br>2. Cancelar | Diálogo cerrado, sesión activa | ⏸️ |
| **PROF-056** | Mostrar intereses | RF-PROF-020 | P3 | Usuario con intereses | 1. Ver perfil | ChipGroup con categorías de interés | ⏸️ |
| **PROF-057** | Gestionar intereses | RF-PROF-021 | P3 | - | 1. Agregar categoría "Deportes"<br>2. Eliminar categoría "Política" | Intereses actualizados | ⏸️ |

### CONFIGURACIÓN (CONF)

| ID Caso | Descripción | Req Asociados | Prioridad | Precondiciones | Pasos | Resultado Esperado | Estado |
|---------|-------------|---------------|-----------|----------------|-------|-------------------|--------|
| **CONF-058** | Pantalla de Ajustes | RF-CONF-001, RF-CONF-002 | P2 | En PerfilActivity | 1. Clic "Ajustes" | AjustesActivity con resumen de perfil | ⏸️ |
| **CONF-059** | Navegar a EditarPerfil | RF-CONF-003 | P2 | En AjustesActivity | 1. Clic "Editar Perfil" | EditarPerfilActivity abierto | ⏸️ |
| **CONF-060** | Opción Mis Ubicaciones | RF-CONF-004 | P3 | En AjustesActivity | 1. Clic "Mis Ubicaciones" | Diálogo con ubicaciones guardadas | ⏸️ |
| **CONF-061** | Seguridad y Privacidad | RF-CONF-005 | P2 | En AjustesActivity | 1. Clic "Seguridad y Privacidad" | Diálogo con opciones de seguridad | ⏸️ |
| **CONF-062** | Categorías de Interés | RF-CONF-006 | P3 | En AjustesActivity | 1. Clic "Categorías de Interés" | Diálogo con 10 categorías (checkboxes) | ⏸️ |
| **CONF-063** | Idioma | RF-CONF-007 | P4 | En AjustesActivity | 1. Clic "Idioma" | Diálogo mostrando "Español (solo disponible)" | ⏸️ |
| **CONF-064** | Centro de Ayuda | RF-CONF-008 | P3 | En AjustesActivity | 1. Clic "Centro de Ayuda" | Diálogo con instrucciones de uso | ⏸️ |
| **CONF-065** | Acerca de | RF-CONF-009 | P3 | En AjustesActivity | 1. Clic "Acerca de" | Diálogo con info del proyecto (versión, equipo) | ⏸️ |
| **CONF-066** | Notificaciones Push | RF-CONF-010 | P2 | En AjustesActivity | 1. Activar/desactivar switch push | Preferencia guardada | ⏸️ |
| **CONF-067** | Email Digest | RF-CONF-011 | P3 | En AjustesActivity | 1. Activar/desactivar switch email | Preferencia guardada | ⏸️ |
| **CONF-068** | Cerrar sesión desde Ajustes | RF-CONF-012 | P2 | En AjustesActivity | 1. Clic "Cerrar Sesión"<br>2. Confirmar | Sesión cerrada | ⏸️ |

### NOTIFICACIONES (NOTI)

| ID Caso | Descripción | Req Asociados | Prioridad | Precondiciones | Pasos | Resultado Esperado | Estado |
|---------|-------------|---------------|-----------|----------------|-------|-------------------|--------|
| **NOTI-069** | Pantalla de Notificaciones | RF-NOTI-001 | P3 | - | 1. Ir a NotificacionesActivity | Pantalla con configuración de notificaciones | ⏸️ |
| **NOTI-070** | Switch principal notificaciones | RF-NOTI-002 | P2 | En NotificacionesActivity | 1. Activar switch principal | Switch activado, opciones visibles | ⏸️ |
| **NOTI-071** | Opciones visibles solo si activo | RF-NOTI-003 | P3 | - | 1. Desactivar switch principal | Opciones de notificaciones ocultas | ⏸️ |
| **NOTI-072** | Notificaciones de noticias nuevas | RF-NOTI-004 | P3 | Notificaciones activas | 1. Activar switch "Noticias nuevas" | Preferencia guardada | ⏸️ |
| **NOTI-073** | Notificaciones destacadas | RF-NOTI-005 | P3 | Notificaciones activas | 1. Activar switch "Noticias destacadas" | Preferencia guardada | ⏸️ |
| **NOTI-074** | Persistencia de preferencias | RF-NOTI-006 | P2 | - | 1. Cambiar preferencias<br>2. Cerrar app<br>3. Abrir app | Preferencias persistidas en SharedPreferences | ⏸️ |
| **NOTI-075** | Integración FCM | RF-NOTI-007 | P3 | FCM configurado | 1. Enviar notificación desde Firebase Console | Notificación recibida en dispositivo | ⏸️ |

### SISTEMA (SYST)

| ID Caso | Descripción | Req Asociados | Prioridad | Precondiciones | Pasos | Resultado Esperado | Estado |
|---------|-------------|---------------|-----------|----------------|-------|-------------------|--------|
| **SYST-076** | Splash Screen con verificación | RF-SYST-001, RF-SYST-002 | P1 | App instalada | 1. Abrir app | Splash visible 2 segundos, verifica sesión | ⏸️ |
| **SYST-077** | Navegar a Login sin sesión | RF-SYST-003 | P1 | No hay sesión | 1. Abrir app | Navega a LoginActivity | ⏸️ |
| **SYST-078** | Navegar a Noticias con sesión | RF-SYST-004 | P1 | Sesión activa | 1. Abrir app | Navega a ListaNoticiasActivity | ⏸️ |
| **SYST-079** | Bottom Navigation 3 secciones | RF-SYST-005, RF-SYST-006 | P1 | En app | 1. Verificar bottom nav | 3 secciones: Noticias (resaltada), Mapa, Perfil | ⏸️ |
| **SYST-080** | Navegación con animaciones | RF-SYST-007 | P2 | - | 1. Navegar entre secciones | Transiciones suaves | ⏸️ |
| **SYST-081** | Mantener estado de secciones | RF-SYST-008 | P2 | - | 1. Scroll en Noticias<br>2. Ir a Mapa<br>3. Volver a Noticias | Scroll position mantenido | ⏸️ |
| **SYST-082** | Permisos de ubicación | RF-SYST-009 | P1 | Android 6+ | 1. Abrir Mapa sin permisos | Runtime permission dialog | ⏸️ |
| **SYST-083** | Permisos de cámara | RF-SYST-010 | P2 | Android 6+ | 1. Cambiar foto de perfil con cámara | Runtime permission dialog | ⏸️ |
| **SYST-084** | Permisos de almacenamiento | RF-SYST-011 | P2 | Android 6+ | 1. Cambiar foto desde galería | Runtime permission dialog | ⏸️ |
| **SYST-085** | Manejo de negación de permisos | RF-SYST-012 | P2 | - | 1. Denegar permiso de ubicación | Mensaje: "Se requiere permiso de ubicación para esta función" | ⏸️ |
| **SYST-086** | Modo offline con caché | RF-SYST-013 | P3 | Datos en caché | 1. Activar modo avión<br>2. Abrir app | Datos en caché visibles | ⏸️ |
| **SYST-087** | Mensaje sin conexión | RF-SYST-014 | P2 | Sin internet | 1. Desactivar WiFi y datos<br>2. Pull-to-refresh | Mensaje: "Sin conexión a internet" | ⏸️ |

### CASOS DE RENDIMIENTO (PERF)

| ID Caso | Descripción | Req Asociados | Prioridad | Criterio de Aceptación |
|---------|-------------|---------------|-----------|------------------------|
| **PERF-AUTH-001** | Tiempo de registro | RNF-AUTH-001 | P2 | < 3 segundos |
| **PERF-AUTH-002** | Tiempo de login | RNF-AUTH-002 | P2 | < 2 segundos |
| **PERF-NEWS-001** | Tiempo carga lista noticias | RNF-NEWS-001 | P1 | < 3 segundos |
| **PERF-NEWS-002** | Carga de 500 noticias | RNF-NEWS-002 | P2 | Sin lag en scroll |
| **PERF-NEWS-003** | Lazy loading de imágenes | RNF-NEWS-003 | P2 | Solo carga visibles |
| **PERF-NEWS-004** | ViewHolder pattern | RNF-NEWS-004 | P1 | Implementado |
| **PERF-MAP-001** | Tiempo carga mapa | RNF-MAP-001 | P1 | < 5 segundos |
| **PERF-MAP-002** | Renderizado 100 marcadores | RNF-MAP-002 | P2 | Sin lag |
| **PERF-MAP-003** | Clustering > 50 marcadores | RNF-MAP-003 | P3 | Implementado |
| **PERF-MAP-004** | Tiles offline | RNF-MAP-005 | P3 | Caché funcional |
| **PERF-PROF-001** | Carga datos perfil | RNF-PROF-001 | P2 | < 2 segundos |
| **PERF-PROF-002** | Subida foto perfil | RNF-PROF-002 | P2 | < 5 segundos (< 2MB) |
| **PERF-PROF-003** | Compresión de imágenes | RNF-PROF-003 | P2 | Max 1MB |
| **PERF-SYST-001** | Tamaño APK | RNF-SYST-004 | P2 | < 50 MB |
| **PERF-SYST-002** | Uso de RAM | RNF-SYST-005 | P2 | < 200 MB |
| **PERF-SYST-003** | Consumo de batería | RNF-SYST-006 | P3 | Clasificación "Bajo" |
| **PERF-SYST-004** | Tasa de crashes | RNF-SYST-007 | P1 | < 0.5% |
| **PERF-SYST-005** | Tiempo de inicio | RNF-SYST-008 | P2 | < 2 segundos |

### CASOS DE SEGURIDAD (SEC)

| ID Caso | Descripción | Req Asociados | Prioridad | Criterio de Aceptación |
|---------|-------------|---------------|-----------|------------------------|
| **SEC-AUTH-001** | Encriptación de contraseñas | RNF-AUTH-003 | P1 | Firebase Auth (bcrypt) |
| **SEC-MAP-001** | Restricción API Key | RNF-MAP-004 | P1 | Restringida a package name |
| **SEC-PROF-001** | Validación tipo de imagen | RNF-PROF-004 | P2 | Solo JPG/PNG aceptados |

### CASOS DE USABILIDAD (UX)

| ID Caso | Descripción | Req Asociados | Prioridad | Criterio de Aceptación |
|---------|-------------|---------------|-----------|------------------------|
| **UX-AUTH-001** | UI Login Material Design 3 | RNF-AUTH-004 | P3 | Cumple MD3 |
| **UX-NEWS-001** | UI Noticias Material Design 3 | RNF-NEWS-005 | P2 | Cumple MD3 |
| **UX-SYST-001** | UI Sistema Material Design 3 | RNF-SYST-003 | P2 | Cumple MD3 |

### CASOS DE COMPATIBILIDAD (COMP)

| ID Caso | Descripción | Req Asociados | Prioridad | Dispositivos de Prueba |
|---------|-------------|---------------|-----------|------------------------|
| **COMP-001** | Compatibilidad Android | RNF-SYST-001 | P1 | API 21, 26, 29, 31, 34 |
| **COMP-002** | Compatibilidad pantallas | RNF-SYST-002 | P1 | 4", 5", 6", 6.7" |

---

## ESTADO DE EJECUCIÓN DE PRUEBAS

### Resumen General

| Estado | Cantidad | Porcentaje |
|--------|----------|------------|
| ✅ **Pasado** | 0 | 0% |
| ❌ **Fallido** | 0 | 0% |
| 🔄 **En Progreso** | 0 | 0% |
| ⏸️ **Pendiente** | 133 | 100% |
| 🚫 **Bloqueado** | 0 | 0% |
| ⏭️ **Omitido** | 0 | 0% |

---

## NOTAS IMPORTANTES

### Exclusiones del Alcance

El **módulo de Eventos** ha sido **completamente eliminado** del proyecto GeoNews versión 0.1.0. Por lo tanto, NO existen:

- Requerimientos funcionales de Eventos
- Requerimientos no funcionales de Eventos
- Casos de prueba de Eventos
- Activities: ListaEventosActivity, RegistrarEventoActivity, DetalleEventoActivity
- Adapters: EventoAdapter
- Modelos: Evento.java
- Servicios: EventoServiceHTTP
- Layouts: activity_lista_eventos.xml, activity_registrar_evento.xml, activity_detalle_evento.xml, item_evento.xml
- Colecciones Firestore: eventos, asistencias_eventos
- Campo en Usuario: eventosAsistidos

### Próximos Pasos

1. **Ejecutar casos de prueba** siguiendo el orden de prioridad (P1 → P2 → P3 → P4)
2. **Actualizar estados** en esta matriz conforme se ejecuten las pruebas
3. **Documentar evidencias** (screenshots, videos, logs) en carpeta compartida
4. **Reportar errores** en Jira con referencia al ID del caso de prueba
5. **Actualizar resultado** cuando se corrijan errores y se re-testen

---

**Fin de la Matriz de Trazabilidad - GeoNews v0.1.0**

*Este documento debe actualizarse continuamente durante el ciclo de pruebas.*
