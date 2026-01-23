# GeoNews - Aplicación de Noticias Locales Geolocalizadas

![GeoNews Logo](logo.png)

Sistema móvil Android para visualizar noticias locales de Ibarra con geolocalización en tiempo real.

## 📱 Descripción

GeoNews es una aplicación móvil Android que permite a los ciudadanos de Ibarra acceder a noticias locales geolocalizadas, visualizar noticias en un mapa interactivo con marcadores por categoría, y gestionar su perfil personalizado.

## ✨ Características Principales

- 📰 **Noticias Geolocalizadas** - Visualiza noticias con ubicación precisa en Ibarra
- 🗺️ **Mapa Interactivo** - Explora noticias en Google Maps con marcadores personalizados por categoría
- 📍 **Filtrado por Proximidad** - Encuentra noticias cercanas (5km, 10km, 20km)
- 🏷️ **10 Categorías** - Política, Economía, Cultura, Deportes, Educación, Salud, Seguridad, Medio Ambiente, Turismo, Tecnología
- 👤 **Perfil Personalizado** - Gestiona tu información y preferencias
- ⭐ **Noticias Destacadas** - Filtra contenido importante
- 💾 **Guardar Favoritos** - Guarda noticias para leer después
- 🌙 **Modo Oscuro** - Interfaz adaptable a preferencias del usuario
- 🔔 **Notificaciones Push** - Recibe alertas de noticias importantes

## 🏗️ Arquitectura

### Aplicación Móvil (Android)
```
app/
├── src/main/java/com/tesistitulacion/noticiaslocales/
│   ├── activities/      # 12 Activities (pantallas)
│   ├── adapters/        # 2 Adapters (RecyclerView, MapInfoWindow)
│   ├── modelo/          # 4 Modelos de datos (Usuario, Noticia, Categoria, Parroquia)
│   ├── firebase/        # FirebaseManager (Singleton)
│   ├── db/              # ApiConfig, ServiceHTTP
│   └── utils/           # 5 Utilidades (Preferences, Theme, Dialog, Ubicacion, etc.)
└── src/main/res/        # Layouts XML, Drawables, Values
```

### Backend (Firebase)
- **Firebase Authentication** - Autenticación de usuarios
- **Cloud Firestore** - Base de datos NoSQL (5 colecciones)
- **Firebase Storage** - Almacenamiento de imágenes
- **Cloud Messaging** - Notificaciones push

## 📂 Estructura del Proyecto

```
noticiaslocales0.1.0/
├── app/                      # Código fuente de la aplicación Android
│   ├── src/
│   │   ├── main/
│   │   │   ├── java/        # Código Java
│   │   │   ├── res/         # Recursos (layouts, drawables, values)
│   │   │   └── AndroidManifest.xml
│   │   └── test/            # Tests unitarios
│   ├── build.gradle         # Configuración Gradle del módulo
│   └── google-services.json # Configuración Firebase
├── docs/                    # 📄 TODA LA DOCUMENTACIÓN
│   ├── README.md
│   ├── PLAN_DE_PRUEBAS_GEONEWS.md
│   ├── MATRIZ_TRAZABILIDAD_GEONEWS.md
│   ├── CASOS_DE_PRUEBA_GEONEWS.md
│   ├── DIAGRAMA_UML_APP_MOVIL.md
│   ├── SCRIPT_MYSQL_GEONEWS.sql
│   └── ... (17 documentos más)
├── backend_fastapi/         # Backend FastAPI (opcional)
├── gradle/                  # Sistema de build Gradle
├── build.gradle             # Configuración Gradle del proyecto
├── settings.gradle
├── gradlew.bat
├── local.properties
├── .gitignore
└── README.md               # Este archivo
```

## 🚀 Instalación y Configuración

### Requisitos Previos
- **Android Studio** Hedgehog 2023.1.1 o superior
- **JDK** 11 o 17
- **Android SDK** API 21 (Android 5.0) - API 34 (Android 14)
- **Cuenta Firebase** con proyecto configurado
- **Google Maps API Key**

### Pasos de Instalación

1. **Clonar el repositorio**
```bash
git clone [URL_DEL_REPOSITORIO]
cd noticiaslocales0.1.0
```

2. **Configurar Firebase**
   - Descarga `google-services.json` desde Firebase Console
   - Colócalo en `app/google-services.json`

3. **Configurar Google Maps**
   - Obtén tu API Key de Google Cloud Console
   - Agrégala en `app/src/main/AndroidManifest.xml`:
   ```xml
   <meta-data
       android:name="com.google.android.geo.API_KEY"
       android:value="TU_API_KEY_AQUI" />
   ```

4. **Abrir en Android Studio**
   - Abre el proyecto en Android Studio
   - Espera a que Gradle sincronice las dependencias

5. **Compilar y Ejecutar**
   ```bash
   ./gradlew assembleDebug
   ```
   O usa el botón "Run" en Android Studio

## 🧪 Pruebas

El proyecto cuenta con documentación completa de pruebas en la carpeta `docs/`:

- **162 Casos de Prueba** organizados en 11 categorías
- **125 Requerimientos** (99 funcionales + 26 no funcionales)
- **100% de Cobertura** de requerimientos

Para ejecutar las pruebas:
```bash
./gradlew test
```

## 🛠️ Tecnologías Utilizadas

| Categoría | Tecnología |
|-----------|-----------|
| **Lenguaje** | Java |
| **Min SDK** | API 21 (Android 5.0 Lollipop) |
| **Target SDK** | API 34 (Android 14) |
| **UI** | Material Design 3 |
| **Backend** | Firebase (Auth, Firestore, Storage, FCM) |
| **Mapas** | Google Maps SDK for Android |
| **Location** | Google Play Services Location |
| **Imágenes** | Glide |
| **HTTP** | Retrofit 2 (opcional) |
| **Build** | Gradle 8.0+ |
| **IDE** | Android Studio |

## 📊 Estadísticas del Proyecto

- **Total de Clases:** 28 clases Java
- **Activities:** 12 pantallas
- **Requerimientos:** 125 (99 funcionales + 26 no funcionales)
- **Casos de Prueba:** 162 casos
- **Cobertura:** 100%
- **Categorías de Noticias:** 10
- **Parroquias de Ibarra:** 12 (5 urbanas + 7 rurales)

## 🚫 Módulo Eliminado

El **módulo de Eventos** fue completamente eliminado en la versión 0.1.0. La aplicación se enfoca únicamente en noticias geolocalizadas.

## 📱 Navegación

La aplicación cuenta con **3 secciones principales**:

1. **Noticias** 📰
   - Lista de noticias con filtros
   - Detalle completo de cada noticia
   - Guardar en favoritos
   - Compartir

2. **Mapa** 🗺️
   - Visualización de noticias en Google Maps
   - Marcadores por categoría
   - InfoWindow con datos de noticia
   - Filtrado por categoría

3. **Perfil** 👤
   - Datos del usuario
   - Editar información
   - Cambiar foto de perfil
   - Configuración de la app
   - Modo oscuro
   - Cerrar sesión

## 🔐 Seguridad

- Autenticación con Firebase Authentication
- Contraseñas encriptadas con bcrypt
- Reglas de seguridad en Firestore y Storage
- API Keys restringidas por package name

## 📝 Documentación

Toda la documentación técnica y académica se encuentra en la carpeta `docs/`:

- Plan de Pruebas
- Matriz de Trazabilidad
- Casos de Prueba (162 casos)
- Diagramas UML
- Scripts de Base de Datos
- Resultados y Discusión
- Documento de Tesis

Ver [docs/README.md](docs/README.md) para más detalles.

## 👥 Equipo

- **Desarrollador:** [Nombre del Estudiante]
- **Director de Tesis:** [Nombre del Director]
- **Universidad:** [Nombre de la Universidad]

## 📄 Licencia

Este proyecto es parte de un trabajo de titulación académico.

## 📞 Contacto

- **Email:** [email@ejemplo.com]
- **GitHub:** [usuario/repositorio]

---

**Versión:** 0.1.0
**Fecha:** Enero 2026
**Estado:** En Desarrollo
