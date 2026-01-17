package com.dulcefina.entity;

import com.fasterxml.jackson.annotation.JsonIgnore;
import jakarta.persistence.*;
import lombok.*;

import java.util.ArrayList;
import java.util.List;

@Entity
@Table(name = "option_value")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class OptionValue {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "option_value_id")
    private Long optionValueId;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "option_group_id", nullable = false)
    @JsonIgnore
    private OptionGroup optionGroup;

    @Column(name = "name", nullable = false, length = 150)
    private String name;

    @Column(name = "slug", length = 150)
    private String slug;

    @Column(name = "price_modifier")
    private Double priceModifier = 0.0;

    @Column(name = "multiplier")
    private Double multiplier = 1.0;

    @Column(name = "image_url", length = 500)
    private String imageUrl;

    @Column(name = "is_active")
    private Boolean isActive = true;

    @OneToMany(mappedBy = "optionValue", cascade = CascadeType.ALL, orphanRemoval = true, fetch = FetchType.LAZY)
    @Builder.Default
    private List<RecipeItem> recipeItems = new ArrayList<>();
}
