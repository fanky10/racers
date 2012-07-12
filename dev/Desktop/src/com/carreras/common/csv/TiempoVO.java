/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package com.carreras.common.csv;

/**
 *
 * @author Fanky10 <fanky10@gmail.com>
 */
public class TiempoVO {
    private Double tiempoRaccion;
    private Double tiempoCien;
    private Double tiempoFin;

    public TiempoVO(Double tiempoRaccion, Double tiempoCien, Double tiempoFin) {
        this.tiempoRaccion = tiempoRaccion;
        this.tiempoCien = tiempoCien;
        this.tiempoFin = tiempoFin;
    }
    public TiempoVO(String tiempoRaccion, String tiempoCien, String tiempoFin) {
        this.tiempoRaccion = Double.parseDouble(tiempoRaccion);
        this.tiempoCien = Double.parseDouble(tiempoCien);
        this.tiempoFin = Double.parseDouble(tiempoFin);
    }

    public Double getTiempoCien() {
        return tiempoCien;
    }

    public void setTiempoCien(Double tiempoCien) {
        this.tiempoCien = tiempoCien;
    }

    public Double getTiempoFin() {
        return tiempoFin;
    }

    public void setTiempoFin(Double tiempoFin) {
        this.tiempoFin = tiempoFin;
    }

    public Double getTiempoRaccion() {
        return tiempoRaccion;
    }

    public void setTiempoRaccion(Double tiempoRaccion) {
        this.tiempoRaccion = tiempoRaccion;
    }
    
}
