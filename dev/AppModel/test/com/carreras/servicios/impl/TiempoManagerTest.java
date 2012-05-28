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
import com.carreras.dominio.modelo.EstadoInscriptoCompetenciaCarrera;
import com.carreras.dominio.modelo.Inscripto;
import com.carreras.dominio.modelo.InscriptoCompetencia;
import com.carreras.dominio.modelo.Tiempo;
import com.carreras.dominio.modelo.TipoCompetencia;
import com.carreras.dominio.modelo.TipoTiempo;
import com.carreras.dominio.modelo.Torneo;
import com.carreras.servicios.AutoManager;
import com.carreras.servicios.CarreraManager;
import com.carreras.servicios.CarrilManager;
import com.carreras.servicios.CategoriaManager;
import com.carreras.servicios.CompetenciaManager;
import com.carreras.servicios.CorredorManager;
import com.carreras.servicios.InscriptoCompetenciaManager;
import com.carreras.servicios.InscriptoManager;
import com.carreras.servicios.TiempoManager;
import com.carreras.servicios.TipoTiempoManager;
import com.carreras.servicios.TorneoManager;
import java.util.Date;
import java.util.List;
import org.junit.Before;
import org.junit.Test;

/**
 *
 * @author Fanky10 <fanky10@gmail.com>
 */
public class TiempoManagerTest extends GeneralTest{
    private CorredorManager corredorManager;
    private AutoManager autoManager;
    private TorneoManager torneoManager;
    private InscriptoManager inscriptoManager;
    private CompetenciaManager competenciaManager;
    private InscriptoCompetenciaManager inscriptoCompetenciaManager;
    private CategoriaManager categoriaManager;
    private CarreraManager carreraManager;
    private CarrilManager carrilManager;
    private TipoTiempoManager tipoTiempoManager;
    private TiempoManager tiempoManager;
    
    private Carril carril;
    private TipoTiempo tipoTiempo;
    private Torneo torneo;
    private Inscripto inscripto;
    @Before
    @Test
    public void buildData(){
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

        inscripto = new Inscripto();
        inscripto.setAuto(auto);
        inscripto.setCorredor(corredor);
        inscriptoManager = new InscriptoManagerImpl();
        inscripto.setId(inscriptoManager.save(inscripto));
        
        torneo = new Torneo();
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
        inscriptoCompetencia.setEstado(EstadoInscriptoCompetenciaCarrera.GANADOR);
        inscriptoCompetencia.setCompetencia(competencia);
        inscriptoCompetencia.setCategoria(categoria);
        inscriptoCompetencia.generaNumero();
        inscriptoCompetenciaManager = new InscriptoCompetenciaManagerImpl();
        inscriptoCompetencia.setId(inscriptoCompetenciaManager.save(inscriptoCompetencia));
        
        Carrera carrera = new Carrera();
        carreraManager = new CarreraManagerImpl();
        carrera.setId(carreraManager.save(carrera));
        
        carril = new Carril();
        carril.setNumero(1);
        carril.setInscriptoCompetencia(inscriptoCompetencia);
        carril.setCarrera(carrera);
        carrilManager = new CarrilManagerImpl();
        carril.setId(carrilManager.save(carril));

        //genero un tipo de tiempo, ver de hacerlo enum :D
        //TODO: hacerlo enum! :D
        tipoTiempo = new TipoTiempo();
        tipoTiempo.setDecisorio(true);
        tipoTiempo.setDescripcion("myTime");
        tipoTiempo.setHabilitado(true);
        tipoTiempo.setPosicion(10);
        tipoTiempoManager = new TipoTiempoManagerImpl();
        tipoTiempo.setId(tipoTiempoManager.save(tipoTiempo));
        
        Tiempo tiempo = new Tiempo();
        tiempo.setTipoTiempo(tipoTiempo);
        tiempo.setTiempo(12f);
        tiempo.setCarril(carril);
        
        tiempoManager = new TiempoManagerImpl();
        idGenerado = tiempoManager.save(tiempo);
        
        assert (idGenerado!=null);
        
    }
    @Test
    public void testGetOne(){
        assert (tiempoManager.getOne(idGenerado) != null);
    }
    @Test
    public void testGetAll(){
        List<Tiempo> tiempos = tiempoManager.getAll();
        assert (tiempos!=null && !tiempos.isEmpty());
    }
    @Test
    public void testGetTiemposCarril(){
        List<Tiempo> tiempos = tiempoManager.getTiemposCarril(carril.getId());
        assert (tiempos!=null && !tiempos.isEmpty());
    }
    @Test
    public void testGetTipoTiempoCarril(){
        Tiempo tiempo = tiempoManager.getTiempo(carril.getId(), tipoTiempo.getId());
        assert (tiempo!=null);
    }
    @Test
    public void testGetMejorTiempo(){
        Tiempo tiempo = tiempoManager.getMejorTiempo(torneo.getId(), inscripto.getId());
        System.out.println("mejor tiempo: "+tiempo.getTiempo());
        assert (tiempo!=null);
    }
}
