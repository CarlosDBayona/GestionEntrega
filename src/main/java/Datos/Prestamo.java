/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package Datos;

import java.io.Serializable;
import java.util.Collection;
import java.util.Date;
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
import javax.persistence.Temporal;
import javax.persistence.TemporalType;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Size;
import javax.xml.bind.annotation.XmlRootElement;
import javax.xml.bind.annotation.XmlTransient;

/**
 *
 * @author Carlos
 */
@Entity
@Table(name = "prestamo")
@XmlRootElement
@NamedQueries({
    @NamedQuery(name = "Prestamo.findAll", query = "SELECT p FROM Prestamo p")
    , @NamedQuery(name = "Prestamo.findByCodPrestamo", query = "SELECT p FROM Prestamo p WHERE p.codPrestamo = :codPrestamo")
    , @NamedQuery(name = "Prestamo.findByFechaEntrada", query = "SELECT p FROM Prestamo p WHERE p.fechaEntrada = :fechaEntrada")
    , @NamedQuery(name = "Prestamo.findByFechaSalida", query = "SELECT p FROM Prestamo p WHERE p.fechaSalida = :fechaSalida")
    , @NamedQuery(name = "Prestamo.findByTipoPrestamo", query = "SELECT p FROM Prestamo p WHERE p.tipoPrestamo = :tipoPrestamo")})
public class Prestamo implements Serializable {

    private static final long serialVersionUID = 1L;
    @Id
    @Basic(optional = false)
    @NotNull
    @Size(min = 1, max = 20)
    @Column(name = "codPrestamo")
    private String codPrestamo;
    @Basic(optional = false)
    @NotNull
    @Column(name = "fechaEntrada")
    @Temporal(TemporalType.TIMESTAMP)
    private Date fechaEntrada;
    @Basic(optional = false)
    @NotNull
    @Column(name = "fechaSalida")
    @Temporal(TemporalType.TIMESTAMP)
    private Date fechaSalida;
    @Basic(optional = false)
    @NotNull
    @Size(min = 1, max = 50)
    @Column(name = "tipoPrestamo")
    private String tipoPrestamo;
    @JoinColumn(name = "idUsuario", referencedColumnName = "idUsuario")
    @ManyToOne(optional = false)
    private Usuario idUsuario;
    @JoinColumn(name = "idAdministrativo", referencedColumnName = "idAdministrativo")
    @ManyToOne(optional = false)
    private Administrativo idAdministrativo;
    @OneToMany(cascade = CascadeType.ALL, mappedBy = "codPrestamo")
    private Collection<Transaccion> transaccionCollection;

    public Prestamo() {
    }

    public Prestamo(String codPrestamo) {
        this.codPrestamo = codPrestamo;
    }

    public Prestamo(String codPrestamo, Date fechaEntrada, Date fechaSalida, String tipoPrestamo) {
        this.codPrestamo = codPrestamo;
        this.fechaEntrada = fechaEntrada;
        this.fechaSalida = fechaSalida;
        this.tipoPrestamo = tipoPrestamo;
    }

    public String getCodPrestamo() {
        return codPrestamo;
    }

    public void setCodPrestamo(String codPrestamo) {
        this.codPrestamo = codPrestamo;
    }

    public Date getFechaEntrada() {
        return fechaEntrada;
    }

    public void setFechaEntrada(Date fechaEntrada) {
        this.fechaEntrada = fechaEntrada;
    }

    public Date getFechaSalida() {
        return fechaSalida;
    }

    public void setFechaSalida(Date fechaSalida) {
        this.fechaSalida = fechaSalida;
    }

    public String getTipoPrestamo() {
        return tipoPrestamo;
    }

    public void setTipoPrestamo(String tipoPrestamo) {
        this.tipoPrestamo = tipoPrestamo;
    }

    public Usuario getIdUsuario() {
        return idUsuario;
    }

    public void setIdUsuario(Usuario idUsuario) {
        this.idUsuario = idUsuario;
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

    @Override
    public int hashCode() {
        int hash = 0;
        hash += (codPrestamo != null ? codPrestamo.hashCode() : 0);
        return hash;
    }

    @Override
    public boolean equals(Object object) {
        // TODO: Warning - this method won't work in the case the id fields are not set
        if (!(object instanceof Prestamo)) {
            return false;
        }
        Prestamo other = (Prestamo) object;
        if ((this.codPrestamo == null && other.codPrestamo != null) || (this.codPrestamo != null && !this.codPrestamo.equals(other.codPrestamo))) {
            return false;
        }
        return true;
    }

    @Override
    public String toString() {
        return "Datos.Prestamo[ codPrestamo=" + codPrestamo + " ]";
    }
    
}
