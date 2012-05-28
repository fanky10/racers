/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package com.carreras.gui;

import com.carreras.dominio.modelo.Corredor;
import com.carreras.servicios.impl.CorredorManagerImpl;
import java.util.List;

/**
 *
 * @author fanky
 */
public class Mainly {

    /**
     * @param args the command line arguments
     */
    public static void main(String[] args) {
        List<Corredor> corredores = new CorredorManagerImpl().getCorredores();
        for(Corredor c: corredores)
            System.out.println("corredor: "+c.getNombre());
    }
}
