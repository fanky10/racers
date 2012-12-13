/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package arduino.entidades;

/**
 *
 * @author Fabian Nonino <<fabian.nonino@gmail.com>>
 */
public class Adelantamiento extends Datos{

    private int nro_carril;

    public Adelantamiento(int nro_carril) {
        this.nro_carril = nro_carril;
    }

    public int getNro_carril() {
        return nro_carril;
    }

    public void setNro_carril(int nro_carril) {
        this.nro_carril = nro_carril;
    }
    
    @Override
    public int getTipo() {
        return Datos.ADELANTAMIENTO;
    }
    
}
