/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package com.carreras.servicios.impl;

import com.carreras.util.GeneralTest;
import com.carreras.dominio.modelo.Auto;
import com.carreras.dominio.modelo.Competencia;
import com.carreras.dominio.modelo.Corredor;
import com.carreras.dominio.modelo.Inscripto;
import com.carreras.dominio.modelo.Torneo;
import com.carreras.servicios.AutoManager;
import com.carreras.servicios.CorredorManager;
import com.carreras.servicios.InscriptoManager;
import com.carreras.servicios.TorneoManager;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import org.junit.Before;
import org.junit.Test;

/**
 *
 * @author Fanky10 <fanky10@gmail.com>
 */
public class InscriptoManagerTest extends GeneralTest{
    private CorredorManager corredorManager;
    private AutoManager autoManager;
    private TorneoManager torneoManager;
    private InscriptoManager inscriptoManager;

    @Before
    public void saveTest() {
        Auto auto = new Auto();
        auto.setPatente("lalal");
        autoManager = new AutoManagerImpl();
        auto.setId(autoManager.guardar(auto));

        Corredor corredor = new Corredor();
        corredor.setNombre("pepe");
        corredor.setDni("123444");
        corredor.setApellido("pepu");
        corredorManager = new CorredorManagerImpl();
        corredor.setId(corredorManager.guardar(corredor));
        
        idGenerados = new ArrayList<Integer>();
        Inscripto inscripto = new Inscripto();
        inscripto.setAuto(auto);
        inscripto.setCorredor(corredor);
        inscriptoManager = new InscriptoManagerImpl();
        inscripto.setId(inscriptoManager.save(inscripto));
        //for testGetOne
        idGenerado = inscripto.getId();
        idGenerados.add(inscripto.getId());
        
        assert (!idGenerados.isEmpty());
    }

    @Test
    public void testGetOne() {
        assert (inscriptoManager.getOne(idGenerado)!=null);
    }
    
    @Test
    public void testGetAll() {
        List<Inscripto> inscriptos = inscriptoManager.getAll();
        assert (inscriptos!=null && !inscriptos.isEmpty());
    }
    @Test
    public void testGetAllBut() {
        Integer[] ids = idGenerados.toArray(new Integer[0]);
        List<Inscripto> inscriptos = inscriptoManager.getAllInscriptosBut(ids);
        assert (!inscriptos.isEmpty());
    }
    
}
