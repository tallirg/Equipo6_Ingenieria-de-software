/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package modelo;

import java.io.Serializable;
import java.util.Collection;
import javax.persistence.Basic;
import javax.persistence.CascadeType;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.NamedQueries;
import javax.persistence.NamedQuery;
import javax.persistence.OneToMany;
import javax.persistence.Table;
import javax.xml.bind.annotation.XmlRootElement;
import javax.xml.bind.annotation.XmlTransient;

/**
 *
 * @author Talli
 */
@Entity
@Table(name = "ingrediente")
@XmlRootElement
@NamedQueries({
    @NamedQuery(name = "Ingrediente.findAll", query = "SELECT i FROM Ingrediente i")
    , @NamedQuery(name = "Ingrediente.findByIdIngrediente", query = "SELECT i FROM Ingrediente i WHERE i.idIngrediente = :idIngrediente")
    , @NamedQuery(name = "Ingrediente.findByCodigo", query = "SELECT i FROM Ingrediente i WHERE i.codigo = :codigo")
    , @NamedQuery(name = "Ingrediente.findByNombre", query = "SELECT i FROM Ingrediente i WHERE i.nombre = :nombre")
    , @NamedQuery(name = "Ingrediente.findByEstado", query = "SELECT i FROM Ingrediente i WHERE i.estado = :estado")
    , @NamedQuery(name = "Ingrediente.findByCantidad", query = "SELECT i FROM Ingrediente i WHERE i.cantidad = :cantidad")})
public class Ingrediente implements Serializable {

    private static final long serialVersionUID = 1L;
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Basic(optional = false)
    @Column(name = "id_ingrediente")
    private Integer idIngrediente;
    @Basic(optional = false)
    @Column(name = "codigo", insertable = false, updatable = false)
    private String codigo;
    @Basic(optional = false)
    @Column(name = "nombre")
    private String nombre;
    @Basic(optional = false)
    @Column(name = "estado")
    private String estado;
    @Basic(optional = false)
    @Column(name = "cantidad")
    private int cantidad;
    @OneToMany(cascade = CascadeType.ALL, mappedBy = "idIngrediente")
    private Collection<ProductoIngrediente> productoIngredienteCollection;

    public Ingrediente() {
    }

    public Ingrediente(Integer idIngrediente) {
        this.idIngrediente = idIngrediente;
    }

    public Ingrediente(Integer idIngrediente, String codigo, String nombre, String estado, int cantidad) {
        this.idIngrediente = idIngrediente;
        this.codigo = codigo;
        this.nombre = nombre;
        this.estado = estado;
        this.cantidad = cantidad;
    }

    public Integer getIdIngrediente() {
        return idIngrediente;
    }

    public void setIdIngrediente(Integer idIngrediente) {
        this.idIngrediente = idIngrediente;
    }

    public String getCodigo() {
        return codigo;
    }

    public void setCodigo(String codigo) {
        this.codigo = codigo;
    }

    public String getNombre() {
        return nombre;
    }

    public void setNombre(String nombre) {
        this.nombre = nombre;
    }

    public String getEstado() {
        return estado;
    }

    public void setEstado(String estado) {
        this.estado = estado;
    }

    public int getCantidad() {
        return cantidad;
    }

    public void setCantidad(int cantidad) {
        this.cantidad = cantidad;
    }

    @XmlTransient
    public Collection<ProductoIngrediente> getProductoIngredienteCollection() {
        return productoIngredienteCollection;
    }

    public void setProductoIngredienteCollection(Collection<ProductoIngrediente> productoIngredienteCollection) {
        this.productoIngredienteCollection = productoIngredienteCollection;
    }

    @Override
    public int hashCode() {
        int hash = 0;
        hash += (idIngrediente != null ? idIngrediente.hashCode() : 0);
        return hash;
    }

    @Override
    public boolean equals(Object object) {
        // TODO: Warning - this method won't work in the case the id fields are not set
        if (!(object instanceof Ingrediente)) {
            return false;
        }
        Ingrediente other = (Ingrediente) object;
        if ((this.idIngrediente == null && other.idIngrediente != null) || (this.idIngrediente != null && !this.idIngrediente.equals(other.idIngrediente))) {
            return false;
        }
        return true;
    }

    @Override
    public String toString() {
        return "modelo.Ingrediente[ idIngrediente=" + idIngrediente + " ]";
    }
    
}
