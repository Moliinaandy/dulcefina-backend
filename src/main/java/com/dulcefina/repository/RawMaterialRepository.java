package com.dulcefina.repository;

import com.dulcefina.entity.RawMaterial;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Lock;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import jakarta.persistence.LockModeType;

public interface RawMaterialRepository extends JpaRepository<RawMaterial, Long> {

    @Lock(LockModeType.PESSIMISTIC_WRITE)
    @Query("select r from RawMaterial r where r.rawId = :id")
    RawMaterial findByRawIdForUpdate(@Param("id") Long id);
}
