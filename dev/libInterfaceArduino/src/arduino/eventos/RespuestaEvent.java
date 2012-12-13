/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package arduino.eventos;
import arduino.entidades.Datos;
import java.util.EventObject;
import arduino.entidades.Error;

/**
 *
 * @author Fabian Nonino <<fabian.nonino@gmail.com>>
 */
public class RespuestaEvent extends EventObject{
    public static final int CARRIL=1;
    public static final int ERROR =2;
    private int nro_evento;
    private Error error = null;
    private Datos datos = null;
    
    public RespuestaEvent (Object fuente, int _nro_evento){
        super(fuente);
        nro_evento = _nro_evento;
    }

    public Datos getDatos() {
        return datos;
    }

    public void setDatos(Datos carril) {
        this.datos = carril;
    }

    public Error getError() {
        return error;
    }

    public void setError(Error error) {
        this.error = error;
    }

    public int getNro_evento() {
        return nro_evento;
    }

    public void setNro_evento(int nro_evento) {
        this.nro_evento = nro_evento;
    }
    
    
    
}
