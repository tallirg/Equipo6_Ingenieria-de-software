/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package control;

import control.exceptions.NonexistentEntityException;
import java.io.Serializable;
import java.util.List;
import javax.persistence.EntityManager;
import javax.persistence.EntityManagerFactory;
import javax.persistence.Query;
import javax.persistence.EntityNotFoundException;
import javax.persistence.criteria.CriteriaQuery;
import javax.persistence.criteria.Root;
import modelo.Notificaciones;
import modelo.Usuario;

/**
 *
 * @author Talli
 */
public class NotificacionesJpaController implements Serializable {

    public NotificacionesJpaController(EntityManagerFactory emf) {
        this.emf = emf;
    }
    private EntityManagerFactory emf = null;

    public EntityManager getEntityManager() {
        return emf.createEntityManager();
    }

    public void create(Notificaciones notificaciones) {
        EntityManager em = null;
        try {
            em = getEntityManager();
            em.getTransaction().begin();
            Usuario idUsuario = notificaciones.getIdUsuario();
            if (idUsuario != null) {
                idUsuario = em.getReference(idUsuario.getClass(), idUsuario.getIdUsuario());
                notificaciones.setIdUsuario(idUsuario);
            }
            em.persist(notificaciones);
            if (idUsuario != null) {
                idUsuario.getNotificacionesCollection().add(notificaciones);
                idUsuario = em.merge(idUsuario);
            }
            em.getTransaction().commit();
        } finally {
            if (em != null) {
                em.close();
            }
        }
    }

    public void edit(Notificaciones notificaciones) throws NonexistentEntityException, Exception {
        EntityManager em = null;
        try {
            em = getEntityManager();
            em.getTransaction().begin();
            Notificaciones persistentNotificaciones = em.find(Notificaciones.class, notificaciones.getIdNotificacion());
            Usuario idUsuarioOld = persistentNotificaciones.getIdUsuario();
            Usuario idUsuarioNew = notificaciones.getIdUsuario();
            if (idUsuarioNew != null) {
                idUsuarioNew = em.getReference(idUsuarioNew.getClass(), idUsuarioNew.getIdUsuario());
                notificaciones.setIdUsuario(idUsuarioNew);
            }
            notificaciones = em.merge(notificaciones);
            if (idUsuarioOld != null && !idUsuarioOld.equals(idUsuarioNew)) {
                idUsuarioOld.getNotificacionesCollection().remove(notificaciones);
                idUsuarioOld = em.merge(idUsuarioOld);
            }
            if (idUsuarioNew != null && !idUsuarioNew.equals(idUsuarioOld)) {
                idUsuarioNew.getNotificacionesCollection().add(notificaciones);
                idUsuarioNew = em.merge(idUsuarioNew);
            }
            em.getTransaction().commit();
        } catch (Exception ex) {
            String msg = ex.getLocalizedMessage();
            if (msg == null || msg.length() == 0) {
                Integer id = notificaciones.getIdNotificacion();
                if (findNotificaciones(id) == null) {
                    throw new NonexistentEntityException("The notificaciones with id " + id + " no longer exists.");
                }
            }
            throw ex;
        } finally {
            if (em != null) {
                em.close();
            }
        }
    }

    public void destroy(Integer id) throws NonexistentEntityException {
        EntityManager em = null;
        try {
            em = getEntityManager();
            em.getTransaction().begin();
            Notificaciones notificaciones;
            try {
                notificaciones = em.getReference(Notificaciones.class, id);
                notificaciones.getIdNotificacion();
            } catch (EntityNotFoundException enfe) {
                throw new NonexistentEntityException("The notificaciones with id " + id + " no longer exists.", enfe);
            }
            Usuario idUsuario = notificaciones.getIdUsuario();
            if (idUsuario != null) {
                idUsuario.getNotificacionesCollection().remove(notificaciones);
                idUsuario = em.merge(idUsuario);
            }
            em.remove(notificaciones);
            em.getTransaction().commit();
        } finally {
            if (em != null) {
                em.close();
            }
        }
    }

    public List<Notificaciones> findNotificacionesEntities() {
        return findNotificacionesEntities(true, -1, -1);
    }

    public List<Notificaciones> findNotificacionesEntities(int maxResults, int firstResult) {
        return findNotificacionesEntities(false, maxResults, firstResult);
    }

    private List<Notificaciones> findNotificacionesEntities(boolean all, int maxResults, int firstResult) {
        EntityManager em = getEntityManager();
        try {
            CriteriaQuery cq = em.getCriteriaBuilder().createQuery();
            cq.select(cq.from(Notificaciones.class));
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

    public Notificaciones findNotificaciones(Integer id) {
        EntityManager em = getEntityManager();
        try {
            return em.find(Notificaciones.class, id);
        } finally {
            em.close();
        }
    }

    public int getNotificacionesCount() {
        EntityManager em = getEntityManager();
        try {
            CriteriaQuery cq = em.getCriteriaBuilder().createQuery();
            Root<Notificaciones> rt = cq.from(Notificaciones.class);
            cq.select(em.getCriteriaBuilder().count(rt));
            Query q = em.createQuery(cq);
            return ((Long) q.getSingleResult()).intValue();
        } finally {
            em.close();
        }
    }
    
}
