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
import java.util.Iterator;
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
    private Map<Integer, Integer> inscriptoWins = new HashMap<Integer, Integer>();
    //lista de categorias para el torneo
    private List<Categoria> categorias = new ArrayList<Categoria>();

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
        categorias = serviceManager.getCategoriasEnUso(competenciaActual);
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
        categorias = serviceManager.getCategoriasEnUso(competenciaActual);
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
            if (!carriles.isEmpty()) {
                inscriptoGanador = carriles.get(0).getInscriptoCompetencia();
            }
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

    public Map nuevaRonda(Categoria categoriaSeleccionada) {
        if(competenciaActual==null){
            //hay que generar una nueva
            //throw new IllegalArgumentException("no se puede armar una nueva ronda sin competencia")
            filtraCorredores(categoriaSeleccionada);
            return new HashMap();
        }
        Map nuevaRondaMap = nuevaRondaInscriptos(categoriaSeleccionada);
        List<InscriptoCompetencia> nuevosInscriptos = (List<InscriptoCompetencia>) nuevaRondaMap.get("nuevosInscriptos");
        Boolean sonFinalistas = (Boolean) nuevaRondaMap.get("sonFinalistas");
        //reiniciamos todo
        inscriptosCorriendo = new ArrayList<InscriptoCompetencia>();
        carriles = new ArrayList<Carril>();
        // por defecto es eliminatoria en una nueva ronda, excepto que se sobre escriba con la comp. actual
        int rondaActual = 1;
        TipoCompetencia tipoCompetencia = TipoCompetencia.ELIMINATORIA;
        if (competenciaActual != null) {
            rondaActual = competenciaActual.getNumeroRonda();
            tipoCompetencia = competenciaActual.getTipoCompetencia();
        }
        Map modelMap = new LinkedHashMap();
        //todo: mover esto a un lugar mas conveniente
        if (competenciaActual.getTipoCompetencia() == TipoCompetencia.FINAL && InscriptosCompetenciaHelper.getGanador(inscriptoWins) != null) {//&& rondaActual == 3) {
            Integer idGanador = InscriptosCompetenciaHelper.getGanador(inscriptoWins);
            Inscripto inscripto = serviceManager.getOneInscripto(idGanador);
            modelMap.put("ganadorCompetencia", inscripto);
            finalizaCategoria(categoriaSeleccionada);
        }
        //chequeamos si debemos cambiar la competencia a final (:
        competenciaActual = new Competencia();
        if (sonFinalistas && tipoCompetencia != TipoCompetencia.FINAL) {
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
            inscriptoCompetencia.setCompetencia(competenciaActual);//actualizo la competencia
            inscriptoCompetencia.setEstado(EstadoInscriptoCompetenciaCarrera.ESPERANDO);
            inscriptoCompetencia.generaNumero();
            System.err.println("generando nuevo id con inscripto.rondasRestantes " + inscriptoCompetencia.getRondasRestantes());
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
        inscriptosCorriendo = serviceManager.getEstadoInscriptosCompetenciaCarrera(competenciaActual, EstadoInscriptoCompetenciaCarrera.ESPERANDO, categoriaSeleccionada);
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

    private void finalizaCategoria(Categoria categoria) {
        this.carreraActual = null;
        this.competenciaActual = null;
        this.inscriptoWins = new HashMap<Integer, Integer>();
        this.inscriptosCorriendo = new ArrayList<InscriptoCompetencia>();
        this.inscriptosSeleccionados.clear();//idk
        Iterator<Categoria> it = categorias.iterator();
        while (it.hasNext()) {
            Categoria c = (Categoria) it.next();
            if (c.equals(categoria)) {
                it.remove();
            }
        }
    }

    /**
     * decide dependiendo de la competenciaActual
     * que traer de la base de datos
     * ademas devuelve si hemos llegado a la final.
     * @param competenciaActual
     * @param inscriptosCorriendo
     * @return 
     */
    private Map nuevaRondaInscriptos(Categoria categoriaSeleccionada) {
        List<InscriptoCompetencia> nuevosInscriptos = new ArrayList<InscriptoCompetencia>();
        Boolean sonFinalistas = false;
        //si es libre o estamos en la primer ronda de la eliminatoria (donde luego se dividen en dos)
        if (competenciaActual.getTipoCompetencia() == TipoCompetencia.LIBRE) {
            nuevosInscriptos = serviceManager.getInscriptosCompetencia(competenciaActual, categoriaSeleccionada);
        } else if (competenciaActual.getTipoCompetencia() == TipoCompetencia.ELIMINATORIA && competenciaActual.getNumeroRonda() == 1) {
            //traigo a todos, pero chequeo si ya estoy en la final (el puto caso si eran JUUUUSTO 2 jaja)
            nuevosInscriptos = serviceManager.getInscriptosCompetencia(competenciaActual, categoriaSeleccionada);
            if (nuevosInscriptos.size() == 2) {
                sonFinalistas = true;
            }
        }// si por el contrario estamos eliminando bichos
        // traigo dos grupos de ganadores y perdedores y evaluo
        else {
            List<InscriptoCompetencia> lineaGanadores = serviceManager.getEstadoCompetencia(competenciaActual, categoriaSeleccionada, EstadoCompetencia.COMPETENCIA_GANADORES);
            List<InscriptoCompetencia> lineaPerdedores = serviceManager.getEstadoCompetencia(competenciaActual, categoriaSeleccionada, EstadoCompetencia.COMPETENCIA_PERDEDORES);
            // si estoy tomando la actual, entonces aca tendria que resolver que hacer con c/ uno
            // si no entro en ningun otro y estoy en tipo eliminatoria entonces, delete!
            // safe use for deletition
            if (lineaGanadores.isEmpty() && lineaPerdedores.isEmpty()) {
                //we are in trouble
                throw new IllegalArgumentException("ambas lineas de ganadores y perdedores vacias, what the fuck?!");
            } else if (lineaGanadores.isEmpty() || lineaPerdedores.isEmpty()) {
                //si alguna de las dos esta vacia, chequear que no estemos en la final de los ganadores o perdedores
                nuevosInscriptos.addAll(lineaGanadores);
                nuevosInscriptos.addAll(lineaPerdedores);
                if (nuevosInscriptos.size() == 2) {
                    sonFinalistas = true;
                }
            } else if (lineaGanadores.size() == 1 && lineaPerdedores.size() == 1) {
                //ambos son finalistas, los agregamos
                sonFinalistas = true;
                nuevosInscriptos.addAll(lineaGanadores);
                nuevosInscriptos.addAll(lineaPerdedores);

            } else if (lineaGanadores.size() == 1) {
                // agregamos los perdedores
                nuevosInscriptos.addAll(lineaPerdedores);
            } else if (lineaPerdedores.size() == 1) {
                // agregamos los ganadores
                nuevosInscriptos.addAll(lineaGanadores);
            } else {
                // agregamos a toodos
                nuevosInscriptos.addAll(lineaGanadores);
                nuevosInscriptos.addAll(lineaPerdedores);
            }
        }
        Map modelMap = new HashMap();
        //chequear a quien eliminamos
        Iterator<InscriptoCompetencia> iterator = nuevosInscriptos.iterator();
        while (iterator.hasNext()) {
            InscriptoCompetencia inscriptoCompetencia = (InscriptoCompetencia) iterator.next();
            if (competenciaActual.getTipoCompetencia() == TipoCompetencia.LIBRE) {
                if (inscriptoCompetencia.getCategoria().getId() == Categoria.ID_CATEGORIA_NO_CORRE) {
                    //es de esos que no corren jeje
                    iterator.remove();
                } else {
                    inscriptoCompetencia.decrementaRondasRestantes();
                    if (inscriptoCompetencia.getRondasRestantes() == 0) {
                        // lo elimino, no tiene mas rondas!
                        iterator.remove();
                    }
                }
            } //c) comp. elim. pero ronda == 1 separamos por estado en comp.
            else if (competenciaActual.getTipoCompetencia() == TipoCompetencia.ELIMINATORIA && competenciaActual.getNumeroRonda() == 1) {
                if (inscriptoCompetencia.getEstado() != EstadoInscriptoCompetenciaCarrera.GANADOR) {
                    inscriptoCompetencia.setEstadoCompetencia(EstadoCompetencia.COMPETENCIA_GANADORES);
                } else {
                    inscriptoCompetencia.setEstadoCompetencia(EstadoCompetencia.COMPETENCIA_PERDEDORES);
                }
            } //c) si es comp. elim y nro ronda > 1 solo agregar a ganadores.
            else if (competenciaActual.getTipoCompetencia() == TipoCompetencia.ELIMINATORIA) {
                if (inscriptoCompetencia.getEstado() != EstadoInscriptoCompetenciaCarrera.GANADOR) {
                    //lo elimino, era eliminatoria :P
                    iterator.remove();
                }
            } else if (competenciaActual.getTipoCompetencia() == TipoCompetencia.FINAL) {
                //still
                //no hay mucho que hacer... solo dejar que se maten! :D
                // WII add to map winer :D
                if (inscriptoCompetencia.getEstado() == EstadoInscriptoCompetenciaCarrera.GANADOR) {
                    //chequeo si gano alguna vez :P
                    if (inscriptoWins.containsKey(inscriptoCompetencia.getInscripto().getId())) {
                        int countWins = inscriptoWins.get(inscriptoCompetencia.getInscripto().getId()) + 1;
                        inscriptoWins.put(inscriptoCompetencia.getInscripto().getId(), countWins);
                    } else {
                        inscriptoWins.put(inscriptoCompetencia.getInscripto().getId(), 1);
                    }
                }
            } else {
                throw new IllegalArgumentException("competencia desconocida... verifique argumentos!");
            }
        }

        modelMap.put("sonFinalistas", sonFinalistas);
        modelMap.put("nuevosInscriptos", nuevosInscriptos);
        return modelMap;
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
        return categorias;

    }
}
