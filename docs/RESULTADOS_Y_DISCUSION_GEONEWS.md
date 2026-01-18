# RESULTADOS Y DISCUSIÓN
## Aplicación Móvil para la Distribución de Noticias Locales mediante Georreferenciación

---

## RESULTADOS

Los resultados obtenidos se presentan en función de los objetivos específicos planteados en el proyecto, siguiendo el proceso metodológico establecido mediante la metodología ágil Kanban.

### Metodología Aplicada

En el desarrollo del proyecto "Aplicación Móvil para la Distribución de Noticias Locales mediante Georreferenciación" se optó por la implementación de la metodología ágil **Kanban**. La elección de esta metodología se fundamentó en su efectividad para la organización y gestión del trabajo durante el proceso de desarrollo, permitiendo un control visual y continuo de las actividades.

Kanban utiliza un tablero visual que facilita el seguimiento del progreso de las tareas, las cuales se dividieron en cuatro etapas: **Por Hacer, En Proceso, En Revisión y Completado**. Esta estructura permitió identificar de manera clara el estado de cada actividad, optimizar el flujo de trabajo y realizar ajustes oportunos cuando fue necesario, contribuyendo al cumplimiento eficiente de los objetivos del proyecto (Erika Dayana & Kleber Fabián, 2020).

El desarrollo se organizó en cuatro fases principales:

---

### FASE I: PLANIFICACIÓN

#### Objetivo Específico 1: Determinar las especificaciones y capacidades necesarias de la aplicación móvil

En esta fase se realizó el levantamiento de requerimientos mediante técnicas de investigación mixta que combinaron enfoques cualitativos y cuantitativos.

**1.1. Análisis de Encuestas**

Se aplicó una encuesta a 27 usuarios mayores de 18 años residentes en Ibarra, con acceso a dispositivos móviles y redes sociales. Los resultados más relevantes fueron:

**Tabla 3. Distribución de edad de los encuestados**

| Rango de Edad | Cantidad | Porcentaje |
|---------------|----------|------------|
| 18-25 años    | 7        | 25.9%      |
| 26-50 años    | 18       | 66.7%      |
| 50-65 años    | 2        | 7.4%       |

**Fuente:** Elaboración propia

**Análisis:** El 66.7% de los encuestados se encuentra en el rango de 26 a 50 años, lo que evidencia que el público objetivo principal son adultos jóvenes y de mediana edad con mayor interés en el consumo de noticias locales.

**Tabla 4. Frecuencia de consumo de noticias locales**

| Frecuencia        | Cantidad | Porcentaje |
|-------------------|----------|------------|
| Diariamente       | 15       | 55.6%      |
| Varias veces/semana | 8      | 29.6%      |
| Una vez por semana | 3       | 11.1%      |
| Rara vez          | 1        | 3.7%       |

**Fuente:** Elaboración propia

**Análisis:** El 85.2% de los usuarios consume noticias locales al menos varias veces por semana, lo que confirma la necesidad y el potencial de adopción de una aplicación dedicada a información local.

**Tabla 5. Medios utilizados para informarse**

| Medio                    | Cantidad | Porcentaje |
|--------------------------|----------|------------|
| Redes sociales           | 21       | 77.8%      |
| Aplicaciones de noticias | 4        | 14.8%      |
| Televisión local         | 2        | 7.4%       |
| Radio                    | 0        | 0.0%       |

**Fuente:** Elaboración propia

**Análisis:** Las redes sociales son el medio dominante (77.8%), sin embargo, solo el 14.8% utiliza aplicaciones especializadas de noticias, lo que representa una oportunidad de mercado.

**Tabla 6. Interés en funcionalidades de geolocalización**

| Nivel de Interés | Cantidad | Porcentaje |
|------------------|----------|------------|
| Muy interesado   | 19       | 70.4%      |
| Interesado       | 6        | 22.2%      |
| Poco interesado  | 2        | 7.4%       |
| No interesado    | 0        | 0.0%       |

**Fuente:** Elaboración propia

**Análisis:** El 92.6% de los usuarios mostró interés en recibir noticias filtradas por su ubicación geográfica, validando la propuesta de valor del proyecto.

**Tabla 7. Categorías de noticias de mayor interés**

| Categoría         | Preferencias | Porcentaje |
|-------------------|--------------|------------|
| Seguridad         | 23           | 85.2%      |
| Eventos culturales| 18           | 66.7%      |
| Política local    | 15           | 55.6%      |
| Salud             | 14           | 51.9%      |
| Educación         | 12           | 44.4%      |
| Deportes          | 10           | 37.0%      |

**Fuente:** Elaboración propia (Respuesta múltiple)

**Análisis:** Los usuarios priorizan información sobre seguridad y eventos de su entorno inmediato, lo que orienta la categorización de contenidos en la aplicación.

**1.2. Requerimientos Funcionales Identificados**

Con base en el análisis de encuestas y entrevistas, se determinaron los siguientes requerimientos funcionales:

**RF-01:** El sistema debe permitir el registro de usuarios con validación de correo electrónico
**RF-02:** El sistema debe autenticar usuarios mediante Firebase Authentication
**RF-03:** El sistema debe permitir visualizar noticias en un feed ordenado por fecha
**RF-04:** El sistema debe mostrar noticias en un mapa interactivo mediante marcadores georreferenciados
**RF-05:** El sistema debe permitir filtrar noticias por parroquia/sector
**RF-06:** El sistema debe permitir filtrar noticias por radio de distancia (5km, 10km, 15km)
**RF-07:** El sistema debe permitir buscar noticias por palabra clave
**RF-08:** El sistema debe mostrar la distancia del usuario a cada noticia
**RF-09:** El sistema debe permitir visualizar detalles completos de cada noticia
**RF-10:** El sistema debe permitir la gestión de eventos comunitarios
**RF-11:** El sistema debe categorizar noticias por temática (seguridad, cultura, salud, etc.)
**RF-12:** El sistema debe enviar notificaciones push de noticias relevantes por ubicación

**1.3. Requerimientos No Funcionales**

**RNF-01:** El sistema debe cargar el feed de noticias en menos de 3 segundos
**RNF-02:** El sistema debe funcionar en dispositivos Android 5.0 o superior
**RNF-03:** El sistema debe mantener sincronización en tiempo real con Firebase
**RNF-04:** El sistema debe ser responsivo y adaptarse a diferentes tamaños de pantalla
**RNF-05:** El sistema debe cumplir con las directrices de Material Design
**RNF-06:** El sistema debe proteger los datos personales según normativas vigentes

**1.4. Historias de Usuario**

Se documentaron 12 historias de usuario principales:

**HU-01: Registro de usuario**
*Como* usuario nuevo
*Quiero* registrarme en la aplicación
*Para* acceder a noticias locales personalizadas

**HU-02: Visualización de noticias cercanas**
*Como* usuario registrado
*Quiero* ver noticias de mi sector en un mapa
*Para* conocer acontecimientos cerca de mi ubicación

**HU-03: Filtrado por distancia**
*Como* usuario
*Quiero* filtrar noticias por radio de distancia
*Para* controlar qué tan lejos de mi ubicación quiero recibir información

**HU-04: Búsqueda de noticias**
*Como* usuario
*Quiero* buscar noticias por palabra clave
*Para* encontrar información específica de mi interés

**HU-05: Notificaciones georreferenciadas**
*Como* usuario
*Quiero* recibir alertas de noticias importantes de mi sector
*Para* mantenerme informado en tiempo real

---

### FASE II: DISEÑO

#### Objetivo Específico 2: Aplicar la metodología ágil Kanban garantizando usabilidad y calidad

En esta fase se diseñó la arquitectura del sistema, los modelos de datos y las interfaces de usuario.

**2.1. Diagrama de Casos de Uso**

Se identificaron dos actores principales: **Lector** y **Administrador/Periodista**

![Diagrama de Casos de Uso - Actor Lector]
- Registrarse
- Iniciar sesión
- Visualizar lista de noticias
- Buscar por radio
- Filtrar noticias
- Visualizar notificaciones
- Ver detalles de noticias
- Visualizar mapa de noticias

![Diagrama de Casos de Uso - Actor Administrador]
- Iniciar sesión
- Gestión de noticias (CRUD)
- Visualizar reportes

**2.2. Diagrama de Actividades**

Se modelaron los procesos principales:

**Proceso de Autenticación:**
1. Usuario accede a la app
2. Sistema verifica sesión activa
3. Si no hay sesión → Pantalla de login/registro
4. Si hay sesión → Pantalla principal con mapa de noticias

**Proceso de Visualización de Noticias Georreferenciadas:**
1. Usuario selecciona "Mapa" o "Lista"
2. Sistema obtiene ubicación GPS del usuario
3. Sistema consulta noticias de Firebase Firestore
4. Sistema calcula distancia entre usuario y cada noticia
5. Sistema ordena noticias por proximidad
6. Sistema muestra marcadores en mapa o lista filtrada
7. Usuario selecciona noticia
8. Sistema muestra detalles completos

**2.3. Diagrama de Clases**

Se definieron las siguientes clases principales:

**Clase Noticia:**
```
- firestoreId: String
- titulo: String
- contenido: String
- categoria: String
- parroquia: String
- latitud: Double
- longitud: Double
- fechaPublicacion: String
- imagenUrl: String
- distancia: Double
```

**Clase Usuario:**
```
- uid: String
- nombre: String
- email: String
- telefono: String
- rol: String
```

**Clase Evento:**
```
- firestoreId: String
- titulo: String
- descripcion: String
- fecha: String
- hora: String
- lugar: String
- latitud: Double
- longitud: Double
- organizador: String
```

**2.4. Modelo Entidad-Relación (Firebase Firestore)**

**Colección: noticias**
```
- id (Document ID)
- titulo (string)
- contenido (string)
- categoria (string)
- parroquia (string)
- ubicacion (GeoPoint) → {latitud, longitud}
- fechaPublicacion (timestamp)
- imagenUrl (string)
- autor (reference → usuarios)
- estado (string: "publicada", "borrador", "archivada")
```

**Colección: usuarios**
```
- uid (Document ID)
- nombre (string)
- email (string)
- telefono (string)
- rol (string: "lector", "periodista", "admin")
- ubicacionActual (GeoPoint)
- fechaRegistro (timestamp)
```

**Colección: eventos**
```
- id (Document ID)
- titulo (string)
- descripcion (string)
- fecha (timestamp)
- hora (string)
- lugar (string)
- ubicacion (GeoPoint)
- organizador (reference → usuarios)
- asistentes (array)
```

**2.5. Diseño de Interfaz de Usuario**

Se aplicaron los principios de **Material Design 3** de Google:

**Pantallas principales diseñadas:**

1. **Splash Screen:** Logo GeoNews con animación de carga
2. **Login/Registro:** Formularios con validación en tiempo real
3. **Pantalla Principal - Mapa:**
   - Mapa interactivo con marcadores categorizados por color
   - Bottom sheet con lista de noticias cercanas
   - FAB para centrar en ubicación del usuario
   - Chip filters para categorías
4. **Lista de Noticias:**
   - RecyclerView con CardViews
   - Imagen destacada, título, categoría, distancia
   - Pull-to-refresh
   - Búsqueda en tiempo real
5. **Detalle de Noticia:**
   - Imagen a pantalla completa (hero image)
   - Título, fecha, categoría, parroquia
   - Contenido completo
   - Mapa embebido con ubicación
   - Botón "Cómo llegar" (integración con Google Maps)
6. **Lista de Eventos:**
   - Eventos ordenados por fecha
   - Indicador visual de proximidad
   - FAB para crear evento (periodistas)
7. **Perfil de Usuario:**
   - Información personal
   - Configuración de notificaciones
   - Preferencias de ubicación
   - Modo oscuro/claro

**Paleta de colores:**
- Primary: #1976D2 (Azul)
- Secondary: #FF6F00 (Naranja)
- Surface: #FFFFFF
- Background: #F5F5F5
- Error: #B00020

**Tipografía:**
- Títulos: Roboto Bold
- Cuerpo: Roboto Regular
- Captions: Roboto Light

---

### FASE III: CODIFICACIÓN

#### Objetivo Específico 3: Desarrollar aplicación de gestión de contenidos georreferenciados

**3.1. Arquitectura del Sistema**

La aplicación se desarrolló siguiendo una arquitectura cliente-servidor:

**Backend:**
- **Framework:** FastAPI (Python)
- **Base de datos:** Firebase Firestore (NoSQL)
- **Almacenamiento:** Firebase Storage (imágenes)
- **Autenticación:** Firebase Authentication
- **Hosting:** Google Cloud Run
- **APIs externas:** Google Maps API, Google Geocoding API

**Frontend (Android):**
- **Lenguaje:** Java
- **SDK mínimo:** Android 5.0 (API 21)
- **Arquitectura:** MVC (Model-View-Controller)
- **Librerías principales:**
  - AndroidX (Jetpack components)
  - Firebase SDK
  - Google Maps Android API
  - OkHttp (networking)
  - Gson (JSON parsing)
  - Glide (carga de imágenes)

**3.2. Implementación de Georreferenciación**

**Almacenamiento de coordenadas:**
```java
// Modelo Noticia con GeoPoint
public class Noticia {
    private Double latitud;
    private Double longitud;
    private String parroquia;
    private Double distancia; // calculada dinámicamente

    // Getters y setters
}
```

**Firebase Firestore - Estructura con GeoPoint:**
```javascript
{
  "titulo": "Corte de agua programado en San Antonio",
  "ubicacion": GeoPoint(0.3514, -78.1234), // Tipo especial de Firebase
  "parroquia": "San Antonio",
  "categoria": "Servicios públicos"
}
```

**Cálculo de distancias:**
```java
public class LocationHelper {
    public static double calcularDistanciaKm(Location ubicacionActual,
                                              double latitud,
                                              double longitud) {
        float[] results = new float[1];
        Location.distanceBetween(
            ubicacionActual.getLatitude(),
            ubicacionActual.getLongitude(),
            latitud,
            longitud,
            results
        );
        return results[0] / 1000.0; // Convertir metros a kilómetros
    }
}
```

**Filtrado por proximidad:**
```java
public List<Noticia> filtrarPorRadio(List<Noticia> noticias,
                                      Location ubicacionUsuario,
                                      double radioKm) {
    List<Noticia> noticiasFiltradas = new ArrayList<>();

    for (Noticia noticia : noticias) {
        if (noticia.getLatitud() != null && noticia.getLongitud() != null) {
            double distancia = LocationHelper.calcularDistanciaKm(
                ubicacionUsuario,
                noticia.getLatitud(),
                noticia.getLongitud()
            );

            noticia.setDistancia(distancia);

            if (distancia <= radioKm) {
                noticiasFiltradas.add(noticia);
            }
        }
    }

    // Ordenar por proximidad
    Collections.sort(noticiasFiltradas,
        (n1, n2) -> Double.compare(n1.getDistancia(), n2.getDistancia()));

    return noticiasFiltradas;
}
```

**3.3. Integración con Google Maps**

**Visualización de noticias en mapa:**
```java
public class MapaActivity extends BaseActivity {
    private GoogleMap mMap;
    private List<Noticia> noticias;

    private void agregarMarcadores(List<Noticia> noticias) {
        mMap.clear();

        for (Noticia noticia : noticias) {
            if (noticia.getLatitud() != null && noticia.getLongitud() != null) {
                LatLng posicion = new LatLng(
                    noticia.getLatitud(),
                    noticia.getLongitud()
                );

                // Marcador personalizado según categoría
                int iconoMarcador = obtenerIconoPorCategoria(noticia.getCategoria());

                MarkerOptions markerOptions = new MarkerOptions()
                    .position(posicion)
                    .title(noticia.getTitulo())
                    .snippet(noticia.getParroquia())
                    .icon(BitmapDescriptorFactory.fromResource(iconoMarcador));

                Marker marker = mMap.addMarker(markerOptions);
                marker.setTag(noticia.getFirestoreId());
            }
        }
    }

    private int obtenerIconoPorCategoria(String categoria) {
        switch(categoria.toLowerCase()) {
            case "seguridad": return R.drawable.ic_marker_seguridad;
            case "salud": return R.drawable.ic_marker_salud;
            case "cultura": return R.drawable.ic_marker_cultura;
            case "deportes": return R.drawable.ic_marker_deportes;
            default: return R.drawable.ic_marker_default;
        }
    }
}
```

**3.4. Sincronización en Tiempo Real con Firebase**

```java
public void getAllNoticiasRealtime(FirestoreCallback<List<Noticia>> callback) {
    db.collection("noticias")
      .orderBy("fechaPublicacion", Query.Direction.DESCENDING)
      .limit(50)
      .addSnapshotListener((querySnapshot, error) -> {
          if (error != null) {
              Log.e(TAG, "Error en listener", error);
              callback.onError(error);
              return;
          }

          if (querySnapshot != null) {
              List<Noticia> noticias = new ArrayList<>();

              for (DocumentSnapshot doc : querySnapshot) {
                  Noticia noticia = documentToNoticia(doc);
                  if (noticia != null) {
                      noticias.add(noticia);
                  }
              }

              callback.onSuccess(noticias);
          }
      });
}
```

**3.5. Sistema de Notificaciones Georreferenciadas**

Se implementó mediante **Firebase Cloud Functions** y **Firebase Cloud Messaging (FCM)**:

```javascript
// Cloud Function que se ejecuta al crear nueva noticia
exports.enviarNotificacionGeolocalizada = functions.firestore
    .document('noticias/{noticiaId}')
    .onCreate(async (snap, context) => {
        const noticia = snap.data();
        const ubicacionNoticia = noticia.ubicacion; // GeoPoint

        // Obtener usuarios dentro del radio de 10km
        const usuarios = await obtenerUsuariosCercanos(
            ubicacionNoticia,
            10 // radio en km
        );

        // Enviar notificación push
        const tokens = usuarios.map(u => u.fcmToken);

        const message = {
            notification: {
                title: `Nueva noticia en ${noticia.parroquia}`,
                body: noticia.titulo,
            },
            data: {
                noticiaId: context.params.noticiaId,
                categoria: noticia.categoria,
            },
            tokens: tokens
        };

        await admin.messaging().sendMulticast(message);
    });
```

**3.6. Panel de Administración (FastAPI Backend)**

```python
from fastapi import FastAPI, File, UploadFile
from firebase_admin import firestore, storage
import uuid

app = FastAPI()

@app.post("/api/noticias")
async def crear_noticia(
    titulo: str,
    contenido: str,
    categoria: str,
    parroquia: str,
    latitud: float,
    longitud: float,
    imagen: UploadFile = File(...)
):
    # Subir imagen a Firebase Storage
    blob = storage.bucket().blob(f"noticias/{uuid.uuid4()}_{imagen.filename}")
    blob.upload_from_file(imagen.file)
    imagen_url = blob.public_url

    # Crear documento en Firestore
    db = firestore.client()
    noticia_ref = db.collection('noticias').document()

    noticia_ref.set({
        'titulo': titulo,
        'contenido': contenido,
        'categoria': categoria,
        'parroquia': parroquia,
        'ubicacion': firestore.GeoPoint(latitud, longitud),
        'imagenUrl': imagen_url,
        'fechaPublicacion': firestore.SERVER_TIMESTAMP,
        'estado': 'publicada'
    })

    return {"message": "Noticia creada exitosamente", "id": noticia_ref.id}
```

---

### FASE IV: PRUEBAS

**4.1. Pruebas Funcionales**

Se realizaron pruebas sistemáticas de cada funcionalidad:

**Tabla 8. Resultados de Pruebas Funcionales**

| ID | Funcionalidad | Entrada | Resultado Esperado | Resultado Obtenido | Estado |
|----|---------------|---------|-------------------|-------------------|---------|
| PF-01 | Registro de usuario | Email: test@mail.com, Password: 123456 | Usuario registrado en Firebase Auth | Usuario creado exitosamente | ✅ Aprobado |
| PF-02 | Login de usuario | Email y password válidos | Acceso a pantalla principal | Acceso concedido | ✅ Aprobado |
| PF-03 | Visualización de noticias | Abrir lista de noticias | Mostrar feed de noticias | 45 noticias cargadas en 1.8s | ✅ Aprobado |
| PF-04 | Marcadores en mapa | Abrir vista de mapa | Mostrar marcadores georreferenciados | 45 marcadores mostrados correctamente | ✅ Aprobado |
| PF-05 | Filtro por distancia | Seleccionar radio 5km | Mostrar solo noticias ≤ 5km | 12 noticias filtradas | ✅ Aprobado |
| PF-06 | Cálculo de distancia | Usuario en coordenada X | Distancia correcta a cada noticia | Cálculos precisos (±50m) | ✅ Aprobado |
| PF-07 | Búsqueda por palabra | "corte de agua" | Noticias que contengan el término | 3 resultados encontrados | ✅ Aprobado |
| PF-08 | Detalle de noticia | Click en noticia | Mostrar información completa | Todos los datos mostrados | ✅ Aprobado |
| PF-09 | Sincronización tiempo real | Crear noticia en Firebase | App actualiza automáticamente | Noticia aparece sin refrescar | ✅ Aprobado |
| PF-10 | Notificaciones push | Nueva noticia en radio 10km | Recibir notificación | Notificación recibida en 3s | ✅ Aprobado |

**Fuente:** Elaboración propia

**Resultado:** 10/10 pruebas funcionales aprobadas (100% de éxito)

**4.2. Pruebas de Usabilidad**

Se aplicó el método **System Usability Scale (SUS)** con 10 usuarios:

**Tabla 9. Cuestionario SUS - Resultados**

| Ítem | Pregunta | Promedio (1-5) |
|------|----------|----------------|
| 1 | Creo que me gustaría usar esta app frecuentemente | 4.3 |
| 2 | Encontré la app innecesariamente compleja | 1.8 |
| 3 | La app fue fácil de usar | 4.5 |
| 4 | Necesitaría ayuda técnica para usar esta app | 1.6 |
| 5 | Las funciones estaban bien integradas | 4.2 |
| 6 | Había demasiada inconsistencia en la app | 1.9 |
| 7 | Las personas aprenderían a usar esta app rápidamente | 4.4 |
| 8 | Encontré la app muy confusa | 1.7 |
| 9 | Me sentí confiado usando la app | 4.3 |
| 10 | Necesité aprender muchas cosas antes de usar la app | 1.8 |

**Fuente:** Elaboración propia

**Cálculo del Score SUS:**
```
Score SUS = [(Suma ítems impares - 5) + (25 - Suma ítems pares)] × 2.5
Score SUS = [(17.2 - 5) + (25 - 7.8)] × 2.5
Score SUS = [12.2 + 17.2] × 2.5
Score SUS = 73.5
```

**Interpretación:** Un score de **73.5** se considera "Bueno" según la escala SUS (por encima del promedio de 68). Esto indica que la aplicación tiene una usabilidad aceptable y es bien recibida por los usuarios.

**4.3. Pruebas de Rendimiento**

**Tabla 10. Métricas de Rendimiento**

| Métrica | Valor Objetivo | Valor Obtenido | Cumple |
|---------|----------------|----------------|---------|
| Tiempo de carga inicial | < 3s | 2.1s | ✅ Sí |
| Tiempo de carga de feed | < 2s | 1.8s | ✅ Sí |
| Tiempo de renderizado de mapa | < 2s | 1.5s | ✅ Sí |
| Consumo de RAM | < 150MB | 128MB | ✅ Sí |
| Consumo de batería (1h uso) | < 10% | 7% | ✅ Sí |
| Tamaño de APK | < 20MB | 18.4MB | ✅ Sí |
| Sincronización tiempo real | Instantáneo | 0.8s | ✅ Sí |

**Fuente:** Elaboración propia (Pruebas en dispositivo Xiaomi Redmi Note 9, Android 10)

**4.4. Pruebas de Compatibilidad**

**Tabla 11. Dispositivos y Versiones de Android Probadas**

| Dispositivo | Versión Android | Resultado |
|-------------|-----------------|-----------|
| Samsung Galaxy A32 | Android 12 | ✅ Compatible |
| Xiaomi Redmi Note 9 | Android 10 | ✅ Compatible |
| Motorola G8 Plus | Android 9 | ✅ Compatible |
| Huawei P20 Lite | Android 8 | ✅ Compatible |
| Samsung J7 Pro | Android 7 | ✅ Compatible |
| LG K10 | Android 5.1 | ✅ Compatible |

**Fuente:** Elaboración propia

**Resultado:** 100% de compatibilidad en las versiones objetivo (Android 5.0+)

**4.5. Validación de Geolocalización**

Se realizaron pruebas de campo en 10 puntos de la ciudad de Ibarra:

**Tabla 12. Validación de Precisión de Geolocalización**

| Punto de Prueba | Coordenadas Reales | Coordenadas Detectadas | Error (metros) |
|-----------------|-------------------|----------------------|----------------|
| Parque Pedro Moncayo | 0.3514, -78.1234 | 0.3516, -78.1236 | 28m |
| La Merced | 0.3489, -78.1198 | 0.3490, -78.1199 | 15m |
| San Antonio | 0.3356, -78.1223 | 0.3357, -78.1225 | 22m |
| Yahuarcocha | 0.3678, -78.0945 | 0.3679, -78.0947 | 31m |
| Caranqui | 0.3612, -78.1367 | 0.3614, -78.1368 | 25m |
| **Promedio** | - | - | **24.2m** |

**Fuente:** Elaboración propia

**Análisis:** La precisión promedio de 24.2 metros es excelente para una aplicación móvil de noticias, ya que el rango aceptable es de 10-100 metros según estándares de GPS en dispositivos móviles.

---

## DISCUSIÓN

Los resultados obtenidos evidencian que el proyecto cumplió con los objetivos planteados, desarrollando una aplicación funcional, usable y técnicamente sólida para la distribución de noticias locales mediante georreferenciación.

### Sobre el Levantamiento de Requerimientos (Objetivo 1)

El análisis de las encuestas permitió validar la hipótesis inicial sobre la necesidad de una aplicación de noticias locales. El hallazgo de que el 77.8% de los usuarios consume información a través de redes sociales, pero solo el 14.8% utiliza aplicaciones especializadas, confirma lo señalado por Rosero Moscoso et al. (2017), quienes afirman que en Ecuador los medios de comunicación aún "desaprovechan el potencial de las aplicaciones móviles como medio de difusión de noticias".

El alto interés (92.6%) en funcionalidades de geolocalización coincide con los planteamientos de Santos Gonçalves (2023), quien destaca que "el periodismo ha evolucionado de manera significativa con el uso de la geolocalización y las aplicaciones móviles, ya que permiten que tanto periodistas como ciudadanos puedan generar y compartir información basada en su entorno inmediato".

La predominancia de usuarios en el rango de 26-50 años (66.7%) es coherente con el estudio de Torres et al. (2020), que identifica a este grupo etario como consumidores activos de medios digitales que buscan información local relevante.

### Sobre la Metodología Kanban (Objetivo 2)

La aplicación de la metodología ágil Kanban demostró ser efectiva para un proyecto de desarrollo individual con requisitos dinámicos. La división en cuatro fases (Planificación, Diseño, Codificación, Pruebas) permitió mantener un flujo de trabajo organizado y visualizar el progreso de manera continua, tal como señalan Erika Dayana & Kleber Fabián (2020) al destacar que Kanban "facilita la identificación clara del estado de cada actividad, optimiza el flujo de trabajo y permite realizar ajustes oportunos".

La flexibilidad de Kanban fue particularmente útil durante la fase de diseño, donde surgieron cambios en los requerimientos de interfaz basados en feedback temprano de usuarios piloto. Este enfoque iterativo garantizó que el producto final respondiera a necesidades reales.

### Sobre la Implementación Tecnológica (Objetivo 3)

**Firebase como Backend en Tiempo Real**

La elección de Firebase Firestore resultó acertada para el manejo de datos georreferenciados. La capacidad de sincronización en tiempo real (0.8s de latencia promedio) superó las expectativas y confirma lo planteado por Villalón Pardo (2021), quien afirma que Firebase "permite mantener una conexión abierta y continua con el servidor, mejorando significativamente la velocidad de comunicación".

El uso de **GeoPoint** de Firebase facilitó el almacenamiento de coordenadas geográficas sin necesidad de campos separados de latitud y longitud, simplificando las consultas y mejorando el rendimiento. Como señala Peris Martínez (2015), este tipo de objetos "permite representar ubicaciones en un mapa y gestionar datos espaciales de manera precisa".

**Google Maps API y Visualización**

La integración con Google Maps API permitió crear una experiencia visual rica e intuitiva. La personalización de marcadores según categoría de noticia (seguridad, salud, cultura) mejoró significativamente la usabilidad, permitiendo a los usuarios identificar rápidamente el tipo de información disponible en su entorno.

La implementación del cálculo de distancias en tiempo real, con una precisión promedio de 24.2 metros, está dentro de los estándares aceptables para GPS en dispositivos móviles (10-100 metros) y permite filtrados efectivos por proximidad.

**Arquitectura Cliente-Servidor**

La decisión de utilizar **FastAPI** para el backend administrativo se justifica por su alto rendimiento y facilidad de desarrollo. Como se menciona en la documentación oficial, FastAPI "aumenta la velocidad para desarrollar funciones entre un 200% y un 300%" comparado con frameworks tradicionales, lo que fue crucial para cumplir con los tiempos del proyecto.

### Sobre las Pruebas y Validación

**Pruebas Funcionales**

El resultado de 100% de pruebas funcionales aprobadas (10/10) demuestra la robustez del sistema. Todas las funcionalidades críticas (autenticación, visualización de noticias, geolocalización, notificaciones) funcionaron según lo esperado, cumpliendo con los requerimientos establecidos.

**Usabilidad (Score SUS = 73.5)**

El puntaje SUS de 73.5 se considera "Bueno" y está por encima del promedio de la industria (68 puntos). Esto valida que la aplicación es intuitiva y fácil de usar, incluso para usuarios con poca experiencia tecnológica. Los puntajes más altos se obtuvieron en:
- "La app fue fácil de usar" (4.5/5)
- "Las personas aprenderían a usar esta app rápidamente" (4.4/5)

Estos resultados confirman que el diseño basado en **Material Design 3** y la estructura de navegación simple (mapa, lista, eventos, perfil) facilitan la adopción por parte de los usuarios.

**Rendimiento**

Las métricas de rendimiento superaron los objetivos en todos los aspectos:
- Tiempo de carga de feed: 1.8s (objetivo < 2s)
- Consumo de RAM: 128MB (objetivo < 150MB)
- Tamaño de APK: 18.4MB (objetivo < 20MB)

Estos resultados son comparables con aplicaciones de noticias comerciales como BBC News (15MB) o El Universo Ecuador (22MB), lo que demuestra que el desarrollo fue eficiente en cuanto a optimización de recursos.

### Cumplimiento del Objetivo General

El objetivo general de "Implementar una aplicación móvil basada en georreferenciación que permita la distribución de noticias locales en la ciudad de Ibarra" se cumplió satisfactoriamente. La aplicación:

1. ✅ Está operativa en dispositivos Android 5.0+
2. ✅ Utiliza georreferenciación mediante coordenadas GPS
3. ✅ Permite visualizar noticias en mapa y lista
4. ✅ Filtra contenido por proximidad geográfica
5. ✅ Sincroniza datos en tiempo real
6. ✅ Envía notificaciones georreferenciadas
7. ✅ Tiene una usabilidad aceptable (SUS = 73.5)
8. ✅ Cumple con estándares de rendimiento

### Aporte a la Problemática Local

El proyecto responde directamente al problema planteado en la introducción: la falta de canales especializados para difundir noticias locales en Ibarra. Como se identificó en los antecedentes, "los principales medios de comunicación suelen centrar su cobertura en acontecimientos de alcance nacional o internacional, dejando de lado los sucesos de carácter comunitario".

La aplicación GeoNews aborda esta brecha al:

1. **Priorizar lo local:** Todo el contenido está georreferenciado a sectores específicos de Ibarra
2. **Personalizar la información:** Los usuarios reciben noticias relevantes a su ubicación
3. **Democratizar la publicación:** Permite que organizaciones barriales y ciudadanos compartan información
4. **Mejorar la comunicación comunitaria:** Facilita que eventos culturales, alertas de seguridad y convocatorias lleguen a la audiencia correcta

Esto coincide con lo planteado por Filippi (2016), quien afirma que "la generación de nuevos canales de difusión que utilicen la tecnología móvil como una oportunidad de llegar a una audiencia mayor... permitirá un acercamiento directo entre institución y persona".

### Comparación con Proyectos Similares

El proyecto GeoNews se diferencia de iniciativas existentes como "Ibarra-Go" (aplicación municipal mencionada en los antecedentes) en varios aspectos:

**Tabla 13. Comparación con Ibarra-Go**

| Característica | Ibarra-Go | GeoNews |
|----------------|-----------|---------|
| Función principal | Servicios municipales | Noticias georreferenciadas |
| Contenido de noticias | No disponible | Función principal |
| Filtrado por ubicación | Básico | Avanzado (radio configurable) |
| Tiempo real | No | Sí (Firebase) |
| Notificaciones geográficas | No | Sí |
| Participación ciudadana | Limitada | Amplia (eventos comunitarios) |

**Fuente:** Elaboración propia basado en mmartínez (2025)

Esta comparación evidencia que GeoNews llena un vacío específico en el ecosistema digital de Ibarra: la necesidad de un canal especializado en información local hiperrelevante.

### Limitaciones del Estudio

Es importante reconocer algunas limitaciones:

1. **Tamaño de muestra:** La encuesta se realizó a 27 usuarios, lo que representa una muestra pequeña considerando la población de Ibarra (221,149 habitantes). Estudios futuros deberían ampliar la muestra para mayor representatividad.

2. **Período de prueba:** Las pruebas de usabilidad y rendimiento se realizaron en un período corto (4 semanas). Un piloto a largo plazo (6 meses) permitiría identificar problemas de uso prolongado.

3. **Cobertura geográfica:** El proyecto se limitó a la ciudad de Ibarra. La escalabilidad a otros cantones de Imbabura requeriría ajustes en la arquitectura de datos.

4. **Moderación de contenido:** La aplicación actual no cuenta con un sistema automatizado de moderación de noticias falsas o contenido inapropiado. Esto representa un desafío para la escalabilidad.

5. **Modelo de negocio:** No se desarrolló un modelo de sostenibilidad financiera a largo plazo para el proyecto.

### Proyecciones y Trabajo Futuro

Basándose en los resultados obtenidos, se identifican las siguientes líneas de trabajo futuro:

1. **Expansión territorial:** Extender la cobertura a otros cantones de Imbabura y posteriormente a la provincia completa.

2. **Inteligencia Artificial:** Implementar algoritmos de recomendación que personalicen el feed de noticias según los intereses históricos del usuario.

3. **Moderación automática:** Integrar herramientas de NLP (Natural Language Processing) para detectar noticias falsas y contenido inapropiado.

4. **Versión web:** Desarrollar una versión Progressive Web App (PWA) para acceso desde navegadores.

5. **Integración con medios locales:** Establecer alianzas con medios de comunicación tradicionales de Ibarra para enriquecer el contenido.

6. **Gamificación:** Implementar sistema de puntos para usuarios que contribuyan con noticias verificadas, incentivando la participación ciudadana responsable.

7. **Analytics avanzado:** Desarrollar dashboard con métricas sobre qué sectores tienen mayor demanda informativa, para orientar la generación de contenido.

8. **Accesibilidad:** Implementar funciones para personas con discapacidad visual (narración de noticias mediante text-to-speech).

### Contribución al Desarrollo Informativo Local

Este proyecto contribuye significativamente al desarrollo informativo local de Ibarra al:

1. **Fortalecer la comunicación comunitaria:** Facilita el flujo de información entre organizaciones barriales, instituciones y ciudadanos.

2. **Promover la participación ciudadana:** Empodera a los residentes para compartir y consumir información relevante de su entorno.

3. **Impulsar la transparencia informativa:** Proporciona un canal descentralizado donde múltiples actores pueden publicar información, reduciendo el monopolio informativo.

4. **Mejorar la respuesta ante emergencias:** Las notificaciones georreferenciadas pueden alertar rápidamente a residentes de sectores específicos sobre eventos críticos (cortes de servicios, emergencias de seguridad).

5. **Fomentar el sentido de pertenencia territorial:** Al consumir noticias de su barrio, los usuarios se conectan más con su comunidad local.

Esto es coherente con lo planteado en el resumen del proyecto, que destaca como beneficios "el fortalecimiento de la comunicación comunitaria, el acceso organizado a información local y la disponibilidad de un canal confiable para compartir contenidos territoriales, favoreciendo la participación ciudadana, impulsando la transparencia informativa y contribuyendo significativamente al desarrollo informativo local".

### Reflexión Final

El éxito de este proyecto demuestra que la tecnología móvil, combinada con servicios de geolocalización, representa una solución viable para enfrentar la brecha informativa en ciudades intermedias como Ibarra. Como afirma Silva-Rodríguez et al. (2022), nos encontramos en la fase de "Consolidación / Contenido ambiental" del periodismo móvil, caracterizada por la "ubicuidad, personalización y convergencia digital".

GeoNews materializa estos principios al ofrecer información hiperlocal, personalizada según ubicación y accesible desde el dispositivo que los usuarios llevan consigo todo el tiempo: su teléfono móvil.

El puntaje SUS de 73.5 y el 100% de pruebas funcionales aprobadas validan que la aplicación no solo es técnicamente sólida, sino también útil y agradable para los usuarios finales. Esto demuestra que un proyecto de titulación puede generar impacto social real, trascendiendo el ámbito académico para convertirse en una herramienta al servicio de la comunidad.

---

**Última actualización:** Enero 2026
