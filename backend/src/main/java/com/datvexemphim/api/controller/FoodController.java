package com.datvexemphim.api.controller;

import com.datvexemphim.api.dto.food.CreateFoodOrderRequest;
import com.datvexemphim.api.dto.food.FoodCategoryDTO;
import com.datvexemphim.api.dto.food.FoodItemDTO;
import com.datvexemphim.api.dto.food.FoodOrderDTO;
import com.datvexemphim.service.FoodCategoryService;
import com.datvexemphim.service.FoodItemService;
import com.datvexemphim.service.FoodOrderService;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/food")
public class FoodController {
    
    private final FoodCategoryService foodCategoryService;
    private final FoodItemService foodItemService;
    private final FoodOrderService foodOrderService;
    
    public FoodController(FoodCategoryService foodCategoryService, 
                         FoodItemService foodItemService, 
                         FoodOrderService foodOrderService) {
        this.foodCategoryService = foodCategoryService;
        this.foodItemService = foodItemService;
        this.foodOrderService = foodOrderService;
    }
    
    // ============ FOOD CATEGORIES ============
    
    /**
     * GET /api/food/categories - Get all active food categories with items
     */
    @GetMapping("/categories")
    public ResponseEntity<List<FoodCategoryDTO>> getAllCategories() {
        List<FoodCategoryDTO> categories = foodCategoryService.getAllActive();
        return ResponseEntity.ok(categories);
    }
    
    /**
     * GET /api/food/categories/{id} - Get category by ID
     */
    @GetMapping("/categories/{id}")
    public ResponseEntity<FoodCategoryDTO> getCategoryById(@PathVariable Long id) {
        FoodCategoryDTO category = foodCategoryService.getById(id);
        return ResponseEntity.ok(category);
    }
    
    // ============ FOOD ITEMS ============
    
    /**
     * GET /api/food/items - Get all available food items
     */
    @GetMapping("/items")
    public ResponseEntity<List<FoodItemDTO>> getAllItems() {
        List<FoodItemDTO> items = foodItemService.getAllAvailable();
        return ResponseEntity.ok(items);
    }
    
    /**
     * GET /api/food/items/category/{categoryId} - Get items by category
     */
    @GetMapping("/items/category/{categoryId}")
    public ResponseEntity<List<FoodItemDTO>> getItemsByCategory(@PathVariable Long categoryId) {
        List<FoodItemDTO> items = foodItemService.getByCategory(categoryId);
        return ResponseEntity.ok(items);
    }
    
    /**
     * GET /api/food/items/{id} - Get item by ID
     */
    @GetMapping("/items/{id}")
    public ResponseEntity<FoodItemDTO> getItemById(@PathVariable Long id) {
        FoodItemDTO item = foodItemService.getById(id);
        return ResponseEntity.ok(item);
    }
    
    // ============ FOOD ORDERS (User) ============
    
    /**
     * POST /api/food/orders - Create new food order (during checkout)
     * User must be authenticated
     */
    @PostMapping("/orders")
    @PreAuthorize("isAuthenticated()")
    public ResponseEntity<FoodOrderDTO> createFoodOrder(
            @RequestBody CreateFoodOrderRequest request
    ) {
        FoodOrderDTO foodOrder = foodOrderService.createFoodOrder(request);
        return ResponseEntity.status(HttpStatus.CREATED).body(foodOrder);
    }
    
    /**
     * GET /api/food/orders - Get all food orders of current user
     */
    @GetMapping("/orders")
    @PreAuthorize("isAuthenticated()")
    public ResponseEntity<List<FoodOrderDTO>> getUserFoodOrders() {
        List<FoodOrderDTO> orders = foodOrderService.getUserFoodOrders();
        return ResponseEntity.ok(orders);
    }
    
    /**
     * GET /api/food/orders/{id} - Get food order by ID
     */
    @GetMapping("/orders/{id}")
    @PreAuthorize("isAuthenticated()")
    public ResponseEntity<FoodOrderDTO> getFoodOrderById(@PathVariable Long id) {
        FoodOrderDTO order = foodOrderService.getById(id);
        return ResponseEntity.ok(order);
    }
    
    /**
     * GET /api/food/orders/ticket/{ticketId} - Get food order by ticket
     */
    @GetMapping("/orders/ticket/{ticketId}")
    @PreAuthorize("isAuthenticated()")
    public ResponseEntity<FoodOrderDTO> getFoodOrderByTicket(@PathVariable Long ticketId) {
        FoodOrderDTO order = foodOrderService.getByTicket(ticketId);
        if (order == null) {
            return ResponseEntity.noContent().build();
        }
        return ResponseEntity.ok(order);
    }
    
    /**
     * DELETE /api/food/orders/{id} - Cancel food order
     */
    @DeleteMapping("/orders/{id}")
    @PreAuthorize("isAuthenticated()")
    public ResponseEntity<FoodOrderDTO> cancelFoodOrder(@PathVariable Long id) {
        FoodOrderDTO order = foodOrderService.cancelFoodOrder(id);
        return ResponseEntity.ok(order);
    }
}
