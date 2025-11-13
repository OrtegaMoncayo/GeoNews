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
import com.google.firebase.firestore.GeoPoint;
import com.google.firebase.firestore.Query;
import com.google.firebase.firestore.QuerySnapshot;
import com.tesistitulacion.noticiaslocales.modelo.Categoria;
import com.tesistitulacion.noticiaslocales.modelo.Evento;
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
 * Maneja CRUD de: Noticias, Eventos, Parroquias, Categorías, Usuarios
 */
public class FirebaseManager {

    private static final String TAG = "FirebaseManager";
    private static FirebaseManager instance;
    private final FirebaseFirestore db;

    // Nombres de colecciones
    public static final String COLLECTION_NOTICIAS = "noticias";
    public static final String COLLECTION_EVENTOS = "eventos";
    public static final String COLLECTION_PARROQUIAS = "parroquias";
    public static final String COLLECTION_CATEGORIAS = "categorias";
    public static final String COLLECTION_USUARIOS = "usuarios";
    public static final String COLLECTION_NOTIFICACIONES = "notificaciones";

    // Constructor privado (Singleton)
    private FirebaseManager() {
        db = FirebaseFirestore.getInstance();
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
     */
    public void getAllNoticias(final FirestoreCallback<List<Noticia>> callback) {
        Log.d(TAG, "Iniciando consulta de noticias...");

        db.collection(COLLECTION_NOTICIAS)
                .limit(50)
                .get()
                .addOnSuccessListener(new OnSuccessListener<QuerySnapshot>() {
                    @Override
                    public void onSuccess(QuerySnapshot queryDocumentSnapshots) {
                        Log.d(TAG, "Consulta exitosa. Documentos recibidos: " + queryDocumentSnapshots.size());
                        List<Noticia> noticias = new ArrayList<>();

                        for (DocumentSnapshot doc : queryDocumentSnapshots) {
                            Log.d(TAG, "Procesando documento: " + doc.getId());
                            Noticia n = documentToNoticia(doc);
                            if (n != null) {
                                noticias.add(n);
                                Log.d(TAG, "Noticia agregada: " + n.getTitulo());
                            } else {
                                Log.w(TAG, "Noticia null para documento: " + doc.getId());
                            }
                        }

                        Log.d(TAG, "Total noticias procesadas: " + noticias.size());
                        callback.onSuccess(noticias);
                    }
                })
                .addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        Log.e(TAG, "Error al obtener noticias", e);
                        e.printStackTrace();
                        callback.onError(e);
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

    /**
     * Obtiene un evento por ID
     */
    public void getEventoById(String eventoId, final FirestoreCallback<Evento> callback) {
        db.collection(COLLECTION_EVENTOS)
                .document(eventoId)
                .get()
                .addOnSuccessListener(new OnSuccessListener<DocumentSnapshot>() {
                    @Override
                    public void onSuccess(DocumentSnapshot documentSnapshot) {
                        if (documentSnapshot.exists()) {
                            Evento evento = documentToEvento(documentSnapshot);
                            callback.onSuccess(evento);
                        } else {
                            callback.onError(new Exception("Evento no encontrado"));
                        }
                    }
                })
                .addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        Log.e(TAG, "Error al obtener evento", e);
                        callback.onError(e);
                    }
                });
    }

    /**
     * Obtiene todos los eventos futuros
     */
    public void getEventosFuturos(final FirestoreCallback<List<Evento>> callback) {
        Log.d(TAG, "Iniciando consulta de eventos...");

        db.collection(COLLECTION_EVENTOS)
                .limit(50)
                .get()
                .addOnSuccessListener(new OnSuccessListener<QuerySnapshot>() {
                    @Override
                    public void onSuccess(QuerySnapshot queryDocumentSnapshots) {
                        Log.d(TAG, "Consulta exitosa. Documentos de eventos recibidos: " + queryDocumentSnapshots.size());
                        List<Evento> eventos = new ArrayList<>();

                        for (DocumentSnapshot doc : queryDocumentSnapshots) {
                            Log.d(TAG, "Procesando evento: " + doc.getId());
                            Evento e = documentToEvento(doc);
                            if (e != null) {
                                eventos.add(e);
                                Log.d(TAG, "Evento agregado: " + e.getDescripcion());
                            } else {
                                Log.w(TAG, "Evento null para documento: " + doc.getId());
                            }
                        }

                        Log.d(TAG, "Total eventos procesados: " + eventos.size());
                        callback.onSuccess(eventos);
                    }
                })
                .addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        Log.e(TAG, "Error al obtener eventos", e);
                        e.printStackTrace();
                        callback.onError(e);
                    }
                });
    }

    /**
     * Crea un nuevo evento
     */
    public void createEvento(Evento evento, final FirestoreCallback<String> callback) {
        Map<String, Object> eventoMap = eventoToMap(evento);

        db.collection(COLLECTION_EVENTOS)
                .add(eventoMap)
                .addOnSuccessListener(new OnSuccessListener<DocumentReference>() {
                    @Override
                    public void onSuccess(DocumentReference documentReference) {
                        String eventoId = documentReference.getId();
                        Log.d(TAG, "Evento creado con ID: " + eventoId);
                        callback.onSuccess(eventoId);
                    }
                })
                .addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        Log.e(TAG, "Error al crear evento", e);
                        callback.onError(e);
                    }
                });
    }

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
     */
    private Noticia documentToNoticia(DocumentSnapshot doc) {
        try {
            Log.d(TAG, "Convirtiendo documento: " + doc.getId());
            Noticia n = new Noticia();

            // Guardar el ID de Firestore
            n.setFirestoreId(doc.getId());

            // Campos de texto
            n.setTitulo(doc.getString("titulo"));
            n.setDescripcion(doc.getString("descripcion"));
            n.setContenido(doc.getString("contenido"));
            n.setImagenUrl(doc.getString("imagenUrl"));

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

            // Referencias de Firestore (DocumentReference) a IDs Integer
            // CategoriaId
            DocumentReference categoriaRef = doc.getDocumentReference("categoriaId");
            if (categoriaRef != null) {
                try {
                    // Extraer el ID del path de la referencia
                    String categoriaId = categoriaRef.getId();
                    n.setCategoriaId(Integer.parseInt(categoriaId));
                } catch (NumberFormatException e) {
                    Log.w(TAG, "No se pudo convertir categoriaId a Integer: " + e.getMessage());
                }
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
     * Convierte DocumentSnapshot a Evento
     */
    private Evento documentToEvento(DocumentSnapshot doc) {
        try {
            Evento e = new Evento();

            // Guardar el ID de Firestore
            e.setFirestoreId(doc.getId());

            // Campos de texto
            e.setDescripcion(doc.getString("descripcion"));
            e.setCategoriaEvento(doc.getString("categoriaEvento"));
            e.setEstado(doc.getString("estado"));

            // Ubicación de texto (Firestore usa "ubicacionTexto", modelo usa "ubicacion")
            String ubicacionTexto = doc.getString("ubicacionTexto");
            if (ubicacionTexto != null) {
                e.setUbicacion(ubicacionTexto);
            }

            // GeoPoint para latitud/longitud
            GeoPoint geoPoint = doc.getGeoPoint("ubicacion");
            if (geoPoint != null) {
                e.setLatitud(geoPoint.getLatitude());
                e.setLongitud(geoPoint.getLongitude());
            }

            // Fechas (Firestore guarda como Date, convertir a Long timestamp)
            Date fecha = doc.getDate("fecha");
            if (fecha != null) {
                e.setFecha(fecha.getTime());
            }

            Date fechaCreacion = doc.getDate("fechaCreacion");
            if (fechaCreacion != null) {
                e.setFechaCreacion(fechaCreacion.getTime());
            }

            // Números
            Long cupoMaximo = doc.getLong("cupoMaximo");
            if (cupoMaximo != null) {
                e.setCupoMaximo(cupoMaximo.intValue());
            }

            Long cupoActual = doc.getLong("cupoActual");
            if (cupoActual != null) {
                e.setCupoActual(cupoActual.intValue());
            }

            Double costo = doc.getDouble("costo");
            if (costo != null) {
                e.setCosto(costo);
            }

            // Referencias de Firestore (DocumentReference) a IDs Integer
            DocumentReference creadorRef = doc.getDocumentReference("creadorId");
            if (creadorRef != null) {
                try {
                    String creadorId = creadorRef.getId();
                    e.setCreadorId(Integer.parseInt(creadorId));
                } catch (NumberFormatException ex) {
                    Log.w(TAG, "No se pudo convertir creadorId a Integer: " + ex.getMessage());
                }
            }

            DocumentReference parroquiaRef = doc.getDocumentReference("parroquiaId");
            if (parroquiaRef != null) {
                try {
                    String parroquiaId = parroquiaRef.getId();
                    e.setParroquiaId(Integer.parseInt(parroquiaId));
                } catch (NumberFormatException ex) {
                    Log.w(TAG, "No se pudo convertir parroquiaId a Integer: " + ex.getMessage());
                }
            }

            return e;
        } catch (Exception ex) {
            Log.e(TAG, "Error convirtiendo documento a Evento", ex);
            return null;
        }
    }

    /**
     * Convierte Noticia a Map para Firestore
     */
    private Map<String, Object> noticiaToMap(Noticia noticia) {
        Map<String, Object> map = new HashMap<>();
        map.put("titulo", noticia.getTitulo());
        map.put("descripcion", noticia.getDescripcion());
        map.put("contenido", noticia.getContenido());
        map.put("imagenUrl", noticia.getImagenUrl());
        map.put("ubicacionTexto", noticia.getUbicacion());

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

    /**
     * Convierte Evento a Map para Firestore
     */
    private Map<String, Object> eventoToMap(Evento evento) {
        Map<String, Object> map = new HashMap<>();
        map.put("descripcion", evento.getDescripcion());
        map.put("fecha", evento.getFecha());
        map.put("ubicacionTexto", evento.getUbicacion());
        map.put("categoriaEvento", evento.getCategoriaEvento());
        map.put("cupoMaximo", evento.getCupoMaximo());
        map.put("cupoActual", evento.getCupoActual());
        map.put("costo", evento.getCosto());
        map.put("estado", evento.getEstado());

        if (evento.getLatitud() != null && evento.getLongitud() != null) {
            map.put("ubicacion", new GeoPoint(evento.getLatitud(), evento.getLongitud()));
        }

        return map;
    }

    // ==================== CALLBACK INTERFACE ====================

    /**
     * Interface genérica para callbacks de Firestore
     */
    public interface FirestoreCallback<T> {
        void onSuccess(T result);
        void onError(Exception e);
    }
}
