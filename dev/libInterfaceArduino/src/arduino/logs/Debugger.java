/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package arduino.logs;

/**
 *
 * @author Prog1
 */
public class Debugger {
    public static boolean DEBUG=true;
    public static void debug(String message){
        System.out.println("debug: "+message);
        debug(message ,Debugger.class.getName());
    }
    public static void debug(String message,String className){
        if(DEBUG){
            System.out.println("debug: "+message);
            MyLogger.getLogger(className).info(message);
        }
    }
    public static void muestraCartel(String aTitle,String aText,int messageType){
        javax.swing.JOptionPane.showMessageDialog(null, aText, aTitle,messageType);
    }
    public static void writeException(Exception e){
        writeException(e,Debugger.class.getName());
    }
    public static void writeException(Exception e,String classname){
        //sn.config.Configuracion.getLogWriter().write(e.getMessage());
        MyLogger.getLogger(classname).warn(e.getMessage());
        System.out.println("exception message: "+e.getMessage());
        e.printStackTrace();
    }
    public static void writeInfoMessage(String message){
        MyLogger.getLogger(Debugger.class.getName()).info(message);
    }
    public static void writeInfoMessage(String message, String classname){
        MyLogger.getLogger(classname).info(message);
    }
    public static void showWarningException(Exception e,String title,String message){
        writeException(e);
        muestraCartel(title,message,javax.swing.JOptionPane.WARNING_MESSAGE);
    }
    public static void showErrorException(Exception e,String title,String message){
        showErrorException(Debugger.class.getName(),e,title,message);
    }
    public static void showErrorException(String classname,Exception e,String title,String message){
        writeException(e,classname);
        muestraCartel(title,message,javax.swing.JOptionPane.ERROR_MESSAGE);
    }
    public static void warn(String message,Class clazz){
        System.out.println("warn: "+message);
        MyLogger.getLogger(clazz).warn(message);
    }
    public static void info(String message,Class clazz){
        System.out.println("info: "+message);
        MyLogger.getLogger(clazz).warn(message);
    }
    
}
