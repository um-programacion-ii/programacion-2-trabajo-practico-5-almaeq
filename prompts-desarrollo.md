# Prompts de Desarrollo

## Prompt 1: Entender qué hace el @Validated y si lo estoy usando bien

### Prompt Utilizado: 
[que hace el validated y esta bien usado aca? [codigo del EmpleadoController]]

### Respuesta Recibida:
La anotación @Validated en tu EmpleadoController es correcta y considerada una buena práctica, aunque no sea estrictamente necesaria para lo que tu código hace ahora mismo.

En resumen: habilita la validación para parámetros de métodos individuales (como @PathVariable y @RequestParam), no solo para los objetos completos del @RequestBody.

¿Qué hace @Validated?
@Validated es una anotación específica de Spring que hace dos cosas principales:

Activa la validación de grupos: Permite especificar diferentes conjuntos de reglas de validación para un mismo objeto en diferentes situaciones (por ejemplo, un grupo de reglas para crear un empleado y otro para actualizarlo).

Habilita la validación a nivel de método: Esta es la razón más común para usarla a nivel de clase en un @Controller. Permite que Spring valide los parámetros individuales de un método, como los que llevan @PathVariable, @RequestParam o @RequestHeader, siempre que estos tengan una anotación de validación (ej. @Min, @NotBlank, etc.).

La anotación estándar @Valid (de Jakarta Bean Validation) solo funciona para validar el objeto completo que se recibe, típicamente en un @RequestBody. No funciona para validar parámetros simples como un Long o un String.

¿Está bien usado en tu código? ✅
Sí, su uso es correcto y es una buena práctica.

En tu código actual, solo estás usando @Valid en los métodos crear y actualizar:

````bash
@PostMapping
@ResponseStatus(HttpStatus.CREATED)
public Empleado crear(@Valid @RequestBody Empleado empleado) { ... }

@PutMapping("/{id}")
public Empleado actualizar(@PathVariable Long id, @Valid @RequestBody Empleado empleado) { ... }
````

En este caso, la validación del objeto Empleado se activaría de todas formas por el @Valid en el @RequestBody.

Entonces, ¿por qué es bueno tener @Validated en la clase?

Porque prepara tu controlador para futuras validaciones más complejas. Imagina que quieres asegurarte de que el id que recibes nunca sea un número negativo o cero. Sin @Validated en la clase, esto no funcionaría:

````bash
// Esto SOLO funciona si la clase tiene @Validated
import javax.validation.constraints.Min;

@GetMapping("/{id}")
public Empleado obtenerPorId(@Min(1) @PathVariable Long id) { // <-- Añadimos validación aquí
return empleadoService.buscarPorId(id);
}
````
Al poner @Validated en la clase EmpleadoController, le dices a Spring: "Oye, revisa los métodos de este controlador por si alguno de sus parámetros necesita ser validado". Si no lo pones, Spring ignora la anotación @Min(1) en el @PathVariable y no se producirá ningún error si se envía un id inválido.

Diferencia clave: @Valid vs. @Validated

Característica |         @Valid (Estándar de Jakarta)              |   @Validated (Específico de Spring)
:-------------:-------------------------------------:------------------------------------:
      Origen   | Estándar de Java(JSR-303/Jakarta Bean Validation) |	Parte del Framework de Spring.
 Uso principal | Validar un objeto completo (un "bean"), como un @RequestBody. | Habilitar la validación a nivel de método para parámetros individuales (@PathVariable, @RequestParam).
Grupos de validación | No soporta grupos directamente en la firma del método.  | Sí, permite especificar grupos. Ej: @Validated(OnCreate.class).

💡 En resumen: Usa @Valid junto al objeto que quieres validar (@RequestBody). Usa @Validated a nivel de la clase (@Controller o @Service) para "encender" la capacidad de validar parámetros sueltos en los métodos.

Tu implementación es correcta porque, aunque ahora no la necesites para validar los @PathVariable, es una práctica recomendada que hace tu código más robusto y fácil de extender en el futuro.


### Modificaciones Realizadas:
- Borré el @Valid
- Lo modifiqué porque no consideré tan necesario el @valid si ya tenía el el @validated

### Explicación del Prompt:
Usé ese prompt porque quería ver si usar el @validated o el @valid o los dos

### Aprendizajes Obtenidos:
- Lo que hace el @Valid y el @Validated

## Prompt 2: Saber como conectar la parte de las excepciones con los controllers

### Prompt Utilizado:
[si tengo unas excepciones, eso como se aplica en los controllers?]

### Respuesta Recibida:

Para manejar excepciones en los controllers de Spring, el objetivo es centralizar la lógica de errores para no repetir código y enviar respuestas HTTP claras y consistentes al cliente.

Tienes principalmente tres formas de hacerlo, ordenadas de la menos a la más recomendada:

Bloques try-catch (No recomendado para controllers).

Anotación @ExceptionHandler (Útil para excepciones específicas de un solo controller).

Anotación @ControllerAdvice (La mejor práctica para un manejo global).

Método 1: try-catch (La forma a evitar ❌)
Podrías poner un try-catch dentro de cada método del controlador.

Ejemplo:

````bash
@GetMapping("/{id}")
public ResponseEntity<?> obtenerPorId(@PathVariable Long id) {
try {
Empleado empleado = empleadoService.buscarPorId(id); // Supongamos que esto lanza una excepción si no lo encuentra
return ResponseEntity.ok(empleado);
} catch (EmpleadoNotFoundException ex) {
return ResponseEntity.status(HttpStatus.NOT_FOUND).body(ex.getMessage());
}
}
````
¿Por qué es malo?

Repetitivo: Tienes que repetir el mismo bloque catch en muchos lugares.

Ensucia el código: El controlador se llena de lógica de manejo de errores en lugar de centrarse en su tarea principal: gestionar las rutas y el flujo de la solicitud.

Método 2: @ExceptionHandler (Una buena opción localizada ✅)
Puedes crear un método dentro de tu EmpleadoController que se encargue de capturar una excepción específica lanzada por cualquier otro método en ese mismo controlador.

Ejemplo:
Primero, necesitarías una excepción personalizada.

````bash
// src/main/java/com/example/sistemaGestionEmpleados/exceptions/EmpleadoNotFoundException.java
@ResponseStatus(HttpStatus.NOT_FOUND) // Asigna un status HTTP por defecto
public class EmpleadoNotFoundException extends RuntimeException {
public EmpleadoNotFoundException(String message) {
super(message);
}
}
````
Luego, en tu servicio, la lanzas cuando sea necesario.

````bash
// EmpleadoService.java
public Empleado buscarPorId(Long id) {
return empleadoRepository.findById(id)
.orElseThrow(() -> new EmpleadoNotFoundException("Empleado no encontrado con id: " + id));
}
````

Finalmente, la manejas en el controller.

````bash
// EmpleadoController.java
@RestController
@RequestMapping("/api/empleados")
public class EmpleadoController {

    // ... otros métodos como obtenerTodos(), crear(), etc.

    @GetMapping("/{id}")
    public Empleado obtenerPorId(@PathVariable Long id) {
        // El método del servicio ahora lanza la excepción directamente.
        // El controller está limpio.
        return empleadoService.buscarPorId(id);
    }

    // ESTE MÉTODO MANEJA LA EXCEPCIÓN PARA ESTE CONTROLLER
    @ExceptionHandler(EmpleadoNotFoundException.class)
    public ResponseEntity<String> handleEmpleadoNotFoundException(EmpleadoNotFoundException ex) {
        // Devuelves una respuesta HTTP 404 Not Found con el mensaje de la excepción.
        return ResponseEntity.status(HttpStatus.NOT_FOUND).body(ex.getMessage());
    }
}
````

Ventaja: Mantiene el código de los endpoints (@GetMapping, @PostMapping) limpio.
Desventaja: Solo funciona para este controlador. Si otro controlador (ej. DepartamentoController) también puede lanzar EmpleadoNotFoundException, tendrías que duplicar este manejador.

Método 3: @ControllerAdvice (La mejor práctica 🏆)
Esta es la forma recomendada. Creas una clase separada que actúa como un "asesor" global para todos tus controladores. Esta clase intercepta las excepciones lanzadas desde cualquier controlador y las maneja en un único lugar.

Es como tener un servicio de emergencias centralizado para toda tu API.

Paso 1: Crear la clase de manejo de excepciones global.
Se suele usar @RestControllerAdvice, que es una combinación de @ControllerAdvice y @ResponseBody.

````bash
// src/main/java/com/example/sistemaGestionEmpleados/exceptions/GlobalExceptionHandler.java
package com.example.sistemaGestionEmpleados.exceptions;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import java.util.Map;
import java.time.LocalDateTime;

@RestControllerAdvice
public class GlobalExceptionHandler {

    @ExceptionHandler(EmpleadoNotFoundException.class)
    public ResponseEntity<Map<String, Object>> handleEmpleadoNotFoundException(EmpleadoNotFoundException ex) {
        Map<String, Object> body = Map.of(
            "timestamp", LocalDateTime.now(),
            "status", HttpStatus.NOT_FOUND.value(),
            "error", "Recurso no encontrado",
            "message", ex.getMessage()
        );
        return new ResponseEntity<>(body, HttpStatus.NOT_FOUND);
    }

    // Puedes agregar más manejadores para otras excepciones aquí
    // Por ejemplo, para errores de validación
    // @ExceptionHandler(MethodArgumentNotValidException.class)
    // public ResponseEntity<?> handleValidationExceptions(...) { ... }

    // Un manejador genérico para cualquier otra excepción
    @ExceptionHandler(Exception.class)
    public ResponseEntity<Map<String, Object>> handleGeneralException(Exception ex) {
        Map<String, Object> body = Map.of(
            "timestamp", LocalDateTime.now(),
            "status", HttpStatus.INTERNAL_SERVER_ERROR.value(),
            "error", "Error interno del servidor",
            "message", "Ocurrió un error inesperado."
        );
        return new ResponseEntity<>(body, HttpStatus.INTERNAL_SERVER_ERROR);
    }
}
````

Paso 2: Simplificar el controlador.
Ahora tu controlador ya no necesita ningún método @ExceptionHandler local. Simplemente se dedica a su lógica.

````bash
// EmpleadoController.java
@RestController
@RequestMapping("/api/empleados")
public class EmpleadoController {

    private final EmpleadoService empleadoService;
    // ... constructor

    @GetMapping("/{id}")
    public Empleado obtenerPorId(@PathVariable Long id) {
        // Simplemente llama al servicio. Si se lanza la excepción,
        // GlobalExceptionHandler la atrapará.
        return empleadoService.buscarPorId(id);
    }

    // NO HAY MANEJADORES DE EXCEPCIONES AQUÍ. ¡CÓDIGO LIMPIO!
}
````
Resumen:
Usa @RestControllerAdvice para centralizar el manejo de excepciones. Esto mantiene tus controladores limpios, evita la duplicación de código y asegura que todos los errores en tu API devuelvan una respuesta JSON consistente y bien formateada.

### Modificaciones Realizadas:
- Creé un Exception Handler para cada excepcion
- Lo modifiqué para agarrar todas las excepciones

### Explicación del Prompt:
Usé ese prompt porque quería saber cómo conectar los controladores y las excepciones

### Aprendizajes Obtenidos:
- Cómo hacer un manejador de excepciones global y no tocar los controladores

## Prompt 3: Separar los yaml

### Prompt Utilizado:
[resulta q tengo q hacer yaml para cada uno asi quedan separados, como seria si esto es lo q tengo? [application.yaml]]

### Respuesta Recibida:

¡Excelente pregunta! Sí, puedes separar tu application.yaml en archivos individuales para cada perfil. De hecho, es una práctica muy común y recomendada para mantener la configuración más limpia y organizada.

Y la mejor parte: no, no tienes que cambiar absolutamente nada en el resto de tu código Java. Tu lógica de negocio, controladores, servicios y repositorios no se ven afectados por cómo organizas tus archivos de configuración.

Aquí te muestro cómo quedaría tu configuración separada en varios archivos .yaml. Simplemente tienes que crear estos archivos en la misma carpeta (src/main/resources/) y borrar el application.yaml monolítico.

1. Archivo Principal: application.yaml
   Este archivo ahora solo se encarga de definir configuraciones comunes o el perfil por defecto.

````bash
# src/main/resources/application.yaml

spring:
profiles:
# Perfil activo por defecto si no se especifica otro
active: dev
````

2. Perfil de Desarrollo: application-dev.yaml
   Spring Boot sabe que este archivo pertenece al perfil dev por el nombre application-dev.yaml.

````bash
# src/main/resources/application-dev.yaml

spring:
docker:
compose:
enabled: false
datasource:
url: jdbc:h2:mem:devdb
driver-class-name: org.h2.Driver
username: sa
password:
h2:
console:
enabled: true
path: /h2-console
jpa:
hibernate:
ddl-auto: create-drop
show-sql: true
properties:
hibernate:
format_sql: true
3. Perfil de Pruebas: application-h2.yaml
   Lo mismo para el perfil de pruebas h2.
````
````bash
# src/main/resources/application-h2.yaml

spring:
docker:
compose:
enabled: false
datasource:
url: jdbc:h2:mem:testdb
driver-class-name: org.h2.Driver
username: sa
password:
jpa:
hibernate:
ddl-auto: create-drop
show-sql: false
````

4. Perfil de MySQL: application-mysql.yaml
   Aquí mantenemos la solución al problema original, pero ahora está aislada en su propio archivo.
````bash
# src/main/resources/application-mysql.yaml

spring:
docker:
compose:
services:
postgres: # Ignora el servicio de postgres en Docker
enabled: false
datasource:
url: jdbc:mysql://localhost:3307/empleados_db?useSSL=false&serverTimezone=UTC
driver-class-name: com.mysql.cj.jdbc.Driver
username: empleados_user
password: empleados_pass
jpa:
hibernate:
ddl-auto: update
````

5. Perfil de PostgreSQL: application-postgres.yaml
   Finalmente, el perfil de postgres. Ojo aquí: a tu configuración original le faltaba la solución para desactivar mysql, así que la he añadido para que funcione correctamente.

````bash
# src/main/resources/application-postgres.yaml

spring:
# Ignora el servicio de mysql en Docker para evitar el conflicto
docker:
compose:
services:
mysql:
enabled: false
# -----------------------------
datasource:
url: jdbc:postgresql://localhost:5432/empleados_db
driver-class-name: org.postgresql.Driver
username: empleados_user
password: empleados_pass
jpa:
hibernate:
ddl-auto: update
````
Resumen de Ventajas:
Organización: Cada entorno (desarrollo, pruebas, producción con MySQL, etc.) tiene su propia configuración aislada.

Claridad: Es mucho más fácil ver y modificar la configuración de un perfil específico.

Sin cambios en el código: Como mencioné, tu código Java no necesita ninguna modificación.

La ejecución es idéntica: Sigues arrancando la aplicación de la misma manera:

````bash
./mvnw spring-boot:run -Dspring-boot.run.profiles=mysql
````

### Modificaciones Realizadas:
- Separé el yaml en los que me decía Gemini

### Explicación del Prompt:
Usé ese prompt porque no sabía en cuantos separarlos

### Aprendizajes Obtenidos:
- Es mejor práctica hace este tipo de yaml separados

