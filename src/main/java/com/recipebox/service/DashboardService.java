package com.recipebox.service;

import com.recipebox.dto.DashboardStatsDTO;
import com.recipebox.dto.MealPlanResponseDTO;
import com.recipebox.repository.IngredientRepository;
import com.recipebox.repository.MealPlanRepository;
import com.recipebox.repository.RecipeRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.util.List;
import java.util.stream.Collectors;

@Service
@Transactional(readOnly = true)
public class DashboardService {

    private final RecipeRepository recipeRepository;
    private final MealPlanRepository mealPlanRepository;
    private final IngredientRepository ingredientRepository;
    private final MealPlanService mealPlanService;

    @Autowired
    public DashboardService(RecipeRepository recipeRepository,
                            MealPlanRepository mealPlanRepository,
                            IngredientRepository ingredientRepository,
                            MealPlanService mealPlanService) {
        this.recipeRepository = recipeRepository;
        this.mealPlanRepository = mealPlanRepository;
        this.ingredientRepository = ingredientRepository;
        this.mealPlanService = mealPlanService;
    }

    public DashboardStatsDTO getDashboardStats() {
        long totalRecipes = recipeRepository.count();
        long favouriteRecipes = recipeRepository.countByFavouriteTrue();
        long plannedMeals = mealPlanRepository.count();
        long totalIngredients = ingredientRepository.count();

        List<MealPlanResponseDTO> upcoming = mealPlanRepository
                .findTop7ByMealDateGreaterThanEqualOrderByMealDateAscMealTypeAsc(LocalDate.now())
                .stream()
                .map(mealPlanService::toResponseDTO)
                .collect(Collectors.toList());

        return new DashboardStatsDTO(totalRecipes, favouriteRecipes, plannedMeals, totalIngredients, upcoming);
    }
}
