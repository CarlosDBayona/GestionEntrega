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
import java.io.Serializable;
import javax.persistence.Query;
import javax.persistence.EntityNotFoundException;
import javax.persistence.criteria.CriteriaQuery;
import javax.persistence.criteria.Root;
import Datos.Administrativo;
import Datos.Herramienta;
import Datos.Transaccion;
import java.util.ArrayList;
import java.util.Collection;
import Datos.Mantenimiento;
import java.util.List;
import javax.persistence.EntityManager;
import javax.persistence.EntityManagerFactory;
import javax.transaction.UserTransaction;

/**
 *
 * @author Carlos
 */
public class HerramientaJpaController implements Serializable {

    public HerramientaJpaController(UserTransaction utx, EntityManagerFactory emf) {
        this.utx = utx;
        this.emf = emf;
    }
    private UserTransaction utx = null;
    private EntityManagerFactory emf = null;

    public EntityManager getEntityManager() {
        return emf.createEntityManager();
    }

    public void create(Herramienta herramienta) throws PreexistingEntityException, RollbackFailureException, Exception {
        if (herramienta.getTransaccionCollection() == null) {
            herramienta.setTransaccionCollection(new ArrayList<Transaccion>());
        }
        if (herramienta.getMantenimientoCollection() == null) {
            herramienta.setMantenimientoCollection(new ArrayList<Mantenimiento>());
        }
        EntityManager em = null;
        try {
            utx.begin();
            em = getEntityManager();
            Administrativo idAdministrativo = herramienta.getIdAdministrativo();
            if (idAdministrativo != null) {
                idAdministrativo = em.getReference(idAdministrativo.getClass(), idAdministrativo.getIdAdministrativo());
                herramienta.setIdAdministrativo(idAdministrativo);
            }
            Collection<Transaccion> attachedTransaccionCollection = new ArrayList<Transaccion>();
            for (Transaccion transaccionCollectionTransaccionToAttach : herramienta.getTransaccionCollection()) {
                transaccionCollectionTransaccionToAttach = em.getReference(transaccionCollectionTransaccionToAttach.getClass(), transaccionCollectionTransaccionToAttach.getIdTransaccion());
                attachedTransaccionCollection.add(transaccionCollectionTransaccionToAttach);
            }
            herramienta.setTransaccionCollection(attachedTransaccionCollection);
            Collection<Mantenimiento> attachedMantenimientoCollection = new ArrayList<Mantenimiento>();
            for (Mantenimiento mantenimientoCollectionMantenimientoToAttach : herramienta.getMantenimientoCollection()) {
                mantenimientoCollectionMantenimientoToAttach = em.getReference(mantenimientoCollectionMantenimientoToAttach.getClass(), mantenimientoCollectionMantenimientoToAttach.getIdMantenimiento());
                attachedMantenimientoCollection.add(mantenimientoCollectionMantenimientoToAttach);
            }
            herramienta.setMantenimientoCollection(attachedMantenimientoCollection);
            em.persist(herramienta);
            if (idAdministrativo != null) {
                idAdministrativo.getHerramientaCollection().add(herramienta);
                idAdministrativo = em.merge(idAdministrativo);
            }
            for (Transaccion transaccionCollectionTransaccion : herramienta.getTransaccionCollection()) {
                Herramienta oldIdHerramientaOfTransaccionCollectionTransaccion = transaccionCollectionTransaccion.getIdHerramienta();
                transaccionCollectionTransaccion.setIdHerramienta(herramienta);
                transaccionCollectionTransaccion = em.merge(transaccionCollectionTransaccion);
                if (oldIdHerramientaOfTransaccionCollectionTransaccion != null) {
                    oldIdHerramientaOfTransaccionCollectionTransaccion.getTransaccionCollection().remove(transaccionCollectionTransaccion);
                    oldIdHerramientaOfTransaccionCollectionTransaccion = em.merge(oldIdHerramientaOfTransaccionCollectionTransaccion);
                }
            }
            for (Mantenimiento mantenimientoCollectionMantenimiento : herramienta.getMantenimientoCollection()) {
                Herramienta oldIdHerramientaOfMantenimientoCollectionMantenimiento = mantenimientoCollectionMantenimiento.getIdHerramienta();
                mantenimientoCollectionMantenimiento.setIdHerramienta(herramienta);
                mantenimientoCollectionMantenimiento = em.merge(mantenimientoCollectionMantenimiento);
                if (oldIdHerramientaOfMantenimientoCollectionMantenimiento != null) {
                    oldIdHerramientaOfMantenimientoCollectionMantenimiento.getMantenimientoCollection().remove(mantenimientoCollectionMantenimiento);
                    oldIdHerramientaOfMantenimientoCollectionMantenimiento = em.merge(oldIdHerramientaOfMantenimientoCollectionMantenimiento);
                }
            }
            utx.commit();
        } catch (Exception ex) {
            try {
                utx.rollback();
            } catch (Exception re) {
                throw new RollbackFailureException("An error occurred attempting to roll back the transaction.", re);
            }
            if (findHerramienta(herramienta.getIdHerramienta()) != null) {
                throw new PreexistingEntityException("Herramienta " + herramienta + " already exists.", ex);
            }
            throw ex;
        } finally {
            if (em != null) {
                em.close();
            }
        }
    }

    public void edit(Herramienta herramienta) throws IllegalOrphanException, NonexistentEntityException, RollbackFailureException, Exception {
        EntityManager em = null;
        try {
            utx.begin();
            em = getEntityManager();
            Herramienta persistentHerramienta = em.find(Herramienta.class, herramienta.getIdHerramienta());
            Administrativo idAdministrativoOld = persistentHerramienta.getIdAdministrativo();
            Administrativo idAdministrativoNew = herramienta.getIdAdministrativo();
            Collection<Transaccion> transaccionCollectionOld = persistentHerramienta.getTransaccionCollection();
            Collection<Transaccion> transaccionCollectionNew = herramienta.getTransaccionCollection();
            Collection<Mantenimiento> mantenimientoCollectionOld = persistentHerramienta.getMantenimientoCollection();
            Collection<Mantenimiento> mantenimientoCollectionNew = herramienta.getMantenimientoCollection();
            List<String> illegalOrphanMessages = null;
            for (Transaccion transaccionCollectionOldTransaccion : transaccionCollectionOld) {
                if (!transaccionCollectionNew.contains(transaccionCollectionOldTransaccion)) {
                    if (illegalOrphanMessages == null) {
                        illegalOrphanMessages = new ArrayList<String>();
                    }
                    illegalOrphanMessages.add("You must retain Transaccion " + transaccionCollectionOldTransaccion + " since its idHerramienta field is not nullable.");
                }
            }
            for (Mantenimiento mantenimientoCollectionOldMantenimiento : mantenimientoCollectionOld) {
                if (!mantenimientoCollectionNew.contains(mantenimientoCollectionOldMantenimiento)) {
                    if (illegalOrphanMessages == null) {
                        illegalOrphanMessages = new ArrayList<String>();
                    }
                    illegalOrphanMessages.add("You must retain Mantenimiento " + mantenimientoCollectionOldMantenimiento + " since its idHerramienta field is not nullable.");
                }
            }
            if (illegalOrphanMessages != null) {
                throw new IllegalOrphanException(illegalOrphanMessages);
            }
            if (idAdministrativoNew != null) {
                idAdministrativoNew = em.getReference(idAdministrativoNew.getClass(), idAdministrativoNew.getIdAdministrativo());
                herramienta.setIdAdministrativo(idAdministrativoNew);
            }
            Collection<Transaccion> attachedTransaccionCollectionNew = new ArrayList<Transaccion>();
            for (Transaccion transaccionCollectionNewTransaccionToAttach : transaccionCollectionNew) {
                transaccionCollectionNewTransaccionToAttach = em.getReference(transaccionCollectionNewTransaccionToAttach.getClass(), transaccionCollectionNewTransaccionToAttach.getIdTransaccion());
                attachedTransaccionCollectionNew.add(transaccionCollectionNewTransaccionToAttach);
            }
            transaccionCollectionNew = attachedTransaccionCollectionNew;
            herramienta.setTransaccionCollection(transaccionCollectionNew);
            Collection<Mantenimiento> attachedMantenimientoCollectionNew = new ArrayList<Mantenimiento>();
            for (Mantenimiento mantenimientoCollectionNewMantenimientoToAttach : mantenimientoCollectionNew) {
                mantenimientoCollectionNewMantenimientoToAttach = em.getReference(mantenimientoCollectionNewMantenimientoToAttach.getClass(), mantenimientoCollectionNewMantenimientoToAttach.getIdMantenimiento());
                attachedMantenimientoCollectionNew.add(mantenimientoCollectionNewMantenimientoToAttach);
            }
            mantenimientoCollectionNew = attachedMantenimientoCollectionNew;
            herramienta.setMantenimientoCollection(mantenimientoCollectionNew);
            herramienta = em.merge(herramienta);
            if (idAdministrativoOld != null && !idAdministrativoOld.equals(idAdministrativoNew)) {
                idAdministrativoOld.getHerramientaCollection().remove(herramienta);
                idAdministrativoOld = em.merge(idAdministrativoOld);
            }
            if (idAdministrativoNew != null && !idAdministrativoNew.equals(idAdministrativoOld)) {
                idAdministrativoNew.getHerramientaCollection().add(herramienta);
                idAdministrativoNew = em.merge(idAdministrativoNew);
            }
            for (Transaccion transaccionCollectionNewTransaccion : transaccionCollectionNew) {
                if (!transaccionCollectionOld.contains(transaccionCollectionNewTransaccion)) {
                    Herramienta oldIdHerramientaOfTransaccionCollectionNewTransaccion = transaccionCollectionNewTransaccion.getIdHerramienta();
                    transaccionCollectionNewTransaccion.setIdHerramienta(herramienta);
                    transaccionCollectionNewTransaccion = em.merge(transaccionCollectionNewTransaccion);
                    if (oldIdHerramientaOfTransaccionCollectionNewTransaccion != null && !oldIdHerramientaOfTransaccionCollectionNewTransaccion.equals(herramienta)) {
                        oldIdHerramientaOfTransaccionCollectionNewTransaccion.getTransaccionCollection().remove(transaccionCollectionNewTransaccion);
                        oldIdHerramientaOfTransaccionCollectionNewTransaccion = em.merge(oldIdHerramientaOfTransaccionCollectionNewTransaccion);
                    }
                }
            }
            for (Mantenimiento mantenimientoCollectionNewMantenimiento : mantenimientoCollectionNew) {
                if (!mantenimientoCollectionOld.contains(mantenimientoCollectionNewMantenimiento)) {
                    Herramienta oldIdHerramientaOfMantenimientoCollectionNewMantenimiento = mantenimientoCollectionNewMantenimiento.getIdHerramienta();
                    mantenimientoCollectionNewMantenimiento.setIdHerramienta(herramienta);
                    mantenimientoCollectionNewMantenimiento = em.merge(mantenimientoCollectionNewMantenimiento);
                    if (oldIdHerramientaOfMantenimientoCollectionNewMantenimiento != null && !oldIdHerramientaOfMantenimientoCollectionNewMantenimiento.equals(herramienta)) {
                        oldIdHerramientaOfMantenimientoCollectionNewMantenimiento.getMantenimientoCollection().remove(mantenimientoCollectionNewMantenimiento);
                        oldIdHerramientaOfMantenimientoCollectionNewMantenimiento = em.merge(oldIdHerramientaOfMantenimientoCollectionNewMantenimiento);
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
                String id = herramienta.getIdHerramienta();
                if (findHerramienta(id) == null) {
                    throw new NonexistentEntityException("The herramienta with id " + id + " no longer exists.");
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
            Herramienta herramienta;
            try {
                herramienta = em.getReference(Herramienta.class, id);
                herramienta.getIdHerramienta();
            } catch (EntityNotFoundException enfe) {
                throw new NonexistentEntityException("The herramienta with id " + id + " no longer exists.", enfe);
            }
            List<String> illegalOrphanMessages = null;
            Collection<Transaccion> transaccionCollectionOrphanCheck = herramienta.getTransaccionCollection();
            for (Transaccion transaccionCollectionOrphanCheckTransaccion : transaccionCollectionOrphanCheck) {
                if (illegalOrphanMessages == null) {
                    illegalOrphanMessages = new ArrayList<String>();
                }
                illegalOrphanMessages.add("This Herramienta (" + herramienta + ") cannot be destroyed since the Transaccion " + transaccionCollectionOrphanCheckTransaccion + " in its transaccionCollection field has a non-nullable idHerramienta field.");
            }
            Collection<Mantenimiento> mantenimientoCollectionOrphanCheck = herramienta.getMantenimientoCollection();
            for (Mantenimiento mantenimientoCollectionOrphanCheckMantenimiento : mantenimientoCollectionOrphanCheck) {
                if (illegalOrphanMessages == null) {
                    illegalOrphanMessages = new ArrayList<String>();
                }
                illegalOrphanMessages.add("This Herramienta (" + herramienta + ") cannot be destroyed since the Mantenimiento " + mantenimientoCollectionOrphanCheckMantenimiento + " in its mantenimientoCollection field has a non-nullable idHerramienta field.");
            }
            if (illegalOrphanMessages != null) {
                throw new IllegalOrphanException(illegalOrphanMessages);
            }
            Administrativo idAdministrativo = herramienta.getIdAdministrativo();
            if (idAdministrativo != null) {
                idAdministrativo.getHerramientaCollection().remove(herramienta);
                idAdministrativo = em.merge(idAdministrativo);
            }
            em.remove(herramienta);
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

    public List<Herramienta> findHerramientaEntities() {
        return findHerramientaEntities(true, -1, -1);
    }

    public List<Herramienta> findHerramientaEntities(int maxResults, int firstResult) {
        return findHerramientaEntities(false, maxResults, firstResult);
    }

    private List<Herramienta> findHerramientaEntities(boolean all, int maxResults, int firstResult) {
        EntityManager em = getEntityManager();
        try {
            CriteriaQuery cq = em.getCriteriaBuilder().createQuery();
            cq.select(cq.from(Herramienta.class));
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

    public Herramienta findHerramienta(String id) {
        EntityManager em = getEntityManager();
        try {
            return em.find(Herramienta.class, id);
        } finally {
            em.close();
        }
    }

    public int getHerramientaCount() {
        EntityManager em = getEntityManager();
        try {
            CriteriaQuery cq = em.getCriteriaBuilder().createQuery();
            Root<Herramienta> rt = cq.from(Herramienta.class);
            cq.select(em.getCriteriaBuilder().count(rt));
            Query q = em.createQuery(cq);
            return ((Long) q.getSingleResult()).intValue();
        } finally {
            em.close();
        }
    }
    
}
