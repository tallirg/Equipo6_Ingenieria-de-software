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
import modelo.PedidoProducto;
import modelo.Pedidos;
import modelo.Producto;

/**
 *
 * @author Talli
 */
public class PedidoProductoJpaController implements Serializable {

    public PedidoProductoJpaController(EntityManagerFactory emf) {
        this.emf = emf;
    }
    private EntityManagerFactory emf = null;

    public EntityManager getEntityManager() {
        return emf.createEntityManager();
    }

    public void create(PedidoProducto pedidoProducto) {
        EntityManager em = null;
        try {
            em = getEntityManager();
            em.getTransaction().begin();
            Pedidos idPedido = pedidoProducto.getIdPedido();
            if (idPedido != null) {
                idPedido = em.getReference(idPedido.getClass(), idPedido.getIdPedido());
                pedidoProducto.setIdPedido(idPedido);
            }
            Producto idProducto = pedidoProducto.getIdProducto();
            if (idProducto != null) {
                idProducto = em.getReference(idProducto.getClass(), idProducto.getIdProducto());
                pedidoProducto.setIdProducto(idProducto);
            }
            em.persist(pedidoProducto);
            if (idPedido != null) {
                idPedido.getPedidoProductoCollection().add(pedidoProducto);
                idPedido = em.merge(idPedido);
            }
            if (idProducto != null) {
                idProducto.getPedidoProductoCollection().add(pedidoProducto);
                idProducto = em.merge(idProducto);
            }
            em.getTransaction().commit();
        } finally {
            if (em != null) {
                em.close();
            }
        }
    }

    public void edit(PedidoProducto pedidoProducto) throws NonexistentEntityException, Exception {
        EntityManager em = null;
        try {
            em = getEntityManager();
            em.getTransaction().begin();
            PedidoProducto persistentPedidoProducto = em.find(PedidoProducto.class, pedidoProducto.getIdPedidoProducto());
            Pedidos idPedidoOld = persistentPedidoProducto.getIdPedido();
            Pedidos idPedidoNew = pedidoProducto.getIdPedido();
            Producto idProductoOld = persistentPedidoProducto.getIdProducto();
            Producto idProductoNew = pedidoProducto.getIdProducto();
            if (idPedidoNew != null) {
                idPedidoNew = em.getReference(idPedidoNew.getClass(), idPedidoNew.getIdPedido());
                pedidoProducto.setIdPedido(idPedidoNew);
            }
            if (idProductoNew != null) {
                idProductoNew = em.getReference(idProductoNew.getClass(), idProductoNew.getIdProducto());
                pedidoProducto.setIdProducto(idProductoNew);
            }
            pedidoProducto = em.merge(pedidoProducto);
            if (idPedidoOld != null && !idPedidoOld.equals(idPedidoNew)) {
                idPedidoOld.getPedidoProductoCollection().remove(pedidoProducto);
                idPedidoOld = em.merge(idPedidoOld);
            }
            if (idPedidoNew != null && !idPedidoNew.equals(idPedidoOld)) {
                idPedidoNew.getPedidoProductoCollection().add(pedidoProducto);
                idPedidoNew = em.merge(idPedidoNew);
            }
            if (idProductoOld != null && !idProductoOld.equals(idProductoNew)) {
                idProductoOld.getPedidoProductoCollection().remove(pedidoProducto);
                idProductoOld = em.merge(idProductoOld);
            }
            if (idProductoNew != null && !idProductoNew.equals(idProductoOld)) {
                idProductoNew.getPedidoProductoCollection().add(pedidoProducto);
                idProductoNew = em.merge(idProductoNew);
            }
            em.getTransaction().commit();
        } catch (Exception ex) {
            String msg = ex.getLocalizedMessage();
            if (msg == null || msg.length() == 0) {
                Integer id = pedidoProducto.getIdPedidoProducto();
                if (findPedidoProducto(id) == null) {
                    throw new NonexistentEntityException("The pedidoProducto with id " + id + " no longer exists.");
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
            PedidoProducto pedidoProducto;
            try {
                pedidoProducto = em.getReference(PedidoProducto.class, id);
                pedidoProducto.getIdPedidoProducto();
            } catch (EntityNotFoundException enfe) {
                throw new NonexistentEntityException("The pedidoProducto with id " + id + " no longer exists.", enfe);
            }
            Pedidos idPedido = pedidoProducto.getIdPedido();
            if (idPedido != null) {
                idPedido.getPedidoProductoCollection().remove(pedidoProducto);
                idPedido = em.merge(idPedido);
            }
            Producto idProducto = pedidoProducto.getIdProducto();
            if (idProducto != null) {
                idProducto.getPedidoProductoCollection().remove(pedidoProducto);
                idProducto = em.merge(idProducto);
            }
            em.remove(pedidoProducto);
            em.getTransaction().commit();
        } finally {
            if (em != null) {
                em.close();
            }
        }
    }

    public List<PedidoProducto> findPedidoProductoEntities() {
        return findPedidoProductoEntities(true, -1, -1);
    }

    public List<PedidoProducto> findPedidoProductoEntities(int maxResults, int firstResult) {
        return findPedidoProductoEntities(false, maxResults, firstResult);
    }

    private List<PedidoProducto> findPedidoProductoEntities(boolean all, int maxResults, int firstResult) {
        EntityManager em = getEntityManager();
        try {
            CriteriaQuery cq = em.getCriteriaBuilder().createQuery();
            cq.select(cq.from(PedidoProducto.class));
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

    public PedidoProducto findPedidoProducto(Integer id) {
        EntityManager em = getEntityManager();
        try {
            return em.find(PedidoProducto.class, id);
        } finally {
            em.close();
        }
    }

    public int getPedidoProductoCount() {
        EntityManager em = getEntityManager();
        try {
            CriteriaQuery cq = em.getCriteriaBuilder().createQuery();
            Root<PedidoProducto> rt = cq.from(PedidoProducto.class);
            cq.select(em.getCriteriaBuilder().count(rt));
            Query q = em.createQuery(cq);
            return ((Long) q.getSingleResult()).intValue();
        } finally {
            em.close();
        }
    }
    
}
