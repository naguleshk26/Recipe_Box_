package com.recipebox.entity;

import jakarta.persistence.*;
import java.time.LocalDate;

@Entity
@Table(name = "meal_plans")
public class MealPlan {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id")
    private User user;

    @ManyToOne(fetch = FetchType.EAGER)
    @JoinColumn(name = "recipe_id", nullable = false)
    private Recipe recipe;

    @Column(nullable = false)
    private LocalDate mealDate;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private MealType mealType;

    public MealPlan() {
    }

    public MealPlan(User user, Recipe recipe, LocalDate mealDate, MealType mealType) {
        this.user = user;
        this.recipe = recipe;
        this.mealDate = mealDate;
        this.mealType = mealType;
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public User getUser() {
        return user;
    }

    public void setUser(User user) {
        this.user = user;
    }

    public Recipe getRecipe() {
        return recipe;
    }

    public void setRecipe(Recipe recipe) {
        this.recipe = recipe;
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
}
