/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package com.carreras.common.util;

import com.carreras.dominio.modelo.EstadoCompetencia;
import com.carreras.dominio.modelo.InscriptoCompetencia;
import java.util.ArrayList;
import java.util.Collections;
import java.util.EnumMap;
import java.util.List;
import java.util.Map;

/**
 *
 * @author Fanky10 <fanky10@gmail.com>
 */
public class InscriptoCompetenciaHelper {
    /**
     * hace un shuffle por tipo de estado en los inscriptos
     * @param inscriptos
     * @return 
     */
    public static List<InscriptoCompetencia> suffleInscriptos(List<InscriptoCompetencia> inscriptos){
        List<InscriptoCompetencia> resultSet = new ArrayList<InscriptoCompetencia>();
        Map<EstadoCompetencia,List<InscriptoCompetencia>> inscriptosMap = new EnumMap<EstadoCompetencia, List<InscriptoCompetencia>> (EstadoCompetencia.class);
        for(InscriptoCompetencia ic: inscriptos){
            List<InscriptoCompetencia> innerList = new ArrayList<InscriptoCompetencia>();
            if(inscriptosMap.containsKey(ic.getEstadoCompetencia())){
                innerList = inscriptosMap.get(ic.getEstadoCompetencia());
                innerList.add(ic);
            }else{
                innerList.add(ic);
                inscriptosMap.put(ic.getEstadoCompetencia(), innerList);
            }
        }
        for(EstadoCompetencia ec: inscriptosMap.keySet()){
            List<InscriptoCompetencia> innerList = inscriptosMap.get(ec);
            Collections.shuffle(innerList);
            resultSet.addAll(innerList);
        }
        return inscriptos;
    }
    public static List<InscriptoCompetencia> mueveInscriptoFordward(List<InscriptoCompetencia> inscriptos,Integer index){
        Integer distance = index+1;
        if(distance>=0 && distance != inscriptos.size())
            Collections.rotate(inscriptos.subList(index, distance+1), -1);
        return inscriptos;
    }
    public static List<InscriptoCompetencia> mueveInscriptoBackward(List<InscriptoCompetencia> inscriptos,Integer index){
        Integer distance = index-1; 
        if(distance>=0 && distance != inscriptos.size() && index>0)
            Collections.rotate(inscriptos.subList(distance, index+1), 1);
        return inscriptos;
    }
//    public static void main(String args[]){
//        List<String> test = new ArrayList<String>();
//        test.add("a");
//        test.add("b");
//        test.add("c");
//        test.add("d");
//        System.out.println("moving forwards: "+test);
//        for(int i=0;i<test.size();i++){
//            System.out.println("--"+i);
//            moveForward(test, i);
//            System.out.println("resultSet: "+test);
//        }
//        System.out.println("========================");
//        System.out.println("moving backwards: "+test);
//        for(int i=(test.size()-1);i>=0;i--){
//            System.out.println("--"+i);
//            moveBackwards(test, i);
//            System.out.println("resultSet: "+test);
//        }
//    }
//    private static void moveForward(List<String> strings,Integer index){
//        Integer distance = index+1; 
//        if(distance>=0 && distance != strings.size())
//            Collections.rotate(strings.subList(index, distance+1), -1);
//    }
//    
//    private static void moveBackwards(List<String> list,Integer index){
//        Integer distance = index-1; 
//        if(distance>=0 && distance != list.size() && index>0)
//            Collections.rotate(list.subList(distance, index+1), 1);
//    }
}
