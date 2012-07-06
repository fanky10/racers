/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package com.carreras.common.util;

/**
 *
 * @author Fanky10 <fanky10@gmail.com>
 */
public class TiemposHelper {
    private static enum TipoTiempo{
        REACCION, FIN, CIEN_METROS
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
    public static Double[] getTiemposTiempoFin(Double ultimoTiempo){
        Double[] reto = new Double[3];
        double ultimo = ultimoTiempo;
        reto[2] = ultimo;
        ultimo = getRandom(ultimo,TipoTiempo.FIN);
        reto[1] = ultimo;
        ultimo = getRandom(ultimo,TipoTiempo.FIN);
        reto[0] = ultimo;
        return reto;
    }
    /**
     * obtiene un 
     * @param anterior es tiempo anterior
     * @return random mayor al anterior y menor a 20 :P
     */
    private static double getRandom(double anterior){
        return getRandom(anterior, TipoTiempo.REACCION);
    }
    private static Double getRandom(double tiempo, TipoTiempo tipoTiempo){
        double ran = 0d;
        while(true){
            ran = getRandom();
            if(tipoTiempo == TipoTiempo.FIN){
                if(ran<=tiempo && ran <=20d){
                    break;
                }
            } else if(tipoTiempo == TipoTiempo.REACCION){
                if(ran>=tiempo && ran <=20d){
                    break;
                }
            }else{
                throw new IllegalArgumentException("tipo tiempo: "+tipoTiempo+ " desconocido");
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
}
