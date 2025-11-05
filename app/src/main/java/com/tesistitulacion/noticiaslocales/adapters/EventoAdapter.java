package com.tesistitulacion.noticiaslocales.adapters;

import android.graphics.Color;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.tesistitulacion.noticiaslocales.R;
import com.tesistitulacion.noticiaslocales.modelo.Evento;

import java.util.ArrayList;
import java.util.List;

/**
 * Adapter para RecyclerView de Eventos
 * Gestiona la visualización de items de eventos en la lista
 *
 * Características:
 * - ViewHolder Pattern para optimización
 * - Formato de fechas en español
 * - Colores por categoría de evento
 * - Estados visuales (programado, en_curso, finalizado)
 * - Indicador de cupos disponibles
 */
public class EventoAdapter extends RecyclerView.Adapter<EventoAdapter.EventoViewHolder> {

    private List<Evento> eventos;
    private OnEventoClickListener listener;

    // Interfaz para manejar clicks en items
    public interface OnEventoClickListener {
        void onEventoClick(Evento evento, int position);
    }

    public EventoAdapter(OnEventoClickListener listener) {
        this.eventos = new ArrayList<>();
        this.listener = listener;
    }

    @NonNull
    @Override
    public EventoViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.item_evento, parent, false);
        return new EventoViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull EventoViewHolder holder, int position) {
        Evento evento = eventos.get(position);
        holder.bind(evento, listener);
    }

    @Override
    public int getItemCount() {
        return eventos.size();
    }

    /**
     * Actualiza la lista completa de eventos
     */
    public void actualizarLista(List<Evento> nuevosEventos) {
        this.eventos.clear();
        if (nuevosEventos != null) {
            this.eventos.addAll(nuevosEventos);
        }
        notifyDataSetChanged();
    }

    /**
     * Agrega un evento al inicio de la lista
     */
    public void agregarEvento(Evento evento) {
        this.eventos.add(0, evento);
        notifyItemInserted(0);
    }

    /**
     * Elimina un evento de la lista
     */
    public void eliminarEvento(int position) {
        if (position >= 0 && position < eventos.size()) {
            eventos.remove(position);
            notifyItemRemoved(position);
        }
    }

    /**
     * ViewHolder para items de evento
     */
    static class EventoViewHolder extends RecyclerView.ViewHolder {
        private final TextView tvDescripcion;
        private final TextView tvFecha;
        private final TextView tvUbicacion;
        private final TextView tvCategoria;
        private final TextView tvCupos;
        private final TextView tvCosto;
        private final TextView tvEstado;
        private final TextView tvDistancia;

        public EventoViewHolder(@NonNull View itemView) {
            super(itemView);
            tvDescripcion = itemView.findViewById(R.id.tv_descripcion_evento);
            tvFecha = itemView.findViewById(R.id.tv_fecha_evento);
            tvUbicacion = itemView.findViewById(R.id.tv_ubicacion_evento);
            tvCategoria = itemView.findViewById(R.id.tv_categoria_evento);
            tvCupos = itemView.findViewById(R.id.tv_cupos_evento);
            tvCosto = itemView.findViewById(R.id.tv_costo_evento);
            tvEstado = itemView.findViewById(R.id.tv_estado_evento);
            tvDistancia = itemView.findViewById(R.id.tv_distancia_evento);
        }

        public void bind(Evento evento, OnEventoClickListener listener) {
            // Descripción
            tvDescripcion.setText(evento.getDescripcion());

            // Fecha formateada
            tvFecha.setText(evento.getFechaFormateada());

            // Ubicación
            if (evento.getUbicacion() != null && !evento.getUbicacion().isEmpty()) {
                tvUbicacion.setText(evento.getUbicacion());
                tvUbicacion.setVisibility(View.VISIBLE);
            } else {
                tvUbicacion.setVisibility(View.GONE);
            }

            // Categoría con color
            String categoriaNombre = evento.getCategoriaNombre();
            String colorCategoria = evento.getColorCategoria();

            tvCategoria.setText(categoriaNombre);
            try {
                tvCategoria.setTextColor(Color.parseColor(colorCategoria));
            } catch (Exception e) {
                tvCategoria.setTextColor(Color.parseColor("#9E9E9E"));
            }

            // Cupos
            if (evento.getCupoMaximo() != null && evento.getCupoMaximo() > 0) {
                Integer cuposRestantes = evento.getCuposRestantes();
                if (cuposRestantes != null) {
                    if (cuposRestantes > 0) {
                        tvCupos.setText(cuposRestantes + " cupos disponibles");
                        tvCupos.setTextColor(Color.parseColor("#4CAF50")); // Verde
                    } else {
                        tvCupos.setText("Cupos agotados");
                        tvCupos.setTextColor(Color.parseColor("#F44336")); // Rojo
                    }
                    tvCupos.setVisibility(View.VISIBLE);
                } else {
                    tvCupos.setVisibility(View.GONE);
                }
            } else {
                tvCupos.setText("Cupos ilimitados");
                tvCupos.setTextColor(Color.parseColor("#4CAF50"));
                tvCupos.setVisibility(View.VISIBLE);
            }

            // Costo
            tvCosto.setText(evento.getCostoFormateado());
            if (evento.esGratuito()) {
                tvCosto.setTextColor(Color.parseColor("#4CAF50")); // Verde para gratis
            } else {
                tvCosto.setTextColor(Color.parseColor("#FF9800")); // Naranja para pago
            }

            // Estado
            String estado = evento.getEstado();
            if (estado != null) {
                switch (estado.toLowerCase()) {
                    case "programado":
                        tvEstado.setText("📅 Próximamente");
                        tvEstado.setTextColor(Color.parseColor("#2196F3")); // Azul
                        tvEstado.setVisibility(View.VISIBLE);
                        break;
                    case "en_curso":
                        tvEstado.setText("🔴 EN VIVO");
                        tvEstado.setTextColor(Color.parseColor("#F44336")); // Rojo
                        tvEstado.setVisibility(View.VISIBLE);
                        break;
                    case "finalizado":
                        tvEstado.setText("✅ Finalizado");
                        tvEstado.setTextColor(Color.parseColor("#9E9E9E")); // Gris
                        tvEstado.setVisibility(View.VISIBLE);
                        break;
                    case "cancelado":
                        tvEstado.setText("❌ Cancelado");
                        tvEstado.setTextColor(Color.parseColor("#F44336")); // Rojo
                        tvEstado.setVisibility(View.VISIBLE);
                        break;
                    default:
                        tvEstado.setVisibility(View.GONE);
                }
            } else {
                tvEstado.setVisibility(View.GONE);
            }

            // Distancia (si está calculada)
            if (evento.getDistancia() != null && evento.getDistancia() >= 0) {
                String distanciaTexto = formatearDistancia(evento.getDistancia());
                tvDistancia.setText(distanciaTexto);
                tvDistancia.setVisibility(View.VISIBLE);
            } else {
                tvDistancia.setVisibility(View.GONE);
            }

            // Click listener
            itemView.setOnClickListener(v -> {
                if (listener != null) {
                    listener.onEventoClick(evento, getAdapterPosition());
                }
            });
        }

        /**
         * Formatea la distancia para mostrar en UI
         */
        private String formatearDistancia(Double distanciaKm) {
            if (distanciaKm == null || distanciaKm < 0) {
                return "";
            }

            if (distanciaKm < 1.0) {
                // Menos de 1 km → mostrar en metros
                int metros = (int) (distanciaKm * 1000);
                return metros + " m";
            } else if (distanciaKm < 10.0) {
                // 1-10 km → 1 decimal
                return String.format("%.1f km", distanciaKm);
            } else {
                // Más de 10 km → sin decimales
                return String.format("%.0f km", distanciaKm);
            }
        }
    }
}
