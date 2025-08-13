package com.example.sistemaGestionEmpleados.repositories.departamento;

import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.test.context.ActiveProfiles;

@DataJpaTest
@ActiveProfiles("mysql")
public class DepartamentoRepositoryMysqlTest extends BaseDepartamentoRepositoryTest{
    // Hereda y ejecuta los tests con mysql
}
