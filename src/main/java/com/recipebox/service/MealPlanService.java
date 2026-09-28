package com.recipebox.service;

import com.recipebox.dto.MealPlanRequestDTO;
import com.recipebox.dto.MealPlanResponseDTO;
import com.recipebox.entity.MealPlan;
import com.recipebox.entity.Recipe;
import com.recipebox.entity.User;
import com.recipebox.exception.BadRequestException;
import com.recipebox.exception.ResourceNotFoundException;
import com.recipebox.repository.MealPlanRepository;
import com.recipebox.repository.RecipeRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.util.List;
import java.util.stream.Collectors;

@Service
@Transactional
public class MealPlanService {

    private final MealPlanRepository mealPlanRepository;
    private final RecipeRepository recipeRepository;
    private final UserService userService;

    @Autowired
    public MealPlanService(MealPlanRepository mealPlanRepository,
                           RecipeRepository recipeRepository,
                           UserService userService) {
        this.mealPlanRepository = mealPlanRepository;
        this.recipeRepository = recipeRepository;
        this.userService = userService;
    }

    public MealPlanResponseDTO createMealPlan(MealPlanRequestDTO dto) {
        if (dto.getRecipeId() == null) {
            throw new BadRequestException("Recipe ID cannot be null");
        }
        if (dto.getMealDate() == null) {
            throw new BadRequestException("Meal date cannot be null");
        }
        if (dto.getMealType() == null) {
            throw new BadRequestException("Meal type cannot be null");
        }

        // CRITICAL BUSINESS RULE:
        // A meal plan entry cannot be created using a recipe that does not exist.
        // Validate this in the SERVICE LAYER before saving.
        Recipe recipe = recipeRepository.findById(dto.getRecipeId())
                .orElseThrow(() -> new ResourceNotFoundException("Cannot create meal plan. The selected recipe does not exist."));

        User user = userService.getUserEntity(dto.getUserId());

        MealPlan mealPlan = new MealPlan(user, recipe, dto.getMealDate(), dto.getMealType());
        MealPlan saved = mealPlanRepository.save(mealPlan);

        return toResponseDTO(saved);
    }

    @Transactional(readOnly = true)
    public List<MealPlanResponseDTO> getAllMealPlans() {
        return mealPlanRepository.findAllByOrderByMealDateAscMealTypeAsc().stream()
                .map(this::toResponseDTO)
                .collect(Collectors.toList());
    }

    @Transactional(readOnly = true)
    public List<MealPlanResponseDTO> getWeeklyMealPlans(LocalDate startDate) {
        if (startDate == null) {
            startDate = LocalDate.now();
        }
        LocalDate endDate = startDate.plusDays(6);
        return mealPlanRepository.findByMealDateBetweenOrderByMealDateAscMealTypeAsc(startDate, endDate).stream()
                .map(this::toResponseDTO)
                .collect(Collectors.toList());
    }

    public void deleteMealPlan(Long id) {
        MealPlan mealPlan = mealPlanRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Meal plan not found with id: " + id));
        mealPlanRepository.delete(mealPlan);
    }

    public MealPlanResponseDTO toResponseDTO(MealPlan mealPlan) {
        Recipe recipe = mealPlan.getRecipe();
        return new MealPlanResponseDTO(
                mealPlan.getId(),
                recipe.getId(),
                recipe.getTitle(),
                recipe.getCuisine(),
                recipe.getPrepTime(),
                recipe.getCookingTime(),
                mealPlan.getMealDate(),
                mealPlan.getMealType(),
                mealPlan.getUser() != null ? mealPlan.getUser().getId() : null,
                mealPlan.getUser() != null ? mealPlan.getUser().getName() : "Demo User"
        );
    }
}
