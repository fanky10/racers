/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package com.carreras.common.emulador;

import arduino.entidades.Carril;
import arduino.entidades.Datos;
import arduino.entidades.Tiempo;
import arduino.entidades.Tiempos;
import arduino.eventos.ArduinoEvent;
import arduino.eventos.ArduinoEventListener;
import arduino.eventos.RespuestaEvent;
import com.carreras.common.config.Configuracion;
import com.carreras.common.logger.CarrerasLogger;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.InetAddress;
import java.net.ServerSocket;
import java.net.Socket;
import java.net.UnknownHostException;


/**
 *
 * @author Fanky10 <fanky10@gmail.com>
 */
public class ArduinoTCPServer extends arduino.Arduino implements Runnable{
    private static ServerSocket socket;
    /** Randomly chosen, but static, high socket number */
    public static int SINGLE_INSTANCE_NETWORK_SOCKET_PORT = 40123;//tiene que ser modificado por el sistema que lo use

    /** Must end with newline */
    public static final String COMANDO_CARRIL1 = "$$Carril1$$\n";
    public static final String COMANDO_CARRIL2 = "$$Carril2$$\n";
    private Thread internal_thread;

    public ArduinoTCPServer(ArduinoEventListener arduinoEvtListener) {
        super(arduinoEvtListener);
        CarrerasLogger.info(ArduinoTCPServer.class,"TCP Arduino Driver intanced on port: "+SINGLE_INSTANCE_NETWORK_SOCKET_PORT);
    }
    
    /**
     * 
     * @return true if everything went fine, false if not
     */
    private boolean init() {
        // returnValueOnError should be true if lenient (allows app to run on network error) or false if strict.
        boolean returnValueOnError = true;
        if(SINGLE_INSTANCE_NETWORK_SOCKET_PORT<40000){
            CarrerasLogger.warn(ArduinoTCPClient.class,"Bad configuration SINGLE_INSTANCE_NETWORK_SOCKET_PORT needs to be > 40000");
            return false;
        }            
        try {
            //final ServerSocket
            socket = new ServerSocket(SINGLE_INSTANCE_NETWORK_SOCKET_PORT, 10, InetAddress.getLocalHost());
            CarrerasLogger.info(ArduinoTCPClient.class,"Listening for application messages on socket " + SINGLE_INSTANCE_NETWORK_SOCKET_PORT);
            internal_thread = new Thread(this);
            internal_thread.start();
        } catch (UnknownHostException e) {
             System.out.println(e.getMessage()+" "+ e);
            return returnValueOnError;
        } catch (IOException e) {
             CarrerasLogger.warn(ArduinoTCPClient.class,"Port is already taken.  Notifying first instance.");
            return returnValueOnError;

        }
        return true;
    }
    @Override
    public void run() {
        boolean socketClosed = false;
        while (!socketClosed) {
            if (socket.isClosed()) {
                socketClosed = true;
            } else {
                try {
                    Socket client = socket.accept();
                    BufferedReader in = new BufferedReader(new InputStreamReader(client.getInputStream()));
                    String message = in.readLine();
                    System.out.println("message captured: " + message);
                    parseMessage(message);
                    in.close();
                    client.close();
                } catch (IOException e) {
                    socketClosed = true;
                }
            }
        }
    }
    private void parseMessage(String message){
        RespuestaEvent rta = new RespuestaEvent(this, RespuestaEvent.CARRIL);
//        rta.setDatos(new Carril(SINGLE_INSTANCE_NETWORK_SOCKET_PORT, null));
        if (COMANDO_CARRIL1.trim().equals(message.trim())) {
            rta.setDatos(new Carril(1, getTiemposAleatorios()));
        }else if(COMANDO_CARRIL2.trim().equals(message.trim())) {
            rta.setDatos(new Carril(2, getTiemposAleatorios()));
        }
        arduino_event_listener.Estado_Datos(rta);
        
    }
    private Tiempos getTiemposAleatorios(){
        Tiempos reto = new Tiempos();
        double anterior = getRandom(0);
        Tiempo t = new Tiempo(Tiempo.TIEMPO_REACCION, anterior);
        reto.add(t);
        anterior = getRandom(anterior);
        t = new Tiempo(Tiempo.TIEMPO_100MTS, anterior);
        reto.add(t);
        anterior = getRandom(anterior);
        t = new Tiempo(Tiempo.TIEMPO_FIN, anterior);
        reto.add(t);
        return reto;
    }
    /**
     * obtiene un 
     * @param anterior es tiempo anterior
     * @return random mayor al anterior y menor a 20 :P
     */
    private double getRandom(double anterior){
        double ran = 0d;
        while(true){
            ran = getRandom();
            if(ran>=anterior && ran <=20d){
                break;
            }
        }
        //juro que trate de hacerlo con un do{}while(cond);
        //pero no me salio, esta estructura es mas sencilla de comprender :P
        return ran;
    }
    /**
     * podria validar que sea menor al anterior y menor a 20 :P
     * @return 
     */
    private double getRandom(){
        double ran = Math.random();
        return ran * 100;
    }
    @Override
    public void finalize() throws IOException{
        close();
    }
    @Override
    public void ini() {
        if(init()){
            arduino_event_listener.EstadoArduino(new ArduinoEvent(this, ArduinoEvent.ARDUINO_ONLINE, "Arduino Online"));
        }else{
            arduino_event_listener.EstadoArduino(new ArduinoEvent(this, ArduinoEvent.ARDUINO_OFFLINE, "Arduino Offline Error"));
        }
    }
    @Override
    public void close(){
        try {
            socket.close();
        } catch (IOException ignored) {
            
        }
            
    }
    @Override
    public void respuesta(RespuestaEvent respuestaEvent) {
        arduino_event_listener.Estado_Datos(respuestaEvent);
    }
    public static void main(String args[]){
        Configuracion.init_conf(args);
        ArduinoTCPServer server = new ArduinoTCPServer(createEventListener());
        server.ini();
    }
    private static ArduinoEventListener createEventListener(){
        return new ArduinoEventListener() {

            @Override
            public void EstadoArduino(ArduinoEvent arduino_event) {               
                
                if(arduino_event.getEstado() == ArduinoEvent.ARDUINO_ONLINE){
                    muestraEstado("Arduino ONLINE - Esperando datos...");
                }else{
                    muestraEstado(arduino_event.getMensaje());
                }
            }

            @Override
            public void Estado_Datos(RespuestaEvent respuesta_event) {
                switch (respuesta_event.getNro_evento()) {
                    case RespuestaEvent.CARRIL:
                        if(respuesta_event.getDatos().getTipo()==Datos.CARRIL){
                            muestraEstado("recibiendo tiempos carril");
                        }else if(respuesta_event.getDatos().getTipo()==Datos.ADELANTAMIENTO){
                            muestraEstado("hubo adelantamiento");
                        }else if(respuesta_event.getDatos().getTipo()==Datos.ROTURA){
                            muestraEstado("hubo roturas que");
                        }
                        break;                        
                    case RespuestaEvent.ERROR:
                        muestraEstado(respuesta_event.getError().getMensaje());
                        //reinicia arduino :D
//                        reinicia_comunicacion();
                        break;
                }
            }
            
        };
    }
    private static void muestraEstado(String message){
        CarrerasLogger.info(ArduinoTCPServer.class, message);
    }
    
    //singleton!
//    private static ArduinoTCPServer object;
//    private static final String lockObj = "Lock"; // -- Use for locking --
//
//    public static ArduinoTCPServer getInstance() {
//        if (object != null) {
//            return object;
//        } else {
//            // --- Bloque sincronizado para evitar que dos o mas hilos entren en el mismo.
//            synchronized (lockObj) {
//                if (object == null) {
//                    createInstance();
//                }
//            } 
//            return object;
//        }
//    }
//
//    private static void createInstance() {
//        object = new ArduinoTCPServer();
//    }
}
