/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package com.carreras.common.emulador;

import com.carreras.common.logger.CarrerasLogger;
import java.awt.BorderLayout;
import java.awt.GridLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.IOException;
import java.io.OutputStream;
import java.net.InetAddress;
import java.net.Socket;
import java.net.UnknownHostException;
import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JPanel;

/**
 *
 * @author Fanky10 <fanky10@gmail.com>
 * es un tcp que sirve para enviarle comandos al servidor (:
 */
public class ArduinoTCPClient {
    private static boolean sendCommand(byte[] bytes) {
        boolean returnValueOnError = false;
        try {
            Socket clientSocket = new Socket(InetAddress.getLocalHost(), ArduinoTCPServer.SINGLE_INSTANCE_NETWORK_SOCKET_PORT);
            OutputStream out = clientSocket.getOutputStream();
            out.write(bytes);
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
    public static boolean sendCommand(int nro_carril){
        if(nro_carril<1 || nro_carril>2){
            throw new IllegalArgumentException("carril not supported!");
        }
        if(nro_carril == 1){
            return sendCommand(ArduinoTCPServer.COMANDO_CARRIL1.getBytes());
        }else if(nro_carril == 2){
            return sendCommand(ArduinoTCPServer.COMANDO_CARRIL2.getBytes());
        }else{
            throw new IllegalArgumentException("carril not supported!");
        }
        
    }
    public static void main(String args[]){
        
//        ArduinoTCPClient.sendCommand(1);
//        ArduinoTCPClient.sendCommand(2);
        //en vez de algo tan cabeza.. mejor una gui! :P
        JFrame frame = new JFrame("Cliente Arduino");
        GridLayout grid = new GridLayout(0, 1);
        JPanel pnl = new JPanel(grid);
        JButton btn = new JButton("Comando 1");
        btn.addActionListener(new ActionListener() {

            @Override
            public void actionPerformed(ActionEvent ae) {
                ArduinoTCPClient.sendCommand(1);
            }
        });
        btn = new JButton("Tiempos Carril 1");
        btn.addActionListener(new ActionListener() {

            @Override
            public void actionPerformed(ActionEvent ae) {
                ArduinoTCPClient.sendCommand(1);
            }
        });
        pnl.add(btn);
        btn = new JButton("Tiempos Carril 2");
        btn.addActionListener(new ActionListener() {

            @Override
            public void actionPerformed(ActionEvent ae) {
                ArduinoTCPClient.sendCommand(2);
            }
        });
        pnl.add(btn);
        btn = new JButton("Enviar Ambos tiempos!");
        btn.addActionListener(new ActionListener() {

            @Override
            public void actionPerformed(ActionEvent ae) {
                ArduinoTCPClient.sendCommand(1);
                ArduinoTCPClient.sendCommand(2);
            }
        });
        pnl.add(btn);
        frame.getContentPane().add(pnl,BorderLayout.CENTER);
        //config
        
        frame.pack();
        frame.setBounds(0, 0, 400, 200);
        frame.setLocationRelativeTo(null);
        frame.setDefaultCloseOperation(javax.swing.WindowConstants.EXIT_ON_CLOSE);
        frame.setVisible(true);
    }
}
