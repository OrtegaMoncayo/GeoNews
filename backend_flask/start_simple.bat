@echo off
echo ========================================
echo   Backend FastAPI - Noticias Ibarra
echo   (Version Simplificada - Python 3.14)
echo ========================================
echo.

REM Verificar si existe el entorno virtual
if not exist "venv\" (
    echo [!] Entorno virtual no encontrado
    echo [*] Creando entorno virtual...
    python -m venv venv
    echo [OK] Entorno virtual creado
    echo.
)

REM Activar entorno virtual
echo [*] Activando entorno virtual...
call venv\Scripts\activate

REM Actualizar pip primero
echo [*] Actualizando pip...
python -m pip install --upgrade pip

REM Instalar dependencias minimas
echo [*] Instalando dependencias minimas (sin Rust)...
pip install -r requirements-minimal.txt

echo.
echo ========================================
echo   Iniciando servidor FastAPI
echo ========================================
echo.
echo [*] API: http://localhost:8000
echo [*] Docs: http://localhost:8000/docs
echo [*] Presiona CTRL+C para detener
echo.

REM Ejecutar servidor con version simplificada
python main_simple.py

pause
