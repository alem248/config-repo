# Análisis técnico y conclusiones

## Circuit Breaker

El Circuit Breaker registra el resultado de llamadas recientes a una dependencia. En estado cerrado deja pasar las llamadas; al superar el umbral configurado pasa a abierto y responde con el fallback sin insistir; luego de la ventana de espera permite llamadas de prueba en estado semiabierto. El laboratorio lo aplica en `cliente-service → pedido-service`, `pedido-service → producto-service` y `producto-service → categoria-service`.

Los fallbacks de este proyecto son respuestas degradadas y visibles: una lista vacía cuando pedido-service no responde, un cliente o producto temporal cuando se agota una consulta y una categoría temporal cuando categoria-service falla. El fallback no reemplaza la persistencia ni corrige la causa del error.

## Retry y Bulkhead

| Aspecto | Retry | Bulkhead |
|---|---|---|
| Qué controla | Repetición de una operación que falló. | Cuántas operaciones pueden estar activas a la vez. |
| Configuración del laboratorio | Hasta 3 intentos, con 2 s entre intentos, al consultar cliente-service. | Hasta 2 consultas concurrentes a producto-service; espera máxima de 1 s. |
| Efecto esperado | Puede recuperar un error transitorio; en la latencia de 5 s cada llamada supera el timeout de 3.5 s y termina en fallback. | Las dos primeras llamadas lentas ocupan los permisos; una adicional espera hasta 1 s y usa fallback al no obtener permiso. |
| Riesgo si se usa mal | Multiplica carga y alarga la respuesta cuando el error es persistente. | Un límite demasiado bajo rechaza tráfico válido; uno demasiado alto no aísla recursos. |
| Escenario aconsejable | Fallos breves e intermitentes, cuando repetir es seguro y cabe en el presupuesto de tiempo. | Dependencias lentas o de capacidad acotada, cuando se debe limitar concurrencia y evitar saturación en cascada. |

Retry y Bulkhead resuelven problemas distintos. Retry da más oportunidades a una llamada individual; Bulkhead pone un techo a las llamadas simultáneas. Conviene combinarlos con timeout y Circuit Breaker, dimensionando sus ventanas para que los reintentos no formen una tormenta de solicitudes.

## Conclusiones

1. La centralización con Spring Cloud Config permite mantener perfiles `dev` y `prod` por servicio en Git y consultar su resolución mediante el endpoint del Config Server.
2. Los perfiles separan el entorno local con H2 del entorno de producción con MySQL, puertos y política de inicialización apropiados para cada contexto.
3. Circuit Breaker corta temporalmente llamadas a dependencias que fallan y permite que los consumidores respondan con un fallback controlado.
4. Retry puede recuperar fallos transitorios, pero los intentos y esperas deben caber en el timeout global para no aumentar la latencia ni la carga.
5. Bulkhead limita la concurrencia hacia una dependencia lenta y protege recursos del servicio consumidor, mientras que el fallback hace explícito el resultado degradado.
