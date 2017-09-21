/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package Datos;

import java.io.Serializable;
import java.util.Date;
import javax.persistence.Basic;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.NamedQueries;
import javax.persistence.NamedQuery;
import javax.persistence.Table;
import javax.persistence.Temporal;
import javax.persistence.TemporalType;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Size;
import javax.xml.bind.annotation.XmlRootElement;

/**
 *
 * @author Carlos
 */
@Entity
@Table(name = "mantenimiento")
@XmlRootElement
@NamedQueries({
    @NamedQuery(name = "Mantenimiento.findAll", query = "SELECT m FROM Mantenimiento m")
    , @NamedQuery(name = "Mantenimiento.findByIdMantenimiento", query = "SELECT m FROM Mantenimiento m WHERE m.idMantenimiento = :idMantenimiento")
    , @NamedQuery(name = "Mantenimiento.findByRefFabricante", query = "SELECT m FROM Mantenimiento m WHERE m.refFabricante = :refFabricante")
    , @NamedQuery(name = "Mantenimiento.findByEnServicio", query = "SELECT m FROM Mantenimiento m WHERE m.enServicio = :enServicio")
    , @NamedQuery(name = "Mantenimiento.findByNivelImportancia", query = "SELECT m FROM Mantenimiento m WHERE m.nivelImportancia = :nivelImportancia")
    , @NamedQuery(name = "Mantenimiento.findByTipoMantenimiento", query = "SELECT m FROM Mantenimiento m WHERE m.tipoMantenimiento = :tipoMantenimiento")
    , @NamedQuery(name = "Mantenimiento.findByEntidadCargo", query = "SELECT m FROM Mantenimiento m WHERE m.entidadCargo = :entidadCargo")
    , @NamedQuery(name = "Mantenimiento.findByMombreTecnico", query = "SELECT m FROM Mantenimiento m WHERE m.mombreTecnico = :mombreTecnico")
    , @NamedQuery(name = "Mantenimiento.findByFechaInicio", query = "SELECT m FROM Mantenimiento m WHERE m.fechaInicio = :fechaInicio")
    , @NamedQuery(name = "Mantenimiento.findByFechaFinal", query = "SELECT m FROM Mantenimiento m WHERE m.fechaFinal = :fechaFinal")})
public class Mantenimiento implements Serializable {

    private static final long serialVersionUID = 1L;
    @Id
    @Basic(optional = false)
    @NotNull
    @Size(min = 1, max = 50)
    @Column(name = "idMantenimiento")
    private String idMantenimiento;
    @Size(max = 50)
    @Column(name = "refFabricante")
    private String refFabricante;
    @Size(max = 50)
    @Column(name = "enServicio")
    private String enServicio;
    @Size(max = 5)
    @Column(name = "nivelImportancia")
    private String nivelImportancia;
    @Size(max = 10)
    @Column(name = "tipoMantenimiento")
    private String tipoMantenimiento;
    @Size(max = 50)
    @Column(name = "entidadCargo")
    private String entidadCargo;
    @Size(max = 50)
    @Column(name = "mombreTecnico")
    private String mombreTecnico;
    @Basic(optional = false)
    @NotNull
    @Column(name = "fechaInicio")
    @Temporal(TemporalType.TIMESTAMP)
    private Date fechaInicio;
    @Basic(optional = false)
    @NotNull
    @Column(name = "fechaFinal")
    @Temporal(TemporalType.TIMESTAMP)
    private Date fechaFinal;
    @JoinColumn(name = "idHerramienta", referencedColumnName = "idHerramienta")
    @ManyToOne(optional = false)
    private Herramienta idHerramienta;

    public Mantenimiento() {
    }

    public Mantenimiento(String idMantenimiento) {
        this.idMantenimiento = idMantenimiento;
    }

    public Mantenimiento(String idMantenimiento, Date fechaInicio, Date fechaFinal) {
        this.idMantenimiento = idMantenimiento;
        this.fechaInicio = fechaInicio;
        this.fechaFinal = fechaFinal;
    }

    public String getIdMantenimiento() {
        return idMantenimiento;
    }

    public void setIdMantenimiento(String idMantenimiento) {
        this.idMantenimiento = idMantenimiento;
    }

    public String getRefFabricante() {
        return refFabricante;
    }

    public void setRefFabricante(String refFabricante) {
        this.refFabricante = refFabricante;
    }

    public String getEnServicio() {
        return enServicio;
    }

    public void setEnServicio(String enServicio) {
        this.enServicio = enServicio;
    }

    public String getNivelImportancia() {
        return nivelImportancia;
    }

    public void setNivelImportancia(String nivelImportancia) {
        this.nivelImportancia = nivelImportancia;
    }

    public String getTipoMantenimiento() {
        return tipoMantenimiento;
    }

    public void setTipoMantenimiento(String tipoMantenimiento) {
        this.tipoMantenimiento = tipoMantenimiento;
    }

    public String getEntidadCargo() {
        return entidadCargo;
    }

    public void setEntidadCargo(String entidadCargo) {
        this.entidadCargo = entidadCargo;
    }

    public String getMombreTecnico() {
        return mombreTecnico;
    }

    public void setMombreTecnico(String mombreTecnico) {
        this.mombreTecnico = mombreTecnico;
    }

    public Date getFechaInicio() {
        return fechaInicio;
    }

    public void setFechaInicio(Date fechaInicio) {
        this.fechaInicio = fechaInicio;
    }

    public Date getFechaFinal() {
        return fechaFinal;
    }

    public void setFechaFinal(Date fechaFinal) {
        this.fechaFinal = fechaFinal;
    }

    public Herramienta getIdHerramienta() {
        return idHerramienta;
    }

    public void setIdHerramienta(Herramienta idHerramienta) {
        this.idHerramienta = idHerramienta;
    }

    @Override
    public int hashCode() {
        int hash = 0;
        hash += (idMantenimiento != null ? idMantenimiento.hashCode() : 0);
        return hash;
    }

    @Override
    public boolean equals(Object object) {
        // TODO: Warning - this method won't work in the case the id fields are not set
        if (!(object instanceof Mantenimiento)) {
            return false;
        }
        Mantenimiento other = (Mantenimiento) object;
        if ((this.idMantenimiento == null && other.idMantenimiento != null) || (this.idMantenimiento != null && !this.idMantenimiento.equals(other.idMantenimiento))) {
            return false;
        }
        return true;
    }

    @Override
    public String toString() {
        return "Datos.Mantenimiento[ idMantenimiento=" + idMantenimiento + " ]";
    }
    
}
