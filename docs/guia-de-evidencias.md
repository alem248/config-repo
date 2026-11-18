# Guía paso a paso para las evidencias

## Preparar la sesión

1. Confirma que Config Server puede leer los archivos de `config-repo` desde `https://github.com/alem248/config-repo.git` y que la rama remota es `main`.
2. Abre seis consolas de PowerShell y ejecuta los módulos en el orden indicado en el README: Config Server, Eureka, categoría, producto, cliente y pedido.
3. En el navegador consulta `/categoria-service/dev`, `/categoria-service/prod`, `/producto-service/dev`, `/producto-service/prod`, y los perfiles de cliente y pedido. Captura el JSON de Config Server mostrando `server.port` y la URL H2 o MySQL para probar que cada perfil se resolvió.
4. Abre `http://localhost:8761` y captura Eureka con las cuatro aplicaciones registradas en estado `UP`.

## Evidencia 1: comportamiento normal

Antes de activar latencias o detener procesos:

1. En el navegador abre `http://localhost:8083/api/productos`. Captura la respuesta que contiene categoría y producto.
2. En otra pestaña abre `http://localhost:8085/api/pedidos`. Captura los pedidos con cliente y producto enriquecidos.
3. Conserva visibles las consolas de producto, cliente y pedido. Captura los logs de consulta sin mensajes `DEMO_LATENCY` ni fallbacks. Esta es la referencia previa a la falla.

## Evidencia 2: Retry al consultar cliente-service

La demora se activa y desactiva por endpoints `PUT` disponibles solo en `dev`. Ejecuta en una consola de PowerShell:

```powershell
Invoke-RestMethod -Method Put -Uri 'http://localhost:8084/api/demo/latencia?enabled=true'
```

1. Captura la consola de cliente-service mostrando `Latencia de demostración activada`.
2. Inicia `http://localhost:8085/api/pedidos/1` en el navegador. Cerca de los 4 s, captura las consolas: el intento inicial debe haber superado el timeout de Feign de 3.5 s y Pedido debe registrar otro `Retry: consultando cliente-service`.
3. Al terminar, captura la respuesta JSON. El cliente debe ser `Cliente temporal no disponible`; en la consola de pedido debe aparecer `Fallback de cliente`. Los tres intentos máximos toman aproximadamente 14.5 s (3.5 s de timeout por intento y 2 s entre intentos).
4. Desactiva la demora para las siguientes pruebas:

```powershell
Invoke-RestMethod -Method Put -Uri 'http://localhost:8084/api/demo/latencia?enabled=false'
```

## Evidencia 3: Bulkhead y demora de producto-service

1. Activa la demora de diez segundos:

```powershell
Invoke-RestMethod -Method Put -Uri 'http://localhost:8083/api/demo/latencia?enabled=true'
```

2. En una consola que no sea la de ningún microservicio, lanza cuatro consultas concurrentes a un pedido:

```powershell
$jobs = 1..4 | ForEach-Object {
    Start-Job -ScriptBlock {
        Invoke-RestMethod -Uri 'http://localhost:8085/api/pedidos/1' -TimeoutSec 40
    }
}
$jobs | Select-Object Id, State
```

3. Inmediatamente después de lanzar los trabajos, captura la lista de trabajos en ejecución y las consolas de pedido y producto. En producto deben aparecer como máximo dos solicitudes ocupando simultáneamente el Bulkhead; en pedido se registrará el fallback de las llamadas que exceden el límite y la espera de 1 s.
4. Cuando terminen, captura las respuestas y los eventos de Bulkhead. Recupera los resultados con `Wait-Job $jobs` y `Receive-Job $jobs`.
5. Para observar el Circuit Breaker de producto, realiza consultas sucesivas a `http://localhost:8085/api/pedidos/1` mientras la latencia sigue activa. Captura el fallback `Producto temporal no disponible` cuando el Circuit Breaker se abra; la consola debe mostrar que ya no se inicia una llamada nueva a producto-service.
6. Desactiva la demora y captura el endpoint de estado para dejar constancia:

```powershell
Invoke-RestMethod -Method Put -Uri 'http://localhost:8083/api/demo/latencia?enabled=false'
Invoke-RestMethod -Uri 'http://localhost:8083/api/demo/latencia'
```

La transición desde Circuit Breaker abierto espera 15 s antes de permitir pruebas en estado semiabierto. Espera esa ventana antes de volver a capturar el comportamiento recuperado.

## Evidencia 4: caída de cliente-service → pedido-service

1. Detén el proceso pedido-service con `Ctrl+C` en su consola. Captura la consola detenida y deja visible la consola de cliente-service.
2. En el navegador solicita `http://localhost:8084/api/clientes/1/pedidos`. Repite tres veces para superar el mínimo de llamadas del Circuit Breaker. Captura el JSON `[]` y los mensajes `Circuit Breaker: pedido-service no disponible`.
3. Captura el endpoint de Actuator `http://localhost:8084/actuator/circuitbreakers` para mostrar el estado del Circuit Breaker.
4. Reinicia pedido-service. Tras la ventana abierta de 10 s, repite la consulta y captura la respuesta con pedidos.

## Evidencia 5: caída de categoria-service → producto-service

1. Detén categoria-service con `Ctrl+C`.
2. Solicita varias veces `http://localhost:8083/api/productos` hasta alcanzar el mínimo de llamadas configurado. Captura que los productos siguen disponibles y que la categoría usa `Categoría temporal no disponible`.
3. Captura los logs `Circuit Breaker activado` en producto-service y el estado de `/actuator/circuitbreakers`.
4. Reinicia categoria-service y, luego de la ventana abierta de 10 s, vuelve a consultar productos para documentar la recuperación.

## Orden sugerido de las capturas

1. Perfiles `dev` y `prod` en Config Server.
2. Eureka con servicios saludables.
3. Respuestas normales de `/api/productos` y `/api/pedidos` antes de activar fallas.
4. Latencia y reintentos durante la consulta al cliente.
5. Llamadas concurrentes durante la latencia de producto y respuesta Bulkhead.
6. Fallback posterior al Circuit Breaker abierto.
7. Caída y recuperación de pedido-service consumido por cliente-service.
8. Caída y recuperación de categoria-service consumido por producto-service.

En cada captura conserva visibles la URL o comando utilizado y la consola que explica el resultado. Vuelve a ejecutar la consulta después de restaurar cada servicio para cerrar la evidencia con una recuperación observada.
