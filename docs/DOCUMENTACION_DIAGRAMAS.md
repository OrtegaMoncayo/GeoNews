# Documentación de Diagramas - GeoNews

## Índice de Documentación Técnica

Este directorio contiene la documentación técnica completa del proyecto GeoNews a través de tres diagramas principales.

---

## 📋 Documentos Disponibles

### 1. [Diagrama de Clases](DIAGRAMA_CLASES.md)

**Descripción**: Estructura completa de clases del sistema GeoNews.

**Contenido**:
- Entidades del modelo de datos (Usuario, Noticia, Evento, Categoría, Parroquia)
- Gestores y servicios (FirebaseManager, ApiConfig, NoticiaServiceHTTP)
- Utilidades (UsuarioPreferences, ThemeManager, DialogHelper, UbicacionUtils)
- Actividades principales (BaseActivity, Login, Registro, Noticias, Eventos, Mapa, Perfil, etc.)
- Adaptadores de RecyclerView
- Relaciones entre clases
- Patrones de diseño aplicados

**Formato**: PlantUML (renderizable en IDEs como IntelliJ IDEA, VS Code con extensiones)

**Uso**:
```bash
# Para visualizar en VS Code
# Instalar extensión: PlantUML
# Abrir archivo y presionar Alt+D
```

---

### 2. [Diagrama Entidad-Relación](DIAGRAMA_ENTIDAD_RELACION.md)

**Descripción**: Modelo de datos completo de Firebase Firestore.

**Contenido**:
- Entidades principales con todos sus atributos
- Relaciones y cardinalidades
- Índices de Firestore
- Estructura de colecciones
- Ejemplos de documentos JSON
- Reglas de integridad
- Valores por defecto
- Restricciones de dominio
- Estrategia de desnormalización

**Estructura de Base de Datos**:
- 7 colecciones principales en Firestore
- Firebase Storage para archivos multimedia
- SharedPreferences local para datos del usuario

**Cardinalidades Principales**:
- Usuario → Noticia: 1:N
- Usuario → Evento: 1:N
- Usuario ↔ Evento (Asistencia): N:M
- Usuario ↔ Noticia (Guardadas): N:M
- Categoría → Noticia: 1:N

---

### 3. [Diseño de Entrada y Salida](DISENO_ENTRADA_SALIDA.md)

**Descripción**: Interfaces de usuario, flujos de datos y formatos de comunicación.

**Contenido**:
- Arquitectura de capas del sistema
- Diseño de 7 pantallas principales:
  - Login y Registro
  - Feed de Noticias
  - Detalle de Noticia
  - Mapa Interactivo
  - Registro de Evento
  - Perfil de Usuario
  - Ajustes
- Flujos de datos:
  - Autenticación
  - Consulta de noticias
  - Geolocalización
- Formatos de entrada/salida JSON
- Validaciones de formularios
- Códigos de estado HTTP
- Navegación entre pantallas

**Incluye**:
- Mockups en formato ASCII
- Ejemplos de Request/Response
- Tablas de validación
- Diagramas de flujo

---

## 🎯 Propósito de los Diagramas

### Para Desarrolladores
- **Diagrama de Clases**: Entender la estructura del código, clases disponibles y sus relaciones
- **Diagrama ER**: Comprender el modelo de datos y cómo diseñar queries
- **Diseño E/S**: Implementar correctamente las interfaces y validaciones

### Para Arquitectos
- **Diagrama de Clases**: Evaluar patrones de diseño y arquitectura
- **Diagrama ER**: Revisar estrategia de persistencia y escalabilidad
- **Diseño E/S**: Analizar flujos de información y experiencia de usuario

### Para Testers
- **Diagrama de Clases**: Identificar componentes a probar
- **Diagrama ER**: Crear datos de prueba realistas
- **Diseño E/S**: Diseñar casos de prueba de interfaz y validaciones

### Para Documentación
- Referencia técnica completa del sistema
- Onboarding de nuevos desarrolladores
- Documentación de proyecto de titulación

---

## 🔧 Herramientas Recomendadas

### Para visualizar PlantUML:

**Visual Studio Code**
```bash
# Extensión recomendada
PlantUML by jebbs
```

**IntelliJ IDEA**
```bash
# Plugin integrado
PlantUML Integration
```

**Online**
```
http://www.plantuml.com/plantuml/uml/
```

### Para editar Markdown:

- Visual Studio Code (con preview)
- Typora
- GitHub (visualización automática)

---

## 📊 Estadísticas del Proyecto

### Modelo de Datos
- **Entidades**: 7 principales (Usuario, Noticia, Evento, Categoría, Parroquia, AsistenciaEvento, NoticiasGuardadas)
- **Atributos totales**: ~80 campos
- **Relaciones**: 8 relaciones principales
- **Colecciones Firestore**: 7

### Código
- **Clases de modelo**: 5
- **Activities**: 15
- **Adapters**: 2
- **Managers/Services**: 3
- **Utilidades**: 4
- **Total de clases**: ~30

### Interfaces
- **Pantallas principales**: 7
- **Pantallas secundarias**: 8
- **Total de layouts**: ~30 archivos XML

---

## 🎨 Convenciones de Diseño

### Colores en Diagramas
- 🟦 **Azul claro (#E3F2FD)**: Entidades del modelo
- 🟨 **Amarillo claro (#FFF3E0)**: Activities/Pantallas
- 🟩 **Verde claro (#E8F5E9)**: Managers/Servicios
- 🟪 **Morado claro (#F3E5F5)**: Utilidades/Helpers

### Nomenclatura
- **PascalCase**: Clases, Activities
- **camelCase**: Métodos, variables
- **UPPER_SNAKE_CASE**: Constantes
- **lower_snake_case**: Nombres de colecciones Firestore

---

## 📱 Información del Proyecto

**Nombre**: GeoNews - Noticias Locales de Ibarra
**Versión**: 0.1.0 (Build 2024)
**Plataforma**: Android (Java)
**Base de Datos**: Firebase Firestore + Firebase Storage
**API Level**: Mínimo 24, Target 33
**Arquitectura**: MVP (Model-View-Presenter) con Repository Pattern

**Universidad**: Universidad Técnica del Norte
**Tipo**: Proyecto de Titulación
**Área**: Ingeniería en Software

---

## 🔄 Actualizaciones

| Versión | Fecha | Cambios |
|---------|-------|---------|
| 1.0 | Enero 2026 | Versión inicial de documentación |
| - | - | - |

---

## 📞 Contacto

Para consultas sobre la documentación técnica:
- **Repositorio**: (Agregar URL de GitHub)
- **Email**: soporte@geonews.ec

---

## 📄 Licencia

Este proyecto es de código cerrado, desarrollado como parte de un trabajo de titulación académica.

© 2024-2026 Universidad Técnica del Norte
