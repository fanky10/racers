/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package com.carreras.controllers;

import arduino.entidades.Adelantamiento;
import arduino.entidades.Datos;
import arduino.entidades.Rotura;
import arduino.entidades.Tiempo;
import arduino.entidades.Tiempos;
import arduino.eventos.RespuestaEvent;
import com.carreras.common.config.Configuracion;
import com.carreras.common.logger.CarrerasLogger;
import com.carreras.dominio.modelo.Carril;
import com.carreras.dominio.modelo.EstadoInscriptoCompetenciaCarrera;
import com.carreras.dominio.modelo.InscriptoCompetencia;
import com.carreras.dominio.modelo.TipoTiempo;
import com.carreras.servicios.ServiceManager;
import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

/**
 *
 * @author Fanky10 <fanky10@gmail.com>
 * este controlador sirve para dar servicio a cosas como:
 * nuevo evento de tiempo
 * lista de la carrera actual y sus tiempos.
 */
public class CarreraController {

    private ServiceManager serviceManager = Configuracion.getInstance().getServiceManager();
    private List<com.carreras.dominio.modelo.Carril> carriles = new ArrayList<com.carreras.dominio.modelo.Carril>();

    private Map tiemposCarril(final RespuestaEvent respuesta_event) {
        Map modelMap = new LinkedHashMap();
        try {


            arduino.entidades.Carril carrilDatos = (arduino.entidades.Carril) respuesta_event.getDatos();
            CarrerasLogger.info(CarreraController.class, "actualizando tiempos de carril: " + carrilDatos.getNro_carril());
            for (com.carreras.dominio.modelo.Carril carril : carriles) {
                if (carril.getNumero() == carrilDatos.getNro_carril()) {
                    //debemos actualizar los tiempos en la db (:
                    Tiempos tiemposDatos = carrilDatos.getTiempoV();
                    for (Tiempo t : tiemposDatos) {
                        double dTiempo = t.getTiempo();
                        int tipoTiempo = t.getTipo_tiempo();
                        TipoTiempo tipoTiempoModel = serviceManager.getTipoTiempo(tipoTiempo);
                        com.carreras.dominio.modelo.Tiempo tiempoModel = new com.carreras.dominio.modelo.Tiempo();
                        tiempoModel.setCarril(carril);
                        tiempoModel.setTiempo((float) dTiempo);
                        tiempoModel.setTipoTiempo(tipoTiempoModel);
                        tiempoModel.setId(serviceManager.saveTiempo(tiempoModel));

                        Float tiempoMinimo = carril.getInscriptoCompetencia().getCategoria().getTiempoMaximo();
                        //TODO: caida de tiempo 
                        if (tipoTiempo == Tiempo.TIEMPO_FIN
                                && tiempoMinimo > 1
                                && dTiempo < tiempoMinimo) {
                            Boolean caido = true;
                        }

                    }
                }
            }
        } catch (IllegalArgumentException ex) {
            //ignorada, me llego datos de un carril que nada que ver xF
        }
        return modelMap;

//            }
//        });
    }

    private Map adelantamientoCarril(final RespuestaEvent respuesta_event) {
        Map modelMap = new LinkedHashMap();
        Adelantamiento adelantamiento = (Adelantamiento) respuesta_event.getDatos();
        for (com.carreras.dominio.modelo.Carril carril : carriles) {
            if (carril.getNumero() == adelantamiento.getNro_carril()) {
                carril.getInscriptoCompetencia().setEstado(EstadoInscriptoCompetenciaCarrera.ADELANTADO);
                serviceManager.saveInscriptoCompetencia(carril.getInscriptoCompetencia());
            }
        }
        return modelMap;

    }

    private Map roturaAuto(final RespuestaEvent respuesta_event) {
        Map modelMap = new LinkedHashMap();
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
        return modelMap;
    }

    public Map estadoDatos(RespuestaEvent respuestaEvent) {
        Map modelMap = new LinkedHashMap();
        switch (respuestaEvent.getNro_evento()) {
            case RespuestaEvent.CARRIL:
                if (respuestaEvent.getDatos().getTipo() == Datos.CARRIL) {
                    modelMap = tiemposCarril(respuestaEvent);
                } else if (respuestaEvent.getDatos().getTipo() == Datos.ADELANTAMIENTO) {
                    CarrerasLogger.info(CarreraController.class, "hubo adelantamiento");
                    modelMap = adelantamientoCarril(respuestaEvent);
                } else if (respuestaEvent.getDatos().getTipo() == Datos.ROTURA) {
                    CarrerasLogger.info(CarreraController.class, "hubo roturas que");
                    modelMap = roturaAuto(respuestaEvent);
                }
                break;
            case RespuestaEvent.ERROR:
                modelMap.put("error", true);
                modelMap.put("mensaje", respuestaEvent.getError().getMensaje());
                
                break;
        }
        return modelMap;
    }
    /*
    public Map proximaCarrera(List<InscriptoCompetencia> inscriptosCorriendo){
        Map modelMap = new LinkedHashMap();
        List<Carril> carriles = new ArrayList<Carril>();

        for (InscriptoCompetencia ic : inscriptosCorriendo) {
            if (ic.getEstado() == EstadoInscriptoCompetenciaCarrera.ESPERANDO && !agregaCarril(ic)) {
                break;
            }
        }
        return modelMap;
    }
    */
    

    /**
     * agrega carril s/sea necesario
     * @return true carril agregado, false otherwise
     */
    /*
    private boolean agregaCarril(InscriptoCompetencia inscriptoCompetencia) {
        if (carreraActual == null) {
            carreraActual = new Carrera();
            carreraActual.setId(serviceManager.saveCarrera(carreraActual));
        }
        //se agrega el carril en cuestion
        //averiguo primero si no es el ganador.
        Integer sizeInscriptosCategoria = serviceManager.getInscriptosCompetencia(competenciaActual, inscriptoCompetencia.getCategoria()).size();
        //si no puedo agregar inscriptos y para mi categoria soy el unico --> GANADOR!
        if (!btnAgregarCorredor.isEnabled() && sizeInscriptosCategoria == 1) {//es el ganador, FIN
            //ganador final! --algun estado en particular?
            //TODO: estado particular!
            inscriptoCompetencia.setEstado(EstadoInscriptoCompetenciaCarrera.GANADOR);
            return false;

        } else if (carriles.isEmpty()) {
            //agrego el primer carril
            com.carreras.dominio.modelo.Carril carril = new com.carreras.dominio.modelo.Carril();
            carril.setCarrera(carreraActual);
            carril.setInscriptoCompetencia(inscriptoCompetencia);
            carril.setNumero(carriles.size() + 1);
            carril.setId(serviceManager.saveCarril(carril));
            carriles.add(carril);
            inscriptoCompetencia.setEstado(EstadoInscriptoCompetenciaCarrera.CORRIENDO);
            recargaCarriles();
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
            recargaCarriles();
            return true;
        } else if (carriles.size() == 1
                //y son de la misma categoria pero distinto estado inicial de comp.
                && carriles.get(carriles.size() - 1).getInscriptoCompetencia().getCategoria().getId().equals(inscriptoCompetencia.getCategoria().getId())
                && !carriles.get(carriles.size() - 1).getInscriptoCompetencia().getEstadoCompetencia().getId().equals(inscriptoCompetencia.getEstadoCompetencia().getId())
                //chequeo que sean los ultimos dos
                && sizeInscriptosCategoria == 2) {
            //OPC A
            //LOOPEAR EN UNA NUEVA COMPETENCIA DONDE SIMPLEMENTE ESTEN JUGANDO ESTOS DOS MAMOCHOS
            //al finalizar la 3er ronda --> ganador de categoria!
            //setear el nro de ronda final xq siempre los va a elegir 
            //como c/ronda es independiente, se genera un tipo de ronda final
            //elegirlos indefinidamente (probar eso)
            //eliminarlos cuando hayan llegado al nro-ronda final

            //OPC B
            //MANDARLOS A UNA COLA DE FINALES
            com.carreras.dominio.modelo.Carril carril = new com.carreras.dominio.modelo.Carril();
            carril.setCarrera(carreraActual);
            carril.setInscriptoCompetencia(inscriptoCompetencia);
            carril.setNumero(carriles.size() + 1);
            carril.setId(serviceManager.saveCarril(carril));
            carriles.add(carril);
            inscriptoCompetencia.setEstado(EstadoInscriptoCompetenciaCarrera.CORRIENDO);
            recargaCarriles();
            return true;
        }
        return false;
    }
     * *
     */
    

    public ServiceManager getServiceManager() {
        return serviceManager;
    }

    public void setServiceManager(ServiceManager serviceManager) {
        this.serviceManager = serviceManager;
    }
}
