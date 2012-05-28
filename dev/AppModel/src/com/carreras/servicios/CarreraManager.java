/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package com.carreras.servicios;

import com.carreras.dominio.modelo.Carrera;
import java.util.List;

/**
 *
 * @author Fanky10 <fanky10@gmail.com>
 */
public interface CarreraManager {

    public Integer save(Carrera carrera);
    
    public Carrera getOne(Integer id);
    
    public List<Carrera> getAll();
}
