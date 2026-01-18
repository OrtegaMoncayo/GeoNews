# CASOS DE PRUEBA - GEONEWS

---

## IDENTIFICACIÓN DE PROYECTO

| Campo | Valor |
|-------|-------|
| **Nombre de la Aplicación** | GeoNews - Sistema de Noticias Locales Geolocalizadas |
| **Líder de Producto** | [Nombre del Líder de Producto] |
| **Participantes en pruebas** | [Nombres de los Participantes] |
| **Responsable de verificación** | [Nombre del Responsable QA] |
| **Fecha de Pruebas** | [DD/MM/AAAA] |
| **Estado del Proyecto** | ☐ PROY. EN PRODUCCIÓN &nbsp;&nbsp;&nbsp; ☑ PROY. NUEVO |

---

## DESCRIPCIÓN DE LAS ACCIONES Y/O CONDICIONES DE LAS PRUEBAS

| Nro | Escenario | Casos de prueba | Resultado de Prueba | | | Descripción de errores | Fotografía |
|-----|-----------|-----------------|---------------------|---|---|------------------------|------------|
| | | | **Correcto** | **Parcial** | **Fallido** | | |

---

### 1. AUTENTICACIÓN

| Nro | Escenario | Casos de prueba | Correcto | Parcial | Fallido | Descripción de errores | Fotografía |
|-----|-----------|-----------------|----------|---------|---------|------------------------|------------|
| 1.1 | Autenticación | Registro de Usuario con Datos Válidos | ☐ | ☐ | ☐ | Verificar que un nuevo usuario puede registrarse con nombre, apellido, email y contraseña válidos | |
| 1.2 | Autenticación | Registro con Email Duplicado | ☐ | ☐ | ☐ | Verificar que no se permite registro con email ya existente | |
| 1.3 | Autenticación | Validación de Formato de Email | ☐ | ☐ | ☐ | Verificar que se valida formato correcto de email (contiene @) | |
| 1.4 | Autenticación | Validación de Contraseña Mínima | ☐ | ☐ | ☐ | Verificar que contraseña debe tener mínimo 6 caracteres | |
| 1.5 | Autenticación | Login Exitoso | ☐ | ☐ | ☐ | Verificar que un usuario registrado puede iniciar sesión correctamente | |
| 1.6 | Autenticación | Login con Credenciales Inválidas | ☐ | ☐ | ☐ | Verificar que no se permite login con credenciales incorrectas | |
| 1.7 | Autenticación | Persistencia de Sesión | ☐ | ☐ | ☐ | Verificar que la sesión se mantiene al cerrar y abrir la app | |
| 1.8 | Autenticación | Cierre de Sesión | ☐ | ☐ | ☐ | Verificar que el usuario puede cerrar sesión correctamente | |
| 1.9 | Autenticación | Redirección tras Cierre de Sesión | ☐ | ☐ | ☐ | Verificar que tras cerrar sesión se redirige a LoginActivity | |
| 1.10 | Autenticación | Creación de Documento en Firestore | ☐ | ☐ | ☐ | Verificar que se crea documento de usuario en Firestore tras registro | |

---

### 2. NOTICIAS

| Nro | Escenario | Casos de prueba | Correcto | Parcial | Fallido | Descripción de errores | Fotografía |
|-----|-----------|-----------------|----------|---------|---------|------------------------|------------|
| 2.1 | Noticias | Listar Noticias | ☐ | ☐ | ☐ | Verificar que se muestran las noticias desde Firestore | |
| 2.2 | Noticias | Ver Detalle de Noticia | ☐ | ☐ | ☐ | Verificar navegación a detalle de noticia completo | |
| 2.3 | Noticias | Mostrar Título, Descripción e Imagen | ☐ | ☐ | ☐ | Verificar que cada noticia muestra título, descripción, imagen, autor y fecha | |
| 2.4 | Noticias | Filtrar por Categoría | ☐ | ☐ | ☐ | Verificar filtrado de noticias por las 10 categorías disponibles | |
| 2.5 | Noticias | Filtrar Noticias Cercanas (5km) | ☐ | ☐ | ☐ | Verificar filtrado de noticias por proximidad (radio 5km) | |
| 2.6 | Noticias | Filtrar Noticias Cercanas (10km) | ☐ | ☐ | ☐ | Verificar filtrado de noticias por proximidad (radio 10km) | |
| 2.7 | Noticias | Filtrar Noticias Cercanas (20km) | ☐ | ☐ | ☐ | Verificar filtrado de noticias por proximidad (radio 20km) | |
| 2.8 | Noticias | Cálculo de Distancia (Haversine) | ☐ | ☐ | ☐ | Verificar que la distancia se calcula correctamente con fórmula Haversine | |
| 2.9 | Noticias | Filtrar Noticias Destacadas | ☐ | ☐ | ☐ | Verificar que solo se muestran noticias marcadas como destacadas | |
| 2.10 | Noticias | Detalle Completo de Noticia | ☐ | ☐ | ☐ | Verificar que el detalle muestra: título, imagen, descripción, contenido, autor, fecha, ubicación, categoría, cita destacada, hashtags, impacto comunitario | |
| 2.11 | Noticias | Guardar Noticia en Favoritos | ☐ | ☐ | ☐ | Verificar que se puede guardar noticia en favoritos | |
| 2.12 | Noticias | Eliminar Noticia de Favoritos | ☐ | ☐ | ☐ | Verificar que se puede eliminar noticia de favoritos | |
| 2.13 | Noticias | Persistencia de Favoritos | ☐ | ☐ | ☐ | Verificar que los favoritos se guardan en SharedPreferences | |
| 2.14 | Noticias | Compartir Noticia | ☐ | ☐ | ☐ | Verificar función compartir noticia con título y link | |
| 2.15 | Noticias | Incrementar Visualizaciones | ☐ | ☐ | ☐ | Verificar que se incrementa contador de visualizaciones al abrir detalle | |
| 2.16 | Noticias | Pull to Refresh | ☐ | ☐ | ☐ | Verificar actualización de lista con pull-to-refresh | |
| 2.17 | Noticias | Empty State sin Noticias | ☐ | ☐ | ☐ | Verificar mensaje cuando no hay noticias disponibles | |
| 2.18 | Noticias | ProgressBar durante Carga | ☐ | ☐ | ☐ | Verificar que se muestra ProgressBar mientras cargan noticias | |
| 2.19 | Noticias | Carga de Imágenes desde Firebase Storage | ☐ | ☐ | ☐ | Verificar que las imágenes se cargan desde Firebase Storage | |
| 2.20 | Noticias | Placeholder si Imagen Falla | ☐ | ☐ | ☐ | Verificar que se muestra placeholder si imagen no carga | |
| 2.21 | Noticias | Caché de Imágenes con Glide | ☐ | ☐ | ☐ | Verificar caché de imágenes con librería Glide | |
| 2.22 | Noticias | Filtro por Fecha (Orden Cronológico) | ☐ | ☐ | ☐ | Verificar que las noticias se ordenan por fecha descendente | |
| 2.23 | Noticias | Mostrar las 10 Categorías | ☐ | ☐ | ☐ | Verificar que existen chips de: Política, Economía, Cultura, Deportes, Educación, Salud, Seguridad, Medio Ambiente, Turismo, Tecnología | |
| 2.24 | Noticias | Hashtags Visibles en Detalle | ☐ | ☐ | ☐ | Verificar que los hashtags se muestran en detalle de noticia | |
| 2.25 | Noticias | Impacto Comunitario Visible | ☐ | ☐ | ☐ | Verificar que el impacto comunitario se muestra en detalle | |
| 2.26 | Noticias | Cita Destacada Visible | ☐ | ☐ | ☐ | Verificar que la cita destacada se muestra en detalle | |
| 2.27 | Noticias | Chip de Categoría con Color | ☐ | ☐ | ☐ | Verificar que el chip de categoría muestra color correcto | |
| 2.28 | Noticias | Icono de Categoría | ☐ | ☐ | ☐ | Verificar que se muestra icono de categoría correspondiente | |

---

### 3. MAPA

| Nro | Escenario | Casos de prueba | Correcto | Parcial | Fallido | Descripción de errores | Fotografía |
|-----|-----------|-----------------|----------|---------|---------|------------------------|------------|
| 3.1 | Mapa | Cargar Mapa | ☐ | ☐ | ☐ | Verificar carga correcta del Google Maps | |
| 3.2 | Mapa | Mostrar Marcadores de Noticias | ☐ | ☐ | ☐ | Verificar que se muestran marcadores de noticias en el mapa | |
| 3.3 | Mapa | Marcadores Diferenciados por Categoría | ☐ | ☐ | ☐ | Verificar que cada categoría tiene icono de marcador diferente (10 tipos) | |
| 3.4 | Mapa | InfoWindow al Clic en Marcador | ☐ | ☐ | ☐ | Verificar que se muestra InfoWindow al hacer clic en marcador | |
| 3.5 | Mapa | Contenido de InfoWindow | ☐ | ☐ | ☐ | Verificar que InfoWindow muestra: título, imagen thumbnail, categoría, distancia | |
| 3.6 | Mapa | Navegar a Detalle desde InfoWindow | ☐ | ☐ | ☐ | Verificar navegación a detalle de noticia desde InfoWindow | |
| 3.7 | Mapa | Solicitud de Permisos de Ubicación | ☐ | ☐ | ☐ | Verificar que se solicitan permisos de ubicación al abrir mapa | |
| 3.8 | Mapa | Mi Ubicación | ☐ | ☐ | ☐ | Verificar centrado en ubicación actual del usuario | |
| 3.9 | Mapa | Botón de Mi Ubicación | ☐ | ☐ | ☐ | Verificar que existe botón "Mi Ubicación" en el mapa | |
| 3.10 | Mapa | Filtrar Marcadores por Categoría | ☐ | ☐ | ☐ | Verificar filtrado de marcadores por categoría seleccionada | |
| 3.11 | Mapa | Error sin Permisos GPS | ☐ | ☐ | ☐ | Verificar manejo de error cuando no hay permisos de ubicación | |
| 3.12 | Mapa | Marcador de Ubicación del Usuario | ☐ | ☐ | ☐ | Verificar que se muestra marcador de ubicación actual del usuario | |
| 3.13 | Mapa | Zoom, Pan y Rotación | ☐ | ☐ | ☐ | Verificar que el mapa permite zoom, pan y rotación | |
| 3.14 | Mapa | Centro por Defecto en Ibarra | ☐ | ☐ | ☐ | Verificar que el mapa se centra por defecto en Ibarra (-0.3514, -78.1267) | |
| 3.15 | Mapa | Renderizado de Múltiples Marcadores | ☐ | ☐ | ☐ | Verificar que se renderizan correctamente hasta 100 marcadores | |
| 3.16 | Mapa | Marcadores Personalizados | ☐ | ☐ | ☐ | Verificar iconos personalizados: ic_marker_politica, ic_marker_deportes, ic_marker_cultura, etc. | |
| 3.17 | Mapa | Datos Correctos en InfoWindow | ☐ | ☐ | ☐ | Verificar que los datos mostrados en InfoWindow coinciden con la noticia | |
| 3.18 | Mapa | Permisos en Tiempo de Ejecución | ☐ | ☐ | ☐ | Verificar solicitud de permisos en tiempo de ejecución (Android 6+) | |
| 3.19 | Mapa | Precisión de Ubicación | ☐ | ☐ | ☐ | Verificar que la precisión de ubicación es menor a 50 metros | |
| 3.20 | Mapa | Error con GPS Desactivado | ☐ | ☐ | ☐ | Verificar mensaje de error cuando GPS está desactivado | |

---

### 4. PERFIL

| Nro | Escenario | Casos de prueba | Correcto | Parcial | Fallido | Descripción de errores | Fotografía |
|-----|-----------|-----------------|----------|---------|---------|------------------------|------------|
| 4.1 | Perfil | Ver Perfil | ☐ | ☐ | ☐ | Verificar visualización de datos del usuario (avatar, nombre, apellido, email, bio, ubicación) | |
| 4.2 | Perfil | Cargar Datos desde Firestore | ☐ | ☐ | ☐ | Verificar que los datos se cargan desde Firestore usando userId | |
| 4.3 | Perfil | Editar Nombre y Apellido | ☐ | ☐ | ☐ | Verificar edición de nombre y apellido del usuario | |
| 4.4 | Perfil | Cambiar Foto de Perfil desde Galería | ☐ | ☐ | ☐ | Verificar cambio de foto de perfil desde galería | |
| 4.5 | Perfil | Cambiar Foto de Perfil desde Cámara | ☐ | ☐ | ☐ | Verificar cambio de foto de perfil desde cámara | |
| 4.6 | Perfil | Subir Foto a Firebase Storage | ☐ | ☐ | ☐ | Verificar que la foto se sube a carpeta fotos_perfil/{userId} en Firebase Storage | |
| 4.7 | Perfil | Actualizar URL de Foto en Firestore | ☐ | ☐ | ☐ | Verificar que el campo fotoPerfil se actualiza con URL de Storage | |
| 4.8 | Perfil | Editar Bio y Ubicación | ☐ | ☐ | ☐ | Verificar edición de biografía y ubicación | |
| 4.9 | Perfil | Cambiar Contraseña | ☐ | ☐ | ☐ | Verificar cambio de contraseña del usuario | |
| 4.10 | Perfil | Validar Contraseña Actual | ☐ | ☐ | ☐ | Verificar que se valida contraseña actual antes de cambiarla | |
| 4.11 | Perfil | Actualizar Contraseña en Firebase Auth | ☐ | ☐ | ☐ | Verificar que la contraseña se actualiza en Firebase Authentication | |
| 4.12 | Perfil | Ver Estadísticas | ☐ | ☐ | ☐ | Verificar visualización de estadísticas: noticias leídas, guardadas, días activo | |
| 4.13 | Perfil | Activar/Desactivar Notificaciones | ☐ | ☐ | ☐ | Verificar activación y desactivación de notificaciones push | |
| 4.14 | Perfil | Cambiar Tema (Modo Oscuro) | ☐ | ☐ | ☐ | Verificar cambio entre modo claro y modo oscuro | |
| 4.15 | Perfil | Aplicar Tema Inmediatamente | ☐ | ☐ | ☐ | Verificar que el tema se aplica inmediatamente al cambiar switch | |
| 4.16 | Perfil | Guardar Preferencias | ☐ | ☐ | ☐ | Verificar que las preferencias se guardan en SharedPreferences | |
| 4.17 | Perfil | Cerrar Sesión | ☐ | ☐ | ☐ | Verificar cierre de sesión correcto desde perfil | |
| 4.18 | Perfil | Diálogo de Confirmación al Cerrar Sesión | ☐ | ☐ | ☐ | Verificar que se muestra diálogo de confirmación antes de cerrar sesión | |
| 4.19 | Perfil | Mostrar ChipGroup de Intereses | ☐ | ☐ | ☐ | Verificar que se muestran chips de categorías de interés del usuario | |
| 4.20 | Perfil | Agregar/Eliminar Categorías de Interés | ☐ | ☐ | ☐ | Verificar gestión de categorías de interés | |
| 4.21 | Perfil | Validar Campos Obligatorios | ☐ | ☐ | ☐ | Verificar validación de campos obligatorios al editar perfil | |
| 4.22 | Perfil | Permisos de Galería | ☐ | ☐ | ☐ | Verificar solicitud de permisos de almacenamiento para galería | |
| 4.23 | Perfil | Permisos de Cámara | ☐ | ☐ | ☐ | Verificar solicitud de permisos de cámara | |
| 4.24 | Perfil | Compresión de Imagen | ☐ | ☐ | ☐ | Verificar que las imágenes se comprimen antes de subir (max 1MB) | |
| 4.25 | Perfil | Validación de Tipo de Imagen | ☐ | ☐ | ☐ | Verificar que solo se aceptan imágenes JPG o PNG | |
| 4.26 | Perfil | Cálculo de Días Activo | ☐ | ☐ | ☐ | Verificar cálculo correcto de días activo desde registro | |
| 4.27 | Perfil | Persistencia de Tema | ☐ | ☐ | ☐ | Verificar que el tema persiste al cerrar y abrir la app | |
| 4.28 | Perfil | Cancelar Cierre de Sesión | ☐ | ☐ | ☐ | Verificar que se puede cancelar el cierre de sesión | |

---

### 5. CONFIGURACIÓN Y AJUSTES

| Nro | Escenario | Casos de prueba | Correcto | Parcial | Fallido | Descripción de errores | Fotografía |
|-----|-----------|-----------------|----------|---------|---------|------------------------|------------|
| 5.1 | Configuración | Pantalla de Ajustes | ☐ | ☐ | ☐ | Verificar acceso a pantalla de Ajustes desde Perfil | |
| 5.2 | Configuración | Resumen de Perfil en Ajustes | ☐ | ☐ | ☐ | Verificar que se muestra resumen de perfil (avatar, nombre, ubicación) | |
| 5.3 | Configuración | Navegar a Editar Perfil | ☐ | ☐ | ☐ | Verificar navegación a EditarPerfilActivity desde Ajustes | |
| 5.4 | Configuración | Opción "Mis Ubicaciones" | ☐ | ☐ | ☐ | Verificar diálogo de ubicaciones guardadas | |
| 5.5 | Configuración | Opción "Seguridad y Privacidad" | ☐ | ☐ | ☐ | Verificar diálogo de opciones de seguridad | |
| 5.6 | Configuración | Opción "Categorías de Interés" | ☐ | ☐ | ☐ | Verificar diálogo con las 10 categorías (checkboxes) | |
| 5.7 | Configuración | Opción "Idioma" | ☐ | ☐ | ☐ | Verificar diálogo de idioma (solo Español disponible) | |
| 5.8 | Configuración | Opción "Centro de Ayuda" | ☐ | ☐ | ☐ | Verificar diálogo con instrucciones de uso de la app | |
| 5.9 | Configuración | Opción "Acerca de" | ☐ | ☐ | ☐ | Verificar diálogo con información del proyecto (versión, equipo) | |
| 5.10 | Configuración | Switch Notificaciones Push | ☐ | ☐ | ☐ | Verificar activación/desactivación de notificaciones push | |
| 5.11 | Configuración | Switch Email Digest | ☐ | ☐ | ☐ | Verificar activación/desactivación de digest por email | |
| 5.12 | Configuración | Cerrar Sesión desde Ajustes | ☐ | ☐ | ☐ | Verificar cierre de sesión desde AjustesActivity | |
| 5.13 | Configuración | Diálogo Cambiar Contraseña | ☐ | ☐ | ☐ | Verificar diálogo de cambio de contraseña desde Seguridad | |

---

### 6. NOTIFICACIONES

| Nro | Escenario | Casos de prueba | Correcto | Parcial | Fallido | Descripción de errores | Fotografía |
|-----|-----------|-----------------|----------|---------|---------|------------------------|------------|
| 6.1 | Notificaciones | Pantalla de Notificaciones | ☐ | ☐ | ☐ | Verificar acceso a NotificacionesActivity | |
| 6.2 | Notificaciones | Switch Principal de Notificaciones | ☐ | ☐ | ☐ | Verificar switch para activar/desactivar notificaciones | |
| 6.3 | Notificaciones | Opciones Visibles Solo si Activo | ☐ | ☐ | ☐ | Verificar que opciones se muestran solo cuando notificaciones están activas | |
| 6.4 | Notificaciones | Notificaciones de Noticias Nuevas | ☐ | ☐ | ☐ | Verificar switch para notificaciones de noticias nuevas | |
| 6.5 | Notificaciones | Notificaciones de Noticias Destacadas | ☐ | ☐ | ☐ | Verificar switch para notificaciones de noticias destacadas | |
| 6.6 | Notificaciones | Persistencia de Preferencias | ☐ | ☐ | ☐ | Verificar que preferencias se guardan en SharedPreferences | |
| 6.7 | Notificaciones | Integración Firebase Cloud Messaging | ☐ | ☐ | ☐ | Verificar recepción de notificaciones push desde Firebase | |

---

### 7. SISTEMA Y NAVEGACIÓN

| Nro | Escenario | Casos de prueba | Correcto | Parcial | Fallido | Descripción de errores | Fotografía |
|-----|-----------|-----------------|----------|---------|---------|------------------------|------------|
| 7.1 | Sistema | Splash Screen | ☐ | ☐ | ☐ | Verificar visualización de Splash Screen al iniciar | |
| 7.2 | Sistema | Verificación de Sesión | ☐ | ☐ | ☐ | Verificar que Splash verifica sesión activa en SharedPreferences | |
| 7.3 | Sistema | Redirección a Login sin Sesión | ☐ | ☐ | ☐ | Verificar navegación a LoginActivity si no hay sesión | |
| 7.4 | Sistema | Redirección a Noticias con Sesión | ☐ | ☐ | ☐ | Verificar navegación a ListaNoticiasActivity si hay sesión activa | |
| 7.5 | Sistema | Bottom Navigation con 3 Secciones | ☐ | ☐ | ☐ | Verificar Bottom Navigation: Noticias, Mapa, Perfil | |
| 7.6 | Sistema | Resaltar Sección Activa | ☐ | ☐ | ☐ | Verificar que la sección activa se resalta en Bottom Navigation | |
| 7.7 | Sistema | Navegación con Animaciones | ☐ | ☐ | ☐ | Verificar transiciones suaves entre secciones | |
| 7.8 | Sistema | Mantener Estado de Secciones | ☐ | ☐ | ☐ | Verificar que se mantiene estado (scroll, filtros) al navegar | |
| 7.9 | Sistema | Solicitud de Permisos de Ubicación | ☐ | ☐ | ☐ | Verificar solicitud de permisos de ubicación cuando sea necesario | |
| 7.10 | Sistema | Solicitud de Permisos de Cámara | ☐ | ☐ | ☐ | Verificar solicitud de permisos de cámara cuando sea necesario | |
| 7.11 | Sistema | Solicitud de Permisos de Almacenamiento | ☐ | ☐ | ☐ | Verificar solicitud de permisos de almacenamiento cuando sea necesario | |
| 7.12 | Sistema | Manejo de Negación de Permisos | ☐ | ☐ | ☐ | Verificar mensajes claros cuando se niegan permisos | |
| 7.13 | Sistema | Modo Offline con Caché | ☐ | ☐ | ☐ | Verificar funcionamiento en modo offline con datos en caché | |
| 7.14 | Sistema | Mensaje sin Conexión | ☐ | ☐ | ☐ | Verificar mensaje cuando no hay conexión a internet | |

---

### 8. RENDIMIENTO

| Nro | Escenario | Casos de prueba | Correcto | Parcial | Fallido | Descripción de errores | Fotografía |
|-----|-----------|-----------------|----------|---------|---------|------------------------|------------|
| 8.1 | Rendimiento | Tiempo de Registro | ☐ | ☐ | ☐ | Verificar que el registro toma menos de 3 segundos | |
| 8.2 | Rendimiento | Tiempo de Login | ☐ | ☐ | ☐ | Verificar que el login toma menos de 2 segundos | |
| 8.3 | Rendimiento | Tiempo de Carga de Noticias | ☐ | ☐ | ☐ | Verificar que la lista de noticias carga en menos de 3 segundos | |
| 8.4 | Rendimiento | Carga de 500 Noticias | ☐ | ☐ | ☐ | Verificar que se pueden cargar 500 noticias sin lag en scroll | |
| 8.5 | Rendimiento | Lazy Loading de Imágenes | ☐ | ☐ | ☐ | Verificar que solo se cargan imágenes visibles en viewport | |
| 8.6 | Rendimiento | ViewHolder Pattern | ☐ | ☐ | ☐ | Verificar implementación de ViewHolder pattern en RecyclerView | |
| 8.7 | Rendimiento | Tiempo de Carga de Mapa | ☐ | ☐ | ☐ | Verificar que el mapa carga en menos de 5 segundos | |
| 8.8 | Rendimiento | Renderizado de 100 Marcadores | ☐ | ☐ | ☐ | Verificar que se pueden renderizar 100 marcadores sin lag | |
| 8.9 | Rendimiento | Clustering de Marcadores | ☐ | ☐ | ☐ | Verificar clustering cuando hay más de 50 marcadores cercanos | |
| 8.10 | Rendimiento | Tiempo de Carga de Perfil | ☐ | ☐ | ☐ | Verificar que los datos del perfil cargan en menos de 2 segundos | |
| 8.11 | Rendimiento | Subida de Foto de Perfil | ☐ | ☐ | ☐ | Verificar que la subida de foto toma menos de 5 segundos (< 2MB) | |
| 8.12 | Rendimiento | Tamaño del APK | ☐ | ☐ | ☐ | Verificar que el tamaño del APK es menor a 50 MB | |
| 8.13 | Rendimiento | Uso de RAM | ☐ | ☐ | ☐ | Verificar que el uso de RAM es menor a 200 MB | |
| 8.14 | Rendimiento | Consumo de Batería | ☐ | ☐ | ☐ | Verificar que el consumo de batería es clasificado como "Bajo" | |
| 8.15 | Rendimiento | Tasa de Crashes | ☐ | ☐ | ☐ | Verificar que la tasa de crashes es menor a 0.5% | |
| 8.16 | Rendimiento | Tiempo de Inicio de App | ☐ | ☐ | ☐ | Verificar que el tiempo de inicio es menor a 2 segundos | |

---

### 9. SEGURIDAD

| Nro | Escenario | Casos de prueba | Correcto | Parcial | Fallido | Descripción de errores | Fotografía |
|-----|-----------|-----------------|----------|---------|---------|------------------------|------------|
| 9.1 | Seguridad | Encriptación de Contraseñas | ☐ | ☐ | ☐ | Verificar que las contraseñas se almacenan encriptadas en Firebase Auth | |
| 9.2 | Seguridad | Restricción de Google Maps API Key | ☐ | ☐ | ☐ | Verificar que la API Key está restringida al package name | |
| 9.3 | Seguridad | Validación de Tipo de Imagen | ☐ | ☐ | ☐ | Verificar que solo se aceptan imágenes JPG o PNG en Storage | |
| 9.4 | Seguridad | Reglas de Seguridad Firestore | ☐ | ☐ | ☐ | Verificar que no se puede acceder a datos sin autenticación | |
| 9.5 | Seguridad | Reglas de Seguridad Storage | ☐ | ☐ | ☐ | Verificar que no se puede subir archivos sin autenticación | |
| 9.6 | Seguridad | Acceso a Datos de Otros Usuarios | ☐ | ☐ | ☐ | Verificar que un usuario no puede acceder a datos de otros usuarios | |

---

### 10. COMPATIBILIDAD

| Nro | Escenario | Casos de prueba | Correcto | Parcial | Fallido | Descripción de errores | Fotografía |
|-----|-----------|-----------------|----------|---------|---------|------------------------|------------|
| 10.1 | Compatibilidad | Android 5.0 (API 21) | ☐ | ☐ | ☐ | Verificar funcionamiento en Android 5.0 Lollipop | |
| 10.2 | Compatibilidad | Android 8.0 (API 26) | ☐ | ☐ | ☐ | Verificar funcionamiento en Android 8.0 Oreo | |
| 10.3 | Compatibilidad | Android 10 (API 29) | ☐ | ☐ | ☐ | Verificar funcionamiento en Android 10 | |
| 10.4 | Compatibilidad | Android 12 (API 31) | ☐ | ☐ | ☐ | Verificar funcionamiento en Android 12 | |
| 10.5 | Compatibilidad | Android 14 (API 34) | ☐ | ☐ | ☐ | Verificar funcionamiento en Android 14 | |
| 10.6 | Compatibilidad | Pantalla 4 pulgadas | ☐ | ☐ | ☐ | Verificar UI en pantallas pequeñas (4") | |
| 10.7 | Compatibilidad | Pantalla 5 pulgadas | ☐ | ☐ | ☐ | Verificar UI en pantallas medianas (5") | |
| 10.8 | Compatibilidad | Pantalla 6 pulgadas | ☐ | ☐ | ☐ | Verificar UI en pantallas grandes (6") | |
| 10.9 | Compatibilidad | Pantalla 6.7 pulgadas | ☐ | ☐ | ☐ | Verificar UI en pantallas extra grandes (6.7") | |
| 10.10 | Compatibilidad | Diferentes Resoluciones | ☐ | ☐ | ☐ | Verificar adaptación a diferentes resoluciones (hdpi, xhdpi, xxhdpi) | |

---

### 11. USABILIDAD

| Nro | Escenario | Casos de prueba | Correcto | Parcial | Fallido | Descripción de errores | Fotografía |
|-----|-----------|-----------------|----------|---------|---------|------------------------|------------|
| 11.1 | Usabilidad | Material Design 3 - Login | ☐ | ☐ | ☐ | Verificar que LoginActivity sigue lineamientos de Material Design 3 | |
| 11.2 | Usabilidad | Material Design 3 - Noticias | ☐ | ☐ | ☐ | Verificar que ListaNoticiasActivity sigue MD3 | |
| 11.3 | Usabilidad | Material Design 3 - Mapa | ☐ | ☐ | ☐ | Verificar que MapaActivity sigue MD3 | |
| 11.4 | Usabilidad | Material Design 3 - Perfil | ☐ | ☐ | ☐ | Verificar que PerfilActivity sigue MD3 | |
| 11.5 | Usabilidad | Feedback Visual de Acciones | ☐ | ☐ | ☐ | Verificar que todas las acciones tienen feedback visual (ripple, animaciones) | |
| 11.6 | Usabilidad | Mensajes de Error Claros | ☐ | ☐ | ☐ | Verificar que los mensajes de error son claros y descriptivos | |
| 11.7 | Usabilidad | Navegación Intuitiva | ☐ | ☐ | ☐ | Verificar que la navegación es intuitiva y fácil de entender | |
| 11.8 | Usabilidad | Accesibilidad de Botones | ☐ | ☐ | ☐ | Verificar que los botones tienen tamaño adecuado (min 48dp) | |
| 11.9 | Usabilidad | Contraste de Colores | ☐ | ☐ | ☐ | Verificar contraste adecuado entre texto y fondo | |
| 11.10 | Usabilidad | Modo Oscuro Funcional | ☐ | ☐ | ☐ | Verificar que el modo oscuro es legible y consistente | |

---

## RESUMEN DE RESULTADOS

| Categoría | Total Casos | Correctos | Parciales | Fallidos | % Éxito |
|-----------|-------------|-----------|-----------|----------|---------|
| **1. Autenticación** | 10 | 0 | 0 | 0 | 0% |
| **2. Noticias** | 28 | 0 | 0 | 0 | 0% |
| **3. Mapa** | 20 | 0 | 0 | 0 | 0% |
| **4. Perfil** | 28 | 0 | 0 | 0 | 0% |
| **5. Configuración** | 13 | 0 | 0 | 0 | 0% |
| **6. Notificaciones** | 7 | 0 | 0 | 0 | 0% |
| **7. Sistema** | 14 | 0 | 0 | 0 | 0% |
| **8. Rendimiento** | 16 | 0 | 0 | 0 | 0% |
| **9. Seguridad** | 6 | 0 | 0 | 0 | 0% |
| **10. Compatibilidad** | 10 | 0 | 0 | 0 | 0% |
| **11. Usabilidad** | 10 | 0 | 0 | 0 | 0% |
| **TOTAL** | **162** | **0** | **0** | **0** | **0%** |

---

## FIRMAS

| Responsable | Nombre | Firma | Fecha |
|-------------|--------|-------|-------|
| **Responsable de Pruebas** | | _________________ | ____/____/____ |
| **Líder Control de Calidad** | | _________________ | ____/____/____ |
| **Product Owner** | | _________________ | ____/____/____ |

---

## COMENTARIOS

```
[Espacio para comentarios generales sobre las pruebas realizadas]





```

---

## NOTAS IMPORTANTES

### ⚠️ Módulo de Eventos ELIMINADO

El **módulo de Eventos** ha sido completamente eliminado del proyecto GeoNews v0.1.0.

**NO** existen casos de prueba para:
- Inscribirse a Evento
- Eventos Próximos
- Ver Ubicación Evento
- Recordatorio de Evento
- Listar Eventos

### ✅ Módulos Actuales (3 Secciones)

La aplicación GeoNews cuenta únicamente con **3 secciones principales**:
1. **Noticias** - Visualización, filtrado y detalle de noticias geolocalizadas
2. **Mapa** - Visualización de noticias en mapa con marcadores por categoría
3. **Perfil** - Gestión de perfil de usuario y configuración

### 📱 Plataforma

- **Sistema Operativo:** Android
- **Versiones Soportadas:** API 21 (Android 5.0) - API 34 (Android 14)
- **Backend:** Firebase (Authentication, Firestore, Storage, Cloud Messaging)
- **Mapas:** Google Maps SDK for Android

---

**Documento generado:** Enero 2026
**Versión de GeoNews:** 0.1.0
**Total de Casos de Prueba:** 162 casos

---

**FIN DEL DOCUMENTO DE CASOS DE PRUEBA**
