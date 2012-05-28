/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package com.carreras.servicios.impl;

import com.carreras.config.HibernateUtil;
import com.carreras.dominio.modelo.TipoTiempo;
import com.carreras.servicios.TipoTiempoManager;
import java.util.List;
import org.hibernate.HibernateException;
import org.hibernate.Session;

/**
 *
 * @author Fanky10 <fanky10@gmail.com>
 */
public class TipoTiempoManagerImpl implements TipoTiempoManager {

    @Override
    public Integer save(TipoTiempo TipoTiempo) {
        Session session = null;
        try {
            session = HibernateUtil.getSessionFactory().openSession();
            session.beginTransaction();
            return (Integer) session.save(TipoTiempo);

        } catch (HibernateException he) {
            he.printStackTrace();
        } finally {
            session.getTransaction().commit();
        }
        return null;
    }

    @Override
    public TipoTiempo getOne(Integer id) {
        Session session = null;
        try {
            session = HibernateUtil.getSessionFactory().openSession();
            session.beginTransaction();
            org.hibernate.Query q = session.createQuery(" from " + TipoTiempo.class.getName() + " where id=" + id);
            return (TipoTiempo) q.list().get(0);

        } catch (HibernateException he) {
            he.printStackTrace();
        } finally {
            session.getTransaction().commit();
        }
        return null;
    }

    @Override
    public List<TipoTiempo> getAll() {
        Session session = null;
        try {
            session = HibernateUtil.getSessionFactory().openSession();
            session.beginTransaction();
            org.hibernate.Query q = session.createQuery(" from " + TipoTiempo.class.getName());
            return q.list();

        } catch (HibernateException he) {
            he.printStackTrace();
        } finally {
            session.getTransaction().commit();
        }
        return null;
    }
}
