/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package com.carreras.gui;

import com.carreras.common.config.Configuracion;
import com.carreras.common.logger.CarrerasLogger;
import com.carreras.common.util.ApplicationInstanceManager;
import java.sql.SQLException;

/**
 *
 * @author fanky10<fanky@gmail.com>
 */
public class Main {
    private static javax.swing.JFrame frmMain;
    private static boolean VERIFICA_APERTURA=true;
    public static void main(String args[]){
        Configuracion.init_conf(args);
        verifica_apertura();    
        frmMain = new frmMain();//new frmMainOld();
        showPpal();
    }
    public static void verifica_apertura(){
        if(!VERIFICA_APERTURA){
            System.out.println("no debo verificar apertura");
            return;
        }
        if (!ApplicationInstanceManager.registerInstance()) {
            //instance already running.
            CarrerasLogger.warn(Main.class,"Another instance of this application is already running.  Exiting.");
            javax.swing.JOptionPane.showMessageDialog(frmMain, "El sistema se encuentra corriendo, no se puede abrir otro");
            System.exit(0);
        }
    }
    private static void showPpal(){
        if(frmMain==null){
            System.exit(-1);
            return ;
        }
        frmMain.setVisible(true);
        frmMain.toFront();
    }

//    private static void createDB(){
//        try{
//            new Database().create();
//        }catch(SQLException ex){
//            System.out.println("exception: "+ex.getLocalizedMessage());
//        }
//    }
    
    
   
}
