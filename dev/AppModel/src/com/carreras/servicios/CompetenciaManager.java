/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package com.carreras.servicios;

import com.carreras.dominio.modelo.Categoria;
import com.carreras.dominio.modelo.Competencia;
import java.util.List;

/**
 *
 * @author Fanky10 <fanky10@gmail.com>
 */
public interface CompetenciaManager {

    public Integer save(Competencia competencia);

    public Competencia getOne(Integer idCompetencia);

    public List<Competencia> getAll();
    
    public Competencia getCompetenciaActual(Integer idCategoria);
}
