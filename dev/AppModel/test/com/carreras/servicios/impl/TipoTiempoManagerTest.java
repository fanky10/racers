/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package com.carreras.servicios.impl;

import com.carreras.dominio.modelo.TipoTiempo;
import com.carreras.servicios.TipoTiempoManager;
import com.carreras.util.GeneralTest;
import java.util.List;
import org.junit.Before;
import org.junit.Test;

/**
 *
 * @author Fanky10 <fanky10@gmail.com>
 */
public class TipoTiempoManagerTest extends GeneralTest{
    private TipoTiempoManager tipoTiempoManager;
    
    @Before
    @Test
    public void buildData(){//voy a ver si genera los ids solo jeje
        //genero un tipo de tiempo, ver de hacerlo enum :D
        TipoTiempo tipoTiempo = new TipoTiempo();
        tipoTiempo.setDecisorio(true);
        tipoTiempo.setDescripcion("myTime");
        tipoTiempo.setHabilitado(true);
        tipoTiempo.setPosicion(10);
        tipoTiempoManager = new TipoTiempoManagerImpl();
        idGenerado = tipoTiempoManager.save(tipoTiempo);
        
    }
    @Test
    public void testGetOne(){
        assert (tipoTiempoManager.getOne(idGenerado) != null);
    }
    @Test
    public void testGetAll(){
        List<TipoTiempo> tipoTiempos = tipoTiempoManager.getAll();
        assert (tipoTiempos!=null && !tipoTiempos.isEmpty());
    }
}
