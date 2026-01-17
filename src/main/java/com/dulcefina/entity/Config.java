package com.dulcefina.entity;

import jakarta.persistence.*;
import lombok.*;
import java.time.LocalDateTime;

@Entity
@Table(name = "config")
@Getter @Setter
@NoArgsConstructor @AllArgsConstructor @Builder
public class Config {

    @Id
    @Column(name = "config_key", length = 100)
    private String configKey;

    @Lob
    @Column(name = "config_value")
    private String configValue;

    @Column(name = "updated_at")
    private LocalDateTime updatedAt;
}
