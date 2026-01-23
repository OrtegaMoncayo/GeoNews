package com.tesistitulacion.noticiaslocales.adapters;

import android.location.Location;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;
import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;
import com.bumptech.glide.Glide;
import com.google.android.material.card.MaterialCardView;
import com.tesistitulacion.noticiaslocales.R;
import com.tesistitulacion.noticiaslocales.modelo.Noticia;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;

/**
 * Adaptador para mostrar noticias en carrusel horizontal del mapa
 */
public class NoticiaMapaAdapter extends RecyclerView.Adapter<NoticiaMapaAdapter.ViewHolder> {

    private List<Noticia> noticias;
    private OnNoticiaClickListener listener;
    private Location ubicacionActual;

    public interface OnNoticiaClickListener {
        void onNoticiaClick(Noticia noticia, int position);
    }

    public NoticiaMapaAdapter(OnNoticiaClickListener listener) {
        this.noticias = new ArrayList<>();
        this.listener = listener;
    }

    public void setUbicacionActual(Location ubicacion) {
        this.ubicacionActual = ubicacion;
    }

    @android.annotation.SuppressLint("NotifyDataSetChanged")
    public void setNoticias(List<Noticia> noticias) {
        this.noticias = noticias != null ? noticias : new ArrayList<>();

        // Ordenar por distancia si hay ubicación actual
        if (ubicacionActual != null && !this.noticias.isEmpty()) {
            ordenarPorDistancia();
        }

        notifyDataSetChanged();
    }

    /**
     * Ordena las noticias por distancia (más cercanas primero)
     */
    private void ordenarPorDistancia() {
        if (ubicacionActual == null) return;

        Collections.sort(noticias, new Comparator<Noticia>() {
            @Override
            public int compare(Noticia n1, Noticia n2) {
                // Noticias sin coordenadas van al final
                if (n1.getLatitud() == null || n1.getLongitud() == null) return 1;
                if (n2.getLatitud() == null || n2.getLongitud() == null) return -1;

                // Calcular distancia de n1
                float[] results1 = new float[1];
                Location.distanceBetween(
                    ubicacionActual.getLatitude(),
                    ubicacionActual.getLongitude(),
                    n1.getLatitud(),
                    n1.getLongitud(),
                    results1
                );

                // Calcular distancia de n2
                float[] results2 = new float[1];
                Location.distanceBetween(
                    ubicacionActual.getLatitude(),
                    ubicacionActual.getLongitude(),
                    n2.getLatitud(),
                    n2.getLongitud(),
                    results2
                );

                // Ordenar ascendente (más cercanas primero)
                return Float.compare(results1[0], results2[0]);
            }
        });
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.item_noticia_mapa, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        Noticia noticia = noticias.get(position);
        holder.bind(noticia);
    }

    @Override
    public int getItemCount() {
        return noticias.size();
    }

    class ViewHolder extends RecyclerView.ViewHolder {
        MaterialCardView cardNoticia;
        TextView tvCategoria;
        TextView tvTitulo;
        TextView tvMetadata;
        TextView tvUbicacion;
        ImageView ivImagen;

        ViewHolder(View itemView) {
            super(itemView);
            cardNoticia = itemView.findViewById(R.id.card_noticia);
            tvCategoria = itemView.findViewById(R.id.tv_categoria);
            tvTitulo = itemView.findViewById(R.id.tv_titulo);
            tvMetadata = itemView.findViewById(R.id.tv_metadata);
            tvUbicacion = itemView.findViewById(R.id.tv_ubicacion);
            ivImagen = itemView.findViewById(R.id.iv_imagen);
        }

        void bind(Noticia noticia) {
            // Categoría
            if (noticia.getCategoriaNombre() != null) {
                tvCategoria.setText(noticia.getCategoriaNombre().toUpperCase(java.util.Locale.ROOT));
            } else {
                tvCategoria.setText("GENERAL");
            }

            // Título
            tvTitulo.setText(noticia.getTitulo());

            // Metadata (distancia si hay ubicación)
            if (ubicacionActual != null && noticia.getLatitud() != null && noticia.getLongitud() != null) {
                float[] results = new float[1];
                Location.distanceBetween(
                        ubicacionActual.getLatitude(),
                        ubicacionActual.getLongitude(),
                        noticia.getLatitud(),
                        noticia.getLongitud(),
                        results
                );
                double distanciaKm = results[0] / 1000.0;
                if (distanciaKm < 1) {
                    tvMetadata.setText(String.format(java.util.Locale.US, "%.0f m", results[0]));
                } else {
                    tvMetadata.setText(String.format(java.util.Locale.US, "%.1f km", distanciaKm));
                }
            } else {
                tvMetadata.setText("Ahora");
            }

            // Ubicación (parroquia)
            if (tvUbicacion != null) {
                String ubicacion = noticia.getParroquiaNombre();
                if (ubicacion != null && !ubicacion.isEmpty()) {
                    tvUbicacion.setText(ubicacion);
                } else {
                    tvUbicacion.setText("Ibarra");
                }
            }

            // Imagen
            if (noticia.getImagenUrl() != null && !noticia.getImagenUrl().isEmpty()) {
                Glide.with(itemView.getContext())
                        .load(noticia.getImagenUrl())
                        .placeholder(R.color.bg_secondary)
                        .error(R.color.bg_secondary)
                        .centerCrop()
                        .into(ivImagen);
            } else {
                ivImagen.setBackgroundColor(itemView.getContext().getResources().getColor(R.color.bg_secondary, null));
            }

            // Click listener
            cardNoticia.setOnClickListener(v -> {
                if (listener != null) {
                    listener.onNoticiaClick(noticia, getAdapterPosition());
                }
            });
        }
    }
}
