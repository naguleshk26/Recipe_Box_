package com.recipebox.dto;

import java.util.ArrayList;
import java.util.List;

public class DashboardStatsDTO {

    private long totalRecipes;
    private long favouriteRecipes;
    private long plannedMeals;
    private long totalIngredients;
    private List<MealPlanResponseDTO> upcomingMeals = new ArrayList<>();

    public DashboardStatsDTO() {
    }

    public DashboardStatsDTO(long totalRecipes, long favouriteRecipes, long plannedMeals, long totalIngredients, List<MealPlanResponseDTO> upcomingMeals) {
        this.totalRecipes = totalRecipes;
        this.favouriteRecipes = favouriteRecipes;
        this.plannedMeals = plannedMeals;
        this.totalIngredients = totalIngredients;
        this.upcomingMeals = upcomingMeals;
    }

    public long getTotalRecipes() {
        return totalRecipes;
    }

    public void setTotalRecipes(long totalRecipes) {
        this.totalRecipes = totalRecipes;
    }

    public long getFavouriteRecipes() {
        return favouriteRecipes;
    }

    public void setFavouriteRecipes(long favouriteRecipes) {
        this.favouriteRecipes = favouriteRecipes;
    }

    public long getPlannedMeals() {
        return plannedMeals;
    }

    public void setPlannedMeals(long plannedMeals) {
        this.plannedMeals = plannedMeals;
    }

    public long getTotalIngredients() {
        return totalIngredients;
    }

    public void setTotalIngredients(long totalIngredients) {
        this.totalIngredients = totalIngredients;
    }

    public List<MealPlanResponseDTO> getUpcomingMeals() {
        return upcomingMeals;
    }

    public void setUpcomingMeals(List<MealPlanResponseDTO> upcomingMeals) {
        this.upcomingMeals = upcomingMeals;
    }
}
