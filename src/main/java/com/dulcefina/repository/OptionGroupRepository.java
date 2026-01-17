package com.dulcefina.repository;

import com.dulcefina.entity.OptionGroup;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;
import java.util.List;

public interface OptionGroupRepository extends JpaRepository<OptionGroup, Long> {
    Optional<OptionGroup> findByCode(String code);
    List<OptionGroup> findAllByIsActiveTrue();
}
