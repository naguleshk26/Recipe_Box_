package com.recipebox.service;

import com.recipebox.dto.ShoppingListItemDTO;
import com.recipebox.entity.MealPlan;
import com.recipebox.entity.Recipe;
import com.recipebox.entity.RecipeIngredient;
import com.recipebox.repository.MealPlanRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.util.*;

@Service
@Transactional(readOnly = true)
public class ShoppingListService {

    private final MealPlanRepository mealPlanRepository;

    @Autowired
    public ShoppingListService(MealPlanRepository mealPlanRepository) {
        this.mealPlanRepository = mealPlanRepository;
    }

    /**
     * CRITICAL BUSINESS RULE:
     * Consolidated shopping list for the selected date range / week.
     * Examines all recipes planned during that week.
     * Aggregates ingredient quantities across ALL planned meals in the service layer.
     */
    public List<ShoppingListItemDTO> generateShoppingList(LocalDate startDate, LocalDate endDate) {
        if (startDate == null) {
            startDate = LocalDate.now();
        }
        if (endDate == null) {
            endDate = startDate.plusDays(6);
        }

        List<MealPlan> mealPlans = mealPlanRepository.findByMealDateBetweenOrderByMealDateAscMealTypeAsc(startDate, endDate);

        // Map key: "normalizedIngredientName###normalizedUnit"
        // Map value: an aggregator holding display name, total quantity, and display unit
        Map<String, AggregatedItem> aggregationMap = new LinkedHashMap<>();

        for (MealPlan plan : mealPlans) {
            Recipe recipe = plan.getRecipe();
            if (recipe == null || recipe.getRecipeIngredients() == null) {
                continue;
            }

            for (RecipeIngredient ri : recipe.getRecipeIngredients()) {
                String rawName = ri.getIngredient().getName();
                String rawUnit = ri.getUnit();
                Double quantity = ri.getQuantity();

                if (rawName == null || quantity == null) {
                    continue;
                }

                String cleanName = rawName.trim();
                String cleanUnit = rawUnit != null ? rawUnit.trim() : "";

                String key = cleanName.toLowerCase() + "###" + cleanUnit.toLowerCase();

                if (aggregationMap.containsKey(key)) {
                    AggregatedItem existing = aggregationMap.get(key);
                    existing.quantity += quantity;
                } else {
                    aggregationMap.put(key, new AggregatedItem(cleanName, cleanUnit, quantity));
                }
            }
        }

        List<ShoppingListItemDTO> result = new ArrayList<>();
        for (AggregatedItem item : aggregationMap.values()) {
            // Round nicely to 2 decimal places if needed
            double roundedQuantity = Math.round(item.quantity * 100.0) / 100.0;
            result.add(new ShoppingListItemDTO(item.name, roundedQuantity, item.unit));
        }

        // Sort alphabetically by ingredient name
        result.sort(Comparator.comparing(ShoppingListItemDTO::getIngredientName, String.CASE_INSENSITIVE_ORDER));

        return result;
    }

    private static class AggregatedItem {
        String name;
        String unit;
        double quantity;

        AggregatedItem(String name, String unit, double quantity) {
            this.name = name;
            this.unit = unit;
            this.quantity = quantity;
        }
    }
}
