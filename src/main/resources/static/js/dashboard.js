/**
 * RecipeBox - Dashboard Script (dashboard.js)
 * Fetches summary statistics and renders upcoming planned meals
 */

document.addEventListener('DOMContentLoaded', () => {
    loadDashboardData();
});

async function loadDashboardData() {
    try {
        const stats = await apiRequest('/dashboard');

        // Update Counter Cards
        animateCounter('count-total-recipes', stats.totalRecipes || 0);
        animateCounter('count-fav-recipes', stats.favouriteRecipes || 0);
        animateCounter('count-planned-meals', stats.plannedMeals || 0);
        animateCounter('count-ingredients', stats.totalIngredients || 0);

        // Render Upcoming Meals Table
        renderUpcomingMeals(stats.upcomingMeals || []);
    } catch (err) {
        showToast('Failed to load dashboard data: ' + err.message, 'error');
    }
}

function renderUpcomingMeals(upcomingMeals) {
    const tableBody = document.getElementById('upcoming-meals-body');
    const emptyState = document.getElementById('upcoming-empty');
    if (!tableBody) return;

    if (!upcomingMeals || upcomingMeals.length === 0) {
        tableBody.innerHTML = '';
        if (emptyState) emptyState.style.display = 'block';
        return;
    }

    if (emptyState) emptyState.style.display = 'none';

    tableBody.innerHTML = upcomingMeals.map(meal => `
        <tr>
            <td style="font-weight: 600;">${formatDate(meal.mealDate)}</td>
            <td>
                <span class="badge badge-meal">${meal.mealType}</span>
            </td>
            <td style="font-weight: 700; color: var(--dark);">${escapeHtml(meal.recipeTitle)}</td>
            <td>
                <span class="badge badge-cuisine">${escapeHtml(meal.cuisine || 'General')}</span>
            </td>
            <td>⏱️ ${meal.prepTime || 15}m prep</td>
        </tr>
    `).join('');
}

function animateCounter(elementId, targetValue) {
    const el = document.getElementById(elementId);
    if (!el) return;

    let start = 0;
    const duration = 600;
    const stepTime = 20;
    const totalSteps = duration / stepTime;
    const increment = targetValue / totalSteps;

    if (targetValue === 0) {
        el.textContent = '0';
        return;
    }

    const timer = setInterval(() => {
        start += increment;
        if (start >= targetValue) {
            el.textContent = targetValue;
            clearInterval(timer);
        } else {
            el.textContent = Math.floor(start);
        }
    }, stepTime);
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
