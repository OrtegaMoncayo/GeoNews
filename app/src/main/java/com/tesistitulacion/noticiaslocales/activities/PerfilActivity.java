package com.tesistitulacion.noticiaslocales.activities;

import android.content.Intent;
import android.os.Bundle;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;
import com.tesistitulacion.noticiaslocales.R;
import com.tesistitulacion.noticiaslocales.utils.UsuarioPreferences;

/**
 * Pantalla de perfil del usuario
 * Extiende de BaseActivity para tener navegación automática
 */
public class PerfilActivity extends BaseActivity {
    private static final String TAG = "PerfilActivity";

    private TextView tvNombre, tvEmail, tvRol;
    private Button btnCerrarSesion;

    @Override
    protected int getNavegacionActiva() {
        return NAV_PERFIL; // Marca esta sección como activa
    }

    @Override
    protected int getLayoutResourceId() {
        return R.layout.activity_perfil;
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        inicializarVistas();
        cargarDatosUsuario();
        configurarBotones();
    }

    private void inicializarVistas() {
        tvNombre = findViewById(R.id.tv_nombre);
        tvEmail = findViewById(R.id.tv_email);
        tvRol = findViewById(R.id.tv_rol);
        btnCerrarSesion = findViewById(R.id.btn_cerrar_sesion);
    }

    private void cargarDatosUsuario() {
        // Obtener datos del usuario desde UsuarioPreferences (encriptado)
        String nombre = UsuarioPreferences.getNombre(this);
        String email = UsuarioPreferences.getEmail(this);
        String rol = UsuarioPreferences.getRol(this);

        // Mostrar datos en UI
        if (nombre != null) {
            tvNombre.setText(nombre);
        } else {
            tvNombre.setText("Usuario");
        }

        if (email != null) {
            tvEmail.setText(email);
        } else {
            tvEmail.setText("Sin email");
        }

        if (rol != null) {
            tvRol.setText(rol.toUpperCase());
        } else {
            tvRol.setText("USUARIO");
        }
    }

    private void configurarBotones() {
        btnCerrarSesion.setOnClickListener(v -> {
            // Cerrar sesión
            UsuarioPreferences.cerrarSesion(this);

            Toast.makeText(this,
                    "Sesión cerrada",
                    Toast.LENGTH_SHORT).show();

            // Volver al login
            Intent intent = new Intent(this, LoginActivity.class);
            intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
            startActivity(intent);
            finish();
        });
    }
}
