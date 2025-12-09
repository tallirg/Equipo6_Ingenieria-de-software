/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package control;

import control.exceptions.NonexistentEntityException;
import java.io.Serializable;
import javax.persistence.Query;
import javax.persistence.EntityNotFoundException;
import javax.persistence.criteria.CriteriaQuery;
import javax.persistence.criteria.Root;
import modelo.Producto;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import javax.persistence.EntityManager;
import javax.persistence.EntityManagerFactory;
import modelo.Categoria;

/**
 *
 * @author Talli
 */
public class CategoriaJpaController implements Serializable {

    public CategoriaJpaController(EntityManagerFactory emf) {
        this.emf = emf;
    }
    private EntityManagerFactory emf = null;

    public EntityManager getEntityManager() {
        return emf.createEntityManager();
    }

    public void create(Categoria categoria) {
        if (categoria.getProductoCollection() == null) {
            categoria.setProductoCollection(new ArrayList<Producto>());
        }
        EntityManager em = null;
        try {
            em = getEntityManager();
            em.getTransaction().begin();
            Collection<Producto> attachedProductoCollection = new ArrayList<Producto>();
            for (Producto productoCollectionProductoToAttach : categoria.getProductoCollection()) {
                productoCollectionProductoToAttach = em.getReference(productoCollectionProductoToAttach.getClass(), productoCollectionProductoToAttach.getIdProducto());
                attachedProductoCollection.add(productoCollectionProductoToAttach);
            }
            categoria.setProductoCollection(attachedProductoCollection);
            em.persist(categoria);
            for (Producto productoCollectionProducto : categoria.getProductoCollection()) {
                Categoria oldIdCategoriaOfProductoCollectionProducto = productoCollectionProducto.getIdCategoria();
                productoCollectionProducto.setIdCategoria(categoria);
                productoCollectionProducto = em.merge(productoCollectionProducto);
                if (oldIdCategoriaOfProductoCollectionProducto != null) {
                    oldIdCategoriaOfProductoCollectionProducto.getProductoCollection().remove(productoCollectionProducto);
                    oldIdCategoriaOfProductoCollectionProducto = em.merge(oldIdCategoriaOfProductoCollectionProducto);
                }
            }
            em.getTransaction().commit();
        } finally {
            if (em != null) {
                em.close();
            }
        }
    }

    public void edit(Categoria categoria) throws NonexistentEntityException, Exception {
        EntityManager em = null;
        try {
            em = getEntityManager();
            em.getTransaction().begin();
            Categoria persistentCategoria = em.find(Categoria.class, categoria.getIdCategoria());
            Collection<Producto> productoCollectionOld = persistentCategoria.getProductoCollection();
            Collection<Producto> productoCollectionNew = categoria.getProductoCollection();
            Collection<Producto> attachedProductoCollectionNew = new ArrayList<Producto>();
            for (Producto productoCollectionNewProductoToAttach : productoCollectionNew) {
                productoCollectionNewProductoToAttach = em.getReference(productoCollectionNewProductoToAttach.getClass(), productoCollectionNewProductoToAttach.getIdProducto());
                attachedProductoCollectionNew.add(productoCollectionNewProductoToAttach);
            }
            productoCollectionNew = attachedProductoCollectionNew;
            categoria.setProductoCollection(productoCollectionNew);
            categoria = em.merge(categoria);
            for (Producto productoCollectionOldProducto : productoCollectionOld) {
                if (!productoCollectionNew.contains(productoCollectionOldProducto)) {
                    productoCollectionOldProducto.setIdCategoria(null);
                    productoCollectionOldProducto = em.merge(productoCollectionOldProducto);
                }
            }
            for (Producto productoCollectionNewProducto : productoCollectionNew) {
                if (!productoCollectionOld.contains(productoCollectionNewProducto)) {
                    Categoria oldIdCategoriaOfProductoCollectionNewProducto = productoCollectionNewProducto.getIdCategoria();
                    productoCollectionNewProducto.setIdCategoria(categoria);
                    productoCollectionNewProducto = em.merge(productoCollectionNewProducto);
                    if (oldIdCategoriaOfProductoCollectionNewProducto != null && !oldIdCategoriaOfProductoCollectionNewProducto.equals(categoria)) {
                        oldIdCategoriaOfProductoCollectionNewProducto.getProductoCollection().remove(productoCollectionNewProducto);
                        oldIdCategoriaOfProductoCollectionNewProducto = em.merge(oldIdCategoriaOfProductoCollectionNewProducto);
                    }
                }
            }
            em.getTransaction().commit();
        } catch (Exception ex) {
            String msg = ex.getLocalizedMessage();
            if (msg == null || msg.length() == 0) {
                Integer id = categoria.getIdCategoria();
                if (findCategoria(id) == null) {
                    throw new NonexistentEntityException("The categoria with id " + id + " no longer exists.");
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
            Categoria categoria;
            try {
                categoria = em.getReference(Categoria.class, id);
                categoria.getIdCategoria();
            } catch (EntityNotFoundException enfe) {
                throw new NonexistentEntityException("The categoria with id " + id + " no longer exists.", enfe);
            }
            Collection<Producto> productoCollection = categoria.getProductoCollection();
            for (Producto productoCollectionProducto : productoCollection) {
                productoCollectionProducto.setIdCategoria(null);
                productoCollectionProducto = em.merge(productoCollectionProducto);
            }
            em.remove(categoria);
            em.getTransaction().commit();
        } finally {
            if (em != null) {
                em.close();
            }
        }
    }

    public List<Categoria> findCategoriaEntities() {
        return findCategoriaEntities(true, -1, -1);
    }

    public List<Categoria> findCategoriaEntities(int maxResults, int firstResult) {
        return findCategoriaEntities(false, maxResults, firstResult);
    }

    private List<Categoria> findCategoriaEntities(boolean all, int maxResults, int firstResult) {
        EntityManager em = getEntityManager();
        try {
            CriteriaQuery cq = em.getCriteriaBuilder().createQuery();
            cq.select(cq.from(Categoria.class));
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

    public Categoria findCategoria(Integer id) {
        EntityManager em = getEntityManager();
        try {
            return em.find(Categoria.class, id);
        } finally {
            em.close();
        }
    }

    public int getCategoriaCount() {
        EntityManager em = getEntityManager();
        try {
            CriteriaQuery cq = em.getCriteriaBuilder().createQuery();
            Root<Categoria> rt = cq.from(Categoria.class);
            cq.select(em.getCriteriaBuilder().count(rt));
            Query q = em.createQuery(cq);
            return ((Long) q.getSingleResult()).intValue();
        } finally {
            em.close();
        }
    }
    
}
