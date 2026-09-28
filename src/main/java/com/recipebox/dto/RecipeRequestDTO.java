package com.recipebox.dto;

import jakarta.validation.Valid;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.PositiveOrZero;
import java.util.ArrayList;
import java.util.List;

public class RecipeRequestDTO {

    @NotBlank(message = "Recipe title cannot be empty")
    private String title;

    private String description;

    @NotBlank(message = "Cuisine cannot be empty")
    private String cuisine;

    @NotNull(message = "Preparation time is required")
    @PositiveOrZero(message = "Preparation time cannot be negative")
    private Integer prepTime;

    @NotNull(message = "Cooking time is required")
    @PositiveOrZero(message = "Cooking time cannot be negative")
    private Integer cookingTime;

    @NotBlank(message = "Cooking steps cannot be empty")
    private String steps;

    private Boolean favourite = false;

    private Long userId;

    @NotEmpty(message = "At least one ingredient is required")
    @Valid
    private List<RecipeIngredientDTO> ingredients = new ArrayList<>();

    public RecipeRequestDTO() {
    }

    public RecipeRequestDTO(String title, String description, String cuisine, Integer prepTime, Integer cookingTime, String steps, Boolean favourite, Long userId, List<RecipeIngredientDTO> ingredients) {
        this.title = title;
        this.description = description;
        this.cuisine = cuisine;
        this.prepTime = prepTime;
        this.cookingTime = cookingTime;
        this.steps = steps;
        this.favourite = favourite;
        this.userId = userId;
        this.ingredients = ingredients;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
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

    public String getSteps() {
        return steps;
    }

    public void setSteps(String steps) {
        this.steps = steps;
    }

    public Boolean getFavourite() {
        return favourite;
    }

    public void setFavourite(Boolean favourite) {
        this.favourite = favourite;
    }

    public Long getUserId() {
        return userId;
    }

    public void setUserId(Long userId) {
        this.userId = userId;
    }

    public List<RecipeIngredientDTO> getIngredients() {
        return ingredients;
    }

    public void setIngredients(List<RecipeIngredientDTO> ingredients) {
        this.ingredients = ingredients;
    }
}
