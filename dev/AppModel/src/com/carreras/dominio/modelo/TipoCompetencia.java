/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package com.carreras.dominio.modelo;

/**
 *
 * @author Fanky10 <fanky10@gmail.com>
 */
public enum TipoCompetencia {
    LIBRE(1,"Libre"), ELIMINATORIA(2,"Eliminatoria"),FINAL(3,"Final");
    
    private Integer id;
    private String descripcion;

    private TipoCompetencia(int id, String descripcion) {
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