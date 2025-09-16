package com.example.sistemaGestionEmpleados.services;

import com.example.sistemaGestionEmpleados.exceptions.ProyectoNoEncontradoException;
import com.example.sistemaGestionEmpleados.models.Empleado;
import com.example.sistemaGestionEmpleados.models.Proyecto;
import com.example.sistemaGestionEmpleados.repositories.EmpleadoRepository;
import com.example.sistemaGestionEmpleados.repositories.ProyectoRepository;
import jakarta.transaction.Transactional;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

@Service
@Transactional
public class ProyectoServiceImpl implements ProyectoService {

    private final ProyectoRepository proyectoRepository;
    private final EmpleadoRepository empleadoRepository;

    public ProyectoServiceImpl(ProyectoRepository proyectoRepository, EmpleadoRepository empleadoRepository) {
        this.proyectoRepository = proyectoRepository;
        this.empleadoRepository = empleadoRepository;
    }

    @Override
    public Proyecto guardar(Proyecto proyecto) {
        return proyectoRepository.save(proyecto);
    }

    @Override
    public Proyecto buscarPorId(Long id) {
        return proyectoRepository.findById(id)
                .orElseThrow(() -> new ProyectoNoEncontradoException("Proyecto no encontrado con ID: " + id));
    }

    @Override
    public List<Proyecto> obtenerTodos() {
        return proyectoRepository.findAll();
    }

    @Override
    public Proyecto actualizar(Long id, Proyecto proyecto) {
        if (!proyectoRepository.existsById(id)) {
            throw new ProyectoNoEncontradoException("Proyecto no encontrado con ID: " + id);
        }
        proyecto.setId(id);
        return proyectoRepository.save(proyecto);
    }

    @Override
    public void eliminar(Long id) {
        if (!proyectoRepository.existsById(id)) {
            throw new ProyectoNoEncontradoException("Proyecto no encontrado con ID: " + id);
        }
        proyectoRepository.deleteById(id);
    }

    @Override
    public List<Proyecto> buscarPorProyectosActivos(){
        return proyectoRepository.findByFechaFinAfter(LocalDate.now());
    }

    @Override
    public Proyecto asignarEmpleadosAProyecto(Long proyectoId, Set<Long> empleadosIds) {
        // 1. Buscas el proyecto
        Proyecto proyecto = buscarPorId(proyectoId);

        // 2. Buscas los nuevos empleados
        Set<Empleado> nuevosEmpleados = new HashSet<>(empleadoRepository.findAllById(empleadosIds));

        // 3. Desvincular los empleados antiguos
        // Se crea una copia para evitar ConcurrentModificationException
        Set<Empleado> empleadosAntiguos = new HashSet<>(proyecto.getEmpleados());
        for (Empleado empleadoAntiguo : empleadosAntiguos) {
            empleadoAntiguo.getProyectos().remove(proyecto);
        }

        // 4. Limpiar la lista actual del proyecto
        proyecto.getEmpleados().clear();

        // 5. Sincronizar la relación desde ambos lados para los nuevos empleados
        for (Empleado nuevoEmpleado : nuevosEmpleados) {
            proyecto.getEmpleados().add(nuevoEmpleado);
            nuevoEmpleado.getProyectos().add(proyecto);
        }

        // 6. Guardar la entidad propietaria de la relación
        return proyectoRepository.save(proyecto);
    }
}
