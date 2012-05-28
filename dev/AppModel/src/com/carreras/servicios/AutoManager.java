/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package com.carreras.servicios;

import com.carreras.dominio.modelo.Auto;
import java.util.List;

/**
 *
 * @author Fanky10 <fanky10@gmail.com>
 */
public interface AutoManager {

    public Integer guardar(Auto a);

    public void eliminar(Auto a);

    public List<Auto> getAutos();

    public Auto getAuto(int id);
}
