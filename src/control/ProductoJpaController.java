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
import modelo.Categoria;
import modelo.ProductoIngrediente;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import javax.persistence.EntityManager;
import javax.persistence.EntityManagerFactory;
import modelo.PedidoProducto;
import modelo.Inventario;
import modelo.Producto;

/**
 *
 * @author Talli
 */
public class ProductoJpaController implements Serializable {

    public ProductoJpaController(EntityManagerFactory emf) {
        this.emf = emf;
    }
    private EntityManagerFactory emf = null;

    public EntityManager getEntityManager() {
        return emf.createEntityManager();
    }

    public void create(Producto producto) {
        if (producto.getProductoIngredienteCollection() == null) {
            producto.setProductoIngredienteCollection(new ArrayList<ProductoIngrediente>());
        }
        if (producto.getPedidoProductoCollection() == null) {
            producto.setPedidoProductoCollection(new ArrayList<PedidoProducto>());
        }
        if (producto.getInventarioCollection() == null) {
            producto.setInventarioCollection(new ArrayList<Inventario>());
        }
        EntityManager em = null;
        try {
            em = getEntityManager();
            em.getTransaction().begin();
            Categoria idCategoria = producto.getIdCategoria();
            if (idCategoria != null) {
                idCategoria = em.getReference(idCategoria.getClass(), idCategoria.getIdCategoria());
                producto.setIdCategoria(idCategoria);
            }
            Collection<ProductoIngrediente> attachedProductoIngredienteCollection = new ArrayList<ProductoIngrediente>();
            for (ProductoIngrediente productoIngredienteCollectionProductoIngredienteToAttach : producto.getProductoIngredienteCollection()) {
                productoIngredienteCollectionProductoIngredienteToAttach = em.getReference(productoIngredienteCollectionProductoIngredienteToAttach.getClass(), productoIngredienteCollectionProductoIngredienteToAttach.getIdProductoIngrediente());
                attachedProductoIngredienteCollection.add(productoIngredienteCollectionProductoIngredienteToAttach);
            }
            producto.setProductoIngredienteCollection(attachedProductoIngredienteCollection);
            Collection<PedidoProducto> attachedPedidoProductoCollection = new ArrayList<PedidoProducto>();
            for (PedidoProducto pedidoProductoCollectionPedidoProductoToAttach : producto.getPedidoProductoCollection()) {
                pedidoProductoCollectionPedidoProductoToAttach = em.getReference(pedidoProductoCollectionPedidoProductoToAttach.getClass(), pedidoProductoCollectionPedidoProductoToAttach.getIdPedidoProducto());
                attachedPedidoProductoCollection.add(pedidoProductoCollectionPedidoProductoToAttach);
            }
            producto.setPedidoProductoCollection(attachedPedidoProductoCollection);
            Collection<Inventario> attachedInventarioCollection = new ArrayList<Inventario>();
            for (Inventario inventarioCollectionInventarioToAttach : producto.getInventarioCollection()) {
                inventarioCollectionInventarioToAttach = em.getReference(inventarioCollectionInventarioToAttach.getClass(), inventarioCollectionInventarioToAttach.getIdInventario());
                attachedInventarioCollection.add(inventarioCollectionInventarioToAttach);
            }
            producto.setInventarioCollection(attachedInventarioCollection);
            em.persist(producto);
            if (idCategoria != null) {
                idCategoria.getProductoCollection().add(producto);
                idCategoria = em.merge(idCategoria);
            }
            for (ProductoIngrediente productoIngredienteCollectionProductoIngrediente : producto.getProductoIngredienteCollection()) {
                Producto oldIdProductoOfProductoIngredienteCollectionProductoIngrediente = productoIngredienteCollectionProductoIngrediente.getIdProducto();
                productoIngredienteCollectionProductoIngrediente.setIdProducto(producto);
                productoIngredienteCollectionProductoIngrediente = em.merge(productoIngredienteCollectionProductoIngrediente);
                if (oldIdProductoOfProductoIngredienteCollectionProductoIngrediente != null) {
                    oldIdProductoOfProductoIngredienteCollectionProductoIngrediente.getProductoIngredienteCollection().remove(productoIngredienteCollectionProductoIngrediente);
                    oldIdProductoOfProductoIngredienteCollectionProductoIngrediente = em.merge(oldIdProductoOfProductoIngredienteCollectionProductoIngrediente);
                }
            }
            for (PedidoProducto pedidoProductoCollectionPedidoProducto : producto.getPedidoProductoCollection()) {
                Producto oldIdProductoOfPedidoProductoCollectionPedidoProducto = pedidoProductoCollectionPedidoProducto.getIdProducto();
                pedidoProductoCollectionPedidoProducto.setIdProducto(producto);
                pedidoProductoCollectionPedidoProducto = em.merge(pedidoProductoCollectionPedidoProducto);
                if (oldIdProductoOfPedidoProductoCollectionPedidoProducto != null) {
                    oldIdProductoOfPedidoProductoCollectionPedidoProducto.getPedidoProductoCollection().remove(pedidoProductoCollectionPedidoProducto);
                    oldIdProductoOfPedidoProductoCollectionPedidoProducto = em.merge(oldIdProductoOfPedidoProductoCollectionPedidoProducto);
                }
            }
            for (Inventario inventarioCollectionInventario : producto.getInventarioCollection()) {
                Producto oldIdProductoOfInventarioCollectionInventario = inventarioCollectionInventario.getIdProducto();
                inventarioCollectionInventario.setIdProducto(producto);
                inventarioCollectionInventario = em.merge(inventarioCollectionInventario);
                if (oldIdProductoOfInventarioCollectionInventario != null) {
                    oldIdProductoOfInventarioCollectionInventario.getInventarioCollection().remove(inventarioCollectionInventario);
                    oldIdProductoOfInventarioCollectionInventario = em.merge(oldIdProductoOfInventarioCollectionInventario);
                }
            }
            em.getTransaction().commit();
        } finally {
            if (em != null) {
                em.close();
            }
        }
    }

    public void edit(Producto producto) throws IllegalOrphanException, NonexistentEntityException, Exception {
        EntityManager em = null;
        try {
            em = getEntityManager();
            em.getTransaction().begin();
            Producto persistentProducto = em.find(Producto.class, producto.getIdProducto());
            Categoria idCategoriaOld = persistentProducto.getIdCategoria();
            Categoria idCategoriaNew = producto.getIdCategoria();
            Collection<ProductoIngrediente> productoIngredienteCollectionOld = persistentProducto.getProductoIngredienteCollection();
            Collection<ProductoIngrediente> productoIngredienteCollectionNew = producto.getProductoIngredienteCollection();
            Collection<PedidoProducto> pedidoProductoCollectionOld = persistentProducto.getPedidoProductoCollection();
            Collection<PedidoProducto> pedidoProductoCollectionNew = producto.getPedidoProductoCollection();
            Collection<Inventario> inventarioCollectionOld = persistentProducto.getInventarioCollection();
            Collection<Inventario> inventarioCollectionNew = producto.getInventarioCollection();
            List<String> illegalOrphanMessages = null;
            for (ProductoIngrediente productoIngredienteCollectionOldProductoIngrediente : productoIngredienteCollectionOld) {
                if (!productoIngredienteCollectionNew.contains(productoIngredienteCollectionOldProductoIngrediente)) {
                    if (illegalOrphanMessages == null) {
                        illegalOrphanMessages = new ArrayList<String>();
                    }
                    illegalOrphanMessages.add("You must retain ProductoIngrediente " + productoIngredienteCollectionOldProductoIngrediente + " since its idProducto field is not nullable.");
                }
            }
            for (PedidoProducto pedidoProductoCollectionOldPedidoProducto : pedidoProductoCollectionOld) {
                if (!pedidoProductoCollectionNew.contains(pedidoProductoCollectionOldPedidoProducto)) {
                    if (illegalOrphanMessages == null) {
                        illegalOrphanMessages = new ArrayList<String>();
                    }
                    illegalOrphanMessages.add("You must retain PedidoProducto " + pedidoProductoCollectionOldPedidoProducto + " since its idProducto field is not nullable.");
                }
            }
            for (Inventario inventarioCollectionOldInventario : inventarioCollectionOld) {
                if (!inventarioCollectionNew.contains(inventarioCollectionOldInventario)) {
                    if (illegalOrphanMessages == null) {
                        illegalOrphanMessages = new ArrayList<String>();
                    }
                    illegalOrphanMessages.add("You must retain Inventario " + inventarioCollectionOldInventario + " since its idProducto field is not nullable.");
                }
            }
            if (illegalOrphanMessages != null) {
                throw new IllegalOrphanException(illegalOrphanMessages);
            }
            if (idCategoriaNew != null) {
                idCategoriaNew = em.getReference(idCategoriaNew.getClass(), idCategoriaNew.getIdCategoria());
                producto.setIdCategoria(idCategoriaNew);
            }
            Collection<ProductoIngrediente> attachedProductoIngredienteCollectionNew = new ArrayList<ProductoIngrediente>();
            for (ProductoIngrediente productoIngredienteCollectionNewProductoIngredienteToAttach : productoIngredienteCollectionNew) {
                productoIngredienteCollectionNewProductoIngredienteToAttach = em.getReference(productoIngredienteCollectionNewProductoIngredienteToAttach.getClass(), productoIngredienteCollectionNewProductoIngredienteToAttach.getIdProductoIngrediente());
                attachedProductoIngredienteCollectionNew.add(productoIngredienteCollectionNewProductoIngredienteToAttach);
            }
            productoIngredienteCollectionNew = attachedProductoIngredienteCollectionNew;
            producto.setProductoIngredienteCollection(productoIngredienteCollectionNew);
            Collection<PedidoProducto> attachedPedidoProductoCollectionNew = new ArrayList<PedidoProducto>();
            for (PedidoProducto pedidoProductoCollectionNewPedidoProductoToAttach : pedidoProductoCollectionNew) {
                pedidoProductoCollectionNewPedidoProductoToAttach = em.getReference(pedidoProductoCollectionNewPedidoProductoToAttach.getClass(), pedidoProductoCollectionNewPedidoProductoToAttach.getIdPedidoProducto());
                attachedPedidoProductoCollectionNew.add(pedidoProductoCollectionNewPedidoProductoToAttach);
            }
            pedidoProductoCollectionNew = attachedPedidoProductoCollectionNew;
            producto.setPedidoProductoCollection(pedidoProductoCollectionNew);
            Collection<Inventario> attachedInventarioCollectionNew = new ArrayList<Inventario>();
            for (Inventario inventarioCollectionNewInventarioToAttach : inventarioCollectionNew) {
                inventarioCollectionNewInventarioToAttach = em.getReference(inventarioCollectionNewInventarioToAttach.getClass(), inventarioCollectionNewInventarioToAttach.getIdInventario());
                attachedInventarioCollectionNew.add(inventarioCollectionNewInventarioToAttach);
            }
            inventarioCollectionNew = attachedInventarioCollectionNew;
            producto.setInventarioCollection(inventarioCollectionNew);
            producto = em.merge(producto);
            if (idCategoriaOld != null && !idCategoriaOld.equals(idCategoriaNew)) {
                idCategoriaOld.getProductoCollection().remove(producto);
                idCategoriaOld = em.merge(idCategoriaOld);
            }
            if (idCategoriaNew != null && !idCategoriaNew.equals(idCategoriaOld)) {
                idCategoriaNew.getProductoCollection().add(producto);
                idCategoriaNew = em.merge(idCategoriaNew);
            }
            for (ProductoIngrediente productoIngredienteCollectionNewProductoIngrediente : productoIngredienteCollectionNew) {
                if (!productoIngredienteCollectionOld.contains(productoIngredienteCollectionNewProductoIngrediente)) {
                    Producto oldIdProductoOfProductoIngredienteCollectionNewProductoIngrediente = productoIngredienteCollectionNewProductoIngrediente.getIdProducto();
                    productoIngredienteCollectionNewProductoIngrediente.setIdProducto(producto);
                    productoIngredienteCollectionNewProductoIngrediente = em.merge(productoIngredienteCollectionNewProductoIngrediente);
                    if (oldIdProductoOfProductoIngredienteCollectionNewProductoIngrediente != null && !oldIdProductoOfProductoIngredienteCollectionNewProductoIngrediente.equals(producto)) {
                        oldIdProductoOfProductoIngredienteCollectionNewProductoIngrediente.getProductoIngredienteCollection().remove(productoIngredienteCollectionNewProductoIngrediente);
                        oldIdProductoOfProductoIngredienteCollectionNewProductoIngrediente = em.merge(oldIdProductoOfProductoIngredienteCollectionNewProductoIngrediente);
                    }
                }
            }
            for (PedidoProducto pedidoProductoCollectionNewPedidoProducto : pedidoProductoCollectionNew) {
                if (!pedidoProductoCollectionOld.contains(pedidoProductoCollectionNewPedidoProducto)) {
                    Producto oldIdProductoOfPedidoProductoCollectionNewPedidoProducto = pedidoProductoCollectionNewPedidoProducto.getIdProducto();
                    pedidoProductoCollectionNewPedidoProducto.setIdProducto(producto);
                    pedidoProductoCollectionNewPedidoProducto = em.merge(pedidoProductoCollectionNewPedidoProducto);
                    if (oldIdProductoOfPedidoProductoCollectionNewPedidoProducto != null && !oldIdProductoOfPedidoProductoCollectionNewPedidoProducto.equals(producto)) {
                        oldIdProductoOfPedidoProductoCollectionNewPedidoProducto.getPedidoProductoCollection().remove(pedidoProductoCollectionNewPedidoProducto);
                        oldIdProductoOfPedidoProductoCollectionNewPedidoProducto = em.merge(oldIdProductoOfPedidoProductoCollectionNewPedidoProducto);
                    }
                }
            }
            for (Inventario inventarioCollectionNewInventario : inventarioCollectionNew) {
                if (!inventarioCollectionOld.contains(inventarioCollectionNewInventario)) {
                    Producto oldIdProductoOfInventarioCollectionNewInventario = inventarioCollectionNewInventario.getIdProducto();
                    inventarioCollectionNewInventario.setIdProducto(producto);
                    inventarioCollectionNewInventario = em.merge(inventarioCollectionNewInventario);
                    if (oldIdProductoOfInventarioCollectionNewInventario != null && !oldIdProductoOfInventarioCollectionNewInventario.equals(producto)) {
                        oldIdProductoOfInventarioCollectionNewInventario.getInventarioCollection().remove(inventarioCollectionNewInventario);
                        oldIdProductoOfInventarioCollectionNewInventario = em.merge(oldIdProductoOfInventarioCollectionNewInventario);
                    }
                }
            }
            em.getTransaction().commit();
        } catch (Exception ex) {
            String msg = ex.getLocalizedMessage();
            if (msg == null || msg.length() == 0) {
                Integer id = producto.getIdProducto();
                if (findProducto(id) == null) {
                    throw new NonexistentEntityException("The producto with id " + id + " no longer exists.");
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
            Producto producto;
            try {
                producto = em.getReference(Producto.class, id);
                producto.getIdProducto();
            } catch (EntityNotFoundException enfe) {
                throw new NonexistentEntityException("The producto with id " + id + " no longer exists.", enfe);
            }
            List<String> illegalOrphanMessages = null;
            Collection<ProductoIngrediente> productoIngredienteCollectionOrphanCheck = producto.getProductoIngredienteCollection();
            for (ProductoIngrediente productoIngredienteCollectionOrphanCheckProductoIngrediente : productoIngredienteCollectionOrphanCheck) {
                if (illegalOrphanMessages == null) {
                    illegalOrphanMessages = new ArrayList<String>();
                }
                illegalOrphanMessages.add("This Producto (" + producto + ") cannot be destroyed since the ProductoIngrediente " + productoIngredienteCollectionOrphanCheckProductoIngrediente + " in its productoIngredienteCollection field has a non-nullable idProducto field.");
            }
            Collection<PedidoProducto> pedidoProductoCollectionOrphanCheck = producto.getPedidoProductoCollection();
            for (PedidoProducto pedidoProductoCollectionOrphanCheckPedidoProducto : pedidoProductoCollectionOrphanCheck) {
                if (illegalOrphanMessages == null) {
                    illegalOrphanMessages = new ArrayList<String>();
                }
                illegalOrphanMessages.add("This Producto (" + producto + ") cannot be destroyed since the PedidoProducto " + pedidoProductoCollectionOrphanCheckPedidoProducto + " in its pedidoProductoCollection field has a non-nullable idProducto field.");
            }
            Collection<Inventario> inventarioCollectionOrphanCheck = producto.getInventarioCollection();
            for (Inventario inventarioCollectionOrphanCheckInventario : inventarioCollectionOrphanCheck) {
                if (illegalOrphanMessages == null) {
                    illegalOrphanMessages = new ArrayList<String>();
                }
                illegalOrphanMessages.add("This Producto (" + producto + ") cannot be destroyed since the Inventario " + inventarioCollectionOrphanCheckInventario + " in its inventarioCollection field has a non-nullable idProducto field.");
            }
            if (illegalOrphanMessages != null) {
                throw new IllegalOrphanException(illegalOrphanMessages);
            }
            Categoria idCategoria = producto.getIdCategoria();
            if (idCategoria != null) {
                idCategoria.getProductoCollection().remove(producto);
                idCategoria = em.merge(idCategoria);
            }
            em.remove(producto);
            em.getTransaction().commit();
        } finally {
            if (em != null) {
                em.close();
            }
        }
    }

    public List<Producto> findProductoEntities() {
        return findProductoEntities(true, -1, -1);
    }

    public List<Producto> findProductoEntities(int maxResults, int firstResult) {
        return findProductoEntities(false, maxResults, firstResult);
    }

    private List<Producto> findProductoEntities(boolean all, int maxResults, int firstResult) {
        EntityManager em = getEntityManager();
        try {
            CriteriaQuery cq = em.getCriteriaBuilder().createQuery();
            cq.select(cq.from(Producto.class));
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

    public Producto findProducto(Integer id) {
        EntityManager em = getEntityManager();
        try {
            return em.find(Producto.class, id);
        } finally {
            em.close();
        }
    }

    public int getProductoCount() {
        EntityManager em = getEntityManager();
        try {
            CriteriaQuery cq = em.getCriteriaBuilder().createQuery();
            Root<Producto> rt = cq.from(Producto.class);
            cq.select(em.getCriteriaBuilder().count(rt));
            Query q = em.createQuery(cq);
            return ((Long) q.getSingleResult()).intValue();
        } finally {
            em.close();
        }
    }
    
}
