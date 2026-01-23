package com.tesistitulacion.noticiaslocales.firebase;

import android.util.Log;

import androidx.annotation.NonNull;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.FirebaseFirestoreSettings;
import com.google.firebase.firestore.GeoPoint;
import com.google.firebase.firestore.Query;
import com.google.firebase.firestore.QuerySnapshot;
import com.tesistitulacion.noticiaslocales.modelo.Categoria;
import com.tesistitulacion.noticiaslocales.modelo.Noticia;
import com.tesistitulacion.noticiaslocales.modelo.Parroquia;
import com.tesistitulacion.noticiaslocales.modelo.Usuario;

import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Clase Helper para operaciones con Firebase Firestore
 * Maneja CRUD de: Noticias, Parroquias, Categorías, Usuarios
 */
public class FirebaseManager {

    private static final String TAG = "FirebaseManager";
    private static FirebaseManager instance;
    private final FirebaseFirestore db;

    // Nombres de colecciones
    public static final String COLLECTION_NOTICIAS = "noticias";
    public static final String COLLECTION_PARROQUIAS = "parroquias";
    public static final String COLLECTION_CATEGORIAS = "categorias";
    public static final String COLLECTION_USUARIOS = "usuarios";
    public static final String COLLECTION_NOTIFICACIONES = "notificaciones";

    // Constructor privado (Singleton)
    private FirebaseManager() {
        db = FirebaseFirestore.getInstance();

        // Habilitar persistencia offline para carga más rápida
        FirebaseFirestoreSettings settings = new FirebaseFirestoreSettings.Builder()
                .setPersistenceEnabled(true)
                .setCacheSizeBytes(50 * 1024 * 1024) // 50 MB de caché
                .build();
        db.setFirestoreSettings(settings);
        Log.d(TAG, "Firestore configurado con persistencia offline (50MB caché)");
    }

    // Singleton getInstance
    public static synchronized FirebaseManager getInstance() {
        if (instance == null) {
            instance = new FirebaseManager();
        }
        return instance;
    }

    // ==================== PARROQUIAS ====================

    /**
     * Obtiene todas las parroquias
     */
    public void getAllParroquias(final FirestoreCallback<List<Parroquia>> callback) {
        db.collection(COLLECTION_PARROQUIAS)
                .orderBy("nombre")
                .get()
                .addOnSuccessListener(new OnSuccessListener<QuerySnapshot>() {
                    @Override
                    public void onSuccess(QuerySnapshot queryDocumentSnapshots) {
                        List<Parroquia> parroquias = new ArrayList<>();
                        for (DocumentSnapshot doc : queryDocumentSnapshots) {
                            Parroquia p = documentToParroquia(doc);
                            if (p != null) parroquias.add(p);
                        }
                        callback.onSuccess(parroquias);
                    }
                })
                .addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        Log.e(TAG, "Error al obtener parroquias", e);
                        callback.onError(e);
                    }
                });
    }

    /**
     * Obtiene parroquias por tipo (urbana/rural)
     */
    public void getParroquiasByTipo(String tipo, final FirestoreCallback<List<Parroquia>> callback) {
        db.collection(COLLECTION_PARROQUIAS)
                .whereEqualTo("tipo", tipo)
                .orderBy("nombre")
                .get()
                .addOnSuccessListener(new OnSuccessListener<QuerySnapshot>() {
                    @Override
                    public void onSuccess(QuerySnapshot queryDocumentSnapshots) {
                        List<Parroquia> parroquias = new ArrayList<>();
                        for (DocumentSnapshot doc : queryDocumentSnapshots) {
                            Parroquia p = documentToParroquia(doc);
                            if (p != null) parroquias.add(p);
                        }
                        callback.onSuccess(parroquias);
                    }
                })
                .addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        Log.e(TAG, "Error al obtener parroquias por tipo", e);
                        callback.onError(e);
                    }
                });
    }

    // ==================== CATEGORÍAS ====================

    /**
     * Obtiene todas las categorías activas
     */
    public void getAllCategorias(final FirestoreCallback<List<Categoria>> callback) {
        db.collection(COLLECTION_CATEGORIAS)
                .whereEqualTo("activa", true)
                .orderBy("orden")
                .get()
                .addOnSuccessListener(new OnSuccessListener<QuerySnapshot>() {
                    @Override
                    public void onSuccess(QuerySnapshot queryDocumentSnapshots) {
                        List<Categoria> categorias = new ArrayList<>();
                        for (DocumentSnapshot doc : queryDocumentSnapshots) {
                            Categoria c = doc.toObject(Categoria.class);
                            if (c != null) categorias.add(c);
                        }
                        callback.onSuccess(categorias);
                    }
                })
                .addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        Log.e(TAG, "Error al obtener categorías", e);
                        callback.onError(e);
                    }
                });
    }

    // ==================== NOTICIAS ====================

    /**
     * Obtiene todas las noticias activas (ordenadas por fecha)
     * NOTA: Esta es una consulta única. Para actualizaciones en tiempo real, usa getAllNoticiasRealtime()
     */
    public void getAllNoticias(final FirestoreCallback<List<Noticia>> callback) {
        db.collection(COLLECTION_NOTICIAS)
                .orderBy("fechaPublicacion", Query.Direction.DESCENDING)
                .limit(20) // Optimizado: solo 20 noticias iniciales
                .get()
                .addOnSuccessListener(queryDocumentSnapshots -> {
                    List<Noticia> noticias = new ArrayList<>();
                    for (DocumentSnapshot doc : queryDocumentSnapshots) {
                        Noticia n = documentToNoticia(doc);
                        if (n != null) {
                            noticias.add(n);
                        }
                    }
                    callback.onSuccess(noticias);
                })
                .addOnFailureListener(e -> {
                    Log.e(TAG, "Error al obtener noticias", e);
                    callback.onError(e);
                });
    }

    /**
     * Obtiene todas las noticias activas con actualizaciones en TIEMPO REAL
     * Se actualiza automáticamente cuando hay cambios en Firebase
     */
    public void getAllNoticiasRealtime(final FirestoreCallback<List<Noticia>> callback) {
        db.collection(COLLECTION_NOTICIAS)
                .orderBy("fechaPublicacion", Query.Direction.DESCENDING)
                .limit(20) // Optimizado: solo 20 noticias
                .addSnapshotListener((querySnapshot, error) -> {
                    if (error != null) {
                        Log.e(TAG, "Error en listener de noticias", error);
                        callback.onError(error);
                        return;
                    }

                    if (querySnapshot != null) {
                        List<Noticia> noticias = new ArrayList<>();
                        for (DocumentSnapshot doc : querySnapshot) {
                            Noticia n = documentToNoticia(doc);
                            if (n != null) {
                                noticias.add(n);
                            }
                        }
                        callback.onSuccess(noticias);
                    } else {
                        callback.onSuccess(new ArrayList<>());
                    }
                });
    }

    /**
     * Obtiene noticias destacadas
     */
    public void getNoticiasDestacadas(final FirestoreCallback<List<Noticia>> callback) {
        db.collection(COLLECTION_NOTICIAS)
                .whereEqualTo("activa", true)
                .whereEqualTo("destacada", true)
                .orderBy("fechaPublicacion", Query.Direction.DESCENDING)
                .limit(10)
                .get()
                .addOnSuccessListener(new OnSuccessListener<QuerySnapshot>() {
                    @Override
                    public void onSuccess(QuerySnapshot queryDocumentSnapshots) {
                        List<Noticia> noticias = new ArrayList<>();
                        for (DocumentSnapshot doc : queryDocumentSnapshots) {
                            Noticia n = documentToNoticia(doc);
                            if (n != null) noticias.add(n);
                        }
                        callback.onSuccess(noticias);
                    }
                })
                .addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        Log.e(TAG, "Error al obtener noticias destacadas", e);
                        callback.onError(e);
                    }
                });
    }

    /**
     * Obtiene una noticia por ID
     */
    public void getNoticiaById(String noticiaId, final FirestoreCallback<Noticia> callback) {
        db.collection(COLLECTION_NOTICIAS)
                .document(noticiaId)
                .get()
                .addOnSuccessListener(new OnSuccessListener<DocumentSnapshot>() {
                    @Override
                    public void onSuccess(DocumentSnapshot documentSnapshot) {
                        if (documentSnapshot.exists()) {
                            Noticia noticia = documentToNoticia(documentSnapshot);
                            callback.onSuccess(noticia);
                        } else {
                            callback.onError(new Exception("Noticia no encontrada"));
                        }
                    }
                })
                .addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        Log.e(TAG, "Error al obtener noticia", e);
                        callback.onError(e);
                    }
                });
    }

    /**
     * Incrementa el contador de visualizaciones de una noticia
     */
    public void incrementarVisualizaciones(String noticiaId) {
        if (noticiaId == null || noticiaId.isEmpty()) {
            Log.w(TAG, "noticiaId es null o vacío, no se puede incrementar visualizaciones");
            return;
        }

        DocumentReference noticiaRef = db.collection(COLLECTION_NOTICIAS).document(noticiaId);

        // Primero obtener el valor actual
        noticiaRef.get().addOnSuccessListener(new OnSuccessListener<DocumentSnapshot>() {
            @Override
            public void onSuccess(DocumentSnapshot documentSnapshot) {
                if (documentSnapshot.exists()) {
                    Integer visualizacionesActuales = documentSnapshot.getLong("visualizaciones") != null ?
                            documentSnapshot.getLong("visualizaciones").intValue() : 0;

                    // Incrementar en 1
                    Map<String, Object> updates = new HashMap<>();
                    updates.put("visualizaciones", visualizacionesActuales + 1);

                    noticiaRef.update(updates)
                            .addOnSuccessListener(aVoid -> Log.d(TAG, "Visualizaciones incrementadas a: " + (visualizacionesActuales + 1)))
                            .addOnFailureListener(e -> Log.e(TAG, "Error al incrementar visualizaciones", e));
                } else {
                    Log.w(TAG, "Noticia no encontrada al incrementar visualizaciones: " + noticiaId);
                }
            }
        }).addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception e) {
                Log.e(TAG, "Error al obtener noticia para incrementar visualizaciones", e);
            }
        });
    }

    /**
     * Incrementa el contador de noticias leídas del usuario
     */
    public void incrementarNoticiasLeidas(String userId) {
        if (userId == null || userId.isEmpty()) {
            Log.w(TAG, "userId es null o vacío, no se puede incrementar noticias leídas");
            return;
        }

        DocumentReference usuarioRef = db.collection(COLLECTION_USUARIOS).document(userId);

        // Primero obtener el valor actual
        usuarioRef.get().addOnSuccessListener(new OnSuccessListener<DocumentSnapshot>() {
            @Override
            public void onSuccess(DocumentSnapshot documentSnapshot) {
                if (documentSnapshot.exists()) {
                    Integer noticiasLeidasActuales = documentSnapshot.getLong("noticiasLeidas") != null ?
                            documentSnapshot.getLong("noticiasLeidas").intValue() : 0;

                    // Incrementar en 1
                    Map<String, Object> updates = new HashMap<>();
                    updates.put("noticiasLeidas", noticiasLeidasActuales + 1);

                    usuarioRef.update(updates)
                            .addOnSuccessListener(aVoid -> Log.d(TAG, "Noticias leídas incrementadas a: " + (noticiasLeidasActuales + 1)))
                            .addOnFailureListener(e -> Log.e(TAG, "Error al incrementar noticias leídas", e));
                } else {
                    Log.w(TAG, "Usuario no encontrado al incrementar noticias leídas: " + userId);
                }
            }
        }).addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception e) {
                Log.e(TAG, "Error al obtener usuario para incrementar noticias leídas", e);
            }
        });
    }

    /**
     * Crea una nueva noticia
     */
    public void createNoticia(Noticia noticia, final FirestoreCallback<String> callback) {
        Map<String, Object> noticiaMap = noticiaToMap(noticia);

        db.collection(COLLECTION_NOTICIAS)
                .add(noticiaMap)
                .addOnSuccessListener(new OnSuccessListener<DocumentReference>() {
                    @Override
                    public void onSuccess(DocumentReference documentReference) {
                        String noticiaId = documentReference.getId();
                        Log.d(TAG, "Noticia creada con ID: " + noticiaId);
                        callback.onSuccess(noticiaId);
                    }
                })
                .addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        Log.e(TAG, "Error al crear noticia", e);
                        callback.onError(e);
                    }
                });
    }

    // ==================== EVENTOS ====================

    // ==================== CONVERSORES ====================

    /**
     * Convierte DocumentSnapshot a Parroquia
     */
    private Parroquia documentToParroquia(DocumentSnapshot doc) {
        try {
            Parroquia p = new Parroquia();
            p.setId(Integer.parseInt(doc.getString("id")));
            p.setNombre(doc.getString("nombre"));
            p.setTipo(doc.getString("tipo"));
            p.setDescripcion(doc.getString("descripcion"));

            // Obtener ubicación como GeoPoint
            GeoPoint ubicacion = doc.getGeoPoint("ubicacion");
            if (ubicacion != null) {
                p.setLatitud(ubicacion.getLatitude());
                p.setLongitud(ubicacion.getLongitude());
            }

            Long poblacion = doc.getLong("poblacion");
            if (poblacion != null) {
                p.setPoblacion(poblacion.intValue());
            }

            return p;
        } catch (Exception e) {
            Log.e(TAG, "Error convirtiendo documento a Parroquia", e);
            return null;
        }
    }

    /**
     * Convierte DocumentSnapshot a Noticia
     * Soporta campos multiidioma (titulo_es, titulo_en, etc.)
     */
    private Noticia documentToNoticia(DocumentSnapshot doc) {
        try {
            Log.d(TAG, "Convirtiendo documento: " + doc.getId());
            Noticia n = new Noticia();

            // Guardar el ID de Firestore
            n.setFirestoreId(doc.getId());

            // Campos de texto (por defecto/fallback)
            n.setTitulo(doc.getString("titulo"));
            n.setDescripcion(doc.getString("descripcion"));
            n.setContenido(doc.getString("contenido"));
            n.setImagenUrl(doc.getString("imagenUrl"));

            // Campos multiidioma - Español
            n.setTitulo_es(doc.getString("titulo_es"));
            n.setDescripcion_es(doc.getString("descripcion_es"));
            n.setContenido_es(doc.getString("contenido_es"));

            // Campos multiidioma - Inglés
            n.setTitulo_en(doc.getString("titulo_en"));
            n.setDescripcion_en(doc.getString("descripcion_en"));
            n.setContenido_en(doc.getString("contenido_en"));

            Log.d(TAG, "Título: " + doc.getString("titulo"));

            // Ubicación de texto (Firestore usa "ubicacionTexto", modelo usa "ubicacion")
            String ubicacionTexto = doc.getString("ubicacionTexto");
            if (ubicacionTexto != null) {
                n.setUbicacion(ubicacionTexto);
            }

            // GeoPoint para latitud/longitud
            GeoPoint geoPoint = doc.getGeoPoint("ubicacion");
            if (geoPoint != null) {
                n.setLatitud(geoPoint.getLatitude());
                n.setLongitud(geoPoint.getLongitude());
            }

            // Fechas (Firestore guarda como Date, convertir a Long timestamp)
            Date fechaPub = doc.getDate("fechaPublicacion");
            if (fechaPub != null) {
                n.setFechaCreacion(fechaPub.getTime());
            }

            Date fechaCreacion = doc.getDate("fechaCreacion");
            if (fechaCreacion != null) {
                n.setFechaCreacion(fechaCreacion.getTime());
            }

            // Booleanos
            Boolean destacada = doc.getBoolean("destacada");
            if (destacada != null) {
                n.setDestacada(destacada);
            }

            // Convertir "activa" booleano de Firestore a "estado" String del modelo
            Boolean activa = doc.getBoolean("activa");
            if (activa != null) {
                n.setEstado(activa ? "published" : "archived");
            }

            // Números
            Long visualizaciones = doc.getLong("visualizaciones");
            if (visualizaciones != null) {
                n.setVisualizaciones(visualizaciones.intValue());
            }

            // CategoriaId - puede ser DocumentReference o String
            try {
                Object categoriaIdObj = doc.get("categoriaId");
                if (categoriaIdObj != null) {
                    if (categoriaIdObj instanceof DocumentReference) {
                        // Es una referencia de Firestore
                        DocumentReference categoriaRef = (DocumentReference) categoriaIdObj;
                        String categoriaId = categoriaRef.getId();
                        n.setCategoriaId(Integer.parseInt(categoriaId));
                    } else if (categoriaIdObj instanceof String) {
                        // Es un string directo
                        n.setCategoriaId(Integer.parseInt((String) categoriaIdObj));
                    } else if (categoriaIdObj instanceof Long) {
                        // Es un número
                        n.setCategoriaId(((Long) categoriaIdObj).intValue());
                    } else if (categoriaIdObj instanceof Integer) {
                        // Es un integer
                        n.setCategoriaId((Integer) categoriaIdObj);
                    }
                }
            } catch (Exception e) {
                Log.w(TAG, "No se pudo convertir categoriaId: " + e.getMessage());
            }

            // ParroquiaId - puede ser DocumentReference o String
            try {
                Object parroquiaIdObj = doc.get("parroquiaId");
                if (parroquiaIdObj != null) {
                    if (parroquiaIdObj instanceof DocumentReference) {
                        // Es una referencia de Firestore
                        DocumentReference parroquiaRef = (DocumentReference) parroquiaIdObj;
                        String parroquiaId = parroquiaRef.getId();
                        n.setAutorId(Integer.parseInt(parroquiaId)); // Nota: usando autorId como placeholder
                    } else if (parroquiaIdObj instanceof String) {
                        // Es un string directo - solo lo logueamos, no lo guardamos
                        Log.d(TAG, "ParroquiaId (string): " + parroquiaIdObj);
                    }
                }
            } catch (Exception e) {
                Log.w(TAG, "No se pudo convertir parroquiaId: " + e.getMessage());
            }

            Log.d(TAG, "Noticia convertida exitosamente: " + n.getTitulo());
            return n;
        } catch (Exception e) {
            Log.e(TAG, "Error convirtiendo documento a Noticia: " + doc.getId(), e);
            Log.e(TAG, "Datos del documento: " + doc.getData());
            e.printStackTrace();
            return null;
        }
    }

    /**
     * Convierte Noticia a Map para Firestore
     * Incluye campos multiidioma si están presentes
     */
    private Map<String, Object> noticiaToMap(Noticia noticia) {
        Map<String, Object> map = new HashMap<>();
        map.put("titulo", noticia.getTitulo());
        map.put("descripcion", noticia.getDescripcion());
        map.put("contenido", noticia.getContenido());
        map.put("imagenUrl", noticia.getImagenUrl());
        map.put("ubicacionTexto", noticia.getUbicacion());

        // Campos multiidioma - Español
        if (noticia.getTitulo_es() != null) {
            map.put("titulo_es", noticia.getTitulo_es());
        }
        if (noticia.getDescripcion_es() != null) {
            map.put("descripcion_es", noticia.getDescripcion_es());
        }
        if (noticia.getContenido_es() != null) {
            map.put("contenido_es", noticia.getContenido_es());
        }

        // Campos multiidioma - Inglés
        if (noticia.getTitulo_en() != null) {
            map.put("titulo_en", noticia.getTitulo_en());
        }
        if (noticia.getDescripcion_en() != null) {
            map.put("descripcion_en", noticia.getDescripcion_en());
        }
        if (noticia.getContenido_en() != null) {
            map.put("contenido_en", noticia.getContenido_en());
        }

        // Convertir timestamp Long a Date para Firestore
        if (noticia.getFechaCreacion() != null) {
            map.put("fechaPublicacion", new Date(noticia.getFechaCreacion()));
        } else {
            map.put("fechaPublicacion", new Date());
        }

        map.put("visualizaciones", noticia.getVisualizaciones());
        map.put("destacada", noticia.getDestacada());
        map.put("activa", noticia.getEstado() != null && noticia.getEstado().equals("published"));

        if (noticia.getLatitud() != null && noticia.getLongitud() != null) {
            map.put("ubicacion", new GeoPoint(noticia.getLatitud(), noticia.getLongitud()));
        }

        return map;
    }

    // ==================== USUARIOS ====================

    /**
     * Obtiene un usuario por su ID
     */
    public void obtenerUsuarioPorId(String userId, final FirestoreCallback<Usuario> callback) {
        db.collection(COLLECTION_USUARIOS)
                .document(userId)
                .get()
                .addOnSuccessListener(new OnSuccessListener<DocumentSnapshot>() {
                    @Override
                    public void onSuccess(DocumentSnapshot documentSnapshot) {
                        if (documentSnapshot.exists()) {
                            try {
                                Usuario usuario = new Usuario();

                                // Convertir manualmente los campos para evitar problemas de tipo
                                Map<String, Object> data = documentSnapshot.getData();
                                if (data != null) {
                                    // ID - convertir String a Integer
                                    try {
                                        usuario.setId(Integer.parseInt(documentSnapshot.getId()));
                                    } catch (NumberFormatException e) {
                                        Log.w(TAG, "No se pudo convertir ID a Integer: " + documentSnapshot.getId());
                                    }

                                    // Strings
                                    usuario.setNombre((String) data.get("nombre"));
                                    usuario.setApellido((String) data.get("apellido"));
                                    usuario.setEmail((String) data.get("email"));
                                    usuario.setFotoPerfil((String) data.get("fotoPerfil"));
                                    usuario.setBio((String) data.get("bio"));
                                    usuario.setTelefonocelular((String) data.get("telefonocelular"));
                                    usuario.setUbicacion((String) data.get("ubicacion"));
                                    usuario.setTipoUsuario((String) data.get("tipoUsuario"));
                                    usuario.setCreatedAt((String) data.get("createdAt"));

                                    // Números
                                    Object fechaReg = data.get("fechaRegistro");
                                    if (fechaReg instanceof Long) {
                                        usuario.setFechaRegistro((Long) fechaReg);
                                    }

                                    Object ultimaCon = data.get("ultimaConexion");
                                    if (ultimaCon instanceof Long) {
                                        usuario.setUltimaConexion((Long) ultimaCon);
                                    }

                                    Object noticiasPub = data.get("noticiasPublicadas");
                                    if (noticiasPub instanceof Long) {
                                        usuario.setNoticiasPublicadas(((Long) noticiasPub).intValue());
                                    } else if (noticiasPub instanceof Integer) {
                                        usuario.setNoticiasPublicadas((Integer) noticiasPub);
                                    }

                                    Object noticiasLeid = data.get("noticiasLeidas");
                                    if (noticiasLeid instanceof Long) {
                                        usuario.setNoticiasLeidas(((Long) noticiasLeid).intValue());
                                    } else if (noticiasLeid instanceof Integer) {
                                        usuario.setNoticiasLeidas((Integer) noticiasLeid);
                                    }

                                    // Boolean
                                    Object verificado = data.get("verificado");
                                    if (verificado instanceof Boolean) {
                                        usuario.setVerificado((Boolean) verificado);
                                    }

                                    callback.onSuccess(usuario);
                                } else {
                                    callback.onError(new Exception("Datos del usuario vacíos"));
                                }
                            } catch (Exception e) {
                                Log.e(TAG, "Error al parsear usuario", e);
                                callback.onError(new Exception("Error al convertir documento a Usuario: " + e.getMessage()));
                            }
                        } else {
                            callback.onError(new Exception("Usuario no encontrado"));
                        }
                    }
                })
                .addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        Log.e(TAG, "Error al obtener usuario", e);
                        callback.onError(e);
                    }
                });
    }

    /**
     * Actualiza los datos de un usuario
     */
    public void actualizarUsuario(String userId, Map<String, Object> datos, final FirestoreCallback<Void> callback) {
        db.collection(COLLECTION_USUARIOS)
                .document(userId)
                .update(datos)
                .addOnSuccessListener(new OnSuccessListener<Void>() {
                    @Override
                    public void onSuccess(Void aVoid) {
                        Log.i(TAG, "Usuario actualizado correctamente");
                        callback.onSuccess(null);
                    }
                })
                .addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        Log.e(TAG, "Error al actualizar usuario", e);
                        callback.onError(e);
                    }
                });
    }

    /**
     * Crea o actualiza un usuario completo
     */
    public void guardarUsuario(Usuario usuario, final FirestoreCallback<Void> callback) {
        String userId = String.valueOf(usuario.getId());

        Map<String, Object> usuarioMap = new HashMap<>();
        usuarioMap.put("nombre", usuario.getNombre());
        usuarioMap.put("apellido", usuario.getApellido());
        usuarioMap.put("email", usuario.getEmail());
        usuarioMap.put("fotoPerfil", usuario.getFotoPerfil());
        usuarioMap.put("bio", usuario.getBio());
        usuarioMap.put("telefonocelular", usuario.getTelefonocelular());
        usuarioMap.put("ubicacion", usuario.getUbicacion());
        usuarioMap.put("fechaRegistro", usuario.getFechaRegistro());
        usuarioMap.put("ultimaConexion", usuario.getUltimaConexion());
        usuarioMap.put("noticiasPublicadas", usuario.getNoticiasPublicadas());
        usuarioMap.put("verificado", usuario.getVerificado());
        usuarioMap.put("tipoUsuario", usuario.getTipoUsuario());

        db.collection(COLLECTION_USUARIOS)
                .document(userId)
                .set(usuarioMap)
                .addOnSuccessListener(new OnSuccessListener<Void>() {
                    @Override
                    public void onSuccess(Void aVoid) {
                        Log.i(TAG, "Usuario guardado correctamente");
                        callback.onSuccess(null);
                    }
                })
                .addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        Log.e(TAG, "Error al guardar usuario", e);
                        callback.onError(e);
                    }
                });
    }

    // ==================== CALLBACK INTERFACE ====================

    /**
     * Interface genérica para callbacks de Firestore
     * Soporta tanto Exception como String para errores
     */
    public interface FirestoreCallback<T> {
        void onSuccess(T result);

        // Método principal que las clases deben implementar
        void onError(Exception e);

        // Método opcional para errores como String
        default void onError(String error) {
            onError(new Exception(error));
        }
    }
}
