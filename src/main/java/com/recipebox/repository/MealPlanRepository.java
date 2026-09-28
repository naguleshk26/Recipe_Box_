package com.recipebox.repository;

import com.recipebox.entity.MealPlan;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.time.LocalDate;
import java.util.List;

@Repository
public interface MealPlanRepository extends JpaRepository<MealPlan, Long> {

    List<MealPlan> findByMealDateBetweenOrderByMealDateAscMealTypeAsc(LocalDate startDate, LocalDate endDate);

    List<MealPlan> findByMealDateGreaterThanEqualOrderByMealDateAscMealTypeAsc(LocalDate startDate);

    List<MealPlan> findAllByOrderByMealDateAscMealTypeAsc();

    List<MealPlan> findTop7ByMealDateGreaterThanEqualOrderByMealDateAscMealTypeAsc(LocalDate date);

    boolean existsByRecipeId(Long recipeId);
}
