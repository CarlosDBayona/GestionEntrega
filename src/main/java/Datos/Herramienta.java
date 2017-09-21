/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package Datos;

import java.io.Serializable;
import java.util.Collection;
import javax.persistence.Basic;
import javax.persistence.CascadeType;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.NamedQueries;
import javax.persistence.NamedQuery;
import javax.persistence.OneToMany;
import javax.persistence.Table;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Size;
import javax.xml.bind.annotation.XmlRootElement;
import javax.xml.bind.annotation.XmlTransient;

/**
 *
 * @author Carlos
 */
@Entity
@Table(name = "herramienta")
@XmlRootElement
@NamedQueries({
    @NamedQuery(name = "Herramienta.findAll", query = "SELECT h FROM Herramienta h")
    , @NamedQuery(name = "Herramienta.findByIdHerramienta", query = "SELECT h FROM Herramienta h WHERE h.idHerramienta = :idHerramienta")
    , @NamedQuery(name = "Herramienta.findByNombreHerramienta", query = "SELECT h FROM Herramienta h WHERE h.nombreHerramienta = :nombreHerramienta")
    , @NamedQuery(name = "Herramienta.findByNoSerial", query = "SELECT h FROM Herramienta h WHERE h.noSerial = :noSerial")
    , @NamedQuery(name = "Herramienta.findByDescripcionHerramienta", query = "SELECT h FROM Herramienta h WHERE h.descripcionHerramienta = :descripcionHerramienta")
    , @NamedQuery(name = "Herramienta.findByEstado", query = "SELECT h FROM Herramienta h WHERE h.estado = :estado")})
public class Herramienta implements Serializable {

    private static final long serialVersionUID = 1L;
    @Id
    @Basic(optional = false)
    @NotNull
    @Size(min = 1, max = 50)
    @Column(name = "idHerramienta")
    private String idHerramienta;
    @Size(max = 20)
    @Column(name = "nombreHerramienta")
    private String nombreHerramienta;
    @Size(max = 20)
    @Column(name = "noSerial")
    private String noSerial;
    @Size(max = 50)
    @Column(name = "descripcionHerramienta")
    private String descripcionHerramienta;
    @Basic(optional = false)
    @NotNull
    @Size(min = 1, max = 20)
    @Column(name = "estado")
    private String estado;
    @JoinColumn(name = "idAdministrativo", referencedColumnName = "idAdministrativo")
    @ManyToOne(optional = false)
    private Administrativo idAdministrativo;
    @OneToMany(cascade = CascadeType.ALL, mappedBy = "idHerramienta")
    private Collection<Transaccion> transaccionCollection;
    @OneToMany(cascade = CascadeType.ALL, mappedBy = "idHerramienta")
    private Collection<Mantenimiento> mantenimientoCollection;

    public Herramienta() {
    }

    public Herramienta(String idHerramienta) {
        this.idHerramienta = idHerramienta;
    }

    public Herramienta(String idHerramienta, String estado) {
        this.idHerramienta = idHerramienta;
        this.estado = estado;
    }

    public String getIdHerramienta() {
        return idHerramienta;
    }

    public void setIdHerramienta(String idHerramienta) {
        this.idHerramienta = idHerramienta;
    }

    public String getNombreHerramienta() {
        return nombreHerramienta;
    }

    public void setNombreHerramienta(String nombreHerramienta) {
        this.nombreHerramienta = nombreHerramienta;
    }

    public String getNoSerial() {
        return noSerial;
    }

    public void setNoSerial(String noSerial) {
        this.noSerial = noSerial;
    }

    public String getDescripcionHerramienta() {
        return descripcionHerramienta;
    }

    public void setDescripcionHerramienta(String descripcionHerramienta) {
        this.descripcionHerramienta = descripcionHerramienta;
    }

    public String getEstado() {
        return estado;
    }

    public void setEstado(String estado) {
        this.estado = estado;
    }

    public Administrativo getIdAdministrativo() {
        return idAdministrativo;
    }

    public void setIdAdministrativo(Administrativo idAdministrativo) {
        this.idAdministrativo = idAdministrativo;
    }

    @XmlTransient
    public Collection<Transaccion> getTransaccionCollection() {
        return transaccionCollection;
    }

    public void setTransaccionCollection(Collection<Transaccion> transaccionCollection) {
        this.transaccionCollection = transaccionCollection;
    }

    @XmlTransient
    public Collection<Mantenimiento> getMantenimientoCollection() {
        return mantenimientoCollection;
    }

    public void setMantenimientoCollection(Collection<Mantenimiento> mantenimientoCollection) {
        this.mantenimientoCollection = mantenimientoCollection;
    }

    @Override
    public int hashCode() {
        int hash = 0;
        hash += (idHerramienta != null ? idHerramienta.hashCode() : 0);
        return hash;
    }

    @Override
    public boolean equals(Object object) {
        // TODO: Warning - this method won't work in the case the id fields are not set
        if (!(object instanceof Herramienta)) {
            return false;
        }
        Herramienta other = (Herramienta) object;
        if ((this.idHerramienta == null && other.idHerramienta != null) || (this.idHerramienta != null && !this.idHerramienta.equals(other.idHerramienta))) {
            return false;
        }
        return true;
    }

    @Override
    public String toString() {
        return "Datos.Herramienta[ idHerramienta=" + idHerramienta + " ]";
    }
    
}
