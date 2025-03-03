package com.assignment.entity;
import java.io.Serializable;
import jakarta.persistence.Embeddable;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Embeddable
@AllArgsConstructor
@NoArgsConstructor
public class ProductItemTypeId implements Serializable {
    private String itemId;
    private Integer typeId;
}
