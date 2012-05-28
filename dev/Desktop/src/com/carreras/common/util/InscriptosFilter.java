/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package com.carreras.common.util;

import com.carreras.dominio.modelo.Inscripto;
import com.carreras.dominio.modelo.InscriptoCompetencia;
import java.util.ArrayList;
import java.util.List;

/**
 *
 * @author Fanky10 <fanky10@gmail.com>
 */
public class InscriptosFilter {

    public static List<Inscripto> filtrarApellido(List<Inscripto> inscriptos, String apellido) {
        if (apellido == null || apellido.trim().isEmpty()) {
            return inscriptos;
        }
        List<Inscripto> reto = new ArrayList<Inscripto>();
        for (final Inscripto inscripto : inscriptos) {
            if (inscripto.getCorredor().getApellido().toLowerCase().startsWith(apellido.toLowerCase())) {
                reto.add(inscripto);
            }
        }
        return reto;
    }
    public static List<Inscripto> filtrarDNI(List<Inscripto> inscriptos, String sDNI) {
        if (sDNI == null || sDNI.trim().isEmpty()) {
            return inscriptos;
        }
        List<Inscripto> reto = new ArrayList<Inscripto>();
        for (final Inscripto inscripto : inscriptos) {
            if (inscripto.getCorredor().getDni().toString().startsWith(sDNI)){
                reto.add(inscripto);
            }
        }
        return reto;
    }
}
