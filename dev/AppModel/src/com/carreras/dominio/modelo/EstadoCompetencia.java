/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package com.carreras.dominio.modelo;

/**
 *
 * @author Fanky10 <fanky10@gmail.com>
 * como inicia en la competencia, como ganador de la anterior o lo que de
 * al inicio sin definicion y luego como ganador o como perdedor
 */
public enum EstadoCompetencia {
    COMPETENCIA_GANADORES(1, "Ganador"),COMPETENCIA_PERDEDORES(2,"Perdedor"),COMPETENCIA_NO_DEFINIDA(3, "Sin definicion");
    private Integer id;
    private String descripcion;

    private EstadoCompetencia(int id, String descripcion) {
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
