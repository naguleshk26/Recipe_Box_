/**
 * RecipeBox - Recipes Script (recipes.js)
 * Handles recipe listing, search, filtering, view modal, delete, and favourite toggle
 */

let allRecipes = [];
const isFavouritesPage = window.location.pathname.includes('favourites.html');

document.addEventListener('DOMContentLoaded', () => {
    loadRecipes();
    setupSearchAndFilters();
    setupMealPlanModal();
});

// ==========================================
// Load Recipes
// ==========================================
async function loadRecipes() {
    const grid = document.getElementById('recipes-grid');
    if (!grid) return;

    grid.innerHTML = '<div style="grid-column: 1/-1; text-align: center; padding: 3rem;"><p>Loading delicious recipes...</p></div>';

    try {
        const endpoint = isFavouritesPage ? '/recipes/favourites' : '/recipes';
        allRecipes = await apiRequest(endpoint);
        renderRecipes(allRecipes);
        populateCuisineDropdown(allRecipes);
    } catch (err) {
        grid.innerHTML = `<div style="grid-column: 1/-1; text-align: center; padding: 3rem; color: var(--danger);"><p>Failed to load recipes: ${err.message}</p></div>`;
        showToast('Error loading recipes: ' + err.message, 'error');
    }
}

// ==========================================
// Render Recipe Cards
// ==========================================
function renderRecipes(recipes) {
    const grid = document.getElementById('recipes-grid');
    if (!grid) return;

    if (!recipes || recipes.length === 0) {
        grid.innerHTML = `
            <div style="grid-column: 1/-1; text-align: center; padding: 4rem 1rem;">
                <div style="font-size: 3rem; margin-bottom: 1rem;">🍳</div>
                <h3>No recipes found</h3>
                <p>${isFavouritesPage ? 'You haven’t added any favourites yet. Browse recipes and tap the heart icon!' : 'Try a different search term or add a new recipe!'}</p>
                <div style="margin-top: 1.5rem;">
                    <a href="${isFavouritesPage ? 'recipes.html' : 'add-recipe.html'}" class="btn btn-primary">
                        ${isFavouritesPage ? 'Browse All Recipes' : '+ Create Recipe'}
                    </a>
                </div>
            </div>
        `;
        return;
    }

    grid.innerHTML = recipes.map(recipe => `
        <div class="recipe-card" id="recipe-card-${recipe.id}">
            <div class="recipe-card-header">
                <div>
                    <span class="badge badge-cuisine">${escapeHtml(recipe.cuisine || 'General')}</span>
                    <h3 class="recipe-card-title" style="margin-top: 0.4rem;">${escapeHtml(recipe.title)}</h3>
                </div>
                <button class="fav-btn ${recipe.favourite ? 'active' : ''}" 
                        onclick="toggleFavourite(${recipe.id})" 
                        title="${recipe.favourite ? 'Remove from favourites' : 'Add to favourites'}">
                    ${recipe.favourite ? '♥' : '♡'}
                </button>
            </div>
            <div class="recipe-card-body">
                <p class="recipe-description">${escapeHtml(recipe.description || 'No description provided.')}</p>
                <div class="recipe-meta">
                    <span class="meta-item" title="Preparation Time">⏱️ <strong>Prep:</strong> ${recipe.prepTime}m</span>
                    <span class="meta-item" title="Cooking Time">🔥 <strong>Cook:</strong> ${recipe.cookingTime}m</span>
                    <span class="meta-item" title="Ingredients Count">🥗 <strong>${recipe.ingredients ? recipe.ingredients.length : 0}</strong> items</span>
                </div>
            </div>
            <div class="recipe-card-footer">
                <div style="display: flex; gap: 0.4rem;">
                    <button class="btn btn-secondary btn-sm" onclick="viewRecipeDetails(${recipe.id})">🔍 View</button>
                    <a href="add-recipe.html?id=${recipe.id}" class="btn btn-secondary btn-sm" title="Edit Recipe">✏️ Edit</a>
                    <button class="btn btn-danger btn-sm" onclick="deleteRecipe(${recipe.id})" title="Delete Recipe">🗑️</button>
                </div>
                <button class="btn btn-outline-primary btn-sm" onclick="openPlanModalForRecipe(${recipe.id}, '${escapeQuotes(recipe.title)}')">
                    📅 Plan
                </button>
            </div>
        </div>
    `).join('');
}

// ==========================================
// Search & Filter Operations
// ==========================================
function setupSearchAndFilters() {
    const searchInput = document.getElementById('search-input');
    const cuisineSelect = document.getElementById('cuisine-filter');

    if (searchInput) {
        let debounceTimeout;
        searchInput.addEventListener('input', (e) => {
            clearTimeout(debounceTimeout);
            debounceTimeout = setTimeout(() => {
                performSearch(e.target.value.trim());
            }, 300);
        });
    }

    if (cuisineSelect) {
        cuisineSelect.addEventListener('change', (e) => {
            const selectedCuisine = e.target.value;
            if (!selectedCuisine) {
                renderRecipes(allRecipes);
            } else {
                const filtered = allRecipes.filter(r => r.cuisine && r.cuisine.toLowerCase() === selectedCuisine.toLowerCase());
                renderRecipes(filtered);
            }
        });
    }
}

async function performSearch(query) {
    if (!query) {
        renderRecipes(allRecipes);
        return;
    }

    try {
        const results = await apiRequest(`/recipes/search?keyword=${encodeURIComponent(query)}`);
        // If on favourites page, filter by favourite
        const filtered = isFavouritesPage ? results.filter(r => r.favourite) : results;
        renderRecipes(filtered);
    } catch (err) {
        showToast('Search failed: ' + err.message, 'error');
    }
}

function populateCuisineDropdown(recipes) {
    const select = document.getElementById('cuisine-filter');
    if (!select) return;

    const cuisines = [...new Set(recipes.map(r => r.cuisine).filter(Boolean))].sort();
    select.innerHTML = '<option value="">All Cuisines</option>' +
        cuisines.map(c => `<option value="${escapeHtml(c)}">${escapeHtml(c)}</option>`).join('');
}

// ==========================================
// Toggle Favourite
// ==========================================
async function toggleFavourite(id) {
    try {
        const updated = await apiRequest(`/recipes/${id}/favourite`, { method: 'PUT' });
        showToast(updated.favourite ? `Added "${updated.title}" to favourites!` : `Removed "${updated.title}" from favourites!`, 'success');

        // Update local state
        const idx = allRecipes.findIndex(r => r.id === id);
        if (idx !== -1) {
            allRecipes[idx].favourite = updated.favourite;
        }

        if (isFavouritesPage && !updated.favourite) {
            // Remove from favourites page view
            allRecipes = allRecipes.filter(r => r.id !== id);
        }

        renderRecipes(allRecipes);
    } catch (err) {
        showToast('Failed to update favourite: ' + err.message, 'error');
    }
}

// ==========================================
// View Recipe Details Modal
// ==========================================
async function viewRecipeDetails(id) {
    try {
        const recipe = await apiRequest(`/recipes/${id}`);
        const modalBody = document.getElementById('recipe-modal-body');
        const modalTitle = document.getElementById('recipe-modal-title');

        if (!modalBody || !modalTitle) return;

        modalTitle.textContent = recipe.title;

        const ingredientItems = (recipe.ingredients || []).map(ing => `
            <li style="display: flex; justify-content: space-between; padding: 0.4rem 0; border-bottom: 1px dashed var(--border);">
                <span><strong>${escapeHtml(ing.ingredientName)}</strong></span>
                <span class="quantity-badge" style="font-size: 0.8rem;">${ing.quantity} ${escapeHtml(ing.unit)}</span>
            </li>
        `).join('');

        modalBody.innerHTML = `
            <div style="margin-bottom: 1.25rem;">
                <span class="badge badge-cuisine">${escapeHtml(recipe.cuisine)}</span>
                <span class="badge badge-primary">⏱️ Prep: ${recipe.prepTime} mins</span>
                <span class="badge badge-meal">🔥 Cook: ${recipe.cookingTime} mins</span>
            </div>
            
            <p style="margin-bottom: 1.5rem; color: var(--text-main); font-size: 0.95rem;">
                ${escapeHtml(recipe.description || 'No description.')}
            </p>

            <h4 style="margin-bottom: 0.75rem;">Ingredients (${recipe.ingredients ? recipe.ingredients.length : 0})</h4>
            <ul style="list-style: none; padding: 0; margin-bottom: 1.5rem; background: var(--bg-alt); padding: 1rem; border-radius: var(--radius-md);">
                ${ingredientItems || '<li style="color: var(--text-muted);">No ingredients listed.</li>'}
            </ul>

            <h4 style="margin-bottom: 0.75rem;">Preparation & Cooking Steps</h4>
            <div style="background: #fff8f1; padding: 1.25rem; border-radius: var(--radius-md); border-left: 4px solid var(--primary); white-space: pre-line; font-size: 0.95rem; line-height: 1.6;">
                ${escapeHtml(recipe.steps)}
            </div>
        `;

        openModal('recipe-view-modal');
    } catch (err) {
        showToast('Failed to load recipe details: ' + err.message, 'error');
    }
}

// ==========================================
// Delete Recipe
// ==========================================
async function deleteRecipe(id) {
    if (!confirm('Are you sure you want to delete this recipe? This will also remove any planned meals using it.')) {
        return;
    }

    try {
        await apiRequest(`/recipes/${id}`, { method: 'DELETE' });
        showToast('Recipe deleted successfully!', 'success');
        allRecipes = allRecipes.filter(r => r.id !== id);
        renderRecipes(allRecipes);
    } catch (err) {
        showToast('Failed to delete recipe: ' + err.message, 'error');
    }
}

// ==========================================
// Quick Plan Modal from Recipe Card
// ==========================================
function openPlanModalForRecipe(recipeId, recipeTitle) {
    const modal = document.getElementById('quick-plan-modal');
    if (!modal) return;

    document.getElementById('plan-recipe-id').value = recipeId;
    document.getElementById('plan-recipe-name-display').textContent = recipeTitle;

    // Set default date to today
    const today = new Date();
    document.getElementById('plan-date').value = toISODate(today);

    openModal('quick-plan-modal');
}

function setupMealPlanModal() {
    const form = document.getElementById('quick-plan-form');
    if (!form) return;

    form.addEventListener('submit', async (e) => {
        e.preventDefault();
        const recipeId = parseInt(document.getElementById('plan-recipe-id').value);
        const mealDate = document.getElementById('plan-date').value;
        const mealType = document.getElementById('plan-meal-type').value;

        try {
            await apiRequest('/meal-plans', {
                method: 'POST',
                body: JSON.stringify({ recipeId, mealDate, mealType })
            });
            showToast('Meal planned successfully!', 'success');
            closeModal('quick-plan-modal');
        } catch (err) {
            showToast(err.message, 'error');
        }
    });
}

function escapeHtml(str) {
    if (!str) return '';
    return str.replace(/[&<>"']/g, m => ({
        '&': '&amp;',
        '<': '&lt;',
        '>': '&gt;',
        '"': '&quot;',
        "'": '&#39;'
    })[m]);
}

function escapeQuotes(str) {
    if (!str) return '';
    return str.replace(/'/g, "\\'").replace(/"/g, '&quot;');
}
