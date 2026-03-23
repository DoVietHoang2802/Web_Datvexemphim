package com.datvexemphim.service;

import com.datvexemphim.api.dto.admin.FoodItemUpsertRequest;
import com.datvexemphim.api.dto.food.FoodItemDTO;
import com.datvexemphim.domain.entity.FoodCategory;
import com.datvexemphim.domain.entity.FoodItem;
import com.datvexemphim.domain.repository.FoodCategoryRepository;
import com.datvexemphim.domain.repository.FoodItemRepository;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.server.ResponseStatusException;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Collectors;

@Service
@Transactional
public class FoodItemService {
    
    private final FoodItemRepository foodItemRepository;
    private final FoodCategoryRepository foodCategoryRepository;
    
    public FoodItemService(FoodItemRepository foodItemRepository, FoodCategoryRepository foodCategoryRepository) {
        this.foodItemRepository = foodItemRepository;
        this.foodCategoryRepository = foodCategoryRepository;
    }
    
    // ============ PUBLIC METHODS ============
    
    /**
     * Get all available food items
     */
    @Transactional(readOnly = true)
    public List<FoodItemDTO> getAllAvailable() {
        List<FoodItem> items = foodItemRepository.findAllAvailable();
        return items.stream()
                .map(this::convertToDTO)
                .collect(Collectors.toList());
    }
    
    /**
     * Get food items by category
     */
    @Transactional(readOnly = true)
    public List<FoodItemDTO> getByCategory(Long categoryId) {
        List<FoodItem> items = foodItemRepository.findByCategoryIdAndAvailable(categoryId);
        return items.stream()
                .map(this::convertToDTO)
                .collect(Collectors.toList());
    }
    
    /**
     * Get food item by ID
     */
    @Transactional(readOnly = true)
    public FoodItemDTO getById(Long id) {
        FoodItem item = foodItemRepository.findById(id)
                .orElseThrow(() -> new ResponseStatusException(
                        HttpStatus.NOT_FOUND, "Sản phẩm đồ ăn không tìm thấy"
                ));
        return convertToDTO(item);
    }
    
    /**
     * Get food item entity by ID (internal)
     */
    @Transactional(readOnly = true)
    public FoodItem getFoodItemEntity(Long id) {
        return foodItemRepository.findById(id)
                .orElseThrow(() -> new ResponseStatusException(
                        HttpStatus.NOT_FOUND, "Sản phẩm đồ ăn không tìm thấy"
                ));
    }
    
    /**
     * Create new food item (Admin) - JSON body
     */
    public FoodItemDTO create(FoodItemUpsertRequest req) {
        FoodCategory category = foodCategoryRepository.findById(req.categoryId())
                .orElseThrow(() -> new ResponseStatusException(
                        HttpStatus.NOT_FOUND, "Danh mục đồ ăn không tìm thấy"
                ));

        FoodItem item = new FoodItem();
        item.setCategory(category);
        item.setName(req.name());
        item.setDescription(req.description());
        item.setPrice(req.price());
        item.setSize(req.size());
        item.setImageUrl(req.imageUrl());
        item.setStock(req.stock() != null ? req.stock() : 999);
        item.setIsAvailable(req.isAvailable() != null ? req.isAvailable() : true);
        item.setDisplayOrder(req.displayOrder() != null ? req.displayOrder() : 0);

        FoodItem saved = foodItemRepository.save(item);
        return convertToDTO(saved);
    }

    /**
     * Create new food item (Admin) - params (legacy)
     */
    public FoodItemDTO create(Long categoryId, String name, String description, BigDecimal price,
                              String size, String imageUrl, Integer stock) {
        FoodCategory category = foodCategoryRepository.findById(categoryId)
                .orElseThrow(() -> new ResponseStatusException(
                        HttpStatus.NOT_FOUND, "Danh mục đồ ăn không tìm thấy"
                ));

        FoodItem item = new FoodItem();
        item.setCategory(category);
        item.setName(name);
        item.setDescription(description);
        item.setPrice(price);
        item.setImageUrl(imageUrl);
        item.setSize(size);
        item.setStock(stock != null ? stock : 999);
        item.setIsAvailable(true);
        item.setDisplayOrder(0);

        FoodItem saved = foodItemRepository.save(item);
        return convertToDTO(saved);
    }

    /**
     * Update food item (Admin) - JSON body
     */
    public FoodItemDTO update(Long id, FoodItemUpsertRequest req) {
        FoodItem item = foodItemRepository.findById(id)
                .orElseThrow(() -> new ResponseStatusException(
                        HttpStatus.NOT_FOUND, "Sản phẩm đồ ăn không tìm thấy"
                ));

        if (req.name() != null) item.setName(req.name());
        if (req.description() != null) item.setDescription(req.description());
        if (req.price() != null) item.setPrice(req.price());
        if (req.size() != null) item.setSize(req.size());
        if (req.imageUrl() != null) item.setImageUrl(req.imageUrl());
        if (req.stock() != null) item.setStock(req.stock());
        if (req.isAvailable() != null) item.setIsAvailable(req.isAvailable());
        if (req.displayOrder() != null) item.setDisplayOrder(req.displayOrder());

        item.setUpdatedAt(LocalDateTime.now());
        FoodItem updated = foodItemRepository.save(item);
        return convertToDTO(updated);
    }

    /**
     * Update food item (Admin) - params (legacy)
     */
    public FoodItemDTO update(Long id, String name, String description, BigDecimal price,
                              String size, String imageUrl, Integer stock, Boolean isAvailable, Integer displayOrder) {
        FoodItem item = foodItemRepository.findById(id)
                .orElseThrow(() -> new ResponseStatusException(
                        HttpStatus.NOT_FOUND, "Sản phẩm đồ ăn không tìm thấy"
                ));

        if (name != null) item.setName(name);
        if (description != null) item.setDescription(description);
        if (price != null) item.setPrice(price);
        if (imageUrl != null) item.setImageUrl(imageUrl);
        if (size != null) item.setSize(size);
        if (stock != null) item.setStock(stock);
        if (isAvailable != null) item.setIsAvailable(isAvailable);
        if (displayOrder != null) item.setDisplayOrder(displayOrder);

        item.setUpdatedAt(LocalDateTime.now());
        FoodItem updated = foodItemRepository.save(item);
        return convertToDTO(updated);
    }
    
    /**
     * Delete food item (Admin)
     */
    public void delete(Long id) {
        FoodItem item = foodItemRepository.findById(id)
                .orElseThrow(() -> new ResponseStatusException(
                        HttpStatus.NOT_FOUND, "Sản phẩm đồ ăn không tìm thấy"
                ));
        foodItemRepository.delete(item);
    }
    
    // ============ PRIVATE METHODS ============
    
    private FoodItemDTO convertToDTO(FoodItem item) {
        FoodItemDTO dto = new FoodItemDTO();
        dto.setId(item.getId());
        dto.setCategoryId(item.getCategory().getId());
        dto.setCategoryName(item.getCategory().getName());
        dto.setName(item.getName());
        dto.setDescription(item.getDescription());
        dto.setPrice(item.getPrice());
        dto.setSize(item.getSize());
        dto.setImageUrl(item.getImageUrl());
        dto.setStock(item.getStock());
        dto.setIsAvailable(item.getIsAvailable());
        dto.setDisplayOrder(item.getDisplayOrder());
        return dto;
    }
}
