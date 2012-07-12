/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package com.carreras.servicios.impl;

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
import com.carreras.dominio.modelo.TipoCompetencia;
import com.carreras.dominio.modelo.TipoTiempo;
import com.carreras.dominio.modelo.Torneo;
import com.carreras.servicios.AutoManager;
import com.carreras.servicios.CarreraManager;
import com.carreras.servicios.CarrilManager;
import com.carreras.servicios.CategoriaManager;
import com.carreras.servicios.CompetenciaManager;
import com.carreras.servicios.CorredorManager;
import com.carreras.servicios.InscriptoCompetenciaManager;
import com.carreras.servicios.InscriptoManager;
import com.carreras.servicios.ServiceManager;
import com.carreras.servicios.TiempoManager;
import com.carreras.servicios.TipoTiempoManager;
import com.carreras.servicios.TorneoManager;
import java.util.ArrayList;
import java.util.List;

/**
 *
 * @author Fanky10 <fanky10@gmail.com>
 */
public class ServiceManagerImpl implements ServiceManager {
    //managers! :D
    //instanciados segun la implementacion que uno guste (:

    private AutoManager autoManager = new AutoManagerImpl();
    private CategoriaManager categoriaManager = new CategoriaManagerImpl();
    private CorredorManager corredorManager = new CorredorManagerImpl();
    private CarreraManager carreraManager = new CarreraManagerImpl();
    private CarrilManager carrilManager = new CarrilManagerImpl();
    private InscriptoManager inscriptoManager = new InscriptoManagerImpl();
    private TorneoManager torneoManager = new TorneoManagerImpl();
    private CompetenciaManager competenciaManager = new CompetenciaManagerImpl();
    private InscriptoCompetenciaManager inscriptoCompetenciaManager = new InscriptoCompetenciaManagerImpl();
    private TiempoManager tiempoManager = new TiempoManagerImpl();
    private TipoTiempoManager tipoTiempoManager = new TipoTiempoManagerImpl();

    public ServiceManagerImpl() {
    }

    @Override
    public Integer saveAuto(Auto auto) {
        return autoManager.guardar(auto);
    }

    @Override
    public Integer saveCorredor(Corredor corredor) {
        return corredorManager.guardar(corredor);
    }

    @Override
    public Integer saveInscripto(Inscripto inscripto) {
        return inscriptoManager.save(inscripto);
    }

    @Override
    public Integer saveInscriptoCompetencia(InscriptoCompetencia inscriptoCompetencia) {
        return inscriptoCompetenciaManager.save(inscriptoCompetencia);
    }

    @Override
    public Integer saveTorneo(Torneo torneo) {
        return torneoManager.save(torneo);
    }

    @Override
    public Integer saveCompetencia(Competencia competencia) {
        return competenciaManager.save(competencia);
    }

    @Override
    public List<Inscripto> getAllInscriptos() {
        return inscriptoManager.getAll();
    }

    @Override
    public List<Inscripto> getAllInscriptosBut(List<InscriptoCompetencia> inscriptosUsados) {
        List<Integer> ids = new ArrayList<Integer>();
        for (InscriptoCompetencia ic : inscriptosUsados) {
            ids.add(ic.getInscripto().getId());
        }
        return inscriptoManager.getAllInscriptosBut((Integer[]) ids.toArray(new Integer[0]));
    }

    @Override
    public Categoria getCategoria(Integer id) {
        return categoriaManager.getOne(id);
    }

    @Override
    public Integer saveCarrera(Carrera carrera) {
        return carreraManager.save(carrera);
    }

    @Override
    public Integer saveCarril(Carril carril) {
        return carrilManager.save(carril);
    }

    @Override
    public Integer saveTiempo(Tiempo tiempo) {
        return tiempoManager.save(tiempo);
    }

    @Override
    public TipoTiempo getTipoTiempo(Integer id) {
        return tipoTiempoManager.getOne(id);
    }

    @Override
    public List<Tiempo> getTiemposCarril(Carril carril) {
        return tiempoManager.getTiemposCarril(carril.getId());
    }

    @Override
    public Tiempo getTiempo(Carril carril, TipoTiempo tipoTiempo) {
        return tiempoManager.getTiempo(carril.getId(), tipoTiempo.getId());
    }

    /**
     * ahora se usa, el getCompetencia(compActual, categoriaSeleccionada)
     * @param competencia
     * @return
     * @deprecated
     */
    @Deprecated
    @Override
    public List<InscriptoCompetencia> getInscriptosCompetencia(Competencia competencia) {

        if (competencia.getTipoCompetencia() == TipoCompetencia.LIBRE) {
            return inscriptoCompetenciaManager.getInscriptosCompetenciaLibre(competencia.getId());
        } else if (competencia.getTipoCompetencia() == TipoCompetencia.ELIMINATORIA) {
            List<InscriptoCompetencia> inscriptos = inscriptoCompetenciaManager.getEstadoInscriptosCompetencia(competencia.getId(), EstadoInscriptoCompetenciaCarrera.GANADOR);
            return inscriptos;
        }
        //TODO: traer a todos (:
//        else if(competencia.getTipoCompetencia() == TipoCompetencia.FINAL){
//            //TODO: check nro.ronda o algo asi
//            return new ArrayList<InscriptoCompetencia>();
//        }

        return new ArrayList<InscriptoCompetencia>();
    }

    @Override
    public void updateInscriptoCompetencia(InscriptoCompetencia inscriptoCompetencia) {
        inscriptoCompetenciaManager.update(inscriptoCompetencia);
    }

    @Override
    public Categoria getCategoria(Torneo torneo, Inscripto inscripto) {
        Tiempo tiempo = tiempoManager.getMejorTiempo(torneo.getId(), inscripto.getId());
        if (tiempo == null) {
            //no participo
            return categoriaManager.getOne(Categoria.ID_CATEGORIA_NO_CORRE);
        }
        return categoriaManager.getCategoria(tiempo.getTiempo());
    }

    @Override
    public List<Categoria> getAllCategorias() {
        return categoriaManager.getAll();
    }

    @Override
    public List<InscriptoCompetencia> getInscriptosCompetencia(Competencia competencia, Categoria categoria) {
        return inscriptoCompetenciaManager.getInscriptosCompetencia(competencia.getId(), categoria.getId());
    }

    @Override
    public List<Categoria> getCategoriasEnUso(Competencia competenciaActual) {
        return inscriptoCompetenciaManager.getCategoriasEnUso(competenciaActual.getId());
    }

    @Override
    public List<InscriptoCompetencia> getAllInscriptosCompetencia(Torneo torneoActual) {
        return inscriptoCompetenciaManager.getAllTorneo(torneoActual.getId());
    }

    @Override
    public Competencia getCompetenciaActual(Categoria categoriaSeleccionada) {
        return competenciaManager.getCompetenciaActual(categoriaSeleccionada.getId());
    }

    @Override
    public List<InscriptoCompetencia> getEstadoInscriptosCompetencia(Competencia competencia, EstadoInscriptoCompetenciaCarrera estado) {
        return inscriptoCompetenciaManager.getEstadoInscriptosCompetencia(competencia.getId(), estado);
    }

    @Override
    public List<InscriptoCompetencia> getEstadoInscriptosCompetenciaCarrera(Competencia competencia, EstadoInscriptoCompetenciaCarrera estado, Categoria categoria) {
        return inscriptoCompetenciaManager.getEstadoInscriptosCompetenciaCarrera(competencia.getId(), estado, categoria.getId());
    }

    @Override
    public List<InscriptoCompetencia> getEstadoCompetencia(Competencia competencia, Categoria categoria, EstadoCompetencia estado) {
        return inscriptoCompetenciaManager.getEstadoCompetencia(competencia.getId(), categoria.getId(), estado);
    }

    @Override
    public Integer saveCategoria(Categoria categoria) {
        return categoriaManager.save(categoria);
    }

    @Override
    public Inscripto getOneInscripto(Integer id) {
        return inscriptoManager.getOne(id);
    }
}
