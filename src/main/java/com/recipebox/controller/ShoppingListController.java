package com.recipebox.controller;

import com.recipebox.dto.ShoppingListItemDTO;
import com.recipebox.service.ShoppingListService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDate;
import java.util.List;

@RestController
@RequestMapping("/api/shopping-list")
@CrossOrigin(origins = "*")
@Tag(name = "Shopping List Generation", description = "Endpoints for generating consolidated shopping lists from meal plans")
public class ShoppingListController {

    private final ShoppingListService shoppingListService;

    @Autowired
    public ShoppingListController(ShoppingListService shoppingListService) {
        this.shoppingListService = shoppingListService;
    }

    @GetMapping
    @Operation(summary = "Get aggregated shopping list for planned meals within a date range")
    public ResponseEntity<List<ShoppingListItemDTO>> getShoppingList(
            @RequestParam(required = false)
            @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate startDate,
            @RequestParam(required = false)
            @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate endDate) {
        return ResponseEntity.ok(shoppingListService.generateShoppingList(startDate, endDate));
    }
}
