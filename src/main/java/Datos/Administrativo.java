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
@Table(name = "administrativo")
@XmlRootElement
@NamedQueries({
    @NamedQuery(name = "Administrativo.findAll", query = "SELECT a FROM Administrativo a")
    , @NamedQuery(name = "Administrativo.findByIdAdministrativo", query = "SELECT a FROM Administrativo a WHERE a.idAdministrativo = :idAdministrativo")
    , @NamedQuery(name = "Administrativo.findByNombreAdmin", query = "SELECT a FROM Administrativo a WHERE a.nombreAdmin = :nombreAdmin")
    , @NamedQuery(name = "Administrativo.findByContrasena", query = "SELECT a FROM Administrativo a WHERE a.contrasena = :contrasena")
    , @NamedQuery(name = "Administrativo.findByTipoPermiso", query = "SELECT a FROM Administrativo a WHERE a.tipoPermiso = :tipoPermiso")})
public class Administrativo implements Serializable {

    private static final long serialVersionUID = 1L;
    @Id
    @Basic(optional = false)
    @NotNull
    @Size(min = 1, max = 50)
    @Column(name = "idAdministrativo")
    private String idAdministrativo;
    @Size(max = 20)
    @Column(name = "nombreAdmin")
    private String nombreAdmin;
    @Size(max = 20)
    @Column(name = "contrasena")
    private String contrasena;
    @Size(max = 20)
    @Column(name = "tipoPermiso")
    private String tipoPermiso;
    @OneToMany(cascade = CascadeType.ALL, mappedBy = "idAdministrativo")
    private Collection<Prestamo> prestamoCollection;
    @OneToMany(cascade = CascadeType.ALL, mappedBy = "idAdministrativo")
    private Collection<Herramienta> herramientaCollection;

    public Administrativo() {
    }

    public Administrativo(String idAdministrativo) {
        this.idAdministrativo = idAdministrativo;
    }

    public String getIdAdministrativo() {
        return idAdministrativo;
    }

    public void setIdAdministrativo(String idAdministrativo) {
        this.idAdministrativo = idAdministrativo;
    }

    public String getNombreAdmin() {
        return nombreAdmin;
    }

    public void setNombreAdmin(String nombreAdmin) {
        this.nombreAdmin = nombreAdmin;
    }

    public String getContrasena() {
        return contrasena;
    }

    public void setContrasena(String contrasena) {
        this.contrasena = contrasena;
    }

    public String getTipoPermiso() {
        return tipoPermiso;
    }

    public void setTipoPermiso(String tipoPermiso) {
        this.tipoPermiso = tipoPermiso;
    }

    @XmlTransient
    public Collection<Prestamo> getPrestamoCollection() {
        return prestamoCollection;
    }

    public void setPrestamoCollection(Collection<Prestamo> prestamoCollection) {
        this.prestamoCollection = prestamoCollection;
    }

    @XmlTransient
    public Collection<Herramienta> getHerramientaCollection() {
        return herramientaCollection;
    }

    public void setHerramientaCollection(Collection<Herramienta> herramientaCollection) {
        this.herramientaCollection = herramientaCollection;
    }

    @Override
    public int hashCode() {
        int hash = 0;
        hash += (idAdministrativo != null ? idAdministrativo.hashCode() : 0);
        return hash;
    }

    @Override
    public boolean equals(Object object) {
        // TODO: Warning - this method won't work in the case the id fields are not set
        if (!(object instanceof Administrativo)) {
            return false;
        }
        Administrativo other = (Administrativo) object;
        if ((this.idAdministrativo == null && other.idAdministrativo != null) || (this.idAdministrativo != null && !this.idAdministrativo.equals(other.idAdministrativo))) {
            return false;
        }
        return true;
    }

    @Override
    public String toString() {
        return "Datos.Administrativo[ idAdministrativo=" + idAdministrativo + " ]";
    }
    
}
