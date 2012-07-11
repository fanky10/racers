/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package com.carreras.common.util;

import com.carreras.dominio.modelo.InscriptoCompetencia;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
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
    
    public static InscriptoCompetencia getGanador(Map<InscriptoCompetencia,Integer> winnersMap){
        List<InscriptoCompetencia> orderedListWiners = new ArrayList<InscriptoCompetencia>();
        ValueComparator comp = new ValueComparator(winnersMap);
        Map<InscriptoCompetencia,Integer> orderedMapWinners = new TreeMap<InscriptoCompetencia, Integer>(comp);
        orderedMapWinners.putAll(winnersMap);
        orderedListWiners.addAll(orderedMapWinners.keySet());
        if(orderedListWiners!=null && !orderedListWiners.isEmpty()){
            return orderedListWiners.get(0);
        }
        return null;
        
    }
    //for testing purposes
    public static void main(String args[]){
        Map<InscriptoCompetencia,Integer> winnersMap = new HashMap<InscriptoCompetencia, Integer>();
        InscriptoCompetencia insc = new InscriptoCompetencia();
        insc.setId(1);
        winnersMap.put(insc, 10);
        insc = new InscriptoCompetencia();
        insc.setId(5);
        winnersMap.put(insc, 40);
        InscriptoCompetencia winner = getGanador(winnersMap);
        if(winner.getId() == 5)
            System.out.println("success!");
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
}
