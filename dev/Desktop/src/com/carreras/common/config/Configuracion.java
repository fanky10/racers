/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package com.carreras.common.config;

import com.carreras.common.logger.CarrerasLogger;
import com.carreras.common.util.ApplicationInstanceManager;
import com.carreras.common.util.ArduinoManager;
import com.carreras.common.util.Utilidades;
import com.carreras.dominio.modelo.Categoria;
import com.carreras.dominio.modelo.TipoTiempo;
import com.carreras.servicios.ServiceManager;
import com.carreras.servicios.impl.CategoriaManagerImpl;
import com.carreras.servicios.impl.ServiceManagerImpl;
import com.carreras.servicios.impl.TipoTiempoManagerImpl;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.List;
import java.util.Properties;

/**
 *
 * @author fanky10<fanky@gmail.com>
 */
public class Configuracion {
    //warns connection to db
    public static boolean WARN=true;
    //properties file of a db
    public static String DB_PROPERTIES_FILE="default_db.properties";
    //system path as default user dir
    private static String pathConfig=null;
    private static Configuracion singleConfiguration = null;
    private static final String CONFIGURATION_PROPERTIES_FILE = "configuration.properties";
    public static final String PROPERTIES_PACKAGE = "/com/carreras/properties/";
    public static Configuracion getInstance(){
        if(singleConfiguration == null){
            createInstance();
        }
        return singleConfiguration;
    }
    
    private static void createInstance(){
        singleConfiguration = new Configuracion();
    }
    
    
    
    
    public static String getCurrentInnerValue(String inner_prop_file,String key,String default_value){        
        InputStream is = null;
        try{
            is = Configuracion.class.getResourceAsStream(inner_prop_file);
            Properties properties = new Properties();
            properties.load(is);
            return properties.getProperty(key);
        }catch(Exception e){
            //ignored --que mas puedo hacer..?
            return default_value;
        }finally{
            try{
                if(is!=null)
                    is.close();
            }catch(IOException ex){
                //ignored --que mas puedo hacer..?
            }
        }
    }
    private static String getConfigurationValue(String propertyFile,String key, String defaultValue){
        InputStream is = null;
        try{
            is = new FileInputStream(propertyFile);
            Properties properties = new Properties();
            properties.load(is);
            return properties.getProperty(key);
        }catch(java.io.IOException e){
            //ignored --que mas puedo hacer..?
            return defaultValue;
        }finally{
            try{
                if(is!=null)
                    is.close();
            }catch(IOException ex){
                //ignored --que mas puedo hacer..?
            }
        }
    }
    
    public static String getCurrentSysVersion(){
        return getCurrentInnerValue(PROPERTIES_PACKAGE + "versioning.properties", "version", "0.0");
    }
    public static Boolean isAutoiniciaCarrera(){
        final String key = "autoinicia_carrera";
        final String defaultValue = "0";//false
        String value = getCurrentInnerValue(PROPERTIES_PACKAGE + CONFIGURATION_PROPERTIES_FILE, key, null);
        if(value==null){
            value = getConfigurationValue(CONFIGURATION_PROPERTIES_FILE, key, defaultValue);
        }
        return value.equals("1");
    }
    public static Boolean isMuestraMensajes(){
        final String key = "muestra_mensajes";
        final String defaultValue = "0";//false
        String value = getCurrentInnerValue(PROPERTIES_PACKAGE + CONFIGURATION_PROPERTIES_FILE, key, null);
        if(value==null){
            value = getConfigurationValue(CONFIGURATION_PROPERTIES_FILE, key, defaultValue);
        }
        return value.equals("1");
    }
    
    private int cant_carriles; //por default
    private List<Categoria> categorias;
    private List<TipoTiempo> tipoTiempos;
    //service manager!
    private ServiceManager serviceManager;
    
    /*
     * carga todas los atributos de la tabla configuracion de la db
     */
    private Configuracion(){
        cant_carriles = 2;//= Integer.parseInt(nConfig.getValue("cant_carriles", "2"));//default
        categorias = new CategoriaManagerImpl().getAll();
        tipoTiempos = new TipoTiempoManagerImpl().getAll();
        //configuracion de los managers! :D
        serviceManager = new ServiceManagerImpl();
    }

    public List<Categoria> getCategorias() {
        return categorias;
    }

    public List<TipoTiempo> getTipoTiempos() {
        return tipoTiempos;
    }

    public ServiceManager getServiceManager() {
        return serviceManager;
    }
    
    
    
    
    public static void init_conf(String args[]){
        arduino.logs.Debugger.DEBUG=false;
        ApplicationInstanceManager.SINGLE_INSTANCE_NETWORK_SOCKET_PORT=45577;
        for(String a: args){
            if(Utilidades.isKeyFound(a, "ard_debug")){
                arduino.logs.Debugger.DEBUG = Utilidades.getValue(a, "ard_debug").equals("1");
            }
            if(Utilidades.isKeyFound(a, "car_debug")){
                CarrerasLogger.DEBUG = Utilidades.getValue(a, "car_debug").equals("1");
            }
            if(Utilidades.isKeyFound(a, "ard_instance")){
                ArduinoManager.ARDUINO_INSTANCE = Integer.parseInt(Utilidades.getValue(a, "ard_instance"));
            }
        }
    }
    
    public static Properties getDBProperties() {
        String full_path = getPath_config()+java.io.File.separator+ DB_PROPERTIES_FILE;
        System.out.println("INFO: getting db configuration from: "+full_path);
        return getDBProperties(full_path);
    }
    
    public static Properties getDBProperties(String file_path) {
        try{
            Properties props = new Properties();
            props.load(new FileInputStream(file_path));
            return props;
        }
        catch(IOException ex){
            warn("ioe: "+ex);
            System.exit(-1);
        }
        return null;
    }
    
    private static void warn(String text){
        if(WARN){
            System.out.println("WARNING: "+text);
            javax.swing.JOptionPane.showMessageDialog(null, text);
        }
    }
    /**
     * se utiliza un metodo en vez de un atributo publico por la necesidad de validar internamente si es correcto el path actual
     * @return el path de configuracion de la libreria de datos
     */
    public static String getPath_config() {
        if(pathConfig == null || pathConfig.isEmpty()){
            return System.getProperty("user.dir");
        }
        return pathConfig;
    }

    public static void setPath_config(String path_config) {
        Configuracion.pathConfig = path_config;
    }

    public int getCant_carriles() {
        return cant_carriles;
    }
    
    
    

}

