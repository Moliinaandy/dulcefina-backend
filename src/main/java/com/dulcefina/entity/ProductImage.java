package com.dulcefina.entity;

import com.fasterxml.jackson.annotation.JsonBackReference;
import jakarta.persistence.*;
import lombok.*;

@Entity
@Table(name = "product_image")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class ProductImage {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "image_id")
    private Long imageId;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "product_id", nullable = false)
    @JsonBackReference
    private Product product;

    @Column(nullable = false, length = 500)
    private String url; // secure_url

    @Column(name = "public_id", length = 500)
    private String publicId;

    @Column(name = "alt_text", length = 255)
    private String altText;

    @Column
    private Integer priority = 0;

    @Column(name = "is_active")
    private Boolean isActive = true;
}
