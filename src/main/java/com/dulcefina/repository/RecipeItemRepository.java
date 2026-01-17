package com.dulcefina.repository;

import com.dulcefina.entity.RecipeItem;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface RecipeItemRepository extends JpaRepository<RecipeItem, Long> {
    List<RecipeItem> findByOptionValue_OptionValueId(Long optionValueId);
}
