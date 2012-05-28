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
@Entity(name = "inscripto")
public class Inscripto implements Serializable {

    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    @Column(name = "id", nullable = false)
    private Integer id;
    @ManyToOne
    @JoinColumn(name = "id_auto")
    private Auto auto;
    @ManyToOne
    @JoinColumn(name = "id_corredor")
    private Corredor corredor;

    public Corredor getCorredor() {
        return corredor;
    }

    public void setCorredor(Corredor corredor) {
        this.corredor = corredor;
    }

    public Auto getAuto() {
        return auto;
    }

    public void setAuto(Auto auto) {
        this.auto = auto;
    }

    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }
}
