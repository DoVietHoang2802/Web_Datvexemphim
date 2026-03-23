package com.datvexemphim.domain.repository;

import com.datvexemphim.domain.entity.FoodOrder;
import com.datvexemphim.domain.entity.FoodOrderItem;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface FoodOrderItemRepository extends JpaRepository<FoodOrderItem, Long> {
    
    @Query("SELECT foi FROM FoodOrderItem foi WHERE foi.foodOrder = :foodOrder")
    List<FoodOrderItem> findByFoodOrder(@Param("foodOrder") FoodOrder foodOrder);
    
    @Query("SELECT foi FROM FoodOrderItem foi WHERE foi.foodOrder.id = :foodOrderId")
    List<FoodOrderItem> findByFoodOrderId(@Param("foodOrderId") Long foodOrderId);
}
