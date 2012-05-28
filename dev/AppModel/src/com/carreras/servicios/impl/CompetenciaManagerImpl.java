/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package com.carreras.servicios.impl;

import com.carreras.config.HibernateUtil;
import com.carreras.dominio.modelo.Competencia;
import com.carreras.dominio.modelo.InscriptoCompetencia;
import com.carreras.servicios.CompetenciaManager;
import java.util.List;
import org.hibernate.HibernateException;
import org.hibernate.Query;
import org.hibernate.Session;

/**
 *
 * @author Fanky10 <fanky10@gmail.com>
 */
public class CompetenciaManagerImpl implements CompetenciaManager{

    @Override
    public Integer save(Competencia competencia) {
        Session session = null;
        try {
            session = HibernateUtil.getSessionFactory().openSession();
            session.beginTransaction();
            return (Integer) session.save(competencia);
            
        } catch (HibernateException he) {
            he.printStackTrace();
        } finally {
            session.getTransaction().commit();
        }
        return null;
    }

    @Override
    public Competencia getOne(Integer idCompetencia) {
        Session session = null;
        try {
            session = HibernateUtil.getSessionFactory().openSession();
            Query q = session.createQuery(" from "+Competencia.class.getName()+" a where a.id="+idCompetencia);
            List result = q.list();
            if(result.isEmpty()){
                return null;
            }
            return (Competencia) q.list().get(0);

        } catch (HibernateException he) {
            he.printStackTrace();
        } finally {
            
        }
        return null;
    }

    @Override
    public List<Competencia> getAll() {
        throw new UnsupportedOperationException("Not supported yet.");
    }
    
    @Override
    public Competencia getCompetenciaActual(Integer idCategoria) {
        Session session = null;
        try {
            session = HibernateUtil.getSessionFactory().openSession();
            Query q = session.createQuery("select c from "+InscriptoCompetencia.class.getName()+" ic"
                    + " JOIN ic.competencia c"
                    + " WHERE ic.categoria.id="+idCategoria
                    + " ORDER BY c.id DESC");
            q.setMaxResults(1);
            List result = q.list();
           
            if(result.isEmpty()){
                return null;
            }
            return (Competencia) q.list().get(0);

        } catch (HibernateException he) {
            he.printStackTrace();
        }
        return null;
    }
    
}
