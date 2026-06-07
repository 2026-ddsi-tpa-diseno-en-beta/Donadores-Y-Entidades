package ar.edu.utn.dds.k3003.model;

import java.util.ArrayList;
import java.util.List;

import jakarta.persistence.CascadeType;
import jakarta.persistence.Column;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.OneToMany;

public class EntidadBenefica {
    @Id
    private String entidadID;

    @Column
    private String razonSocial;

    @Column
    private String domicilio;

    @Column
    private String telefono;

    @Column
    private String correo;

    @OneToMany(cascade = CascadeType.ALL)
    @JoinColumn(name = "entidad_id")
    private List<NecesidadMaterial> necesidades = new ArrayList<>();


    

    public EntidadBenefica() {
    }

    public void agregarNecesidad(NecesidadMaterial nuevaNecesidad) {
        this.necesidades.add(nuevaNecesidad);
    }

    public String getEntidadID() {
        return entidadID;
    }

    public void setEntidadID(String entidadID) {
        this.entidadID = entidadID;
    }

    public String getRazonSocial() {
        return razonSocial;
    }

    public void setRazonSocial(String razonSocial) {
        this.razonSocial = razonSocial;
    }

    public String getDomicilio() {
        return domicilio;
    }

    public void setDomicilio(String domicilio) {
        this.domicilio = domicilio;
    }

    public String getTelefono() {
        return telefono;
    }

    public void setTelefono(String telefono) {
        this.telefono = telefono;
    }

    public String getCorreo() {
        return correo;
    }

    public void setCorreo(String correo) {
        this.correo = correo;
    }

    public List<NecesidadMaterial> getNecesidades() {
        return necesidades;
    }

    public void setNecesidades(List<NecesidadMaterial> necesidades) {
        this.necesidades = necesidades;
    }



    public String getId() {
        return this.entidadID;
    }   

    public void setId(String id) {
        this.entidadID = id;
    }



}