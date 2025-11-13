# Usar imagen oficial de Python 3.11
FROM python:3.11-slim

# Configurar directorio de trabajo
WORKDIR /app

# Copiar archivos de requisitos
COPY requirements.txt .

# Instalar dependencias
RUN pip install --no-cache-dir -r requirements.txt

# Copiar código de la aplicación
COPY main.py .
COPY serviceAccountKey.json .

# Exponer puerto 8080 (Cloud Run usa este puerto)
EXPOSE 8080

# Comando para ejecutar la aplicación
CMD ["uvicorn", "main:app", "--host", "0.0.0.0", "--port", "8080"]
