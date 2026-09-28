package com.recipebox.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;

public class RecipeIngredientDTO {

    private Long id;

    @NotBlank(message = "Ingredient name cannot be empty")
    private String ingredientName;

    @NotNull(message = "Quantity is required")
    @Positive(message = "Quantity must be greater than zero")
    private Double quantity;

    @NotBlank(message = "Unit cannot be empty")
    private String unit;

    public RecipeIngredientDTO() {
    }

    public RecipeIngredientDTO(String ingredientName, Double quantity, String unit) {
        this.ingredientName = ingredientName;
        this.quantity = quantity;
        this.unit = unit;
    }

    public RecipeIngredientDTO(Long id, String ingredientName, Double quantity, String unit) {
        this.id = id;
        this.ingredientName = ingredientName;
        this.quantity = quantity;
        this.unit = unit;
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getIngredientName() {
        return ingredientName;
    }

    public void setIngredientName(String ingredientName) {
        this.ingredientName = ingredientName;
    }

    public Double getQuantity() {
        return quantity;
    }

    public void setQuantity(Double quantity) {
        this.quantity = quantity;
    }

    public String getUnit() {
        return unit;
    }

    public void setUnit(String unit) {
        this.unit = unit;
    }
}
