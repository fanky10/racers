/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package com.carreras.servicios.impl;

import com.carreras.config.HibernateUtil;
import com.carreras.dominio.modelo.Categoria;
import com.carreras.dominio.modelo.EstadoInscriptoCompetenciaCarrera;
import com.carreras.dominio.modelo.InscriptoCompetencia;
import com.carreras.servicios.InscriptoCompetenciaManager;
import java.util.List;
import org.hibernate.HibernateException;
import org.hibernate.Query;
import org.hibernate.Session;

/**
 *
 * @author Fanky10 <fanky10@gmail.com>
 */
public class InscriptoCompetenciaManagerImpl implements InscriptoCompetenciaManager {
    
    @Override
    public Integer save(InscriptoCompetencia inscriptoCompetencia) {
        Session session = null;
        try {
            session = HibernateUtil.getSessionFactory().openSession();
            session.beginTransaction();
            return (Integer) session.save(inscriptoCompetencia);

        } catch (HibernateException he) {
            he.printStackTrace();
        } finally {
            session.getTransaction().commit();
        }
        return null;
    }
    @Override
    public void update(InscriptoCompetencia inscriptoCompetencia) {
        Session session = null;
        try {
            session = HibernateUtil.getSessionFactory().openSession();
            session.beginTransaction();
            session.update(inscriptoCompetencia);
        } catch (HibernateException he) {
            he.printStackTrace();
            session.getTransaction().rollback();
        } finally {
            session.getTransaction().commit();
        }
    }

    @Override
    public InscriptoCompetencia getOne(Integer id) {
        Session session = null;
        try {
            session = HibernateUtil.getSessionFactory().openSession();
            Query q = session.createQuery(" from " + InscriptoCompetencia.class.getName() + " i where i.id=" + id);
            List result = q.list();
            if (result.isEmpty()) {
                return null;
            }
            return (InscriptoCompetencia) q.list().get(0);

        } catch (HibernateException he) {
            he.printStackTrace();
        } finally {
        }
        return null;
    }

    @Override
    public List<InscriptoCompetencia> getAll() {
        Session session = null;
        try {
            session = HibernateUtil.getSessionFactory().openSession();
            Query q = session.createQuery(" from " + InscriptoCompetencia.class.getName());
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
    public List<InscriptoCompetencia> getInscriptosCompetencia(Integer idCompetencia) {
        return getEstadoInscriptosCompetencia(idCompetencia,null);
    }

    @Override
    public List<InscriptoCompetencia> getEstadoInscriptosCompetencia(Integer idCompetencia, EstadoInscriptoCompetenciaCarrera estado) {
        Session session = null;
        try {
            session = HibernateUtil.getSessionFactory().openSession();
            StringBuilder sbQuery = new StringBuilder();
            sbQuery.append(" FROM " + InscriptoCompetencia.class.getName() + " i");
            sbQuery.append(" WHERE i.competencia.id =" + idCompetencia);
            if(estado!=null){
                sbQuery.append(" AND i.estado =" + estado.ordinal());
            }
            sbQuery.append(" ORDER BY i.estado, i.categoria.descripcion");
            
            Query q = session.createQuery(sbQuery.toString());
            return q.list();

        } catch (HibernateException he) {
            he.printStackTrace();
        } finally {
        }
        return null;
    }

    @Override
    public List<InscriptoCompetencia> getInscriptosCompetencia(Integer idCompetencia, Integer idCategoria){
        Session session = null;
        try {
            session = HibernateUtil.getSessionFactory().openSession();
            StringBuilder sbQuery = new StringBuilder();
            sbQuery.append(" FROM " + InscriptoCompetencia.class.getName() + " i");
            sbQuery.append(" WHERE i.competencia.id =" + idCompetencia);
            sbQuery.append(" AND i.categoria.id =" + idCategoria);
            sbQuery.append(" ORDER BY i.estado, i.categoria.descripcion");
            
            Query q = session.createQuery(sbQuery.toString());
            return q.list();

        } catch (HibernateException he) {
            he.printStackTrace();
        } finally {
        }
        return null;
    }
    @Override
    public List<InscriptoCompetencia> getInscriptosCompetenciaLibre(Integer idCompetencia){
        Session session = null;
        try {
            session = HibernateUtil.getSessionFactory().openSession();
            StringBuilder sbQuery = new StringBuilder();
            sbQuery.append(" FROM " + InscriptoCompetencia.class.getName() + " i");
            sbQuery.append(" WHERE i.competencia.id =" + idCompetencia);
            sbQuery.append(" AND i.rondasRestantes > 1");
            sbQuery.append(" ORDER BY i.estado, i.categoria.descripcion");
            
            Query q = session.createQuery(sbQuery.toString());
            return q.list();

        } catch (HibernateException he) {
            he.printStackTrace();
        } finally {
        }
        return null;
    }
    
    public List<Categoria> getCategoriasEnUso(Integer idCompetencia){
        Session session = null;
        try {
            session = HibernateUtil.getSessionFactory().openSession();
            StringBuilder sbQuery = new StringBuilder();
            sbQuery.append(" SELECT DISTINCT ic.categoria FROM " + InscriptoCompetencia.class.getName() + " ic");
            sbQuery.append(" WHERE ic.competencia.id =" + idCompetencia);
            //sbQuery.append(" GROUP BY ic.categoria.id");
            sbQuery.append(" ORDER BY ic.categoria.descripcion");
            
            
            
            Query q = session.createQuery(sbQuery.toString());
            return q.list();

        } catch (HibernateException he) {
            he.printStackTrace();
        } finally {
        }
        return null;
    }
    
    @Override
    public List<InscriptoCompetencia> getAllTorneo(Integer idTorneo) {
        Session session = null;
        try {
            session = HibernateUtil.getSessionFactory().openSession();
            StringBuilder sbQuery = new StringBuilder();
            sbQuery.append(" FROM " + InscriptoCompetencia.class.getName() + " i");
            sbQuery.append(" WHERE i.competencia.torneo.id =" + idTorneo);
            sbQuery.append(" GROUP BY i.inscripto.id");
            sbQuery.append(" ORDER BY i.estado, i.categoria.descripcion");
            
            Query q = session.createQuery(sbQuery.toString());
            return q.list();

        } catch (HibernateException he) {
            he.printStackTrace();
        } finally {
        }
        return null;
    }

    @Override
    public List<InscriptoCompetencia> getEstadoInscriptosCompetencia(Integer idCompetencia, EstadoInscriptoCompetenciaCarrera estado, Integer idCategoria) {
        Session session = null;
        try {
            session = HibernateUtil.getSessionFactory().openSession();
            StringBuilder sbQuery = new StringBuilder();
            sbQuery.append(" FROM " + InscriptoCompetencia.class.getName() + " i");
            sbQuery.append(" WHERE i.competencia.id =" + idCompetencia);
            if(estado!=null){
                sbQuery.append(" AND i.estado =" + estado.ordinal());
            }
            sbQuery.append(" AND i.categoria.id =" + idCategoria);
            sbQuery.append(" ORDER BY i.estado, i.categoria.descripcion");
            
            Query q = session.createQuery(sbQuery.toString());
            return q.list();

        } catch (HibernateException he) {
            he.printStackTrace();
        } finally {
        }
        return null;
    }
}
