/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package com.carreras.common.logger;

/**
 *
 * @author fanky10
 */
public class CarrerasLogger {
    public static boolean DEBUG = true;
    public static void warn(Class aClass, String message){
        System.err.println("WARN - "+aClass.getName()+" - "+message);
    }
    public static void info(Class aClass, String message){
        System.out.println("INFO - "+aClass.getName()+" - "+message);
    }
    public static void writeException(Class aClass, Exception exception){
        System.out.println("EXCEPTION - "+aClass.getName()+" - "+exception.getMessage());
    }
    public static void debug(Class aClass, String message){
        if(DEBUG)
            System.out.println("DEBUG - "+aClass.getName()+" - "+message);
    }
}
