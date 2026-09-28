package com.recipebox.dto;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

public class RecipeResponseDTO {

    private Long id;
    private String title;
    private String description;
    private String cuisine;
    private Integer prepTime;
    private Integer cookingTime;
    private String steps;
    private Boolean favourite;
    private LocalDateTime createdAt;
    private Long userId;
    private String userName;
    private List<RecipeIngredientDTO> ingredients = new ArrayList<>();

    public RecipeResponseDTO() {
    }

    public RecipeResponseDTO(Long id, String title, String description, String cuisine, Integer prepTime, Integer cookingTime, String steps, Boolean favourite, LocalDateTime createdAt, Long userId, String userName, List<RecipeIngredientDTO> ingredients) {
        this.id = id;
        this.title = title;
        this.description = description;
        this.cuisine = cuisine;
        this.prepTime = prepTime;
        this.cookingTime = cookingTime;
        this.steps = steps;
        this.favourite = favourite;
        this.createdAt = createdAt;
        this.userId = userId;
        this.userName = userName;
        this.ingredients = ingredients;
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
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

    public LocalDateTime getCreatedAt() {
        return createdAt;
    }

    public void setCreatedAt(LocalDateTime createdAt) {
        this.createdAt = createdAt;
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

    public List<RecipeIngredientDTO> getIngredients() {
        return ingredients;
    }

    public void setIngredients(List<RecipeIngredientDTO> ingredients) {
        this.ingredients = ingredients;
    }
}
