/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package arduino.entidades;

import arduino.eventos.RespuestaEvent;
import arduino.eventos.RespuestaEventListener;
import arduino.logs.Debugger;
import arduino.serialport.TimeOut;
import java.util.ArrayList;

/**
 *
 * @author Fabian Nonino <<fabian.nonino@gmail.com>>
 */
public class Mensaje extends ArrayList<Byte> {

    private static Mensaje instancia = null;
    private boolean inicio = false;
    private RespuestaEventListener respuestaEventListener;
    public static final int F = 70;//indicador inicio de mensaje
    public static final int Z = 90;//indicador fin de mensaje
    public static final int V = 86; //separador de mensajes
    public static final int T = 84;//indicador de tipo mensaje Tiempos
    public static final int A = 65;//indicador de tipo mensaje Tiempos
    public static final int R = 82;//indicador de tipo mensaje Tiempos
    public static final int INDICE_TIPO_MENSAJE = 1;

    protected Mensaje() {
    }

    private synchronized static void crearInstancia() {
        if (instancia == null) {
            instancia = new Mensaje();
        }
    }

    public static Mensaje getInstancia() {
        crearInstancia();
        return instancia;
    }

    public void getMensaje(byte octeto) {
        Debugger.debug("Reciviendo " + MByte.intToAscii(MByte.ByteToInt(octeto)));
        
        if (!inicio) {
            if (MByte.ByteToInt(octeto) == F) {
                this.add(octeto);
                this.inicio = true;
                TimeOut.instancia().start();
            }
        } else {
            this.add(octeto);
            if (MByte.ByteToInt(octeto) == Z) {
                TimeOut.instancia().cancel();
                reconoceMensaje();
                
            }else{
                TimeOut.instancia().restart();
            }
        }
    }

    private void clearAll() {
        inicio = false;
        this.clear();

    }

    private void reconoceMensaje() {
        try {
            RespuestaEvent respuesta_event;
            int tipo_mensaje = MByte.ByteToInt(this.get(INDICE_TIPO_MENSAJE));
            if (tipo_mensaje == T) {
                respuesta_event = new RespuestaEvent(this, RespuestaEvent.CARRIL);
                respuesta_event.setDatos(getCarril());
                this.respuestaEventListener.respuesta(respuesta_event);

            } else if(tipo_mensaje == A){
                 respuesta_event = new RespuestaEvent(this, RespuestaEvent.CARRIL);
                 respuesta_event.setDatos(getAdelantamiento());
                 this.respuestaEventListener.respuesta(respuesta_event);
                 
            }else if(tipo_mensaje == R){
                respuesta_event = new RespuestaEvent(this, RespuestaEvent.CARRIL);
                respuesta_event.setDatos(getRotura());
                this.respuestaEventListener.respuesta(respuesta_event);
            }else{
                respuesta_event = new RespuestaEvent(this, RespuestaEvent.ERROR);
                respuesta_event.setError(new Error(Error.ERROR_COMUNICACION, "tipo de mensaje " + MByte.intToAscii(tipo_mensaje)));
                this.respuestaEventListener.respuesta(respuesta_event);
            }
        } catch (IndexOutOfBoundsException ex) {

            RespuestaEvent respuesta_event = new RespuestaEvent(this, RespuestaEvent.ERROR);
            respuesta_event.setError(new Error(Error.ERROR_COMUNICACION, "Mensaje fuera del rango estipulado"));
            Debugger.debug(ex.getMessage());
            this.respuestaEventListener.respuesta(respuesta_event);

        } catch (NumberFormatException ex) {
            RespuestaEvent respuesta_event = new RespuestaEvent(this, RespuestaEvent.ERROR);
            respuesta_event.setError(new Error(Error.ERROR_COMUNICACION, "Mensaje Fuera del protocolo"));
            Debugger.debug("Mensaje Fuera del protocolo" + this.toString());
            this.respuestaEventListener.respuesta(respuesta_event);
        } finally {

            clearAll();

        }
    }

    public RespuestaEventListener getRespuestaEventListener() {
        return respuestaEventListener;
    }

    public void setRespuestaEventListener(RespuestaEventListener _respuestaEventListener) {
        this.respuestaEventListener = _respuestaEventListener;
        TimeOut.instancia().setRespuestaEventListener(_respuestaEventListener);
    }

    public String toString() {
        String reto = "";
        for (int i = 0; i < this.size(); i++) {
            reto += MByte.byteToChar(this.get(i));
        }
        return reto;
    }

    private Carril getCarril() throws NumberFormatException {
        Tiempos tiempoV = new Tiempos();
        //trabajando la comunicacion
        String mensaje = this.toString();
        String mensaje_sin_inicio_fin = mensaje.substring(3, mensaje.length() - 1);//deja solo los datos (quita inicio, tipo mensaje, el primer V y fin
        String[] datos = mensaje_sin_inicio_fin.split("V");//separo los mensajes
        //decodificando el mensaje
        int nro_carril = Integer.valueOf(datos[0]);
        double tiempo_reaccion = Double.valueOf(datos[1]) * 0.001;
        double tiempo_100mts = Double.valueOf(datos[2]) * 0.001;
        double tiempo_llegada = Double.valueOf(datos[3]) * 0.001;
        tiempoV.add(new Tiempo(Tiempo.TIEMPO_REACCION, tiempo_reaccion));
        tiempoV.add(new Tiempo(Tiempo.TIEMPO_100MTS, tiempo_100mts));
        tiempoV.add(new Tiempo(Tiempo.TIEMPO_FIN, tiempo_llegada));
        return new Carril(nro_carril, tiempoV);

    }

    private Adelantamiento getAdelantamiento() {
        String mensaje = this.toString();
        String mensaje_sin_inicio_fin = mensaje.substring(3, mensaje.length() - 1);//deja solo los datos (quita inicio, tipo mensaje, el primer V y fin
        String[] datos = mensaje_sin_inicio_fin.split("V");//separo los mensajes
        //decodificando el mensaje
        int nro_carril = Integer.valueOf(datos[0]);
        return new Adelantamiento(nro_carril);
    }
    
    private Rotura getRotura() {
        String mensaje = this.toString();
        String mensaje_sin_inicio_fin = mensaje.substring(3, mensaje.length() - 1);//deja solo los datos (quita inicio, tipo mensaje, el primer V y fin
        String[] datos = mensaje_sin_inicio_fin.split("V");//separo los mensajes
        //decodificando el mensaje
        int nro_carril = Integer.valueOf(datos[0]);
        return new Rotura(nro_carril);
    }
}
