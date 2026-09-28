package com.recipebox.entity;

import jakarta.persistence.*;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

@Entity
@Table(name = "recipes")
public class Recipe {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false)
    private String title;

    @Column(length = 1000)
    private String description;

    @Column(nullable = false)
    private String cuisine;

    @Column(nullable = false)
    private Integer prepTime; // in minutes

    @Column(nullable = false)
    private Integer cookingTime; // in minutes

    @Lob
    @Column(columnDefinition = "TEXT", nullable = false)
    private String steps;

    @Column(nullable = false)
    private Boolean favourite = false;

    @Column(nullable = false)
    private LocalDateTime createdAt = LocalDateTime.now();

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id")
    private User user;

    @OneToMany(mappedBy = "recipe", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<RecipeIngredient> recipeIngredients = new ArrayList<>();

    @OneToMany(mappedBy = "recipe", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<MealPlan> mealPlans = new ArrayList<>();

    public Recipe() {
    }

    public Recipe(String title, String description, String cuisine, Integer prepTime, Integer cookingTime, String steps, Boolean favourite) {
        this.title = title;
        this.description = description;
        this.cuisine = cuisine;
        this.prepTime = prepTime;
        this.cookingTime = cookingTime;
        this.steps = steps;
        this.favourite = favourite != null ? favourite : false;
        this.createdAt = LocalDateTime.now();
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

    public User getUser() {
        return user;
    }

    public void setUser(User user) {
        this.user = user;
    }

    public List<RecipeIngredient> getRecipeIngredients() {
        return recipeIngredients;
    }

    public void setRecipeIngredients(List<RecipeIngredient> recipeIngredients) {
        this.recipeIngredients = recipeIngredients;
    }

    public List<MealPlan> getMealPlans() {
        return mealPlans;
    }

    public void setMealPlans(List<MealPlan> mealPlans) {
        this.mealPlans = mealPlans;
    }

    public void addRecipeIngredient(RecipeIngredient recipeIngredient) {
        recipeIngredients.add(recipeIngredient);
        recipeIngredient.setRecipe(this);
    }

    public void removeRecipeIngredient(RecipeIngredient recipeIngredient) {
        recipeIngredients.remove(recipeIngredient);
        recipeIngredient.setRecipe(null);
    }
}
