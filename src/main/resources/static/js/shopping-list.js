/**
 * RecipeBox - Shopping List Script (shopping-list.js)
 * Manages consolidated ingredient aggregation, week date filters, checkboxes, and printing
 */

let selectedStartDate = getMondayOfCurrentWeek();

document.addEventListener('DOMContentLoaded', () => {
    initDateControls();
    loadShoppingList();
    setupPrintButton();
});

// ==========================================
// Date Selector Controls
// ==========================================
function initDateControls() {
    const startInput = document.getElementById('start-date');
    const endInput = document.getElementById('end-date');
    const btnThisWeek = document.getElementById('btn-this-week');
    const btnNextWeek = document.getElementById('btn-next-week');
    const btnApply = document.getElementById('btn-apply-dates');

    setInputsForWeek(selectedStartDate);

    if (btnThisWeek) {
        btnThisWeek.addEventListener('click', () => {
            selectedStartDate = getMondayOfCurrentWeek();
            setInputsForWeek(selectedStartDate);
            loadShoppingList();
        });
    }

    if (btnNextWeek) {
        btnNextWeek.addEventListener('click', () => {
            const nextMon = getMondayOfCurrentWeek();
            nextMon.setDate(nextMon.getDate() + 7);
            selectedStartDate = nextMon;
            setInputsForWeek(selectedStartDate);
            loadShoppingList();
        });
    }

    if (btnApply) {
        btnApply.addEventListener('click', () => {
            loadShoppingList();
        });
    }
}

function setInputsForWeek(monday) {
    const startInput = document.getElementById('start-date');
    const endInput = document.getElementById('end-date');

    const sunday = new Date(monday);
    sunday.setDate(monday.getDate() + 6);

    if (startInput) startInput.value = toISODate(monday);
    if (endInput) endInput.value = toISODate(sunday);
}

// ==========================================
// Fetch & Render Consolidated Shopping List
// ==========================================
async function loadShoppingList() {
    const tableBody = document.getElementById('shopping-list-body');
    const emptyState = document.getElementById('shopping-empty');
    const subtitle = document.getElementById('shopping-date-subtitle');
    const totalItemsBadge = document.getElementById('total-items-count');

    if (!tableBody) return;

    const startDate = document.getElementById('start-date').value;
    const endDate = document.getElementById('end-date').value;

    if (subtitle) {
        subtitle.textContent = `Consolidated for ${formatDate(startDate)} – ${formatDate(endDate)}`;
    }

    tableBody.innerHTML = '<tr><td colspan="3" style="text-align: center; padding: 2rem;">Consolidating your weekly ingredients...</td></tr>';
    if (emptyState) emptyState.style.display = 'none';

    try {
        const items = await apiRequest(`/shopping-list?startDate=${startDate}&endDate=${endDate}`);

        if (totalItemsBadge) {
            totalItemsBadge.textContent = `${items.length} items to buy`;
        }

        if (!items || items.length === 0) {
            tableBody.innerHTML = '';
            if (emptyState) emptyState.style.display = 'block';
            return;
        }

        tableBody.innerHTML = items.map((item, index) => `
            <tr>
                <td style="width: 50px; text-align: center;">
                    <input type="checkbox" class="item-checkbox" id="item-chk-${index}" onchange="toggleItemCheck(this, 'item-name-${index}')">
                </td>
                <td>
                    <label for="item-chk-${index}" id="item-name-${index}" style="cursor: pointer; font-weight: 600;">
                        ${escapeHtml(item.ingredientName)}
                    </label>
                </td>
                <td style="text-align: right;">
                    <span class="quantity-badge">
                        ${item.totalQuantity} ${escapeHtml(item.unit)}
                    </span>
                </td>
            </tr>
        `).join('');

    } catch (err) {
        tableBody.innerHTML = `<tr><td colspan="3" style="text-align: center; color: var(--danger); padding: 2rem;">Error: ${err.message}</td></tr>`;
        showToast('Failed to load shopping list: ' + err.message, 'error');
    }
}

function toggleItemCheck(checkbox, labelId) {
    const label = document.getElementById(labelId);
    if (!label) return;

    if (checkbox.checked) {
        label.classList.add('item-checked');
    } else {
        label.classList.remove('item-checked');
    }
}

// ==========================================
// Print Shopping List
// ==========================================
function setupPrintButton() {
    const printBtn = document.getElementById('btn-print-list');
    if (printBtn) {
        printBtn.addEventListener('click', () => {
            window.print();
        });
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
