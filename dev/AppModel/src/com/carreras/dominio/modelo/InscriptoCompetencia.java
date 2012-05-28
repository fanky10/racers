/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package com.carreras.dominio.modelo;

import java.io.Serializable;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Enumerated;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;

/**
 *
 * @author Fanky10 <fanky10@gmail.com>
 */
@Entity(name = "inscripto_competencia")
public class InscriptoCompetencia implements Serializable {

    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    @Column(name = "id", nullable = false)
    private Integer id;
    @ManyToOne
    @JoinColumn(name = "id_inscripto")
    private Inscripto inscripto;
    @ManyToOne
    @JoinColumn(name = "id_categoria")
    private Categoria categoria;
    @ManyToOne
    @JoinColumn(name = "id_competencia")
    private Competencia competencia;
    @Column(name = "numero_generado")
    private Integer numeroGenerado;
    @Column(name = "estado")
    @Enumerated(javax.persistence.EnumType.ORDINAL)
    private EstadoInscriptoCompetenciaCarrera estado;
    @Enumerated(javax.persistence.EnumType.ORDINAL)
    @Column(name = "estado_competencia")
    private EstadoCompetencia estadoCompetencia;
    @Column(name = "rondas_restantes")
    private Integer rondasRestantes = 0;

    public void generaNumero() {
        numeroGenerado = Integer.parseInt( categoria.getId().toString() + inscripto.getId().toString() );
    }

    public Competencia getCompetencia() {
        return competencia;
    }

    public void setCompetencia(Competencia competencia) {
        this.competencia = competencia;
    }

    public Categoria getCategoria() {
        return categoria;
    }

    public void setCategoria(Categoria categoria) {
        this.categoria = categoria;
    }

    public Inscripto getInscripto() {
        return inscripto;
    }

    public void setInscripto(Inscripto inscripto) {
        this.inscripto = inscripto;
    }

    public Integer getNumeroGenerado() {
        return numeroGenerado;
    }

    public void setNumeroGenerado(Integer numeroGenerado) {
        this.numeroGenerado = numeroGenerado;
    }

    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    public EstadoInscriptoCompetenciaCarrera getEstado() {
        return estado;
    }

    public void setEstado(EstadoInscriptoCompetenciaCarrera estado) {
        this.estado = estado;
    }

    public EstadoCompetencia getEstadoCompetencia() {
        return estadoCompetencia;
    }

    public void setEstadoCompetencia(EstadoCompetencia estadoCompetencia) {
        this.estadoCompetencia = estadoCompetencia;
    }

    public Integer getRondasRestantes() {
        return rondasRestantes;
    }

    public void setRondasRestantes(Integer rondasRestantes) {
        this.rondasRestantes = rondasRestantes;
    }
    public void decrementaRondasRestantes(){
        if(rondasRestantes>0){
            this.rondasRestantes--;
        }
    }
}
