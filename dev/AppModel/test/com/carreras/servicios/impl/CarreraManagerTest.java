/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package com.carreras.servicios.impl;

import com.carreras.dominio.modelo.Carrera;
import com.carreras.servicios.CarreraManager;
import com.carreras.util.GeneralTest;
import java.util.List;
import org.junit.Before;
import org.junit.Test;

/**
 *
 * @author Fanky10 <fanky10@gmail.com>
 */
public class CarreraManagerTest extends GeneralTest {

    private CarreraManager carreraManager;

    @Before
    public void buildData() {
        Carrera carrera = new Carrera();
        carreraManager = new CarreraManagerImpl();
        idGenerado = carreraManager.save(carrera);
        assert (idGenerado != null);
    }
    
    @Test
    public void getOne(){
        assert (carreraManager.getOne(idGenerado) != null);
    }
    @Test
    public void getAll(){
        List<Carrera> carreras = carreraManager.getAll();
        assert (carreras!=null && !carreras.isEmpty());
    }
}
