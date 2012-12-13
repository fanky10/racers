/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package arduino.serialport.exception;

/**
 *
 * @author Fabian Nonino <<fabian.nonino@gmail.com>>
 */
public class NoHayPuertosDisponiblesException extends Exception{
     @Override
     public String getMessage(){
         return "No hay Puertos Disponibles";
     }
 }