/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package arduino.entidades;

/**
 *
 * @author Fabian Nonino <<fabian.nonino@gmail.com>>
 */
public class Carril extends Datos{
    
    private int nro_carril;
    private Tiempos tiempoV;

    public Carril(int nro_carril, Tiempos tiempoV) {
        this.nro_carril = nro_carril;
        this.tiempoV = tiempoV;
    }

    public int getNro_carril() {
        return nro_carril;
    }

    public void setNro_carril(int nro_carril) {
        this.nro_carril = nro_carril;
    }

    public Tiempos getTiempoV() {
        return tiempoV;
    }

    public void setTiempoV(Tiempos tiempoV) {
        this.tiempoV = tiempoV;
    }

    @Override
    public int getTipo() {
        return Datos.CARRIL;
    }

   
    
    
    
}
