package ar.edu.utn.dds.k3003.model;

import jakarta.persistence.DiscriminatorValue;
import jakarta.persistence.Entity;
import lombok.NoArgsConstructor;

@Entity
@DiscriminatorValue("EXTRAORDINARIA")
@NoArgsConstructor
public class NecesidadExtraordinaria extends NecesidadMaterial {

}