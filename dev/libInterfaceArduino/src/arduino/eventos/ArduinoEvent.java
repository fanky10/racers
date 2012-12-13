/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package arduino.eventos;

import arduino.logs.Debugger;
import java.util.EventObject;

/**
 *
 * @author Fabian Nonino <<fabian.nonino@gmail.com>>
 */
public class ArduinoEvent extends EventObject {

    public static int ARDUINO_ONLINE = 1;
    public static int ARDUINO_OFFLINE = 2;
    public static int ERROR_DESCONOCIDO = 3;
    private int estado;
    private String mensaje;

    public ArduinoEvent(Object source, int _estado, String _mensaje) {
        super(source);
        estado = _estado;
        mensaje = _mensaje;
        Debugger.debug(mensaje);
    }

    public int getEstado() {
        return estado;
    }

    public void setEstado(int estado) {
        this.estado = estado;
    }

    public String getMensaje() {
        return mensaje;
    }

    public void setMensaje(String mensaje) {
        this.mensaje = mensaje;
    }
}
