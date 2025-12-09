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
import modelo.Ingrediente;
import modelo.Producto;
import modelo.ProductoIngrediente;

/**
 *
 * @author Talli
 */
public class ProductoIngredienteJpaController implements Serializable {

    public ProductoIngredienteJpaController(EntityManagerFactory emf) {
        this.emf = emf;
    }
    private EntityManagerFactory emf = null;

    public EntityManager getEntityManager() {
        return emf.createEntityManager();
    }

    public void create(ProductoIngrediente productoIngrediente) {
        EntityManager em = null;
        try {
            em = getEntityManager();
            em.getTransaction().begin();
            Ingrediente idIngrediente = productoIngrediente.getIdIngrediente();
            if (idIngrediente != null) {
                idIngrediente = em.getReference(idIngrediente.getClass(), idIngrediente.getIdIngrediente());
                productoIngrediente.setIdIngrediente(idIngrediente);
            }
            Producto idProducto = productoIngrediente.getIdProducto();
            if (idProducto != null) {
                idProducto = em.getReference(idProducto.getClass(), idProducto.getIdProducto());
                productoIngrediente.setIdProducto(idProducto);
            }
            em.persist(productoIngrediente);
            if (idIngrediente != null) {
                idIngrediente.getProductoIngredienteCollection().add(productoIngrediente);
                idIngrediente = em.merge(idIngrediente);
            }
            if (idProducto != null) {
                idProducto.getProductoIngredienteCollection().add(productoIngrediente);
                idProducto = em.merge(idProducto);
            }
            em.getTransaction().commit();
        } finally {
            if (em != null) {
                em.close();
            }
        }
    }

    public void edit(ProductoIngrediente productoIngrediente) throws NonexistentEntityException, Exception {
        EntityManager em = null;
        try {
            em = getEntityManager();
            em.getTransaction().begin();
            ProductoIngrediente persistentProductoIngrediente = em.find(ProductoIngrediente.class, productoIngrediente.getIdProductoIngrediente());
            Ingrediente idIngredienteOld = persistentProductoIngrediente.getIdIngrediente();
            Ingrediente idIngredienteNew = productoIngrediente.getIdIngrediente();
            Producto idProductoOld = persistentProductoIngrediente.getIdProducto();
            Producto idProductoNew = productoIngrediente.getIdProducto();
            if (idIngredienteNew != null) {
                idIngredienteNew = em.getReference(idIngredienteNew.getClass(), idIngredienteNew.getIdIngrediente());
                productoIngrediente.setIdIngrediente(idIngredienteNew);
            }
            if (idProductoNew != null) {
                idProductoNew = em.getReference(idProductoNew.getClass(), idProductoNew.getIdProducto());
                productoIngrediente.setIdProducto(idProductoNew);
            }
            productoIngrediente = em.merge(productoIngrediente);
            if (idIngredienteOld != null && !idIngredienteOld.equals(idIngredienteNew)) {
                idIngredienteOld.getProductoIngredienteCollection().remove(productoIngrediente);
                idIngredienteOld = em.merge(idIngredienteOld);
            }
            if (idIngredienteNew != null && !idIngredienteNew.equals(idIngredienteOld)) {
                idIngredienteNew.getProductoIngredienteCollection().add(productoIngrediente);
                idIngredienteNew = em.merge(idIngredienteNew);
            }
            if (idProductoOld != null && !idProductoOld.equals(idProductoNew)) {
                idProductoOld.getProductoIngredienteCollection().remove(productoIngrediente);
                idProductoOld = em.merge(idProductoOld);
            }
            if (idProductoNew != null && !idProductoNew.equals(idProductoOld)) {
                idProductoNew.getProductoIngredienteCollection().add(productoIngrediente);
                idProductoNew = em.merge(idProductoNew);
            }
            em.getTransaction().commit();
        } catch (Exception ex) {
            String msg = ex.getLocalizedMessage();
            if (msg == null || msg.length() == 0) {
                Integer id = productoIngrediente.getIdProductoIngrediente();
                if (findProductoIngrediente(id) == null) {
                    throw new NonexistentEntityException("The productoIngrediente with id " + id + " no longer exists.");
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
            ProductoIngrediente productoIngrediente;
            try {
                productoIngrediente = em.getReference(ProductoIngrediente.class, id);
                productoIngrediente.getIdProductoIngrediente();
            } catch (EntityNotFoundException enfe) {
                throw new NonexistentEntityException("The productoIngrediente with id " + id + " no longer exists.", enfe);
            }
            Ingrediente idIngrediente = productoIngrediente.getIdIngrediente();
            if (idIngrediente != null) {
                idIngrediente.getProductoIngredienteCollection().remove(productoIngrediente);
                idIngrediente = em.merge(idIngrediente);
            }
            Producto idProducto = productoIngrediente.getIdProducto();
            if (idProducto != null) {
                idProducto.getProductoIngredienteCollection().remove(productoIngrediente);
                idProducto = em.merge(idProducto);
            }
            em.remove(productoIngrediente);
            em.getTransaction().commit();
        } finally {
            if (em != null) {
                em.close();
            }
        }
    }

    public List<ProductoIngrediente> findProductoIngredienteEntities() {
        return findProductoIngredienteEntities(true, -1, -1);
    }

    public List<ProductoIngrediente> findProductoIngredienteEntities(int maxResults, int firstResult) {
        return findProductoIngredienteEntities(false, maxResults, firstResult);
    }

    private List<ProductoIngrediente> findProductoIngredienteEntities(boolean all, int maxResults, int firstResult) {
        EntityManager em = getEntityManager();
        try {
            CriteriaQuery cq = em.getCriteriaBuilder().createQuery();
            cq.select(cq.from(ProductoIngrediente.class));
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

    public ProductoIngrediente findProductoIngrediente(Integer id) {
        EntityManager em = getEntityManager();
        try {
            return em.find(ProductoIngrediente.class, id);
        } finally {
            em.close();
        }
    }

    public int getProductoIngredienteCount() {
        EntityManager em = getEntityManager();
        try {
            CriteriaQuery cq = em.getCriteriaBuilder().createQuery();
            Root<ProductoIngrediente> rt = cq.from(ProductoIngrediente.class);
            cq.select(em.getCriteriaBuilder().count(rt));
            Query q = em.createQuery(cq);
            return ((Long) q.getSingleResult()).intValue();
        } finally {
            em.close();
        }
    }
    
}
