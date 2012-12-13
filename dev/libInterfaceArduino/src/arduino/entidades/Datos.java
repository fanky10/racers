/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package arduino.entidades;

/**
 *
 * @author Fabian Nonino <<fabian.nonino@gmail.com>>
 */
public abstract class Datos {
    public static final int CARRIL = 1;
    public static final int ROTURA = 2;
    public static final int ADELANTAMIENTO = 3;
    public abstract int getTipo();
    
}
