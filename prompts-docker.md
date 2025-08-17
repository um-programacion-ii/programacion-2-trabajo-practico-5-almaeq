# Prompts de Docker

## Prompt 1: Error con las bases

### Prompt Utilizado:
[me da esto: [Error creating bean with name 'dataSourceScriptDatabaseInitializer' defined in class path resource 
[org/springframework/boot/autoconfigure/sql/init/DataSourceInitializationConfiguration.class]: Unsatisfied dependency expressed through method 
'dataSourceScriptDatabaseInitializer' parameter 0: Error creating bean with name 'dataSource' defined in class path resource 
[org/springframework/boot/autoconfigure/jdbc/DataSourceConfiguration$Hikari.class]: Unsatisfied dependency expressed through method 'dataSource' parameter 1: 
No qualifying bean of type 'org.springframework.boot.autoconfigure.jdbc.JdbcConnectionDetails' available: expected single matching bean but found 2:
jdbcConnectionDetailsForEmpleados_mysql,jdbcConnectionDetailsForEmpleados_postgres]]

### Respuesta Recibida:

El error es claro:

“expected single matching bean but found 2: jdbcConnectionDetailsForEmpleados_mysql, jdbcConnectionDetailsForEmpleados_postgres”

Spring Boot (3.5.x) te está creando dos JdbcConnectionDetails a partir de tu docker-compose.yaml (uno para mysql y otro para postgres) y, como los service connections tienen prioridad sobre spring.datasource.*, Hibernate no sabe cuál usar y se cae el contexto.

Soluciones (elige una)
Opción A — Activar Compose profile correcto (recomendado)

En tu docker-compose.yaml, etiqueta cada servicio con un perfil:

````bash
services:
mysql:
image: mysql:8.0
profiles: ["mysql"]
container_name: empleados_mysql
environment:
MYSQL_ROOT_PASSWORD: root_password
MYSQL_DATABASE: empleados_db
MYSQL_USER: empleados_user
MYSQL_PASSWORD: empleados_pass
ports: ["3307:3306"]
volumes: [ "mysql_data:/var/lib/mysql" ]
networks: [ "empleados_network" ]

postgres:
image: postgres:16
profiles: ["postgres"]
container_name: empleados_postgres
environment:
POSTGRES_DB: empleados_db
POSTGRES_USER: empleados_user
POSTGRES_PASSWORD: empleados_pass
ports: ["5432:5432"]
healthcheck:
test: ["CMD-SHELL", "pg_isready -U empleados_user -d empleados_db"]
timeout: 20s
retries: 10
volumes: [ "postgres_data:/var/lib/postgresql/data" ]
networks: [ "empleados_network" ]

volumes:
mysql_data: {}
postgres_data: {}
networks:
empleados_network:
driver: bridge
````

En application-mysql.yaml activa el perfil de Compose “mysql” (y quita la clave que intentaba “deshabilitar” postgres, porque no es una propiedad válida):
````bash
spring:
  docker:
    compose:
      profiles:
        active: mysql
  jpa:
    hibernate:
      ddl-auto: update
````

(Opcional) En application-postgres.yaml:
````bash
spring:
  docker:
    compose:
      profiles:
        active: postgres
  jpa:
    hibernate:
      ddl-auto: update
````

Con esto, al levantar con -Dspring-boot.run.profiles=mysql, Spring Boot solo creará el service connection de MySQL y desaparecerá el choque de beans.

Opción B — Ignorar explícitamente el servicio que no quieras

Si preferís no usar profiles de Compose, podés ignorar el contenedor no deseado con una etiqueta:
````bash
services:
  postgres:
    image: postgres:16
    labels:
      org.springframework.boot.ignore: "true"
````

Esa etiqueta le dice a Spring Boot que no cree JdbcConnectionDetails para ese servicio aunque esté corriendo.

Opción C — Apagar el servicio que sobra

Pará o eliminá el contenedor que no usás:
````bash
docker compose stop postgres   # o mysql, según el caso
# o directamente
docker compose up mysql        # sólo mysql
````

(Si solo queda un JDBC service, ya no hay conflicto.)

Qué tocar en tu repo ahora mismo

Quitar de application-mysql.yaml la sección:
````bash
spring:
  docker:
    compose:
      services:
        postgres:
          enabled: false
````

(no existe esa propiedad y no surte efecto).

Elegir A (profiles) o B (labels) y aplicar.

Con eso, al volver a correr:
````bash
./mvnw spring-boot:run -Dspring-boot.run.profiles=mysql
````

la app debería iniciar sin el NoUniqueBeanDefinitionException de JdbcConnectionDetails.

### Modificaciones Realizadas:
- Agregué los perfiles en el docker-compose y arreglé los yaml

### Explicación del Prompt:
Usé ese prompt porque me daba error y no sabía cómo arreglarlo

### Aprendizajes Obtenidos:
- Que hay que agregar el perfil 