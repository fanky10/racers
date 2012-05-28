/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package com.carreras.servicios.impl;

import com.carreras.config.HibernateUtil;
import com.carreras.dominio.modelo.Corredor;
import com.carreras.servicios.CorredorManager;
import java.util.List;
import org.hibernate.HibernateException;
import org.hibernate.Query;
import org.hibernate.Session;

/**
 *
 * @author Fanky10 <fanky10@gmail.com>
 */
public class CorredorManagerImpl implements CorredorManager {

    @Override
    public Integer guardar(Corredor c) {
        Session session = null;
        try {
            session = HibernateUtil.getSessionFactory().openSession();
            session.beginTransaction();
            return (Integer) session.save(c);
            
        } catch (HibernateException he) {
            he.printStackTrace();
        } finally {
            session.getTransaction().commit();
        }
        return null;
    }

    @Override
    public void eliminar(Corredor c) {
        Session session = null;
        try {
            session = HibernateUtil.getSessionFactory().openSession();
            session.beginTransaction();
            session.delete(c);
            session.getTransaction().commit();
        } catch (HibernateException he) {
            he.printStackTrace();
        }
    }

    @Override
    public List<Corredor> getCorredores() {
        Session session = null;
        try {
            session = HibernateUtil.getSessionFactory().openSession();
            session.beginTransaction();
            Query q = session.createQuery(" from Corredor");
            return q.list();

        } catch (HibernateException he) {
            he.printStackTrace();
        } finally {
            session.getTransaction().commit();
        }
        return null;
    }

    @Override
    public Corredor getCorredor(int id) {
        Session session = null;
        try {
            session = HibernateUtil.getSessionFactory().openSession();
            Query q = session.createQuery(" from Corredor c where c.id="+id);
            List result = q.list();
            if(result.isEmpty()){
                return null;
            }
            return (Corredor) q.list().get(0);

        } catch (HibernateException he) {
            he.printStackTrace();
        } finally {
            
        }
        return null;
    }
    
}
