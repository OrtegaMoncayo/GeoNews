@echo off
echo ========================================
echo   Backend FastAPI - Noticias Ibarra
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

REM Instalar/actualizar dependencias
echo [*] Instalando dependencias...
pip install -r requirements-simple.txt --quiet

echo.
echo ========================================
echo   Iniciando servidor FastAPI
echo ========================================
echo.
echo [*] API: http://localhost:8000
echo [*] Docs: http://localhost:8000/docs
echo [*] Presiona CTRL+C para detener
echo.

REM Ejecutar servidor
python main.py

pause
