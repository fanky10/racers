/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package com.carreras.common.util;

import com.carreras.common.logger.CarrerasLogger;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.net.InetAddress;
import java.net.ServerSocket;
import java.net.Socket;
import java.net.UnknownHostException;

/**
 *
 * @author Prog1
 */
public class ApplicationInstanceManager {

    private static ApplicationInstanceListener subListener;
    private static ServerSocket socket;
    /** Randomly chosen, but static, high socket number */
    public static int SINGLE_INSTANCE_NETWORK_SOCKET_PORT = 0;//tiene que ser modificado por el sistema que lo use
    /** Must end with newline */
    public static final String SINGLE_INSTANCE_SHARED_KEY = "$$NewInstance$$\n";

    /**
     * Registers this instance of the application.
     *
     * @return true if first instance, false if not.
     */
    public static boolean registerInstance() {
        // returnValueOnError should be true if lenient (allows app to run on network error) or false if strict.
        boolean returnValueOnError = true;
        if (SINGLE_INSTANCE_NETWORK_SOCKET_PORT < 40000) {//45577
            CarrerasLogger.warn(ApplicationInstanceManager.class, "Bad configuration SINGLE_INSTANCE_NETWORK_SOCKET_PORT needs to be > 40000");
            return false;
        }

        // try to open network socket
        // if success, listen to socket for new instance message, return true
        // if unable to open, connect to existing and send new instance message, return false
        try {
            //final ServerSocket
            socket = new ServerSocket(SINGLE_INSTANCE_NETWORK_SOCKET_PORT, 10, InetAddress.getLocalHost());
            CarrerasLogger.info(ApplicationInstanceManager.class, "Listening for application instances on socket " + SINGLE_INSTANCE_NETWORK_SOCKET_PORT);
            Thread instanceListenerThread = new Thread(new Runnable() {

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
                                if (SINGLE_INSTANCE_SHARED_KEY.trim().equals(message.trim())) {
                                    CarrerasLogger.warn(ApplicationInstanceManager.class, "Shared key matched - new application instance found");
                                    fireNewInstance();
                                }
                                in.close();
                                client.close();
                            } catch (IOException e) {
                                socketClosed = true;
                            }
                        }
                    }
                }
            });
            instanceListenerThread.start();
            // listen
        } catch (UnknownHostException e) {
            System.out.println(e.getMessage() + " " + e);
            return returnValueOnError;
        } catch (IOException e) {
            CarrerasLogger.warn(ApplicationInstanceManager.class, "Port is already taken.  Notifying first instance.");
            try {
                Socket clientSocket = new Socket(InetAddress.getLocalHost(), SINGLE_INSTANCE_NETWORK_SOCKET_PORT);
                OutputStream out = clientSocket.getOutputStream();
                out.write(SINGLE_INSTANCE_SHARED_KEY.getBytes());
                out.close();
                clientSocket.close();
                CarrerasLogger.warn(ApplicationInstanceManager.class, "Successfully notified first instance.");
                return false;
            } catch (UnknownHostException e1) {
                CarrerasLogger.warn(ApplicationInstanceManager.class, e.getMessage() + e);
                return returnValueOnError;
            } catch (IOException e1) {
                CarrerasLogger.warn(ApplicationInstanceManager.class, "Error connecting to local port for single instance notification");
                CarrerasLogger.warn(ApplicationInstanceManager.class, e1.getMessage() + e1);
                return returnValueOnError;
            }

        }
        return true;
    }

    public static void destroyInstance() {
        try {
            if (socket != null) //may be theres no instance created
            {
                socket.close();
            }
        } catch (IOException ex) {
            CarrerasLogger.writeException(ApplicationInstanceManager.class, ex);
            System.exit(0);
        }
    }

    public static void setApplicationInstanceListener(ApplicationInstanceListener listener) {
        subListener = listener;
    }

    private static void fireNewInstance() {
        if (subListener != null) {
            subListener.newInstanceCreated();
        }
    }
}
