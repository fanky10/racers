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
@Entity(name = "competencia")
public class Competencia implements Serializable {

    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    @Column(name = "id", nullable = false)
    private Integer id;
    @Column(name = "numero_ronda")
    private Integer numeroRonda;
    @Column(name = "tipo_competicion")
    private TipoCompetencia tipoCompetencia;
    @ManyToOne
    @JoinColumn(name = "id_torneo")
    private Torneo torneo;

    public Torneo getTorneo() {
        return torneo;
    }

    public void setTorneo(Torneo torneo) {
        this.torneo = torneo;
    }

    public Integer getNumeroRonda() {
        return numeroRonda;
    }

    public void setNumeroRonda(Integer numeroRonda) {
        this.numeroRonda = numeroRonda;
    }

    public TipoCompetencia getTipoCompetencia() {
        return tipoCompetencia;
    }

    public void setTipoCompetencia(TipoCompetencia tipoCompetencia) {
        this.tipoCompetencia = tipoCompetencia;
    }

    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }
}
