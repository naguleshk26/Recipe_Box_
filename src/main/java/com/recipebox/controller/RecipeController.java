package com.recipebox.controller;

import com.recipebox.dto.RecipeRequestDTO;
import com.recipebox.dto.RecipeResponseDTO;
import com.recipebox.service.RecipeService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/recipes")
@CrossOrigin(origins = "*")
@Tag(name = "Recipe Management", description = "Endpoints for creating, managing, and searching recipes")
public class RecipeController {

    private final RecipeService recipeService;

    @Autowired
    public RecipeController(RecipeService recipeService) {
        this.recipeService = recipeService;
    }

    @PostMapping
    @Operation(summary = "Create a new recipe with ingredients")
    public ResponseEntity<RecipeResponseDTO> createRecipe(@Valid @RequestBody RecipeRequestDTO dto) {
        RecipeResponseDTO created = recipeService.createRecipe(dto);
        return new ResponseEntity<>(created, HttpStatus.CREATED);
    }

    @GetMapping
    @Operation(summary = "Get all recipes")
    public ResponseEntity<List<RecipeResponseDTO>> getAllRecipes() {
        return ResponseEntity.ok(recipeService.getAllRecipes());
    }

    @GetMapping("/{id}")
    @Operation(summary = "Get recipe by ID")
    public ResponseEntity<RecipeResponseDTO> getRecipeById(@PathVariable Long id) {
        return ResponseEntity.ok(recipeService.getRecipeById(id));
    }

    @PutMapping("/{id}")
    @Operation(summary = "Update an existing recipe")
    public ResponseEntity<RecipeResponseDTO> updateRecipe(@PathVariable Long id, @Valid @RequestBody RecipeRequestDTO dto) {
        return ResponseEntity.ok(recipeService.updateRecipe(id, dto));
    }

    @DeleteMapping("/{id}")
    @Operation(summary = "Delete recipe by ID")
    public ResponseEntity<Map<String, String>> deleteRecipe(@PathVariable Long id) {
        recipeService.deleteRecipe(id);
        return ResponseEntity.ok(Map.of("message", "Recipe deleted successfully with id: " + id));
    }

    @GetMapping("/search")
    @Operation(summary = "Search recipes by keyword (title, ingredient, cuisine)")
    public ResponseEntity<List<RecipeResponseDTO>> searchRecipes(@RequestParam(required = false, defaultValue = "") String keyword) {
        return ResponseEntity.ok(recipeService.searchRecipes(keyword));
    }

    @GetMapping("/cuisine/{cuisine}")
    @Operation(summary = "Find recipes by cuisine")
    public ResponseEntity<List<RecipeResponseDTO>> getRecipesByCuisine(@PathVariable String cuisine) {
        return ResponseEntity.ok(recipeService.getRecipesByCuisine(cuisine));
    }

    @GetMapping("/ingredient/{ingredient}")
    @Operation(summary = "Find recipes containing a specific ingredient")
    public ResponseEntity<List<RecipeResponseDTO>> getRecipesByIngredient(@PathVariable String ingredient) {
        return ResponseEntity.ok(recipeService.getRecipesByIngredient(ingredient));
    }

    @PutMapping("/{id}/favourite")
    @Operation(summary = "Toggle favourite status of a recipe")
    public ResponseEntity<RecipeResponseDTO> toggleFavourite(@PathVariable Long id) {
        return ResponseEntity.ok(recipeService.toggleFavourite(id));
    }

    @GetMapping("/favourites")
    @Operation(summary = "Get all favourite recipes")
    public ResponseEntity<List<RecipeResponseDTO>> getFavouriteRecipes() {
        return ResponseEntity.ok(recipeService.getFavouriteRecipes());
    }
}
