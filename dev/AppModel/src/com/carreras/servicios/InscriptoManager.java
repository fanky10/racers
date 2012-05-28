/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package com.carreras.servicios;

import com.carreras.dominio.modelo.Inscripto;
import java.util.List;

/**
 *
 * @author Fanky10 <fanky10@gmail.com>
 */
public interface InscriptoManager {
    public Integer save(Inscripto inscripto);
    
    public Inscripto getOne(Integer id);
    
    public List<Inscripto> getAll();
    
    public List<Inscripto> getAllInscriptosBut(Integer[] idInscriptos);
}
