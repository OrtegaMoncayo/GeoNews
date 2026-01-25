package com.tesistitulacion.noticiaslocales.adapters;

import android.animation.ObjectAnimator;
import android.animation.ValueAnimator;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.tesistitulacion.noticiaslocales.R;

/**
 * Adapter para mostrar skeleton placeholders mientras cargan los datos
 * Incluye animación shimmer para feedback visual
 */
public class SkeletonAdapter extends RecyclerView.Adapter<SkeletonAdapter.SkeletonViewHolder> {

    private int itemCount;
    private boolean animacionActiva = true;

    /**
     * Constructor con cantidad de items por defecto
     */
    public SkeletonAdapter() {
        this(5); // 5 skeletons por defecto
    }

    /**
     * Constructor con cantidad de items personalizada
     * @param itemCount Cantidad de skeleton items a mostrar
     */
    public SkeletonAdapter(int itemCount) {
        this.itemCount = itemCount;
    }

    @NonNull
    @Override
    public SkeletonViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.item_noticia_skeleton, parent, false);
        return new SkeletonViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull SkeletonViewHolder holder, int position) {
        if (animacionActiva) {
            holder.iniciarAnimacion();
        }
    }

    @Override
    public int getItemCount() {
        return itemCount;
    }

    /**
     * Cambia la cantidad de items skeleton
     */
    public void setItemCount(int count) {
        this.itemCount = count;
        notifyDataSetChanged();
    }

    /**
     * Activa/desactiva la animación shimmer
     */
    public void setAnimacionActiva(boolean activa) {
        this.animacionActiva = activa;
        notifyDataSetChanged();
    }

    /**
     * ViewHolder para skeleton items
     */
    static class SkeletonViewHolder extends RecyclerView.ViewHolder {

        private final View skeletonImagen;
        private final View skeletonCategoria;
        private final View skeletonTitulo1;
        private final View skeletonTitulo2;
        private final View skeletonDesc1;
        private final View skeletonDesc2;
        private final View skeletonUbicacion;
        private final View skeletonFecha;

        private ObjectAnimator shimmerAnimator;

        public SkeletonViewHolder(@NonNull View itemView) {
            super(itemView);

            skeletonImagen = itemView.findViewById(R.id.skeleton_imagen);
            skeletonCategoria = itemView.findViewById(R.id.skeleton_categoria);
            skeletonTitulo1 = itemView.findViewById(R.id.skeleton_titulo_1);
            skeletonTitulo2 = itemView.findViewById(R.id.skeleton_titulo_2);
            skeletonDesc1 = itemView.findViewById(R.id.skeleton_desc_1);
            skeletonDesc2 = itemView.findViewById(R.id.skeleton_desc_2);
            skeletonUbicacion = itemView.findViewById(R.id.skeleton_ubicacion);
            skeletonFecha = itemView.findViewById(R.id.skeleton_fecha);
        }

        /**
         * Inicia la animación shimmer (pulse de alpha)
         */
        public void iniciarAnimacion() {
            // Cancelar animación anterior si existe
            if (shimmerAnimator != null) {
                shimmerAnimator.cancel();
            }

            // Animar todos los elementos con efecto pulse
            View[] elementos = {
                skeletonImagen, skeletonCategoria, skeletonTitulo1, skeletonTitulo2,
                skeletonDesc1, skeletonDesc2, skeletonUbicacion, skeletonFecha
            };

            for (int i = 0; i < elementos.length; i++) {
                View elemento = elementos[i];
                if (elemento != null) {
                    // Delay escalonado para efecto onda
                    int delay = i * 50;

                    elemento.postDelayed(() -> {
                        ObjectAnimator animator = ObjectAnimator.ofFloat(elemento, "alpha", 1f, 0.4f, 1f);
                        animator.setDuration(1200);
                        animator.setRepeatCount(ValueAnimator.INFINITE);
                        animator.setRepeatMode(ValueAnimator.RESTART);
                        animator.start();
                    }, delay);
                }
            }
        }

        /**
         * Detiene la animación
         */
        public void detenerAnimacion() {
            if (shimmerAnimator != null) {
                shimmerAnimator.cancel();
                shimmerAnimator = null;
            }
        }
    }
}
