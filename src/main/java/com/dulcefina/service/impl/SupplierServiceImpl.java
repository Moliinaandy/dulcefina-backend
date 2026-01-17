package com.dulcefina.service.impl;

import com.dulcefina.dto.SupplierDTO;
import com.dulcefina.dto.SupplierRequestDTO;
import com.dulcefina.entity.Supplier;
import com.dulcefina.repository.SupplierRepository;
import com.dulcefina.service.SupplierService;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.NoSuchElementException;
import java.util.stream.Collectors;

@Service
@Transactional
public class SupplierServiceImpl implements SupplierService {

    private final SupplierRepository supplierRepository;

    public SupplierServiceImpl(SupplierRepository supplierRepository) {
        this.supplierRepository = supplierRepository;
    }


    private SupplierDTO toDTO(Supplier supplier) {
        return SupplierDTO.builder()
                .supplierId(supplier.getSupplierId())
                .name(supplier.getName())
                .email(supplier.getEmail())
                .contactPerson(supplier.getContactPerson())
                .phone(supplier.getPhone())
                .build();
    }

    @Override
    @Transactional(readOnly = true)
    public List<SupplierDTO> findAll() {
        return supplierRepository.findAll().stream()
                .map(this::toDTO)
                .collect(Collectors.toList());
    }

    @Override
    @Transactional(readOnly = true)
    public SupplierDTO findById(Long id) {
        return supplierRepository.findById(id)
                .map(this::toDTO)
                .orElseThrow(() -> new NoSuchElementException("Proveedor no encontrado: " + id));
    }

    @Override
    public SupplierDTO create(SupplierRequestDTO req) {
        if (supplierRepository.existsByName(req.getName())) {
            throw new IllegalArgumentException("Ya existe un proveedor con ese nombre.");
        }
        if (supplierRepository.existsByEmail(req.getEmail())) {
            throw new IllegalArgumentException("El email del proveedor ya está en uso.");
        }

        Supplier supplier = Supplier.builder()
                .name(req.getName())
                .email(req.getEmail())
                .contactPerson(req.getContactPerson())
                .phone(req.getPhone())
                .build();

        Supplier saved = supplierRepository.save(supplier);
        return toDTO(saved);
    }

    @Override
    public SupplierDTO update(Long id, SupplierRequestDTO req) {
        Supplier existing = supplierRepository.findById(id)
                .orElseThrow(() -> new NoSuchElementException("Proveedor no encontrado: " + id));

        if (supplierRepository.existsByNameAndSupplierIdNot(req.getName(), id)) {
            throw new IllegalArgumentException("Ya existe otro proveedor con ese nombre.");
        }
        if (supplierRepository.existsByEmailAndSupplierIdNot(req.getEmail(), id)) {
            throw new IllegalArgumentException("El email ya está en uso por otro proveedor.");
        }

        existing.setName(req.getName());
        existing.setEmail(req.getEmail());
        existing.setContactPerson(req.getContactPerson());
        existing.setPhone(req.getPhone());

        Supplier saved = supplierRepository.save(existing);
        return toDTO(saved);
    }

    @Override
    public void delete(Long id) {
        if (!supplierRepository.existsById(id)) {
            throw new NoSuchElementException("Proveedor no encontrado: " + id);
        }
        try {
            supplierRepository.deleteById(id);
        } catch (Exception e) {
            throw new IllegalStateException("No se puede eliminar el proveedor, está asignado a uno o más productos.");
        }
    }
}