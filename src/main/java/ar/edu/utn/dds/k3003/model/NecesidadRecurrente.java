package ar.edu.utn.dds.k3003.model;

import jakarta.persistence.DiscriminatorValue;
import jakarta.persistence.Entity;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import java.time.LocalDate;

@Getter
@Setter
@Entity
@DiscriminatorValue("RECURRENTE")
@NoArgsConstructor
public class NecesidadRecurrente extends NecesidadMaterial {

    private LocalDate ultimaEntrega;

    @Override
    public void satisfacer(Integer cantidad) {
        super.satisfacer(cantidad);
        this.ultimaEntrega = LocalDate.now();
    }
}