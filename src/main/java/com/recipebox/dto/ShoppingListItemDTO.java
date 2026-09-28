package com.recipebox.dto;

public class ShoppingListItemDTO {

    private String ingredientName;
    private Double totalQuantity;
    private String unit;

    public ShoppingListItemDTO() {
    }

    public ShoppingListItemDTO(String ingredientName, Double totalQuantity, String unit) {
        this.ingredientName = ingredientName;
        this.totalQuantity = totalQuantity;
        this.unit = unit;
    }

    public String getIngredientName() {
        return ingredientName;
    }

    public void setIngredientName(String ingredientName) {
        this.ingredientName = ingredientName;
    }

    public Double getTotalQuantity() {
        return totalQuantity;
    }

    public void setTotalQuantity(Double totalQuantity) {
        this.totalQuantity = totalQuantity;
    }

    public String getUnit() {
        return unit;
    }

    public void setUnit(String unit) {
        this.unit = unit;
    }
}
