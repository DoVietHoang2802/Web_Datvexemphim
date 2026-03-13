package com.datvexemphim.domain.repository;

import com.datvexemphim.domain.entity.FoodCategory;
import com.datvexemphim.domain.entity.FoodItem;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface FoodItemRepository extends JpaRepository<FoodItem, Long> {
    
    Optional<FoodItem> findByName(String name);
    
    @Query("SELECT fi FROM FoodItem fi WHERE fi.isAvailable = true ORDER BY fi.displayOrder ASC, fi.id ASC")
    List<FoodItem> findAllAvailable();
    
    @Query("SELECT fi FROM FoodItem fi WHERE fi.category = :category AND fi.isAvailable = true ORDER BY fi.displayOrder ASC")
    List<FoodItem> findByCategoryAndAvailable(@Param("category") FoodCategory category);
    
    @Query("SELECT fi FROM FoodItem fi WHERE fi.category.id = :categoryId AND fi.isAvailable = true ORDER BY fi.displayOrder ASC")
    List<FoodItem> findByCategoryIdAndAvailable(@Param("categoryId") Long categoryId);
}
