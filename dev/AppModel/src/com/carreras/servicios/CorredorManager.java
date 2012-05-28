/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package com.carreras.servicios;

import com.carreras.dominio.modelo.Corredor;
import java.util.List;

/**
 *
 * @author Fanky10 <fanky10@gmail.com>
 */
public interface CorredorManager {
    public Integer guardar(Corredor c);

    public void eliminar(Corredor c);

    public List<Corredor> getCorredores();

    public Corredor getCorredor(int id);
}
