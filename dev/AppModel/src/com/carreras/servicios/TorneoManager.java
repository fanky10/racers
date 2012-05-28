/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package com.carreras.servicios;

import com.carreras.dominio.modelo.Torneo;
import java.util.List;

/**
 *
 * @author Fanky10 <fanky10@gmail.com>
 */
public interface TorneoManager {

    public Integer save(Torneo torneo);

    public Torneo getOne(Integer idTorneo);

    public List<Torneo> getAll();
}
