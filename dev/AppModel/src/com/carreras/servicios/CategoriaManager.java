/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package com.carreras.servicios;

import com.carreras.dominio.modelo.Categoria;
import java.util.List;

/**
 *
 * @author Fanky10 <fanky10@gmail.com>
 */
public interface CategoriaManager {
    public Integer save(Categoria categoria);
    public Categoria getOne(Integer id);
    public List<Categoria> getAll();

    public Categoria getCategoria(Float tiempoRelacionado);
}
