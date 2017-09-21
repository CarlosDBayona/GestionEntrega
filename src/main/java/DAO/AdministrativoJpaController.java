/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package DAO;

import DAO.exceptions.IllegalOrphanException;
import DAO.exceptions.NonexistentEntityException;
import DAO.exceptions.PreexistingEntityException;
import DAO.exceptions.RollbackFailureException;
import Datos.Administrativo;
import java.io.Serializable;
import javax.persistence.Query;
import javax.persistence.EntityNotFoundException;
import javax.persistence.criteria.CriteriaQuery;
import javax.persistence.criteria.Root;
import Datos.Prestamo;
import java.util.ArrayList;
import java.util.Collection;
import Datos.Herramienta;
import java.util.List;
import javax.persistence.EntityManager;
import javax.persistence.EntityManagerFactory;
import javax.transaction.UserTransaction;

/**
 *
 * @author Carlos
 */
public class AdministrativoJpaController implements Serializable {

    public AdministrativoJpaController(UserTransaction utx, EntityManagerFactory emf) {
        this.utx = utx;
        this.emf = emf;
    }
    private UserTransaction utx = null;
    private EntityManagerFactory emf = null;

    public EntityManager getEntityManager() {
        return emf.createEntityManager();
    }

    public void create(Administrativo administrativo) throws PreexistingEntityException, RollbackFailureException, Exception {
        if (administrativo.getPrestamoCollection() == null) {
            administrativo.setPrestamoCollection(new ArrayList<Prestamo>());
        }
        if (administrativo.getHerramientaCollection() == null) {
            administrativo.setHerramientaCollection(new ArrayList<Herramienta>());
        }
        EntityManager em = null;
        try {
            utx.begin();
            em = getEntityManager();
            Collection<Prestamo> attachedPrestamoCollection = new ArrayList<Prestamo>();
            for (Prestamo prestamoCollectionPrestamoToAttach : administrativo.getPrestamoCollection()) {
                prestamoCollectionPrestamoToAttach = em.getReference(prestamoCollectionPrestamoToAttach.getClass(), prestamoCollectionPrestamoToAttach.getCodPrestamo());
                attachedPrestamoCollection.add(prestamoCollectionPrestamoToAttach);
            }
            administrativo.setPrestamoCollection(attachedPrestamoCollection);
            Collection<Herramienta> attachedHerramientaCollection = new ArrayList<Herramienta>();
            for (Herramienta herramientaCollectionHerramientaToAttach : administrativo.getHerramientaCollection()) {
                herramientaCollectionHerramientaToAttach = em.getReference(herramientaCollectionHerramientaToAttach.getClass(), herramientaCollectionHerramientaToAttach.getIdHerramienta());
                attachedHerramientaCollection.add(herramientaCollectionHerramientaToAttach);
            }
            administrativo.setHerramientaCollection(attachedHerramientaCollection);
            em.persist(administrativo);
            for (Prestamo prestamoCollectionPrestamo : administrativo.getPrestamoCollection()) {
                Administrativo oldIdAdministrativoOfPrestamoCollectionPrestamo = prestamoCollectionPrestamo.getIdAdministrativo();
                prestamoCollectionPrestamo.setIdAdministrativo(administrativo);
                prestamoCollectionPrestamo = em.merge(prestamoCollectionPrestamo);
                if (oldIdAdministrativoOfPrestamoCollectionPrestamo != null) {
                    oldIdAdministrativoOfPrestamoCollectionPrestamo.getPrestamoCollection().remove(prestamoCollectionPrestamo);
                    oldIdAdministrativoOfPrestamoCollectionPrestamo = em.merge(oldIdAdministrativoOfPrestamoCollectionPrestamo);
                }
            }
            for (Herramienta herramientaCollectionHerramienta : administrativo.getHerramientaCollection()) {
                Administrativo oldIdAdministrativoOfHerramientaCollectionHerramienta = herramientaCollectionHerramienta.getIdAdministrativo();
                herramientaCollectionHerramienta.setIdAdministrativo(administrativo);
                herramientaCollectionHerramienta = em.merge(herramientaCollectionHerramienta);
                if (oldIdAdministrativoOfHerramientaCollectionHerramienta != null) {
                    oldIdAdministrativoOfHerramientaCollectionHerramienta.getHerramientaCollection().remove(herramientaCollectionHerramienta);
                    oldIdAdministrativoOfHerramientaCollectionHerramienta = em.merge(oldIdAdministrativoOfHerramientaCollectionHerramienta);
                }
            }
            utx.commit();
        } catch (Exception ex) {
            try {
                utx.rollback();
            } catch (Exception re) {
                throw new RollbackFailureException("An error occurred attempting to roll back the transaction.", re);
            }
            if (findAdministrativo(administrativo.getIdAdministrativo()) != null) {
                throw new PreexistingEntityException("Administrativo " + administrativo + " already exists.", ex);
            }
            throw ex;
        } finally {
            if (em != null) {
                em.close();
            }
        }
    }

    public void edit(Administrativo administrativo) throws IllegalOrphanException, NonexistentEntityException, RollbackFailureException, Exception {
        EntityManager em = null;
        try {
            utx.begin();
            em = getEntityManager();
            Administrativo persistentAdministrativo = em.find(Administrativo.class, administrativo.getIdAdministrativo());
            Collection<Prestamo> prestamoCollectionOld = persistentAdministrativo.getPrestamoCollection();
            Collection<Prestamo> prestamoCollectionNew = administrativo.getPrestamoCollection();
            Collection<Herramienta> herramientaCollectionOld = persistentAdministrativo.getHerramientaCollection();
            Collection<Herramienta> herramientaCollectionNew = administrativo.getHerramientaCollection();
            List<String> illegalOrphanMessages = null;
            for (Prestamo prestamoCollectionOldPrestamo : prestamoCollectionOld) {
                if (!prestamoCollectionNew.contains(prestamoCollectionOldPrestamo)) {
                    if (illegalOrphanMessages == null) {
                        illegalOrphanMessages = new ArrayList<String>();
                    }
                    illegalOrphanMessages.add("You must retain Prestamo " + prestamoCollectionOldPrestamo + " since its idAdministrativo field is not nullable.");
                }
            }
            for (Herramienta herramientaCollectionOldHerramienta : herramientaCollectionOld) {
                if (!herramientaCollectionNew.contains(herramientaCollectionOldHerramienta)) {
                    if (illegalOrphanMessages == null) {
                        illegalOrphanMessages = new ArrayList<String>();
                    }
                    illegalOrphanMessages.add("You must retain Herramienta " + herramientaCollectionOldHerramienta + " since its idAdministrativo field is not nullable.");
                }
            }
            if (illegalOrphanMessages != null) {
                throw new IllegalOrphanException(illegalOrphanMessages);
            }
            Collection<Prestamo> attachedPrestamoCollectionNew = new ArrayList<Prestamo>();
            for (Prestamo prestamoCollectionNewPrestamoToAttach : prestamoCollectionNew) {
                prestamoCollectionNewPrestamoToAttach = em.getReference(prestamoCollectionNewPrestamoToAttach.getClass(), prestamoCollectionNewPrestamoToAttach.getCodPrestamo());
                attachedPrestamoCollectionNew.add(prestamoCollectionNewPrestamoToAttach);
            }
            prestamoCollectionNew = attachedPrestamoCollectionNew;
            administrativo.setPrestamoCollection(prestamoCollectionNew);
            Collection<Herramienta> attachedHerramientaCollectionNew = new ArrayList<Herramienta>();
            for (Herramienta herramientaCollectionNewHerramientaToAttach : herramientaCollectionNew) {
                herramientaCollectionNewHerramientaToAttach = em.getReference(herramientaCollectionNewHerramientaToAttach.getClass(), herramientaCollectionNewHerramientaToAttach.getIdHerramienta());
                attachedHerramientaCollectionNew.add(herramientaCollectionNewHerramientaToAttach);
            }
            herramientaCollectionNew = attachedHerramientaCollectionNew;
            administrativo.setHerramientaCollection(herramientaCollectionNew);
            administrativo = em.merge(administrativo);
            for (Prestamo prestamoCollectionNewPrestamo : prestamoCollectionNew) {
                if (!prestamoCollectionOld.contains(prestamoCollectionNewPrestamo)) {
                    Administrativo oldIdAdministrativoOfPrestamoCollectionNewPrestamo = prestamoCollectionNewPrestamo.getIdAdministrativo();
                    prestamoCollectionNewPrestamo.setIdAdministrativo(administrativo);
                    prestamoCollectionNewPrestamo = em.merge(prestamoCollectionNewPrestamo);
                    if (oldIdAdministrativoOfPrestamoCollectionNewPrestamo != null && !oldIdAdministrativoOfPrestamoCollectionNewPrestamo.equals(administrativo)) {
                        oldIdAdministrativoOfPrestamoCollectionNewPrestamo.getPrestamoCollection().remove(prestamoCollectionNewPrestamo);
                        oldIdAdministrativoOfPrestamoCollectionNewPrestamo = em.merge(oldIdAdministrativoOfPrestamoCollectionNewPrestamo);
                    }
                }
            }
            for (Herramienta herramientaCollectionNewHerramienta : herramientaCollectionNew) {
                if (!herramientaCollectionOld.contains(herramientaCollectionNewHerramienta)) {
                    Administrativo oldIdAdministrativoOfHerramientaCollectionNewHerramienta = herramientaCollectionNewHerramienta.getIdAdministrativo();
                    herramientaCollectionNewHerramienta.setIdAdministrativo(administrativo);
                    herramientaCollectionNewHerramienta = em.merge(herramientaCollectionNewHerramienta);
                    if (oldIdAdministrativoOfHerramientaCollectionNewHerramienta != null && !oldIdAdministrativoOfHerramientaCollectionNewHerramienta.equals(administrativo)) {
                        oldIdAdministrativoOfHerramientaCollectionNewHerramienta.getHerramientaCollection().remove(herramientaCollectionNewHerramienta);
                        oldIdAdministrativoOfHerramientaCollectionNewHerramienta = em.merge(oldIdAdministrativoOfHerramientaCollectionNewHerramienta);
                    }
                }
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
                String id = administrativo.getIdAdministrativo();
                if (findAdministrativo(id) == null) {
                    throw new NonexistentEntityException("The administrativo with id " + id + " no longer exists.");
                }
            }
            throw ex;
        } finally {
            if (em != null) {
                em.close();
            }
        }
    }

    public void destroy(String id) throws IllegalOrphanException, NonexistentEntityException, RollbackFailureException, Exception {
        EntityManager em = null;
        try {
            utx.begin();
            em = getEntityManager();
            Administrativo administrativo;
            try {
                administrativo = em.getReference(Administrativo.class, id);
                administrativo.getIdAdministrativo();
            } catch (EntityNotFoundException enfe) {
                throw new NonexistentEntityException("The administrativo with id " + id + " no longer exists.", enfe);
            }
            List<String> illegalOrphanMessages = null;
            Collection<Prestamo> prestamoCollectionOrphanCheck = administrativo.getPrestamoCollection();
            for (Prestamo prestamoCollectionOrphanCheckPrestamo : prestamoCollectionOrphanCheck) {
                if (illegalOrphanMessages == null) {
                    illegalOrphanMessages = new ArrayList<String>();
                }
                illegalOrphanMessages.add("This Administrativo (" + administrativo + ") cannot be destroyed since the Prestamo " + prestamoCollectionOrphanCheckPrestamo + " in its prestamoCollection field has a non-nullable idAdministrativo field.");
            }
            Collection<Herramienta> herramientaCollectionOrphanCheck = administrativo.getHerramientaCollection();
            for (Herramienta herramientaCollectionOrphanCheckHerramienta : herramientaCollectionOrphanCheck) {
                if (illegalOrphanMessages == null) {
                    illegalOrphanMessages = new ArrayList<String>();
                }
                illegalOrphanMessages.add("This Administrativo (" + administrativo + ") cannot be destroyed since the Herramienta " + herramientaCollectionOrphanCheckHerramienta + " in its herramientaCollection field has a non-nullable idAdministrativo field.");
            }
            if (illegalOrphanMessages != null) {
                throw new IllegalOrphanException(illegalOrphanMessages);
            }
            em.remove(administrativo);
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

    public List<Administrativo> findAdministrativoEntities() {
        return findAdministrativoEntities(true, -1, -1);
    }

    public List<Administrativo> findAdministrativoEntities(int maxResults, int firstResult) {
        return findAdministrativoEntities(false, maxResults, firstResult);
    }

    private List<Administrativo> findAdministrativoEntities(boolean all, int maxResults, int firstResult) {
        EntityManager em = getEntityManager();
        try {
            CriteriaQuery cq = em.getCriteriaBuilder().createQuery();
            cq.select(cq.from(Administrativo.class));
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

    public Administrativo findAdministrativo(String id) {
        EntityManager em = getEntityManager();
        try {
            return em.find(Administrativo.class, id);
        } finally {
            em.close();
        }
    }

    public int getAdministrativoCount() {
        EntityManager em = getEntityManager();
        try {
            CriteriaQuery cq = em.getCriteriaBuilder().createQuery();
            Root<Administrativo> rt = cq.from(Administrativo.class);
            cq.select(em.getCriteriaBuilder().count(rt));
            Query q = em.createQuery(cq);
            return ((Long) q.getSingleResult()).intValue();
        } finally {
            em.close();
        }
    }
    
}
