/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package com.carreras.common.util;

import com.carreras.dominio.modelo.Categoria;
import com.carreras.dominio.modelo.EstadoCompetencia;
import com.carreras.dominio.modelo.EstadoInscriptoCompetenciaCarrera;
import com.carreras.dominio.modelo.InscriptoCompetencia;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.EnumMap;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.TreeMap;

/**
 *
 * @author Fanky10 <fanky10@gmail.com>
 */
public class InscriptosCompetenciaHelper {
    /**
     * elimina el finalista de 
     * @return 
     */
    public static List<InscriptoCompetencia> getInscriptosValidos(List<InscriptoCompetencia> inscriptos){
        List<InscriptoCompetencia> result = new ArrayList<InscriptoCompetencia>();
        int count = 0;
        InscriptoCompetencia actual = null;
        
        // sorting
        Collections.sort(inscriptos, new Comparator<InscriptoCompetencia>(){

                @Override
                public int compare(InscriptoCompetencia c1, InscriptoCompetencia c2) {
                    //sin el getestadocomp andaba bien hey! xD
                    Integer cat1 = c1.getCategoria().getId();
                    Integer cat2 = c2.getCategoria().getId();
                    int catComp = cat1.compareTo(cat2);
                    if(catComp!=0){
                        return catComp;
                    }else{
                        Integer ecomp1 = c1.getEstadoCompetencia().getId();
                        Integer ecomp2 = c2.getEstadoCompetencia().getId();
                        return ecomp1.compareTo(ecomp2);
                    }
                }
        });
        //este mini algoritmo supone que los inscriptos estan ordenados por:
        //categoria primero y luego por estado (ganador o perdedor).
        for(InscriptoCompetencia inscripto: inscriptos){
            if(actual == null){
                actual = inscripto;
            }else if(actual.getCategoria().getId() == inscripto.getCategoria().getId()){
                
            }
        }
        return result;
    }
    
    public static Integer getGanador(Map<Integer,Integer> winnersMap){
        // cambio del concepto
        for(Integer ic: winnersMap.keySet()){
            if(winnersMap.get(ic) == 2){
                return ic;
            }
        }
        return null;
        
    }
    
    /**
     * orders a map in desc order.
     */
    static class ValueComparator implements Comparator {

        Map base;

        public ValueComparator(Map base) {
            this.base = base;
        }

        public int compare(Object a, Object b) {
            
            if ((Integer) base.get(a) < (Integer) base.get(b)) {
                return 1;
            } else if ((Integer) base.get(a) == (Integer) base.get(b)) {
                return 0;
            } else {
                return -1;
            }
        }
    }
    
    public static Map<String, List<InscriptoCompetencia>> getCategoriaInscriptosCompetenciaMap(List<InscriptoCompetencia> inscriptos) {
        Map<String, List<InscriptoCompetencia>> categoriaInscriptosCompetencia = new java.util.LinkedHashMap<String, List<InscriptoCompetencia>>();
        for (InscriptoCompetencia ic : inscriptos) {
            List<InscriptoCompetencia> inscriptosValues = null;
            if (categoriaInscriptosCompetencia.containsKey(ic.getCategoria().getDescripcion())) {
                inscriptosValues = categoriaInscriptosCompetencia.get(ic.getCategoria().getDescripcion());
                inscriptosValues.add(ic);
            } else {
                inscriptosValues = new ArrayList<InscriptoCompetencia>();
                inscriptosValues.add(ic);
                categoriaInscriptosCompetencia.put(ic.getCategoria().getDescripcion(), inscriptosValues);
            }
        }
        return categoriaInscriptosCompetencia;
    }
    
    public static Map<EstadoCompetencia, List<InscriptoCompetencia>> getEstadoCompetenciaInscriptosCompetenciaMap(List<InscriptoCompetencia> inscriptos) {
        Map<EstadoCompetencia, List<InscriptoCompetencia>> estadoInscriptos = new EnumMap<EstadoCompetencia, List<InscriptoCompetencia>>(EstadoCompetencia.class);
        //TODO: mover a un controlador
        if(inscriptos.size()>2){
            for (InscriptoCompetencia ic : inscriptos) {
                List<InscriptoCompetencia> inscriptosActual = null;
                if (estadoInscriptos.containsKey(ic.getEstadoCompetencia())) {
                    inscriptosActual = estadoInscriptos.get(ic.getEstadoCompetencia());
                    inscriptosActual.add(ic);
                } else {
                    inscriptosActual = new ArrayList<InscriptoCompetencia>();
                    inscriptosActual.add(ic);
                    estadoInscriptos.put(ic.getEstadoCompetencia(), inscriptosActual);
                }
            }
        }else{//estamos en la final (:
            estadoInscriptos.put(EstadoCompetencia.COMPETENCIA_EN_FINAL, inscriptos);
            
        }
        return estadoInscriptos;
    }
    
    
    
    /**
     * a aquellos que son los ultimos y son impar = saltearlos
     */
    public static List<InscriptoCompetencia> salteaImpares(List<InscriptoCompetencia> inscriptos,EstadoInscriptoCompetenciaCarrera estadoDefault) {
        List<InscriptoCompetencia> resultSet = new ArrayList<InscriptoCompetencia>();
        // ahora para c/categoria, lo intenta agregar, si queda alguno afuera, lo pone en estado proxima ronda y sigue con el proximo.
        Map<String, List<InscriptoCompetencia>> categoriaInscriptos = InscriptosCompetenciaHelper.getCategoriaInscriptosCompetenciaMap(inscriptos);
        for (String categoria : categoriaInscriptos.keySet()) {
            //para c/categoria, verificar por estadoCompetencia el impar.
            List<InscriptoCompetencia> inscriptosCategoria = categoriaInscriptos.get(categoria);
            Map<EstadoCompetencia, List<InscriptoCompetencia>> inscriptosEstadoCategoria = InscriptosCompetenciaHelper.getEstadoCompetenciaInscriptosCompetenciaMap(inscriptosCategoria);
            for (EstadoCompetencia ec : inscriptosEstadoCategoria.keySet()) {
                List<InscriptoCompetencia> inscriptosActual = inscriptosEstadoCategoria.get(ec);
                Iterator<InscriptoCompetencia> icIterator = inscriptosActual.iterator();
                while(icIterator.hasNext()){
                    InscriptoCompetencia ic = (InscriptoCompetencia) icIterator.next();
                    //FIXME: fixme! (:
                    if(!icIterator.hasNext() && inscriptosActual.size()%2!=0){//estoy en el ultimo! :D y el size del array es impar lo quiero hacer de goma (:
                        ic.setEstado(EstadoInscriptoCompetenciaCarrera.PROXIMA_RONDA);
                    }else if(ic.getEstado()==EstadoInscriptoCompetenciaCarrera.PROXIMA_RONDA){//no es el ultimo y no tiene el estado adecuado
                        ic.setEstado(estadoDefault);
                    }
                }
                resultSet.addAll(inscriptosActual);
            }

        }
        return resultSet;
    }

    /**
     * hace un shuffle por tipo de estado en los inscriptos
     * @param inscriptos
     * @return 
     */
    public static List<InscriptoCompetencia> suffleInscriptos(List<InscriptoCompetencia> inscriptos) {
        List<InscriptoCompetencia> resultSet = new ArrayList<InscriptoCompetencia>();
        Map<EstadoCompetencia, List<InscriptoCompetencia>> inscriptosMap = new java.util.EnumMap<EstadoCompetencia, List<InscriptoCompetencia>>(EstadoCompetencia.class);
        for (InscriptoCompetencia ic : inscriptos) {
            List<InscriptoCompetencia> innerList = new ArrayList<InscriptoCompetencia>();
            if (inscriptosMap.containsKey(ic.getEstadoCompetencia())) {
                innerList = inscriptosMap.get(ic.getEstadoCompetencia());
                innerList.add(ic);
            } else {
                innerList.add(ic);
                inscriptosMap.put(ic.getEstadoCompetencia(), innerList);
            }
        }
        for (EstadoCompetencia ec : inscriptosMap.keySet()) {
            List<InscriptoCompetencia> innerList = inscriptosMap.get(ec);
            Collections.shuffle(innerList);
            resultSet.addAll(innerList);
        }
        return inscriptos;
    }

    public static List<InscriptoCompetencia> mueveInscriptoFordward(List<InscriptoCompetencia> inscriptos, Integer index) {
        Integer distance = index + 1;
        if (distance >= 0 && distance != inscriptos.size()) {
            Collections.rotate(inscriptos.subList(index, distance + 1), -1);
        }
        return inscriptos;
    }

    public static List<InscriptoCompetencia> mueveInscriptoBackward(List<InscriptoCompetencia> inscriptos, Integer index) {
        Integer distance = index - 1;
        if (distance >= 0 && distance != inscriptos.size() && index > 0) {
            Collections.rotate(inscriptos.subList(distance, index + 1), 1);
        }
        return inscriptos;
    }
}
