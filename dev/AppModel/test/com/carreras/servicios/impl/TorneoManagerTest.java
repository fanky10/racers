/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package com.carreras.servicios.impl;

import com.carreras.dominio.modelo.Torneo;
import com.carreras.servicios.TorneoManager;
import java.util.Date;
import java.util.List;
import org.junit.Before;
import org.junit.Test;

/**
 *
 * @author Fanky10 <fanky10@gmail.com>
 */
public class TorneoManagerTest {
    
    private TorneoManager torneoManager;
    
    private Integer idGenerado;
    @Before
    public void builData(){
        torneoManager = new TorneoManagerImpl();
        Torneo t = new Torneo();
        t.setFechaHora(new Date(System.currentTimeMillis()));
        idGenerado = torneoManager.save(t);
    }
    
    @Test
    public void getOne(){
        assert (torneoManager.getOne(idGenerado)!=null);
    }
    
    @Test 
    public void getAll(){
        assert (!torneoManager.getAll().isEmpty());
    }
    
}
