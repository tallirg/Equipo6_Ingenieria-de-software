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
import modelo.Usuario;
import modelo.PedidoProducto;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import javax.persistence.EntityManager;
import javax.persistence.EntityManagerFactory;
import modelo.Pagos;
import modelo.Pedidos;

/**
 *
 * @author Talli
 */
public class PedidosJpaController implements Serializable {

    public PedidosJpaController(EntityManagerFactory emf) {
        this.emf = emf;
    }
    private EntityManagerFactory emf = null;

    public EntityManager getEntityManager() {
        return emf.createEntityManager();
    }

    public void create(Pedidos pedidos) {
        if (pedidos.getPedidoProductoCollection() == null) {
            pedidos.setPedidoProductoCollection(new ArrayList<PedidoProducto>());
        }
        if (pedidos.getPagosCollection() == null) {
            pedidos.setPagosCollection(new ArrayList<Pagos>());
        }
        EntityManager em = null;
        try {
            em = getEntityManager();
            em.getTransaction().begin();
            Usuario idUsuario = pedidos.getIdUsuario();
            if (idUsuario != null) {
                idUsuario = em.getReference(idUsuario.getClass(), idUsuario.getIdUsuario());
                pedidos.setIdUsuario(idUsuario);
            }
            Collection<PedidoProducto> attachedPedidoProductoCollection = new ArrayList<PedidoProducto>();
            for (PedidoProducto pedidoProductoCollectionPedidoProductoToAttach : pedidos.getPedidoProductoCollection()) {
                pedidoProductoCollectionPedidoProductoToAttach = em.getReference(pedidoProductoCollectionPedidoProductoToAttach.getClass(), pedidoProductoCollectionPedidoProductoToAttach.getIdPedidoProducto());
                attachedPedidoProductoCollection.add(pedidoProductoCollectionPedidoProductoToAttach);
            }
            pedidos.setPedidoProductoCollection(attachedPedidoProductoCollection);
            Collection<Pagos> attachedPagosCollection = new ArrayList<Pagos>();
            for (Pagos pagosCollectionPagosToAttach : pedidos.getPagosCollection()) {
                pagosCollectionPagosToAttach = em.getReference(pagosCollectionPagosToAttach.getClass(), pagosCollectionPagosToAttach.getIdPago());
                attachedPagosCollection.add(pagosCollectionPagosToAttach);
            }
            pedidos.setPagosCollection(attachedPagosCollection);
            em.persist(pedidos);
            if (idUsuario != null) {
                idUsuario.getPedidosCollection().add(pedidos);
                idUsuario = em.merge(idUsuario);
            }
            for (PedidoProducto pedidoProductoCollectionPedidoProducto : pedidos.getPedidoProductoCollection()) {
                Pedidos oldIdPedidoOfPedidoProductoCollectionPedidoProducto = pedidoProductoCollectionPedidoProducto.getIdPedido();
                pedidoProductoCollectionPedidoProducto.setIdPedido(pedidos);
                pedidoProductoCollectionPedidoProducto = em.merge(pedidoProductoCollectionPedidoProducto);
                if (oldIdPedidoOfPedidoProductoCollectionPedidoProducto != null) {
                    oldIdPedidoOfPedidoProductoCollectionPedidoProducto.getPedidoProductoCollection().remove(pedidoProductoCollectionPedidoProducto);
                    oldIdPedidoOfPedidoProductoCollectionPedidoProducto = em.merge(oldIdPedidoOfPedidoProductoCollectionPedidoProducto);
                }
            }
            for (Pagos pagosCollectionPagos : pedidos.getPagosCollection()) {
                Pedidos oldIdPedidoOfPagosCollectionPagos = pagosCollectionPagos.getIdPedido();
                pagosCollectionPagos.setIdPedido(pedidos);
                pagosCollectionPagos = em.merge(pagosCollectionPagos);
                if (oldIdPedidoOfPagosCollectionPagos != null) {
                    oldIdPedidoOfPagosCollectionPagos.getPagosCollection().remove(pagosCollectionPagos);
                    oldIdPedidoOfPagosCollectionPagos = em.merge(oldIdPedidoOfPagosCollectionPagos);
                }
            }
            em.getTransaction().commit();
        } finally {
            if (em != null) {
                em.close();
            }
        }
    }

    public void edit(Pedidos pedidos) throws IllegalOrphanException, NonexistentEntityException, Exception {
        EntityManager em = null;
        try {
            em = getEntityManager();
            em.getTransaction().begin();
            Pedidos persistentPedidos = em.find(Pedidos.class, pedidos.getIdPedido());
            Usuario idUsuarioOld = persistentPedidos.getIdUsuario();
            Usuario idUsuarioNew = pedidos.getIdUsuario();
            Collection<PedidoProducto> pedidoProductoCollectionOld = persistentPedidos.getPedidoProductoCollection();
            Collection<PedidoProducto> pedidoProductoCollectionNew = pedidos.getPedidoProductoCollection();
            Collection<Pagos> pagosCollectionOld = persistentPedidos.getPagosCollection();
            Collection<Pagos> pagosCollectionNew = pedidos.getPagosCollection();
            List<String> illegalOrphanMessages = null;
            for (PedidoProducto pedidoProductoCollectionOldPedidoProducto : pedidoProductoCollectionOld) {
                if (!pedidoProductoCollectionNew.contains(pedidoProductoCollectionOldPedidoProducto)) {
                    if (illegalOrphanMessages == null) {
                        illegalOrphanMessages = new ArrayList<String>();
                    }
                    illegalOrphanMessages.add("You must retain PedidoProducto " + pedidoProductoCollectionOldPedidoProducto + " since its idPedido field is not nullable.");
                }
            }
            for (Pagos pagosCollectionOldPagos : pagosCollectionOld) {
                if (!pagosCollectionNew.contains(pagosCollectionOldPagos)) {
                    if (illegalOrphanMessages == null) {
                        illegalOrphanMessages = new ArrayList<String>();
                    }
                    illegalOrphanMessages.add("You must retain Pagos " + pagosCollectionOldPagos + " since its idPedido field is not nullable.");
                }
            }
            if (illegalOrphanMessages != null) {
                throw new IllegalOrphanException(illegalOrphanMessages);
            }
            if (idUsuarioNew != null) {
                idUsuarioNew = em.getReference(idUsuarioNew.getClass(), idUsuarioNew.getIdUsuario());
                pedidos.setIdUsuario(idUsuarioNew);
            }
            Collection<PedidoProducto> attachedPedidoProductoCollectionNew = new ArrayList<PedidoProducto>();
            for (PedidoProducto pedidoProductoCollectionNewPedidoProductoToAttach : pedidoProductoCollectionNew) {
                pedidoProductoCollectionNewPedidoProductoToAttach = em.getReference(pedidoProductoCollectionNewPedidoProductoToAttach.getClass(), pedidoProductoCollectionNewPedidoProductoToAttach.getIdPedidoProducto());
                attachedPedidoProductoCollectionNew.add(pedidoProductoCollectionNewPedidoProductoToAttach);
            }
            pedidoProductoCollectionNew = attachedPedidoProductoCollectionNew;
            pedidos.setPedidoProductoCollection(pedidoProductoCollectionNew);
            Collection<Pagos> attachedPagosCollectionNew = new ArrayList<Pagos>();
            for (Pagos pagosCollectionNewPagosToAttach : pagosCollectionNew) {
                pagosCollectionNewPagosToAttach = em.getReference(pagosCollectionNewPagosToAttach.getClass(), pagosCollectionNewPagosToAttach.getIdPago());
                attachedPagosCollectionNew.add(pagosCollectionNewPagosToAttach);
            }
            pagosCollectionNew = attachedPagosCollectionNew;
            pedidos.setPagosCollection(pagosCollectionNew);
            pedidos = em.merge(pedidos);
            if (idUsuarioOld != null && !idUsuarioOld.equals(idUsuarioNew)) {
                idUsuarioOld.getPedidosCollection().remove(pedidos);
                idUsuarioOld = em.merge(idUsuarioOld);
            }
            if (idUsuarioNew != null && !idUsuarioNew.equals(idUsuarioOld)) {
                idUsuarioNew.getPedidosCollection().add(pedidos);
                idUsuarioNew = em.merge(idUsuarioNew);
            }
            for (PedidoProducto pedidoProductoCollectionNewPedidoProducto : pedidoProductoCollectionNew) {
                if (!pedidoProductoCollectionOld.contains(pedidoProductoCollectionNewPedidoProducto)) {
                    Pedidos oldIdPedidoOfPedidoProductoCollectionNewPedidoProducto = pedidoProductoCollectionNewPedidoProducto.getIdPedido();
                    pedidoProductoCollectionNewPedidoProducto.setIdPedido(pedidos);
                    pedidoProductoCollectionNewPedidoProducto = em.merge(pedidoProductoCollectionNewPedidoProducto);
                    if (oldIdPedidoOfPedidoProductoCollectionNewPedidoProducto != null && !oldIdPedidoOfPedidoProductoCollectionNewPedidoProducto.equals(pedidos)) {
                        oldIdPedidoOfPedidoProductoCollectionNewPedidoProducto.getPedidoProductoCollection().remove(pedidoProductoCollectionNewPedidoProducto);
                        oldIdPedidoOfPedidoProductoCollectionNewPedidoProducto = em.merge(oldIdPedidoOfPedidoProductoCollectionNewPedidoProducto);
                    }
                }
            }
            for (Pagos pagosCollectionNewPagos : pagosCollectionNew) {
                if (!pagosCollectionOld.contains(pagosCollectionNewPagos)) {
                    Pedidos oldIdPedidoOfPagosCollectionNewPagos = pagosCollectionNewPagos.getIdPedido();
                    pagosCollectionNewPagos.setIdPedido(pedidos);
                    pagosCollectionNewPagos = em.merge(pagosCollectionNewPagos);
                    if (oldIdPedidoOfPagosCollectionNewPagos != null && !oldIdPedidoOfPagosCollectionNewPagos.equals(pedidos)) {
                        oldIdPedidoOfPagosCollectionNewPagos.getPagosCollection().remove(pagosCollectionNewPagos);
                        oldIdPedidoOfPagosCollectionNewPagos = em.merge(oldIdPedidoOfPagosCollectionNewPagos);
                    }
                }
            }
            em.getTransaction().commit();
        } catch (Exception ex) {
            String msg = ex.getLocalizedMessage();
            if (msg == null || msg.length() == 0) {
                Integer id = pedidos.getIdPedido();
                if (findPedidos(id) == null) {
                    throw new NonexistentEntityException("The pedidos with id " + id + " no longer exists.");
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
            Pedidos pedidos;
            try {
                pedidos = em.getReference(Pedidos.class, id);
                pedidos.getIdPedido();
            } catch (EntityNotFoundException enfe) {
                throw new NonexistentEntityException("The pedidos with id " + id + " no longer exists.", enfe);
            }
            List<String> illegalOrphanMessages = null;
            Collection<PedidoProducto> pedidoProductoCollectionOrphanCheck = pedidos.getPedidoProductoCollection();
            for (PedidoProducto pedidoProductoCollectionOrphanCheckPedidoProducto : pedidoProductoCollectionOrphanCheck) {
                if (illegalOrphanMessages == null) {
                    illegalOrphanMessages = new ArrayList<String>();
                }
                illegalOrphanMessages.add("This Pedidos (" + pedidos + ") cannot be destroyed since the PedidoProducto " + pedidoProductoCollectionOrphanCheckPedidoProducto + " in its pedidoProductoCollection field has a non-nullable idPedido field.");
            }
            Collection<Pagos> pagosCollectionOrphanCheck = pedidos.getPagosCollection();
            for (Pagos pagosCollectionOrphanCheckPagos : pagosCollectionOrphanCheck) {
                if (illegalOrphanMessages == null) {
                    illegalOrphanMessages = new ArrayList<String>();
                }
                illegalOrphanMessages.add("This Pedidos (" + pedidos + ") cannot be destroyed since the Pagos " + pagosCollectionOrphanCheckPagos + " in its pagosCollection field has a non-nullable idPedido field.");
            }
            if (illegalOrphanMessages != null) {
                throw new IllegalOrphanException(illegalOrphanMessages);
            }
            Usuario idUsuario = pedidos.getIdUsuario();
            if (idUsuario != null) {
                idUsuario.getPedidosCollection().remove(pedidos);
                idUsuario = em.merge(idUsuario);
            }
            em.remove(pedidos);
            em.getTransaction().commit();
        } finally {
            if (em != null) {
                em.close();
            }
        }
    }

    public List<Pedidos> findPedidosEntities() {
        return findPedidosEntities(true, -1, -1);
    }

    public List<Pedidos> findPedidosEntities(int maxResults, int firstResult) {
        return findPedidosEntities(false, maxResults, firstResult);
    }

    private List<Pedidos> findPedidosEntities(boolean all, int maxResults, int firstResult) {
        EntityManager em = getEntityManager();
        try {
            CriteriaQuery cq = em.getCriteriaBuilder().createQuery();
            cq.select(cq.from(Pedidos.class));
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

    public Pedidos findPedidos(Integer id) {
        EntityManager em = getEntityManager();
        try {
            return em.find(Pedidos.class, id);
        } finally {
            em.close();
        }
    }

    public int getPedidosCount() {
        EntityManager em = getEntityManager();
        try {
            CriteriaQuery cq = em.getCriteriaBuilder().createQuery();
            Root<Pedidos> rt = cq.from(Pedidos.class);
            cq.select(em.getCriteriaBuilder().count(rt));
            Query q = em.createQuery(cq);
            return ((Long) q.getSingleResult()).intValue();
        } finally {
            em.close();
        }
    }
    
}
