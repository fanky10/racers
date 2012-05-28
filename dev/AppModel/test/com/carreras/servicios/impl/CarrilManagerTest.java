/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package com.carreras.servicios.impl;

import com.carreras.util.GeneralTest;
import com.carreras.dominio.modelo.Auto;
import com.carreras.dominio.modelo.Carrera;
import com.carreras.dominio.modelo.Carril;
import com.carreras.dominio.modelo.Categoria;
import com.carreras.dominio.modelo.Competencia;
import com.carreras.dominio.modelo.Corredor;
import com.carreras.dominio.modelo.Inscripto;
import com.carreras.dominio.modelo.InscriptoCompetencia;
import com.carreras.dominio.modelo.TipoCompetencia;
import com.carreras.dominio.modelo.Torneo;
import com.carreras.servicios.AutoManager;
import com.carreras.servicios.CarreraManager;
import com.carreras.servicios.CarrilManager;
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
public class CarrilManagerTest extends GeneralTest {

    private CorredorManager corredorManager;
    private AutoManager autoManager;
    private TorneoManager torneoManager;
    private InscriptoManager inscriptoManager;
    private CompetenciaManager competenciaManager;
    private InscriptoCompetenciaManager inscriptoCompetenciaManager;
    private CategoriaManager categoriaManager;
    private CarreraManager carreraManager;
    private CarrilManager carrilManager;

    @Before
    @Test
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

        Competencia competencia = new Competencia();
        competencia.setNumeroRonda(1);
        competencia.setTipoCompetencia(TipoCompetencia.LIBRE);
        competencia.setTorneo(torneo);
        competenciaManager = new CompetenciaManagerImpl();
        competencia.setId(competenciaManager.save(competencia));

        Categoria categoria = new Categoria();
        categoria.setDescripcion("aDesc");
        categoria.setTiempoMaximo(10f);
        categoriaManager = new CategoriaManagerImpl();
        categoria.setId(categoriaManager.save(categoria));

        InscriptoCompetencia inscriptoCompetencia = new InscriptoCompetencia();
        inscriptoCompetencia.setInscripto(inscripto);
        inscriptoCompetencia.setCompetencia(competencia);
        inscriptoCompetencia.setCategoria(categoria);
        inscriptoCompetencia.generaNumero();
        inscriptoCompetenciaManager = new InscriptoCompetenciaManagerImpl();
        inscriptoCompetencia.setId(inscriptoCompetenciaManager.save(inscriptoCompetencia));

        Carrera carrera = new Carrera();
        carreraManager = new CarreraManagerImpl();
        carrera.setId(carreraManager.save(carrera));

        Carril carril = new Carril();
        carril.setNumero(1);
        carril.setInscriptoCompetencia(inscriptoCompetencia);
        carril.setCarrera(carrera);
        carrilManager = new CarrilManagerImpl();
        idGenerado = carrilManager.save(carril);

        assert idGenerado != null;
    }

    @Test
    public void getOneTest() {
        assert (carrilManager.getOne(idGenerado) != null);
    }

    @Test
    public void getAllTest() {
        List<Carril> carriles = carrilManager.getAll();
        assert (carriles != null && !carriles.isEmpty());
    }
}
