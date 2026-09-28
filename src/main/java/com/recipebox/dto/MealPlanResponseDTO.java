package com.recipebox.dto;

import com.recipebox.entity.MealType;
import java.time.LocalDate;

public class MealPlanResponseDTO {

    private Long id;
    private Long recipeId;
    private String recipeTitle;
    private String cuisine;
    private Integer prepTime;
    private Integer cookingTime;
    private LocalDate mealDate;
    private MealType mealType;
    private Long userId;
    private String userName;

    public MealPlanResponseDTO() {
    }

    public MealPlanResponseDTO(Long id, Long recipeId, String recipeTitle, String cuisine, Integer prepTime, Integer cookingTime, LocalDate mealDate, MealType mealType, Long userId, String userName) {
        this.id = id;
        this.recipeId = recipeId;
        this.recipeTitle = recipeTitle;
        this.cuisine = cuisine;
        this.prepTime = prepTime;
        this.cookingTime = cookingTime;
        this.mealDate = mealDate;
        this.mealType = mealType;
        this.userId = userId;
        this.userName = userName;
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public Long getRecipeId() {
        return recipeId;
    }

    public void setRecipeId(Long recipeId) {
        this.recipeId = recipeId;
    }

    public String getRecipeTitle() {
        return recipeTitle;
    }

    public void setRecipeTitle(String recipeTitle) {
        this.recipeTitle = recipeTitle;
    }

    public String getCuisine() {
        return cuisine;
    }

    public void setCuisine(String cuisine) {
        this.cuisine = cuisine;
    }

    public Integer getPrepTime() {
        return prepTime;
    }

    public void setPrepTime(Integer prepTime) {
        this.prepTime = prepTime;
    }

    public Integer getCookingTime() {
        return cookingTime;
    }

    public void setCookingTime(Integer cookingTime) {
        this.cookingTime = cookingTime;
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

    public String getUserName() {
        return userName;
    }

    public void setUserName(String userName) {
        this.userName = userName;
    }
}
