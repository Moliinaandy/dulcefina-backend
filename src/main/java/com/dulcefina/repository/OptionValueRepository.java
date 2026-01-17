package com.dulcefina.repository;

import com.dulcefina.entity.OptionValue;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface OptionValueRepository extends JpaRepository<OptionValue, Long> {
    List<OptionValue> findByOptionGroup_OptionGroupId(Long optionGroupId);
    List<OptionValue> findByOptionGroup_Code(String code);
}
