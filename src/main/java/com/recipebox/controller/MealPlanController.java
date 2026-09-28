package com.recipebox.controller;

import com.recipebox.dto.MealPlanRequestDTO;
import com.recipebox.dto.MealPlanResponseDTO;
import com.recipebox.service.MealPlanService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDate;
import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/meal-plans")
@CrossOrigin(origins = "*")
@Tag(name = "Meal Plan Management", description = "Endpoints for scheduling and managing weekly meal plans")
public class MealPlanController {

    private final MealPlanService mealPlanService;

    @Autowired
    public MealPlanController(MealPlanService mealPlanService) {
        this.mealPlanService = mealPlanService;
    }

    @PostMapping
    @Operation(summary = "Assign a recipe to a specific day and meal type")
    public ResponseEntity<MealPlanResponseDTO> createMealPlan(@Valid @RequestBody MealPlanRequestDTO dto) {
        MealPlanResponseDTO created = mealPlanService.createMealPlan(dto);
        return new ResponseEntity<>(created, HttpStatus.CREATED);
    }

    @GetMapping
    @Operation(summary = "Get all scheduled meal plan entries")
    public ResponseEntity<List<MealPlanResponseDTO>> getAllMealPlans() {
        return ResponseEntity.ok(mealPlanService.getAllMealPlans());
    }

    @GetMapping("/week")
    @Operation(summary = "Get meal plans for a 7-day period starting from startDate")
    public ResponseEntity<List<MealPlanResponseDTO>> getWeeklyMealPlans(
            @RequestParam(required = false)
            @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate startDate) {
        if (startDate == null) {
            startDate = LocalDate.now();
        }
        return ResponseEntity.ok(mealPlanService.getWeeklyMealPlans(startDate));
    }

    @DeleteMapping("/{id}")
    @Operation(summary = "Remove a meal plan entry by ID")
    public ResponseEntity<Map<String, String>> deleteMealPlan(@PathVariable Long id) {
        mealPlanService.deleteMealPlan(id);
        return ResponseEntity.ok(Map.of("message", "Meal plan deleted successfully with id: " + id));
    }
}
