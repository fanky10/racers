/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package com.carreras.servicios.impl;

import com.carreras.util.GeneralTest;
import com.carreras.dominio.modelo.Categoria;
import com.carreras.servicios.CategoriaManager;
import java.util.List;
import org.junit.Before;
import org.junit.Test;

/**
 *
 * @author Fanky10 <fanky10@gmail.com>
 */
public class CategoriaManagerTest extends GeneralTest{
    
    private CategoriaManager categoriaManager;
    private static final Float TEST_TIEMPO_MAXIMO = 12f;
    @Before
    public void saveTest(){
        Categoria categoria = new Categoria();
        categoria.setDescripcion("Categoria 12");
        categoria.setTiempoMaximo(TEST_TIEMPO_MAXIMO);
        categoriaManager = new CategoriaManagerImpl();
        idGenerado = categoriaManager.save(categoria);
        assert (idGenerado!=null);
    }
    
    @Test
    public void getOneTest(){
        assert(categoriaManager.getOne(idGenerado)!=null);
    }
    
    
    @Test
    public void getAllTest(){
        List<Categoria> categorias = categoriaManager.getAll();
        assert(categorias!=null && !categorias.isEmpty());
    }
    
    @Test
    public void testGetCategoriaTiempo(){
        assert(categoriaManager.getCategoria(TEST_TIEMPO_MAXIMO - 1) !=null);
    }
    
}
