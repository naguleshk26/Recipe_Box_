package com.recipebox;

import com.recipebox.dto.MealPlanRequestDTO;
import com.recipebox.dto.RecipeIngredientDTO;
import com.recipebox.dto.RecipeRequestDTO;
import com.recipebox.dto.ShoppingListItemDTO;
import com.recipebox.entity.*;
import com.recipebox.exception.BadRequestException;
import com.recipebox.exception.ResourceNotFoundException;
import com.recipebox.repository.IngredientRepository;
import com.recipebox.repository.MealPlanRepository;
import com.recipebox.repository.RecipeIngredientRepository;
import com.recipebox.repository.RecipeRepository;
import com.recipebox.service.MealPlanService;
import com.recipebox.service.RecipeService;
import com.recipebox.service.ShoppingListService;
import com.recipebox.service.UserService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.time.LocalDate;
import java.util.*;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
public class RecipeBoxServiceTests {

    @Mock
    private RecipeRepository recipeRepository;

    @Mock
    private IngredientRepository ingredientRepository;

    @Mock
    private RecipeIngredientRepository recipeIngredientRepository;

    @Mock
    private MealPlanRepository mealPlanRepository;

    @Mock
    private UserService userService;

    @InjectMocks
    private RecipeService recipeService;

    @InjectMocks
    private MealPlanService mealPlanService;

    @InjectMocks
    private ShoppingListService shoppingListService;

    private User sampleUser;
    private Recipe sampleRecipe;
    private Ingredient rice;
    private Ingredient carrot;

    @BeforeEach
    void setUp() {
        sampleUser = new User("Test Cook", "cook@recipebox.com", "password");
        sampleUser.setId(1L);

        sampleRecipe = new Recipe("Vegetable Fried Rice", "Quick fried rice", "Chinese", 15, 20, "Cook and toss", false);
        sampleRecipe.setId(100L);
        sampleRecipe.setUser(sampleUser);

        rice = new Ingredient("Rice");
        rice.setId(10L);
        carrot = new Ingredient("Carrot");
        carrot.setId(11L);

        RecipeIngredient ri1 = new RecipeIngredient(sampleRecipe, rice, 2.0, "cups");
        RecipeIngredient ri2 = new RecipeIngredient(sampleRecipe, carrot, 1.0, "cup");

        sampleRecipe.setRecipeIngredients(new ArrayList<>(Arrays.asList(ri1, ri2)));
    }

    @Test
    @DisplayName("Business Rule 1: Meal plan creation fails if recipe does not exist")
    void testMealPlanFailsWhenRecipeDoesNotExist() {
        MealPlanRequestDTO request = new MealPlanRequestDTO(999L, LocalDate.now(), MealType.LUNCH, 1L);

        when(recipeRepository.findById(999L)).thenReturn(Optional.empty());

        ResourceNotFoundException ex = assertThrows(ResourceNotFoundException.class, () -> {
            mealPlanService.createMealPlan(request);
        });

        assertEquals("Cannot create meal plan. The selected recipe does not exist.", ex.getMessage());
        verify(mealPlanRepository, never()).save(any());
    }

    @Test
    @DisplayName("Business Rule 1 (Success): Meal plan creation succeeds when recipe exists")
    void testMealPlanSucceedsWhenRecipeExists() {
        MealPlanRequestDTO request = new MealPlanRequestDTO(100L, LocalDate.now(), MealType.DINNER, 1L);

        when(recipeRepository.findById(100L)).thenReturn(Optional.of(sampleRecipe));
        when(userService.getUserEntity(1L)).thenReturn(sampleUser);
        when(mealPlanRepository.save(any(MealPlan.class))).thenAnswer(invocation -> {
            MealPlan mp = invocation.getArgument(0);
            mp.setId(500L);
            return mp;
        });

        var response = mealPlanService.createMealPlan(request);

        assertNotNull(response);
        assertEquals(500L, response.getId());
        assertEquals(100L, response.getRecipeId());
        assertEquals("Vegetable Fried Rice", response.getRecipeTitle());
        assertEquals(MealType.DINNER, response.getMealType());
    }

    @Test
    @DisplayName("Business Rule 2: Shopping list aggregates quantities across all planned meals")
    void testShoppingListAggregationAcrossMeals() {
        LocalDate startDate = LocalDate.of(2026, 9, 28);
        LocalDate endDate = startDate.plusDays(6);

        // Meal 1: Vegetable Fried Rice (2 cups Rice, 1 cup Carrot)
        MealPlan meal1 = new MealPlan(sampleUser, sampleRecipe, startDate, MealType.LUNCH);

        // Meal 2: Same recipe planned a second time (2 cups Rice, 1 cup Carrot)
        MealPlan meal2 = new MealPlan(sampleUser, sampleRecipe, startDate.plusDays(1), MealType.DINNER);

        when(mealPlanRepository.findByMealDateBetweenOrderByMealDateAscMealTypeAsc(startDate, endDate))
                .thenReturn(Arrays.asList(meal1, meal2));

        List<ShoppingListItemDTO> shoppingList = shoppingListService.generateShoppingList(startDate, endDate);

        assertNotNull(shoppingList);
        assertEquals(2, shoppingList.size());

        // Carrot should aggregate: 1 + 1 = 2 cups
        ShoppingListItemDTO carrotItem = shoppingList.stream()
                .filter(i -> i.getIngredientName().equalsIgnoreCase("Carrot"))
                .findFirst().orElse(null);
        assertNotNull(carrotItem);
        assertEquals(2.0, carrotItem.getTotalQuantity());
        assertEquals("cup", carrotItem.getUnit());

        // Rice should aggregate: 2 + 2 = 4 cups
        ShoppingListItemDTO riceItem = shoppingList.stream()
                .filter(i -> i.getIngredientName().equalsIgnoreCase("Rice"))
                .findFirst().orElse(null);
        assertNotNull(riceItem);
        assertEquals(4.0, riceItem.getTotalQuantity());
        assertEquals("cups", riceItem.getUnit());
    }

    @Test
    @DisplayName("Validation Rules: Recipe title cannot be empty")
    void testRecipeTitleCannotBeEmpty() {
        RecipeRequestDTO request = new RecipeRequestDTO(
                "",
                "Description",
                "Italian",
                10,
                15,
                "Cook pasta",
                false,
                1L,
                Collections.singletonList(new RecipeIngredientDTO("Pasta", 200.0, "g"))
        );

        BadRequestException ex = assertThrows(BadRequestException.class, () -> {
            recipeService.createRecipe(request);
        });

        assertEquals("Recipe title cannot be empty", ex.getMessage());
    }

    @Test
    @DisplayName("Validation Rules: Quantity must be positive")
    void testIngredientQuantityMustBePositive() {
        RecipeRequestDTO request = new RecipeRequestDTO(
                "Valid Title",
                "Description",
                "Italian",
                10,
                15,
                "Cook pasta",
                false,
                1L,
                Collections.singletonList(new RecipeIngredientDTO("Pasta", -5.0, "g"))
        );

        BadRequestException ex = assertThrows(BadRequestException.class, () -> {
            recipeService.createRecipe(request);
        });

        assertEquals("Quantity must be greater than zero", ex.getMessage());
    }

    @Test
    @DisplayName("Validation Rules: Prep time cannot be negative")
    void testPrepTimeCannotBeNegative() {
        RecipeRequestDTO request = new RecipeRequestDTO(
                "Valid Title",
                "Description",
                "Italian",
                -10,
                15,
                "Cook pasta",
                false,
                1L,
                Collections.singletonList(new RecipeIngredientDTO("Pasta", 200.0, "g"))
        );

        BadRequestException ex = assertThrows(BadRequestException.class, () -> {
            recipeService.createRecipe(request);
        });

        assertEquals("Preparation time cannot be negative", ex.getMessage());
    }

    @Test
    @DisplayName("Recipe ID not found returns clear error message")
    void testGetRecipeNotFound() {
        when(recipeRepository.findById(888L)).thenReturn(Optional.empty());

        ResourceNotFoundException ex = assertThrows(ResourceNotFoundException.class, () -> {
            recipeService.getRecipeById(888L);
        });

        assertEquals("Recipe not found with id: 888", ex.getMessage());
    }
}
