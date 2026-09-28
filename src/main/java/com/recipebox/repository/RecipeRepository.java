package com.recipebox.repository;

import com.recipebox.entity.Recipe;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface RecipeRepository extends JpaRepository<Recipe, Long> {

    List<Recipe> findByFavouriteTrue();

    List<Recipe> findByCuisineIgnoreCase(String cuisine);

    long countByFavouriteTrue();

    @Query("SELECT DISTINCT r FROM Recipe r LEFT JOIN r.recipeIngredients ri LEFT JOIN ri.ingredient i " +
           "WHERE LOWER(r.title) LIKE LOWER(CONCAT('%', :keyword, '%')) " +
           "OR LOWER(r.cuisine) LIKE LOWER(CONCAT('%', :keyword, '%')) " +
           "OR LOWER(r.description) LIKE LOWER(CONCAT('%', :keyword, '%')) " +
           "OR LOWER(i.name) LIKE LOWER(CONCAT('%', :keyword, '%'))")
    List<Recipe> searchByKeyword(@Param("keyword") String keyword);

    @Query("SELECT DISTINCT r FROM Recipe r JOIN r.recipeIngredients ri JOIN ri.ingredient i " +
           "WHERE LOWER(i.name) LIKE LOWER(CONCAT('%', :ingredient, '%'))")
    List<Recipe> findByIngredientName(@Param("ingredient") String ingredient);
}
