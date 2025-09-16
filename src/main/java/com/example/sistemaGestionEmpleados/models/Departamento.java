package com.example.sistemaGestionEmpleados.models;

import com.fasterxml.jackson.annotation.JsonIdentityInfo;
import com.fasterxml.jackson.annotation.JsonManagedReference;
import com.fasterxml.jackson.annotation.ObjectIdGenerators;
import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.persistence.*;
import lombok.*;

import java.util.ArrayList;
import java.util.List;

@Entity
@Table(name = "departamentos")
@Getter
@Setter
@ToString(exclude = "empleados") // Excluye la relación
@EqualsAndHashCode(of = "id") // Basa la igualdad solo en el ID
@JsonIdentityInfo(generator = ObjectIdGenerators.PropertyGenerator.class, property = "id")
@NoArgsConstructor
@AllArgsConstructor
public class Departamento {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Schema(description = "ID único del departamento, generado automáticamente.",
            example = "1", accessMode = Schema.AccessMode.READ_ONLY)
    private Long id;

    @Column(nullable = false, length = 100, unique = true)
    @Schema(description = "Nombre del departamento.", example = "Tecnología")
    private String nombre;

    @Column(length = 500)
    @Schema(description = "Descripción de las funciones del departamento.", example = "Departamento de desarrollo de software e infraestructura.")
    private String descripcion;

    @OneToMany(mappedBy = "departamento", cascade = CascadeType.ALL, fetch = FetchType.LAZY)
    @JsonManagedReference
    private List<Empleado> empleados = new ArrayList<>();
}