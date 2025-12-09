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
@Table(name = "pedido_producto")
@XmlRootElement
@NamedQueries({
    @NamedQuery(name = "PedidoProducto.findAll", query = "SELECT p FROM PedidoProducto p")
    , @NamedQuery(name = "PedidoProducto.findByIdPedidoProducto", query = "SELECT p FROM PedidoProducto p WHERE p.idPedidoProducto = :idPedidoProducto")
    , @NamedQuery(name = "PedidoProducto.findByCantidad", query = "SELECT p FROM PedidoProducto p WHERE p.cantidad = :cantidad")
    , @NamedQuery(name = "PedidoProducto.findBySubtotal", query = "SELECT p FROM PedidoProducto p WHERE p.subtotal = :subtotal")})
public class PedidoProducto implements Serializable {

    private static final long serialVersionUID = 1L;
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Basic(optional = false)
    @Column(name = "id_pedido_producto")
    private Integer idPedidoProducto;
    @Basic(optional = false)
    @Column(name = "cantidad")
    private int cantidad;
    // @Max(value=?)  @Min(value=?)//if you know range of your decimal fields consider using these annotations to enforce field validation
    @Basic(optional = false)
    @Column(name = "subtotal")
    private BigDecimal subtotal;
    @JoinColumn(name = "id_pedido", referencedColumnName = "id_pedido")
    @ManyToOne(optional = false)
    private Pedidos idPedido;
    @JoinColumn(name = "id_producto", referencedColumnName = "id_producto")
    @ManyToOne(optional = false)
    private Producto idProducto;

    public PedidoProducto() {
    }

    public PedidoProducto(Integer idPedidoProducto) {
        this.idPedidoProducto = idPedidoProducto;
    }

    public PedidoProducto(Integer idPedidoProducto, int cantidad, BigDecimal subtotal) {
        this.idPedidoProducto = idPedidoProducto;
        this.cantidad = cantidad;
        this.subtotal = subtotal;
    }

    public Integer getIdPedidoProducto() {
        return idPedidoProducto;
    }

    public void setIdPedidoProducto(Integer idPedidoProducto) {
        this.idPedidoProducto = idPedidoProducto;
    }

    public int getCantidad() {
        return cantidad;
    }

    public void setCantidad(int cantidad) {
        this.cantidad = cantidad;
    }

    public BigDecimal getSubtotal() {
        return subtotal;
    }

    public void setSubtotal(BigDecimal subtotal) {
        this.subtotal = subtotal;
    }

    public Pedidos getIdPedido() {
        return idPedido;
    }

    public void setIdPedido(Pedidos idPedido) {
        this.idPedido = idPedido;
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
        hash += (idPedidoProducto != null ? idPedidoProducto.hashCode() : 0);
        return hash;
    }

    @Override
    public boolean equals(Object object) {
        // TODO: Warning - this method won't work in the case the id fields are not set
        if (!(object instanceof PedidoProducto)) {
            return false;
        }
        PedidoProducto other = (PedidoProducto) object;
        if ((this.idPedidoProducto == null && other.idPedidoProducto != null) || (this.idPedidoProducto != null && !this.idPedidoProducto.equals(other.idPedidoProducto))) {
            return false;
        }
        return true;
    }

    @Override
    public String toString() {
        return "modelo.PedidoProducto[ idPedidoProducto=" + idPedidoProducto + " ]";
    }
    
}
