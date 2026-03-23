package com.datvexemphim.api.controller.admin;

import com.datvexemphim.api.dto.admin.FoodCategoryUpsertRequest;
import com.datvexemphim.api.dto.admin.FoodItemUpsertRequest;
import com.datvexemphim.api.dto.food.FoodCategoryDTO;
import com.datvexemphim.api.dto.food.FoodItemDTO;
import com.datvexemphim.service.FoodCategoryService;
import com.datvexemphim.service.FoodItemService;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

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

    @GetMapping("/categories")
    public ResponseEntity<List<FoodCategoryDTO>> getAllCategories() {
        List<FoodCategoryDTO> categories = foodCategoryService.getAllActive();
        return ResponseEntity.ok(categories);
    }

    @GetMapping("/categories/{id}")
    public ResponseEntity<FoodCategoryDTO> getCategoryById(@PathVariable Long id) {
        FoodCategoryDTO category = foodCategoryService.getById(id);
        return ResponseEntity.ok(category);
    }

    @PostMapping("/categories")
    public ResponseEntity<FoodCategoryDTO> createCategory(@Valid @RequestBody FoodCategoryUpsertRequest req) {
        FoodCategoryDTO category = foodCategoryService.create(req);
        return ResponseEntity.status(HttpStatus.CREATED).body(category);
    }

    @PutMapping("/categories/{id}")
    public ResponseEntity<FoodCategoryDTO> updateCategory(
            @PathVariable Long id,
            @Valid @RequestBody FoodCategoryUpsertRequest req) {
        FoodCategoryDTO category = foodCategoryService.update(id, req);
        return ResponseEntity.ok(category);
    }

    @DeleteMapping("/categories/{id}")
    public ResponseEntity<Void> deleteCategory(@PathVariable Long id) {
        foodCategoryService.delete(id);
        return ResponseEntity.noContent().build();
    }

    // ============ FOOD ITEMS - CRUD ============

    @GetMapping("/items")
    public ResponseEntity<List<FoodItemDTO>> getAllItems() {
        List<FoodItemDTO> items = foodItemService.getAllAvailable();
        return ResponseEntity.ok(items);
    }

    @GetMapping("/items/{id}")
    public ResponseEntity<FoodItemDTO> getItemById(@PathVariable Long id) {
        FoodItemDTO item = foodItemService.getById(id);
        return ResponseEntity.ok(item);
    }

    @GetMapping("/items/category/{categoryId}")
    public ResponseEntity<List<FoodItemDTO>> getItemsByCategory(@PathVariable Long categoryId) {
        List<FoodItemDTO> items = foodItemService.getByCategory(categoryId);
        return ResponseEntity.ok(items);
    }

    @PostMapping("/items")
    public ResponseEntity<FoodItemDTO> createItem(@Valid @RequestBody FoodItemUpsertRequest req) {
        FoodItemDTO item = foodItemService.create(req);
        return ResponseEntity.status(HttpStatus.CREATED).body(item);
    }

    @PutMapping("/items/{id}")
    public ResponseEntity<FoodItemDTO> updateItem(
            @PathVariable Long id,
            @Valid @RequestBody FoodItemUpsertRequest req) {
        FoodItemDTO item = foodItemService.update(id, req);
        return ResponseEntity.ok(item);
    }

    @DeleteMapping("/items/{id}")
    public ResponseEntity<Void> deleteItem(@PathVariable Long id) {
        foodItemService.delete(id);
        return ResponseEntity.noContent().build();
    }
}
