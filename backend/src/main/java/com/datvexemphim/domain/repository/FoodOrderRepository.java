package com.datvexemphim.domain.repository;

import com.datvexemphim.domain.entity.FoodOrder;
import com.datvexemphim.domain.entity.Payment;
import com.datvexemphim.domain.entity.Ticket;
import com.datvexemphim.domain.entity.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface FoodOrderRepository extends JpaRepository<FoodOrder, Long> {
    
    @Query("SELECT fo FROM FoodOrder fo WHERE fo.payment = :payment")
    List<FoodOrder> findByPayment(@Param("payment") Payment payment);
    
    @Query("SELECT fo FROM FoodOrder fo WHERE fo.ticket = :ticket")
    Optional<FoodOrder> findByTicket(@Param("ticket") Ticket ticket);
    
    @Query("SELECT fo FROM FoodOrder fo WHERE fo.originalBuyer = :user ORDER BY fo.createdAt DESC")
    List<FoodOrder> findByOriginalBuyer(@Param("user") User user);
    
    @Query("SELECT fo FROM FoodOrder fo WHERE fo.currentOwner = :user ORDER BY fo.createdAt DESC")
    List<FoodOrder> findByCurrentOwner(@Param("user") User user);
    
    @Query("SELECT fo FROM FoodOrder fo WHERE fo.originalBuyer = :user OR fo.currentOwner = :user ORDER BY fo.createdAt DESC")
    List<FoodOrder> findByUserAsOwnerOrOriginalBuyer(@Param("user") User user);
    
    @Query("SELECT fo FROM FoodOrder fo WHERE fo.foodOrderStatus = com.datvexemphim.domain.entity.FoodOrder.FoodOrderStatus.PENDING")
    List<FoodOrder> findAllPending();
}
