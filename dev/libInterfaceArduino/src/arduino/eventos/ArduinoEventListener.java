/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package arduino.eventos;

/**
 *
 * @author Fabian Nonino <<fabian.nonino@gmail.com>>
 */
public interface ArduinoEventListener {
    public void EstadoArduino(ArduinoEvent arduino_event);
    public void Estado_Datos (RespuestaEvent respuesta_event);
}
