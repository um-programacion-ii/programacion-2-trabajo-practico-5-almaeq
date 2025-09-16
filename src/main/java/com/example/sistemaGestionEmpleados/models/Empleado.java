package com.example.sistemaGestionEmpleados.models;

import com.fasterxml.jackson.annotation.JsonBackReference;
import com.fasterxml.jackson.annotation.JsonIdentityInfo;
import com.fasterxml.jackson.annotation.ObjectIdGenerators;
import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.persistence.*;
import lombok.*;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.HashSet;
import java.util.Set;

@Entity
@Table(name = "empleados")
@Getter
@Setter
@JsonIdentityInfo(generator = ObjectIdGenerators.PropertyGenerator.class, property = "id")
@NoArgsConstructor
@AllArgsConstructor
@ToString(exclude = {"departamento", "proyectos"}) // Excluye las relaciones
@EqualsAndHashCode(of = "id") // Basa la igualdad solo en el ID
public class Empleado {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Schema(description = "ID único del empleado, generado automáticamente.",
           example = "101", accessMode = Schema.AccessMode.READ_ONLY)
    private Long id;

    @Column(nullable = false, length = 100)
    @Schema(description = "Nombre del empleado.", example = "Ana")
    private String nombre;

    @Column(nullable = false, length = 100)
    @Schema(description = "Apellido del empleado.", example = "Gomez")
    private String apellido;

    @Column(unique = true, nullable = false)
    @Schema(description = "Email corporativo del empleado.", example = "ana.gomez@empresa.com")
    private String email;

    @Column(name = "fecha_contratacion", nullable = false)
    @Temporal(TemporalType.DATE)
    @Schema(description = "Fecha de contratación del empleado.", example = "2025-09-14")
    private LocalDate fechaContratacion;

    @Column(nullable = false, precision = 10, scale = 2)
    @Schema(description = "Salario bruto del empleado.", example = "75000.50")
    private BigDecimal salario;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "departamento_id")
    @Schema(description = "Para crear/actualizar, solo se necesita el 'id' del departamento.", example = "{\"id\": 1}")
    @JsonBackReference
    private Departamento departamento;

    @ManyToMany
    @JoinTable(
            name = "empleado_proyecto",
            joinColumns = @JoinColumn(name = "empleado_id"),
            inverseJoinColumns = @JoinColumn(name = "proyecto_id")
    )
    private Set<Proyecto> proyectos = new HashSet<>();
}
