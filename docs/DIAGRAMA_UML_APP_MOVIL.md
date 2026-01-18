# Diagrama de Clases UML - Aplicación Móvil GeoNews Android

## Descripción
Diagrama de clases de la **aplicación móvil Android** del sistema GeoNews (sin módulo de Eventos).
Este diagrama muestra únicamente las clases del lado cliente (Android).

---

## Diagrama UML - Aplicación Android

```plantuml
@startuml GeoNews_App_Movil_Android

!define ENTITY_COLOR #E3F2FD
!define ACTIVITY_COLOR #FFF3E0
!define MANAGER_COLOR #E8F5E9
!define UTIL_COLOR #F3E5F5
!define ADAPTER_COLOR #FCE4EC

title Diagrama de Clases - GeoNews App Móvil Android

' ============================================
' PAQUETE: MODELO (Entidades de Datos)
' ============================================

package "com.tesistitulacion.noticiaslocales.modelo" <<Rectangle>> {

    class Usuario <<Entity>> ENTITY_COLOR {
        - Integer id
        - String nombre
        - String apellido
        - String email
        - String password
        - String fotoPerfil
        - String bio
        - String telefonocelular
        - String ubicacion
        - Long fechaRegistro
        - Long ultimaConexion
        - Integer noticiasPublicadas
        - Integer noticiasLeidas
        - Boolean verificado
        - String tipoUsuario
        - String createdAt
        __Constructores__
        + Usuario()
        + Usuario(nombre, apellido, email, password)
        __Métodos de Negocio__
        + String getNombreCompleto()
        + boolean esAdmin()
        + boolean esReportero()
        __Getters y Setters__
        + getId() : Integer
        + setId(Integer) : void
        + getNombre() : String
        + setNombre(String) : void
        ... [otros getters/setters]
    }

    class Noticia <<Entity>> ENTITY_COLOR {
        - Integer id
        - String firestoreId
        - String titulo
        - String descripcion
        - String contenido
        - String imagenUrl
        - Integer autorId
        - String autorNombre
        - Integer categoriaId
        - String categoriaNombre
        - Long fechaCreacion
        - Long fechaActualizacion
        - Integer visualizaciones
        - Double latitud
        - Double longitud
        - String ubicacion
        - Boolean destacada
        - String estado
        - Double distancia
        - String citaDestacada
        - String hashtags
        - String impactoComunitario
        __Constructores__
        + Noticia()
        + Noticia(titulo, descripcion, contenido, categoriaId)
        __Métodos de Negocio__
        + String getColorCategoria()
        + String getFechaPublicacion()
        + String getParroquiaNombre()
        __Getters y Setters__
        + getId() : Integer
        + getTitulo() : String
        ... [otros getters/setters]
    }

    class Categoria <<Entity>> ENTITY_COLOR {
        - Integer id
        - String nombre
        - String descripcion
        - String icono
        - String color
        __Constructores__
        + Categoria()
        + Categoria(nombre)
        __Getters y Setters__
        + getId() : Integer
        + getNombre() : String
        + getColor() : String
        ... [otros getters/setters]
    }

    class Parroquia <<Entity>> ENTITY_COLOR {
        - Integer id
        - String nombre
        - Double latitud
        - Double longitud
        - String tipo
        - String descripcion
        __Constructores__
        + Parroquia()
        + Parroquia(nombre, latitud, longitud)
        __Getters y Setters__
        + getId() : Integer
        + getNombre() : String
        + getLatitud() : Double
        + getLongitud() : Double
        ... [otros getters/setters]
    }
}

' ============================================
' PAQUETE: FIREBASE (Gestión de Backend)
' ============================================

package "com.tesistitulacion.noticiaslocales.firebase" <<Rectangle>> {

    class FirebaseManager <<Singleton>> MANAGER_COLOR {
        - {static} FirebaseManager instance
        - FirebaseFirestore db
        - FirebaseAuth auth
        - FirebaseStorage storage
        - {static} String COLLECTION_USUARIOS
        - {static} String COLLECTION_NOTICIAS
        - {static} String COLLECTION_CATEGORIAS
        - {static} String COLLECTION_PARROQUIAS
        - {static} String COLLECTION_NOTIFICACIONES
        __Constructor Privado__
        - FirebaseManager()
        __Singleton__
        + {static} FirebaseManager getInstance() : FirebaseManager
        __Autenticación__
        + void registrarUsuario(Usuario, callback)
        + void loginUsuario(email, password, callback)
        + void cerrarSesion()
        + String obtenerUsuarioActualId()
        __Gestión de Usuarios__
        + void obtenerUsuarioPorId(String, callback)
        + void actualizarUsuario(String, Map, callback)
        + void actualizarFotoPerfil(String, String, callback)
        __Gestión de Noticias__
        + void obtenerNoticias(callback)
        + void obtenerNoticiasCercanas(lat, lon, radio, callback)
        + void obtenerNoticiaPorId(String, callback)
        + void incrementarVisualizaciones(String)
        + void obtenerNoticiasDestacadas(callback)
        + void obtenerNoticiasPorCategoria(categoriaId, callback)
        __Gestión de Categorías__
        + void obtenerCategorias(callback)
        __Gestión de Parroquias__
        + void obtenerParroquias(callback)
        __Storage__
        + void subirImagen(Uri, String, callback)
        + void eliminarImagen(String, callback)
        __Métodos Auxiliares__
        - Usuario documentToUsuario(DocumentSnapshot)
        - Noticia documentToNoticia(DocumentSnapshot)
        - Map usuarioToMap(Usuario)
        - Map noticiaToMap(Noticia)
    }

    interface FirestoreCallback<T> <<Interface>> MANAGER_COLOR {
        + void onSuccess(T result)
        + void onError(Exception e)
    }
}

' ============================================
' PAQUETE: DB (Servicios HTTP - Opcional)
' ============================================

package "com.tesistitulacion.noticiaslocales.db" <<Rectangle>> {

    class ApiConfig <<Singleton>> MANAGER_COLOR {
        + {static} String BASE_URL
        + {static} String ENVIRONMENT
        - {static} Retrofit retrofit
        __Singleton__
        + {static} Retrofit getRetrofitInstance() : Retrofit
        __Configuración__
        + {static} void setProduction()
        + {static} void setDevelopment()
        + {static} void setBaseUrl(String)
    }

    interface NoticiaServiceHTTP <<Interface>> MANAGER_COLOR {
        + Call<List<Noticia>> obtenerNoticias()
        + Call<List<Noticia>> obtenerNoticiasCercanas(lat, lon, radio)
        + Call<Noticia> obtenerNoticiaPorId(id)
        + Call<List<Noticia>> obtenerNoticiasPorCategoria(categoriaId)
    }
}

' ============================================
' PAQUETE: UTILS (Utilidades)
' ============================================

package "com.tesistitulacion.noticiaslocales.utils" <<Rectangle>> {

    class UsuarioPreferences <<Singleton>> UTIL_COLOR {
        - {static} SharedPreferences sharedPreferences
        - {static} SharedPreferences.Editor editor
        - {static} String PREFS_NAME
        __Inicialización__
        + {static} void init(Context)
        __Sesión__
        + {static} void guardarDatosUsuario(userId, nombre, apellido, email)
        + {static} String getUserId(Context)
        + {static} String getEmail(Context)
        + {static} String getNombre(Context)
        + {static} String getApellido(Context)
        + {static} void cerrarSesion(Context)
        __Perfil__
        + {static} void guardarFotoPerfil(Context, url)
        + {static} String getFotoPerfil(Context)
        + {static} void guardarBio(Context, bio)
        + {static} String getBio(Context)
        + {static} void guardarUbicacion(Context, ubicacion)
        + {static} String getUbicacion(Context)
        __Preferencias__
        + {static} void guardarNotificacionesActivas(Context, activo)
        + {static} boolean getNotificacionesActivas(Context)
        + {static} void guardarModoOscuro(Context, activo)
        + {static} boolean getModoOscuro(Context)
        __Noticias Guardadas__
        + {static} void agregarNoticiaGuardada(Context, noticiaId)
        + {static} void eliminarNoticiaGuardada(Context, noticiaId)
        + {static} List<String> getNoticiasGuardadas(Context)
        + {static} boolean esNoticiaGuardada(Context, noticiaId)
        __Intereses__
        + {static} void agregarInteres(Context, categoria)
        + {static} void eliminarInteres(Context, categoria)
        + {static} List<String> getIntereses(Context)
    }

    class ThemeManager UTIL_COLOR {
        - {static} String PREFS_THEME
        - {static} String KEY_DARK_MODE
        __Tema__
        + {static} void applyTheme(Context)
        + {static} boolean isDarkMode(Context)
        + {static} void setDarkMode(Context, boolean)
        + {static} void toggleDarkMode(Context)
    }

    class DialogHelper UTIL_COLOR {
        __Diálogos de Información__
        + {static} void showInfo(Context, title, message)
        + {static} void showError(Context, title, message)
        + {static} void showSuccess(Context, message)
        __Diálogos de Confirmación__
        + {static} void showConfirmationDialog(Context, title, message, positive, negative, callback)
        + {static} void showDeleteConfirmation(Context, message, callback)
        __Diálogos de Lista__
        + {static} void showListDialog(Context, title, items, listener)
        + {static} void showSingleChoiceDialog(Context, title, items, selected, listener)
        __Diálogos de Entrada__
        + {static} void showEditDialog(Context, title, hint, currentValue, callback)
        + {static} void showPasswordDialog(Context, callback)
    }

    class UbicacionUtils UTIL_COLOR {
        - {static} int EARTH_RADIUS_KM
        __Cálculos de Distancia__
        + {static} double calcularDistancia(lat1, lon1, lat2, lon2) : double
        + {static} String formatearDistancia(distanciaKm) : String
        __Geocodificación__
        + {static} String obtenerNombreCiudad(Context, lat, lon) : String
        + {static} String obtenerDireccion(Context, lat, lon) : String
        __Permisos__
        + {static} void solicitarPermisoUbicacion(Activity)
        + {static} boolean tienePermisoUbicacion(Context) : boolean
        __Ubicación Actual__
        + {static} void obtenerUbicacionActual(Context, callback)
    }

    interface UbicacionCallback <<Interface>> UTIL_COLOR {
        + void onUbicacionObtenida(double lat, double lon)
        + void onError(String mensaje)
    }
}

' ============================================
' PAQUETE: ACTIVITIES (Pantallas)
' ============================================

package "com.tesistitulacion.noticiaslocales.activities" <<Rectangle>> {

    abstract class BaseActivity extends AppCompatActivity ACTIVITY_COLOR {
        # {static} int NAV_NOTICIAS = 1
        # {static} int NAV_MAPA = 3
        # {static} int NAV_PERFIL = 4
        # BottomNavigationView bottomNav
        # LinearLayout llNavNoticias
        # LinearLayout llNavMapa
        # LinearLayout llNavPerfil
        # ImageView ivNavNoticias, ivNavMapa, ivNavPerfil
        # TextView tvNavNoticias, tvNavMapa, tvNavPerfil
        # View indicatorNoticias, indicatorMapa, indicatorPerfil
        __Métodos Abstractos__
        # {abstract} int getNavegacionActiva() : int
        # {abstract} int getLayoutResourceId() : int
        __Ciclo de Vida__
        # void onCreate(Bundle)
        # void onResume()
        __Navegación__
        # void setupBottomNavigation()
        # void actualizarNavegacionActiva(int)
        # void navegarA(Class, int)
        __UI Helpers__
        # void showToast(String)
        # void showLoading()
        # void hideLoading()
    }

    class SplashActivity extends AppCompatActivity ACTIVITY_COLOR {
        - FirebaseAuth firebaseAuth
        - {static} int SPLASH_DURATION = 2000
        __Ciclo de Vida__
        # void onCreate(Bundle)
        __Lógica__
        - void verificarSesion()
        - void irALogin()
        - void irAPrincipal()
    }

    class LoginActivity extends AppCompatActivity ACTIVITY_COLOR {
        - TextInputEditText etEmail
        - TextInputEditText etPassword
        - MaterialButton btnLogin
        - TextView btnRegistro
        - ProgressBar progressBar
        - FirebaseAuth firebaseAuth
        - FirebaseManager firebaseManager
        __Ciclo de Vida__
        # void onCreate(Bundle)
        __UI__
        - void inicializarVistas()
        - void configurarListeners()
        __Lógica de Autenticación__
        - void iniciarSesion()
        - void validarCampos() : boolean
        - void mostrarError(String)
        - void irARegistro()
        - void irAPrincipal()
    }

    class RegistroActivity extends AppCompatActivity ACTIVITY_COLOR {
        - TextInputEditText etNombre
        - TextInputEditText etApellido
        - TextInputEditText etEmail
        - TextInputEditText etPassword
        - TextInputEditText etConfirmarPassword
        - MaterialButton btnRegistrar
        - TextView btnYaTengoCuenta
        - ProgressBar progressBar
        - FirebaseAuth firebaseAuth
        - FirebaseManager firebaseManager
        __Ciclo de Vida__
        # void onCreate(Bundle)
        __UI__
        - void inicializarVistas()
        - void configurarListeners()
        __Lógica de Registro__
        - void registrarUsuario()
        - void validarCampos() : boolean
        - void mostrarError(String)
        - void irALogin()
        - void irAPrincipal()
    }

    class ListaNoticiasActivity extends BaseActivity ACTIVITY_COLOR {
        - RecyclerView rvNoticias
        - NoticiaAdapter adapter
        - List<Noticia> listaNoticias
        - FirebaseManager firebaseManager
        - SwipeRefreshLayout swipeRefresh
        - ProgressBar progressBar
        - Chip chipTodas, chipCercanas, chipDestacadas
        - ChipGroup chipGroupCategorias
        - TextView tvEmptyState
        __Ciclo de Vida__
        # void onCreate(Bundle)
        # int getNavegacionActiva() : int
        # int getLayoutResourceId() : int
        __UI__
        - void inicializarVistas()
        - void configurarRecyclerView()
        - void configurarChips()
        __Carga de Datos__
        - void cargarNoticias()
        - void cargarNoticiasCercanas()
        - void cargarNoticiasDestacadas()
        - void filtrarPorCategoria(categoriaId)
        __Navegación__
        - void abrirDetalleNoticia(Noticia)
    }

    class DetalleNoticiaActivity extends AppCompatActivity ACTIVITY_COLOR {
        - ImageView ivImagen
        - TextView tvTitulo, tvContenido, tvDescripcion
        - TextView tvAutor, tvFecha, tvUbicacion
        - TextView tvCitaDestacada, tvImpactoComunitario
        - Chip chipCategoria
        - FloatingActionButton fabGuardar
        - MaterialButton btnCompartir
        - Noticia noticia
        - FirebaseManager firebaseManager
        - boolean esGuardada
        __Ciclo de Vida__
        # void onCreate(Bundle)
        __UI__
        - void inicializarVistas()
        - void configurarListeners()
        __Carga de Datos__
        - void cargarDatosNoticia()
        - void cargarNoticiaDesdeFirebase(String)
        __Acciones__
        - void toggleGuardado()
        - void compartirNoticia()
        - void mostrarEnMapa()
        - void incrementarVisualizaciones()
    }

    class MapaActivity extends BaseActivity ACTIVITY_COLOR {
        - GoogleMap map
        - MapView mapView
        - List<Noticia> noticias
        - SwitchMaterial switchNoticias
        - ChipGroup chipGroupCategorias
        - FirebaseManager firebaseManager
        - ProgressBar progressBar
        - FusedLocationProviderClient locationClient
        __Ciclo de Vida__
        # void onCreate(Bundle)
        # void onResume()
        # void onPause()
        # void onDestroy()
        # int getNavegacionActiva() : int
        # int getLayoutResourceId() : int
        __Mapa__
        - void inicializarMapa()
        - void cargarMapa()
        - void agregarMarcadoresNoticias()
        - void centrarEnUbicacionActual()
        - void moverCamara(lat, lon, zoom)
        __Marcadores__
        - void mostrarDetalleMarker(Marker)
        - void crearMarcadorNoticia(Noticia) : MarkerOptions
        __Filtros__
        - void filtrarPorCategoria(categoriaId)
    }

    class PerfilActivity extends BaseActivity ACTIVITY_COLOR {
        - ImageView ivAvatar
        - TextView tvNombre, tvEmail, tvBio, tvUbicacion
        - TextView tvNoticiasLeidas, tvNoticiasGuardadas, tvDiasActivo
        - MaterialCardView cardEstadisticas
        - SwitchMaterial switchNotificaciones, switchModoOscuro
        - ChipGroup chipGroupIntereses
        - FloatingActionButton fabCamera
        - MaterialButton btnEditarPerfil, btnAjustes, btnCerrarSesion
        - FirebaseManager firebaseManager
        - FirebaseStorage storage
        __Ciclo de Vida__
        # void onCreate(Bundle)
        # void onResume()
        # int getNavegacionActiva() : int
        # int getLayoutResourceId() : int
        __UI__
        - void inicializarVistas()
        - void configurarListeners()
        __Carga de Datos__
        - void cargarDatosUsuario()
        - void cargarEstadisticas()
        - void cargarIntereses()
        __Acciones de Perfil__
        - void mostrarDialogoSeleccionarFoto()
        - void abrirGaleria()
        - void abrirCamara()
        - void subirFotoAPerfil(Bitmap)
        __Configuración__
        - void toggleNotificaciones(boolean)
        - void toggleModoOscuro(boolean)
        - void cambiarPassword(actual, nuevo)
        __Navegación__
        - void irAEditarPerfil()
        - void irAAjustes()
        - void cerrarSesion()
    }

    class EditarPerfilActivity extends AppCompatActivity ACTIVITY_COLOR {
        - TextInputEditText etNombre, etApellido, etUsername, etBio, etUbicacion, etTelefono
        - ImageView ivAvatar
        - FloatingActionButton fabCambiarFoto
        - MaterialButton btnGuardar, btnCancelar
        - FirebaseManager firebaseManager
        - Uri selectedImageUri
        - boolean imagenCambiada
        __Ciclo de Vida__
        # void onCreate(Bundle)
        __UI__
        - void inicializarVistas()
        - void configurarListeners()
        __Carga de Datos__
        - void cargarDatosUsuario()
        __Acciones__
        - void cambiarFoto()
        - void guardarCambios()
        - void validarCampos() : boolean
        - void subirNuevaFoto(callback)
        - void actualizarDatosUsuario()
    }

    class AjustesActivity extends AppCompatActivity ACTIVITY_COLOR {
        - ImageView ivAvatarSettings
        - FloatingActionButton fabEditAvatar
        - TextView tvNombreSettings, tvUbicacionSettings
        - LinearLayout btnEditProfile, btnMyLocations, btnSecurityPrivacy
        - LinearLayout btnNewsCategories, btnLanguage
        - LinearLayout btnHelpCenter, btnAbout
        - SwitchMaterial switchPushNotifications, switchEmailDigest
        - MaterialButton btnLogoutSettings
        - FirebaseManager firebaseManager
        __Ciclo de Vida__
        # void onCreate(Bundle)
        # void onResume()
        __UI__
        - void inicializarVistas()
        - void configurarListeners()
        __Carga de Datos__
        - void cargarDatosUsuario()
        __Diálogos de Configuración__
        - void mostrarDialogoUbicaciones()
        - void mostrarDialogoSeguridad()
        - void mostrarDialogoCategorias()
        - void mostrarDialogoIdioma()
        - void mostrarDialogoCentroAyuda()
        - void mostrarDialogoAcercaDe()
        - void mostrarDialogoCambiarPassword()
        - void mostrarDialogoCerrarSesion()
        __Navegación__
        - void abrirEditarPerfil()
        - void cerrarSesion()
    }

    class ArticulosGuardadosActivity extends BaseActivity ACTIVITY_COLOR {
        - RecyclerView rvArticulosGuardados
        - NoticiaAdapter adapter
        - List<Noticia> noticiasGuardadas
        - TextView tvEmptyState
        - ProgressBar progressBar
        - FirebaseManager firebaseManager
        __Ciclo de Vida__
        # void onCreate(Bundle)
        # int getNavegacionActiva() : int
        # int getLayoutResourceId() : int
        __UI__
        - void inicializarVistas()
        - void configurarRecyclerView()
        __Carga de Datos__
        - void cargarArticulosGuardados()
        __Acciones__
        - void eliminarArticulo(noticiaId)
        - void abrirDetalleNoticia(Noticia)
    }

    class NotificacionesActivity extends BaseActivity ACTIVITY_COLOR {
        - SwitchMaterial switchNotificaciones
        - SwitchMaterial switchNoticias, switchDestacadas
        - LinearLayout layoutConfiguracion
        - TextView tvEstado
        __Ciclo de Vida__
        # void onCreate(Bundle)
        # int getNavegacionActiva() : int
        # int getLayoutResourceId() : int
        __UI__
        - void inicializarVistas()
        - void configurarListeners()
        __Preferencias__
        - void cargarPreferencias()
        - void actualizarVisibilidadOpciones(activo)
        - void guardarPreferencias()
    }
}

' ============================================
' PAQUETE: ADAPTERS (Adaptadores RecyclerView)
' ============================================

package "com.tesistitulacion.noticiaslocales.adapters" <<Rectangle>> {

    class NoticiaAdapter extends RecyclerView.Adapter ADAPTER_COLOR {
        - List<Noticia> noticias
        - OnNoticiaClickListener listener
        - Context context
        __Constructor__
        + NoticiaAdapter(noticias, listener)
        __RecyclerView Override__
        + ViewHolder onCreateViewHolder(parent, viewType) : ViewHolder
        + void onBindViewHolder(holder, position)
        + int getItemCount() : int
        __Datos__
        + void setNoticias(List<Noticia>)
        + void agregarNoticia(Noticia)
        + void eliminarNoticia(int position)
        + void limpiar()
        __ViewHolder Interno__
        {static} class ViewHolder extends RecyclerView.ViewHolder {
            - ImageView ivImagen
            - TextView tvTitulo, tvDescripcion
            - TextView tvAutor, tvFecha, tvCategoria
            - Chip chipCategoria
            - ImageButton btnGuardar, btnCompartir
            + ViewHolder(itemView)
            + void bind(Noticia, listener)
        }
    }

    interface OnNoticiaClickListener <<Interface>> ADAPTER_COLOR {
        + void onNoticiaClick(Noticia)
        + void onGuardarClick(Noticia)
        + void onCompartirClick(Noticia)
    }

    class NoticiaMapaAdapter ADAPTER_COLOR {
        - Context context
        __Constructor__
        + NoticiaMapaAdapter(Context)
        __Métodos__
        + View getInfoWindow(Marker) : View
        + View getInfoContents(Marker) : View
        - void cargarDatosNoticia(View, Noticia)
    }
}

' ============================================
' RELACIONES ENTRE CLASES
' ============================================

' Relaciones del Modelo
Usuario "1" -- "0..*" Noticia : autor >
Noticia "0..*" -- "1" Categoria : categoría >
Noticia "0..*" -- "0..1" Parroquia : ubicada en >

' FirebaseManager gestiona entidades
FirebaseManager ..> Usuario : gestiona >
FirebaseManager ..> Noticia : gestiona >
FirebaseManager ..> Categoria : gestiona >
FirebaseManager ..> Parroquia : gestiona >
FirebaseManager ..|> FirestoreCallback : usa >

' Utilidades
UsuarioPreferences ..> Usuario : almacena datos >
UbicacionUtils ..|> UbicacionCallback : usa >

' BaseActivity es base para otras activities
LoginActivity --|> BaseActivity
RegistroActivity --|> BaseActivity
ListaNoticiasActivity --|> BaseActivity
MapaActivity --|> BaseActivity
PerfilActivity --|> BaseActivity
ArticulosGuardadosActivity --|> BaseActivity
NotificacionesActivity --|> BaseActivity

' Activities usan FirebaseManager
ListaNoticiasActivity ..> FirebaseManager : consulta >
DetalleNoticiaActivity ..> FirebaseManager : consulta >
MapaActivity ..> FirebaseManager : consulta >
PerfilActivity ..> FirebaseManager : consulta >
EditarPerfilActivity ..> FirebaseManager : actualiza >
AjustesActivity ..> FirebaseManager : consulta >
LoginActivity ..> FirebaseManager : autentica >
RegistroActivity ..> FirebaseManager : registra >

' Activities usan UsuarioPreferences
PerfilActivity ..> UsuarioPreferences : lee/escribe >
AjustesActivity ..> UsuarioPreferences : lee/escribe >
EditarPerfilActivity ..> UsuarioPreferences : lee/escribe >
ListaNoticiasActivity ..> UsuarioPreferences : lee >
DetalleNoticiaActivity ..> UsuarioPreferences : lee/escribe >

' Activities usan Utils
BaseActivity ..> ThemeManager : aplica tema >
AjustesActivity ..> DialogHelper : muestra diálogos >
EditarPerfilActivity ..> DialogHelper : muestra diálogos >
MapaActivity ..> UbicacionUtils : cálculos GPS >
ListaNoticiasActivity ..> UbicacionUtils : distancias >

' Adapters
ListaNoticiasActivity ..> NoticiaAdapter : usa >
ArticulosGuardadosActivity ..> NoticiaAdapter : usa >
NoticiaAdapter "1" o-- "0..*" Noticia : muestra >
NoticiaAdapter ..|> OnNoticiaClickListener : implementa listener >
ListaNoticiasActivity ..|> OnNoticiaClickListener : implementa >
ArticulosGuardadosActivity ..|> OnNoticiaClickListener : implementa >
MapaActivity ..> NoticiaMapaAdapter : usa >

@enduml
```

---

## Resumen de la Arquitectura Android

### Paquetes y Clases

| Paquete | Clases | Descripción |
|---------|--------|-------------|
| **modelo** | 4 | Usuario, Noticia, Categoria, Parroquia |
| **firebase** | 2 | FirebaseManager (Singleton), FirestoreCallback |
| **db** | 2 | ApiConfig (Singleton), NoticiaServiceHTTP |
| **utils** | 5 | UsuarioPreferences, ThemeManager, DialogHelper, UbicacionUtils, UbicacionCallback |
| **activities** | 12 | SplashActivity, LoginActivity, RegistroActivity, ListaNoticiasActivity, DetalleNoticiaActivity, MapaActivity, PerfilActivity, EditarPerfilActivity, AjustesActivity, ArticulosGuardadosActivity, NotificacionesActivity, BaseActivity |
| **adapters** | 3 | NoticiaAdapter, OnNoticiaClickListener, NoticiaMapaAdapter |

**Total de clases: 28**

---

## Patrones de Diseño Utilizados

1. **Singleton**
   - `FirebaseManager`: Instancia única para gestión de Firebase
   - `ApiConfig`: Configuración única de Retrofit
   - `UsuarioPreferences`: Acceso centralizado a SharedPreferences

2. **Callback/Observer**
   - `FirestoreCallback<T>`: Manejo asíncrono de operaciones Firebase
   - `OnNoticiaClickListener`: Eventos de clicks en RecyclerView
   - `UbicacionCallback`: Callbacks de geolocalización

3. **Adapter Pattern**
   - `NoticiaAdapter`: Adaptador para RecyclerView de noticias
   - `NoticiaMapaAdapter`: Adaptador para InfoWindows de Google Maps

4. **Template Method**
   - `BaseActivity`: Define estructura común para activities con navegación
   - Métodos abstractos: `getNavegacionActiva()`, `getLayoutResourceId()`

5. **Repository Pattern** (implícito)
   - `FirebaseManager`: Abstrae acceso a datos de Firebase
   - `UsuarioPreferences`: Abstrae acceso a datos locales

---

## Navegación de la App

### Estructura de 3 Secciones Principales

```
┌─────────────────────────────────────────┐
│          BaseActivity                    │
│  (Navegación común)                     │
└─────────────────────────────────────────┘
           │
           ├── NAV_NOTICIAS (1)
           │   └── ListaNoticiasActivity
           │       └── DetalleNoticiaActivity
           │
           ├── NAV_MAPA (3)
           │   └── MapaActivity
           │
           └── NAV_PERFIL (4)
               └── PerfilActivity
                   ├── EditarPerfilActivity
                   ├── AjustesActivity
                   ├── ArticulosGuardadosActivity
                   └── NotificacionesActivity
```

---

## Flujo de Datos

```
Usuario Interactúa
     ↓
Activity (UI)
     ↓
FirebaseManager / UsuarioPreferences
     ↓
Firebase Firestore / SharedPreferences
     ↓
Callback (onSuccess / onError)
     ↓
Activity actualiza UI
```

---

## Tecnologías Android Utilizadas

- **Language**: Java
- **Min SDK**: API 21 (Android 5.0 Lollipop)
- **Target SDK**: API 34 (Android 14)
- **UI**: Material Design 3 Components
- **Navigation**: BottomNavigationView personalizada
- **Backend**: Firebase (Auth, Firestore, Storage)
- **Maps**: Google Maps SDK for Android
- **Location**: Google Play Services Location
- **Image Loading**: Glide
- **HTTP**: Retrofit 2 (opcional)
- **Storage**: SharedPreferences (encriptadas)

---

**Proyecto**: GeoNews - Aplicación Móvil Android
**Versión**: 0.1.0 (Sin módulo de Eventos)
**Plataforma**: Android
**Fecha**: Enero 2026
