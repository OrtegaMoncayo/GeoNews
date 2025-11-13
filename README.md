# 📱 Noticias Locales Ibarra

Aplicación móvil Android para noticias y eventos locales de Ibarra, Ecuador, con geolocalización y notificaciones.

![Android](https://img.shields.io/badge/Android-3DDC84?style=for-the-badge&logo=android&logoColor=white)
![Java](https://img.shields.io/badge/Java-ED8B00?style=for-the-badge&logo=java&logoColor=white)
![Firebase](https://img.shields.io/badge/Firebase-FFCA28?style=for-the-badge&logo=firebase&logoColor=black)
![FastAPI](https://img.shields.io/badge/FastAPI-009688?style=for-the-badge&logo=fastapi&logoColor=white)
![Python](https://img.shields.io/badge/Python-3776AB?style=for-the-badge&logo=python&logoColor=white)
![Cloud Run](https://img.shields.io/badge/Cloud_Run-4285F4?style=for-the-badge&logo=googlecloud&logoColor=white)

**Estado:** 🟢 85% Completado | **Versión:** 0.1.0 | **Última actualización:** 11 de Noviembre de 2025

## 📋 Descripción

**Noticias Locales Ibarra** es una aplicación móvil desarrollada como proyecto de tesis para el Instituto Superior Tecnológico 17 de Julio. Permite a los usuarios de la ciudad de Ibarra, Ecuador:

- 📰 Ver noticias locales con geolocalización
- 📍 Buscar noticias por ubicación y radio de distancia
- 🎉 Crear y visualizar eventos comunitarios
- 🗺️ Explorar noticias y eventos en mapa interactivo
- 🔔 Recibir notificaciones de nuevos eventos
- 🏘️ Filtrar contenido por parroquias (5 urbanas, 7 rurales)

### 📚 Documentación del Proyecto

- 📋 **[KANBAN_PLAN.md](./KANBAN_PLAN.md)** - Planificación Kanban completa con 23 User Stories
- 🏗️ **[ARQUITECTURA.md](./ARQUITECTURA.md)** - Arquitectura del sistema detallada
- 📊 **[ESTADO_PROYECTO.md](./ESTADO_PROYECTO.md)** - Estado actual del proyecto (85% completado)
- 🔥 **[FIREBASE_SETUP.md](./FIREBASE_SETUP.md)** - Guía de configuración de Firebase
- 📐 **[firebase_schema.md](./firebase_schema.md)** - Estructura de Firestore
- 🐛 **[DIAGNOSTICO_FIREBASE.md](./DIAGNOSTICO_FIREBASE.md)** - Troubleshooting

---

## 🚀 Características

### **Frontend Android**
- ✅ Interfaz Material Design 3
- ✅ Modo oscuro/claro
- ✅ Geolocalización con Google Maps
- ✅ Búsqueda por radio de distancia (Haversine)
- ✅ RecyclerView con animaciones
- ✅ EncryptedSharedPreferences para datos sensibles
- ✅ Navegación fluida con DrawerLayout
- ✅ Validación de formularios

### **Backend FastAPI + Firebase**
- ✅ API REST asíncrona con FastAPI
- ✅ Desplegado en Google Cloud Run
- ✅ 11 endpoints completamente funcionales
- ✅ Firebase Firestore como base de datos
- ✅ Validación automática con Pydantic
- ✅ Búsqueda geográfica optimizada (Haversine)
- ✅ CORS configurado para Android
- ✅ Documentación Swagger automática en `/docs`
- ✅ URL: https://noticiasibarra-api-166115544761.southamerica-east1.run.app

### **Base de Datos Firebase Firestore**
- ✅ Firestore Database NoSQL
- ✅ 5 colecciones principales
- ✅ 7 noticias activas
- ✅ 9 eventos futuros
- ✅ 12 parroquias de Ibarra (5 urbanas, 7 rurales)
- ✅ 10 categorías de contenido
- ✅ GeoPoint nativo para coordenadas
- ✅ Migración exitosa desde MySQL

---

## 📁 Estructura del Proyecto

```
noticiaslocales/
├── app/                          # Aplicación Android
│   └── src/
│       └── main/
│           ├── java/com/tesistitulacion/noticiaslocales/
│           │   ├── models/       # Modelos de datos (5)
│           │   ├── db/           # Servicios HTTP (4)
│           │   ├── adapters/     # Adaptadores RecyclerView (2)
│           │   ├── utils/        # Utilidades (2)
│           │   └── *.java        # Activities (8)
│           ├── res/              # Recursos Android
│           └── AndroidManifest.xml
│
├── backend_flask/                # Backend FastAPI
│   ├── main.py                   # Aplicación FastAPI (550 líneas)
│   ├── database.sql              # Schema + datos iniciales
│   ├── requirements.txt          # Dependencias Python
│   ├── start.bat                 # Script de inicio Windows
│   ├── .env.example              # Variables de entorno ejemplo
│   └── README.md                 # Documentación del backend
│
├── documentacion/                # Documentación del proyecto
│   ├── CONFIGURACION_WIFI.md     # Guía WiFi para testing
│   ├── RESUMEN_BACKEND.md        # Resumen completo backend
│   └── ...                       # Otros archivos de documentación
│
├── build.gradle                  # Configuración Gradle proyecto
├── settings.gradle               # Configuración módulos
└── README.md                     # Este archivo
```

---

## 🛠️ Tecnologías Utilizadas

### **Android**
- **Java 11** - Lenguaje principal
- **Android SDK 34** - API Level 34 (Android 14)
- **Material Design 3** - Componentes UI
- **Google Maps SDK** - Mapas y geolocalización
- **RecyclerView** - Listas optimizadas
- **Gson** - Serialización JSON

### **Backend**
- **FastAPI 0.115** - Framework web moderno
- **Python 3.10+** - Lenguaje backend
- **aiomysql** - MySQL asíncrono
- **Pydantic 2.9** - Validación de datos
- **Uvicorn** - Servidor ASGI

### **Base de Datos**
- **MySQL 8.0** - Base de datos relacional
- **12 Parroquias** - 5 urbanas, 7 rurales de Ibarra

---

## 📦 Instalación

### **Requisitos Previos**
- ✅ Android Studio Hedgehog o superior
- ✅ JDK 11 o superior
- ✅ Python 3.10 o superior
- ✅ MySQL 8.0
- ✅ Google Maps API Key

### **1. Clonar el Repositorio**
```bash
git clone https://github.com/OrtegaMoncayo/NoticiasIbarra.git
cd NoticiasIbarra
```

### **2. Configurar Backend**
```bash
cd backend_flask

# Crear entorno virtual
python -m venv venv

# Activar entorno (Windows)
venv\Scripts\activate

# Instalar dependencias
pip install -r requirements.txt

# Configurar base de datos
mysql -u root -p < database.sql

# Editar main.py con tu password de MySQL (línea 41)
# DB_CONFIG['password'] = 'tu_password_aqui'

# Iniciar servidor
python main.py
```

El backend estará disponible en:
- **API**: http://localhost:8000
- **Documentación**: http://localhost:8000/docs

### **3. Configurar Android**

#### **Obtener Google Maps API Key**
1. Ir a [Google Cloud Console](https://console.cloud.google.com/)
2. Crear proyecto "Noticias Ibarra"
3. Habilitar "Maps SDK for Android"
4. Crear API Key
5. Editar `app/src/main/AndroidManifest.xml` línea 37:
```xml
<meta-data
    android:name="com.google.android.geo.API_KEY"
    android:value="TU_API_KEY_AQUI"/>
```

#### **Configurar URL del Backend**

**Para Emulador Android:**
```java
// app/src/main/java/com/tesistitulacion/noticiaslocales/db/ApiConfig.java
public static final String BASE_URL = "http://10.0.2.2:8000/api/";
```

**Para Dispositivo Físico:**
```java
// Obtener IP con: ipconfig (Windows) o ifconfig (Linux/Mac)
public static final String BASE_URL = "http://192.168.1.XXX:8000/api/";
```

Ver [CONFIGURACION_WIFI.md](documentacion/CONFIGURACION_WIFI.md) para instrucciones detalladas.

### **4. Compilar e Instalar App**
```bash
# Desde Android Studio:
Build > Make Project (Ctrl+F9)
Run > Run 'app' (Shift+F10)

# O desde terminal:
./gradlew assembleDebug
adb install app/build/outputs/apk/debug/app-debug.apk
```

---

## 🧪 Uso

### **1. Registro e Inicio de Sesión**
- Crear cuenta nueva con email y contraseña
- Iniciar sesión con credenciales

### **2. Ver Noticias**
- **Lista de Noticias**: Ver todas las noticias disponibles
- **Buscar por Radio**: Ingresar coordenadas y radio en km
- **Mapa de Noticias**: Ver noticias geolocalizadas en mapa

### **3. Gestionar Eventos**
- **Ver Eventos**: Lista de eventos programados
- **Crear Evento**: Formulario con fecha, ubicación, categoría
- **Notificaciones**: Email automático al crear evento

### **4. Configuración**
- Cambiar tema (oscuro/claro)
- Cerrar sesión

---

## 📡 API Endpoints

### **Salud del Sistema**
- `GET /` - Información de la API
- `GET /health` - Estado de API y base de datos

### **Eventos**
- `GET /eventos` - Listar todos los eventos
- `POST /eventos` - Crear evento (+ notificación)
- `GET /eventos/{id}` - Obtener evento específico

### **Parroquias**
- `GET /parroquias` - Todas las parroquias
- `GET /parroquias?tipo=urbana` - Solo urbanas
- `GET /parroquias/{id}` - Parroquia específica

### **Noticias**
- `GET /noticias` - Listar con filtros
- `GET /noticias/radio?latitud=X&longitud=Y&radio_km=Z` - Búsqueda geográfica
- `GET /noticias/{id}` - Noticia específica

**Documentación completa**: http://localhost:8000/docs

---

## 🔧 Configuración

### **Variables de Entorno Backend**
Crear archivo `.env` en `backend_flask/`:
```env
DB_HOST=localhost
DB_PORT=3306
DB_USER=root
DB_PASSWORD=tu_password_aqui
DB_NAME=noticias_ibarra2
API_HOST=0.0.0.0
API_PORT=8000
```

### **Firewall (Testing con Dispositivo Físico)**
```powershell
# Windows PowerShell (Administrador)
New-NetFirewallRule -DisplayName "FastAPI Port 8000" `
    -Direction Inbound -Protocol TCP -LocalPort 8000 -Action Allow
```

---

## 📊 Base de Datos

### **Tablas**
1. **parroquias** - 12 parroquias de Ibarra
2. **categorias** - 10 categorías de noticias
3. **usuarios** - Usuarios del sistema
4. **noticias** - Noticias con geolocalización
5. **eventos** - Eventos comunitarios

### **Datos Iniciales**
- ✅ 12 parroquias (GPS real)
- ✅ 10 categorías
- ✅ 1 usuario admin
- ✅ 6 noticias de ejemplo
- ✅ 5 eventos de ejemplo

---

## 🐛 Solución de Problemas

### **Backend no inicia**
```bash
# Verificar MySQL corriendo
net start MySQL80  # Windows

# Verificar conexión
mysql -u root -p

# Reinstalar dependencias
pip install -r requirements.txt
```

### **App no conecta al backend**
1. Verificar backend corriendo: http://localhost:8000/docs
2. Verificar `ApiConfig.java` tiene IP correcta
3. Para emulador: usar `10.0.2.2:8000`
4. Para dispositivo: ver [CONFIGURACION_WIFI.md](documentacion/CONFIGURACION_WIFI.md)

### **Google Maps no muestra**
1. Verificar API Key en AndroidManifest.xml
2. Habilitar "Maps SDK for Android" en Google Cloud
3. Verificar permisos de ubicación en AndroidManifest.xml

---

## 📚 Documentación Adicional

- [Configuración WiFi para Testing](documentacion/CONFIGURACION_WIFI.md)
- [Resumen Completo del Backend](documentacion/RESUMEN_BACKEND.md)
- [Backend README](backend_flask/README.md)
- [FastAPI Docs](https://fastapi.tiangolo.com/)
- [Android Developers](https://developer.android.com/)

---

## 🎓 Proyecto de Tesis

**Título**: Aplicación Móvil para Noticias Locales con Geolocalización - Ibarra, Ecuador

**Estudiante**: Richard Adrian Ortega Moncayo

**Institución**: Instituto Superior Tecnológico 17 de Julio

**Año**: 2025

**Alcance**: Ciudad de Ibarra - 12 parroquias (5 urbanas, 7 rurales)

---

## 📝 Licencia

Este proyecto fue desarrollado como trabajo de tesis educativo.

---

## 🤝 Contribuciones

Este es un proyecto de tesis individual. Sin embargo, reportes de bugs y sugerencias son bienvenidos.

---

## 📧 Contacto

**Estudiante**: Richard Adrian Ortega Moncayo

**Email**: richard.ortega778@ist17dejulio.edu.ec

**GitHub**: [@OrtegaMoncayo](https://github.com/OrtegaMoncayo)

**Repositorio**: [NoticiasIbarra](https://github.com/OrtegaMoncayo/NoticiasIbarra)

---

## ✅ Estado del Proyecto

- ✅ Frontend Android - 100% Completo
- ✅ Backend FastAPI - 100% Completo
- ✅ Base de Datos - 100% Completa
- ✅ Documentación - 100% Completa
- ✅ Testing - Funcional

**Versión**: 1.0.0

**Estado**: ✅ Listo para Presentación de Tesis

---

*Desarrollado con ❤️ en Ibarra, Ecuador*
