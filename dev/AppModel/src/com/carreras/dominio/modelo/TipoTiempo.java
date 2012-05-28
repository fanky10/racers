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
@Entity(name="tipo_tiempo")
public class TipoTiempo implements Serializable {
    
    public static final int ID_TIEMPO_REACCION =1;
    public static final int ID_TIEMPO_100MTS = 2;
    public static final int ID_TIEMPO_FIN = 3;
    
    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    @Column(name = "id", nullable = false)
    private Integer id;
    @Column(name="descripcion")
    private String descripcion;
    @Column(name="posicion")
    private Integer posicion;
    @Column(name="habilitado")
    private boolean habilitado;
    @Column(name="decisorio")
    private boolean decisorio;

    public boolean isDecisorio() {
        return decisorio;
    }

    public void setDecisorio(boolean decisorio) {
        this.decisorio = decisorio;
    }

    public String getDescripcion() {
        return descripcion;
    }

    public void setDescripcion(String descripcion) {
        this.descripcion = descripcion;
    }

    public boolean isHabilitado() {
        return habilitado;
    }

    public void setHabilitado(boolean habilitado) {
        this.habilitado = habilitado;
    }

    public Integer getPosicion() {
        return posicion;
    }

    public void setPosicion(Integer posicion) {
        this.posicion = posicion;
    }
    

    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }
    
}
