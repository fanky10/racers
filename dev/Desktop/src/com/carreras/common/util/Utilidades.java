/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package com.carreras.common.util;

import java.awt.Point;
import java.awt.Rectangle;
import java.text.SimpleDateFormat;
import java.util.regex.Pattern;
import javax.swing.JTable;
import javax.swing.JViewport;

/**
 *
 * @author fanky10
 */
public class Utilidades {
    public static SimpleDateFormat FORMATERO_FECHA_HORA = new SimpleDateFormat("dd/MM/yyyy HH:mm");
    public static SimpleDateFormat FORMATERO_FECHA = new SimpleDateFormat("dd/MM/yyyy");
    public static SimpleDateFormat FORMATERO_HORA = new SimpleDateFormat("HH:mm");
    public static boolean esInteger(String text){
        try{
            Integer.parseInt(text);
            return true;
        }catch(NumberFormatException ex){
            //ignored
            return false;
        }
    }
    
    
    public static boolean esPatente(String text){
        return text.matches("[a-zA-Z]{3}-[0-9]{3}");
//        return text.matches("[a-zA-Z][a-zA-Z][a-zA-Z]-[0-9][0-9][0-9]");
    }
    public static void main(String args[]){
        String text = "jjj-666";
        System.out.println("text: "+text+" es patente?? xD "+esPatente(text));
    }
    public static boolean isKeyFound(String arg,String key){
        return(arg.length()>(key+"=").length() && arg.substring(0, (key+"=").length()).equals(key+"="));
    }
    public static String getValue(String arg,String key){
        return arg.substring((key+"=").length());
    }
    public static void scrollToVisible(JTable table, int rowIndex, int vColIndex) {
        if (!(table.getParent() instanceof JViewport)) {
            return;
        }
        JViewport viewport = (JViewport)table.getParent();

        // This rectangle is relative to the table where the
        // northwest corner of cell (0,0) is always (0,0).
        Rectangle rect = table.getCellRect(rowIndex, vColIndex, true);

        // The location of the viewport relative to the table
        Point pt = viewport.getViewPosition();

        // Translate the cell location so that it is relative
        // to the view, assuming the northwest corner of the
        // view is (0,0)
        rect.setLocation(rect.x-pt.x, rect.y-pt.y);

        // Scroll the area into view
        viewport.scrollRectToVisible(rect);
    }
    
    //<editor-fold defaultstate="collapsed" desc="static-util-methods">
    public static String adapta_string(String txt){
        return "\""+txt+"\"";
    }
    public static String traduce_boolean(boolean aBool){
        return aBool?"1":"0";
    }
    public static String traduce_nro(int nro){
        return String.valueOf(nro);
    }

    public static String traduce_nro(float nro){
        return String.valueOf(nro);
    }
    
    public static String traduce_nro(double nro){
        return String.valueOf(nro);
    }

    public static String traduce_nro(String nro_como_cadena){
        return traduce_nro(Float.parseFloat(nro_como_cadena.replace(",", ".")));
    }

    public static String traduce_nro(javax.swing.JTextField txt){
        return traduce_nro(txt.getText());
    }

    public static String traduce_nro(javax.swing.JCheckBox chk){
//        return chk.isSelected()?"1":"0";
        return traduce_boolean(chk.isSelected());
    }

    public static String traduce_time(java.sql.Time t){
        return "'"+new java.text.SimpleDateFormat("HH:mm:ss").format(t)+"'";
    }

    public static String traduce_timestamp(java.util.Date fecha){
        return traduce_date(fecha);
    }

    public static String traduce_date(javax.swing.JTextField txt){
        return traduce_date(txt.getText());
    }

    public static String traduce_date(java.util.Date fecha){
        return traduce_date(new java.text.SimpleDateFormat("dd/MM/yyyy").format(fecha));
    }

    public static String traduce_date(String cadena){
        char[] caracteres=cadena.toCharArray();
        return "'" + transforma_fecha(caracteres) + "'";
    }

    public static String transforma_fecha(char[] caracteres){
        return String.valueOf(caracteres[6])+String.valueOf(caracteres[7])+String.valueOf(caracteres[8])+String.valueOf(caracteres[9]) + "-" + String.valueOf(caracteres[3])+String.valueOf(caracteres[4]) + "-" + String.valueOf(caracteres[0])+String.valueOf(caracteres[1]);
    }

    public static String traduce_timestamp(java.sql.Timestamp ts){
        return "'" + transforma_fecha(new java.text.SimpleDateFormat("dd/MM/yyyy").format(ts).toCharArray()) + " " + new java.text.SimpleDateFormat("HH:mm:ss").format(ts) + "'";
    }

    public static String traduce_str(String cadena){
        return cadena==null?"null":"'" + cadena.replace("'", " ").replace("\r", " ").replace("\n", " ") + "'";
    }

    public static String traduce_str(javax.swing.JTextField txt){
        return "'" + txt.getText().replace("'", " ") + "'";
    }

    public static boolean es_numero(javax.swing.JTextField txt){
        return es_numero(txt.getText());
    }

    public static boolean es_numero(String cadena){
        if(cadena.trim().equals("")){
            return false;
        }
        try{
            Float.parseFloat(cadena.replace(",", "."));
        }catch(Exception e){
            return false;
        }
        return true;
    }

    public static boolean es_fecha(javax.swing.JTextField txt){
        return es_fecha(txt.getText());
    }

    public static boolean es_fecha(String cadena){
        if(cadena.length()!=10){
            return false;
        }else{
            char[] caracteres=cadena.toCharArray();
            if(caracteres[2]!='/' || caracteres[5]!='/'){
                return false;
            }else if(
                    caracteres[0]<'0' || caracteres[0]>'9' ||
                    caracteres[1]<'0' || caracteres[1]>'9' ||
                    caracteres[3]<'0' || caracteres[3]>'9' ||
                    caracteres[4]<'0' || caracteres[4]>'9' ||
                    caracteres[6]<'0' || caracteres[6]>'9' ||
                    caracteres[7]<'0' || caracteres[7]>'9' ||
                    caracteres[8]<'0' || caracteres[8]>'9' ||
                    caracteres[9]<'0' || caracteres[9]>'9'
                    ){
                return false;
            }else if(Integer.valueOf(String.valueOf(caracteres[0])+String.valueOf(caracteres[1]))>31 || Integer.valueOf(String.valueOf(caracteres[3])+String.valueOf(caracteres[4]))>12 || Integer.valueOf(String.valueOf(caracteres[6])+String.valueOf(caracteres[7])+String.valueOf(caracteres[8])+String.valueOf(caracteres[9]))<2000 || Integer.valueOf(String.valueOf(caracteres[6])+String.valueOf(caracteres[7])+String.valueOf(caracteres[8])+String.valueOf(caracteres[9]))>2200){
                return false;
            }
        }
        return true;
    }

    public static String para_mostrar(float nro){
        return String.valueOf(nro).replace(".", ",");
    }
    //</editor-fold>
}
