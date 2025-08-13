package com.example.sistemaGestionEmpleados.repositories.departamento;

import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.test.context.ActiveProfiles;

@DataJpaTest
@ActiveProfiles("postgresql")
public class DepartamentoRepositoryPostgresqlTest {
    // Hereda y ejecuta los tests con postgresql
}
