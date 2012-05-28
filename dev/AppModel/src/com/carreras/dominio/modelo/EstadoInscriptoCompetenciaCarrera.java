/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package com.carreras.dominio.modelo;

/**
 *
 * @author Fanky10 <fanky10@gmail.com>
 */
public enum EstadoInscriptoCompetenciaCarrera {
    GANADOR(1, "Ganador"), PERDEDOR(2, "Perdedor"), ROTO(3, "Roto"), ESPERANDO(4, "Esperando"), CORRIENDO(5, "Corriendo"), 
    ADELANTADO(6, "Adelantado"), GANADOR_CATEGORIA(7, "Ganador Categoria!"), NO_CORRE(8, "No Corre"),CAIDO_CATEGORIA(8, "Caido Categoria");
    private Integer id;
    private String descripcion;

    private EstadoInscriptoCompetenciaCarrera(int id, String descripcion) {
        this.id = id;
        this.descripcion = descripcion;
    }

    @Override
    public String toString() {
        return descripcion;
    }

    public String getDescripcion() {
        return descripcion;
    }

    public void setDescripcion(String descripcion) {
        this.descripcion = descripcion;
    }

    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }
}
