/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package com.carreras.servicios;

import com.carreras.dominio.modelo.Carril;
import java.util.List;

/**
 *
 * @author Fanky10 <fanky10@gmail.com>
 */
public interface CarrilManager {

    public Integer save(Carril carril);

    public Carril getOne(Integer id);

    public List<Carril> getAll();
}
