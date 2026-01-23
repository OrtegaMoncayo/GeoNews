package com.tesistitulacion.noticiaslocales.utils;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.util.Base64;
import android.util.Log;
import android.widget.ImageView;

import com.bumptech.glide.Glide;
import com.tesistitulacion.noticiaslocales.R;

/**
 * Helper para cargar imágenes de diferentes fuentes (URL, Base64)
 */
public class ImageHelper {

    private static final String TAG = "ImageHelper";

    /**
     * Carga una imagen en un ImageView, soporta URLs y Base64
     * @param context Contexto de la aplicación
     * @param imageView ImageView donde cargar la imagen
     * @param imageSource URL o string Base64 de la imagen
     * @param placeholder Recurso drawable para placeholder
     * @param circular Si la imagen debe ser circular
     */
    public static void cargarImagen(Context context, ImageView imageView,
                                     String imageSource, int placeholder, boolean circular) {
        if (imageView == null) {
            Log.w(TAG, "ImageView es null, no se puede cargar imagen");
            return;
        }

        if (imageSource == null || imageSource.isEmpty()) {
            imageView.setImageResource(placeholder);
            return;
        }

        if (imageSource.startsWith("data:image")) {
            // Es una imagen Base64
            cargarImagenBase64(imageView, imageSource, placeholder);
        } else {
            // Es una URL, usar Glide
            cargarImagenUrl(context, imageView, imageSource, placeholder, circular);
        }
    }

    /**
     * Carga una imagen circular (para avatares)
     */
    public static void cargarAvatar(Context context, ImageView imageView, String imageSource) {
        cargarImagen(context, imageView, imageSource, R.drawable.ic_person, true);
    }

    /**
     * Carga una imagen desde Base64
     */
    private static void cargarImagenBase64(ImageView imageView, String base64Source, int placeholder) {
        try {
            // Limpiar cualquier tint previo para que la imagen se muestre correctamente
            imageView.setImageTintList(null);

            String base64Data;
            if (base64Source.contains(",")) {
                base64Data = base64Source.substring(base64Source.indexOf(",") + 1);
            } else {
                base64Data = base64Source;
            }

            byte[] decodedBytes = Base64.decode(base64Data, Base64.DEFAULT);
            Bitmap bitmap = BitmapFactory.decodeByteArray(decodedBytes, 0, decodedBytes.length);

            if (bitmap != null) {
                imageView.setImageBitmap(bitmap);
                Log.d(TAG, "Imagen Base64 cargada correctamente");
            } else {
                imageView.setImageResource(placeholder);
                Log.w(TAG, "No se pudo decodificar imagen Base64");
            }
        } catch (Exception e) {
            Log.e(TAG, "Error al cargar imagen Base64", e);
            imageView.setImageResource(placeholder);
        }
    }

    /**
     * Carga una imagen desde URL usando Glide
     */
    private static void cargarImagenUrl(Context context, ImageView imageView,
                                         String url, int placeholder, boolean circular) {
        try {
            // Limpiar cualquier tint previo para que la imagen se muestre correctamente
            imageView.setImageTintList(null);

            if (circular) {
                Glide.with(context)
                        .load(url)
                        .placeholder(placeholder)
                        .error(placeholder)
                        .circleCrop()
                        .into(imageView);
            } else {
                Glide.with(context)
                        .load(url)
                        .placeholder(placeholder)
                        .error(placeholder)
                        .into(imageView);
            }
            Log.d(TAG, "Imagen URL cargada: " + url);
        } catch (Exception e) {
            Log.e(TAG, "Error al cargar imagen URL", e);
            imageView.setImageResource(placeholder);
        }
    }
}
