package com.datvexemphim.api.controller.admin;

import com.datvexemphim.api.dto.food.FoodCategoryDTO;
import com.datvexemphim.api.dto.food.FoodItemDTO;
import com.datvexemphim.service.FoodCategoryService;
import com.datvexemphim.service.FoodItemService;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.math.BigDecimal;
import java.util.List;

@RestController
@RequestMapping("/api/admin/food")
@PreAuthorize("hasRole('ADMIN')")
public class AdminFoodController {
    
    private final FoodCategoryService foodCategoryService;
    private final FoodItemService foodItemService;
    
    public AdminFoodController(FoodCategoryService foodCategoryService, 
                              FoodItemService foodItemService) {
        this.foodCategoryService = foodCategoryService;
        this.foodItemService = foodItemService;
    }
    
    // ============ FOOD CATEGORIES - CRUD ============
    
    /**
     * GET /api/admin/food/categories - Get all food categories
     */
    @GetMapping("/categories")
    public ResponseEntity<List<FoodCategoryDTO>> getAllCategories() {
        List<FoodCategoryDTO> categories = foodCategoryService.getAllActive();
        return ResponseEntity.ok(categories);
    }
    
    /**
     * GET /api/admin/food/categories/{id} - Get category by ID
     */
    @GetMapping("/categories/{id}")
    public ResponseEntity<FoodCategoryDTO> getCategoryById(@PathVariable Long id) {
        FoodCategoryDTO category = foodCategoryService.getById(id);
        return ResponseEntity.ok(category);
    }
    
    /**
     * POST /api/admin/food/categories - Create new category
     */
    @PostMapping("/categories")
    public ResponseEntity<FoodCategoryDTO> createCategory(
            @RequestParam String name,
            @RequestParam(required = false) String description,
            @RequestParam(required = false) String icon
    ) {
        FoodCategoryDTO category = foodCategoryService.create(name, description, icon);
        return ResponseEntity.status(HttpStatus.CREATED).body(category);
    }
    
    /**
     * PUT /api/admin/food/categories/{id} - Update category
     */
    @PutMapping("/categories/{id}")
    public ResponseEntity<FoodCategoryDTO> updateCategory(
            @PathVariable Long id,
            @RequestParam(required = false) String name,
            @RequestParam(required = false) String description,
            @RequestParam(required = false) String icon,
            @RequestParam(required = false) Boolean isActive,
            @RequestParam(required = false) Integer displayOrder
    ) {
        FoodCategoryDTO category = foodCategoryService.update(id, name, description, icon, isActive, displayOrder);
        return ResponseEntity.ok(category);
    }
    
    /**
     * DELETE /api/admin/food/categories/{id} - Delete category
     */
    @DeleteMapping("/categories/{id}")
    public ResponseEntity<Void> deleteCategory(@PathVariable Long id) {
        foodCategoryService.delete(id);
        return ResponseEntity.noContent().build();
    }
    
    // ============ FOOD ITEMS - CRUD ============
    
    /**
     * GET /api/admin/food/items - Get all food items
     */
    @GetMapping("/items")
    public ResponseEntity<List<FoodItemDTO>> getAllItems() {
        List<FoodItemDTO> items = foodItemService.getAllAvailable();
        return ResponseEntity.ok(items);
    }
    
    /**
     * GET /api/admin/food/items/{id} - Get item by ID
     */
    @GetMapping("/items/{id}")
    public ResponseEntity<FoodItemDTO> getItemById(@PathVariable Long id) {
        FoodItemDTO item = foodItemService.getById(id);
        return ResponseEntity.ok(item);
    }
    
    /**
     * GET /api/admin/food/items/category/{categoryId} - Get items by category
     */
    @GetMapping("/items/category/{categoryId}")
    public ResponseEntity<List<FoodItemDTO>> getItemsByCategory(@PathVariable Long categoryId) {
        List<FoodItemDTO> items = foodItemService.getByCategory(categoryId);
        return ResponseEntity.ok(items);
    }
    
    /**
     * POST /api/admin/food/items - Create new food item
     */
    @PostMapping("/items")
    public ResponseEntity<FoodItemDTO> createItem(
            @RequestParam Long categoryId,
            @RequestParam String name,
            @RequestParam(required = false) String description,
            @RequestParam BigDecimal price,
            @RequestParam(required = false) String imageUrl,
            @RequestParam(required = false) Integer stock
    ) {
        FoodItemDTO item = foodItemService.create(categoryId, name, description, price, imageUrl, stock);
        return ResponseEntity.status(HttpStatus.CREATED).body(item);
    }
    
    /**
     * PUT /api/admin/food/items/{id} - Update food item
     */
    @PutMapping("/items/{id}")
    public ResponseEntity<FoodItemDTO> updateItem(
            @PathVariable Long id,
            @RequestParam(required = false) String name,
            @RequestParam(required = false) String description,
            @RequestParam(required = false) BigDecimal price,
            @RequestParam(required = false) String imageUrl,
            @RequestParam(required = false) Integer stock,
            @RequestParam(required = false) Boolean isAvailable,
            @RequestParam(required = false) Integer displayOrder
    ) {
        FoodItemDTO item = foodItemService.update(id, name, description, price, imageUrl, stock, isAvailable, displayOrder);
        return ResponseEntity.ok(item);
    }
    
    /**
     * DELETE /api/admin/food/items/{id} - Delete food item
     */
    @DeleteMapping("/items/{id}")
    public ResponseEntity<Void> deleteItem(@PathVariable Long id) {
        foodItemService.delete(id);
        return ResponseEntity.noContent().build();
    }
}
