package com.example.sistemaGestionEmpleados.controllers;

import com.example.sistemaGestionEmpleados.exceptions.DepartamentoNoEncontradoException;
import com.example.sistemaGestionEmpleados.models.Departamento;
import com.example.sistemaGestionEmpleados.services.DepartamentoService;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;

import java.util.Collections;
import java.util.List;

import static org.hamcrest.CoreMatchers.is;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.BDDMockito.given;
import static org.mockito.Mockito.doNothing;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@WebMvcTest(controllers = DepartamentoController.class)
public class DepartamentoControllerTest {
    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @MockBean
    private DepartamentoService departamentoService;

    private Departamento departamentoPrueba;

    @BeforeEach
    void setUp() {
        // Preparamos un objeto de prueba que se usará en varios tests
        departamentoPrueba = new Departamento(1L, "Recursos Humanos", "Departamento de gestión de personal", Collections.emptyList());
    }

    @Test
    void cuandoObtenerTodos_entoncesRetornaListaDeDepartamentos() throws Exception {

        given(departamentoService.obtenerTodos()).willReturn(List.of(departamentoPrueba));

        mockMvc.perform(get("/api/departamentos"))
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$.size()", is(1)))
                .andExpect(jsonPath("$[0].nombre", is("Recursos Humanos")));
    }

    @Test
    void cuandoObtenerPorIdExistente_entoncesRetornaDepartamento() throws Exception {

        given(departamentoService.buscarPorId(1L)).willReturn(departamentoPrueba);

        mockMvc.perform(get("/api/departamentos/{id}", 1L))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.nombre", is("Recursos Humanos")))
                .andExpect(jsonPath("$.descripcion", is("Departamento de gestión de personal")));
    }

    @Test
    void cuandoObtenerPorIdNoExistente_entoncesRetornaNotFound() throws Exception {

        given(departamentoService.buscarPorId(anyLong())).willThrow(new DepartamentoNoEncontradoException("No se encontró el depto."));

        mockMvc.perform(get("/api/departamentos/{id}", 99L))
                .andExpect(status().isNotFound());
    }

    @Test
    void cuandoCrearDepartamento_entoncesRetornaCreated() throws Exception {

        given(departamentoService.guardar(any(Departamento.class))).willReturn(departamentoPrueba);

        mockMvc.perform(post("/api/departamentos")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(departamentoPrueba)))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.id", is(1)))
                .andExpect(jsonPath("$.nombre", is("Recursos Humanos")));
    }

    @Test
    void cuandoActualizarDepartamento_entoncesRetornaOk() throws Exception {

        Departamento departamentoActualizado = new Departamento(1L, "RR.HH. Global", "Gestión de personal internacional", null);
        given(departamentoService.actualizar(anyLong(), any(Departamento.class))).willReturn(departamentoActualizado);

        mockMvc.perform(put("/api/departamentos/{id}", 1L)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(departamentoActualizado)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.nombre", is("RR.HH. Global")));
    }

    @Test
    void cuandoActualizarDepartamentoNoExistente_entoncesRetornaNotFound() throws Exception {

        given(departamentoService.actualizar(anyLong(), any(Departamento.class)))
                .willThrow(new DepartamentoNoEncontradoException("No se encontró el depto. para actualizar"));

        mockMvc.perform(put("/api/departamentos/{id}", 99L)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(departamentoPrueba)))
                .andExpect(status().isNotFound());
    }

    @Test
    void cuandoEliminarDepartamento_entoncesRetornaNoContent() throws Exception {

        doNothing().when(departamentoService).eliminar(1L);

        mockMvc.perform(delete("/api/departamentos/{id}", 1L))
                .andExpect(status().isNoContent());
    }
}
