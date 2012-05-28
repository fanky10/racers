/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package com.carreras.servicios.impl;

import com.carreras.dominio.modelo.Corredor;
import com.carreras.servicios.CorredorManager;
import java.util.List;
import org.junit.Before;
import org.junit.Test;

/**
 *
 * @author Fanky10 <fanky10@gmail.com>
 */
public class CorredorManagerTest {
    private CorredorManager manager;
    private int idGenerado;
    
    @Before
    public void buildData(){
        manager = new CorredorManagerImpl();
        Corredor corredor = new Corredor();
        corredor.setNombre("pepe");
        corredor.setDni("123444");
        corredor.setApellido("pepu");
        idGenerado = getManager().guardar(corredor);
    }
    @Test
    public void testGetAll(){
        List<Corredor> list = getManager().getCorredores();
        assert !list.isEmpty();//assert true;
    }
    
    @Test
    public void testGetOne(){
        Corredor c = getManager().getCorredor(idGenerado);
        assert c!=null;//assert true;
    }

    public int getIdGenerado() {
        return idGenerado;
    }

    public void setIdGenerado(int idGenerado) {
        this.idGenerado = idGenerado;
    }

    public CorredorManager getManager() {
        return manager;
    }

    public void setManager(CorredorManager manager) {
        this.manager = manager;
    }
    
}
