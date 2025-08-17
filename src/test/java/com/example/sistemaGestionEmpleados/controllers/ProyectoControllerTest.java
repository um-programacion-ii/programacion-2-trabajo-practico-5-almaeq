package com.example.sistemaGestionEmpleados.controllers;

import com.example.sistemaGestionEmpleados.exceptions.ProyectoNoEncontradoException;
import com.example.sistemaGestionEmpleados.models.Proyecto;
import com.example.sistemaGestionEmpleados.services.ProyectoService;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;

import java.time.LocalDate;
import java.util.Collections;
import java.util.List;

import static org.hamcrest.CoreMatchers.is;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.BDDMockito.given;
import static org.mockito.Mockito.doNothing;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@WebMvcTest(controllers = ProyectoController.class)
public class ProyectoControllerTest {
    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @MockBean
    private ProyectoService proyectoService;

    private Proyecto proyectoPrueba;

    @BeforeEach
    void setUp() {
        // Configuramos un objeto de prueba para reutilizarlo
        proyectoPrueba = new Proyecto(1L, "Proyecto Centinela", "Iniciativa de seguridad global",
                LocalDate.now(), LocalDate.now().plusMonths(6), Collections.emptySet());

        // El ObjectMapper necesita el módulo de JavaTime para serializar/deserializar LocalDate
        objectMapper.registerModule(new JavaTimeModule());
    }

    @Test
    void cuandoObtenerTodos_entoncesRetornaListaDeProyectos() throws Exception {

        given(proyectoService.obtenerTodos()).willReturn(List.of(proyectoPrueba));

        mockMvc.perform(get("/api/proyectos"))
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$.size()", is(1)))
                .andExpect(jsonPath("$[0].nombre", is("Proyecto Centinela")));
    }

    @Test
    void cuandoObtenerPorIdExistente_entoncesRetornaProyecto() throws Exception {

        given(proyectoService.buscarPorId(1L)).willReturn(proyectoPrueba);

        mockMvc.perform(get("/api/proyectos/{id}", 1L))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id", is(1)))
                .andExpect(jsonPath("$.nombre", is(proyectoPrueba.getNombre())));
    }

    @Test
    void cuandoObtenerPorIdNoExistente_entoncesRetornaNotFound() throws Exception {

        given(proyectoService.buscarPorId(anyLong())).willThrow(new ProyectoNoEncontradoException("No se encontró"));

        mockMvc.perform(get("/api/proyectos/{id}", 99L))
                .andExpect(status().isNotFound());
    }

    @Test
    void cuandoCrearProyecto_entoncesRetornaCreated() throws Exception {

        given(proyectoService.guardar(any(Proyecto.class))).willReturn(proyectoPrueba);

        mockMvc.perform(post("/api/proyectos")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(proyectoPrueba)))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.nombre", is("Proyecto Centinela")));
    }

    @Test
    void cuandoActualizarProyecto_entoncesRetornaOk() throws Exception {

        Proyecto proyectoActualizado = new Proyecto(1L, "Proyecto Centinela V2", "Versión mejorada", null, null, null);
        given(proyectoService.actualizar(anyLong(), any(Proyecto.class))).willReturn(proyectoActualizado);

        mockMvc.perform(put("/api/proyectos/{id}", 1L)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(proyectoActualizado)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.nombre", is("Proyecto Centinela V2")));
    }

    @Test
    void cuandoEliminarProyecto_entoncesRetornaNoContent() throws Exception {

        doNothing().when(proyectoService).eliminar(1L);

        mockMvc.perform(delete("/api/proyectos/{id}", 1L))
                .andExpect(status().isNoContent());
    }

    @Test
    void cuandoObtenerProyectosActivos_entoncesRetornaListaCorrecta() throws Exception {

        given(proyectoService.buscarPorProyectosActivos()).willReturn(List.of(proyectoPrueba));

        mockMvc.perform(get("/api/proyectos/activos"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.size()", is(1)))
                .andExpect(jsonPath("$[0].nombre", is(proyectoPrueba.getNombre())));
    }
}
