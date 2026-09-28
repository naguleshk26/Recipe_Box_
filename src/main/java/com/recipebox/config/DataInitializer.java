package com.recipebox.config;

import com.recipebox.dto.MealPlanRequestDTO;
import com.recipebox.dto.RecipeIngredientDTO;
import com.recipebox.dto.RecipeRequestDTO;
import com.recipebox.dto.RecipeResponseDTO;
import com.recipebox.entity.MealType;
import com.recipebox.entity.User;
import com.recipebox.repository.RecipeRepository;
import com.recipebox.service.MealPlanService;
import com.recipebox.service.RecipeService;
import com.recipebox.service.UserService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.CommandLineRunner;
import org.springframework.stereotype.Component;

import java.time.DayOfWeek;
import java.time.LocalDate;
import java.time.temporal.TemporalAdjusters;
import java.util.Arrays;
import java.util.List;

@Component
public class DataInitializer implements CommandLineRunner {

    private static final Logger logger = LoggerFactory.getLogger(DataInitializer.class);

    private final RecipeRepository recipeRepository;
    private final RecipeService recipeService;
    private final MealPlanService mealPlanService;
    private final UserService userService;

    @Autowired
    public DataInitializer(RecipeRepository recipeRepository,
                           RecipeService recipeService,
                           MealPlanService mealPlanService,
                           UserService userService) {
        this.recipeRepository = recipeRepository;
        this.recipeService = recipeService;
        this.mealPlanService = mealPlanService;
        this.userService = userService;
    }

    @Override
    public void run(String... args) {
        if (recipeRepository.count() > 0) {
            logger.info("Sample recipes already exist. Skipping seed data initialization.");
            return;
        }

        logger.info("Initializing sample data for RecipeBox...");

        User demoUser = userService.getOrCreateDemoUser();
        Long userId = demoUser.getId();

        // 1. Vegetable Fried Rice
        RecipeRequestDTO r1 = new RecipeRequestDTO(
                "Vegetable Fried Rice",
                "Flavorful Chinese style fried rice packed with colorful vegetables and seasoned with aromatic soy sauce.",
                "Chinese",
                15,
                20,
                "1. Cook rice and allow it to cool completely.\n2. Finely chop carrots, onions, and beans.\n3. Heat oil in a wok over high flame.\n4. Sauté onions and vegetables for 3-4 minutes.\n5. Add cooked rice and soy sauce.\n6. Toss gently, garnish with spring onions, and serve hot.",
                true,
                userId,
                Arrays.asList(
                        new RecipeIngredientDTO("Rice", 2.0, "cups"),
                        new RecipeIngredientDTO("Carrot", 1.0, "cup"),
                        new RecipeIngredientDTO("Onion", 1.0, "piece"),
                        new RecipeIngredientDTO("Oil", 2.0, "tbsp"),
                        new RecipeIngredientDTO("Soy Sauce", 1.0, "tbsp")
                )
        );
        RecipeResponseDTO friedRice = recipeService.createRecipe(r1);

        // 2. Tomato Pasta
        RecipeRequestDTO r2 = new RecipeRequestDTO(
                "Tomato Pasta",
                "Classic Italian penne pasta tossed in a rich, tangy garlic-herb tomato sauce.",
                "Italian",
                10,
                15,
                "1. Boil pasta in salted water until al dente, drain and reserve pasta water.\n2. Heat olive oil in a pan and sauté minced garlic.\n3. Add fresh chopped tomatoes and simmer until a saucy consistency forms.\n4. Season with salt, pepper, and freshly torn basil.\n5. Toss cooked pasta into the sauce and stir for 2 minutes.\n6. Serve warm with grated cheese.",
                true,
                userId,
                Arrays.asList(
                        new RecipeIngredientDTO("Pasta", 250.0, "g"),
                        new RecipeIngredientDTO("Tomato", 4.0, "pieces"),
                        new RecipeIngredientDTO("Garlic", 3.0, "cloves"),
                        new RecipeIngredientDTO("Olive Oil", 2.0, "tbsp"),
                        new RecipeIngredientDTO("Basil", 1.0, "tsp")
                )
        );
        RecipeResponseDTO tomatoPasta = recipeService.createRecipe(r2);

        // 3. Chicken Biryani
        RecipeRequestDTO r3 = new RecipeRequestDTO(
                "Chicken Biryani",
                "Aromatic, richly spiced royal Indian rice dish layered with tender marinated chicken.",
                "Indian",
                30,
                45,
                "1. Marinate chicken with yogurt, biryani masala, and salt for 30 minutes.\n2. Cook basmati rice with whole spices until 70% done.\n3. Heat oil and ghee in a heavy pot, brown sliced onions.\n4. Cook marinated chicken until tender.\n5. Layer semi-cooked rice over chicken, sprinkle fried onions and fresh mint.\n6. Seal pot and slow cook (dum) for 20 minutes on low heat.\n7. Fluff and serve with cool cucumber raita.",
                false,
                userId,
                Arrays.asList(
                        new RecipeIngredientDTO("Chicken", 500.0, "g"),
                        new RecipeIngredientDTO("Rice", 2.0, "cups"),
                        new RecipeIngredientDTO("Onion", 2.0, "pieces"),
                        new RecipeIngredientDTO("Yogurt", 1.0, "cup"),
                        new RecipeIngredientDTO("Oil", 2.0, "tbsp")
                )
        );
        RecipeResponseDTO biryani = recipeService.createRecipe(r3);

        // 4. Lemon Rice
        RecipeRequestDTO r4 = new RecipeRequestDTO(
                "Lemon Rice",
                "Tangy and crunchy South Indian tempered rice infused with lemon juice, curry leaves, and crunchy peanuts.",
                "South Indian",
                10,
                15,
                "1. Spread cooked rice on a flat tray to cool.\n2. Heat oil in a pan and add mustard seeds.\n3. Add peanuts and fry until golden and crunchy.\n4. Add curry leaves, green chillies, and turmeric powder.\n5. Turn off heat and stir in lemon juice and salt.\n6. Pour tempering over rice and mix evenly until vibrant yellow.",
                true,
                userId,
                Arrays.asList(
                        new RecipeIngredientDTO("Rice", 2.0, "cups"),
                        new RecipeIngredientDTO("Lemon Juice", 3.0, "tbsp"),
                        new RecipeIngredientDTO("Mustard Seeds", 1.0, "tsp"),
                        new RecipeIngredientDTO("Peanuts", 2.0, "tbsp"),
                        new RecipeIngredientDTO("Oil", 1.0, "tbsp")
                )
        );
        RecipeResponseDTO lemonRice = recipeService.createRecipe(r4);

        // 5. Vegetable Sandwich
        RecipeRequestDTO r5 = new RecipeRequestDTO(
                "Vegetable Sandwich",
                "Crispy, wholesome toasted sandwich filled with sliced cucumbers, tomatoes, and gooey cheese.",
                "Continental",
                10,
                5,
                "1. Butter bread slices lightly.\n2. Layer thin slices of fresh cucumber and tomato.\n3. Sprinkle black pepper and a pinch of salt.\n4. Place cheese slices and top with another bread slice.\n5. Toast on a skillet until golden-brown and crispy.",
                false,
                userId,
                Arrays.asList(
                        new RecipeIngredientDTO("Bread Slices", 4.0, "pieces"),
                        new RecipeIngredientDTO("Cucumber", 1.0, "piece"),
                        new RecipeIngredientDTO("Tomato", 1.0, "piece"),
                        new RecipeIngredientDTO("Butter", 1.0, "tbsp"),
                        new RecipeIngredientDTO("Cheese Slice", 2.0, "pieces")
                )
        );
        RecipeResponseDTO vegSandwich = recipeService.createRecipe(r5);

        // Create sample meal plans for current week (Monday to Sunday)
        LocalDate thisMonday = LocalDate.now().with(TemporalAdjusters.previousOrSame(DayOfWeek.MONDAY));

        mealPlanService.createMealPlan(new MealPlanRequestDTO(vegSandwich.getId(), thisMonday, MealType.BREAKFAST, userId));
        mealPlanService.createMealPlan(new MealPlanRequestDTO(lemonRice.getId(), thisMonday, MealType.LUNCH, userId));
        mealPlanService.createMealPlan(new MealPlanRequestDTO(tomatoPasta.getId(), thisMonday, MealType.DINNER, userId));

        mealPlanService.createMealPlan(new MealPlanRequestDTO(biryani.getId(), thisMonday.plusDays(1), MealType.LUNCH, userId));
        mealPlanService.createMealPlan(new MealPlanRequestDTO(friedRice.getId(), thisMonday.plusDays(1), MealType.DINNER, userId));

        // Note: Fried Rice planned again on Wednesday - testing aggregation!
        mealPlanService.createMealPlan(new MealPlanRequestDTO(friedRice.getId(), thisMonday.plusDays(2), MealType.DINNER, userId));

        mealPlanService.createMealPlan(new MealPlanRequestDTO(vegSandwich.getId(), thisMonday.plusDays(3), MealType.BREAKFAST, userId));
        mealPlanService.createMealPlan(new MealPlanRequestDTO(tomatoPasta.getId(), thisMonday.plusDays(3), MealType.LUNCH, userId));

        logger.info("Sample recipes and weekly meal plans successfully initialized!");
    }
}
