package com.dulcefina.service;

import com.dulcefina.entity.Driver;

import java.util.List;
import java.util.Optional;

public interface DriverService {
    List<Driver> findAll();
    Optional<Driver> findById(Long id);
    Driver create(Driver driver);
    Driver update(Long id, Driver driver);
    void delete(Long id);
}
