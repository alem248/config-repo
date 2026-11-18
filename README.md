# Lab 14 Config Server y resiliencia

Proyecto Maven multimódulo para practicar configuración centralizada, perfiles `dev`/`prod` y tolerancia a fallos con Spring Boot, Spring Cloud y Resilience4j. Incluye los cuatro microservicios del laboratorio, Config Server, Eureka y datos iniciales H2 para ejecutar las demostraciones sin preparar MySQL.

## Módulos y puertos

| Módulo | `dev` | `prod` | Función |
|---|---:|---:|---|
| `config-server` | 8888 | 8888 | Lee configuración desde `https://github.com/alem248/config-repo.git`. |
| `discovery-server` | 8761 | 8761 | Registro Eureka para las llamadas Feign por nombre. |
| `categoria-service` | 8082 | 9092 | Consulta de categorías y fallback desde producto. |
| `producto-service` | 8083 | 9093 | Consulta de productos y latencia de demostración de 10 s. |
| `cliente-service` | 8084 | 9094 | Clientes, pedidos consultados por cliente y latencia de 5 s. |
| `pedido-service` | 8085 | 9095 | Agrega cliente y producto; implementa Retry, Bulkhead y Circuit Breaker. |

Las configuraciones fuente para Config Server están en [`config-repo`](config-repo). Si el repositorio remoto aún no tiene esos archivos, publícalos en su rama `main` antes de iniciar las aplicaciones. Config Server apunta a la URL requerida y puede recibir otra URL mediante `CONFIG_REPO_URI`.

## Requisitos

- Java 17 y Maven 3.9 o compatible.
- Git y acceso HTTPS a GitHub y Maven Central.
- MySQL solo para ejecutar con perfil `prod`; `dev` usa H2 en memoria.

## Preparación de Config Server

Confirma que el repositorio remoto contiene los 12 archivos `.yml` de [`config-repo`](config-repo). Si debes cargarlos, clona el repositorio remoto, copia allí los archivos de configuración y publica el cambio en `main`:

```powershell
$labRoot = (Get-Location).Path
git clone https://github.com/alem248/config-repo.git "$env:TEMP\config-repo"
Copy-Item .\config-repo\*.yml "$env:TEMP\config-repo" -Force
Set-Location "$env:TEMP\config-repo"
git add *.yml
git commit -m "Add dev and prod profiles for lab services"
git push origin main
Set-Location $labRoot
```

Necesitas acceso de escritura al repositorio solo para publicar sus propiedades. Si ya contiene los archivos equivalentes, no repitas la publicación.

## Ejecución local

Abre una terminal de PowerShell por módulo y ejecuta `mvn spring-boot:run`. Inicia en este orden:

```powershell
Set-Location .\config-server
mvn spring-boot:run
```

```powershell
Set-Location .\discovery-server
mvn spring-boot:run
```

```powershell
Set-Location .\categoria-service
mvn spring-boot:run
```

```powershell
Set-Location .\producto-service
mvn spring-boot:run
```

```powershell
Set-Location .\cliente-service
mvn spring-boot:run
```

```powershell
Set-Location .\pedido-service
mvn spring-boot:run
```

Antes de iniciar los servicios, consulta Config Server y Eureka en el navegador:

- `http://localhost:8888/categoria-service/default`
- `http://localhost:8888/producto-service/default`
- `http://localhost:8888/categoria-service/dev`
- `http://localhost:8888/cliente-service/prod`
- `http://localhost:8761`

Config Server debe mostrar las propiedades del perfil pedido y Eureka debe listar las instancias en estado `UP`.

## Verificación manual de endpoints

- `GET http://localhost:8082/api/categorias/1`
- `GET http://localhost:8083/api/productos` — enriquece los productos con categoría.
- `GET http://localhost:8084/api/clientes/1`
- `GET http://localhost:8084/api/clientes/1/pedidos` — cliente consulta pedidos mediante Circuit Breaker.
- `GET http://localhost:8085/api/pedidos` — respuesta enriquecida con datos de cliente y producto.
- `GET http://localhost:8085/api/pedidos/1` — consulta individual para la evidencia de Retry y Bulkhead.
- `GET http://localhost:8085/api/pedidos/cliente/1` — contrato compacto consumido por cliente-service.

La guía completa de ejecución y captura está en [`docs/guia-de-evidencias.md`](docs/guia-de-evidencias.md). El análisis comparativo y las cinco conclusiones están en [`docs/analisis-y-conclusiones.md`](docs/analisis-y-conclusiones.md).

## Selección del perfil

Cada aplicación activa `dev` por defecto. Para iniciar una con `prod`, establece el perfil en su consola antes de ejecutar Maven:

```powershell
$env:SPRING_PROFILES_ACTIVE = 'prod'
mvn spring-boot:run
```

Configura `CATEGORIA_DB_URL`, `PRODUCTO_DB_URL`, `CLIENTE_DB_URL` y `PEDIDO_DB_URL`, además de `DB_USERNAME` y `DB_PASSWORD`, antes de ejecutar `prod`. Ese perfil usa MySQL, el puerto `909x`, `ddl-auto: validate` y no carga los datos semilla; las bases y tablas deben existir previamente.

## Historial Git

El workspace contiene commits granulares que muestran la secuencia de construcción. Revisa el historial con:

```powershell
git log --oneline --reverse
```
