# Diagrama de Clases Actualizado - GeoNews (Sin Eventos)

## Descripción
Diagrama de clases del sistema GeoNews después de la eliminación del módulo de Eventos.

---

## Diagrama UML Actualizado

```plantuml
@startuml GeoNews_Class_Diagram_Sin_Eventos

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
        - String tipo
        - String descripcion
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
        + void incrementarVisualizaciones(String)
        + void obtenerCategorias(callback)
        + void obtenerParroquias(callback)
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

    class MapaActivity extends BaseActivity ACTIVITY_COLOR {
        - GoogleMap map
        - List<Noticia> noticias
        - SwitchMaterial switchNoticias
        - FirebaseManager firebaseManager
        --
        # void onCreate(Bundle)
        - void cargarMapa()
        - void agregarMarcadoresNoticias()
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

    interface OnNoticiaClickListener {
        + void onNoticiaClick(Noticia)
        + void onGuardarClick(Noticia)
        + void onCompartirClick(Noticia)
    }
}

' ============================================
' RELACIONES
' ============================================

' Relaciones del Modelo
Usuario "1" -- "0..*" Noticia : autor >
Noticia "0..*" -- "1" Categoria : pertenece >
Noticia "0..*" -- "1" Parroquia : ubicada en >

' FirebaseManager usa las entidades
FirebaseManager ..> Usuario : gestiona >
FirebaseManager ..> Noticia : gestiona >
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
MapaActivity ..> FirebaseManager : consulta >
PerfilActivity ..> FirebaseManager : consulta >
PerfilActivity ..> UsuarioPreferences : usa >
EditarPerfilActivity ..> FirebaseManager : actualiza >
AjustesActivity ..> UsuarioPreferences : configura >

' Adaptadores usan entidades
NoticiaAdapter "1" o-- "0..*" Noticia : muestra >

' Herencia de actividades
LoginActivity --|> BaseActivity
RegistroActivity --|> BaseActivity
ListaNoticiasActivity --|> BaseActivity
MapaActivity --|> BaseActivity
PerfilActivity --|> BaseActivity
ArticulosGuardadosActivity --|> BaseActivity

@enduml
```

---

## Cambios Principales (Eliminación de Eventos)

### Clases Eliminadas
- ❌ **Evento** (modelo)
- ❌ **EventoAdapter** (adapter)
- ❌ **ListaEventosActivity** (activity)
- ❌ **RegistrarEventoActivity** (activity)
- ❌ **DetalleEventoActivity** (activity)
- ❌ **EventoServiceHTTP** (service)

### Modificaciones en Clases Existentes

#### Usuario
**Campo eliminado:**
- ❌ `eventosAsistidos: Integer`

**Métodos eliminados:**
- ❌ `getEventosAsistidos()`
- ❌ `setEventosAsistidos(Integer)`

#### FirebaseManager
**Constante eliminada:**
- ❌ `COLLECTION_EVENTOS`

**Métodos eliminados:**
- ❌ `getEventoById(String, callback)`
- ❌ `getEventosFuturos(callback)`
- ❌ `getEventosFuturosRealtime(callback)`
- ❌ `createEvento(Evento, callback)`
- ❌ `documentToEvento(DocumentSnapshot)`
- ❌ `eventoToMap(Evento)`

#### BaseActivity
**Constante eliminada:**
- ❌ `NAV_EVENTOS = 2`

**Variables eliminadas:**
- ❌ `llNavEventos`
- ❌ `ivNavEventos`
- ❌ `tvNavEventos`
- ❌ `indicatorEventos`

**Navegación actualizada:**
- ✅ 3 secciones: NAV_NOTICIAS (1), NAV_MAPA (3), NAV_PERFIL (4)

---

## Resumen de Arquitectura Actualizada

### Paquetes y Cantidad de Clases

| Paquete | Clases | Descripción |
|---------|--------|-------------|
| modelo | 4 | Usuario, Noticia, Categoria, Parroquia |
| firebase | 2 | FirebaseManager, FirestoreCallback |
| db | 2 | ApiConfig, NoticiaServiceHTTP |
| utils | 4 | UsuarioPreferences, ThemeManager, DialogHelper, UbicacionUtils |
| activities | 12 | Splash, Login, Registro, Noticias, Detalle, Mapa, Perfil, Editar, Ajustes, Guardados, Notificaciones |
| adapters | 2 | NoticiaAdapter, OnNoticiaClickListener |

**Total de clases**: ~26 (reducción de 30 a 26)

---

## Patrones de Diseño Utilizados

1. **Singleton**: FirebaseManager, ApiConfig, UsuarioPreferences
2. **Callback**: FirestoreCallback para operaciones asíncronas
3. **Adapter**: NoticiaAdapter para RecyclerView
4. **Template Method**: BaseActivity define estructura común
5. **Observer**: Listeners para clicks en adaptadores

---

**Proyecto**: GeoNews - Noticias Locales de Ibarra
**Versión**: 0.1.0 (Sin módulo de Eventos)
**Fecha**: Enero 2026
