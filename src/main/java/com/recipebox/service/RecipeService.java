package com.recipebox.service;

import com.recipebox.dto.RecipeIngredientDTO;
import com.recipebox.dto.RecipeRequestDTO;
import com.recipebox.dto.RecipeResponseDTO;
import com.recipebox.entity.Ingredient;
import com.recipebox.entity.Recipe;
import com.recipebox.entity.RecipeIngredient;
import com.recipebox.entity.User;
import com.recipebox.exception.BadRequestException;
import com.recipebox.exception.ResourceNotFoundException;
import com.recipebox.repository.IngredientRepository;
import com.recipebox.repository.RecipeIngredientRepository;
import com.recipebox.repository.RecipeRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

@Service
@Transactional
public class RecipeService {

    private final RecipeRepository recipeRepository;
    private final IngredientRepository ingredientRepository;
    private final RecipeIngredientRepository recipeIngredientRepository;
    private final UserService userService;

    @Autowired
    public RecipeService(RecipeRepository recipeRepository,
                         IngredientRepository ingredientRepository,
                         RecipeIngredientRepository recipeIngredientRepository,
                         UserService userService) {
        this.recipeRepository = recipeRepository;
        this.ingredientRepository = ingredientRepository;
        this.recipeIngredientRepository = recipeIngredientRepository;
        this.userService = userService;
    }

    public RecipeResponseDTO createRecipe(RecipeRequestDTO dto) {
        validateRecipeRequest(dto);

        User user = userService.getUserEntity(dto.getUserId());

        Recipe recipe = new Recipe();
        recipe.setTitle(dto.getTitle().trim());
        recipe.setDescription(dto.getDescription() != null ? dto.getDescription().trim() : "");
        recipe.setCuisine(dto.getCuisine().trim());
        recipe.setPrepTime(dto.getPrepTime());
        recipe.setCookingTime(dto.getCookingTime());
        recipe.setSteps(dto.getSteps().trim());
        recipe.setFavourite(dto.getFavourite() != null ? dto.getFavourite() : false);
        recipe.setCreatedAt(LocalDateTime.now());
        recipe.setUser(user);

        // Save recipe first to obtain ID
        Recipe savedRecipe = recipeRepository.save(recipe);

        // Save ingredients
        attachIngredients(savedRecipe, dto.getIngredients());
        Recipe finalRecipe = recipeRepository.save(savedRecipe);

        return toResponseDTO(finalRecipe);
    }

    @Transactional(readOnly = true)
    public RecipeResponseDTO getRecipeById(Long id) {
        Recipe recipe = recipeRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Recipe not found with id: " + id));
        return toResponseDTO(recipe);
    }

    @Transactional(readOnly = true)
    public List<RecipeResponseDTO> getAllRecipes() {
        return recipeRepository.findAll().stream()
                .map(this::toResponseDTO)
                .collect(Collectors.toList());
    }

    public RecipeResponseDTO updateRecipe(Long id, RecipeRequestDTO dto) {
        validateRecipeRequest(dto);

        Recipe recipe = recipeRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Recipe not found with id: " + id));

        recipe.setTitle(dto.getTitle().trim());
        recipe.setDescription(dto.getDescription() != null ? dto.getDescription().trim() : "");
        recipe.setCuisine(dto.getCuisine().trim());
        recipe.setPrepTime(dto.getPrepTime());
        recipe.setCookingTime(dto.getCookingTime());
        recipe.setSteps(dto.getSteps().trim());
        if (dto.getFavourite() != null) {
            recipe.setFavourite(dto.getFavourite());
        }

        if (dto.getUserId() != null) {
            recipe.setUser(userService.getUserEntity(dto.getUserId()));
        }

        // Clear existing recipe ingredients and add new
        recipe.getRecipeIngredients().clear();
        recipeRepository.saveAndFlush(recipe);

        attachIngredients(recipe, dto.getIngredients());
        Recipe updated = recipeRepository.save(recipe);

        return toResponseDTO(updated);
    }

    public void deleteRecipe(Long id) {
        Recipe recipe = recipeRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Recipe not found with id: " + id));
        recipeRepository.delete(recipe);
    }

    @Transactional(readOnly = true)
    public List<RecipeResponseDTO> searchRecipes(String keyword) {
        if (keyword == null || keyword.trim().isEmpty()) {
            return getAllRecipes();
        }
        return recipeRepository.searchByKeyword(keyword.trim()).stream()
                .map(this::toResponseDTO)
                .collect(Collectors.toList());
    }

    @Transactional(readOnly = true)
    public List<RecipeResponseDTO> getRecipesByCuisine(String cuisine) {
        if (cuisine == null || cuisine.trim().isEmpty()) {
            return getAllRecipes();
        }
        return recipeRepository.findByCuisineIgnoreCase(cuisine.trim()).stream()
                .map(this::toResponseDTO)
                .collect(Collectors.toList());
    }

    @Transactional(readOnly = true)
    public List<RecipeResponseDTO> getRecipesByIngredient(String ingredient) {
        if (ingredient == null || ingredient.trim().isEmpty()) {
            return getAllRecipes();
        }
        return recipeRepository.findByIngredientName(ingredient.trim()).stream()
                .map(this::toResponseDTO)
                .collect(Collectors.toList());
    }

    public RecipeResponseDTO toggleFavourite(Long id) {
        Recipe recipe = recipeRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Recipe not found with id: " + id));
        recipe.setFavourite(!Boolean.TRUE.equals(recipe.getFavourite()));
        Recipe saved = recipeRepository.save(recipe);
        return toResponseDTO(saved);
    }

    @Transactional(readOnly = true)
    public List<RecipeResponseDTO> getFavouriteRecipes() {
        return recipeRepository.findByFavouriteTrue().stream()
                .map(this::toResponseDTO)
                .collect(Collectors.toList());
    }

    private void attachIngredients(Recipe recipe, List<RecipeIngredientDTO> ingredientDTOs) {
        if (ingredientDTOs == null) return;

        for (RecipeIngredientDTO ingDTO : ingredientDTOs) {
            String ingName = ingDTO.getIngredientName().trim();
            // Reusable ingredient lookup or create
            Ingredient ingredient = ingredientRepository.findByNameIgnoreCase(ingName)
                    .orElseGet(() -> ingredientRepository.save(new Ingredient(ingName)));

            RecipeIngredient recipeIngredient = new RecipeIngredient(
                    recipe,
                    ingredient,
                    ingDTO.getQuantity(),
                    ingDTO.getUnit().trim()
            );
            recipe.addRecipeIngredient(recipeIngredient);
        }
    }

    private void validateRecipeRequest(RecipeRequestDTO dto) {
        if (dto.getTitle() == null || dto.getTitle().trim().isEmpty()) {
            throw new BadRequestException("Recipe title cannot be empty");
        }
        if (dto.getPrepTime() == null || dto.getPrepTime() < 0) {
            throw new BadRequestException("Preparation time cannot be negative");
        }
        if (dto.getCookingTime() == null || dto.getCookingTime() < 0) {
            throw new BadRequestException("Cooking time cannot be negative");
        }
        if (dto.getIngredients() == null || dto.getIngredients().isEmpty()) {
            throw new BadRequestException("Recipe must have at least one ingredient");
        }
        for (RecipeIngredientDTO ing : dto.getIngredients()) {
            if (ing.getIngredientName() == null || ing.getIngredientName().trim().isEmpty()) {
                throw new BadRequestException("Ingredient name cannot be empty");
            }
            if (ing.getQuantity() == null || ing.getQuantity() <= 0) {
                throw new BadRequestException("Quantity must be greater than zero");
            }
            if (ing.getUnit() == null || ing.getUnit().trim().isEmpty()) {
                throw new BadRequestException("Ingredient unit cannot be empty");
            }
        }
    }

    public RecipeResponseDTO toResponseDTO(Recipe recipe) {
        List<RecipeIngredientDTO> ingList = recipe.getRecipeIngredients().stream()
                .map(ri -> new RecipeIngredientDTO(
                        ri.getId(),
                        ri.getIngredient().getName(),
                        ri.getQuantity(),
                        ri.getUnit()
                ))
                .collect(Collectors.toList());

        return new RecipeResponseDTO(
                recipe.getId(),
                recipe.getTitle(),
                recipe.getDescription(),
                recipe.getCuisine(),
                recipe.getPrepTime(),
                recipe.getCookingTime(),
                recipe.getSteps(),
                recipe.getFavourite(),
                recipe.getCreatedAt(),
                recipe.getUser() != null ? recipe.getUser().getId() : null,
                recipe.getUser() != null ? recipe.getUser().getName() : "Demo User",
                ingList
        );
    }
}
