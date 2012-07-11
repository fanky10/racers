/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package com.carreras.common.util;

import com.carreras.dominio.modelo.InscriptoCompetencia;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.TreeMap;

/**
 *
 * @author Fanky10 <fanky10@gmail.com>
 * it helps to find a winner (:
 */
public class GanadorCompetenciaHelper {
    
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
