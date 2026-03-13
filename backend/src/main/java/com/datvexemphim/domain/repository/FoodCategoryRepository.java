package com.datvexemphim.domain.repository;

import com.datvexemphim.domain.entity.FoodCategory;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface FoodCategoryRepository extends JpaRepository<FoodCategory, Long> {
    
    Optional<FoodCategory> findByName(String name);
    
    @Query("SELECT fc FROM FoodCategory fc WHERE fc.isActive = true ORDER BY fc.displayOrder ASC, fc.id ASC")
    List<FoodCategory> findAllActive();
}
