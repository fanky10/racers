/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package com.carreras.servicios;

import com.carreras.dominio.modelo.Categoria;
import com.carreras.dominio.modelo.EstadoInscriptoCompetenciaCarrera;
import com.carreras.dominio.modelo.InscriptoCompetencia;
import java.util.List;

/**
 *
 * @author Fanky10 <fanky10@gmail.com>
 */
public interface InscriptoCompetenciaManager {

    public Integer save(InscriptoCompetencia inscriptoCompetencia);

    public void update(InscriptoCompetencia inscriptoCompetencia);

    public InscriptoCompetencia getOne(Integer id);

    public List<InscriptoCompetencia> getAll();

    public List<InscriptoCompetencia> getInscriptosCompetencia(Integer idCompetencia);

    public List<InscriptoCompetencia> getEstadoInscriptosCompetencia(Integer idCompetencia, EstadoInscriptoCompetenciaCarrera estado);

    public List<InscriptoCompetencia> getInscriptosCompetencia(Integer idCompetencia, Integer idCategoria);

    public List<InscriptoCompetencia> getInscriptosCompetenciaLibre(Integer idCompetencia);

    public List<Categoria> getCategoriasEnUso(Integer idCompetencia);
    
    public List<InscriptoCompetencia> getAllTorneo(Integer idTorneo);

    public List<InscriptoCompetencia> getEstadoInscriptosCompetencia(Integer idCompetencia, EstadoInscriptoCompetenciaCarrera estadoInscriptoCompetenciaCarrera, Integer idCategoria);
    
}
