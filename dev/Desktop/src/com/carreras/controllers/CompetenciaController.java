/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package com.carreras.controllers;

import arduino.entidades.Adelantamiento;
import arduino.entidades.Datos;
import arduino.entidades.Rotura;
import arduino.entidades.Tiempos;
import arduino.eventos.RespuestaEvent;
import com.carreras.common.config.Configuracion;
import com.carreras.common.logger.CarrerasLogger;
import com.carreras.common.util.InscriptosCompetenciaHelper;
import com.carreras.dominio.modelo.Carrera;
import com.carreras.dominio.modelo.Carril;
import com.carreras.dominio.modelo.Categoria;
import com.carreras.dominio.modelo.Competencia;
import com.carreras.dominio.modelo.EstadoCompetencia;
import com.carreras.dominio.modelo.EstadoInscriptoCompetenciaCarrera;
import com.carreras.dominio.modelo.Inscripto;
import com.carreras.dominio.modelo.InscriptoCompetencia;
import com.carreras.dominio.modelo.Tiempo;
import com.carreras.dominio.modelo.TipoCompetencia;
import com.carreras.dominio.modelo.TipoTiempo;
import com.carreras.dominio.modelo.Torneo;
import com.carreras.servicios.ServiceManager;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.Date;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

/**
 *
 * @author Fanky10 <fanky10@gmail.com>
 * controla toda la evolucion de una competencia.
 * se crea un nuevo controlador por competencia 
 */
public class CompetenciaController {

    private ServiceManager serviceManager = Configuracion.getInstance().getServiceManager();
    //es una unica instancia para toda la competencia
    private Torneo torneo;
    //esta instancia ira cambiando en el tiempo (:
    private Competencia competenciaActual;
    //inscriptos que estan actualmente en uso
    private List<InscriptoCompetencia> inscriptosCorriendo = new ArrayList<InscriptoCompetencia>();
    //son aquellos que ya fueron seleccionados y que no necesariamente estan corriendo
    //seleccionados.size() >= corriendo.size()
    private List<InscriptoCompetencia> inscriptosSeleccionados = new ArrayList<InscriptoCompetencia>();
    //carrera actual
    private Carrera carreraActual;
    //carriles disponibles
    private List<com.carreras.dominio.modelo.Carril> carriles = new ArrayList<com.carreras.dominio.modelo.Carril>();
    //cant de veces que va ganando cada finalista
    private Map<InscriptoCompetencia,Integer> inscriptoWins = new HashMap<InscriptoCompetencia, Integer>();
    
    public void iniciaTorneo() {
        torneo = new Torneo();
        torneo.setFechaHora(new Date(System.currentTimeMillis()));
        torneo.setId(serviceManager.saveTorneo(torneo));
        competenciaActual = new Competencia();
        competenciaActual.setNumeroRonda(1);
        competenciaActual.setTipoCompetencia(TipoCompetencia.LIBRE);
        competenciaActual.setTorneo(torneo);
        competenciaActual.setId(serviceManager.saveCompetencia(competenciaActual));
    }

    public void agregaNuevoInscripto(Inscripto inscripto, Integer nroRondas) {
        InscriptoCompetencia inscriptoCompetencia = new InscriptoCompetencia();
        inscriptoCompetencia.setCategoria(serviceManager.getCategoria(Categoria.ID_CATEGORIA_LIBRE));
        inscriptoCompetencia.setRondasRestantes(nroRondas);
        inscriptoCompetencia.setCompetencia(competenciaActual);
        inscriptoCompetencia.setEstadoCompetencia(EstadoCompetencia.COMPETENCIA_NO_DEFINIDA);
        inscriptoCompetencia.setInscripto(inscripto);
        inscriptoCompetencia.generaNumero();
        if (nroRondas > 0) {
            inscriptoCompetencia.setEstado(EstadoInscriptoCompetenciaCarrera.ESPERANDO);
        } else {
            inscriptoCompetencia.setEstado(EstadoInscriptoCompetenciaCarrera.NO_CORRE);
        }
        inscriptoCompetencia.setId(serviceManager.saveInscriptoCompetencia(inscriptoCompetencia));
        //save and add to inscriptosSeleccionados (:
        inscriptosSeleccionados.add(inscriptoCompetencia);
        if (nroRondas > 0) {
            inscriptosCorriendo.add(inscriptoCompetencia);
            agregaCarril(inscriptoCompetencia);
        }//si no participa ni me gasto (:
    }

    /**
     * agrega carril s/sea necesario
     * @return true carril agregado, false otherwise
     */
    private boolean agregaCarril(InscriptoCompetencia inscriptoCompetencia) {
        if (carreraActual == null) {
            carreraActual = new Carrera();
            carreraActual.setId(serviceManager.saveCarrera(carreraActual));
        }
        //se agrega el carril en cuestion
        //averiguo primero si no es el ganador.
        Integer sizeInscriptosCategoria = serviceManager.getInscriptosCompetencia(competenciaActual, inscriptoCompetencia.getCategoria()).size();
        if (carriles.isEmpty()) {
            //agrego el primer carril
            com.carreras.dominio.modelo.Carril carril = new com.carreras.dominio.modelo.Carril();
            carril.setCarrera(carreraActual);
            carril.setInscriptoCompetencia(inscriptoCompetencia);
            carril.setNumero(carriles.size() + 1);
            carril.setId(serviceManager.saveCarril(carril));
            carriles.add(carril);
            inscriptoCompetencia.setEstado(EstadoInscriptoCompetenciaCarrera.CORRIENDO);

            return true;
        }//agrego los demas carriles, si hay carriles disponibles
        else if (carriles.size() < Configuracion.getInstance().getCant_carriles()
                //y son de la misma categoria y competencia
                && carriles.get(carriles.size() - 1).getInscriptoCompetencia().getCategoria().getId().equals(inscriptoCompetencia.getCategoria().getId())
                && carriles.get(carriles.size() - 1).getInscriptoCompetencia().getEstadoCompetencia().getId().equals(inscriptoCompetencia.getEstadoCompetencia().getId())) {
            com.carreras.dominio.modelo.Carril carril = new com.carreras.dominio.modelo.Carril();
            carril.setCarrera(carreraActual);
            carril.setInscriptoCompetencia(inscriptoCompetencia);
            carril.setNumero(carriles.size() + 1);
            carril.setId(serviceManager.saveCarril(carril));
            carriles.add(carril);
            inscriptoCompetencia.setEstado(EstadoInscriptoCompetenciaCarrera.CORRIENDO);

            return true;
        } else if (carriles.size() == 1
                //y son de la misma categoria pero distinto estado inicial de comp.
                && carriles.get(carriles.size() - 1).getInscriptoCompetencia().getCategoria().getId().equals(inscriptoCompetencia.getCategoria().getId())
                && !carriles.get(carriles.size() - 1).getInscriptoCompetencia().getEstadoCompetencia().getId().equals(inscriptoCompetencia.getEstadoCompetencia().getId())
                //chequeo que sean los ultimos dos
                && sizeInscriptosCategoria == 2) {
            com.carreras.dominio.modelo.Carril carril = new com.carreras.dominio.modelo.Carril();
            carril.setCarrera(carreraActual);
            carril.setInscriptoCompetencia(inscriptoCompetencia);
            carril.setNumero(carriles.size() + 1);
            carril.setId(serviceManager.saveCarril(carril));
            carriles.add(carril);
            inscriptoCompetencia.setEstado(EstadoInscriptoCompetenciaCarrera.CORRIENDO);

            return true;
        }
        return false;
    }

    public void finalizaInscripcion(List<InscriptoCompetencia> inscriptos) {
        inscriptosCorriendo = new ArrayList<InscriptoCompetencia>();
        carriles = new ArrayList<Carril>();

        competenciaActual = new Competencia();
        competenciaActual.setNumeroRonda(1);
        competenciaActual.setTipoCompetencia(TipoCompetencia.ELIMINATORIA);
        competenciaActual.setTorneo(torneo);
        competenciaActual.setId(serviceManager.saveCompetencia(competenciaActual));

        for (InscriptoCompetencia inscriptoCompetencia : inscriptos) {
            if (inscriptoCompetencia.getCategoria().getId() == Categoria.ID_CATEGORIA_NO_CORRE) {
                continue;
            }
            inscriptoCompetencia.setCompetencia(competenciaActual);//actualizo la competencia
            inscriptoCompetencia.setEstado(EstadoInscriptoCompetenciaCarrera.ESPERANDO);
            inscriptoCompetencia.generaNumero();
            inscriptoCompetencia.setId(serviceManager.saveInscriptoCompetencia(inscriptoCompetencia));
            inscriptosCorriendo.add(inscriptoCompetencia);
            agregaCarril(inscriptoCompetencia);
        }
        if (inscriptosCorriendo.isEmpty()) {
            throw new IllegalArgumentException("al generar finalizar la competencia libre no pueden quedarse sin inscriptos!!");
        }
    }

    public Map recargaTiempos() {
        Map<Carril, List<com.carreras.dominio.modelo.Tiempo>> carrilTiempos = new LinkedHashMap<Carril, List<Tiempo>>();
        InscriptoCompetencia inscriptoGanador = null;
        Boolean isCarreraOver = true;
        for (Carril carrilModelo : carriles) {
            List<com.carreras.dominio.modelo.Tiempo> tiemposModel = serviceManager.getTiemposCarril(carrilModelo);
            carrilTiempos.put(carrilModelo, tiemposModel);
            if (tiemposModel.isEmpty()) {
                //si al menos hay uno sin tiempos, la carrera NO termino!
                isCarreraOver = false;
            }
        }
        if (isCarreraOver) {
            final TipoTiempo decisorio = serviceManager.getTipoTiempo(TipoTiempo.ID_TIEMPO_FIN);
            //db consistance checker
            if (decisorio == null) {
                throw new IllegalArgumentException("bad configuration tipoTiempos, idTiempoFin desconocido: " + TipoTiempo.ID_TIEMPO_FIN);
            }
            Collections.sort(carriles, new Comparator<Carril>() {

                @Override
                public int compare(Carril t1, Carril t2) {

                    com.carreras.dominio.modelo.Tiempo tCarril1 = serviceManager.getTiempo(t1, decisorio);
                    com.carreras.dominio.modelo.Tiempo tCarril2 = serviceManager.getTiempo(t2, decisorio);

                    return tCarril1.getTiempo().compareTo(tCarril2.getTiempo());

                }
            });

            //chequeamos una caida de tiempo, de darse eso, el siguiente con menor tiempo es ganador.
            chequeaCaidaTiempo(carriles);
            int i = 0;
            for (Carril cc : carriles) {
                //TODO: check el tiempo que saco (:
                //una vez ordenados, el primero --> break! naa chequear el maximo 
                if (i == 0 && cc.getInscriptoCompetencia().getEstado() == EstadoInscriptoCompetenciaCarrera.CORRIENDO) {
                    cc.getInscriptoCompetencia().setEstado(EstadoInscriptoCompetenciaCarrera.GANADOR);

                }//no es el primer por comparacion de tiempos, y esta corriendo (no tiene asignado estado) 
                else if (cc.getInscriptoCompetencia().getEstado() == EstadoInscriptoCompetenciaCarrera.CORRIENDO) {
                    cc.getInscriptoCompetencia().setEstado(EstadoInscriptoCompetenciaCarrera.PERDEDOR);
                }
                //actualizo su estado final en la db
                //si se quiere el estado puede cambiar en el tiempo y se hace un save con el tstamp
                serviceManager.updateInscriptoCompetencia(cc.getInscriptoCompetencia());
                i++;
            }
            //TODO: check if this is winner
            inscriptoGanador = carriles.get(0).getInscriptoCompetencia();
        }

        Map modelMap = new HashMap();
        modelMap.put("carrilTiempos", carrilTiempos);
        modelMap.put("isCarreraOver", isCarreraOver);
        modelMap.put("inscriptoGanador", inscriptoGanador);
        return modelMap;
    }

    private void chequeaCaidaTiempo(List<Carril> carriles) {
        final TipoTiempo decisorio = serviceManager.getTipoTiempo(TipoTiempo.ID_TIEMPO_FIN);
        for (Carril c : carriles) {
            com.carreras.dominio.modelo.Tiempo tCarril1 = serviceManager.getTiempo(c, decisorio);
            if (tCarril1.getTiempo() < c.getInscriptoCompetencia().getCategoria().getTiempoMaximo()) {
                //listo, se cayo el pobrecin!
                c.getInscriptoCompetencia().setEstado(EstadoInscriptoCompetenciaCarrera.CAIDO_CATEGORIA);
            }
        }
    }

    public Map proximaCarrera() {
        //reinicio att.
        carreraActual = null;
        carriles = new ArrayList<Carril>();

        for (InscriptoCompetencia ic : inscriptosCorriendo) {
            if (ic.getEstado() == EstadoInscriptoCompetenciaCarrera.ESPERANDO && !agregaCarril(ic)) {
                break;
            }
        }
        Boolean finCarreras = carriles.isEmpty();
        Map modelMap = new HashMap();
        modelMap.put("finCarreras", finCarreras);
        return modelMap;
    }
    public Map nuevaRonda(Categoria categoriaSeleccionada){
        Map modelMap = new LinkedHashMap();
        inscriptosCorriendo = new ArrayList<InscriptoCompetencia>();
        carriles = new ArrayList<Carril>();
        //traigo todos los inscriptos filtrados s/la competencia y categoria que acaba de terminar
        List<InscriptoCompetencia> nuevosInscriptos = serviceManager.getInscriptosCompetencia(competenciaActual, categoriaSeleccionada);
        //usamos un helper que nos ayude a eliminar el que finalista de la categoria
        //TODO: generar un buen Helper.
        // nuevosInscriptos = InscriptosCompetenciaHelper.getInscriptosValidos(nuevosInscriptos);
        //TODO: checkear el size, si es == 2 competencia final!! jojojo y ahi no se eliminan los bichos!
        int rondaActual = competenciaActual.getNumeroRonda();
        TipoCompetencia tipoCompetencia = competenciaActual.getTipoCompetencia();
        //chequeamos si debemos cambiar la competencia a final (:
        competenciaActual = new Competencia();
        if (nuevosInscriptos.size() == 2 && tipoCompetencia == TipoCompetencia.ELIMINATORIA) {
            competenciaActual.setNumeroRonda(1);
            competenciaActual.setTipoCompetencia(TipoCompetencia.FINAL);
            competenciaActual.setTorneo(torneo);
            competenciaActual.setId(serviceManager.saveCompetencia(competenciaActual));
        } else {
            competenciaActual.setNumeroRonda(rondaActual + 1);
            competenciaActual.setTipoCompetencia(tipoCompetencia);
            competenciaActual.setTorneo(torneo);
            competenciaActual.setId(serviceManager.saveCompetencia(competenciaActual));
        }
        
        for (InscriptoCompetencia inscriptoCompetencia : nuevosInscriptos) {
            //hay que chequear varias cosas aca:
            //a) si es comp. libre 
            if (competenciaActual.getTipoCompetencia() == TipoCompetencia.LIBRE) {
                if(inscriptoCompetencia.getCategoria().getId() == Categoria.ID_CATEGORIA_NO_CORRE){
                    continue; //es de esos que no corren jeje
                }else{
                    inscriptoCompetencia.decrementaRondasRestantes();
                    if (inscriptoCompetencia.getRondasRestantes() == 0) {
                        continue; //no lo agrego no le quedan rondas (:
                    }
                }
            } //c) comp. elim. pero ronda == 1 separamos por estado en comp.
            else if (competenciaActual.getTipoCompetencia() == TipoCompetencia.ELIMINATORIA && rondaActual == 1) {
                if (inscriptoCompetencia.getEstado() != EstadoInscriptoCompetenciaCarrera.GANADOR) {
                    inscriptoCompetencia.setEstadoCompetencia(EstadoCompetencia.COMPETENCIA_GANADORES);
                } else {
                    inscriptoCompetencia.setEstadoCompetencia(EstadoCompetencia.COMPETENCIA_PERDEDORES);
                }
            } //c) si es comp. elim y nro ronda > 1 solo agregar a ganadores.
            else if (competenciaActual.getTipoCompetencia() == TipoCompetencia.ELIMINATORIA) {
                if (inscriptoCompetencia.getEstado() != EstadoInscriptoCompetenciaCarrera.GANADOR) {
                    continue; //buscamos el proximo
                }
            } else if (competenciaActual.getTipoCompetencia() == TipoCompetencia.FINAL && rondaActual == 3) {
                //que se supone que queremos?
                modelMap.put("ganadorCompetencia", InscriptosCompetenciaHelper.getGanador(inscriptoWins));
            } else if (competenciaActual.getTipoCompetencia() == TipoCompetencia.FINAL) {
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
            inscriptoCompetencia.setCompetencia(competenciaActual);//actualizo la competencia
            inscriptoCompetencia.setEstado(EstadoInscriptoCompetenciaCarrera.ESPERANDO);
            inscriptoCompetencia.generaNumero();
            System.err.println("generando nuevo id con inscripto.rondasRestantes "+inscriptoCompetencia.getRondasRestantes());
            inscriptoCompetencia.setId(serviceManager.saveInscriptoCompetencia(inscriptoCompetencia));
            inscriptosCorriendo.add(inscriptoCompetencia);
            agregaCarril(inscriptoCompetencia);
        }
        
        return modelMap;
    }

    public Map filtraCorredores(Categoria categoriaSeleccionada) {
        //override la competencia actual, por la ultima para la categoria seleccionada y luego, traer a los que estan juegando
        //tener en cuenta que hace dentro el manager (:
        //System.err.println("proxima carrera... [competencia]");
        competenciaActual = serviceManager.getCompetenciaActual(categoriaSeleccionada);
        inscriptosCorriendo = serviceManager.getEstadoInscriptosCompetencia(competenciaActual, EstadoInscriptoCompetenciaCarrera.ESPERANDO, categoriaSeleccionada);
        // es mas o menos la misma logica de prox. carrera.. 
        return proximaCarrera();
    }

    public void eventoCarril(RespuestaEvent respuestaEvent) {
        if (respuestaEvent.getDatos().getTipo() == Datos.CARRIL) {
            tiemposCarril(respuestaEvent);
        } else if (respuestaEvent.getDatos().getTipo() == Datos.ADELANTAMIENTO) {
            CarrerasLogger.info(CompetenciaController.class, "hubo adelantamiento");
            adelantamientoCarril(respuestaEvent);
        } else if (respuestaEvent.getDatos().getTipo() == Datos.ROTURA) {
            CarrerasLogger.info(CompetenciaController.class, "hubo roturas que");
            roturaAuto(respuestaEvent);
        }
    }

    private void tiemposCarril(final RespuestaEvent respuestaEvent) {
        try {
            arduino.entidades.Carril carrilDatos = (arduino.entidades.Carril) respuestaEvent.getDatos();
            CarrerasLogger.info(CompetenciaController.class, "actualizando tiempos de carril: " + carrilDatos.getNro_carril());
            for (com.carreras.dominio.modelo.Carril carril : carriles) {
                if (carril.getNumero() == carrilDatos.getNro_carril()) {
                    //debemos actualizar los tiempos en la db (:
                    Tiempos tiemposDatos = carrilDatos.getTiempoV();
                    for (arduino.entidades.Tiempo t : tiemposDatos) {
                        double dTiempo = t.getTiempo();
                        int tipoTiempo = t.getTipo_tiempo();
                        TipoTiempo tipoTiempoModel = serviceManager.getTipoTiempo(tipoTiempo);
                        com.carreras.dominio.modelo.Tiempo tiempoModel = new com.carreras.dominio.modelo.Tiempo();
                        tiempoModel.setCarril(carril);
                        tiempoModel.setTiempo((float) dTiempo);
                        tiempoModel.setTipoTiempo(tipoTiempoModel);
                        tiempoModel.setId(serviceManager.saveTiempo(tiempoModel));


                    }
                }
            }
        } catch (IllegalArgumentException ex) {
            //ignorada, me llego datos de un carril que nada que ver xF
        }
    }

    private void adelantamientoCarril(final RespuestaEvent respuesta_event) {
        try {

            Adelantamiento adelantamiento = (Adelantamiento) respuesta_event.getDatos();
            for (com.carreras.dominio.modelo.Carril carril : carriles) {
                if (carril.getNumero() == adelantamiento.getNro_carril()) {
                    carril.getInscriptoCompetencia().setEstado(EstadoInscriptoCompetenciaCarrera.ADELANTADO);
                    serviceManager.saveInscriptoCompetencia(carril.getInscriptoCompetencia());
                }
            }
        } catch (IllegalArgumentException ex) {
            //ignorada, me llego datos de un carril que nada que ver xF
        }

    }

    private void roturaAuto(final RespuestaEvent respuesta_event) {
        try {
            Rotura rotura = (Rotura) respuesta_event.getDatos();
            for (com.carreras.dominio.modelo.Carril carril : carriles) {
                if (carril.getNumero() == rotura.getNro_carril()) {
                    carril.getInscriptoCompetencia().setEstado(EstadoInscriptoCompetenciaCarrera.ROTO);
                    serviceManager.saveInscriptoCompetencia(carril.getInscriptoCompetencia());
                }
            }
        } catch (IllegalArgumentException ex) {
            //ignorada, me llego datos de un carril que nada que ver xF
        }
    }

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
        return serviceManager.getCategoriasEnUso(competenciaActual);
    }
}
