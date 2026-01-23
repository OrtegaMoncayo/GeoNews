package com.tesistitulacion.noticiaslocales.adapters;

import android.graphics.Color;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;
import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;
import com.squareup.picasso.Picasso;
import com.tesistitulacion.noticiaslocales.R;
import com.tesistitulacion.noticiaslocales.modelo.Noticia;
import com.tesistitulacion.noticiaslocales.utils.LocationHelper;
import com.tesistitulacion.noticiaslocales.utils.UsuarioPreferences;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Locale;

/**
 * Adapter para RecyclerView de Noticias
 * Gestiona la visualización de items de noticias en la lista
 */
public class NoticiaAdapter extends RecyclerView.Adapter<NoticiaAdapter.NoticiaViewHolder> {

    private List<Noticia> noticias;
    private OnNoticiaClickListener listener;

    // Interfaz para manejar clicks en items
    public interface OnNoticiaClickListener {
        void onNoticiaClick(Noticia noticia, int position);
    }

    public NoticiaAdapter(OnNoticiaClickListener listener) {
        this.noticias = new ArrayList<>();
        this.listener = listener;
    }

    @NonNull
    @Override
    public NoticiaViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.item_noticia, parent, false);
        return new NoticiaViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull NoticiaViewHolder holder, int position) {
        Noticia noticia = noticias.get(position);
        holder.bind(noticia, listener);
    }

    @Override
    public int getItemCount() {
        return noticias.size();
    }

    /**
     * Actualiza la lista completa de noticias
     */
    @android.annotation.SuppressLint("NotifyDataSetChanged")
    public void actualizarLista(List<Noticia> nuevasNoticias) {
        this.noticias.clear();
        if (nuevasNoticias != null) {
            this.noticias.addAll(nuevasNoticias);
        }
        notifyDataSetChanged();
    }

    /**
     * Agrega una noticia al inicio de la lista
     */
    public void agregarNoticia(Noticia noticia) {
        this.noticias.add(0, noticia);
        notifyItemInserted(0);
    }

    /**
     * ViewHolder para items de noticia
     */
    static class NoticiaViewHolder extends RecyclerView.ViewHolder {
        private final TextView tvCategoria;
        private final TextView tvTitulo;
        private final TextView tvDescripcion;
        private final TextView tvFecha;
        private final TextView tvUbicacion;
        private final TextView tvDistancia;
        private final ImageView ivImagen;
        private final ImageView ivBookmark;

        public NoticiaViewHolder(@NonNull View itemView) {
            super(itemView);
            tvCategoria = itemView.findViewById(R.id.tv_categoria);
            tvTitulo = itemView.findViewById(R.id.tv_titulo);
            tvDescripcion = itemView.findViewById(R.id.tv_descripcion);
            tvFecha = itemView.findViewById(R.id.tv_fecha);
            tvUbicacion = itemView.findViewById(R.id.tv_ubicacion);
            tvDistancia = itemView.findViewById(R.id.tv_distancia);
            ivImagen = itemView.findViewById(R.id.iv_imagen_noticia);
            ivBookmark = itemView.findViewById(R.id.iv_bookmark);
        }

        public void bind(Noticia noticia, OnNoticiaClickListener listener) {
            if (noticia == null) {
                return; // Protección contra noticias null
            }

            // Título
            String titulo = noticia.getTitulo();
            tvTitulo.setText(titulo != null ? titulo : "Sin título");

            // Descripción
            if (noticia.getDescripcion() != null && !noticia.getDescripcion().isEmpty()) {
                tvDescripcion.setText(noticia.getDescripcion());
                tvDescripcion.setVisibility(View.VISIBLE);
            } else {
                tvDescripcion.setVisibility(View.GONE);
            }

            // Categoría
            String categoriaNombre = obtenerNombreCategoria(noticia.getCategoriaId());
            tvCategoria.setText(categoriaNombre);

            // Color de categoría
            String colorHex = noticia.getColorCategoria();
            try {
                tvCategoria.setTextColor(Color.parseColor(colorHex));
            } catch (Exception e) {
                tvCategoria.setTextColor(Color.parseColor("#1976D2"));
            }

            // Fecha
            String fechaStr = noticia.getFechaPublicacion();
            if (fechaStr != null && !fechaStr.isEmpty()) {
                tvFecha.setText(formatearFecha(fechaStr));
            } else {
                tvFecha.setText("Hoy");
            }

            // Ubicación (parroquia)
            if (noticia.getParroquiaNombre() != null && !noticia.getParroquiaNombre().isEmpty()) {
                tvUbicacion.setText(noticia.getParroquiaNombre());
                tvUbicacion.setVisibility(View.VISIBLE);
            } else {
                tvUbicacion.setVisibility(View.GONE);
            }

            // Distancia (se muestra solo si fue calculada)
            if (noticia.getDistancia() != null && noticia.getDistancia() >= 0) {
                tvDistancia.setText(LocationHelper.formatearDistancia(noticia.getDistancia()));
                tvDistancia.setVisibility(View.VISIBLE);
            } else {
                tvDistancia.setVisibility(View.GONE);
            }

            // Imagen de la noticia
            if (noticia.getImagenUrl() != null && !noticia.getImagenUrl().isEmpty()) {
                ivImagen.setVisibility(View.VISIBLE);
                Picasso.get()
                        .load(noticia.getImagenUrl())
                        .placeholder(R.color.surface_dark)
                        .error(R.color.surface_dark)
                        .fit()
                        .centerCrop()
                        .into(ivImagen);
            } else {
                // Si no hay imagen, mostrar placeholder
                ivImagen.setVisibility(View.VISIBLE);
                ivImagen.setImageResource(R.color.surface_dark);
            }

            // Click listener
            itemView.setOnClickListener(v -> {
                if (listener != null) {
                    listener.onNoticiaClick(noticia, getAdapterPosition());
                }
            });

            // Bookmark: verificar estado y configurar
            // Usar firestoreId si existe, sino usar id numérico
            String noticiaId = noticia.getFirestoreId();
            if (noticiaId == null && noticia.getId() != null) {
                noticiaId = String.valueOf(noticia.getId());
            }
            final String finalNoticiaId = noticiaId;
            if (finalNoticiaId != null && ivBookmark != null) {
                boolean isGuardada = UsuarioPreferences.isNoticiaGuardada(itemView.getContext(), finalNoticiaId);
                actualizarIconoBookmark(isGuardada);

                ivBookmark.setOnClickListener(v -> {
                    boolean guardadaActual = UsuarioPreferences.isNoticiaGuardada(itemView.getContext(), finalNoticiaId);
                    if (guardadaActual) {
                        UsuarioPreferences.eliminarNoticiaGuardada(itemView.getContext(), finalNoticiaId);
                        actualizarIconoBookmark(false);
                    } else {
                        UsuarioPreferences.guardarNoticia(itemView.getContext(), finalNoticiaId);
                        actualizarIconoBookmark(true);
                    }
                });
            }
        }

        /**
         * Actualiza el icono del bookmark según el estado
         */
        private void actualizarIconoBookmark(boolean isGuardada) {
            if (ivBookmark != null) {
                if (isGuardada) {
                    ivBookmark.setImageResource(R.drawable.ic_bookmark_filled);
                    ivBookmark.clearColorFilter(); // Mostrar color dorado del icono
                } else {
                    ivBookmark.setImageResource(R.drawable.ic_bookmark_outline);
                    ivBookmark.setColorFilter(android.graphics.Color.WHITE);
                }
            }
        }

        /**
         * Obtiene el nombre de la categoría según su ID
         */
        private String obtenerNombreCategoria(Integer categoriaId) {
            if (categoriaId == null) return "General";

            switch (categoriaId) {
                case 1: return "Política";
                case 2: return "Economía";
                case 3: return "Cultura";
                case 4: return "Deportes";
                case 5: return "Educación";
                case 6: return "Salud";
                case 7: return "Seguridad";
                case 8: return "Medio Ambiente";
                case 9: return "Turismo";
                case 10: return "Tecnología";
                default: return "General";
            }
        }

        /**
         * Formatea la fecha para mostrar
         */
        private String formatearFecha(String fechaStr) {
            if (fechaStr == null || fechaStr.isEmpty()) {
                return "Hoy";
            }

            try {
                // Formato de entrada: "2025-10-31T14:30:00"
                SimpleDateFormat inputFormat = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss", Locale.getDefault());
                SimpleDateFormat outputFormat = new SimpleDateFormat("dd MMM yyyy", new Locale("es", "ES"));

                Date fecha = inputFormat.parse(fechaStr);
                if (fecha != null) {
                    // Verificar si es hoy
                    Date hoy = new Date();
                    SimpleDateFormat sdfDia = new SimpleDateFormat("yyyyMMdd", Locale.getDefault());

                    if (sdfDia.format(fecha).equals(sdfDia.format(hoy))) {
                        return "Hoy";
                    }

                    return outputFormat.format(fecha);
                }
            } catch (ParseException e) {
                // Si falla el parsing, intentar formato simple
                try {
                    SimpleDateFormat simpleFormat = new SimpleDateFormat("yyyy-MM-dd", Locale.getDefault());
                    Date fecha = simpleFormat.parse(fechaStr.substring(0, Math.min(10, fechaStr.length())));
                    if (fecha != null) {
                        SimpleDateFormat outputFormat = new SimpleDateFormat("dd MMM yyyy", new Locale("es", "ES"));
                        return outputFormat.format(fecha);
                    }
                } catch (Exception ex) {
                    // Ignorar
                }
            }

            return fechaStr.substring(0, Math.min(10, fechaStr.length()));
        }
    }
}
