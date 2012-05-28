/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package com.carreras.servicios;

import com.carreras.dominio.modelo.Tiempo;
import java.util.List;

/**
 *
 * @author Fanky10 <fanky10@gmail.com>
 */
public interface TiempoManager {

    public Integer save(Tiempo tiempo);

    public Tiempo getOne(Integer id);

    public List<Tiempo> getAll();
    
    public List<Tiempo> getTiemposCarril(Integer idCarril);
    
    public Tiempo getTiempo(Integer idCarril, Integer idTipoTiempo);
    
    public Tiempo getMejorTiempo(Integer idTorneo, Integer idInscripto);
}
