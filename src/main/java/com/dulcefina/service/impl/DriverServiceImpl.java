package com.dulcefina.service.impl;

import com.dulcefina.entity.Driver;
import com.dulcefina.repository.DriverRepository;
import com.dulcefina.service.DriverService;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.NoSuchElementException;
import java.util.Optional;

@Service
@Transactional
public class DriverServiceImpl implements DriverService {

    private final DriverRepository driverRepository;

    public DriverServiceImpl(DriverRepository driverRepository) {
        this.driverRepository = driverRepository;
    }

    @Override
    public List<Driver> findAll() {
        return driverRepository.findAll();
    }

    @Override
    public Optional<Driver> findById(Long id) {
        return driverRepository.findById(id);
    }

    @Override
    public Driver create(Driver driver) {
        return driverRepository.save(driver);
    }

    @Override
    public Driver update(Long id, Driver driver) {
        Driver existing = driverRepository.findById(id)
                .orElseThrow(() -> new NoSuchElementException("Driver not found: " + id));
        existing.setName(driver.getName());
        existing.setPhone(driver.getPhone());
        existing.setVehiclePlate(driver.getVehiclePlate());
        return driverRepository.save(existing);
    }

    @Override
    public void delete(Long id) {
        if (!driverRepository.existsById(id)) {
            throw new NoSuchElementException("Driver not found: " + id);
        }
        driverRepository.deleteById(id);
    }
}
