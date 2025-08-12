package org.example.beerProj.entity;


import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.PrePersist;
import jakarta.persistence.Table;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.example.beerProj.component.IdGenerator;

@Data
@Entity
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Table(name = "store")
public class StoreEntity {

    @Id
    private String id;

    @NotBlank
    private String address;

    @NotNull
    private String phone;

    @PrePersist
    protected void assignIdIfMissing() {
        if (id == null) {
            id = IdGenerator.generate();
        }
    }
}
