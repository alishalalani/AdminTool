// OddsLogic Schedule Manager - Modern 2025 UI

const API_BASE_URL = '/api';

// Global state
let state = {
    selectedDate: null,
    selectedSport: null,
    selectedLeague: null,
    selectedCategory: null,
    sports: [],
    leagues: [],
    categories: [],
    events: []
};

// Initialize app
document.addEventListener('DOMContentLoaded', () => {
    console.log('OddsLogic Schedule Manager initialized');
    initializeDatePicker();
    loadSports();
    checkConnection();
    setInterval(checkConnection, 30000);
});

// Date picker initialization
function initializeDatePicker() {
    const dateInput = document.getElementById('schedule-date');
    const today = new Date().toISOString().split('T')[0];
    dateInput.value = today;
    state.selectedDate = today;

    dateInput.addEventListener('change', (e) => {
        state.selectedDate = e.target.value;
        loadCategories();
    });
}

// Connection status check
function checkConnection() {
    fetch(`${API_BASE_URL}/sports`)
        .then(response => {
            updateConnectionStatus(response.ok);
        })
        .catch(() => {
            updateConnectionStatus(false);
        });
}

function updateConnectionStatus(connected) {
    const statusBadge = document.getElementById('connection-status');
    if (connected) {
        statusBadge.className = 'status-badge connected';
        statusBadge.innerHTML = '<span class="status-dot"></span><span>Connected</span>';
    } else {
        statusBadge.className = 'status-badge disconnected';
        statusBadge.innerHTML = '<span class="status-dot"></span><span>Disconnected</span>';
    }
}

// Refresh all data
function refreshAll() {
    loadSports();
    if (state.selectedSport) {
        loadLeagues(state.selectedSport.id);
    }
    if (state.selectedLeague) {
        loadCategories();
    }
    if (state.selectedCategory) {
        loadEvents(state.selectedCategory.id);
    }
}

// Load sports
function loadSports() {
    fetch(`${API_BASE_URL}/sports`)
        .then(response => response.json())
        .then(sports => {
            state.sports = sports;
            displaySports(sports);
        })
        .catch(error => {
            console.error('Error loading sports:', error);
            showError('Failed to load sports');
        });
}

function displaySports(sports) {
    const sportsList = document.getElementById('sports-list');

    if (sports.length === 0) {
        sportsList.innerHTML = '<div class="empty-state"><p>No sports found</p></div>';
        return;
    }

    sportsList.innerHTML = sports.map(sport => `
        <div class="sport-item ${state.selectedSport && state.selectedSport.id === sport.id ? 'active' : ''}"
             onclick="selectSport(${sport.id})">
            <span>${sport.name}</span>
            <span class="item-badge">${sport.abbreviation || ''}</span>
        </div>
    `).join('');
}

function selectSport(sportId) {
    const sport = state.sports.find(s => s.id === sportId);
    if (!sport) return;

    state.selectedSport = sport;
    state.selectedLeague = null;
    state.selectedCategory = null;

    displaySports(state.sports);
    loadLeagues(sportId);
    clearCategories();
    clearEvents();
}

// Load leagues
function loadLeagues(sportId) {
    fetch(`${API_BASE_URL}/leagues`)
        .then(response => response.json())
        .then(leagues => {
            const filteredLeagues = leagues.filter(l => l.sport && l.sport.id === sportId);
            state.leagues = filteredLeagues;
            displayLeagues(filteredLeagues);
        })
        .catch(error => {
            console.error('Error loading leagues:', error);
            showError('Failed to load leagues');
        });
}

function displayLeagues(leagues) {
    const leaguesList = document.getElementById('leagues-list');

    if (leagues.length === 0) {
        leaguesList.innerHTML = '<div class="empty-state"><p>No leagues found</p></div>';
        return;
    }

    leaguesList.innerHTML = leagues.map(league => `
        <div class="league-item ${state.selectedLeague && state.selectedLeague.id === league.id ? 'active' : ''}"
             onclick="selectLeague(${league.id})">
            <span>${league.name}</span>
        </div>
    `).join('');
}

function selectLeague(leagueId) {
    const league = state.leagues.find(l => l.id === leagueId);
    if (!league) return;

    state.selectedLeague = league;
    state.selectedCategory = null;

    displayLeagues(state.leagues);
    loadCategories();
    clearEvents();
}

// Load categories
function loadCategories() {
    if (!state.selectedLeague) {
        clearCategories();
        return;
    }

    fetch(`${API_BASE_URL}/categories`)
        .then(response => response.json())
        .then(categories => {
            state.categories = categories;
            displayCategories(categories);
        })
        .catch(error => {
            console.error('Error loading categories:', error);
            showError('Failed to load categories');
        });
}

function displayCategories(categories) {
    const container = document.getElementById('categories-container');
    const countBadge = document.getElementById('category-count');

    countBadge.textContent = categories.length;

    if (categories.length === 0) {
        container.innerHTML = `
            <div class="empty-state">
                <svg class="empty-icon" viewBox="0 0 24 24" fill="none" stroke="currentColor">
                    <rect x="3" y="3" width="18" height="18" rx="2"/>
                    <line x1="9" y1="9" x2="15" y2="15"/>
                    <line x1="15" y1="9" x2="9" y2="15"/>
                </svg>
                <p>No categories found for this league</p>
            </div>
        `;
        return;
    }

    container.innerHTML = categories.map(category => `
        <div class="category-card ${state.selectedCategory && state.selectedCategory.id === category.id ? 'active' : ''}"
             onclick="selectCategory(${category.id})">
            <div class="category-header">
                <div class="category-title">${category.header || category.name || 'Unnamed Category'}</div>
                <div class="category-actions">
                    <button class="icon-btn-small" onclick="event.stopPropagation(); editCategory(${category.id})" title="Edit">
                        <svg viewBox="0 0 24 24" fill="none" stroke="currentColor">
                            <path d="M11 4H4a2 2 0 0 0-2 2v14a2 2 0 0 0 2 2h14a2 2 0 0 0 2-2v-7"/>
                            <path d="M18.5 2.5a2.121 2.121 0 0 1 3 3L12 15l-4 1 1-4 9.5-9.5z"/>
                        </svg>
                    </button>
                    <button class="icon-btn-small" onclick="event.stopPropagation(); deleteCategory(${category.id})" title="Delete">
                        <svg viewBox="0 0 24 24" fill="none" stroke="currentColor">
                            <polyline points="3 6 5 6 21 6"/>
                            <path d="M19 6v14a2 2 0 0 1-2 2H7a2 2 0 0 1-2-2V6m3 0V4a2 2 0 0 1 2-2h4a2 2 0 0 1 2 2v2"/>
                        </svg>
                    </button>
                </div>
            </div>
            <div class="category-meta">
                <div class="category-meta-item">
                    <svg viewBox="0 0 24 24" fill="none" stroke="currentColor">
                        <rect x="3" y="4" width="18" height="18" rx="2" ry="2"/>
                        <line x1="16" y1="2" x2="16" y2="6"/>
                        <line x1="8" y1="2" x2="8" y2="6"/>
                        <line x1="3" y1="10" x2="21" y2="10"/>
                    </svg>
                    <span>${category.date ? new Date(category.date).toLocaleDateString() : 'No date'}</span>
                </div>
                <div class="category-meta-item">
                    <svg viewBox="0 0 24 24" fill="none" stroke="currentColor">
                        <circle cx="12" cy="12" r="10"/>
                        <polyline points="12 6 12 12 16 14"/>
                    </svg>
                    <span>ID: ${category.id}</span>
                </div>
            </div>
        </div>
    `).join('');
}

function selectCategory(categoryId) {
    const category = state.categories.find(c => c.id === categoryId);
    if (!category) return;

    state.selectedCategory = category;
    displayCategories(state.categories);
    loadEvents(categoryId);
}

function clearCategories() {
    const container = document.getElementById('categories-container');
    const countBadge = document.getElementById('category-count');

    countBadge.textContent = '0';
    container.innerHTML = `
        <div class="empty-state">
            <svg class="empty-icon" viewBox="0 0 24 24" fill="none" stroke="currentColor">
                <rect x="3" y="3" width="18" height="18" rx="2"/>
            </svg>
            <p>Select a league to view categories</p>
        </div>
    `;
}




// Load events
function loadEvents(categoryId) {
    fetch(`${API_BASE_URL}/events`)
        .then(response => response.json())
        .then(events => {
            state.events = events;
            displayEvents(events);
        })
        .catch(error => {
            console.error('Error loading events:', error);
            showError('Failed to load events');
        });
}

function displayEvents(events) {
    const container = document.getElementById('events-container');
    const countBadge = document.getElementById('event-count');

    countBadge.textContent = events.length;

    if (events.length === 0) {
        container.innerHTML = `
            <div class="empty-state">
                <svg class="empty-icon" viewBox="0 0 24 24" fill="none" stroke="currentColor">
                    <rect x="3" y="3" width="18" height="18" rx="2"/>
                    <line x1="9" y1="9" x2="15" y2="15"/>
                    <line x1="15" y1="9" x2="9" y2="15"/>
                </svg>
                <p>No games found for this category</p>
            </div>
        `;
        return;
    }

    container.innerHTML = events.map(event => `
        <div class="event-card">
            <div class="event-header">
                <div class="event-time">${event.time || 'TBD'}</div>
                <div class="event-number">#${event.number || event.id}</div>
            </div>
            <div class="event-teams">
                <div class="team-row">
                    <div class="team-name">Team 1</div>
                    <div class="team-score">-</div>
                </div>
                <div class="team-row">
                    <div class="team-name">Team 2</div>
                    <div class="team-score">-</div>
                </div>
            </div>
        </div>
    `).join('');
}

function clearEvents() {
    const container = document.getElementById('events-container');
    const countBadge = document.getElementById('event-count');

    countBadge.textContent = '0';
    container.innerHTML = `
        <div class="empty-state">
            <svg class="empty-icon" viewBox="0 0 24 24" fill="none" stroke="currentColor">
                <rect x="3" y="3" width="18" height="18" rx="2"/>
            </svg>
            <p>Select a category to view games</p>
        </div>
    `;
}

// CRUD Operations - Categories
function addCategory() {
    if (!state.selectedLeague) {
        showError('Please select a league first');
        return;
    }

    const header = prompt('Enter category header (e.g., "NFL - Thursday October 22nd"):');
    if (!header) return;

    const category = {
        header: header,
        date: state.selectedDate,
        league: { id: state.selectedLeague.id },
        active: true
    };

    fetch(`${API_BASE_URL}/categories`, {
        method: 'POST',
        headers: { 'Content-Type': 'application/json' },
        body: JSON.stringify(category)
    })
    .then(response => response.json())
    .then(() => {
        loadCategories();
        showSuccess('Category added successfully');
    })
    .catch(error => {
        console.error('Error adding category:', error);
        showError('Failed to add category');
    });
}

function editCategory(categoryId) {
    const category = state.categories.find(c => c.id === categoryId);
    if (!category) return;

    const newHeader = prompt('Edit category header:', category.header);
    if (!newHeader || newHeader === category.header) return;

    category.header = newHeader;

    fetch(`${API_BASE_URL}/categories/${categoryId}`, {
        method: 'PUT',
        headers: { 'Content-Type': 'application/json' },
        body: JSON.stringify(category)
    })
    .then(response => response.json())
    .then(() => {
        loadCategories();
        showSuccess('Category updated successfully');
    })
    .catch(error => {
        console.error('Error updating category:', error);
        showError('Failed to update category');
    });
}

function deleteCategory(categoryId) {
    if (!confirm('Are you sure you want to delete this category?')) return;

    fetch(`${API_BASE_URL}/categories/${categoryId}`, {
        method: 'DELETE'
    })
    .then(() => {
        loadCategories();
        showSuccess('Category deleted successfully');
    })
    .catch(error => {
        console.error('Error deleting category:', error);
        showError('Failed to delete category');
    });
}

// CRUD Operations - Events
function addEvent() {
    if (!state.selectedCategory) {
        showError('Please select a category first');
        return;
    }

    const time = prompt('Enter game time (e.g., "7:00 PM"):');
    if (!time) return;

    const event = {
        time: time,
        category: { id: state.selectedCategory.id },
        active: true
    };

    fetch(`${API_BASE_URL}/events`, {
        method: 'POST',
        headers: { 'Content-Type': 'application/json' },
        body: JSON.stringify(event)
    })
    .then(response => response.json())
    .then(() => {
        loadEvents(state.selectedCategory.id);
        showSuccess('Game added successfully');
    })
    .catch(error => {
        console.error('Error adding event:', error);
        showError('Failed to add game');
    });
}

// Utility functions
function toggleAllLeagues() {
    if (state.selectedSport) {
        loadLeagues(state.selectedSport.id);
    }
}

function showError(message) {
    alert('Error: ' + message);
}

function showSuccess(message) {
    console.log('Success: ' + message);
}
