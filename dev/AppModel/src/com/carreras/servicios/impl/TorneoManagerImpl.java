/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package com.carreras.servicios.impl;

import com.carreras.config.HibernateUtil;
import com.carreras.dominio.modelo.Torneo;
import com.carreras.servicios.TorneoManager;
import java.util.List;
import org.hibernate.HibernateException;
import org.hibernate.Query;
import org.hibernate.Session;

/**
 *
 * @author Fanky10 <fanky10@gmail.com>
 */
public class TorneoManagerImpl implements TorneoManager{

    @Override
    public Integer save(Torneo torneo) {
        Session session = null;
        try {
            session = HibernateUtil.getSessionFactory().openSession();
            session.beginTransaction();
            return (Integer) session.save(torneo);
            
        } catch (HibernateException he) {
            he.printStackTrace();
        } finally {
            session.getTransaction().commit();
        }
        return null;
    }

    @Override
    public Torneo getOne(Integer idTorneo) {
        Session session = null;
        try {
            session = HibernateUtil.getSessionFactory().openSession();
            Query q = session.createQuery(" from "+Torneo.class.getName()+" t where t.id="+idTorneo);
            List result = q.list();
            if(result.isEmpty()){
                return null;
            }
            return (Torneo) q.list().get(0);

        } catch (HibernateException he) {
            he.printStackTrace();
        } finally {
            
        }
        return null;
    }

    @Override
    public List<Torneo> getAll() {
        Session session = null;
        try {
            session = HibernateUtil.getSessionFactory().openSession();
            session.beginTransaction();
            Query q = session.createQuery(" from "+Torneo.class.getName());
            return q.list();

        } catch (HibernateException he) {
            he.printStackTrace();
        } finally {
            session.getTransaction().commit();
        }
        return null;
    }
    
}
