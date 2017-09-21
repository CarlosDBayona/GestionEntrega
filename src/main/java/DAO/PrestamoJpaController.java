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
import Datos.Usuario;
import Datos.Administrativo;
import Datos.Prestamo;
import Datos.Transaccion;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import javax.persistence.EntityManager;
import javax.persistence.EntityManagerFactory;
import javax.transaction.UserTransaction;

/**
 *
 * @author Carlos
 */
public class PrestamoJpaController implements Serializable {

    public PrestamoJpaController(UserTransaction utx, EntityManagerFactory emf) {
        this.utx = utx;
        this.emf = emf;
    }
    private UserTransaction utx = null;
    private EntityManagerFactory emf = null;

    public EntityManager getEntityManager() {
        return emf.createEntityManager();
    }

    public void create(Prestamo prestamo) throws PreexistingEntityException, RollbackFailureException, Exception {
        if (prestamo.getTransaccionCollection() == null) {
            prestamo.setTransaccionCollection(new ArrayList<Transaccion>());
        }
        EntityManager em = null;
        try {
            utx.begin();
            em = getEntityManager();
            Usuario idUsuario = prestamo.getIdUsuario();
            if (idUsuario != null) {
                idUsuario = em.getReference(idUsuario.getClass(), idUsuario.getIdUsuario());
                prestamo.setIdUsuario(idUsuario);
            }
            Administrativo idAdministrativo = prestamo.getIdAdministrativo();
            if (idAdministrativo != null) {
                idAdministrativo = em.getReference(idAdministrativo.getClass(), idAdministrativo.getIdAdministrativo());
                prestamo.setIdAdministrativo(idAdministrativo);
            }
            Collection<Transaccion> attachedTransaccionCollection = new ArrayList<Transaccion>();
            for (Transaccion transaccionCollectionTransaccionToAttach : prestamo.getTransaccionCollection()) {
                transaccionCollectionTransaccionToAttach = em.getReference(transaccionCollectionTransaccionToAttach.getClass(), transaccionCollectionTransaccionToAttach.getIdTransaccion());
                attachedTransaccionCollection.add(transaccionCollectionTransaccionToAttach);
            }
            prestamo.setTransaccionCollection(attachedTransaccionCollection);
            em.persist(prestamo);
            if (idUsuario != null) {
                idUsuario.getPrestamoCollection().add(prestamo);
                idUsuario = em.merge(idUsuario);
            }
            if (idAdministrativo != null) {
                idAdministrativo.getPrestamoCollection().add(prestamo);
                idAdministrativo = em.merge(idAdministrativo);
            }
            for (Transaccion transaccionCollectionTransaccion : prestamo.getTransaccionCollection()) {
                Prestamo oldCodPrestamoOfTransaccionCollectionTransaccion = transaccionCollectionTransaccion.getCodPrestamo();
                transaccionCollectionTransaccion.setCodPrestamo(prestamo);
                transaccionCollectionTransaccion = em.merge(transaccionCollectionTransaccion);
                if (oldCodPrestamoOfTransaccionCollectionTransaccion != null) {
                    oldCodPrestamoOfTransaccionCollectionTransaccion.getTransaccionCollection().remove(transaccionCollectionTransaccion);
                    oldCodPrestamoOfTransaccionCollectionTransaccion = em.merge(oldCodPrestamoOfTransaccionCollectionTransaccion);
                }
            }
            utx.commit();
        } catch (Exception ex) {
            try {
                utx.rollback();
            } catch (Exception re) {
                throw new RollbackFailureException("An error occurred attempting to roll back the transaction.", re);
            }
            if (findPrestamo(prestamo.getCodPrestamo()) != null) {
                throw new PreexistingEntityException("Prestamo " + prestamo + " already exists.", ex);
            }
            throw ex;
        } finally {
            if (em != null) {
                em.close();
            }
        }
    }

    public void edit(Prestamo prestamo) throws IllegalOrphanException, NonexistentEntityException, RollbackFailureException, Exception {
        EntityManager em = null;
        try {
            utx.begin();
            em = getEntityManager();
            Prestamo persistentPrestamo = em.find(Prestamo.class, prestamo.getCodPrestamo());
            Usuario idUsuarioOld = persistentPrestamo.getIdUsuario();
            Usuario idUsuarioNew = prestamo.getIdUsuario();
            Administrativo idAdministrativoOld = persistentPrestamo.getIdAdministrativo();
            Administrativo idAdministrativoNew = prestamo.getIdAdministrativo();
            Collection<Transaccion> transaccionCollectionOld = persistentPrestamo.getTransaccionCollection();
            Collection<Transaccion> transaccionCollectionNew = prestamo.getTransaccionCollection();
            List<String> illegalOrphanMessages = null;
            for (Transaccion transaccionCollectionOldTransaccion : transaccionCollectionOld) {
                if (!transaccionCollectionNew.contains(transaccionCollectionOldTransaccion)) {
                    if (illegalOrphanMessages == null) {
                        illegalOrphanMessages = new ArrayList<String>();
                    }
                    illegalOrphanMessages.add("You must retain Transaccion " + transaccionCollectionOldTransaccion + " since its codPrestamo field is not nullable.");
                }
            }
            if (illegalOrphanMessages != null) {
                throw new IllegalOrphanException(illegalOrphanMessages);
            }
            if (idUsuarioNew != null) {
                idUsuarioNew = em.getReference(idUsuarioNew.getClass(), idUsuarioNew.getIdUsuario());
                prestamo.setIdUsuario(idUsuarioNew);
            }
            if (idAdministrativoNew != null) {
                idAdministrativoNew = em.getReference(idAdministrativoNew.getClass(), idAdministrativoNew.getIdAdministrativo());
                prestamo.setIdAdministrativo(idAdministrativoNew);
            }
            Collection<Transaccion> attachedTransaccionCollectionNew = new ArrayList<Transaccion>();
            for (Transaccion transaccionCollectionNewTransaccionToAttach : transaccionCollectionNew) {
                transaccionCollectionNewTransaccionToAttach = em.getReference(transaccionCollectionNewTransaccionToAttach.getClass(), transaccionCollectionNewTransaccionToAttach.getIdTransaccion());
                attachedTransaccionCollectionNew.add(transaccionCollectionNewTransaccionToAttach);
            }
            transaccionCollectionNew = attachedTransaccionCollectionNew;
            prestamo.setTransaccionCollection(transaccionCollectionNew);
            prestamo = em.merge(prestamo);
            if (idUsuarioOld != null && !idUsuarioOld.equals(idUsuarioNew)) {
                idUsuarioOld.getPrestamoCollection().remove(prestamo);
                idUsuarioOld = em.merge(idUsuarioOld);
            }
            if (idUsuarioNew != null && !idUsuarioNew.equals(idUsuarioOld)) {
                idUsuarioNew.getPrestamoCollection().add(prestamo);
                idUsuarioNew = em.merge(idUsuarioNew);
            }
            if (idAdministrativoOld != null && !idAdministrativoOld.equals(idAdministrativoNew)) {
                idAdministrativoOld.getPrestamoCollection().remove(prestamo);
                idAdministrativoOld = em.merge(idAdministrativoOld);
            }
            if (idAdministrativoNew != null && !idAdministrativoNew.equals(idAdministrativoOld)) {
                idAdministrativoNew.getPrestamoCollection().add(prestamo);
                idAdministrativoNew = em.merge(idAdministrativoNew);
            }
            for (Transaccion transaccionCollectionNewTransaccion : transaccionCollectionNew) {
                if (!transaccionCollectionOld.contains(transaccionCollectionNewTransaccion)) {
                    Prestamo oldCodPrestamoOfTransaccionCollectionNewTransaccion = transaccionCollectionNewTransaccion.getCodPrestamo();
                    transaccionCollectionNewTransaccion.setCodPrestamo(prestamo);
                    transaccionCollectionNewTransaccion = em.merge(transaccionCollectionNewTransaccion);
                    if (oldCodPrestamoOfTransaccionCollectionNewTransaccion != null && !oldCodPrestamoOfTransaccionCollectionNewTransaccion.equals(prestamo)) {
                        oldCodPrestamoOfTransaccionCollectionNewTransaccion.getTransaccionCollection().remove(transaccionCollectionNewTransaccion);
                        oldCodPrestamoOfTransaccionCollectionNewTransaccion = em.merge(oldCodPrestamoOfTransaccionCollectionNewTransaccion);
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
                String id = prestamo.getCodPrestamo();
                if (findPrestamo(id) == null) {
                    throw new NonexistentEntityException("The prestamo with id " + id + " no longer exists.");
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
            Prestamo prestamo;
            try {
                prestamo = em.getReference(Prestamo.class, id);
                prestamo.getCodPrestamo();
            } catch (EntityNotFoundException enfe) {
                throw new NonexistentEntityException("The prestamo with id " + id + " no longer exists.", enfe);
            }
            List<String> illegalOrphanMessages = null;
            Collection<Transaccion> transaccionCollectionOrphanCheck = prestamo.getTransaccionCollection();
            for (Transaccion transaccionCollectionOrphanCheckTransaccion : transaccionCollectionOrphanCheck) {
                if (illegalOrphanMessages == null) {
                    illegalOrphanMessages = new ArrayList<String>();
                }
                illegalOrphanMessages.add("This Prestamo (" + prestamo + ") cannot be destroyed since the Transaccion " + transaccionCollectionOrphanCheckTransaccion + " in its transaccionCollection field has a non-nullable codPrestamo field.");
            }
            if (illegalOrphanMessages != null) {
                throw new IllegalOrphanException(illegalOrphanMessages);
            }
            Usuario idUsuario = prestamo.getIdUsuario();
            if (idUsuario != null) {
                idUsuario.getPrestamoCollection().remove(prestamo);
                idUsuario = em.merge(idUsuario);
            }
            Administrativo idAdministrativo = prestamo.getIdAdministrativo();
            if (idAdministrativo != null) {
                idAdministrativo.getPrestamoCollection().remove(prestamo);
                idAdministrativo = em.merge(idAdministrativo);
            }
            em.remove(prestamo);
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

    public List<Prestamo> findPrestamoEntities() {
        return findPrestamoEntities(true, -1, -1);
    }

    public List<Prestamo> findPrestamoEntities(int maxResults, int firstResult) {
        return findPrestamoEntities(false, maxResults, firstResult);
    }

    private List<Prestamo> findPrestamoEntities(boolean all, int maxResults, int firstResult) {
        EntityManager em = getEntityManager();
        try {
            CriteriaQuery cq = em.getCriteriaBuilder().createQuery();
            cq.select(cq.from(Prestamo.class));
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

    public Prestamo findPrestamo(String id) {
        EntityManager em = getEntityManager();
        try {
            return em.find(Prestamo.class, id);
        } finally {
            em.close();
        }
    }

    public int getPrestamoCount() {
        EntityManager em = getEntityManager();
        try {
            CriteriaQuery cq = em.getCriteriaBuilder().createQuery();
            Root<Prestamo> rt = cq.from(Prestamo.class);
            cq.select(em.getCriteriaBuilder().count(rt));
            Query q = em.createQuery(cq);
            return ((Long) q.getSingleResult()).intValue();
        } finally {
            em.close();
        }
    }
    
}
