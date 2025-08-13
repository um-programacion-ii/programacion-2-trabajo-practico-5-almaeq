package com.example.sistemaGestionEmpleados.repositories.departamento;

import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.test.context.ActiveProfiles;

@DataJpaTest
@ActiveProfiles("h2")
public class DepartamentoRepositoryH2Test extends BaseDepartamentoRepositoryTest{
    // Hereda y ejecuta los tests con H2
}
