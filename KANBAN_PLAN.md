# 📋 PLANIFICACIÓN KANBAN - NOTICIAS LOCALES IBARRA

**Proyecto:** Sistema de Noticias Locales Ibarra
**Versión:** 0.1.0
**Estado General:** 85% Completado
**Última Actualización:** 11 de Noviembre de 2025

---

## 🎯 OBJETIVO DEL SPRINT FINAL

Completar el 15% restante del proyecto para tener una aplicación lista para producción y presentación de tesis.

**Duración estimada:** 2-3 semanas
**Prioridad:** Funcionalidades core antes que features adicionales

---

## 📊 TABLERO KANBAN

### 🟢 COMPLETADO (Done)

#### **Infraestructura y Setup**
- ✅ Proyecto Android creado y configurado
- ✅ Firebase proyecto configurado (noticiaslocalesibarra)
- ✅ Firestore Database habilitado
- ✅ Firebase Cloud Messaging configurado
- ✅ Google Maps SDK integrado
- ✅ Material Design 3 implementado
- ✅ Gradle con Firebase BOM 32.7.0
- ✅ Backend FastAPI + Firestore desplegado en Cloud Run
- ✅ Migración de datos MySQL → Firestore completada

#### **Modelos de Datos**
- ✅ Modelo Noticia con firestoreId
- ✅ Modelo Evento con firestoreId
- ✅ Modelo Usuario
- ✅ Modelo Parroquia con coordenadas
- ✅ Modelo Categoria

#### **Firebase Integration**
- ✅ FirebaseManager.java (Singleton CRUD)
- ✅ MyFirebaseMessagingService.java
- ✅ FCMTokenHelper.java
- ✅ google-services.json configurado
- ✅ ServiceAccountKey.json en backend

#### **Activities Implementadas**
- ✅ SplashActivity (pantalla inicial)
- ✅ BaseActivity (navegación común)
- ✅ ListaNoticiasActivity con Firebase
- ✅ DetalleNoticiaActivity con Firebase
- ✅ ListaEventosActivity con Firebase
- ✅ DetalleEventoActivity con Firebase
- ✅ MapaActivity con Google Maps
- ✅ RegistrarEventoActivity (UI completa)
- ✅ LoginActivity (UI con mock)
- ✅ RegistroActivity (UI con mock)
- ✅ PerfilActivity (UI básica)

#### **Adapters y UI**
- ✅ NoticiaAdapter para RecyclerView
- ✅ EventoAdapter para RecyclerView
- ✅ Layouts XML (14 archivos)
- ✅ Temas NoActionBar para detail screens
- ✅ CollapsingToolbarLayout en detalles
- ✅ DrawerLayout + BottomNavigation

#### **Backend API**
- ✅ Backend FastAPI + Firestore desplegado
- ✅ URL: https://noticiasibarra-api-166115544761.southamerica-east1.run.app
- ✅ Endpoint /noticias (7 noticias)
- ✅ Endpoint /eventos (9 eventos)
- ✅ Endpoint /stats
- ✅ Documentación Swagger en /docs
- ✅ CORS configurado para Android

#### **Datos en Firestore**
- ✅ 12 Parroquias (5 urbanas, 7 rurales)
- ✅ 10 Categorías
- ✅ 7 Noticias activas
- ✅ 9 Eventos futuros
- ✅ 3 Usuarios de prueba

#### **Documentación**
- ✅ README.md principal
- ✅ FIREBASE_SETUP.md
- ✅ firebase_schema.md
- ✅ DIAGNOSTICO_FIREBASE.md
- ✅ backend_fastapi/README.md
- ✅ migrate_mysql_to_firestore.py

---

### 🔴 ALTA PRIORIDAD (To Do - Sprint 1)

**Estimación:** 1 semana

#### **US-001: Implementar Firebase Authentication**
- **Descripción:** Reemplazar el sistema mock de login con Firebase Auth
- **Tareas:**
  1. [ ] Habilitar Firebase Authentication en consola
  2. [ ] Configurar Email/Password provider
  3. [ ] Actualizar LoginActivity con FirebaseAuth
  4. [ ] Actualizar RegistroActivity con FirebaseAuth
  5. [ ] Implementar recuperación de contraseña
  6. [ ] Guardar usuario en Firestore al registrarse
  7. [ ] Manejar estados de sesión correctamente
- **Criterios de Aceptación:**
  - Usuario puede registrarse con email/password
  - Usuario puede iniciar sesión
  - Sesión persiste entre aperturas de app
  - Cerrar sesión funciona correctamente
- **Prioridad:** 🔴 CRÍTICA
- **Estimación:** 2 días

#### **US-002: Guardar tokens FCM en Firestore**
- **Descripción:** Persistir tokens de FCM para enviar notificaciones personalizadas
- **Tareas:**
  1. [ ] Actualizar FirebaseManager con método saveUserToken()
  2. [ ] Guardar token al iniciar sesión
  3. [ ] Actualizar token cuando se renueve
  4. [ ] Agregar campo fcmToken al modelo Usuario
  5. [ ] Implementar suscripción a tópicos por parroquia
- **Criterios de Aceptación:**
  - Token se guarda en Firestore al login
  - Token se actualiza automáticamente
  - Se puede enviar notificación a usuario específico
- **Prioridad:** 🔴 CRÍTICA
- **Estimación:** 1 día

#### **US-003: Implementar carga de imágenes con Glide**
- **Descripción:** Agregar librería Glide para cargar imágenes eficientemente
- **Tareas:**
  1. [ ] Agregar dependencia Glide en build.gradle
  2. [ ] Actualizar NoticiaAdapter con Glide
  3. [ ] Actualizar EventoAdapter con Glide
  4. [ ] Actualizar DetalleNoticiaActivity con Glide
  5. [ ] Actualizar DetalleEventoActivity con Glide
  6. [ ] Agregar placeholders y error images
  7. [ ] Implementar caché de imágenes
- **Criterios de Aceptación:**
  - Imágenes cargan correctamente desde URLs
  - Placeholder se muestra mientras carga
  - Error image se muestra si falla
  - Caché funciona correctamente
- **Prioridad:** 🔴 CRÍTICA
- **Estimación:** 1 día

#### **US-004: Testing de creación de eventos**
- **Descripción:** Verificar y completar funcionalidad de crear eventos
- **Tareas:**
  1. [ ] Testear guardado en Firestore desde RegistrarEventoActivity
  2. [ ] Corregir conversión de fechas
  3. [ ] Validar campos obligatorios
  4. [ ] Agregar feedback al usuario (Toast/Snackbar)
  5. [ ] Regresar a lista de eventos después de crear
  6. [ ] Agregar usuario como creador del evento
- **Criterios de Aceptación:**
  - Evento se guarda correctamente en Firestore
  - Validaciones funcionan
  - Usuario recibe confirmación
  - Evento aparece en la lista inmediatamente
- **Prioridad:** 🔴 CRÍTICA
- **Estimación:** 1 día

#### **US-005: Upload de imágenes a Firebase Storage**
- **Descripción:** Permitir subir imágenes al crear noticias/eventos
- **Tareas:**
  1. [ ] Configurar Firebase Storage en proyecto
  2. [ ] Implementar ImagePickerHelper
  3. [ ] Agregar botón seleccionar imagen en RegistrarEventoActivity
  4. [ ] Subir imagen a Storage
  5. [ ] Obtener URL de descarga
  6. [ ] Guardar URL en Firestore
  7. [ ] Comprimir imagen antes de subir
- **Criterios de Aceptación:**
  - Usuario puede seleccionar imagen de galería
  - Imagen se sube a Firebase Storage
  - URL se guarda en documento Firestore
  - Imagen se muestra en detalle
- **Prioridad:** 🔴 ALTA
- **Estimación:** 2 días

---

### 🟡 MEDIA PRIORIDAD (To Do - Sprint 2)

**Estimación:** 1 semana

#### **US-006: Implementar publicación de noticias**
- **Descripción:** Permitir a usuarios crear nuevas noticias
- **Tareas:**
  1. [ ] Crear PublicarNoticiaActivity
  2. [ ] Diseñar layout con campos (título, descripción, contenido, imagen)
  3. [ ] Implementar selección de categoría
  4. [ ] Implementar selección de parroquia
  5. [ ] Agregar obtención de ubicación actual (GPS)
  6. [ ] Implementar guardado en Firestore
  7. [ ] Validar permisos de usuario (solo admin puede publicar)
- **Criterios de Aceptación:**
  - Usuario admin puede crear noticia
  - Campos son validados
  - Ubicación se obtiene automáticamente
  - Noticia aparece en lista
- **Prioridad:** 🟡 MEDIA
- **Estimación:** 2 días

#### **US-007: Sistema de notificaciones push desde backend**
- **Descripción:** Enviar notificaciones cuando se publica nueva noticia
- **Tareas:**
  1. [ ] Implementar endpoint POST /notificaciones/nueva-noticia en backend
  2. [ ] Obtener lista de tokens FCM de Firestore
  3. [ ] Enviar notificación multicast a todos los usuarios
  4. [ ] Agregar data payload con noticiaId
  5. [ ] Manejar notificación en MyFirebaseMessagingService
  6. [ ] Abrir DetalleNoticiaActivity al hacer tap
- **Criterios de Aceptación:**
  - Notificación se envía al publicar noticia
  - Usuarios reciben notificación
  - Tap abre detalle de noticia correcta
- **Prioridad:** 🟡 MEDIA
- **Estimación:** 1 día

#### **US-008: Búsqueda por texto en noticias**
- **Descripción:** Permitir buscar noticias por palabra clave
- **Tareas:**
  1. [ ] Agregar SearchView en ListaNoticiasActivity
  2. [ ] Implementar filtrado local por título/descripción
  3. [ ] Actualizar adapter en tiempo real
  4. [ ] Agregar indicador "sin resultados"
  5. [ ] Opcional: Implementar búsqueda full-text con Algolia
- **Criterios de Aceptación:**
  - Usuario puede buscar por texto
  - Resultados se filtran en tiempo real
  - Búsqueda es case-insensitive
- **Prioridad:** 🟡 MEDIA
- **Estimación:** 1 día

#### **US-009: Implementar inscripción a eventos**
- **Descripción:** Permitir a usuarios inscribirse a eventos
- **Tareas:**
  1. [ ] Agregar colección "inscripciones" en Firestore
  2. [ ] Implementar botón "Inscribirse" en DetalleEventoActivity
  3. [ ] Validar cupo disponible
  4. [ ] Guardar inscripción en Firestore
  5. [ ] Incrementar cupoActual
  6. [ ] Mostrar usuarios inscritos (solo creador)
  7. [ ] Enviar notificación de confirmación
- **Criterios de Aceptación:**
  - Usuario puede inscribirse si hay cupos
  - Cupo se actualiza correctamente
  - Usuario recibe confirmación
  - No puede inscribirse dos veces
- **Prioridad:** 🟡 MEDIA
- **Estimación:** 2 días

#### **US-010: Filtros avanzados en noticias**
- **Descripción:** Agregar UI para filtrar noticias por categoría y parroquia
- **Tareas:**
  1. [ ] Diseñar bottom sheet con filtros
  2. [ ] Implementar selección de categoría (chips)
  3. [ ] Implementar selección de parroquia (spinner)
  4. [ ] Aplicar filtros a query de Firestore
  5. [ ] Mostrar filtros activos
  6. [ ] Botón "Limpiar filtros"
- **Criterios de Aceptación:**
  - Usuario puede filtrar por categoría
  - Usuario puede filtrar por parroquia
  - Filtros se pueden combinar
  - Resultados se actualizan
- **Prioridad:** 🟡 MEDIA
- **Estimación:** 1 día

---

### 🔵 BAJA PRIORIDAD (Backlog)

**Estimación:** 1-2 semanas (opcional para v1.0)

#### **US-011: Sistema de favoritos**
- **Descripción:** Permitir guardar noticias como favoritas
- **Tareas:**
  1. [ ] Crear colección "favoritos" en Firestore
  2. [ ] Agregar botón favorito (estrella) en DetalleNoticiaActivity
  3. [ ] Implementar toggle favorito
  4. [ ] Agregar pestaña "Favoritos" en navegación
  5. [ ] Mostrar lista de noticias favoritas
- **Prioridad:** 🔵 BAJA
- **Estimación:** 1 día

#### **US-012: Compartir noticia**
- **Descripción:** Permitir compartir noticias en redes sociales
- **Tareas:**
  1. [ ] Agregar botón compartir en DetalleNoticiaActivity
  2. [ ] Implementar Intent.ACTION_SEND
  3. [ ] Formato de texto (título + URL)
  4. [ ] Agregar deep linking para abrir noticia desde link
- **Prioridad:** 🔵 BAJA
- **Estimación:** 0.5 días

#### **US-013: Comentarios en noticias**
- **Descripción:** Sistema de comentarios en cada noticia
- **Tareas:**
  1. [ ] Crear subcolección "comentarios" en noticias
  2. [ ] Diseñar UI de comentarios
  3. [ ] Implementar RecyclerView de comentarios
  4. [ ] Agregar campo de texto para comentar
  5. [ ] Validar autenticación
  6. [ ] Implementar borrado de propios comentarios
- **Prioridad:** 🔵 BAJA
- **Estimación:** 2 días

#### **US-014: Editar perfil de usuario**
- **Descripción:** Permitir editar información del perfil
- **Tareas:**
  1. [ ] Actualizar PerfilActivity con campos editables
  2. [ ] Agregar botón "Editar"
  3. [ ] Permitir cambiar nombre, teléfono
  4. [ ] Implementar upload de foto de perfil
  5. [ ] Guardar cambios en Firestore
  6. [ ] Validar campos
- **Prioridad:** 🔵 BAJA
- **Estimación:** 1 día

#### **US-015: Historial de noticias leídas**
- **Descripción:** Mostrar noticias que el usuario ha leído
- **Tareas:**
  1. [ ] Crear colección "historial" en Firestore
  2. [ ] Guardar timestamp al abrir DetalleNoticiaActivity
  3. [ ] Agregar pestaña "Historial" en navegación
  4. [ ] Mostrar lista ordenada por fecha
  5. [ ] Implementar borrar historial
- **Prioridad:** 🔵 BAJA
- **Estimación:** 1 día

#### **US-016: Caché offline con Room Database**
- **Descripción:** Guardar datos localmente para uso offline
- **Tareas:**
  1. [ ] Agregar dependencia Room
  2. [ ] Crear entidades (NoticiaEntity, EventoEntity)
  3. [ ] Crear DAOs
  4. [ ] Crear AppDatabase
  5. [ ] Implementar sync Firestore ↔ Room
  6. [ ] Mostrar datos de Room cuando no hay internet
- **Prioridad:** 🔵 BAJA
- **Estimación:** 2 días

#### **US-017: Paginación infinita**
- **Descripción:** Cargar noticias en lotes para mejor performance
- **Tareas:**
  1. [ ] Implementar paginación de Firestore (startAfter)
  2. [ ] Cargar 10 noticias por página
  3. [ ] Detectar scroll al final (EndlessScrollListener)
  4. [ ] Cargar siguiente página automáticamente
  5. [ ] Mostrar loading indicator al cargar más
- **Prioridad:** 🔵 BAJA
- **Estimación:** 1 día

---

### ⚪ TESTING Y CALIDAD

#### **US-018: Testing de unidades**
- **Tareas:**
  1. [ ] Unit tests para FirebaseManager
  2. [ ] Unit tests para UbicacionUtils
  3. [ ] Unit tests para modelos
  4. [ ] Alcanzar 70% code coverage
- **Prioridad:** 🟡 MEDIA
- **Estimación:** 2 días

#### **US-019: Testing de integración**
- **Tareas:**
  1. [ ] Integration tests para Activities principales
  2. [ ] Test de flujo login → ver noticia
  3. [ ] Test de flujo crear evento
  4. [ ] Test de notificaciones
- **Prioridad:** 🟡 MEDIA
- **Estimación:** 2 días

#### **US-020: Testing de UI (Espresso)**
- **Tareas:**
  1. [ ] UI test para ListaNoticiasActivity
  2. [ ] UI test para DetalleNoticiaActivity
  3. [ ] UI test para LoginActivity
  4. [ ] UI test para navegación
- **Prioridad:** 🔵 BAJA
- **Estimación:** 2 días

---

### 🚀 DEPLOYMENT

#### **US-021: Preparar APK de producción**
- **Tareas:**
  1. [ ] Generar keystore para firma
  2. [ ] Configurar signing config en build.gradle
  3. [ ] Crear versión release
  4. [ ] Probar APK en múltiples dispositivos
  5. [ ] Crear iconos de diferentes tamaños
  6. [ ] Preparar screenshots para Play Store
- **Prioridad:** 🔴 CRÍTICA (antes de lanzar)
- **Estimación:** 1 día

#### **US-022: Configurar Firebase Security Rules de producción**
- **Tareas:**
  1. [ ] Diseñar reglas de seguridad
  2. [ ] Solo lectura pública para noticias/eventos
  3. [ ] Solo escritura autenticados
  4. [ ] Validaciones de datos
  5. [ ] Testear reglas con emulador
  6. [ ] Aplicar en producción
- **Prioridad:** 🔴 CRÍTICA (antes de lanzar)
- **Estimación:** 1 día

#### **US-023: Monitoreo y Analytics**
- **Tareas:**
  1. [ ] Configurar Firebase Analytics events
  2. [ ] Track apertura de noticias
  3. [ ] Track creación de eventos
  4. [ ] Configurar Crashlytics
  5. [ ] Configurar Performance Monitoring
- **Prioridad:** 🟡 MEDIA
- **Estimación:** 0.5 días

---

## 📈 MÉTRICAS Y ESTIMACIONES

### Progreso General
```
Total User Stories: 23
Completadas: ~15 (65%)
Alta Prioridad: 5 (estimado 7 días)
Media Prioridad: 5 (estimado 7 días)
Baja Prioridad: 8 (estimado 10 días)
Testing: 3 (estimado 6 días)
Deployment: 3 (estimado 2.5 días)
```

### Sprint Planning

**Sprint 1 (Semana 1): Funcionalidades Core**
- US-001: Firebase Authentication
- US-002: Tokens FCM
- US-003: Glide para imágenes
- US-004: Testing eventos
- US-005: Upload imágenes

**Sprint 2 (Semana 2): Features Adicionales**
- US-006: Publicar noticias
- US-007: Notificaciones push
- US-008: Búsqueda
- US-009: Inscripción eventos
- US-010: Filtros avanzados

**Sprint 3 (Semana 3): Testing y Deployment**
- US-018: Unit tests
- US-019: Integration tests
- US-021: APK producción
- US-022: Security Rules
- US-023: Monitoreo

---

## 🎯 DEFINICIÓN DE "DONE"

Una User Story se considera completada cuando:

1. ✅ Código implementado y funcional
2. ✅ Probado manualmente en dispositivo
3. ✅ Sin errores en logs
4. ✅ UI responsive y sin bugs visuales
5. ✅ Documentado (si aplica)
6. ✅ Committed a Git con mensaje claro
7. ✅ Revisado por al menos 1 persona (opcional en tesis)

---

## 🔄 PROCESO DE TRABAJO

### Daily Workflow
1. Revisar tareas pendientes del día
2. Mover US a "En Progreso"
3. Desarrollar feature
4. Testear en dispositivo
5. Commit y push a Git
6. Mover a "Done"
7. Actualizar este documento

### Weekly Review
1. Revisar progreso del sprint
2. Ajustar prioridades si necesario
3. Re-estimar tareas pendientes
4. Planear siguiente semana

---

## 📝 NOTAS IMPORTANTES

### Dependencias entre US
- US-002 depende de US-001 (necesita auth para guardar token)
- US-006 depende de US-005 (upload de imagen)
- US-007 depende de US-002 (tokens en Firestore)
- US-009 depende de US-001 (auth para inscribirse)

### Riesgos Identificados
1. **Firebase Auth**: Puede requerir más tiempo del estimado
2. **Upload de imágenes**: Tamaño y compresión pueden ser complejos
3. **Notificaciones**: Testing requiere múltiples dispositivos
4. **Testing**: Puede descubrir bugs que requieran tiempo adicional

### Recursos Necesarios
- Dispositivo Android físico (para testing)
- Cuenta Google Cloud con billing habilitado (gratis hasta límite)
- Tiempo: 2-3 semanas full-time o 4-6 semanas part-time

---

**Última actualización:** 11 de Noviembre de 2025
**Responsable:** Richard Adrian Ortega Moncayo
**Institución:** IST 17 de Julio
