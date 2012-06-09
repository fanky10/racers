/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package com.carreras.common.emulador;

import com.carreras.common.logger.CarrerasLogger;
import java.io.IOException;
import java.io.ObjectOutputStream;
import java.io.OutputStream;
import java.net.InetAddress;
import java.net.Socket;
import java.net.UnknownHostException;

/**
 *
 * @author Fanky10 <fanky10@gmail.com>
 * es un tcp que sirve para enviarle comandos al servidor (:
 */
public class ArduinoTCPClient {

    public static boolean sendCommand(MensajeCarril mensaje) {
        if (mensaje == null) {
            throw new IllegalArgumentException("el mensaje no puede ser nulo");
        }
        boolean returnValueOnError = false;
        try {
            Socket clientSocket = new Socket(InetAddress.getLocalHost(), ArduinoTCPServer.SINGLE_INSTANCE_NETWORK_SOCKET_PORT);
            OutputStream out = clientSocket.getOutputStream();
            ObjectOutputStream ooStream = new ObjectOutputStream(out);
            ooStream.writeObject(mensaje);  // send serilized payload
            ooStream.close();
            out.close();
            clientSocket.close();
            CarrerasLogger.warn(ArduinoTCPClient.class, "Successfully notified first instance.");
            return true;
        } catch (UnknownHostException e) {
            CarrerasLogger.warn(ArduinoTCPClient.class, e.getMessage() + e);
            return returnValueOnError;
        } catch (IOException e1) {
            CarrerasLogger.warn(ArduinoTCPClient.class, "Error connecting to local port for single instance notification");
            CarrerasLogger.warn(ArduinoTCPClient.class, e1.getMessage() + e1);
            return returnValueOnError;
        }
    }
}
