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