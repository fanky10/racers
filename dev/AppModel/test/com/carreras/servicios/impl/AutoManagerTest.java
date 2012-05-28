/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package com.carreras.servicios.impl;

import com.carreras.dominio.modelo.Auto;
import com.carreras.servicios.AutoManager;
import java.util.List;
import org.junit.Before;
import org.junit.Test;

/**
 *
 * @author Fanky10 <fanky10@gmail.com>
 */

public class AutoManagerTest {
    
    private AutoManager manager;
    private int idGenerado;
    
    @Before
    public void buildData(){
        manager = new AutoManagerImpl();
        Auto auto = new Auto();
        auto.setPatente("lalal");
        idGenerado = getManager().guardar(auto);
    }
    @Test
    public void testGetAll(){
        List<Auto> list = getManager().getAutos();
        assert !list.isEmpty();//assert true;
    }
    
    @Test
    public void testGetOne(){
        Auto a = getManager().getAuto(idGenerado);
        assert a!=null;//assert true;
    }

    public AutoManager getManager() {
        return manager;
    }

    public void setManager(AutoManager manager) {
        this.manager = manager;
    }
    
    
}
