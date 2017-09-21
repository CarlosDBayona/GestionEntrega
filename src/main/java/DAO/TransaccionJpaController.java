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
import Datos.Prestamo;
import Datos.Transaccion;
import java.util.List;
import javax.persistence.EntityManager;
import javax.persistence.EntityManagerFactory;
import javax.transaction.UserTransaction;

/**
 *
 * @author Carlos
 */
public class TransaccionJpaController implements Serializable {

    public TransaccionJpaController(UserTransaction utx, EntityManagerFactory emf) {
        this.utx = utx;
        this.emf = emf;
    }
    private UserTransaction utx = null;
    private EntityManagerFactory emf = null;

    public EntityManager getEntityManager() {
        return emf.createEntityManager();
    }

    public void create(Transaccion transaccion) throws PreexistingEntityException, RollbackFailureException, Exception {
        EntityManager em = null;
        try {
            utx.begin();
            em = getEntityManager();
            Herramienta idHerramienta = transaccion.getIdHerramienta();
            if (idHerramienta != null) {
                idHerramienta = em.getReference(idHerramienta.getClass(), idHerramienta.getIdHerramienta());
                transaccion.setIdHerramienta(idHerramienta);
            }
            Prestamo codPrestamo = transaccion.getCodPrestamo();
            if (codPrestamo != null) {
                codPrestamo = em.getReference(codPrestamo.getClass(), codPrestamo.getCodPrestamo());
                transaccion.setCodPrestamo(codPrestamo);
            }
            em.persist(transaccion);
            if (idHerramienta != null) {
                idHerramienta.getTransaccionCollection().add(transaccion);
                idHerramienta = em.merge(idHerramienta);
            }
            if (codPrestamo != null) {
                codPrestamo.getTransaccionCollection().add(transaccion);
                codPrestamo = em.merge(codPrestamo);
            }
            utx.commit();
        } catch (Exception ex) {
            try {
                utx.rollback();
            } catch (Exception re) {
                throw new RollbackFailureException("An error occurred attempting to roll back the transaction.", re);
            }
            if (findTransaccion(transaccion.getIdTransaccion()) != null) {
                throw new PreexistingEntityException("Transaccion " + transaccion + " already exists.", ex);
            }
            throw ex;
        } finally {
            if (em != null) {
                em.close();
            }
        }
    }

    public void edit(Transaccion transaccion) throws NonexistentEntityException, RollbackFailureException, Exception {
        EntityManager em = null;
        try {
            utx.begin();
            em = getEntityManager();
            Transaccion persistentTransaccion = em.find(Transaccion.class, transaccion.getIdTransaccion());
            Herramienta idHerramientaOld = persistentTransaccion.getIdHerramienta();
            Herramienta idHerramientaNew = transaccion.getIdHerramienta();
            Prestamo codPrestamoOld = persistentTransaccion.getCodPrestamo();
            Prestamo codPrestamoNew = transaccion.getCodPrestamo();
            if (idHerramientaNew != null) {
                idHerramientaNew = em.getReference(idHerramientaNew.getClass(), idHerramientaNew.getIdHerramienta());
                transaccion.setIdHerramienta(idHerramientaNew);
            }
            if (codPrestamoNew != null) {
                codPrestamoNew = em.getReference(codPrestamoNew.getClass(), codPrestamoNew.getCodPrestamo());
                transaccion.setCodPrestamo(codPrestamoNew);
            }
            transaccion = em.merge(transaccion);
            if (idHerramientaOld != null && !idHerramientaOld.equals(idHerramientaNew)) {
                idHerramientaOld.getTransaccionCollection().remove(transaccion);
                idHerramientaOld = em.merge(idHerramientaOld);
            }
            if (idHerramientaNew != null && !idHerramientaNew.equals(idHerramientaOld)) {
                idHerramientaNew.getTransaccionCollection().add(transaccion);
                idHerramientaNew = em.merge(idHerramientaNew);
            }
            if (codPrestamoOld != null && !codPrestamoOld.equals(codPrestamoNew)) {
                codPrestamoOld.getTransaccionCollection().remove(transaccion);
                codPrestamoOld = em.merge(codPrestamoOld);
            }
            if (codPrestamoNew != null && !codPrestamoNew.equals(codPrestamoOld)) {
                codPrestamoNew.getTransaccionCollection().add(transaccion);
                codPrestamoNew = em.merge(codPrestamoNew);
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
                String id = transaccion.getIdTransaccion();
                if (findTransaccion(id) == null) {
                    throw new NonexistentEntityException("The transaccion with id " + id + " no longer exists.");
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
            Transaccion transaccion;
            try {
                transaccion = em.getReference(Transaccion.class, id);
                transaccion.getIdTransaccion();
            } catch (EntityNotFoundException enfe) {
                throw new NonexistentEntityException("The transaccion with id " + id + " no longer exists.", enfe);
            }
            Herramienta idHerramienta = transaccion.getIdHerramienta();
            if (idHerramienta != null) {
                idHerramienta.getTransaccionCollection().remove(transaccion);
                idHerramienta = em.merge(idHerramienta);
            }
            Prestamo codPrestamo = transaccion.getCodPrestamo();
            if (codPrestamo != null) {
                codPrestamo.getTransaccionCollection().remove(transaccion);
                codPrestamo = em.merge(codPrestamo);
            }
            em.remove(transaccion);
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

    public List<Transaccion> findTransaccionEntities() {
        return findTransaccionEntities(true, -1, -1);
    }

    public List<Transaccion> findTransaccionEntities(int maxResults, int firstResult) {
        return findTransaccionEntities(false, maxResults, firstResult);
    }

    private List<Transaccion> findTransaccionEntities(boolean all, int maxResults, int firstResult) {
        EntityManager em = getEntityManager();
        try {
            CriteriaQuery cq = em.getCriteriaBuilder().createQuery();
            cq.select(cq.from(Transaccion.class));
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

    public Transaccion findTransaccion(String id) {
        EntityManager em = getEntityManager();
        try {
            return em.find(Transaccion.class, id);
        } finally {
            em.close();
        }
    }

    public int getTransaccionCount() {
        EntityManager em = getEntityManager();
        try {
            CriteriaQuery cq = em.getCriteriaBuilder().createQuery();
            Root<Transaccion> rt = cq.from(Transaccion.class);
            cq.select(em.getCriteriaBuilder().count(rt));
            Query q = em.createQuery(cq);
            return ((Long) q.getSingleResult()).intValue();
        } finally {
            em.close();
        }
    }
    
}
