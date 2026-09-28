# RecipeBox – Personal Recipe and Meal Planner

A full-stack web application designed for home cooks, hostel students, and busy families to organize homemade recipes, plan weekly meals, and automatically generate consolidated grocery shopping lists.

Built with **Java Spring Boot**, **MySQL**, **Spring Data JPA**, **Hibernate**, and **Vanilla HTML5/CSS3/JavaScript (Fetch API)**.

---

## Table of Contents
1. [Project Description](#project-description)
2. [Key Features](#key-features)
3. [Technology Stack](#technology-stack)
4. [Database Design](#database-design)
5. [Project Architecture & Folder Structure](#project-architecture--folder-structure)
6. [Prerequisites](#prerequisites)
7. [MySQL Database Setup](#mysql-database-setup)
8. [Configuration (application.properties)](#configuration-applicationproperties)
9. [How to Run the Application](#how-to-run-the-application)
10. [REST API Documentation & Swagger UI](#rest-api-documentation--swagger-ui)
11. [Service Layer Business Rules & Validations](#service-layer-business-rules--validations)
12. [User Interface Walkthrough](#user-interface-walkthrough)

---

## 1. Project Description

**RecipeBox** is a personal recipe and weekly meal planning application.

Managing daily cooking decisions often leads to repetitive meals, forgotten ingredients, and disorganized grocery runs. **RecipeBox** solves this by providing:
- A digital recipe notebook with ingredient quantities, preparation times, cooking times, and step-by-step instructions.
- A 7-day weekly meal planning calendar (Monday to Sunday) covering Breakfast, Lunch, Dinner, and Snacks.
- A consolidated shopping list engine that examines all planned meals and sums up required ingredients automatically.
- A dashboard with quick statistics and an overview of upcoming meals.

---

## 2. Key Features

- **Feature 1 – Add & Edit Recipes**:
  - Interactive form with dynamic ingredient rows (Name, Quantity, Unit).
  - Preparation and cooking time tracking.
  - Cuisine tagging (Indian, South Indian, Chinese, Italian, Continental, etc.).
  - Step-by-step preparation steps.
  - Automatic reuse of existing ingredient names across recipes.

- **Feature 2 – Search & Filter Recipes**:
  - Instant real-time search across recipe titles, cuisines, and ingredients.
  - Search by ingredient (e.g., "Rice", "Tomato").
  - Filter recipes by cuisine.

- **Feature 3 – Weekly Meal Planner**:
  - 7-Day calendar grid (Monday to Sunday) with Breakfast, Lunch, Dinner, and Snack slots.
  - Quick scheduling of any recipe to any date and meal type.
  - Week navigation (Previous Week, Current Week, Next Week).
  - **Business Rule**: Validates recipe existence before creating meal plan entries.

- **Feature 4 – Consolidated Shopping List**:
  - Automatically calculates total ingredient quantities across all planned meals for the selected date range.
  - If a recipe is planned multiple times, ingredients are multiplied and aggregated.
  - If multiple distinct recipes use the same ingredient (e.g. Tomato or Rice), quantities are combined into a single entry.
  - Interactive checkbox checklist to cross off items while shopping in the supermarket.
  - One-click **Print Shopping List** button formatted for clean paper printing (`@media print`).

- **Feature 5 – Favourites System**:
  - Quick-toggle favourite status (`♥` / `♡`) directly from recipe cards.
  - Dedicated Favourites page displaying curated dishes.

- **Feature 6 – Dashboard**:
  - Summary cards displaying Total Recipes, Favourite Recipes, Planned Meals, and Total Ingredients.
  - Chronological list of upcoming planned meals with date, slot, and prep time.

---

## 3. Technology Stack

### Frontend:
- **HTML5** – Semantic structure and responsive layout
- **CSS3** – Custom master design system, responsive grid/flexbox, glassmorphism, print stylesheet
- **Vanilla JavaScript (ES6+)** – Asynchronous Fetch API, DOM manipulation, dynamic row insertion
- *No heavy frontend frameworks (no React, Angular, or Vue) – clean and lightweight for college mini projects.*

### Backend:
- **Java 17+**
- **Spring Boot 3.3.x**
- **Spring Web (Spring MVC)** – RESTful API controllers
- **Spring Data JPA & Hibernate** – ORM and automated DDL schema management
- **Bean Validation (Hibernate Validator)** – Robust request validation
- **SpringDoc OpenAPI 3 / Swagger UI** – Live interactive API documentation

### Database:
- **MySQL 8.0+**

### Build Tool:
- **Apache Maven**

---

## 4. Database Design

### Entity Relationship Diagram:

```
+--------------------+            +--------------------+
|       User         | 1        * |      Recipe        |
|--------------------|------------|--------------------|
| id (PK)            |            | id (PK)            |
| name               |            | title              |
| email (Unique)     |            | description        |
| password           |            | cuisine            |
+--------------------+            | prepTime           |
        | 1                       | cookingTime        |
        |                         | steps (TEXT)       |
        |                         | favourite          |
        |                         | createdAt          |
        | *                       | user_id (FK)       |
+--------------------+            +--------------------+
|     MealPlan       |                      | 1
|--------------------|                      |
| id (PK)            |                      |
| mealDate           |                      | *
| mealType           |            +--------------------+
| user_id (FK)       |            |  RecipeIngredient  |
| recipe_id (FK)-----|------------|--------------------|
+--------------------+            | id (PK)            |
                                  | recipe_id (FK)     |
                                  | ingredient_id (FK)-|----+
                                  | quantity           |    |
                                  | unit               |    |
                                  +--------------------+    |
                                                            | *
                                  +--------------------+    |
                                  |    Ingredient      | 1  |
                                  |--------------------|----+
                                  | id (PK)            |
                                  | name (Unique)      |
                                  +--------------------+
```

### Tables Overview:
1. **`users`**: User profile credentials (id, name, email, password).
2. **`recipes`**: Recipe details (id, title, description, cuisine, prep_time, cooking_time, steps, favourite, created_at, user_id).
3. **`ingredients`**: Reusable ingredient catalog (id, name).
4. **`recipe_ingredients`**: Join table connecting recipes with ingredients along with quantity and unit (id, recipe_id, ingredient_id, quantity, unit).
5. **`meal_plans`**: Meal scheduling records (id, user_id, recipe_id, meal_date, meal_type).

---

## 5. Project Architecture & Folder Structure

```
recipebox/
│
├── pom.xml                                      # Maven configuration & dependencies
├── README.md                                    # Project documentation
│
└── src/
    ├── main/
    │   ├── java/com/recipebox/
    │   │   ├── RecipeBoxApplication.java       # Spring Boot main entry point
    │   │   │
    │   │   ├── config/
    │   │   │   └── DataInitializer.java        # Seeds demo user, 5 recipes, & sample meal plans
    │   │   │
    │   │   ├── controller/
    │   │   │   ├── UserController.java          # /api/users
    │   │   │   ├── RecipeController.java        # /api/recipes
    │   │   │   ├── MealPlanController.java      # /api/meal-plans
    │   │   │   ├── ShoppingListController.java  # /api/shopping-list
    │   │   │   └── DashboardController.java     # /api/dashboard
    │   │   │
    │   │   ├── service/
    │   │   │   ├── UserService.java             # User management
    │   │   │   ├── RecipeService.java           # Recipe CRUD, search, favourite toggle
    │   │   │   ├── MealPlanService.java         # Meal scheduling & existence validation
    │   │   │   ├── ShoppingListService.java     # Service-layer ingredient quantity aggregation
    │   │   │   └── DashboardService.java        # Summary counts & upcoming schedules
    │   │   │
    │   │   ├── repository/
    │   │   │   ├── UserRepository.java
    │   │   │   ├── RecipeRepository.java
    │   │   │   ├── IngredientRepository.java
    │   │   │   ├── RecipeIngredientRepository.java
    │   │   │   └── MealPlanRepository.java
    │   │   │
    │   │   ├── entity/
    │   │   │   ├── User.java
    │   │   │   ├── Recipe.java
    │   │   │   ├── Ingredient.java
    │   │   │   ├── RecipeIngredient.java
    │   │   │   ├── MealPlan.java
    │   │   │   └── MealType.java (BREAKFAST, LUNCH, DINNER, SNACK)
    │   │   │
    │   │   ├── dto/
    │   │   │   ├── UserDTO.java
    │   │   │   ├── RecipeRequestDTO.java
    │   │   │   ├── RecipeResponseDTO.java
    │   │   │   ├── RecipeIngredientDTO.java
    │   │   │   ├── MealPlanRequestDTO.java
    │   │   │   ├── MealPlanResponseDTO.java
    │   │   │   ├── ShoppingListItemDTO.java
    │   │   │   ├── DashboardStatsDTO.java
    │   │   │   └── ErrorResponseDTO.java
    │   │   │
    │   │   └── exception/
    │   │       ├── ResourceNotFoundException.java
    │   │       ├── BadRequestException.java
    │   │       └── GlobalExceptionHandler.java  # @RestControllerAdvice clean error handler
    │   │
    │   └── resources/
    │       ├── application.properties           # Database & server configuration
    │       │
    │       └── static/                          # Frontend files served by Spring Boot
    │           ├── index.html                   # Landing & featured recipes
    │           ├── recipes.html                 # Recipes list with search & filters
    │           ├── add-recipe.html              # Dynamic recipe creation & edit form
    │           ├── meal-plan.html               # 7-day weekly meal planner calendar
    │           ├── shopping-list.html           # Consolidated grocery checklist
    │           ├── favourites.html              # Favourite recipes collection
    │           ├── dashboard.html               # Statistics & upcoming meal overview
    │           │
    │           ├── css/
    │           │   └── style.css                # Master CSS stylesheet
    │           │
    │           └── js/
    │               ├── app.js                   # Common utilities, API client, toast notifications
    │               ├── recipes.js               # Recipes listing, search & modal logic
    │               ├── meal-plan.js             # Calendar logic & scheduling modal
    │               ├── shopping-list.js         # Date filtering & checklist logic
    │               └── dashboard.js             # Live statistics counters & upcoming meals
    │
    └── test/java/com/recipebox/
        └── RecipeBoxServiceTests.java           # Unit & business rule tests
```

---

## 6. Prerequisites

Make sure the following are installed on your machine:
- **Java Development Kit (JDK 17 or later)** (`java -version`)
- **Apache Maven (3.8 or later)** (`mvn -version`)
- **MySQL Server (8.0 or later)** running on `localhost:3306`

---

## 7. MySQL Database Setup

1. Open your MySQL client (MySQL Workbench, MySQL Command Line, or phpMyAdmin).
2. Create the database:
   ```sql
   CREATE DATABASE IF NOT EXISTS recipebox_db;
   ```
3. Hibernate will automatically create all tables and foreign keys upon starting the application (`spring.jpa.hibernate.ddl-auto=update`).

---

## 8. Configuration (`application.properties`)

The database configuration is located in `src/main/resources/application.properties`:

```properties
# Connect to MySQL on port 3306
spring.datasource.url=jdbc:mysql://localhost:3306/recipebox_db?createDatabaseIfNotExist=true&useSSL=false&serverTimezone=UTC&allowPublicKeyRetrieval=true
spring.datasource.username=root

# IMPORTANT: Replace YOUR_PASSWORD with your actual local MySQL password
# (Or pass it using the DB_PASSWORD environment variable)
spring.datasource.password=${DB_PASSWORD:YOUR_PASSWORD}

# Hibernate JPA settings
spring.jpa.hibernate.ddl-auto=update
spring.jpa.show-sql=true
spring.jpa.properties.hibernate.format_sql=true

# Server port
server.port=8080

# Swagger / OpenAPI documentation
springdoc.api-docs.path=/v3/api-docs
springdoc.swagger-ui.path=/swagger-ui.html
springdoc.swagger-ui.enabled=true
```

> **Note**: Replace `YOUR_PASSWORD` with your actual MySQL root password (e.g. `root`, `admin`, or your custom password). Alternatively, you can run `export DB_PASSWORD=your_password` in terminal before launching.

---

## 9. How to Run the Application

### Step 1: Open Terminal in Project Directory
```bash
cd e:/Receipe_Box
```

### Step 2: Run with Maven
```bash
mvn spring-boot:run
```

*(On Windows PowerShell, if you wish to pass your MySQL password directly without modifying the file)*:
```powershell
$env:DB_PASSWORD="your_password"; mvn spring-boot:run
```

### Step 3: Open in Browser
Once the console displays `Started RecipeBoxApplication in ... seconds`:
- **Web Application**: [http://localhost:8080](http://localhost:8080)
- **Interactive Swagger Documentation**: [http://localhost:8080/swagger-ui.html](http://localhost:8080/swagger-ui.html)
- **OpenAPI JSON Spec**: [http://localhost:8080/v3/api-docs](http://localhost:8080/v3/api-docs)

### Step 4: Run Tests
To execute all business rule unit tests:
```bash
mvn test
```

---

## 10. REST API Documentation & Swagger UI

Interactive testing is available via Swagger at `http://localhost:8080/swagger-ui.html`.

### Summary of REST Endpoints:

| Category | HTTP Method | Endpoint | Description |
|---|---|---|---|
| **User** | `POST` | `/api/users` | Register a new user |
| **User** | `GET` | `/api/users/{id}` | Get user details by ID |
| **User** | `GET` | `/api/users` | List all users |
| **Recipes** | `POST` | `/api/recipes` | Create a recipe with ingredients |
| **Recipes** | `GET` | `/api/recipes` | Retrieve all recipes |
| **Recipes** | `GET` | `/api/recipes/{id}` | Retrieve recipe by ID |
| **Recipes** | `PUT` | `/api/recipes/{id}` | Update recipe and ingredients |
| **Recipes** | `DELETE` | `/api/recipes/{id}` | Delete recipe and associated plans |
| **Search** | `GET` | `/api/recipes/search?keyword={query}` | Search recipes by title, cuisine, or ingredient |
| **Search** | `GET` | `/api/recipes/cuisine/{cuisine}` | Filter recipes by cuisine name |
| **Search** | `GET` | `/api/recipes/ingredient/{ingredient}` | Filter recipes containing ingredient |
| **Favourites** | `PUT` | `/api/recipes/{id}/favourite` | Toggle favourite status |
| **Favourites** | `GET` | `/api/recipes/favourites` | List all favourite recipes |
| **Meal Plan** | `POST` | `/api/meal-plans` | Schedule a recipe for a date and meal slot |
| **Meal Plan** | `GET` | `/api/meal-plans` | Retrieve all meal plan entries |
| **Meal Plan** | `GET` | `/api/meal-plans/week?startDate=YYYY-MM-DD` | Get 7-day meal plan starting from date |
| **Meal Plan** | `DELETE` | `/api/meal-plans/{id}` | Delete a meal plan entry |
| **Shopping List** | `GET` | `/api/shopping-list?startDate=YYYY-MM-DD&endDate=YYYY-MM-DD` | Generate consolidated shopping list |
| **Dashboard** | `GET` | `/api/dashboard` | Get summary counts and upcoming meal schedule |

---

## 11. Service Layer Business Rules & Validations

1. **Recipe Existence Validation in Meal Plan**:
   - A meal plan entry cannot be created using a recipe that does not exist.
   - Handled in `MealPlanService.createMealPlan()`:
     ```java
     Recipe recipe = recipeRepository.findById(dto.getRecipeId())
         .orElseThrow(() -> new ResourceNotFoundException("Cannot create meal plan. The selected recipe does not exist."));
     ```
   - Returns HTTP 404 with clean message: `"Cannot create meal plan. The selected recipe does not exist."`

2. **Ingredient Aggregation in Service Layer**:
   - `ShoppingListService.generateShoppingList()` examines all recipes planned in the chosen date range.
   - Ingredients with identical names and units are summed together into single consolidated rows.
   - Handled entirely in Java service layer memory without raw SQL duplicates.

3. **Field Validations**:
   - Recipe title cannot be empty (`@NotBlank`).
   - Ingredient name cannot be empty (`@NotBlank`).
   - Ingredient quantity must be greater than zero (`@Positive`).
   - Preparation time cannot be negative (`@PositiveOrZero`).
   - Cooking time cannot be negative (`@PositiveOrZero`).
   - Invalid recipe ID returns HTTP 404: `"Recipe not found with id: X"`.

4. **Global Exception Handling**:
   - `@RestControllerAdvice` translates errors into structured JSON:
     ```json
     {
       "status": 404,
       "message": "Recipe not found with id: 10",
       "timestamp": "2026-09-28T10:25:07",
       "validationErrors": null
     }
     ```

---

## 12. Default Demo Data

The application automatically initializes sample records upon first launch via `DataInitializer`:
- **Default User**:
  - Name: `Demo User`
  - Email: `demo@recipebox.com`
  - Password: `password123`
- **Preloaded Recipes**:
  1. *Vegetable Fried Rice* (Chinese)
  2. *Tomato Pasta* (Italian)
  3. *Chicken Biryani* (Indian)
  4. *Lemon Rice* (South Indian)
  5. *Vegetable Sandwich* (Continental)
- **Preloaded Weekly Meal Plans**:
  - Monday Breakfast: Vegetable Sandwich
  - Monday Lunch: Lemon Rice
  - Monday Dinner: Tomato Pasta
  - Tuesday Lunch: Chicken Biryani
  - Tuesday Dinner: Vegetable Fried Rice
  - Wednesday Dinner: Vegetable Fried Rice *(Demonstrates repeated ingredient aggregation)*
  - Thursday Breakfast: Vegetable Sandwich
  - Thursday Lunch: Tomato Pasta

---

## 13. User Interface Walkthrough

1. **Home (`index.html`)**:
   - Hero banner with quick links.
   - Highlight badges for key benefits.
   - Featured cards with preparation time, cuisine, and quick-actions.

2. **Recipes (`recipes.html`)**:
   - Search bar with live search by recipe name, cuisine, or ingredients.
   - Cuisine filter dropdown.
   - Quick **🔍 View** modal showing detailed ingredients and step-by-step instructions.
   - Direct **📅 Plan** button to schedule the recipe to your week.

3. **Add / Edit Recipe (`add-recipe.html`)**:
   - Dynamic ingredient rows: add or remove ingredient lines with custom quantities and units.
   - Cooking steps multiline textarea.
   - Edit mode supported via `?id={recipeId}` query parameter.

4. **Meal Planner (`meal-plan.html`)**:
   - 7-day columns (Monday to Sunday) with current week date badges.
   - Distinct sections for Breakfast, Lunch, Dinner, and Snacks.
   - Scheduled recipes display prep time with a single-click remove button (`🗑️`).
   - Week navigation buttons (`Previous Week`, `Today / This Week`, `Next Week`).

5. **Shopping List (`shopping-list.html`)**:
   - Consolidated grocery table with total aggregated quantities.
   - Interactive checkboxes allowing the user to mark items as bought while shopping.
   - Date range selector (`This Week`, `Next Week`, or custom dates).
   - Clean printable layout with **🖨️ Print Shopping List** button.

6. **Favourites (`favourites.html`)**:
   - Filtered view of recipes marked with `♥`.
   - Real-time unmark/mark synchronization with the database.

7. **Dashboard (`dashboard.html`)**:
   - Four real-time count metrics: Total Recipes, Favourites, Planned Meals, Ingredients Tracked.
   - Table of upcoming meals chronologically ordered with cuisine and prep times.

---

## 14. License & Academic Attribution
Developed as an academic Mini Project for university coursework demonstrating clean MVC architecture, RESTful API design, database normalization, and responsive frontend design without external UI libraries.
