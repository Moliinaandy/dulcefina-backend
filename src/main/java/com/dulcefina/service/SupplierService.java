package com.dulcefina.service;

import com.dulcefina.dto.SupplierDTO;
import com.dulcefina.dto.SupplierRequestDTO;

import java.util.List;

public interface SupplierService {

    List<SupplierDTO> findAll();

    SupplierDTO findById(Long id);

    SupplierDTO create(SupplierRequestDTO req);

    SupplierDTO update(Long id, SupplierRequestDTO req);

    void delete(Long id);
}