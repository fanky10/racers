/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package arduino.logs;

import org.apache.log4j.Logger;
import org.apache.log4j.PropertyConfigurator;

/**
 *
 * @author Prog1
 */
public class MyLogger extends Logger {
    private static String FQCN = MyLogger.class.getName() + ".";
    public static String CONFIGURATION_FILE = "ArduinoLog.properties";
//  It's enough to instantiate a factory once and for all.
  private static MyLoggerFactory myFactory = new MyLoggerFactory();

    public static Logger getLogger(Class aClass) {
        return getLogger(aClass.getName());
    }

  /**
     Just calls the parent constuctor.
   */
  public MyLogger(String name) {
    super(name);
  }

  /**
     This method overrides {@link Logger#getLogger} by supplying
     its own factory type as a parameter.
  */
    public  static  Logger getLogger(String className) {
        //configures the logger from a properties file
        //default user.dir
        PropertyConfigurator.configure(getFile_path()+java.io.File.separator+CONFIGURATION_FILE);
        return Logger.getLogger(className, myFactory);
    }
    
    private static String file_path;

    public static String getFile_path() {
        if(file_path==null || file_path.trim().equals(""))
            return System.getProperty("user.dir");
        return file_path;
    }

    public static void setFile_path(String file_path) {
        MyLogger.file_path = file_path;
    }
}
