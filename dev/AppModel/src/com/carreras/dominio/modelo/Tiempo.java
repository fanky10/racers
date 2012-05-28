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
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;

/**
 *
 * @author Fanky10 <fanky10@gmail.com>
 */
@Entity(name="tiempo")
public class Tiempo implements Serializable {

    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    @Column(name = "id", nullable = false)
    private Integer id;
    @ManyToOne
    @JoinColumn(name = "tipo_tiempo")
    private TipoTiempo tipoTiempo;
    @Column(name = "tiempo")
    private Float tiempo;
    @ManyToOne
    @JoinColumn(name = "id_carril_carrera")
    private Carril carril;

    public Carril getCarril() {
        return carril;
    }

    public void setCarril(Carril carril) {
        this.carril = carril;
    }

    public Float getTiempo() {
        return tiempo;
    }

    public void setTiempo(Float tiempo) {
        this.tiempo = tiempo;
    }

    public TipoTiempo getTipoTiempo() {
        return tipoTiempo;
    }

    public void setTipoTiempo(TipoTiempo tipoTiempo) {
        this.tipoTiempo = tipoTiempo;
    }

    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }
}
