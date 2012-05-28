/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package com.carreras.dominio.modelo;

import java.io.Serializable;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;

/**
 *
 * @author Fanky10 <fanky10@gmail.com>
 */
@Entity(name="categoria")
public class Categoria implements Serializable {
    public static final int ID_CATEGORIA_LIBRE = 11;
    public static final int ID_CATEGORIA_NO_CORRE = 13;
    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    @Column(name = "id", nullable = false)
    private Integer id;
    @Column(name="descripcion")
    private String descripcion;
    @Column(name="tiempo_maximo")
    private Float tiempoMaximo;
    
    @Override
    public String toString(){
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

    public Float getTiempoMaximo() {
        return tiempoMaximo;
    }

    public void setTiempoMaximo(Float tiempoMaximo) {
        this.tiempoMaximo = tiempoMaximo;
    }
    
    
    
}
