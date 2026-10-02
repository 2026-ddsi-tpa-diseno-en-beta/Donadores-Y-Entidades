# Donadores y Entidades: ejecución y cambios de entrega 5

Requiere Java 21 y Maven. Validación: `mvn package`. En Logística `verify` también exige 80% de cobertura; no se cambió ese umbral.

Una reserva desde stock no incrementa la cantidad entregada. Las necesidades recurrentes se renuevan por semana o mes. Los errores de integración se propagan: no se informa un alta exitosa si falló la reserva.

Configurar las URLs de integración definidas en application.properties. En producción usar variables de entorno para conexión, usuario y contraseña de PostgreSQL y para las integraciones. No reemplazar application.properties de producción con las configuraciones H2 de prueba.

Swagger: `/swagger-ui/index.html`; contrato: `/v3/api-docs`; salud: `/actuator/health`; métricas: `/actuator/prometheus`. Los cuatro componentes propagan `X-Trace-Id`. Para logs centralizados configurar `BETTERSTACK_SOURCE_TOKEN` y `BETTERSTACK_INGEST_URL`; verificar la recepción en la cuenta del equipo.

La integración de los seis flujos está en el repo testing, `local/probar_integracion.py`, y requiere los cuatro repos como carpetas hermanas. La suite usa H2 aislado y simula callbacks del worker; no valida un broker externo. MCP está como módulo independiente en `testing/mcp-server` y el bot en telegramBot.

El despliegue todavía requiere probar la base existente, las credenciales reales y las URLs públicas. Una llamada HTTP entre servicios no participa de la transacción de la base local: si falla otro componente durante una operación, revisar el resultado con el traceId antes de reintentar.

Las necesidades agregan columnas `periodo` e `inicio_periodo`. Revisar el cambio de esquema antes de desplegar sobre una base existente. Para filas históricas sin período, el código usa SEMANAL y conserva la cantidad registrada al inicializar el período. GET /necesidades/{id}/periodo devuelve el inicio vigente. Las asignaciones antiguas necesitan replanificación operativa.
