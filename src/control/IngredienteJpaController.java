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
import modelo.ProductoIngrediente;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import javax.persistence.EntityManager;
import javax.persistence.EntityManagerFactory;
import modelo.Ingrediente;

/**
 *
 * @author Talli
 */
public class IngredienteJpaController implements Serializable {

    public IngredienteJpaController(EntityManagerFactory emf) {
        this.emf = emf;
    }
    private EntityManagerFactory emf = null;

    public EntityManager getEntityManager() {
        return emf.createEntityManager();
    }

    public void create(Ingrediente ingrediente) {
        if (ingrediente.getProductoIngredienteCollection() == null) {
            ingrediente.setProductoIngredienteCollection(new ArrayList<ProductoIngrediente>());
        }
        EntityManager em = null;
        try {
            em = getEntityManager();
            em.getTransaction().begin();
            Collection<ProductoIngrediente> attachedProductoIngredienteCollection = new ArrayList<ProductoIngrediente>();
            for (ProductoIngrediente productoIngredienteCollectionProductoIngredienteToAttach : ingrediente.getProductoIngredienteCollection()) {
                productoIngredienteCollectionProductoIngredienteToAttach = em.getReference(productoIngredienteCollectionProductoIngredienteToAttach.getClass(), productoIngredienteCollectionProductoIngredienteToAttach.getIdProductoIngrediente());
                attachedProductoIngredienteCollection.add(productoIngredienteCollectionProductoIngredienteToAttach);
            }
            ingrediente.setProductoIngredienteCollection(attachedProductoIngredienteCollection);
            em.persist(ingrediente);
            for (ProductoIngrediente productoIngredienteCollectionProductoIngrediente : ingrediente.getProductoIngredienteCollection()) {
                Ingrediente oldIdIngredienteOfProductoIngredienteCollectionProductoIngrediente = productoIngredienteCollectionProductoIngrediente.getIdIngrediente();
                productoIngredienteCollectionProductoIngrediente.setIdIngrediente(ingrediente);
                productoIngredienteCollectionProductoIngrediente = em.merge(productoIngredienteCollectionProductoIngrediente);
                if (oldIdIngredienteOfProductoIngredienteCollectionProductoIngrediente != null) {
                    oldIdIngredienteOfProductoIngredienteCollectionProductoIngrediente.getProductoIngredienteCollection().remove(productoIngredienteCollectionProductoIngrediente);
                    oldIdIngredienteOfProductoIngredienteCollectionProductoIngrediente = em.merge(oldIdIngredienteOfProductoIngredienteCollectionProductoIngrediente);
                }
            }
            em.getTransaction().commit();
        } finally {
            if (em != null) {
                em.close();
            }
        }
    }

    public void edit(Ingrediente ingrediente) throws IllegalOrphanException, NonexistentEntityException, Exception {
        EntityManager em = null;
        try {
            em = getEntityManager();
            em.getTransaction().begin();
            Ingrediente persistentIngrediente = em.find(Ingrediente.class, ingrediente.getIdIngrediente());
            Collection<ProductoIngrediente> productoIngredienteCollectionOld = persistentIngrediente.getProductoIngredienteCollection();
            Collection<ProductoIngrediente> productoIngredienteCollectionNew = ingrediente.getProductoIngredienteCollection();
            List<String> illegalOrphanMessages = null;
            for (ProductoIngrediente productoIngredienteCollectionOldProductoIngrediente : productoIngredienteCollectionOld) {
                if (!productoIngredienteCollectionNew.contains(productoIngredienteCollectionOldProductoIngrediente)) {
                    if (illegalOrphanMessages == null) {
                        illegalOrphanMessages = new ArrayList<String>();
                    }
                    illegalOrphanMessages.add("You must retain ProductoIngrediente " + productoIngredienteCollectionOldProductoIngrediente + " since its idIngrediente field is not nullable.");
                }
            }
            if (illegalOrphanMessages != null) {
                throw new IllegalOrphanException(illegalOrphanMessages);
            }
            Collection<ProductoIngrediente> attachedProductoIngredienteCollectionNew = new ArrayList<ProductoIngrediente>();
            for (ProductoIngrediente productoIngredienteCollectionNewProductoIngredienteToAttach : productoIngredienteCollectionNew) {
                productoIngredienteCollectionNewProductoIngredienteToAttach = em.getReference(productoIngredienteCollectionNewProductoIngredienteToAttach.getClass(), productoIngredienteCollectionNewProductoIngredienteToAttach.getIdProductoIngrediente());
                attachedProductoIngredienteCollectionNew.add(productoIngredienteCollectionNewProductoIngredienteToAttach);
            }
            productoIngredienteCollectionNew = attachedProductoIngredienteCollectionNew;
            ingrediente.setProductoIngredienteCollection(productoIngredienteCollectionNew);
            ingrediente = em.merge(ingrediente);
            for (ProductoIngrediente productoIngredienteCollectionNewProductoIngrediente : productoIngredienteCollectionNew) {
                if (!productoIngredienteCollectionOld.contains(productoIngredienteCollectionNewProductoIngrediente)) {
                    Ingrediente oldIdIngredienteOfProductoIngredienteCollectionNewProductoIngrediente = productoIngredienteCollectionNewProductoIngrediente.getIdIngrediente();
                    productoIngredienteCollectionNewProductoIngrediente.setIdIngrediente(ingrediente);
                    productoIngredienteCollectionNewProductoIngrediente = em.merge(productoIngredienteCollectionNewProductoIngrediente);
                    if (oldIdIngredienteOfProductoIngredienteCollectionNewProductoIngrediente != null && !oldIdIngredienteOfProductoIngredienteCollectionNewProductoIngrediente.equals(ingrediente)) {
                        oldIdIngredienteOfProductoIngredienteCollectionNewProductoIngrediente.getProductoIngredienteCollection().remove(productoIngredienteCollectionNewProductoIngrediente);
                        oldIdIngredienteOfProductoIngredienteCollectionNewProductoIngrediente = em.merge(oldIdIngredienteOfProductoIngredienteCollectionNewProductoIngrediente);
                    }
                }
            }
            em.getTransaction().commit();
        } catch (Exception ex) {
            String msg = ex.getLocalizedMessage();
            if (msg == null || msg.length() == 0) {
                Integer id = ingrediente.getIdIngrediente();
                if (findIngrediente(id) == null) {
                    throw new NonexistentEntityException("The ingrediente with id " + id + " no longer exists.");
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
            Ingrediente ingrediente;
            try {
                ingrediente = em.getReference(Ingrediente.class, id);
                ingrediente.getIdIngrediente();
            } catch (EntityNotFoundException enfe) {
                throw new NonexistentEntityException("The ingrediente with id " + id + " no longer exists.", enfe);
            }
            List<String> illegalOrphanMessages = null;
            Collection<ProductoIngrediente> productoIngredienteCollectionOrphanCheck = ingrediente.getProductoIngredienteCollection();
            for (ProductoIngrediente productoIngredienteCollectionOrphanCheckProductoIngrediente : productoIngredienteCollectionOrphanCheck) {
                if (illegalOrphanMessages == null) {
                    illegalOrphanMessages = new ArrayList<String>();
                }
                illegalOrphanMessages.add("This Ingrediente (" + ingrediente + ") cannot be destroyed since the ProductoIngrediente " + productoIngredienteCollectionOrphanCheckProductoIngrediente + " in its productoIngredienteCollection field has a non-nullable idIngrediente field.");
            }
            if (illegalOrphanMessages != null) {
                throw new IllegalOrphanException(illegalOrphanMessages);
            }
            em.remove(ingrediente);
            em.getTransaction().commit();
        } finally {
            if (em != null) {
                em.close();
            }
        }
    }

    public List<Ingrediente> findIngredienteEntities() {
        return findIngredienteEntities(true, -1, -1);
    }

    public List<Ingrediente> findIngredienteEntities(int maxResults, int firstResult) {
        return findIngredienteEntities(false, maxResults, firstResult);
    }

    private List<Ingrediente> findIngredienteEntities(boolean all, int maxResults, int firstResult) {
        EntityManager em = getEntityManager();
        try {
            CriteriaQuery cq = em.getCriteriaBuilder().createQuery();
            cq.select(cq.from(Ingrediente.class));
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

    public Ingrediente findIngrediente(Integer id) {
        EntityManager em = getEntityManager();
        try {
            return em.find(Ingrediente.class, id);
        } finally {
            em.close();
        }
    }

    public int getIngredienteCount() {
        EntityManager em = getEntityManager();
        try {
            CriteriaQuery cq = em.getCriteriaBuilder().createQuery();
            Root<Ingrediente> rt = cq.from(Ingrediente.class);
            cq.select(em.getCriteriaBuilder().count(rt));
            Query q = em.createQuery(cq);
            return ((Long) q.getSingleResult()).intValue();
        } finally {
            em.close();
        }
    }
    
}
