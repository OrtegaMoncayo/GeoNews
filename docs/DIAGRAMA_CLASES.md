# Diagrama de Clases - GeoNews

## Descripción
Este diagrama muestra la estructura de clases del sistema GeoNews, incluyendo las entidades del modelo de datos, actividades principales y componentes de gestión.

---

## Diagrama UML (PlantUML)

```plantuml
@startuml GeoNews_Class_Diagram

!define ENTITY_COLOR #E3F2FD
!define ACTIVITY_COLOR #FFF3E0
!define MANAGER_COLOR #E8F5E9
!define UTIL_COLOR #F3E5F5

' ============================================
' ENTIDADES DEL MODELO
' ============================================

package "modelo" <<Rectangle>> {

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
        - Integer eventosAsistidos
        - Boolean verificado
        - String tipoUsuario
        - String createdAt
        --
        + Usuario()
        + Usuario(nombre, apellido, email, password)
        + String getNombreCompleto()
        + boolean esAdmin()
        + boolean esReportero()
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
        --
        + Noticia()
        + Noticia(titulo, descripcion, contenido, categoriaId)
        + String getColorCategoria()
        + String getFechaPublicacion()
        + String getParroquiaNombre()
    }

    class Evento <<Entity>> ENTITY_COLOR {
        - Integer id
        - String firestoreId
        - String descripcion
        - Long fecha
        - String ubicacion
        - Integer creadorId
        - Integer parroquiaId
        - Double latitud
        - Double longitud
        - String categoriaEvento
        - Integer cupoMaximo
        - Integer cupoActual
        - Double costo
        - String imagenUrl
        - String contactoTelefono
        - String contactoEmail
        - String estado
        - Long fechaCreacion
        - String parroquiaNombre
        - String creadorNombre
        - Double distancia
        --
        + Evento()
        + Evento(descripcion, fecha, ubicacion)
        + boolean tieneCuposDisponibles()
        + Integer getCuposRestantes()
        + boolean yaOcurrio()
        + boolean esGratuito()
        + String getFechaFormateada()
        + String getCostoFormateado()
        + String getCategoriaNombre()
        + String getColorCategoria()
    }

    class Categoria <<Entity>> ENTITY_COLOR {
        - Integer id
        - String nombre
        - String descripcion
        - String icono
        - String color
        --
        + Categoria()
        + Categoria(nombre)
    }

    class Parroquia <<Entity>> ENTITY_COLOR {
        - Integer id
        - String nombre
        - Double latitud
        - Double longitud
        --
        + Parroquia()
        + Parroquia(nombre, latitud, longitud)
    }
}

' ============================================
' GESTORES Y SERVICIOS
' ============================================

package "firebase" <<Rectangle>> {

    class FirebaseManager <<Singleton>> MANAGER_COLOR {
        - {static} FirebaseManager instance
        - FirebaseFirestore db
        - FirebaseAuth auth
        - FirebaseStorage storage
        --
        + {static} FirebaseManager getInstance()
        + void registrarUsuario(Usuario, callback)
        + void obtenerUsuarioPorId(String, callback)
        + void actualizarUsuario(String, Map, callback)
        + void obtenerNoticias(callback)
        + void obtenerNoticiasCercanas(lat, lon, radio, callback)
        + void obtenerEventos(callback)
        + void obtenerEventosCercanos(lat, lon, radio, callback)
        + void crearEvento(Evento, callback)
        + void registrarAsistenciaEvento(String, String, callback)
        + void incrementarVisualizaciones(String)
    }

    interface FirestoreCallback<T> {
        + void onSuccess(T result)
        + void onError(Exception e)
    }
}

package "db" <<Rectangle>> {

    class ApiConfig <<Singleton>> MANAGER_COLOR {
        + {static} String BASE_URL
        + {static} String ENVIRONMENT
        --
        + {static} Retrofit getRetrofitInstance()
        + {static} void setProduction()
        + {static} void setDevelopment()
    }

    interface NoticiaServiceHTTP {
        + Call<List<Noticia>> obtenerNoticias()
        + Call<List<Noticia>> obtenerNoticiasCercanas(lat, lon, radio)
        + Call<Noticia> obtenerNoticiaPorId(id)
        + Call<List<Evento>> obtenerEventos()
    }
}

' ============================================
' UTILIDADES
' ============================================

package "utils" <<Rectangle>> {

    class UsuarioPreferences <<Singleton>> UTIL_COLOR {
        - {static} SharedPreferences sharedPreferences
        - {static} SharedPreferences.Editor editor
        --
        + {static} void init(Context)
        + {static} void guardarDatosUsuario(userId, nombre, apellido, email)
        + {static} void guardarFotoPerfil(url)
        + {static} void guardarBio(bio)
        + {static} void guardarUbicacion(ubicacion)
        + {static} void guardarNotificacionesActivas(activo)
        + {static} void agregarNoticiaGuardada(noticiaId)
        + {static} void eliminarNoticiaGuardada(noticiaId)
        + {static} List<String> getNoticiasGuardadas()
        + {static} List<String> getIntereses()
        + {static} void agregarInteres(interes)
        + {static} void eliminarInteres(interes)
        + {static} void cerrarSesion()
    }

    class ThemeManager UTIL_COLOR {
        --
        + {static} void applyTheme(Context)
        + {static} boolean isDarkMode(Context)
        + {static} void setDarkMode(Context, boolean)
    }

    class DialogHelper UTIL_COLOR {
        --
        + {static} void showInfo(Context, title, message)
        + {static} void showConfirmationDialog(Context, title, message, positive, negative, callback)
        + {static} void showListDialog(Context, title, items, listener)
        + {static} void showEditDialog(Context, title, hint, currentValue, callback)
    }

    class UbicacionUtils UTIL_COLOR {
        --
        + {static} double calcularDistancia(lat1, lon1, lat2, lon2)
        + {static} String obtenerNombreCiudad(Context, lat, lon)
        + {static} void solicitarPermisoUbicacion(Activity)
    }
}

' ============================================
' ACTIVIDADES PRINCIPALES
' ============================================

package "activities" <<Rectangle>> {

    abstract class BaseActivity extends AppCompatActivity ACTIVITY_COLOR {
        # {static} int NAV_NOTICIAS
        # {static} int NAV_EVENTOS
        # {static} int NAV_MAPA
        # {static} int NAV_PERFIL
        # BottomNavigationView bottomNav
        --
        # {abstract} int getNavegacionActiva()
        # {abstract} int getLayoutResourceId()
        # void setupBottomNavigation()
        # void showToast(String)
    }

    class SplashActivity extends AppCompatActivity ACTIVITY_COLOR {
        - FirebaseAuth firebaseAuth
        --
        # void onCreate(Bundle)
        - void verificarSesion()
        - void irALogin()
        - void irAPrincipal()
    }

    class LoginActivity extends AppCompatActivity ACTIVITY_COLOR {
        - TextInputEditText etEmail
        - TextInputEditText etPassword
        - MaterialButton btnLogin
        - TextView btnRegistro
        - FirebaseAuth firebaseAuth
        - FirebaseManager firebaseManager
        --
        # void onCreate(Bundle)
        - void iniciarSesion()
        - void validarCampos()
    }

    class RegistroActivity extends AppCompatActivity ACTIVITY_COLOR {
        - TextInputEditText etNombre
        - TextInputEditText etApellido
        - TextInputEditText etEmail
        - TextInputEditText etPassword
        - MaterialButton btnRegistrar
        - FirebaseAuth firebaseAuth
        - FirebaseManager firebaseManager
        --
        # void onCreate(Bundle)
        - void registrarUsuario()
        - void validarCampos()
    }

    class ListaNoticiasActivity extends BaseActivity ACTIVITY_COLOR {
        - RecyclerView rvNoticias
        - NoticiaAdapter adapter
        - List<Noticia> listaNoticias
        - FirebaseManager firebaseManager
        - SwipeRefreshLayout swipeRefresh
        - Chip chipTodas, chipCercanas, chipDestacadas
        --
        # void onCreate(Bundle)
        - void cargarNoticias()
        - void cargarNoticiasCercanas()
        - void cargarNoticiasDestacadas()
        - void filtrarPorCategoria(categoriaId)
        - void abrirDetalleNoticia(noticia)
    }

    class DetalleNoticiaActivity extends AppCompatActivity ACTIVITY_COLOR {
        - ImageView ivImagen
        - TextView tvTitulo, tvContenido
        - TextView tvAutor, tvFecha
        - Chip chipCategoria
        - FloatingActionButton fabGuardar
        - Noticia noticia
        - FirebaseManager firebaseManager
        --
        # void onCreate(Bundle)
        - void cargarDatosNoticia()
        - void toggleGuardado()
        - void compartirNoticia()
        - void mostrarEnMapa()
    }

    class ListaEventosActivity extends BaseActivity ACTIVITY_COLOR {
        - RecyclerView rvEventos
        - EventoAdapter adapter
        - List<Evento> listaEventos
        - FirebaseManager firebaseManager
        --
        # void onCreate(Bundle)
        - void cargarEventos()
        - void cargarEventosCercanos()
        - void filtrarPorCategoria(categoria)
        - void abrirDetalleEvento(evento)
    }

    class RegistrarEventoActivity extends AppCompatActivity ACTIVITY_COLOR {
        - TextInputEditText etDescripcion
        - TextInputEditText etUbicacion
        - MaterialButton btnFecha
        - MaterialButton btnRegistrar
        - Spinner spCategoria, spParroquia
        - FirebaseManager firebaseManager
        --
        # void onCreate(Bundle)
        - void seleccionarFecha()
        - void seleccionarUbicacion()
        - void registrarEvento()
    }

    class MapaActivity extends BaseActivity ACTIVITY_COLOR {
        - GoogleMap map
        - List<Noticia> noticias
        - List<Evento> eventos
        - SwitchMaterial switchNoticias, switchEventos
        - FirebaseManager firebaseManager
        --
        # void onCreate(Bundle)
        - void cargarMapa()
        - void agregarMarcadoresNoticias()
        - void agregarMarcadoresEventos()
        - void mostrarDetalleMarker(marker)
        - void centrarEnUbicacionActual()
    }

    class PerfilActivity extends BaseActivity ACTIVITY_COLOR {
        - ImageView ivAvatar
        - TextView tvNombre, tvEmail
        - TextView tvNoticiasLeidas, tvNoticiasGuardadas, tvDiasActivo
        - SwitchMaterial switchNotificaciones, switchModoOscuro
        - ChipGroup chipGroupIntereses
        - FloatingActionButton fabCamera
        - FirebaseManager firebaseManager
        - FirebaseStorage storage
        --
        # void onCreate(Bundle)
        - void cargarDatosUsuario()
        - void cargarEstadisticas()
        - void cargarIntereses()
        - void mostrarDialogoSeleccionarFoto()
        - void subirFotoAPerfil(Bitmap)
        - void cambiarPassword(actual, nuevo)
        - void cerrarSesion()
    }

    class EditarPerfilActivity extends AppCompatActivity ACTIVITY_COLOR {
        - TextInputEditText etNombre, etUsername, etBio, etUbicacion
        - ImageView ivAvatar
        - FloatingActionButton fabCambiarFoto
        - MaterialButton btnGuardar
        - FirebaseManager firebaseManager
        --
        # void onCreate(Bundle)
        - void cargarDatosUsuario()
        - void guardarCambios()
        - void cambiarFoto()
    }

    class AjustesActivity extends AppCompatActivity ACTIVITY_COLOR {
        - ImageView ivAvatar
        - TextView tvNombre, tvUbicacion
        - SwitchMaterial switchPushNotifications, switchEmailDigest
        - LinearLayout btnEditProfile, btnMyLocations, btnSecurityPrivacy
        - LinearLayout btnNewsCategories, btnLanguage
        - MaterialButton btnLogout
        --
        # void onCreate(Bundle)
        - void mostrarDialogoUbicaciones()
        - void mostrarDialogoSeguridad()
        - void mostrarDialogoCategorias()
        - void mostrarDialogoCentroAyuda()
    }

    class ArticulosGuardadosActivity extends BaseActivity ACTIVITY_COLOR {
        - RecyclerView rvArticulosGuardados
        - NoticiaAdapter adapter
        - List<Noticia> noticiasGuardadas
        - TextView tvEmptyState
        --
        # void onCreate(Bundle)
        - void cargarArticulosGuardados()
        - void eliminarArticulo(noticiaId)
    }

    class NotificacionesActivity extends BaseActivity ACTIVITY_COLOR {
        - SwitchMaterial switchNotificaciones, switchNoticias, switchDestacadas
        - LinearLayout layoutConfiguracion
        --
        # void onCreate(Bundle)
        - void cargarPreferencias()
        - void actualizarVisibilidadOpciones(activo)
    }
}

' ============================================
' ADAPTADORES
' ============================================

package "adapters" <<Rectangle>> {

    class NoticiaAdapter extends RecyclerView.Adapter ACTIVITY_COLOR {
        - List<Noticia> noticias
        - OnNoticiaClickListener listener
        - Context context
        --
        + NoticiaAdapter(noticias, listener)
        + ViewHolder onCreateViewHolder(parent, viewType)
        + void onBindViewHolder(holder, position)
        + int getItemCount()
    }

    class EventoAdapter extends RecyclerView.Adapter ACTIVITY_COLOR {
        - List<Evento> eventos
        - OnEventoClickListener listener
        - Context context
        --
        + EventoAdapter(eventos, listener)
        + ViewHolder onCreateViewHolder(parent, viewType)
        + void onBindViewHolder(holder, position)
        + int getItemCount()
    }

    interface OnNoticiaClickListener {
        + void onNoticiaClick(Noticia)
        + void onGuardarClick(Noticia)
        + void onCompartirClick(Noticia)
    }

    interface OnEventoClickListener {
        + void onEventoClick(Evento)
        + void onAsistirClick(Evento)
    }
}

' ============================================
' RELACIONES
' ============================================

' Relaciones del Modelo
Usuario "1" -- "0..*" Noticia : autor >
Usuario "1" -- "0..*" Evento : creador >
Noticia "0..*" -- "1" Categoria : pertenece >
Noticia "0..*" -- "1" Parroquia : ubicada en >
Evento "0..*" -- "1" Parroquia : ubicada en >

' FirebaseManager usa las entidades
FirebaseManager ..> Usuario : gestiona >
FirebaseManager ..> Noticia : gestiona >
FirebaseManager ..> Evento : gestiona >
FirebaseManager ..|> FirestoreCallback : implementa

' Utilidades usan Context
UsuarioPreferences ..> Usuario : almacena >
ThemeManager ..> BaseActivity : aplica tema >

' Actividades usan Managers
BaseActivity ..> ThemeManager : usa >
ListaNoticiasActivity ..> FirebaseManager : consulta >
ListaNoticiasActivity ..> NoticiaAdapter : usa >
ListaNoticiasActivity ..|> OnNoticiaClickListener : implementa
DetalleNoticiaActivity ..> FirebaseManager : consulta >
ListaEventosActivity ..> FirebaseManager : consulta >
ListaEventosActivity ..> EventoAdapter : usa >
MapaActivity ..> FirebaseManager : consulta >
PerfilActivity ..> FirebaseManager : consulta >
PerfilActivity ..> UsuarioPreferences : usa >
EditarPerfilActivity ..> FirebaseManager : actualiza >
AjustesActivity ..> UsuarioPreferences : configura >

' Adaptadores usan entidades
NoticiaAdapter "1" o-- "0..*" Noticia : muestra >
EventoAdapter "1" o-- "0..*" Evento : muestra >

' Herencia de actividades
LoginActivity --|> BaseActivity
RegistroActivity --|> BaseActivity
ListaNoticiasActivity --|> BaseActivity
ListaEventosActivity --|> BaseActivity
MapaActivity --|> BaseActivity
PerfilActivity --|> BaseActivity
ArticulosGuardadosActivity --|> BaseActivity

@enduml
```

---

## Descripción de Componentes

### Paquete `modelo`
Contiene las clases de entidad que representan los objetos de dominio del sistema:
- **Usuario**: Representa un usuario del sistema con sus datos personales y estadísticas
- **Noticia**: Noticias locales con geolocalización
- **Evento**: Eventos comunitarios con gestión de cupos
- **Categoria**: Categorías de noticias
- **Parroquia**: Parroquias de Ibarra con coordenadas

### Paquete `firebase`
Gestión de la persistencia con Firebase:
- **FirebaseManager**: Singleton que maneja todas las operaciones CRUD con Firestore
- **FirestoreCallback**: Interfaz para callbacks asíncronos

### Paquete `db`
Configuración de servicios HTTP (legacy):
- **ApiConfig**: Configuración de Retrofit
- **NoticiaServiceHTTP**: Interface de servicios REST

### Paquete `utils`
Utilidades y helpers:
- **UsuarioPreferences**: Gestión de preferencias y datos locales encriptados
- **ThemeManager**: Gestión del tema oscuro/claro
- **DialogHelper**: Creación de diálogos estándar
- **UbicacionUtils**: Cálculos de geolocalización

### Paquete `activities`
Pantallas de la aplicación:
- **BaseActivity**: Clase base con navegación inferior
- **LoginActivity/RegistroActivity**: Autenticación
- **ListaNoticiasActivity**: Feed de noticias con filtros
- **DetalleNoticiaActivity**: Vista detallada de noticia
- **ListaEventosActivity**: Lista de eventos
- **MapaActivity**: Visualización en mapa de noticias y eventos
- **PerfilActivity**: Perfil del usuario con estadísticas
- **EditarPerfilActivity**: Edición de datos del perfil
- **AjustesActivity**: Configuración de la aplicación
- **ArticulosGuardadosActivity**: Noticias guardadas
- **NotificacionesActivity**: Preferencias de notificaciones

### Paquete `adapters`
Adaptadores para RecyclerView:
- **NoticiaAdapter**: Muestra lista de noticias
- **EventoAdapter**: Muestra lista de eventos

---

## Patrones de Diseño Utilizados

1. **Singleton**: FirebaseManager, ApiConfig, UsuarioPreferences
2. **Callback**: FirestoreCallback para operaciones asíncronas
3. **Adapter**: NoticiaAdapter, EventoAdapter para RecyclerView
4. **Template Method**: BaseActivity define estructura común
5. **Observer**: Listeners para clicks en adaptadores

---

**Proyecto**: GeoNews - Noticias Locales de Ibarra
**Versión**: 0.1.0
**Fecha**: Enero 2026
