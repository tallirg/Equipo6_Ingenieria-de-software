/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package control;

import control.exceptions.IllegalOrphanException;
import control.exceptions.NonexistentEntityException;
import java.io.Serializable;
import javax.persistence.Query;
import javax.persistence.EntityNotFoundException;
import javax.persistence.criteria.CriteriaQuery;
import javax.persistence.criteria.Root;
import modelo.Rol;
import modelo.Notificaciones;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import javax.persistence.EntityManager;
import javax.persistence.EntityManagerFactory;
import modelo.Pedidos;
import modelo.Usuario;

/**
 *
 * @author Talli
 */
public class UsuarioJpaController implements Serializable {

    public UsuarioJpaController(EntityManagerFactory emf) {
        this.emf = emf;
    }
    private EntityManagerFactory emf = null;

    public EntityManager getEntityManager() {
        return emf.createEntityManager();
    }

    public void create(Usuario usuario) {
        if (usuario.getNotificacionesCollection() == null) {
            usuario.setNotificacionesCollection(new ArrayList<Notificaciones>());
        }
        if (usuario.getPedidosCollection() == null) {
            usuario.setPedidosCollection(new ArrayList<Pedidos>());
        }
        EntityManager em = null;
        try {
            em = getEntityManager();
            em.getTransaction().begin();
            Rol idRol = usuario.getIdRol();
            if (idRol != null) {
                idRol = em.getReference(idRol.getClass(), idRol.getIdRol());
                usuario.setIdRol(idRol);
            }
            Collection<Notificaciones> attachedNotificacionesCollection = new ArrayList<Notificaciones>();
            for (Notificaciones notificacionesCollectionNotificacionesToAttach : usuario.getNotificacionesCollection()) {
                notificacionesCollectionNotificacionesToAttach = em.getReference(notificacionesCollectionNotificacionesToAttach.getClass(), notificacionesCollectionNotificacionesToAttach.getIdNotificacion());
                attachedNotificacionesCollection.add(notificacionesCollectionNotificacionesToAttach);
            }
            usuario.setNotificacionesCollection(attachedNotificacionesCollection);
            Collection<Pedidos> attachedPedidosCollection = new ArrayList<Pedidos>();
            for (Pedidos pedidosCollectionPedidosToAttach : usuario.getPedidosCollection()) {
                pedidosCollectionPedidosToAttach = em.getReference(pedidosCollectionPedidosToAttach.getClass(), pedidosCollectionPedidosToAttach.getIdPedido());
                attachedPedidosCollection.add(pedidosCollectionPedidosToAttach);
            }
            usuario.setPedidosCollection(attachedPedidosCollection);
            em.persist(usuario);
            if (idRol != null) {
                idRol.getUsuarioCollection().add(usuario);
                idRol = em.merge(idRol);
            }
            for (Notificaciones notificacionesCollectionNotificaciones : usuario.getNotificacionesCollection()) {
                Usuario oldIdUsuarioOfNotificacionesCollectionNotificaciones = notificacionesCollectionNotificaciones.getIdUsuario();
                notificacionesCollectionNotificaciones.setIdUsuario(usuario);
                notificacionesCollectionNotificaciones = em.merge(notificacionesCollectionNotificaciones);
                if (oldIdUsuarioOfNotificacionesCollectionNotificaciones != null) {
                    oldIdUsuarioOfNotificacionesCollectionNotificaciones.getNotificacionesCollection().remove(notificacionesCollectionNotificaciones);
                    oldIdUsuarioOfNotificacionesCollectionNotificaciones = em.merge(oldIdUsuarioOfNotificacionesCollectionNotificaciones);
                }
            }
            for (Pedidos pedidosCollectionPedidos : usuario.getPedidosCollection()) {
                Usuario oldIdUsuarioOfPedidosCollectionPedidos = pedidosCollectionPedidos.getIdUsuario();
                pedidosCollectionPedidos.setIdUsuario(usuario);
                pedidosCollectionPedidos = em.merge(pedidosCollectionPedidos);
                if (oldIdUsuarioOfPedidosCollectionPedidos != null) {
                    oldIdUsuarioOfPedidosCollectionPedidos.getPedidosCollection().remove(pedidosCollectionPedidos);
                    oldIdUsuarioOfPedidosCollectionPedidos = em.merge(oldIdUsuarioOfPedidosCollectionPedidos);
                }
            }
            em.getTransaction().commit();
        } finally {
            if (em != null) {
                em.close();
            }
        }
    }

    public void edit(Usuario usuario) throws IllegalOrphanException, NonexistentEntityException, Exception {
        EntityManager em = null;
        try {
            em = getEntityManager();
            em.getTransaction().begin();
            Usuario persistentUsuario = em.find(Usuario.class, usuario.getIdUsuario());
            Rol idRolOld = persistentUsuario.getIdRol();
            Rol idRolNew = usuario.getIdRol();
            Collection<Notificaciones> notificacionesCollectionOld = persistentUsuario.getNotificacionesCollection();
            Collection<Notificaciones> notificacionesCollectionNew = usuario.getNotificacionesCollection();
            Collection<Pedidos> pedidosCollectionOld = persistentUsuario.getPedidosCollection();
            Collection<Pedidos> pedidosCollectionNew = usuario.getPedidosCollection();
            List<String> illegalOrphanMessages = null;
            for (Notificaciones notificacionesCollectionOldNotificaciones : notificacionesCollectionOld) {
                if (!notificacionesCollectionNew.contains(notificacionesCollectionOldNotificaciones)) {
                    if (illegalOrphanMessages == null) {
                        illegalOrphanMessages = new ArrayList<String>();
                    }
                    illegalOrphanMessages.add("You must retain Notificaciones " + notificacionesCollectionOldNotificaciones + " since its idUsuario field is not nullable.");
                }
            }
            for (Pedidos pedidosCollectionOldPedidos : pedidosCollectionOld) {
                if (!pedidosCollectionNew.contains(pedidosCollectionOldPedidos)) {
                    if (illegalOrphanMessages == null) {
                        illegalOrphanMessages = new ArrayList<String>();
                    }
                    illegalOrphanMessages.add("You must retain Pedidos " + pedidosCollectionOldPedidos + " since its idUsuario field is not nullable.");
                }
            }
            if (illegalOrphanMessages != null) {
                throw new IllegalOrphanException(illegalOrphanMessages);
            }
            if (idRolNew != null) {
                idRolNew = em.getReference(idRolNew.getClass(), idRolNew.getIdRol());
                usuario.setIdRol(idRolNew);
            }
            Collection<Notificaciones> attachedNotificacionesCollectionNew = new ArrayList<Notificaciones>();
            for (Notificaciones notificacionesCollectionNewNotificacionesToAttach : notificacionesCollectionNew) {
                notificacionesCollectionNewNotificacionesToAttach = em.getReference(notificacionesCollectionNewNotificacionesToAttach.getClass(), notificacionesCollectionNewNotificacionesToAttach.getIdNotificacion());
                attachedNotificacionesCollectionNew.add(notificacionesCollectionNewNotificacionesToAttach);
            }
            notificacionesCollectionNew = attachedNotificacionesCollectionNew;
            usuario.setNotificacionesCollection(notificacionesCollectionNew);
            Collection<Pedidos> attachedPedidosCollectionNew = new ArrayList<Pedidos>();
            for (Pedidos pedidosCollectionNewPedidosToAttach : pedidosCollectionNew) {
                pedidosCollectionNewPedidosToAttach = em.getReference(pedidosCollectionNewPedidosToAttach.getClass(), pedidosCollectionNewPedidosToAttach.getIdPedido());
                attachedPedidosCollectionNew.add(pedidosCollectionNewPedidosToAttach);
            }
            pedidosCollectionNew = attachedPedidosCollectionNew;
            usuario.setPedidosCollection(pedidosCollectionNew);
            usuario = em.merge(usuario);
            if (idRolOld != null && !idRolOld.equals(idRolNew)) {
                idRolOld.getUsuarioCollection().remove(usuario);
                idRolOld = em.merge(idRolOld);
            }
            if (idRolNew != null && !idRolNew.equals(idRolOld)) {
                idRolNew.getUsuarioCollection().add(usuario);
                idRolNew = em.merge(idRolNew);
            }
            for (Notificaciones notificacionesCollectionNewNotificaciones : notificacionesCollectionNew) {
                if (!notificacionesCollectionOld.contains(notificacionesCollectionNewNotificaciones)) {
                    Usuario oldIdUsuarioOfNotificacionesCollectionNewNotificaciones = notificacionesCollectionNewNotificaciones.getIdUsuario();
                    notificacionesCollectionNewNotificaciones.setIdUsuario(usuario);
                    notificacionesCollectionNewNotificaciones = em.merge(notificacionesCollectionNewNotificaciones);
                    if (oldIdUsuarioOfNotificacionesCollectionNewNotificaciones != null && !oldIdUsuarioOfNotificacionesCollectionNewNotificaciones.equals(usuario)) {
                        oldIdUsuarioOfNotificacionesCollectionNewNotificaciones.getNotificacionesCollection().remove(notificacionesCollectionNewNotificaciones);
                        oldIdUsuarioOfNotificacionesCollectionNewNotificaciones = em.merge(oldIdUsuarioOfNotificacionesCollectionNewNotificaciones);
                    }
                }
            }
            for (Pedidos pedidosCollectionNewPedidos : pedidosCollectionNew) {
                if (!pedidosCollectionOld.contains(pedidosCollectionNewPedidos)) {
                    Usuario oldIdUsuarioOfPedidosCollectionNewPedidos = pedidosCollectionNewPedidos.getIdUsuario();
                    pedidosCollectionNewPedidos.setIdUsuario(usuario);
                    pedidosCollectionNewPedidos = em.merge(pedidosCollectionNewPedidos);
                    if (oldIdUsuarioOfPedidosCollectionNewPedidos != null && !oldIdUsuarioOfPedidosCollectionNewPedidos.equals(usuario)) {
                        oldIdUsuarioOfPedidosCollectionNewPedidos.getPedidosCollection().remove(pedidosCollectionNewPedidos);
                        oldIdUsuarioOfPedidosCollectionNewPedidos = em.merge(oldIdUsuarioOfPedidosCollectionNewPedidos);
                    }
                }
            }
            em.getTransaction().commit();
        } catch (Exception ex) {
            String msg = ex.getLocalizedMessage();
            if (msg == null || msg.length() == 0) {
                Integer id = usuario.getIdUsuario();
                if (findUsuario(id) == null) {
                    throw new NonexistentEntityException("The usuario with id " + id + " no longer exists.");
                }
            }
            throw ex;
        } finally {
            if (em != null) {
                em.close();
            }
        }
    }

    public void destroy(Integer id) throws IllegalOrphanException, NonexistentEntityException {
        EntityManager em = null;
        try {
            em = getEntityManager();
            em.getTransaction().begin();
            Usuario usuario;
            try {
                usuario = em.getReference(Usuario.class, id);
                usuario.getIdUsuario();
            } catch (EntityNotFoundException enfe) {
                throw new NonexistentEntityException("The usuario with id " + id + " no longer exists.", enfe);
            }
            List<String> illegalOrphanMessages = null;
            Collection<Notificaciones> notificacionesCollectionOrphanCheck = usuario.getNotificacionesCollection();
            for (Notificaciones notificacionesCollectionOrphanCheckNotificaciones : notificacionesCollectionOrphanCheck) {
                if (illegalOrphanMessages == null) {
                    illegalOrphanMessages = new ArrayList<String>();
                }
                illegalOrphanMessages.add("This Usuario (" + usuario + ") cannot be destroyed since the Notificaciones " + notificacionesCollectionOrphanCheckNotificaciones + " in its notificacionesCollection field has a non-nullable idUsuario field.");
            }
            Collection<Pedidos> pedidosCollectionOrphanCheck = usuario.getPedidosCollection();
            for (Pedidos pedidosCollectionOrphanCheckPedidos : pedidosCollectionOrphanCheck) {
                if (illegalOrphanMessages == null) {
                    illegalOrphanMessages = new ArrayList<String>();
                }
                illegalOrphanMessages.add("This Usuario (" + usuario + ") cannot be destroyed since the Pedidos " + pedidosCollectionOrphanCheckPedidos + " in its pedidosCollection field has a non-nullable idUsuario field.");
            }
            if (illegalOrphanMessages != null) {
                throw new IllegalOrphanException(illegalOrphanMessages);
            }
            Rol idRol = usuario.getIdRol();
            if (idRol != null) {
                idRol.getUsuarioCollection().remove(usuario);
                idRol = em.merge(idRol);
            }
            em.remove(usuario);
            em.getTransaction().commit();
        } finally {
            if (em != null) {
                em.close();
            }
        }
    }

    public List<Usuario> findUsuarioEntities() {
        return findUsuarioEntities(true, -1, -1);
    }

    public List<Usuario> findUsuarioEntities(int maxResults, int firstResult) {
        return findUsuarioEntities(false, maxResults, firstResult);
    }

    private List<Usuario> findUsuarioEntities(boolean all, int maxResults, int firstResult) {
        EntityManager em = getEntityManager();
        try {
            CriteriaQuery cq = em.getCriteriaBuilder().createQuery();
            cq.select(cq.from(Usuario.class));
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

    public Usuario findUsuario(Integer id) {
        EntityManager em = getEntityManager();
        try {
            return em.find(Usuario.class, id);
        } finally {
            em.close();
        }
    }

    public int getUsuarioCount() {
        EntityManager em = getEntityManager();
        try {
            CriteriaQuery cq = em.getCriteriaBuilder().createQuery();
            Root<Usuario> rt = cq.from(Usuario.class);
            cq.select(em.getCriteriaBuilder().count(rt));
            Query q = em.createQuery(cq);
            return ((Long) q.getSingleResult()).intValue();
        } finally {
            em.close();
        }
    }
    
}
