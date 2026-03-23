package com.datvexemphim.service;

import com.datvexemphim.api.dto.admin.FoodCategoryUpsertRequest;
import com.datvexemphim.api.dto.admin.FoodItemUpsertRequest;
import com.datvexemphim.api.dto.food.FoodCategoryDTO;
import com.datvexemphim.api.dto.food.FoodItemDTO;
import com.datvexemphim.domain.entity.FoodCategory;
import com.datvexemphim.domain.entity.FoodItem;
import com.datvexemphim.domain.repository.FoodCategoryRepository;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.server.ResponseStatusException;

import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Collectors;

@Service
@Transactional
public class FoodCategoryService {
    
    private final FoodCategoryRepository foodCategoryRepository;
    
    public FoodCategoryService(FoodCategoryRepository foodCategoryRepository) {
        this.foodCategoryRepository = foodCategoryRepository;
    }
    
    // ============ PUBLIC METHODS ============
    
    /**
     * Get all active food categories with their items
     */
    @Transactional(readOnly = true)
    public List<FoodCategoryDTO> getAllActive() {
        List<FoodCategory> categories = foodCategoryRepository.findAllActive();
        return categories.stream()
                .map(this::convertToDTO)
                .collect(Collectors.toList());
    }
    
    /**
     * Get category by ID
     */
    @Transactional(readOnly = true)
    public FoodCategoryDTO getById(Long id) {
        FoodCategory category = foodCategoryRepository.findById(id)
                .orElseThrow(() -> new ResponseStatusException(
                        HttpStatus.NOT_FOUND, "Danh mục đồ ăn không tìm thấy"
                ));
        return convertToDTO(category);
    }
    
    /**
     * Create new food category (Admin) - JSON body
     */
    public FoodCategoryDTO create(FoodCategoryUpsertRequest req) {
        FoodCategory category = new FoodCategory();
        category.setName(req.name());
        category.setDescription(req.description());
        category.setIcon(req.icon());
        category.setIsActive(req.isActive() != null ? req.isActive() : true);
        category.setDisplayOrder(req.displayOrder() != null ? req.displayOrder() : 0);
        FoodCategory saved = foodCategoryRepository.save(category);
        return convertToDTO(saved);
    }

    /**
     * Create new food category (Admin) - params (legacy)
     */
    public FoodCategoryDTO create(String name, String description, String icon) {
        FoodCategory category = new FoodCategory();
        category.setName(name);
        category.setDescription(description);
        category.setIcon(icon);
        category.setIsActive(true);
        category.setDisplayOrder(0);
        FoodCategory saved = foodCategoryRepository.save(category);
        return convertToDTO(saved);
    }

    /**
     * Update food category (Admin) - JSON body
     */
    public FoodCategoryDTO update(Long id, FoodCategoryUpsertRequest req) {
        FoodCategory category = foodCategoryRepository.findById(id)
                .orElseThrow(() -> new ResponseStatusException(
                        HttpStatus.NOT_FOUND, "Danh mục đồ ăn không tìm thấy"
                ));

        if (req.name() != null) category.setName(req.name());
        if (req.description() != null) category.setDescription(req.description());
        if (req.icon() != null) category.setIcon(req.icon());
        if (req.isActive() != null) category.setIsActive(req.isActive());
        if (req.displayOrder() != null) category.setDisplayOrder(req.displayOrder());

        category.setUpdatedAt(LocalDateTime.now());
        FoodCategory updated = foodCategoryRepository.save(category);
        return convertToDTO(updated);
    }

    /**
     * Update food category (Admin) - params (legacy)
     */
    public FoodCategoryDTO update(Long id, String name, String description, String icon, Boolean isActive, Integer displayOrder) {
        FoodCategory category = foodCategoryRepository.findById(id)
                .orElseThrow(() -> new ResponseStatusException(
                        HttpStatus.NOT_FOUND, "Danh mục đồ ăn không tìm thấy"
                ));

        if (name != null) category.setName(name);
        if (description != null) category.setDescription(description);
        if (icon != null) category.setIcon(icon);
        if (isActive != null) category.setIsActive(isActive);
        if (displayOrder != null) category.setDisplayOrder(displayOrder);

        category.setUpdatedAt(LocalDateTime.now());
        FoodCategory updated = foodCategoryRepository.save(category);
        return convertToDTO(updated);
    }
    
    /**
     * Delete food category (Admin)
     */
    public void delete(Long id) {
        FoodCategory category = foodCategoryRepository.findById(id)
                .orElseThrow(() -> new ResponseStatusException(
                        HttpStatus.NOT_FOUND, "Danh mục đồ ăn không tìm thấy"
                ));
        foodCategoryRepository.delete(category);
    }
    
    // ============ PRIVATE METHODS ============
    
    private FoodCategoryDTO convertToDTO(FoodCategory category) {
        List<FoodItemDTO> itemDTOs = category.getFoodItems() != null ?
                category.getFoodItems().stream()
                        .map(this::convertItemToDTO)
                        .collect(Collectors.toList())
                : List.of();
        
        FoodCategoryDTO dto = new FoodCategoryDTO();
        dto.setId(category.getId());
        dto.setName(category.getName());
        dto.setDescription(category.getDescription());
        dto.setIcon(category.getIcon());
        dto.setDisplayOrder(category.getDisplayOrder());
        dto.setIsActive(category.getIsActive());
        dto.setItems(itemDTOs);
        return dto;
    }
    
    private FoodItemDTO convertItemToDTO(FoodItem item) {
        FoodItemDTO dto = new FoodItemDTO();
        dto.setId(item.getId());
        dto.setCategoryId(item.getCategory().getId());
        dto.setCategoryName(item.getCategory().getName());
        dto.setName(item.getName());
        dto.setDescription(item.getDescription());
        dto.setPrice(item.getPrice());
        dto.setImageUrl(item.getImageUrl());
        dto.setStock(item.getStock());
        dto.setIsAvailable(item.getIsAvailable());
        dto.setDisplayOrder(item.getDisplayOrder());
        return dto;
    }
}
