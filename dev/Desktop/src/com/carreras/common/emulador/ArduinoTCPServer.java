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
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.ObjectInputStream;
import java.net.InetAddress;
import java.net.ServerSocket;
import java.net.Socket;
import java.net.UnknownHostException;

/**
 *
 * @author Fanky10 <fanky10@gmail.com>
 */
public class ArduinoTCPServer extends arduino.Arduino implements Runnable {

    private static ServerSocket socket;
    /** Randomly chosen, but static, high socket number */
    public static int SINGLE_INSTANCE_NETWORK_SOCKET_PORT = 40123;//tiene que ser modificado por el sistema que lo use
    /** Must end with newline */
    public static final String COMANDO_CARRIL1 = "$$Carril1$$\n";
    public static final String COMANDO_CARRIL2 = "$$Carril2$$\n";
    private Thread internal_thread;

    public ArduinoTCPServer(ArduinoEventListener arduinoEvtListener) {
        super(arduinoEvtListener);
        CarrerasLogger.info(ArduinoTCPServer.class, "TCP Arduino Driver intanced on port: " + SINGLE_INSTANCE_NETWORK_SOCKET_PORT);
    }

    /**
     * 
     * @return true if everything went fine, false if not
     */
    private boolean init() {
        // returnValueOnError should be true if lenient (allows app to run on network error) or false if strict.
        boolean returnValueOnError = true;
        if (SINGLE_INSTANCE_NETWORK_SOCKET_PORT < 40000) {
            CarrerasLogger.warn(ArduinoTCPClient.class, "Bad configuration SINGLE_INSTANCE_NETWORK_SOCKET_PORT needs to be > 40000");
            return false;
        }
        try {
            //final ServerSocket
            socket = new ServerSocket(SINGLE_INSTANCE_NETWORK_SOCKET_PORT, 10, InetAddress.getLocalHost());
            CarrerasLogger.info(ArduinoTCPClient.class, "Listening for application messages on socket " + SINGLE_INSTANCE_NETWORK_SOCKET_PORT);
            internal_thread = new Thread(this);
            internal_thread.start();
        } catch (UnknownHostException e) {
            System.out.println(e.getMessage() + " " + e);
            return returnValueOnError;
        } catch (IOException e) {
            CarrerasLogger.warn(ArduinoTCPClient.class, "Port is already taken.  Notifying first instance.");
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
                    InputStream inputStream = client.getInputStream();
                    ObjectInputStream oiStream = new ObjectInputStream(inputStream);
                    MensajeCarril mensajeRecibido = (MensajeCarril) oiStream.readObject();
                    System.out.println("message captured: " + mensajeRecibido.toString());
                    parseMessage(mensajeRecibido);
                    inputStream.close();
                    client.close();
                } catch (IOException e) {
                    socketClosed = true;
                } catch (ClassNotFoundException ex) {
                    System.err.println("classNoFound: " + ex.getLocalizedMessage());
                    socketClosed = true;
                }
            }
        }
    }

//    private void parseMessage(String message) {
//        RespuestaEvent rta = new RespuestaEvent(this, RespuestaEvent.CARRIL);
////        rta.setDatos(new Carril(SINGLE_INSTANCE_NETWORK_SOCKET_PORT, null));
//        if (COMANDO_CARRIL1.trim().equals(message.trim())) {
//            rta.setDatos(new Carril(1, getTiemposAleatorios()));
//        } else if (COMANDO_CARRIL2.trim().equals(message.trim())) {
//            rta.setDatos(new Carril(2, getTiemposAleatorios()));
//        }
//        arduino_event_listener.Estado_Datos(rta);
//
//    }

    private void parseMessage(MensajeCarril mensajeCarril) {
        Carril c = mensajeCarril.getCarril();
        RespuestaEvent rta = new RespuestaEvent(this, RespuestaEvent.CARRIL);
        rta.setDatos(c);
        arduino_event_listener.Estado_Datos(rta);
    }

    @Override
    public void finalize() throws IOException {
        close();
    }

    @Override
    public void ini() {
        if (init()) {
            arduino_event_listener.EstadoArduino(new ArduinoEvent(this, ArduinoEvent.ARDUINO_ONLINE, "Arduino Online"));
        } else {
            arduino_event_listener.EstadoArduino(new ArduinoEvent(this, ArduinoEvent.ARDUINO_OFFLINE, "Arduino Offline Error"));
        }
    }

    @Override
    public void close() {
        try {
            socket.close();
        } catch (IOException ignored) {
        }

    }

    @Override
    public void respuesta(RespuestaEvent respuestaEvent) {
        arduino_event_listener.Estado_Datos(respuestaEvent);
    }

    public static void main(String args[]) {
        Configuracion.init_conf(args);
        ArduinoTCPServer server = new ArduinoTCPServer(createEventListener());
        server.ini();
    }

    private static ArduinoEventListener createEventListener() {
        return new ArduinoEventListener() {

            @Override
            public void EstadoArduino(ArduinoEvent arduino_event) {

                if (arduino_event.getEstado() == ArduinoEvent.ARDUINO_ONLINE) {
                    muestraEstado("Arduino ONLINE - Esperando datos...");
                } else {
                    muestraEstado(arduino_event.getMensaje());
                }
            }

            @Override
            public void Estado_Datos(RespuestaEvent respuesta_event) {
                switch (respuesta_event.getNro_evento()) {
                    case RespuestaEvent.CARRIL:
                        if (respuesta_event.getDatos().getTipo() == Datos.CARRIL) {
                            muestraEstado("recibiendo tiempos carril");
                        } else if (respuesta_event.getDatos().getTipo() == Datos.ADELANTAMIENTO) {
                            muestraEstado("hubo adelantamiento");
                        } else if (respuesta_event.getDatos().getTipo() == Datos.ROTURA) {
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

    private static void muestraEstado(String message) {
        CarrerasLogger.info(ArduinoTCPServer.class, message);
    }
}
