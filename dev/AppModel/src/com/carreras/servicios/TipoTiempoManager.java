/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package com.carreras.servicios;

import com.carreras.dominio.modelo.TipoTiempo;
import java.util.List;

/**
 *
 * @author Fanky10 <fanky10@gmail.com>
 */
public interface TipoTiempoManager {

    public Integer save(TipoTiempo carril);

    public TipoTiempo getOne(Integer id);

    public List<TipoTiempo> getAll();
}
