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
@Entity(name = "carril_carrera")
public class Carril implements Serializable {

    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    @Column(name = "id", nullable = false)
    private Integer id;
    @Column(name = "numero_carril")
    private Integer numero;
    @ManyToOne
    @JoinColumn(name = "id_inscripto_competencia")
    private InscriptoCompetencia inscriptoCompetencia;
    @ManyToOne
    @JoinColumn(name = "id_carrera")
    private Carrera carrera;

    public Integer getNumero() {
        return numero;
    }

    public void setNumero(Integer numero) {
        this.numero = numero;
    }

    public InscriptoCompetencia getInscriptoCompetencia() {
        return inscriptoCompetencia;
    }

    public void setInscriptoCompetencia(InscriptoCompetencia inscripto) {
        this.inscriptoCompetencia = inscripto;
    }

    public Carrera getCarrera() {
        return carrera;
    }

    public void setCarrera(Carrera carrera) {
        this.carrera = carrera;
    }

    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }
}
