/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package modelo;

import java.io.Serializable;
import java.math.BigDecimal;
import javax.persistence.Basic;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.NamedQueries;
import javax.persistence.NamedQuery;
import javax.persistence.Table;
import javax.xml.bind.annotation.XmlRootElement;

/**
 *
 * @author Talli
 */
@Entity
@Table(name = "producto_ingrediente")
@XmlRootElement
@NamedQueries({
    @NamedQuery(name = "ProductoIngrediente.findAll", query = "SELECT p FROM ProductoIngrediente p")
    , @NamedQuery(name = "ProductoIngrediente.findByIdProductoIngrediente", query = "SELECT p FROM ProductoIngrediente p WHERE p.idProductoIngrediente = :idProductoIngrediente")
    , @NamedQuery(name = "ProductoIngrediente.findByCantidadNecesaria", query = "SELECT p FROM ProductoIngrediente p WHERE p.cantidadNecesaria = :cantidadNecesaria")})
public class ProductoIngrediente implements Serializable {

    private static final long serialVersionUID = 1L;
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Basic(optional = false)
    @Column(name = "id_producto_ingrediente")
    private Integer idProductoIngrediente;
    // @Max(value=?)  @Min(value=?)//if you know range of your decimal fields consider using these annotations to enforce field validation
    @Basic(optional = false)
    @Column(name = "cantidad_necesaria")
    private BigDecimal cantidadNecesaria;
    @JoinColumn(name = "id_ingrediente", referencedColumnName = "id_ingrediente")
    @ManyToOne(optional = false)
    private Ingrediente idIngrediente;
    @JoinColumn(name = "id_producto", referencedColumnName = "id_producto")
    @ManyToOne(optional = false)
    private Producto idProducto;

    public ProductoIngrediente() {
    }

    public ProductoIngrediente(Integer idProductoIngrediente) {
        this.idProductoIngrediente = idProductoIngrediente;
    }

    public ProductoIngrediente(Integer idProductoIngrediente, BigDecimal cantidadNecesaria) {
        this.idProductoIngrediente = idProductoIngrediente;
        this.cantidadNecesaria = cantidadNecesaria;
    }

    public Integer getIdProductoIngrediente() {
        return idProductoIngrediente;
    }

    public void setIdProductoIngrediente(Integer idProductoIngrediente) {
        this.idProductoIngrediente = idProductoIngrediente;
    }

    public BigDecimal getCantidadNecesaria() {
        return cantidadNecesaria;
    }

    public void setCantidadNecesaria(BigDecimal cantidadNecesaria) {
        this.cantidadNecesaria = cantidadNecesaria;
    }

    public Ingrediente getIdIngrediente() {
        return idIngrediente;
    }

    public void setIdIngrediente(Ingrediente idIngrediente) {
        this.idIngrediente = idIngrediente;
    }

    public Producto getIdProducto() {
        return idProducto;
    }

    public void setIdProducto(Producto idProducto) {
        this.idProducto = idProducto;
    }

    @Override
    public int hashCode() {
        int hash = 0;
        hash += (idProductoIngrediente != null ? idProductoIngrediente.hashCode() : 0);
        return hash;
    }

    @Override
    public boolean equals(Object object) {
        // TODO: Warning - this method won't work in the case the id fields are not set
        if (!(object instanceof ProductoIngrediente)) {
            return false;
        }
        ProductoIngrediente other = (ProductoIngrediente) object;
        if ((this.idProductoIngrediente == null && other.idProductoIngrediente != null) || (this.idProductoIngrediente != null && !this.idProductoIngrediente.equals(other.idProductoIngrediente))) {
            return false;
        }
        return true;
    }

    @Override
    public String toString() {
        return "modelo.ProductoIngrediente[ idProductoIngrediente=" + idProductoIngrediente + " ]";
    }
    
}
