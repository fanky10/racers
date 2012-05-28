/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package com.carreras.controllers;

import com.carreras.common.config.Configuracion;
import com.carreras.common.util.GanadorCompetenciaHelper;
import com.carreras.dominio.modelo.Carril;
import com.carreras.dominio.modelo.Categoria;
import com.carreras.dominio.modelo.Competencia;
import com.carreras.dominio.modelo.EstadoCompetencia;
import com.carreras.dominio.modelo.EstadoInscriptoCompetenciaCarrera;
import com.carreras.dominio.modelo.InscriptoCompetencia;
import com.carreras.dominio.modelo.TipoCompetencia;
import com.carreras.dominio.modelo.Torneo;
import com.carreras.servicios.ServiceManager;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

/**
 *
 * @author Fanky10 <fanky10@gmail.com>
 */
public class InscriptoCompetenciaController {
    //TODO: cuando implemente spring mvc aca van los autowired que tanto quieroouuu

    private ServiceManager serviceManager = Configuracion.getInstance().getServiceManager();
    private Map<InscriptoCompetencia,Integer> inscriptoWins = new HashMap<InscriptoCompetencia, Integer>();
    
    public Map generaNuevaRonda(Torneo torneoActual, Competencia competenciaActual, Categoria categoriaSeleccionada) {
        Map modelMap = new LinkedHashMap();
        List<InscriptoCompetencia> inscriptosCorriendo = new ArrayList<InscriptoCompetencia>();
        List<Carril> carriles = new ArrayList<Carril>();
        //traigo todos los inscriptos filtrados s/la competencia y categoria que acaba de terminar
        List<InscriptoCompetencia> nuevosInscriptos = serviceManager.getInscriptosCompetencia(competenciaActual, categoriaSeleccionada);
        //TODO: checkear el size, si es == 2 competencia final!! jojojo y ahi no se eliminan los bichos!
        int rondaActual = competenciaActual.getNumeroRonda();
        TipoCompetencia tipoCompetencia = competenciaActual.getTipoCompetencia();
        //chequeamos si debemos cambiar la competencia a final (:
        Competencia competencia = new Competencia();
        if (nuevosInscriptos.size() == 2 && tipoCompetencia == TipoCompetencia.ELIMINATORIA) {
            competencia.setNumeroRonda(1);
            competencia.setTipoCompetencia(TipoCompetencia.FINAL);
            competencia.setTorneo(torneoActual);
            competencia.setId(serviceManager.saveCompetencia(competencia));
        } else {
            competencia.setNumeroRonda(rondaActual + 1);
            competencia.setTipoCompetencia(tipoCompetencia);
            competencia.setTorneo(torneoActual);
            competencia.setId(serviceManager.saveCompetencia(competencia));
        }
        
        for (InscriptoCompetencia inscriptoCompetencia : nuevosInscriptos) {
            //hay que chequear varias cosas aca:
            //a) si es comp. libre 
            if (competencia.getTipoCompetencia() == TipoCompetencia.LIBRE) {
                if(inscriptoCompetencia.getCategoria().getId() == Categoria.ID_CATEGORIA_NO_CORRE){
                    continue; //es de esos que no corren jeje
                }else{
                    inscriptoCompetencia.decrementaRondasRestantes();
                    if (inscriptoCompetencia.getRondasRestantes() == 0) {
                        continue; //no lo agrego no le quedan rondas (:
                    }
                }
            } //c) comp. elim. pero ronda == 1 separamos por estado en comp.
            else if (competencia.getTipoCompetencia() == TipoCompetencia.ELIMINATORIA && rondaActual == 1) {
                if (inscriptoCompetencia.getEstado() != EstadoInscriptoCompetenciaCarrera.GANADOR) {
                    inscriptoCompetencia.setEstadoCompetencia(EstadoCompetencia.COMPETENCIA_GANADORES);
                } else {
                    inscriptoCompetencia.setEstadoCompetencia(EstadoCompetencia.COMPETENCIA_PERDEDORES);
                }
            } //c) si es comp. elim y nro ronda > 1 solo agregar a ganadores.
            else if (competencia.getTipoCompetencia() == TipoCompetencia.ELIMINATORIA) {
                if (inscriptoCompetencia.getEstado() != EstadoInscriptoCompetenciaCarrera.GANADOR) {
                    continue; //buscamos el proximo
                }
            } else if (competencia.getTipoCompetencia() == TipoCompetencia.FINAL && rondaActual == 3) {
                //que se supone que queremos?
                modelMap.put("ganadorCompetencia", GanadorCompetenciaHelper.getGanador(inscriptoWins));
            } else if (competencia.getTipoCompetencia() == TipoCompetencia.FINAL) {
                //still
                //no hay mucho que hacer... solo dejar que se maten! :D
                // WII add to map winer :D
                if(inscriptoCompetencia.getEstado() == EstadoInscriptoCompetenciaCarrera.GANADOR){
                    //chequeo si gano alguna vez :P
                    if(inscriptoWins.containsKey(inscriptoCompetencia)){
                        int countWins = inscriptoWins.get(inscriptoCompetencia) + 1;
                        inscriptoWins.put(inscriptoCompetencia, countWins);
                    }else{
                        inscriptoWins.put(inscriptoCompetencia, 1);
                    }
                }
            } else {
                throw new IllegalArgumentException("competencia desconocida... verifique argumentos!");
            }
            inscriptoCompetencia.setCompetencia(competencia);//actualizo la competencia
            inscriptoCompetencia.setEstado(EstadoInscriptoCompetenciaCarrera.ESPERANDO);
            inscriptoCompetencia.generaNumero();
            System.err.println("generando nuevo id con inscripto.rondas_restantes "+inscriptoCompetencia.getRondasRestantes());
            inscriptoCompetencia.setId(serviceManager.saveInscriptoCompetencia(inscriptoCompetencia));
            inscriptosCorriendo.add(inscriptoCompetencia);
            //TODO: descomentar esto!
//            agregaCarril(inscriptoCompetencia);
        }

        
        modelMap.put("inscriptos", inscriptosCorriendo);
        modelMap.put("carriles", carriles);
        modelMap.put("competencia", competencia);
        
        return modelMap;
    }
    
    public Map finalizaInscripcion(Torneo torneo, Competencia competenciaActual,List<InscriptoCompetencia> inscriptos) {
        List<InscriptoCompetencia> inscriptosCorriendo = new ArrayList<InscriptoCompetencia>();
        List<Carril> carriles = new ArrayList<Carril>();
        
        competenciaActual = new Competencia();
        competenciaActual.setNumeroRonda(1);
        competenciaActual.setTipoCompetencia(TipoCompetencia.ELIMINATORIA);
        competenciaActual.setTorneo(torneo);
        competenciaActual.setId(serviceManager.saveCompetencia(competenciaActual));
        
        for (InscriptoCompetencia inscriptoCompetencia : inscriptos) {
            if(inscriptoCompetencia.getCategoria().getId() == Categoria.ID_CATEGORIA_NO_CORRE){
                continue ;
            }
            inscriptoCompetencia.setCompetencia(competenciaActual);//actualizo la competencia
            inscriptoCompetencia.setEstado(EstadoInscriptoCompetenciaCarrera.ESPERANDO);
            inscriptoCompetencia.generaNumero();
            inscriptoCompetencia.setId(serviceManager.saveInscriptoCompetencia(inscriptoCompetencia));
            inscriptosCorriendo.add(inscriptoCompetencia);
            //TODO: descomentar esto!
//            agregaCarril(inscriptoCompetencia);

        }
        Map modelMap = new LinkedHashMap();
        modelMap.put("inscriptos", inscriptosCorriendo);
        modelMap.put("carriles", carriles);
        modelMap.put("competencia", competenciaActual);
        return modelMap;
    }


    public ServiceManager getServiceManager() {
        return serviceManager;
    }

    public void setServiceManager(ServiceManager serviceManager) {
        this.serviceManager = serviceManager;
    }
}
