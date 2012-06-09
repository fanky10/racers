/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package com.carreras.common.emulador;

import arduino.entidades.Carril;
import arduino.entidades.Tiempo;
import arduino.entidades.Tiempos;

/**
 *
 * @author Fanky10 <fanky10@gmail.com>
 */
public class MensajeCarril implements java.io.Serializable{
    private Integer numeroCarril;
    private Double[] tiempos;
    
    public MensajeCarril(){
        
    }

    public MensajeCarril(Integer numeroCarril, Double[] tiempos) {
        this.numeroCarril = numeroCarril;
        this.tiempos = tiempos;
    }
    private Tiempos getTiemposArduino(){
        Tiempos reto = new Tiempos();
        reto.add(new Tiempo(Tiempo.TIEMPO_REACCION, tiempos[0]));
        reto.add(new Tiempo(Tiempo.TIEMPO_100MTS, tiempos[1]));
        reto.add(new Tiempo(Tiempo.TIEMPO_FIN, tiempos[2]));
        return reto;
    }
    public Carril getCarril(){
        return new Carril(numeroCarril, getTiemposArduino());
    }
    
    public static Double[] getTiemposAleatorios(){
        Double[] reto = new Double[3];
        
        double anterior = getRandom(0);
        reto[0] = anterior;
        anterior = getRandom(anterior);
        reto[1] = anterior;
        anterior = getRandom(anterior);
        reto[2] = anterior;
        return reto;
    }
    /**
     * obtiene un 
     * @param anterior es tiempo anterior
     * @return random mayor al anterior y menor a 20 :P
     */
    private static double getRandom(double anterior){
        double ran = 0d;
        while(true){
            ran = getRandom();
            if(ran>=anterior && ran <=20d){
                break;
            }
        }
        //juro que trate de hacerlo con un do{}while(cond);
        //pero no me salio, esta estructura es mas sencilla de comprender :P
        return ran;
    }
    /**
     * podria validar que sea menor al anterior y menor a 20 :P
     * @return 
     */
    private static double getRandom(){
        double ran = Math.random();
        return ran * 100;
    }

    public Integer getNumeroCarril() {
        return numeroCarril;
    }

    public void setNumeroCarril(Integer numeroCarril) {
        this.numeroCarril = numeroCarril;
    }

    public Double[] getTiempos() {
        return tiempos;
    }

    public void setTiempos(Double[] tiempos) {
        this.tiempos = tiempos;
    }
    
}
