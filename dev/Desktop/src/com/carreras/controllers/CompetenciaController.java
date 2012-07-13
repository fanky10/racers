/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package com.carreras.controllers;



import arduino.eventos.RespuestaEvent;
import com.carreras.common.config.Configuracion;
import com.carreras.dominio.modelo.Carrera;
import com.carreras.dominio.modelo.Carril;
import com.carreras.dominio.modelo.Categoria;
import com.carreras.dominio.modelo.Competencia;
import com.carreras.dominio.modelo.Inscripto;
import com.carreras.dominio.modelo.InscriptoCompetencia;
import com.carreras.dominio.modelo.Torneo;
import com.carreras.servicios.ServiceManager;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 *
 * @author Fanky10 <fanky10@gmail.com>
 * controla toda la evolucion de una competencia.
 * se crea un nuevo controlador por competencia 
 */
public abstract class CompetenciaController {
    protected ServiceManager serviceManager = Configuracion.getInstance().getServiceManager();
    //es una unica instancia para toda la competencia
    protected Torneo torneo;
    //esta instancia ira cambiando en el tiempo (:
    protected Competencia competenciaActual;
    //inscriptos que estan actualmente en uso
    protected List<InscriptoCompetencia> inscriptosCorriendo = new ArrayList<InscriptoCompetencia>();
    //son aquellos que ya fueron seleccionados y que no necesariamente estan corriendo
    //seleccionados.size() >= corriendo.size()
    protected List<InscriptoCompetencia> inscriptosSeleccionados = new ArrayList<InscriptoCompetencia>();
    //carrera actual
    protected Carrera carreraActual;
    //carriles disponibles
    protected List<Carril> carriles = new ArrayList<Carril>();
    //cant de veces que va ganando cada finalista
    protected Map<Integer, Integer> inscriptoWins = new HashMap<Integer, Integer>();
    //lista de categorias para el torneo
    protected List<Categoria> categorias = new ArrayList<Categoria>();
    
    public abstract void iniciaTorneo();

    public abstract void agregaNuevoInscripto(Inscripto inscripto, Integer nroRondas);

    public abstract void finalizaInscripcion(List<InscriptoCompetencia> inscriptos);

    public abstract Map recargaTiempos();

    public abstract Map proximaCarrera();

    public abstract Map nuevaRonda(Categoria categoriaSeleccionada);

    public abstract Map filtraCorredores(Categoria categoriaSeleccionada);

    public abstract void eventoCarril(RespuestaEvent respuestaEvent);
    

    public Competencia getCompetenciaActual() {
        return competenciaActual;
    }

    public void setCompetenciaActual(Competencia competenciaActual) {
        this.competenciaActual = competenciaActual;
    }

    public List<InscriptoCompetencia> getInscriptosCorriendo() {
        return inscriptosCorriendo;
    }

    public void setInscriptosCorriendo(List<InscriptoCompetencia> inscriptosCorriendo) {
        this.inscriptosCorriendo = inscriptosCorriendo;
    }

    public List<InscriptoCompetencia> getInscriptosSeleccionados() {
        return inscriptosSeleccionados;
    }

    public void setInscriptosSeleccionados(List<InscriptoCompetencia> inscriptosSeleccionados) {
        this.inscriptosSeleccionados = inscriptosSeleccionados;
    }

    public ServiceManager getServiceManager() {
        return serviceManager;
    }

    public void setServiceManager(ServiceManager serviceManager) {
        this.serviceManager = serviceManager;
    }

    public Torneo getTorneo() {
        return torneo;
    }

    public void setTorneo(Torneo torneo) {
        this.torneo = torneo;
    }

    public Carrera getCarreraActual() {
        return carreraActual;
    }

    public void setCarreraActual(Carrera carreraActual) {
        this.carreraActual = carreraActual;
    }

    public List<Carril> getCarriles() {
        return carriles;
    }

    public void setCarriles(List<Carril> carriles) {
        this.carriles = carriles;
    }

    public List<Categoria> getCategorias() {
        return categorias;

    }
}