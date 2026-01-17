package com.dulcefina.entity;

import jakarta.persistence.*;
import lombok.*;

@Entity
@Table(name = "driver")
@Getter @Setter
@NoArgsConstructor @AllArgsConstructor @Builder
public class Driver {

    @Id
    @Column(name = "driver_id")
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long driverId;

    @Column(name = "name")
    private String name;

    @Column(name = "phone")
    private String phone;

    @Column(name = "vehicle_plate")
    private String vehiclePlate;
}
