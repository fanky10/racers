/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package com.carreras.servicios.impl;

import com.carreras.config.HibernateUtil;
import com.carreras.dominio.modelo.Inscripto;
import com.carreras.servicios.InscriptoManager;
import java.util.ArrayList;
import java.util.List;
import org.hibernate.HibernateException;
import org.hibernate.Query;
import org.hibernate.Session;

/**
 *
 * @author Fanky10 <fanky10@gmail.com>
 */
public class InscriptoManagerImpl implements InscriptoManager {

    @Override
    public Integer save(Inscripto inscripto) {
        Session session = null;
        try {
            session = HibernateUtil.getSessionFactory().openSession();
            session.beginTransaction();
            return (Integer) session.save(inscripto);

        } catch (HibernateException he) {
            he.printStackTrace();
        } finally {
            session.getTransaction().commit();
        }
        return null;
    }

    @Override
    public Inscripto getOne(Integer id) {
        Session session = null;
        try {
            session = HibernateUtil.getSessionFactory().openSession();
            Query q = session.createQuery(" from " + Inscripto.class.getName() + " i where i.id=" + id);
            List result = q.list();
            if (result.isEmpty()) {
                return null;
            }
            return (Inscripto) q.list().get(0);

        } catch (HibernateException he) {
            he.printStackTrace();
        } finally {
        }
        return null;
    }

    @Override
    public List<Inscripto> getAll() {
        Session session = null;
        try {
            session = HibernateUtil.getSessionFactory().openSession();
            Query q = session.createQuery(" from " + Inscripto.class.getName() + " i");
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
    public List<Inscripto> getAllInscriptosBut(Integer[] idInscriptos) {
        StringBuilder sb = new StringBuilder();
        if (idInscriptos.length > 0) {
            sb.append(" where id not in (");
            for (int i = 0; i < idInscriptos.length; i++) {
                sb.append(idInscriptos[i]);
                if ((i + 1) != idInscriptos.length) {//not lastone
                    sb.append(",");
                }
            }
            sb.append(")");
        }
        Session session = null;
        try {
            session = HibernateUtil.getSessionFactory().openSession();
            Query q = session.createQuery(" from " + Inscripto.class.getName() + sb);
            List result = q.list();
            if (result.isEmpty()) {
                return null;
            }
            return q.list();

        } catch (HibernateException he) {
            he.printStackTrace();
        } finally {
        }
        return new ArrayList<Inscripto>();
    }
    
}
