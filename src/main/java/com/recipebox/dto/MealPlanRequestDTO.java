package com.recipebox.dto;

import com.recipebox.entity.MealType;
import jakarta.validation.constraints.NotNull;
import java.time.LocalDate;

public class MealPlanRequestDTO {

    @NotNull(message = "Recipe ID is required")
    private Long recipeId;

    @NotNull(message = "Meal date is required")
    private LocalDate mealDate;

    @NotNull(message = "Meal type is required")
    private MealType mealType;

    private Long userId;

    public MealPlanRequestDTO() {
    }

    public MealPlanRequestDTO(Long recipeId, LocalDate mealDate, MealType mealType, Long userId) {
        this.recipeId = recipeId;
        this.mealDate = mealDate;
        this.mealType = mealType;
        this.userId = userId;
    }

    public Long getRecipeId() {
        return recipeId;
    }

    public void setRecipeId(Long recipeId) {
        this.recipeId = recipeId;
    }

    public LocalDate getMealDate() {
        return mealDate;
    }

    public void setMealDate(LocalDate mealDate) {
        this.mealDate = mealDate;
    }

    public MealType getMealType() {
        return mealType;
    }

    public void setMealType(MealType mealType) {
        this.mealType = mealType;
    }

    public Long getUserId() {
        return userId;
    }

    public void setUserId(Long userId) {
        this.userId = userId;
    }
}
