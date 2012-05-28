/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package com.carreras.common.util;

import com.carreras.dominio.modelo.InscriptoCompetencia;
import java.util.ArrayList;
import java.util.List;

/**
 *
 * @author Fanky10 <fanky10@gmail.com>
 */
public class InscriptosCompetenciaFilter {
    
    public static List<InscriptoCompetencia> filtrarApellido(List<InscriptoCompetencia> inscriptosCompetencia, String apellido) {
        if (apellido == null || apellido.trim().isEmpty()) {
            return inscriptosCompetencia;
        }
        List<InscriptoCompetencia> reto = new ArrayList<InscriptoCompetencia> ();
        for (final InscriptoCompetencia inscriptoCompetencia : inscriptosCompetencia) {
            if (inscriptoCompetencia.getInscripto().getCorredor().getApellido().toLowerCase().startsWith(apellido.toLowerCase())) {
                reto.add(inscriptoCompetencia);
            }
        }
        return reto;
    }
    public static List<InscriptoCompetencia> filtrarDNI(List<InscriptoCompetencia> inscriptosCompetencia, String sDNI) {
        if (sDNI == null || sDNI.trim().isEmpty()) {
            return inscriptosCompetencia;
        }
        List<InscriptoCompetencia> reto = new ArrayList<InscriptoCompetencia>();
        for (final InscriptoCompetencia inscriptoCompetencia : inscriptosCompetencia) {
            if (inscriptoCompetencia.getInscripto().getCorredor().getDni().toString().startsWith(sDNI)){
                reto.add(inscriptoCompetencia);
            }
        }
        return reto;
    }
}
