@echo off
echo =======================================================
echo EXPORTANDO IMAGENES DOCKER DE STAEM PARA MODO OFFLINE
echo =======================================================
echo.
echo Paso 1: Asegurando que todas las imagenes esten construidas y actualizadas...
docker compose build

echo.
echo Paso 2: Empaquetando imagenes en staem-proyecto.tar (Esto puede tardar unos minutos)...
docker save -o staem-proyecto.tar staem-usuarios staem-catalogo staem-pagos staem-biblioteca staem-resenas staem-carrito staem-promociones staem-amigos staem-logros staem-soporte staem-api-gateway staem-eureka-server

echo.
if %errorlevel% equ 0 (
    echo =======================================================
    echo EXITO: El archivo staem-proyecto.tar ha sido creado.
    echo Puedes copiar toda esta carpeta a un Pendrive.
    echo =======================================================
) else (
    echo =======================================================
    echo ERROR: Hubo un problema al exportar las imagenes.
    echo Asegurate de que Docker Desktop este corriendo.
    echo =======================================================
)
pause
