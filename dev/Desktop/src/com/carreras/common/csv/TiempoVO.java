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
    private Long tiempoRaccion;
    private Long tiempoCien;
    private Long tiempoFin;

    public TiempoVO(Long tiempoRaccion, Long tiempoCien, Long tiempoFin) {
        this.tiempoRaccion = tiempoRaccion;
        this.tiempoCien = tiempoCien;
        this.tiempoFin = tiempoFin;
    }
    public TiempoVO(String tiempoRaccion, String tiempoCien, String tiempoFin) {
        this.tiempoRaccion = Long.parseLong(tiempoRaccion);
        this.tiempoCien = Long.parseLong(tiempoCien);
        this.tiempoFin = Long.parseLong(tiempoFin);
    }

    public Long getTiempoCien() {
        return tiempoCien;
    }

    public void setTiempoCien(Long tiempoCien) {
        this.tiempoCien = tiempoCien;
    }

    public Long getTiempoFin() {
        return tiempoFin;
    }

    public void setTiempoFin(Long tiempoFin) {
        this.tiempoFin = tiempoFin;
    }

    public Long getTiempoRaccion() {
        return tiempoRaccion;
    }

    public void setTiempoRaccion(Long tiempoRaccion) {
        this.tiempoRaccion = tiempoRaccion;
    }
    
}
