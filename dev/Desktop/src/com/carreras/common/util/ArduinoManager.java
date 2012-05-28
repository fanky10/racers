/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package com.carreras.common.util;

import arduino.Arduino;
import arduino.entidades.Carril;
import arduino.entidades.Tiempo;
import arduino.eventos.ArduinoEvent;
import arduino.eventos.ArduinoEventListener;
import arduino.eventos.RespuestaEvent;
import com.carreras.common.emulador.ArduinoTCPServer;
/**
 *
 * @author fanky10
 */
public class ArduinoManager {
    public static final int ARDUINO_RS232_INSTANCE=0;
    public static final int ARDUINO_TCP_INSTANCE = 1;
    //muy destructivo si alguien toca esto... desp. se descompagina todo y hay que usar find usages..
    //pero de igual forma es muy flexible xD
    public static int ARDUINO_INSTANCE = ARDUINO_RS232_INSTANCE;
    private Arduino arduino;
    private ArduinoEventListener ardListener;
    private boolean closed;
    private int instancia_arduino;
    public ArduinoManager(){
        this(null);
    }
    public ArduinoManager(ArduinoEventListener listener){
        this(listener, ARDUINO_INSTANCE);
    }
    public ArduinoManager(ArduinoEventListener listener,int instancia_arduino){
        this.ardListener = listener;
        this.instancia_arduino = instancia_arduino;
        this.closed = true;
        
        init();
    }
    private void init(){
        if(ardListener==null)
            default_listener();
        if(instancia_arduino == ARDUINO_RS232_INSTANCE){
            arduino = new Arduino(ardListener);        
        }else if(instancia_arduino == ARDUINO_TCP_INSTANCE){
            arduino = new ArduinoTCPServer(ardListener);
        }else{
            throw new IllegalArgumentException("argumento: "+instancia_arduino+ "desconocido");
        }
    }
    public void inicializa_arduino()throws java.io.IOException,RuntimeException{
        if(!isClosed()){
            return;
        }
        try{
            arduino.ini();
            closed = false;
        }catch(UnsatisfiedLinkError ex ){
            throw new java.io.IOException("unsatisfied link error: "+ex.getMessage());
        }
    }
    public void finaliza_arduino(){
        arduino.close();
        closed=true;
    }
    private static void debug(String text){
        System.out.println("debug - arduino: "+text);
    }

    public boolean isClosed() {
        return closed;
    }
    

    private void default_listener() {
        ardListener = new ArduinoEventListener() {

            @Override
            public void EstadoArduino(ArduinoEvent arduino_event) {
                debug(arduino_event.getMensaje() + "\n");
            }

            @Override
            public void Estado_Datos(RespuestaEvent respuesta_event) {
                switch (respuesta_event.getNro_evento()) {
                    case RespuestaEvent.CARRIL:
                        Carril reto = (Carril) respuesta_event.getDatos();
                        debug("**** INICIO******\n");
                        debug("nro: carril: " + reto.getNro_carril() + "\n");
                        for (Tiempo t : reto.getTiempoV()) {
                            
                            debug("***\n");
                            debug("const tipo tiempo: " + t.getTipo_tiempo() + "\n");
                            debug("tipo tiempo: " + t.getTipo_tiempo() + "\n");
                            debug("tiempo: " + t.getTiempo() + "\n");
                        }
                        debug("**** FIN******\n");
                        break;
                    case RespuestaEvent.ERROR:
                        debug(respuesta_event.getError().getMensaje() + "\n");
    //                        txtLog.setSelectionStart(txtLog.getText().length());
                }
            }
        };
    }
}
