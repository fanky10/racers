/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package com.carreras.servicios.impl;

import com.carreras.config.HibernateUtil;
import com.carreras.dominio.modelo.Categoria;
import com.carreras.servicios.CategoriaManager;
import java.util.List;
import org.hibernate.HibernateException;
import org.hibernate.Query;
import org.hibernate.Session;

/**
 *
 * @author Fanky10 <fanky10@gmail.com>
 */
public class CategoriaManagerImpl implements CategoriaManager {

    @Override
    public Integer save(Categoria categoria) {
        Session session = null;
        try {
            session = HibernateUtil.getSessionFactory().openSession();
            session.beginTransaction();
            return (Integer) session.save(categoria);

        } catch (HibernateException he) {
            he.printStackTrace();
        } finally {
            session.getTransaction().commit();
        }
        return null;
    }

    @Override
    public Categoria getOne(Integer id) {
        Session session = null;
        try {
            session = HibernateUtil.getSessionFactory().openSession();
            Query q = session.createQuery(" from " + Categoria.class.getName() + " c where c.id=" + id);
            List result = q.list();
            if (result.isEmpty()) {
                return null;
            }
            return (Categoria) q.list().get(0);

        } catch (HibernateException he) {
            he.printStackTrace();
        } finally {
        }
        return null;
    }

    @Override
    public List<Categoria> getAll() {
        Session session = null;
        try {
            session = HibernateUtil.getSessionFactory().openSession();
            Query q = session.createQuery(" from " + Categoria.class.getName());
            List result = q.list();
            if (result.isEmpty()) {
                return null;
            }
            return q.list();

        } catch (HibernateException he) {
            he.printStackTrace();
        } finally {
        }
        return null;
    }

    @Override
    public Categoria getCategoria(Float tiempoRelacionado) {
        Session session = null;
        try {
            session = HibernateUtil.getSessionFactory().openSession();
            Query q = session.createQuery(" from " + Categoria.class.getName() + " c where c.tiempoMaximo<=" + tiempoRelacionado);
            q.setMaxResults(1);
            List result = q.list();
            if (result.isEmpty()) {
                return null;
            }
            
            return (Categoria) q.list().get(0);

        } catch (HibernateException he) {
            he.printStackTrace();
        } finally {
        }
        return null;
    }
}
