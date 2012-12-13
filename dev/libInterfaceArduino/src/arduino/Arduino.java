/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package arduino;

import arduino.eventos.ArduinoEvent;
import arduino.eventos.ArduinoEventListener;
import arduino.eventos.RespuestaEvent;
import arduino.eventos.RespuestaEventListener;
import arduino.logs.Debugger;
import arduino.serialport.Rs232;
import arduino.serialport.exception.PuertosVaciosException;
import java.io.IOException;
import java.util.TooManyListenersException;
import javax.comm.NoSuchPortException;
import javax.comm.PortInUseException;
import javax.comm.UnsupportedCommOperationException;

/**
 *
 * @author Fabian Nonino <<fabian.nonino@gmail.com>>
 */
public class Arduino implements RespuestaEventListener {

    protected ArduinoEventListener arduino_event_listener;

    public Arduino(ArduinoEventListener _arduino_event_listener) {
        this.arduino_event_listener = _arduino_event_listener;
        Debugger.debug("Arduino Driver " + Config.VERSION);
    }

    public void ini() {
        Rs232.instancia().setRespuesta_event_listener(this);
        try {
            Rs232.instancia().ini();
            arduino_event_listener.EstadoArduino(new ArduinoEvent(this, ArduinoEvent.ARDUINO_ONLINE, "Arduino On Line"));
        } catch (NoSuchPortException ex) {
            arduino_event_listener.EstadoArduino(new ArduinoEvent(this, ArduinoEvent.ERROR_DESCONOCIDO, ex.getMessage()));
        } catch (PortInUseException ex) {
            arduino_event_listener.EstadoArduino(new ArduinoEvent(this, ArduinoEvent.ERROR_DESCONOCIDO, ex.getMessage()));
        } catch (TooManyListenersException ex) {
            arduino_event_listener.EstadoArduino(new ArduinoEvent(this, ArduinoEvent.ERROR_DESCONOCIDO, ex.getMessage()));
        } catch (IOException ex) {
            arduino_event_listener.EstadoArduino(new ArduinoEvent(this, ArduinoEvent.ERROR_DESCONOCIDO, ex.getMessage()));
        } catch (UnsupportedCommOperationException ex) {
            arduino_event_listener.EstadoArduino(new ArduinoEvent(this, ArduinoEvent.ERROR_DESCONOCIDO, ex.getMessage()));

        } catch (PuertosVaciosException ex) {
            arduino_event_listener.EstadoArduino(new ArduinoEvent(this, ArduinoEvent.ARDUINO_OFFLINE, ex.getMessage()));
        }

    }
    public void close(){
        try {
            Rs232.instancia().close();
            arduino_event_listener.EstadoArduino(new ArduinoEvent(this, ArduinoEvent.ARDUINO_OFFLINE, "arduino desconectado"));
        } catch (IOException ex) {
             arduino_event_listener.EstadoArduino(new ArduinoEvent(this, ArduinoEvent.ERROR_DESCONOCIDO, ex.getMessage()));
        }
        
            
    }
    @Override
    public void respuesta(RespuestaEvent respuestaEvent) {
        arduino_event_listener.Estado_Datos(respuestaEvent);
    }
}
