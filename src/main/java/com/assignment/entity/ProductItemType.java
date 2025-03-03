package com.assignment.entity;

import java.io.Serializable;

import jakarta.persistence.*;
import lombok.Data;

@Entity
@Table(name = "product_item_type")
@Data
public class ProductItemType implements Serializable {

    @EmbeddedId
    private ProductItemTypeId id;

    @ManyToOne
    @MapsId("itemId") // liên kết với productId trong ProductItemTypeId
    @JoinColumn(name = "item_id")
    private Product product;

    @ManyToOne
    @MapsId("typeId") // liên kết với typeId trong ProductItemTypeId
    @JoinColumn(name = "type_id")
    private ItemType itemType;
}
