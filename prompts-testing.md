# Prompts de Testing

## Prompt 1: Entender qué es el @Autowired

### Prompt Utilizado:
[que es @Autowired? [codigo del EmpleadoServiceIntegrationTest]]

### Respuesta Recibida:

Claro. La anotación @Autowired es uno de los mecanismos más fundamentales en el framework de Spring para realizar la inyección de dependencias.

En pocas palabras, @Autowired le dice a Spring: "encuentra un objeto (un bean) que coincida con este tipo y asígnamelo automáticamente a esta variable".

Analogía: Construyendo un Coche 🚗
Imagina que estás construyendo un coche (EmpleadoController). Este coche necesita un motor (EmpleadoService) para funcionar.

Sin inyección de dependencias: Tendrías que construir el motor tú mismo, dentro del coche.

````bash
public class Coche {
private Motor motor;

    public Coche() {
        this.motor = new Motor(); // ¡Tú creas el motor!
    }
}
````
Con inyección de dependencias (@Autowired): Simplemente declaras que necesitas un motor. El "mecánico" (el contenedor de Spring) se encarga de buscar un motor compatible ya fabricado y lo instala por ti.

````bash
public class Coche {
@Autowired // Spring, por favor, inyecta un motor aquí
private Motor motor;

    public Coche() {
        // El motor ya está instalado cuando lo necesitas
    }
}
````

Spring se encarga de crear y gestionar el ciclo de vida de tus objetos (los llama "beans"). Cuando encuentra @Autowired en un campo, constructor o método, busca en su "almacén" (el Contenedor de Inversión de Control o IoC) un bean que sea del tipo requerido y lo "inyecta".

Tipos de Inyección con @Autowired
Hay tres lugares comunes donde puedes usar @Autowired:

1. Inyección por Campo (Field Injection)

Es la forma más simple, pero no la más recomendada para dependencias obligatorias.

Ejemplo en tu código (EmpleadoServiceIntegrationTest.java):

````bash
@SpringBootTest
class EmpleadoServiceIntegrationTest {
@Autowired
private EmpleadoService empleadoService; // Inyección por campo

    @Autowired
    private EmpleadoRepository empleadoRepository; // Inyección por campo
    // ...
}
````

Aquí, Spring inyecta las instancias de EmpleadoService y EmpleadoRepository directamente en los campos de la clase de prueba.

2. Inyección por Constructor (Constructor Injection)

Esta es la práctica recomendada y moderna. La dependencia se solicita como un parámetro en el constructor de la clase.

Ejemplo en tu código (EmpleadoController.java):

````bash
@RestController
@RequestMapping("/api/empleados")
public class EmpleadoController {
private final EmpleadoService empleadoService; // La dependencia es 'final'

    // A partir de Spring 4.3, si la clase tiene un solo constructor,
    // la anotación @Autowired es opcional. Spring lo hace automáticamente.
    public EmpleadoController(EmpleadoService empleadoService) {
        this.empleadoService = empleadoService;
    }
    // ...
}
````
Ventajas:

Dependencias explícitas: Queda claro qué necesita la clase para funcionar.

Inmutabilidad: Puedes declarar los campos como final, lo que significa que una vez asignados, no pueden cambiar. Esto hace tu código más robusto.

Facilidad para las pruebas: Es muy fácil crear una instancia de la clase en una prueba unitaria, ya que simplemente pasas los mocks (objetos simulados) al constructor.

3. Inyección por Setter (Setter Injection)

La inyección se realiza a través de un método set. Es menos común y se usa principalmente para dependencias opcionales que pueden cambiarse en tiempo de ejecución.

````bash
public class Coche {
private Motor motor;

    @Autowired
    public void setMotor(Motor motor) {
        this.motor = motor;
    }
}
````

### Modificaciones Realizadas:
- No realicé ninguna modificación

### Explicación del Prompt:
Usé ese prompt porque quería entender bien que es lo que hace el @Autowired

### Aprendizajes Obtenidos:
- Lo que hace el @Autowired y como funciona en mi código


# Prompts de Testing

## Prompt 2: Verificar q tengo bien el application yaml

### Prompt Utilizado:
[tengo un application yaml [application.yaml]]

### Respuesta Recibida:

Tu archivo application.yml sirve, pero no es la mejor práctica para configurar los tests. Estás mezclando la configuración de desarrollo con la de otros entornos, lo que puede traer problemas.

Lo ideal es crear perfiles exclusivamente para testing. Esto asegura que tus pruebas siempre se ejecuten en un entorno aislado y predecible, sin importar cómo cambie la configuración de desarrollo.

Por qué tu configuración actual funciona (pero no es ideal)
Tu archivo application.yml es funcional. Podrías anotar tus clases de prueba con @ActiveProfiles("dev") y se ejecutarían usando tu base de datos H2 en memoria.

El problema es el riesgo y la falta de claridad:

Falta de Aislamiento: El perfil dev es para desarrollo. ¿Qué pasa si mañana decides cambiar la configuración de dev para que use una base de datos H2 en disco (y no en memoria) o cambias el ddl-auto a update para no perder tus datos mientras programas? Tus tests, que dependen de una base de datos limpia cada vez, empezarían a fallar de forma inesperada.

Confusión de Intenciones: Mezclar perfiles de entorno (mysql, postgres) con perfiles de desarrollo (dev) en el mismo nivel que el testing puede ser confuso. Un perfil test deja claro que esa configuración es solo para pruebas automatizadas.

La Mejor Práctica: Crear un Perfil de Test Dedicado
No necesitas crear nuevos archivos .properties. Puedes adaptar tu application.yml para que sea más robusto y claro.

Recomendación: Adapta tu application.yml
Añade un nuevo perfil llamado test que contenga la configuración de la base de datos en memoria. Este perfil será usado exclusivamente por tus pruebas.

````bash
spring:
profiles:
# El perfil activo por defecto para desarrollo sigue siendo 'dev'
active: dev

---
spring:
config:
activate:
on-profile: dev
# ... (Tu configuración de 'dev' se queda como está) ...
datasource:
url: jdbc:h2:mem:devdb # Cambia el nombre para diferenciarla
driver-class-name: org.h2.Driver
# ...

---
# =======================================================
# NUEVO PERFIL EXCLUSIVO PARA TESTING
# =======================================================
spring:
config:
activate:
on-profile: test
datasource:
# Usa una URL diferente para asegurar el aislamiento total
url: jdbc:h2:mem:testdb
driver-class-name: org.h2.Driver
username: sa
password:
jpa:
hibernate:
# 'create-drop' es perfecto para tests: crea el esquema, ejecuta los tests y lo borra.
ddl-auto: create-drop
show-sql: false # Generalmente no necesitas ver el SQL en los logs de los tests

---
spring:
config:
activate:
on-profile: mysql
# ... (Tu configuración de 'mysql' se queda como está) ...

---
spring:
config:
activate:
on-profile: postgres
# ... (Tu configuración de 'postgres' se queda como está) ...

````

¿Cómo lo usas en tus tests?
Ahora, en todas tus clases de prueba (@DataJpaTest o @SpringBootTest), simplemente activas este nuevo perfil. Es más limpio, seguro y explícito.

````bash
@DataJpaTest
@ActiveProfiles("test") // <-- Le dices a Spring que use la configuración del perfil 'test'
class EmpleadoRepositoryTest {
// ... tus tests ...
}
````
Ventajas de este enfoque:

✅ Aislamiento Total: Tu configuración de dev puede cambiar libremente sin afectar jamás a tus pruebas.

✅ Claridad: Cualquiera que vea el archivo YAML y los tests entiende inmediatamente qué configuración se está utilizando.

✅ Seguridad: Eliminas el riesgo de que tus pruebas se ejecuten accidentalmente contra una base de datos de desarrollo o, peor aún, una de producción.

### Modificaciones Realizadas:
- Agregué lo que me recomendó la IA

### Explicación del Prompt:
Usé ese prompt porque quería ver como se usaba lo de los perfiles en los tests de repository

### Aprendizajes Obtenidos:
- Aprendí q es mucho mejor tener los tests totalmente aislados del resto del código
