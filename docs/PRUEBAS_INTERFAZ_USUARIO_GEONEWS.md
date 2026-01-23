# PRUEBAS DE INTERFAZ Y EXPERIENCIA DE USUARIO - GEONEWS

**Proyecto:** GeoNews - Aplicación Móvil de Noticias Locales Geolocalizadas
**Versión:** 0.1.0
**Fecha:** Enero 2026
**Plataforma:** Android (API 21 - API 34)

---

## ÍNDICE

1. [Pruebas de Accesibilidad de Elementos de Interfaz](#1-pruebas-de-accesibilidad-de-elementos-de-interfaz)
2. [Pruebas de Usabilidad de Interfaz](#2-pruebas-de-usabilidad-de-interfaz)
3. [Pruebas de Interactividad](#3-pruebas-de-interactividad)
4. [Pruebas de Velocidad y Rendimiento](#4-pruebas-de-velocidad-y-rendimiento)

---

## 1. PRUEBAS DE ACCESIBILIDAD DE ELEMENTOS DE INTERFAZ

### Descripción
Las pruebas de accesibilidad están enfocadas en comprobar que la parte visual e interactiva de la aplicación esté comprensible y utilizable por diferentes tipos de dispositivos y fácil de manejar para el usuario.

### Tabla de Pruebas de Accesibilidad

| ID | Elemento a Probar | Criterio de Aceptación | Resultado | Observaciones |
|----|-------------------|------------------------|-----------|---------------|
| **ACC-001** | Tamaño y legibilidad de textos | Mínimo 14sp para textos normales, 16sp para títulos, legible sin zoom | ☐ Correcto<br>☐ Parcial<br>☐ Fallido | |
| **ACC-002** | Contraste de texto y fondo | Ratio mínimo 4.5:1 (WCAG AA) en modo claro y oscuro | ☐ Correcto<br>☐ Parcial<br>☐ Fallido | |
| **ACC-003** | Tamaño de botones y áreas táctiles | Mínimo 48dp x 48dp, área táctil accesible | ☐ Correcto<br>☐ Parcial<br>☐ Fallido | |
| **ACC-004** | Comprensibilidad de iconos | Iconos comprensibles sin texto (navegación, categorías, acciones) | ☐ Correcto<br>☐ Parcial<br>☐ Fallido | |
| **ACC-005** | Navegación entre pantallas | Transiciones fluidas, botón atrás funcional, jerarquía visual clara | ☐ Correcto<br>☐ Parcial<br>☐ Fallido | |
| **ACC-006** | Feedback visual en interacciones | Ripple effect, animaciones, cambios de estado visibles | ☐ Correcto<br>☐ Parcial<br>☐ Fallido | |
| **ACC-007** | Compatibilidad multi-dispositivo | Funciona correctamente en gama baja, media y alta (API 21-34) | ☐ Correcto<br>☐ Parcial<br>☐ Fallido | |
| **ACC-008** | Modo horizontal (landscape) | Layout se adapta correctamente en orientación horizontal | ☐ Correcto<br>☐ Parcial<br>☐ Fallido | |

---

## 2. PRUEBAS DE USABILIDAD DE INTERFAZ

### Descripción
En las pruebas de usabilidad se realizaron pruebas con usuarios que interactuaron con el producto para evaluar la facilidad de uso. Se verifican las tareas principales realizadas en la aplicación.

### Tabla de Pruebas con Usuarios (5 Usuarios)

| ID | Tarea | Instrucción al Usuario | Criterio de Éxito | Tiempo Esperado | Usuario 1 | Usuario 2 | Usuario 3 | Usuario 4 | Usuario 5 | Promedio |
|----|-------|------------------------|-------------------|-----------------|-----------|-----------|-----------|-----------|-----------|----------|
| **USA-001** | Registro de cuenta | "Crea una cuenta nueva con tu email" | Usuario completa registro sin ayuda | < 2 min | ☐ Éxito<br>☐ Dificultad<br>☐ Fallo | ☐ Éxito<br>☐ Dificultad<br>☐ Fallo | ☐ Éxito<br>☐ Dificultad<br>☐ Fallo | ☐ Éxito<br>☐ Dificultad<br>☐ Fallo | ☐ Éxito<br>☐ Dificultad<br>☐ Fallo | ___ min |
| **USA-002** | Navegación en feed de noticias | "Desplázate y observa las noticias disponibles" | Usuario navega fluidamente por la lista | < 30 seg | ☐ Éxito<br>☐ Dificultad<br>☐ Fallo | ☐ Éxito<br>☐ Dificultad<br>☐ Fallo | ☐ Éxito<br>☐ Dificultad<br>☐ Fallo | ☐ Éxito<br>☐ Dificultad<br>☐ Fallo | ☐ Éxito<br>☐ Dificultad<br>☐ Fallo | ___ seg |
| **USA-003** | Filtrar por categoría | "Muestra solo noticias de 'Deportes'" | Usuario encuentra y aplica filtro sin ayuda | < 30 seg | ☐ Éxito<br>☐ Dificultad<br>☐ Fallo | ☐ Éxito<br>☐ Dificultad<br>☐ Fallo | ☐ Éxito<br>☐ Dificultad<br>☐ Fallo | ☐ Éxito<br>☐ Dificultad<br>☐ Fallo | ☐ Éxito<br>☐ Dificultad<br>☐ Fallo | ___ seg |
| **USA-004** | Búsqueda por ubicación | "Encuentra noticias cerca de ti (5km)" | Usuario activa filtro cercanas sin ayuda | < 30 seg | ☐ Éxito<br>☐ Dificultad<br>☐ Fallo | ☐ Éxito<br>☐ Dificultad<br>☐ Fallo | ☐ Éxito<br>☐ Dificultad<br>☐ Fallo | ☐ Éxito<br>☐ Dificultad<br>☐ Fallo | ☐ Éxito<br>☐ Dificultad<br>☐ Fallo | ___ seg |
| **USA-005** | Visualización del mapa | "Ve al mapa y encuentra marcadores de noticias" | Usuario accede al mapa y reconoce marcadores | < 20 seg | ☐ Éxito<br>☐ Dificultad<br>☐ Fallo | ☐ Éxito<br>☐ Dificultad<br>☐ Fallo | ☐ Éxito<br>☐ Dificultad<br>☐ Fallo | ☐ Éxito<br>☐ Dificultad<br>☐ Fallo | ☐ Éxito<br>☐ Dificultad<br>☐ Fallo | ___ seg |
| **USA-006** | Acceso a detalle de noticia | "Abre una noticia y lee su contenido completo" | Usuario abre detalle y scrollea el contenido | < 1 min | ☐ Éxito<br>☐ Dificultad<br>☐ Fallo | ☐ Éxito<br>☐ Dificultad<br>☐ Fallo | ☐ Éxito<br>☐ Dificultad<br>☐ Fallo | ☐ Éxito<br>☐ Dificultad<br>☐ Fallo | ☐ Éxito<br>☐ Dificultad<br>☐ Fallo | ___ min |
| **USA-007** | Guardar noticia favorita | "Guarda esta noticia para leer después" | Usuario encuentra y usa botón guardar | < 15 seg | ☐ Éxito<br>☐ Dificultad<br>☐ Fallo | ☐ Éxito<br>☐ Dificultad<br>☐ Fallo | ☐ Éxito<br>☐ Dificultad<br>☐ Fallo | ☐ Éxito<br>☐ Dificultad<br>☐ Fallo | ☐ Éxito<br>☐ Dificultad<br>☐ Fallo | ___ seg |
| **USA-008** | Gestión de perfil | "Edita tu información de perfil" | Usuario accede a editar perfil y modifica datos | < 1 min | ☐ Éxito<br>☐ Dificultad<br>☐ Fallo | ☐ Éxito<br>☐ Dificultad<br>☐ Fallo | ☐ Éxito<br>☐ Dificultad<br>☐ Fallo | ☐ Éxito<br>☐ Dificultad<br>☐ Fallo | ☐ Éxito<br>☐ Dificultad<br>☐ Fallo | ___ min |

### Escala de Satisfacción del Usuario (1-5)

| ID | Pregunta | Usuario 1 | Usuario 2 | Usuario 3 | Usuario 4 | Usuario 5 | Promedio |
|----|----------|-----------|-----------|-----------|-----------|-----------|----------|
| **SAT-001** | ¿Qué tan fácil fue usar la aplicación? (1-5) | ☐1 ☐2 ☐3 ☐4 ☐5 | ☐1 ☐2 ☐3 ☐4 ☐5 | ☐1 ☐2 ☐3 ☐4 ☐5 | ☐1 ☐2 ☐3 ☐4 ☐5 | ☐1 ☐2 ☐3 ☐4 ☐5 | ___ |
| **SAT-002** | ¿Qué tan intuitiva es la navegación? (1-5) | ☐1 ☐2 ☐3 ☐4 ☐5 | ☐1 ☐2 ☐3 ☐4 ☐5 | ☐1 ☐2 ☐3 ☐4 ☐5 | ☐1 ☐2 ☐3 ☐4 ☐5 | ☐1 ☐2 ☐3 ☐4 ☐5 | ___ |
| **SAT-003** | ¿Qué tan útil es el mapa con marcadores? (1-5) | ☐1 ☐2 ☐3 ☐4 ☐5 | ☐1 ☐2 ☐3 ☐4 ☐5 | ☐1 ☐2 ☐3 ☐4 ☐5 | ☐1 ☐2 ☐3 ☐4 ☐5 | ☐1 ☐2 ☐3 ☐4 ☐5 | ___ |
| **SAT-004** | ¿Recomendarías esta aplicación? (1-5) | ☐1 ☐2 ☐3 ☐4 ☐5 | ☐1 ☐2 ☐3 ☐4 ☐5 | ☐1 ☐2 ☐3 ☐4 ☐5 | ☐1 ☐2 ☐3 ☐4 ☐5 | ☐1 ☐2 ☐3 ☐4 ☐5 | ___ |

**Escala:** 1 = Muy difícil/Malo, 2 = Difícil/Regular, 3 = Neutral, 4 = Fácil/Bueno, 5 = Muy fácil/Excelente

---

## 3. PRUEBAS DE INTERACTIVIDAD

### Descripción
Estas pruebas se centran en comprobar cómo responde la aplicación ante las acciones realizadas por el usuario, como desplazarse por la aplicación, selecciones, cargas dinámicas de datos y actualización de contenido.

### Tabla de Pruebas de Interactividad

| ID | Acción del Usuario | Respuesta Esperada | Tiempo de Respuesta | Resultado | Observaciones |
|----|-------------------|-------------------|---------------------|-----------|---------------|
| **INT-001** | Scroll en lista de noticias | Desplazamiento fluido sin lag (≥55 FPS) | Inmediato | ☐ Correcto<br>☐ Parcial<br>☐ Fallido | |
| **INT-002** | Pull-to-refresh en lista | Animación de carga, lista actualizada con nuevas noticias | < 2 seg | ☐ Correcto<br>☐ Parcial<br>☐ Fallido | |
| **INT-003** | Clic en botones | Feedback visual (ripple effect), ejecuta acción | < 300ms | ☐ Correcto<br>☐ Parcial<br>☐ Fallido | |
| **INT-004** | Seleccionar chip de categoría | Chip se marca, lista se filtra por categoría | < 500ms | ☐ Correcto<br>☐ Parcial<br>☐ Fallido | |
| **INT-005** | Zoom y movimiento en mapa | Gestos funcionan fluidamente (pinch, pan) | Inmediato | ☐ Correcto<br>☐ Parcial<br>☐ Fallido | |
| **INT-006** | Clic en marcador del mapa | InfoWindow aparece con datos de la noticia | < 300ms | ☐ Correcto<br>☐ Parcial<br>☐ Fallido | |
| **INT-007** | Guardar/quitar favorito | Icono cambia de estado, acción se registra | < 500ms | ☐ Correcto<br>☐ Parcial<br>☐ Fallido | |
| **INT-008** | Carga de imágenes en lista | Lazy loading, placeholder → imagen real con transición | < 2 seg/imagen | ☐ Correcto<br>☐ Parcial<br>☐ Fallido | |
| **INT-009** | Toggle modo oscuro | Toda la UI cambia de tema inmediatamente | < 500ms | ☐ Correcto<br>☐ Parcial<br>☐ Fallido | |
| **INT-010** | Rotación de pantalla | Mantiene estado y adapta layout correctamente | < 1 seg | ☐ Correcto<br>☐ Parcial<br>☐ Fallido | |

---

## 4. PRUEBAS DE VELOCIDAD Y RENDIMIENTO

### Descripción
Las pruebas de velocidad se enfocan en el tiempo de carga del contenido y rendimiento general de la aplicación. Se realizan comparaciones utilizando conexión WiFi y datos móviles 4G.

### 4.1 Tabla de Pruebas - Tiempos de Carga

| ID | Operación | WiFi<br>Criterio | WiFi<br>Medición | 4G<br>Criterio | 4G<br>Medición | Resultado |
|----|-----------|------------------|------------------|----------------|----------------|-----------|
| **VEL-001** | Carga inicial de feed de noticias | < 3 seg | ___ seg | < 5 seg | ___ seg | ☐ Correcto<br>☐ Parcial<br>☐ Fallido |
| **VEL-002** | Abrir detalle de noticia | < 1 seg | ___ seg | < 2 seg | ___ seg | ☐ Correcto<br>☐ Parcial<br>☐ Fallido |
| **VEL-003** | Carga de imagen alta resolución (1MB) | < 2 seg | ___ seg | < 5 seg | ___ seg | ☐ Correcto<br>☐ Parcial<br>☐ Fallido |
| **VEL-004** | Carga inicial del mapa con marcadores | < 5 seg | ___ seg | < 8 seg | ___ seg | ☐ Correcto<br>☐ Parcial<br>☐ Fallido |
| **VEL-005** | Aplicar filtro de categoría | < 1 seg | ___ seg | < 2 seg | ___ seg | ☐ Correcto<br>☐ Parcial<br>☐ Fallido |
| **VEL-006** | Login con credenciales | < 2 seg | ___ seg | < 3 seg | ___ seg | ☐ Correcto<br>☐ Parcial<br>☐ Fallido |
| **VEL-007** | Subir foto de perfil (500KB) | < 3 seg | ___ seg | < 8 seg | ___ seg | ☐ Correcto<br>☐ Parcial<br>☐ Fallido |
| **VEL-008** | Pull-to-refresh (actualizar lista) | < 2 seg | ___ seg | < 4 seg | ___ seg | ☐ Correcto<br>☐ Parcial<br>☐ Fallido |

### 4.2 Tabla de Pruebas - Consumo de Recursos

| ID | Métrica | Escenario | Criterio de Aceptación | Medición | Resultado | Observaciones |
|----|---------|-----------|----------------------|----------|-----------|---------------|
| **REC-001** | Uso de RAM | App en uso normal | < 200 MB | ___ MB | ☐ Correcto<br>☐ Parcial<br>☐ Fallido | |
| **REC-002** | Uso de RAM | Con mapa abierto | < 300 MB | ___ MB | ☐ Correcto<br>☐ Parcial<br>☐ Fallido | |
| **REC-003** | Tamaño del APK | APK release | < 50 MB | ___ MB | ☐ Correcto<br>☐ Parcial<br>☐ Fallido | |
| **REC-004** | Consumo de batería | 30 min de uso continuo | Clasificación "Bajo" en Android | ___ % | ☐ Correcto<br>☐ Parcial<br>☐ Fallido | |
| **REC-005** | Uso de CPU | App en primer plano | < 30% promedio | ___ % | ☐ Correcto<br>☐ Parcial<br>☐ Fallido | |
| **REC-006** | Fluidez de scroll (FPS) | Lista con 100 noticias | ≥ 55 FPS | ___ FPS | ☐ Correcto<br>☐ Parcial<br>☐ Fallido | |

---

## RESUMEN DE RESULTADOS

### Estadísticas Generales

| Categoría | Total Pruebas | Correctas | Parciales | Fallidas | % Éxito |
|-----------|---------------|-----------|-----------|----------|---------|
| **Accesibilidad** | 8 | 0 | 0 | 0 | 0% |
| **Usabilidad** | 12 | 0 | 0 | 0 | 0% |
| **Interactividad** | 10 | 0 | 0 | 0 | 0% |
| **Velocidad y Recursos** | 14 | 0 | 0 | 0 | 0% |
| **TOTAL** | **44** | **0** | **0** | **0** | **0%** |

### Criterios de Aprobación

- **Aprobado:** ≥ 90% de pruebas correctas
- **Aprobado con Observaciones:** 80-89% correctas
- **Requiere Mejoras:** 70-79% correctas
- **No Aprobado:** < 70% correctas

---

## OBSERVACIONES GENERALES

| Aspecto | Observaciones |
|---------|---------------|
| **Puntos Fuertes** | |
| **Áreas de Mejora** | |
| **Problemas Críticos** | |
| **Recomendaciones** | |

---

## FIRMAS

| Responsable | Nombre | Firma | Fecha |
|-------------|--------|-------|-------|
| **Responsable de Pruebas** | | _________________ | ____/____/____ |
| **Líder Control de Calidad** | | _________________ | ____/____/____ |
| **Product Owner** | | _________________ | ____/____/____ |

---

**Fin del Documento de Pruebas de Interfaz y Usuario - GeoNews v0.1.0**

*Documento generado: Enero 2026*
