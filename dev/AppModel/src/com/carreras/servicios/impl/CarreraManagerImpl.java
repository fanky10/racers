/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package com.carreras.servicios.impl;

import com.carreras.config.HibernateUtil;
import com.carreras.dominio.modelo.Carrera;
import com.carreras.servicios.CarreraManager;
import java.util.List;
import org.hibernate.HibernateException;
import org.hibernate.Query;
import org.hibernate.Session;

/**
 *
 * @author Fanky10 <fanky10@gmail.com>
 */
public class CarreraManagerImpl implements CarreraManager{

    @Override
    public Integer save(Carrera carrera) {
        Session session = null;
        try {
            session = HibernateUtil.getSessionFactory().openSession();
            session.beginTransaction();
            return (Integer) session.save(carrera);
            
        } catch (HibernateException he) {
            he.printStackTrace();
        } finally {
            session.getTransaction().commit();
        }
        return null;
    }

    @Override
    public Carrera getOne(Integer id) {
        Session session = null;
        try {
            session = HibernateUtil.getSessionFactory().openSession();
            Query q = session.createQuery(" from "+Carrera.class.getName()+" a where a.id="+id);
            List result = q.list();
            if(result.isEmpty()){
                return null;
            }
            return (Carrera) q.list().get(0);

        } catch (HibernateException he) {
            he.printStackTrace();
        } finally {
            
        }
        return null;
    }

    @Override
    public List<Carrera> getAll() {
        Session session = null;
        try {
            session = HibernateUtil.getSessionFactory().openSession();
            Query q = session.createQuery(" from "+Carrera.class.getName());
            List result = q.list();
            if(result.isEmpty()){
                return null;
            }
            return q.list();
        } catch (HibernateException he) {
            he.printStackTrace();
        } finally {
            
        }
        return null;
    }
    
}
