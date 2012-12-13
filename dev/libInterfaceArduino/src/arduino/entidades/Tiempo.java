/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package arduino.entidades;

/**
 *
 * @author Fabian Nonino <<fabian.nonino@gmail.com>>
 */
public class Tiempo {
    public static final int TIEMPO_REACCION =1;
    public static final int TIEMPO_100MTS = 2;
    public static final int TIEMPO_FIN = 3;
    
    private int tipo_tiempo;
    private double tiempo;

    public Tiempo(int tipo_tiempo, double tiempo) {
        this.tipo_tiempo = tipo_tiempo;
        this.tiempo = tiempo;
    }

    public double getTiempo() {
        return tiempo;
    }

    public void setTiempo(double tiempo) {
        this.tiempo = tiempo;
    }

    public int getTipo_tiempo() {
        return tipo_tiempo;
    }

    public void setTipo_tiempo(int tipo_tiempo) {
        this.tipo_tiempo = tipo_tiempo;
    }
    
    
}
