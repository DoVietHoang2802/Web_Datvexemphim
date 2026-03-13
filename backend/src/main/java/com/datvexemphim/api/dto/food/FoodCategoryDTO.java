package com.datvexemphim.api.dto.food;

import java.util.List;

public class FoodCategoryDTO {
    private Long id;
    private String name;
    private String description;
    private String icon;
    private Integer displayOrder;
    private Boolean isActive;
    private List<FoodItemDTO> items;
    
    public FoodCategoryDTO() {}
    
    public FoodCategoryDTO(Long id, String name, String description, String icon, 
                          Integer displayOrder, Boolean isActive, List<FoodItemDTO> items) {
        this.id = id;
        this.name = name;
        this.description = description;
        this.icon = icon;
        this.displayOrder = displayOrder;
        this.isActive = isActive;
        this.items = items;
    }
    
    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }
    
    public String getName() { return name; }
    public void setName(String name) { this.name = name; }
    
    public String getDescription() { return description; }
    public void setDescription(String description) { this.description = description; }
    
    public String getIcon() { return icon; }
    public void setIcon(String icon) { this.icon = icon; }
    
    public Integer getDisplayOrder() { return displayOrder; }
    public void setDisplayOrder(Integer displayOrder) { this.displayOrder = displayOrder; }
    
    public Boolean getIsActive() { return isActive; }
    public void setIsActive(Boolean isActive) { this.isActive = isActive; }
    
    public List<FoodItemDTO> getItems() { return items; }
    public void setItems(List<FoodItemDTO> items) { this.items = items; }
}
