# Configuración central de Lab 14

Este directorio contiene los archivos que consume `config-server`. Publica su contenido en la rama `main` del repositorio indicado por el laboratorio:

`https://github.com/alem248/config-repo.git`

Los nombres de los archivos deben conservarse porque Spring Cloud Config los resuelve a partir de `spring.application.name` y del perfil activo. El servidor del workspace ya apunta a esa URL. La configuración de desarrollo usa bases H2 en memoria; producción usa MySQL y variables de entorno.
