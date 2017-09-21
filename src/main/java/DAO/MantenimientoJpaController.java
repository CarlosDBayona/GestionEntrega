/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package DAO;

import DAO.exceptions.NonexistentEntityException;
import DAO.exceptions.PreexistingEntityException;
import DAO.exceptions.RollbackFailureException;
import java.io.Serializable;
import javax.persistence.Query;
import javax.persistence.EntityNotFoundException;
import javax.persistence.criteria.CriteriaQuery;
import javax.persistence.criteria.Root;
import Datos.Herramienta;
import Datos.Mantenimiento;
import java.util.List;
import javax.persistence.EntityManager;
import javax.persistence.EntityManagerFactory;
import javax.transaction.UserTransaction;

/**
 *
 * @author Carlos
 */
public class MantenimientoJpaController implements Serializable {

    public MantenimientoJpaController(UserTransaction utx, EntityManagerFactory emf) {
        this.utx = utx;
        this.emf = emf;
    }
    private UserTransaction utx = null;
    private EntityManagerFactory emf = null;

    public EntityManager getEntityManager() {
        return emf.createEntityManager();
    }

    public void create(Mantenimiento mantenimiento) throws PreexistingEntityException, RollbackFailureException, Exception {
        EntityManager em = null;
        try {
            utx.begin();
            em = getEntityManager();
            Herramienta idHerramienta = mantenimiento.getIdHerramienta();
            if (idHerramienta != null) {
                idHerramienta = em.getReference(idHerramienta.getClass(), idHerramienta.getIdHerramienta());
                mantenimiento.setIdHerramienta(idHerramienta);
            }
            em.persist(mantenimiento);
            if (idHerramienta != null) {
                idHerramienta.getMantenimientoCollection().add(mantenimiento);
                idHerramienta = em.merge(idHerramienta);
            }
            utx.commit();
        } catch (Exception ex) {
            try {
                utx.rollback();
            } catch (Exception re) {
                throw new RollbackFailureException("An error occurred attempting to roll back the transaction.", re);
            }
            if (findMantenimiento(mantenimiento.getIdMantenimiento()) != null) {
                throw new PreexistingEntityException("Mantenimiento " + mantenimiento + " already exists.", ex);
            }
            throw ex;
        } finally {
            if (em != null) {
                em.close();
            }
        }
    }

    public void edit(Mantenimiento mantenimiento) throws NonexistentEntityException, RollbackFailureException, Exception {
        EntityManager em = null;
        try {
            utx.begin();
            em = getEntityManager();
            Mantenimiento persistentMantenimiento = em.find(Mantenimiento.class, mantenimiento.getIdMantenimiento());
            Herramienta idHerramientaOld = persistentMantenimiento.getIdHerramienta();
            Herramienta idHerramientaNew = mantenimiento.getIdHerramienta();
            if (idHerramientaNew != null) {
                idHerramientaNew = em.getReference(idHerramientaNew.getClass(), idHerramientaNew.getIdHerramienta());
                mantenimiento.setIdHerramienta(idHerramientaNew);
            }
            mantenimiento = em.merge(mantenimiento);
            if (idHerramientaOld != null && !idHerramientaOld.equals(idHerramientaNew)) {
                idHerramientaOld.getMantenimientoCollection().remove(mantenimiento);
                idHerramientaOld = em.merge(idHerramientaOld);
            }
            if (idHerramientaNew != null && !idHerramientaNew.equals(idHerramientaOld)) {
                idHerramientaNew.getMantenimientoCollection().add(mantenimiento);
                idHerramientaNew = em.merge(idHerramientaNew);
            }
            utx.commit();
        } catch (Exception ex) {
            try {
                utx.rollback();
            } catch (Exception re) {
                throw new RollbackFailureException("An error occurred attempting to roll back the transaction.", re);
            }
            String msg = ex.getLocalizedMessage();
            if (msg == null || msg.length() == 0) {
                String id = mantenimiento.getIdMantenimiento();
                if (findMantenimiento(id) == null) {
                    throw new NonexistentEntityException("The mantenimiento with id " + id + " no longer exists.");
                }
            }
            throw ex;
        } finally {
            if (em != null) {
                em.close();
            }
        }
    }

    public void destroy(String id) throws NonexistentEntityException, RollbackFailureException, Exception {
        EntityManager em = null;
        try {
            utx.begin();
            em = getEntityManager();
            Mantenimiento mantenimiento;
            try {
                mantenimiento = em.getReference(Mantenimiento.class, id);
                mantenimiento.getIdMantenimiento();
            } catch (EntityNotFoundException enfe) {
                throw new NonexistentEntityException("The mantenimiento with id " + id + " no longer exists.", enfe);
            }
            Herramienta idHerramienta = mantenimiento.getIdHerramienta();
            if (idHerramienta != null) {
                idHerramienta.getMantenimientoCollection().remove(mantenimiento);
                idHerramienta = em.merge(idHerramienta);
            }
            em.remove(mantenimiento);
            utx.commit();
        } catch (Exception ex) {
            try {
                utx.rollback();
            } catch (Exception re) {
                throw new RollbackFailureException("An error occurred attempting to roll back the transaction.", re);
            }
            throw ex;
        } finally {
            if (em != null) {
                em.close();
            }
        }
    }

    public List<Mantenimiento> findMantenimientoEntities() {
        return findMantenimientoEntities(true, -1, -1);
    }

    public List<Mantenimiento> findMantenimientoEntities(int maxResults, int firstResult) {
        return findMantenimientoEntities(false, maxResults, firstResult);
    }

    private List<Mantenimiento> findMantenimientoEntities(boolean all, int maxResults, int firstResult) {
        EntityManager em = getEntityManager();
        try {
            CriteriaQuery cq = em.getCriteriaBuilder().createQuery();
            cq.select(cq.from(Mantenimiento.class));
            Query q = em.createQuery(cq);
            if (!all) {
                q.setMaxResults(maxResults);
                q.setFirstResult(firstResult);
            }
            return q.getResultList();
        } finally {
            em.close();
        }
    }

    public Mantenimiento findMantenimiento(String id) {
        EntityManager em = getEntityManager();
        try {
            return em.find(Mantenimiento.class, id);
        } finally {
            em.close();
        }
    }

    public int getMantenimientoCount() {
        EntityManager em = getEntityManager();
        try {
            CriteriaQuery cq = em.getCriteriaBuilder().createQuery();
            Root<Mantenimiento> rt = cq.from(Mantenimiento.class);
            cq.select(em.getCriteriaBuilder().count(rt));
            Query q = em.createQuery(cq);
            return ((Long) q.getSingleResult()).intValue();
        } finally {
            em.close();
        }
    }
    
}
