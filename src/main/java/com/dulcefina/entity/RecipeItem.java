package com.dulcefina.entity;

import com.fasterxml.jackson.annotation.JsonIgnore;
import jakarta.persistence.*;
import lombok.*;

@Entity
@Table(name = "recipe_item")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class RecipeItem {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "recipe_item_id")
    private Long recipeItemId;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "option_value_id", nullable = false)
    @JsonIgnore
    private OptionValue optionValue;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "raw_id", nullable = false)
    private RawMaterial rawMaterial;

    @Column(name = "quantity", nullable = false)
    private Double quantity;

    @Column(name = "unit_measure", length = 50)
    private String unitMeasure;
}
