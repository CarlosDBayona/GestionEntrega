/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package Datos;

import java.io.Serializable;
import javax.persistence.Basic;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.NamedQueries;
import javax.persistence.NamedQuery;
import javax.persistence.Table;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Size;
import javax.xml.bind.annotation.XmlRootElement;

/**
 *
 * @author Carlos
 */
@Entity
@Table(name = "transaccion")
@XmlRootElement
@NamedQueries({
    @NamedQuery(name = "Transaccion.findAll", query = "SELECT t FROM Transaccion t")
    , @NamedQuery(name = "Transaccion.findByIdTransaccion", query = "SELECT t FROM Transaccion t WHERE t.idTransaccion = :idTransaccion")
    , @NamedQuery(name = "Transaccion.findByObservaciones", query = "SELECT t FROM Transaccion t WHERE t.observaciones = :observaciones")})
public class Transaccion implements Serializable {

    private static final long serialVersionUID = 1L;
    @Id
    @Basic(optional = false)
    @NotNull
    @Size(min = 1, max = 50)
    @Column(name = "idTransaccion")
    private String idTransaccion;
    @Size(max = 50)
    @Column(name = "observaciones")
    private String observaciones;
    @JoinColumn(name = "idHerramienta", referencedColumnName = "idHerramienta")
    @ManyToOne(optional = false)
    private Herramienta idHerramienta;
    @JoinColumn(name = "codPrestamo", referencedColumnName = "codPrestamo")
    @ManyToOne(optional = false)
    private Prestamo codPrestamo;

    public Transaccion() {
    }

    public Transaccion(String idTransaccion) {
        this.idTransaccion = idTransaccion;
    }

    public String getIdTransaccion() {
        return idTransaccion;
    }

    public void setIdTransaccion(String idTransaccion) {
        this.idTransaccion = idTransaccion;
    }

    public String getObservaciones() {
        return observaciones;
    }

    public void setObservaciones(String observaciones) {
        this.observaciones = observaciones;
    }

    public Herramienta getIdHerramienta() {
        return idHerramienta;
    }

    public void setIdHerramienta(Herramienta idHerramienta) {
        this.idHerramienta = idHerramienta;
    }

    public Prestamo getCodPrestamo() {
        return codPrestamo;
    }

    public void setCodPrestamo(Prestamo codPrestamo) {
        this.codPrestamo = codPrestamo;
    }

    @Override
    public int hashCode() {
        int hash = 0;
        hash += (idTransaccion != null ? idTransaccion.hashCode() : 0);
        return hash;
    }

    @Override
    public boolean equals(Object object) {
        // TODO: Warning - this method won't work in the case the id fields are not set
        if (!(object instanceof Transaccion)) {
            return false;
        }
        Transaccion other = (Transaccion) object;
        if ((this.idTransaccion == null && other.idTransaccion != null) || (this.idTransaccion != null && !this.idTransaccion.equals(other.idTransaccion))) {
            return false;
        }
        return true;
    }

    @Override
    public String toString() {
        return "Datos.Transaccion[ idTransaccion=" + idTransaccion + " ]";
    }
    
}
