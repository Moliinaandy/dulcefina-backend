package com.dulcefina.entity;

import jakarta.persistence.*;
import lombok.*;

import java.util.ArrayList;
import java.util.List;

@Entity
@Table(name = "option_group")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class OptionGroup {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "option_group_id")
    private Long optionGroupId;

    @Column(name = "code", nullable = false, unique = true, length = 80)
    private String code; // ejemplo "SABOR", "TAMANIO", "COBERTURA"

    @Column(name = "name", nullable = false, length = 150)
    private String name;

    @Column(name = "is_active")
    private Boolean isActive = true;

    @OneToMany(mappedBy = "optionGroup", cascade = CascadeType.ALL, orphanRemoval = true, fetch = FetchType.LAZY)
    @Builder.Default
    private List<OptionValue> values = new ArrayList<>();
}
