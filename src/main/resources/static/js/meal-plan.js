/**
 * RecipeBox - Weekly Meal Planner Script (meal-plan.js)
 * Manages 7-day calendar, meal slot assignments, recipe selection, and deletions
 */

let currentWeekMonday = getMondayOfCurrentWeek();
let availableRecipes = [];
const MEAL_TYPES = ['BREAKFAST', 'LUNCH', 'DINNER', 'SNACK'];

document.addEventListener('DOMContentLoaded', async () => {
    await loadRecipesDropdown();
    await loadWeekMealPlans(currentWeekMonday);
    setupWeekNavigation();
    setupAddMealModal();
});

// ==========================================
// Load Available Recipes for Dropdown
// ==========================================
async function loadRecipesDropdown() {
    try {
        availableRecipes = await apiRequest('/recipes');
        const select = document.getElementById('modal-recipe-select');
        if (select) {
            select.innerHTML = '<option value="">-- Choose a Recipe --</option>' +
                availableRecipes.map(r => `<option value="${r.id}">${escapeHtml(r.title)} (${escapeHtml(r.cuisine)})</option>`).join('');
        }
    } catch (err) {
        showToast('Could not load recipe list: ' + err.message, 'error');
    }
}

// ==========================================
// Load & Render Weekly Meal Plans
// ==========================================
async function loadWeekMealPlans(mondayDate) {
    const weekTitle = document.getElementById('week-range-title');
    const container = document.getElementById('week-columns-container');
    if (!container) return;

    const startDateStr = toISODate(mondayDate);
    const sundayDate = new Date(mondayDate);
    sundayDate.setDate(mondayDate.getDate() + 6);
    const endDateStr = toISODate(sundayDate);

    if (weekTitle) {
        weekTitle.textContent = `${formatDate(startDateStr)} – ${formatDate(endDateStr)}`;
    }

    container.innerHTML = '<div style="grid-column: 1/-1; text-align: center; padding: 2rem;"><p>Loading weekly schedule...</p></div>';

    try {
        const mealPlans = await apiRequest(`/meal-plans/week?startDate=${startDateStr}`);
        renderWeekCalendar(mondayDate, mealPlans);
    } catch (err) {
        container.innerHTML = `<div style="grid-column: 1/-1; text-align: center; color: var(--danger);"><p>Failed to load meal plans: ${err.message}</p></div>`;
        showToast('Failed to load meal plans: ' + err.message, 'error');
    }
}

function renderWeekCalendar(mondayDate, mealPlans) {
    const container = document.getElementById('week-columns-container');
    if (!container) return;

    const todayStr = toISODate(new Date());
    let html = '';

    for (let i = 0; i < 7; i++) {
        const dayDate = new Date(mondayDate);
        dayDate.setDate(mondayDate.getDate() + i);
        const dayIso = toISODate(dayDate);
        const isToday = dayIso === todayStr;

        const dayName = dayDate.toLocaleDateString('en-US', { weekday: 'short' });
        const dayDisplay = dayDate.toLocaleDateString('en-US', { month: 'short', day: 'numeric' });

        // Filter plans for this day
        const dayPlans = mealPlans.filter(p => p.mealDate === dayIso);

        html += `
            <div class="day-column ${isToday ? 'today' : ''}">
                <div class="day-header">
                    <div class="day-name">${dayName}</div>
                    <div class="day-date">${dayDisplay}</div>
                </div>

                ${MEAL_TYPES.map(type => {
                    const planForType = dayPlans.find(p => p.mealType === type);
                    return `
                        <div class="meal-slot">
                            <div class="slot-label">${type}</div>
                            ${planForType ? `
                                <div class="planned-recipe-chip">
                                    <div class="chip-title">${escapeHtml(planForType.recipeTitle)}</div>
                                    <div class="chip-meta">
                                        <span>⏱️ ${planForType.prepTime || 15}m</span>
                                        <button class="btn-delete-meal" onclick="deleteMealPlanEntry(${planForType.id})" title="Remove meal">🗑️</button>
                                    </div>
                                </div>
                            ` : `
                                <button class="btn-add-meal-slot" onclick="openAddMealModal('${dayIso}', '${type}')">
                                    + Add
                                </button>
                            `}
                        </div>
                    `;
                }).join('')}
            </div>
        `;
    }

    container.innerHTML = html;
}

// ==========================================
// Week Navigation
// ==========================================
function setupWeekNavigation() {
    const prevBtn = document.getElementById('btn-prev-week');
    const nextBtn = document.getElementById('btn-next-week');
    const currentBtn = document.getElementById('btn-current-week');

    if (prevBtn) {
        prevBtn.addEventListener('click', () => {
            currentWeekMonday.setDate(currentWeekMonday.getDate() - 7);
            loadWeekMealPlans(currentWeekMonday);
        });
    }

    if (nextBtn) {
        nextBtn.addEventListener('click', () => {
            currentWeekMonday.setDate(currentWeekMonday.getDate() + 7);
            loadWeekMealPlans(currentWeekMonday);
        });
    }

    if (currentBtn) {
        currentBtn.addEventListener('click', () => {
            currentWeekMonday = getMondayOfCurrentWeek();
            loadWeekMealPlans(currentWeekMonday);
        });
    }
}

// ==========================================
// Modal & Meal Planning Action
// ==========================================
function openAddMealModal(dateStr, mealType) {
    document.getElementById('modal-meal-date').value = dateStr;
    document.getElementById('modal-meal-type').value = mealType;
    document.getElementById('modal-recipe-select').value = '';
    openModal('add-meal-modal');
}

function setupAddMealModal() {
    const form = document.getElementById('add-meal-form');
    if (!form) return;

    form.addEventListener('submit', async (e) => {
        e.preventDefault();

        const recipeId = parseInt(document.getElementById('modal-recipe-select').value);
        const mealDate = document.getElementById('modal-meal-date').value;
        const mealType = document.getElementById('modal-meal-type').value;

        if (!recipeId) {
            showToast('Please select a recipe!', 'error');
            return;
        }

        try {
            await apiRequest('/meal-plans', {
                method: 'POST',
                body: JSON.stringify({ recipeId, mealDate, mealType })
            });

            showToast('Meal successfully added to schedule!', 'success');
            closeModal('add-meal-modal');
            await loadWeekMealPlans(currentWeekMonday);
        } catch (err) {
            showToast(err.message, 'error');
        }
    });
}

// ==========================================
// Delete Meal Plan
// ==========================================
async function deleteMealPlanEntry(id) {
    if (!confirm('Remove this planned meal from schedule?')) return;

    try {
        await apiRequest(`/meal-plans/${id}`, { method: 'DELETE' });
        showToast('Meal plan removed!', 'success');
        await loadWeekMealPlans(currentWeekMonday);
    } catch (err) {
        showToast('Failed to remove meal: ' + err.message, 'error');
    }
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
