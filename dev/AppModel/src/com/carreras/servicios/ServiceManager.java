/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package com.carreras.servicios;

import com.carreras.dominio.modelo.Auto;
import com.carreras.dominio.modelo.Carrera;
import com.carreras.dominio.modelo.Carril;
import com.carreras.dominio.modelo.Categoria;
import com.carreras.dominio.modelo.Competencia;
import com.carreras.dominio.modelo.Corredor;
import com.carreras.dominio.modelo.EstadoCompetencia;
import com.carreras.dominio.modelo.EstadoInscriptoCompetenciaCarrera;
import com.carreras.dominio.modelo.Inscripto;
import com.carreras.dominio.modelo.InscriptoCompetencia;
import com.carreras.dominio.modelo.Tiempo;
import com.carreras.dominio.modelo.TipoTiempo;
import com.carreras.dominio.modelo.Torneo;
import java.util.List;

/**
 *
 * @author Fanky10 <fanky10@gmail.com>
 */
public interface ServiceManager {

    public Integer saveAuto(Auto auto);

    public Integer saveCorredor(Corredor corredor);

    public Integer saveInscripto(Inscripto inscripto);

    public Integer saveCarrera(Carrera carrera);

    public Integer saveCarril(Carril carril);

    public Integer saveTiempo(Tiempo tiempo);

    public Integer saveTorneo(Torneo torneo);

    public Integer saveCompetencia(Competencia competencia);

    public Integer saveInscriptoCompetencia(InscriptoCompetencia inscriptoCompetencia);

    public void updateInscriptoCompetencia(InscriptoCompetencia inscriptoCompetencia);

    public List<Inscripto> getAllInscriptos();

    public List<Inscripto> getAllInscriptosBut(List<InscriptoCompetencia> inscriptosUsados);

    public Categoria getCategoria(Integer id);

    public Integer saveCategoria(Categoria categoria);

    /**
     * 
     * @param torneo
     * @param inscripto
     * @return la categoria generada a partir del mejor tiempo que tuvo un inscripto en el torneo actual
     */
    public Categoria getCategoria(Torneo torneo, Inscripto inscripto);

    public List<Categoria> getAllCategorias();

    public TipoTiempo getTipoTiempo(Integer id);

    public List<Tiempo> getTiemposCarril(Carril carril);

    public Tiempo getTiempo(Carril carril, TipoTiempo tipoTiempo);
    //te los filtra por ganador o perdedor...

    public List<InscriptoCompetencia> getInscriptosCompetencia(Competencia competencia);

    public List<InscriptoCompetencia> getInscriptosCompetencia(Competencia competenciaActual, Categoria categoria);

    public List<InscriptoCompetencia> getAllInscriptosCompetencia(Torneo torneoActual);

    public List<Categoria> getCategoriasEnUso(Competencia competenciaActual);

    public Competencia getCompetenciaActual(Categoria categoriaSeleccionada);

    public List<InscriptoCompetencia> getEstadoInscriptosCompetencia(Competencia competencia, EstadoInscriptoCompetenciaCarrera estado);

    public List<InscriptoCompetencia> getEstadoInscriptosCompetenciaCarrera(Competencia competenciaActual, EstadoInscriptoCompetenciaCarrera estadoInscriptoCompetenciaCarrera, Categoria categoria);

    public List<InscriptoCompetencia> getEstadoCompetencia(Competencia competencia, Categoria categoria, EstadoCompetencia estado);
}
