/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package arduino.entidades;

/**
 *
 * @author Fabian Nonino <<fabian.nonino@gmail.com>>
 */
public class Error {
    public static final int ERROR_TIMEOUT=1;
    public static final int ERROR_COMUNICACION=2;
    private int codigo_error=0;
    private String mensaje;

    public Error(int _codigo_error,String _mensaje) {
        this.mensaje = _mensaje;
        this.codigo_error = _codigo_error;
    }

    public int getCodigo_error() {
        return codigo_error;
    }

    public void setCodigo_error(int codigo_error) {
        this.codigo_error = codigo_error;
    }

    public String getMensaje() {
        return mensaje;
    }

    public void setMensaje(String mensaje) {
        this.mensaje = mensaje;
    }
    
    
}
