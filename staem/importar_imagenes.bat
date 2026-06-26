@echo off
echo =======================================================
echo IMPORTANDO IMAGENES DOCKER DE STAEM PARA MODO OFFLINE
echo =======================================================
echo.

if not exist "staem-proyecto.tar" (
    echo ERROR: No se encuentra el archivo staem-proyecto.tar
    echo Asegurate de haber ejecutado exportar_imagenes.bat en la PC original
    echo y haber traido el archivo a esta misma carpeta.
    echo =======================================================
    pause
    exit /b 1
)

echo Paso 1: Cargando imagenes desde staem-proyecto.tar (Esto puede tardar)...
docker load -i staem-proyecto.tar

echo.
if %errorlevel% neq 0 (
    echo =======================================================
    echo ERROR: Hubo un problema al cargar las imagenes.
    echo Asegurate de que Docker Desktop este corriendo en esta PC.
    echo =======================================================
    pause
    exit /b 1
)

echo Paso 2: Iniciando todo el sistema STAEM...
docker compose up -d

echo.
echo =======================================================
echo EXITO: El sistema esta iniciando en segundo plano.
echo Espera unos 30-40 segundos para que todo levante.
echo Puedes verificar el estado en Docker Desktop.
echo =======================================================
pause
