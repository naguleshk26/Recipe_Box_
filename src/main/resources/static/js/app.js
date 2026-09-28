/**
 * RecipeBox - Global Application Script (app.js)
 * Common utilities, API wrappers, Toast Notifications, and Navigation
 */

const API_BASE = '/api';

// ==========================================
// Toast Notification System
// ==========================================
function showToast(message, type = 'info') {
    let container = document.getElementById('toast-container');
    if (!container) {
        container = document.createElement('div');
        container.id = 'toast-container';
        document.body.appendChild(container);
    }

    const toast = document.createElement('div');
    toast.className = `toast toast-${type}`;

    let icon = 'ℹ️';
    if (type === 'success') icon = '✅';
    if (type === 'error') icon = '⚠️';

    toast.innerHTML = `<span>${icon}</span><span>${message}</span>`;
    container.appendChild(toast);

    setTimeout(() => {
        toast.style.opacity = '0';
        toast.style.transform = 'translateY(10px)';
        setTimeout(() => toast.remove(), 300);
    }, 4000);
}

// ==========================================
// Centralized API Client (Fetch API wrapper)
// ==========================================
async function apiRequest(endpoint, options = {}) {
    const url = endpoint.startsWith('http') ? endpoint : `${API_BASE}${endpoint}`;
    const defaultHeaders = {
        'Content-Type': 'application/json',
        'Accept': 'application/json'
    };

    options.headers = {
        ...defaultHeaders,
        ...options.headers
    };

    try {
        const response = await fetch(url, options);

        // Check if empty response (e.g. 204 No Content)
        if (response.status === 204) {
            return null;
        }

        const data = await response.json().catch(() => null);

        if (!response.ok) {
            const errorMsg = (data && data.message) ? data.message : `Error ${response.status}: Request failed`;
            throw new Error(errorMsg);
        }

        return data;
    } catch (err) {
        console.error('API Error:', err);
        throw err;
    }
}

// ==========================================
// Modal Helpers
// ==========================================
function openModal(modalId) {
    const modal = document.getElementById(modalId);
    if (modal) {
        modal.classList.add('active');
        document.body.style.overflow = 'hidden';
    }
}

function closeModal(modalId) {
    const modal = document.getElementById(modalId);
    if (modal) {
        modal.classList.remove('active');
        document.body.style.overflow = 'auto';
    }
}

// ==========================================
// Date Formatting Helpers
// ==========================================
function formatDate(dateString) {
    if (!dateString) return '';
    const date = new Date(dateString);
    return date.toLocaleDateString('en-US', {
        weekday: 'short',
        month: 'short',
        day: 'numeric'
    });
}

function getMondayOfCurrentWeek(d = new Date()) {
    const date = new Date(d);
    const day = date.getDay();
    const diff = date.getDate() - day + (day === 0 ? -6 : 1); // adjust when day is sunday
    return new Date(date.setDate(diff));
}

function toISODate(d) {
    const year = d.getFullYear();
    const month = String(d.getMonth() + 1).padStart(2, '0');
    const day = String(d.getDate()).padStart(2, '0');
    return `${year}-${month}-${day}`;
}

// ==========================================
// Mobile Nav & Global Event Listeners
// ==========================================
document.addEventListener('DOMContentLoaded', () => {
    // Mobile navigation toggle
    const menuBtn = document.querySelector('.mobile-menu-btn');
    const navLinks = document.querySelector('.nav-links');
    if (menuBtn && navLinks) {
        menuBtn.addEventListener('click', () => {
            navLinks.classList.toggle('show');
        });
    }

    // Modal background click to close
    document.querySelectorAll('.modal-overlay').forEach(overlay => {
        overlay.addEventListener('click', (e) => {
            if (e.target === overlay) {
                overlay.classList.remove('active');
                document.body.style.overflow = 'auto';
            }
        });
    });
});
