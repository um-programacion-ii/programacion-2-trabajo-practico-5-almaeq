package com.example.sistemaGestionEmpleados.models;

import com.fasterxml.jackson.annotation.JsonIdentityInfo;
import com.fasterxml.jackson.annotation.ObjectIdGenerators;
import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.persistence.*;
import lombok.*;

import java.time.LocalDate;
import java.util.HashSet;
import java.util.Set;

@Entity
@Table(name = "proyectos")
@Getter
@Setter
@ToString(exclude = "empleados") // Excluye la relación
@EqualsAndHashCode(of = "id") // Basa la igualdad solo en el ID
@JsonIdentityInfo(generator = ObjectIdGenerators.PropertyGenerator.class, property = "id")
@NoArgsConstructor
@AllArgsConstructor
public class Proyecto {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Schema(description = "ID único del proyecto, generado automáticamente.",
            example = "55", accessMode = Schema.AccessMode.READ_ONLY)
    private Long id;

    @Column(nullable = false, length = 100)
    @Schema(description = "Nombre clave del proyecto.", example = "Proyecto Titán")
    private String nombre;

    @Column(length = 1000)
    @Schema(description = "Breve descripción del objetivo del proyecto.", example = "Migración de la base de datos a un entorno cloud.")
    private String descripcion;

    @Column(name = "fecha_inicio")
    @Temporal(TemporalType.DATE)
    @Schema(description = "Fecha de inicio planificada para el proyecto.", example = "2025-01-15")
    private LocalDate fechaInicio;

    @Column(name = "fecha_fin")
    @Temporal(TemporalType.DATE)
    @Schema(description = "Fecha de finalización estimada del proyecto.", example = "2025-12-31")
    private LocalDate fechaFin;

    @ManyToMany(mappedBy = "proyectos")
    private Set<Empleado> empleados = new HashSet<>();
}