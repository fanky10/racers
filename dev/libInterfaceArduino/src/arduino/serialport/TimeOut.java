/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package arduino.serialport;

import arduino.eventos.RespuestaEvent;
import arduino.eventos.RespuestaEventListener;
import java.util.Timer;
import java.util.TimerTask;
import arduino.entidades.Error;
import arduino.logs.Debugger;


/**
 *
 * @author Administrador
 */
public class TimeOut {
    private static TimeOut instancia = null;
    private static Timer timer=null;
    private RespuestaEventListener respuestaEventListener;
    protected TimeOut(){

    }

    public static TimeOut instancia(){
        if(instancia == null){
            instancia = new TimeOut();

        }
        return instancia;
    }

    public void start(){
       Debugger.debug("timer iniciado");
       timer= new Timer();
       timer.scheduleAtFixedRate(new TimerTask() {

            @Override
            public void run() {
                RespuestaEvent respuesta = new RespuestaEvent(this, RespuestaEvent.ERROR);
                respuesta.setError(new Error(Error.ERROR_TIMEOUT,"EL arduino no respondio en el tiempo esperado - TIME OUT"));
                respuestaEventListener.respuesta(respuesta);
//                arduino_listener.error(new ErrorEvent(this, ErrorEvent.TIMEOUT, ));
                timer.cancel();

            }
        }, 1200, 1200);

    }

    public void cancel(){
        timer.cancel();
        timer=null;
    }
    public void restart(){
        if(timer !=null){
            cancel();
        }
        start();
    }

    public RespuestaEventListener getRespuestaEventListener() {
        return respuestaEventListener;
    }

    public void setRespuestaEventListener(RespuestaEventListener respuestaEventListener) {
        this.respuestaEventListener = respuestaEventListener;
    }
    
    
}
