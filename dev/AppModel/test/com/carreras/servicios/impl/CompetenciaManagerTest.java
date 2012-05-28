/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package com.carreras.servicios.impl;

import static org.junit.Assert.assertTrue;

import com.carreras.util.GeneralTest;
import com.carreras.dominio.modelo.Auto;
import com.carreras.dominio.modelo.Categoria;
import com.carreras.dominio.modelo.Competencia;
import com.carreras.dominio.modelo.Corredor;
import com.carreras.dominio.modelo.EstadoCompetencia;
import com.carreras.dominio.modelo.EstadoInscriptoCompetenciaCarrera;
import com.carreras.dominio.modelo.Inscripto;
import com.carreras.dominio.modelo.InscriptoCompetencia;
import com.carreras.dominio.modelo.TipoCompetencia;
import com.carreras.dominio.modelo.Torneo;
import com.carreras.servicios.AutoManager;
import com.carreras.servicios.CategoriaManager;
import com.carreras.servicios.CompetenciaManager;
import com.carreras.servicios.CorredorManager;
import com.carreras.servicios.InscriptoCompetenciaManager;
import com.carreras.servicios.InscriptoManager;
import com.carreras.servicios.TorneoManager;
import java.util.Date;
import java.util.List;
import org.junit.Before;
import org.junit.Test;

/**
 *
 * @author Fanky10 <fanky10@gmail.com>
 */
public class CompetenciaManagerTest extends GeneralTest {

    private CorredorManager corredorManager;
    private AutoManager autoManager;
    private TorneoManager torneoManager;
    private InscriptoManager inscriptoManager;
    private CompetenciaManager competenciaManager;
    private InscriptoCompetenciaManager inscriptoCompetenciaManager;
    private CategoriaManager categoriaManager;
    
    private Categoria categoriaGenerada;
    private Competencia competenciaGenerada;
    
    @Before
    public void buildData() {
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

        Inscripto inscripto = new Inscripto();
        inscripto.setAuto(auto);
        inscripto.setCorredor(corredor);
        inscriptoManager = new InscriptoManagerImpl();
        inscripto.setId(inscriptoManager.save(inscripto));
        
        Torneo torneo = new Torneo();
        torneo.setFechaHora(new Date(System.currentTimeMillis()));
        torneoManager = new TorneoManagerImpl();
        torneo.setId(torneoManager.save(torneo));

        competenciaGenerada = new Competencia();
        competenciaGenerada.setNumeroRonda(1);
        competenciaGenerada.setTipoCompetencia(TipoCompetencia.LIBRE);
        competenciaGenerada.setTorneo(torneo);
        competenciaManager = new CompetenciaManagerImpl();
        competenciaGenerada.setId(competenciaManager.save(competenciaGenerada));
        
        categoriaGenerada = new Categoria();
        categoriaGenerada.setDescripcion("aDesc");
        categoriaGenerada.setTiempoMaximo(10f);
        categoriaManager = new CategoriaManagerImpl();
        categoriaGenerada.setId(categoriaManager.save(categoriaGenerada));
        
        InscriptoCompetencia inscriptoCompetencia = new InscriptoCompetencia();
        inscriptoCompetencia.setInscripto(inscripto);
        inscriptoCompetencia.setCompetencia(competenciaGenerada);
        inscriptoCompetencia.setCategoria(categoriaGenerada);
        inscriptoCompetencia.setRondasRestantes(3);
        inscriptoCompetencia.generaNumero();
        inscriptoCompetencia.setEstado(EstadoInscriptoCompetenciaCarrera.GANADOR);
        inscriptoCompetencia.setEstadoCompetencia(EstadoCompetencia.COMPETENCIA_GANADORES);
        inscriptoCompetenciaManager = new InscriptoCompetenciaManagerImpl();
        inscriptoCompetencia.setId(inscriptoCompetenciaManager.save(inscriptoCompetencia));
    }

    @Test
    public void testSave() {
        Competencia competencia = competenciaManager.getOne(competenciaGenerada.getId());
        assert competencia != null;
    }
    @Test
    public void testGetCompetenciaCategoria(){
        Competencia competencia = competenciaManager.getCompetenciaActual(categoriaGenerada.getId());
        assertTrue(competencia != null);
    }
    
}
