# Arquitectura del laboratorio

## Componentes y llamadas

```mermaid
flowchart LR
    Config[Config Server\nGit config-repo]
    Eureka[Eureka\n8761]
    Cliente[cliente-service\n8084]
    Pedido[pedido-service\n8085]
    Producto[producto-service\n8083]
    Categoria[categoria-service\n8082]
    DB1[(H2 clientes)]
    DB2[(H2 pedidos)]
    DB3[(H2 productos)]
    DB4[(H2 categorías)]

    Config --> Cliente
    Config --> Pedido
    Config --> Producto
    Config --> Categoria
    Cliente -.registro.-> Eureka
    Pedido -.registro.-> Eureka
    Producto -.registro.-> Eureka
    Categoria -.registro.-> Eureka
    Cliente -->|Circuit Breaker| Pedido
    Pedido -->|Retry| Cliente
    Pedido -->|Circuit Breaker + Bulkhead| Producto
    Producto -->|Circuit Breaker| Categoria
    Cliente --> DB1
    Pedido --> DB2
    Producto --> DB3
    Categoria --> DB4
```

## Fronteras de resiliencia

- `cliente-service → pedido-service`: Circuit Breaker `pedidoService`; fallback de pedidos vacío cuando pedido-service no está disponible.
- `pedido-service → cliente-service`: Retry `clienteRetry`; tres intentos como máximo, con dos segundos entre intentos y un fallback de cliente.
- `pedido-service → producto-service`: Bulkhead `productoBulkhead` limita a dos llamadas simultáneas y espera hasta un segundo; Circuit Breaker `productoService` detiene consultas lentas o fallidas y entrega un producto temporal.
- `producto-service → categoria-service`: Circuit Breaker `categoriaService`; los productos permanecen disponibles con una categoría temporal.

Config Server publica propiedades por nombre de aplicación y perfil. Cada servicio usa H2 y puerto `808x` en desarrollo; el perfil `prod` separa su puerto `909x` y su URL MySQL.
