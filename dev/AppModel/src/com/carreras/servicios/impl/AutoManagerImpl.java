/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package com.carreras.servicios.impl;
import com.carreras.config.HibernateUtil;
import com.carreras.dominio.modelo.Auto;
import com.carreras.servicios.AutoManager;
import java.util.List;
import org.hibernate.HibernateException;
import org.hibernate.Query;
import org.hibernate.Session;

/**
 *
 * @author Fanky10 <fanky10@gmail.com>
 */
public class AutoManagerImpl implements AutoManager{

    public List<Auto> getAutos() {
        Session session = null;
        try {
            session = HibernateUtil.getSessionFactory().openSession();
            session.beginTransaction();
            Query q = session.createQuery(" from Auto");
            return q.list();

        } catch (HibernateException he) {
            he.printStackTrace();
        } finally {
            session.getTransaction().commit();
        }
        return null;
    }

    @Override
    public Integer guardar(Auto a) {
        Session session = null;
        try {
            session = HibernateUtil.getSessionFactory().openSession();
            session.beginTransaction();
            return (Integer) session.save(a);
            
        } catch (HibernateException he) {
            he.printStackTrace();
        } finally {
            session.getTransaction().commit();
        }
        return null;
    }

    @Override
    public void eliminar(Auto a) {
        Session session = null;
        try {
            session = HibernateUtil.getSessionFactory().openSession();
            session.beginTransaction();
            session.delete(a);
            session.getTransaction().commit();
        } catch (HibernateException he) {
            he.printStackTrace();
        } finally {
            
        }
    }

    @Override
    public Auto getAuto(int id) {
        Session session = null;
        try {
            session = HibernateUtil.getSessionFactory().openSession();
            Query q = session.createQuery(" from Auto a where a.id="+id);
            List result = q.list();
            if(result.isEmpty()){
                return null;
            }
            return (Auto) q.list().get(0);

        } catch (HibernateException he) {
            he.printStackTrace();
        } finally {
            
        }
        return null;
    }
}
