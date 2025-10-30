// Intel Odds Schedule Manager - Modern 2025 UI

const API_BASE_URL = '/api';

// Global state
let state = {
    selectedDate: null,
    selectedSport: null,
    selectedLeague: null,
    selectedGroup: null,
    sports: [],
    leagues: [],
    groups: [],
    events: []
};

// Initialize app
document.addEventListener('DOMContentLoaded', () => {
    console.log('Intel Odds Schedule Manager initialized');
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
        loadGroups();
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
        loadGroups();
    }
    if (state.selectedGroup) {
        loadEvents(state.selectedGroup.id);
    }
}

// Load sports
function loadSports() {
    fetch(`${API_BASE_URL}/sports`)
        .then(response => {
            if (!response.ok) {
                throw new Error(`HTTP error! status: ${response.status}`);
            }
            return response.json();
        })
        .then(sports => {
            console.log('Loaded sports:', sports);
            state.sports = sports;
            displaySports(sports);
        })
        .catch(error => {
            console.error('Error loading sports:', error);
            showError('Failed to load sports');
            // Show error in dropdown
            const sportSelect = document.getElementById('sport-select');
            sportSelect.innerHTML = '<option value="">Error loading sports</option>';
        });
}

function displaySports(sports) {
    const sportSelect = document.getElementById('sport-select');

    if (!sports || sports.length === 0) {
        sportSelect.innerHTML = '<option value="">No sports available</option>';
        return;
    }

    // Filter only active sports (active = 1 or true)
    const activeSports = sports.filter(sport => sport.active === true || sport.active === 1);

    console.log('Active sports:', activeSports);

    if (activeSports.length === 0) {
        sportSelect.innerHTML = '<option value="">No active sports</option>';
        return;
    }

    sportSelect.innerHTML = '<option value="">Select Sport...</option>' +
        activeSports.map(sport => `
            <option value="${sport.id}">${sport.name}</option>
        `).join('');

    // Add change event listener
    sportSelect.onchange = function() {
        const sportId = parseInt(this.value);
        if (sportId) {
            selectSport(sportId);
        } else {
            // Clear selection
            state.selectedSport = null;
            state.selectedLeague = null;
            state.selectedGroup = null;
            document.getElementById('leagues-list').innerHTML = '<div class="empty-state"><p>Select a sport to view leagues</p></div>';
            document.getElementById('groups-container').innerHTML = '<div class="empty-state"><p>Select a league to view groups</p></div>';
            document.getElementById('events-container').innerHTML = '<div class="empty-state"><p>Select a group to view games</p></div>';
        }
    };
}

function selectSport(sportId) {
    const sport = state.sports.find(s => s.id === sportId);
    if (!sport) return;

    state.selectedSport = sport;
    state.selectedLeague = null;
    state.selectedGroup = null;

    // Update dropdown selection
    const sportSelect = document.getElementById('sport-select');
    sportSelect.value = sportId;

    loadLeagues(sportId);
    clearGroups();
    clearEvents();
}

// Load leagues
function loadLeagues(sportId) {
    fetch(`${API_BASE_URL}/leagues/sport/${sportId}`)
        .then(response => {
            if (!response.ok) {
                throw new Error(`HTTP error! status: ${response.status}`);
            }
            return response.json();
        })
        .then(leagues => {
            console.log('Loaded leagues for sport', sportId, ':', leagues);
            state.leagues = leagues;
            displayLeagues(leagues);
        })
        .catch(error => {
            console.error('Error loading leagues:', error);
            showError('Failed to load leagues');
            document.getElementById('leagues-list').innerHTML = '<div class="empty-state"><p>Error loading leagues</p></div>';
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
    state.selectedGroup = null;

    displayLeagues(state.leagues);
    loadGroups();
    clearEvents();
}

// Load groups
function loadGroups() {
    if (!state.selectedLeague) {
        clearGroups();
        return;
    }

    fetch(`${API_BASE_URL}/categories`)
        .then(response => response.json())
        .then(groups => {
            state.groups = groups;
            displayGroups(groups);
        })
        .catch(error => {
            console.error('Error loading groups:', error);
            showError('Failed to load groups');
        });
}

function displayGroups(groups) {
    const container = document.getElementById('groups-container');
    const countBadge = document.getElementById('group-count');

    countBadge.textContent = groups.length;

    if (groups.length === 0) {
        container.innerHTML = `
            <div class="empty-state">
                <svg class="empty-icon" viewBox="0 0 24 24" fill="none" stroke="currentColor">
                    <rect x="3" y="3" width="18" height="18" rx="2"/>
                    <line x1="9" y1="9" x2="15" y2="15"/>
                    <line x1="15" y1="9" x2="9" y2="15"/>
                </svg>
                <p>No groups found for this league</p>
            </div>
        `;
        return;
    }

    container.innerHTML = groups.map(group => `
        <div class="group-card ${state.selectedGroup && state.selectedGroup.id === group.id ? 'active' : ''}"
             onclick="selectGroup(${group.id})">
            <div class="group-header">
                <div class="group-title">${group.header || group.name || 'Unnamed Group'}</div>
                <div class="group-actions">
                    <button class="icon-btn-small" onclick="event.stopPropagation(); editGroup(${group.id})" title="Edit">
                        <svg viewBox="0 0 24 24" fill="none" stroke="currentColor">
                            <path d="M11 4H4a2 2 0 0 0-2 2v14a2 2 0 0 0 2 2h14a2 2 0 0 0 2-2v-7"/>
                            <path d="M18.5 2.5a2.121 2.121 0 0 1 3 3L12 15l-4 1 1-4 9.5-9.5z"/>
                        </svg>
                    </button>
                    <button class="icon-btn-small" onclick="event.stopPropagation(); deleteGroup(${group.id})" title="Delete">
                        <svg viewBox="0 0 24 24" fill="none" stroke="currentColor">
                            <polyline points="3 6 5 6 21 6"/>
                            <path d="M19 6v14a2 2 0 0 1-2 2H7a2 2 0 0 1-2-2V6m3 0V4a2 2 0 0 1 2-2h4a2 2 0 0 1 2 2v2"/>
                        </svg>
                    </button>
                </div>
            </div>
            <div class="group-meta">
                <div class="group-meta-item">
                    <svg viewBox="0 0 24 24" fill="none" stroke="currentColor">
                        <rect x="3" y="4" width="18" height="18" rx="2" ry="2"/>
                        <line x1="16" y1="2" x2="16" y2="6"/>
                        <line x1="8" y1="2" x2="8" y2="6"/>
                        <line x1="3" y1="10" x2="21" y2="10"/>
                    </svg>
                    <span>${group.date ? new Date(group.date).toLocaleDateString() : 'No date'}</span>
                </div>
                <div class="group-meta-item">
                    <svg viewBox="0 0 24 24" fill="none" stroke="currentColor">
                        <circle cx="12" cy="12" r="10"/>
                        <polyline points="12 6 12 12 16 14"/>
                    </svg>
                    <span>ID: ${group.id}</span>
                </div>
            </div>
        </div>
    `).join('');
}

function selectGroup(groupId) {
    const group = state.groups.find(c => c.id === groupId);
    if (!group) return;

    state.selectedGroup = group;
    displayGroups(state.groups);
    loadEvents(groupId);
}

function clearGroups() {
    const container = document.getElementById('groups-container');
    const countBadge = document.getElementById('group-count');

    countBadge.textContent = '0';
    container.innerHTML = `
        <div class="empty-state">
            <svg class="empty-icon" viewBox="0 0 24 24" fill="none" stroke="currentColor">
                <rect x="3" y="3" width="18" height="18" rx="2"/>
            </svg>
            <p>Select a league to view groups</p>
        </div>
    `;
}




// Load events
function loadEvents(groupId) {
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
                <p>No games found for this group</p>
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
            <p>Select a group to view games</p>
        </div>
    `;
}

// CRUD Operations - Groups
function addGroup() {
    if (!state.selectedLeague) {
        showError('Please select a league first');
        return;
    }

    const header = prompt('Enter group header (e.g., "NFL - Thursday October 22nd"):');
    if (!header) return;

    const group = {
        header: header,
        date: state.selectedDate,
        league: { id: state.selectedLeague.id },
        active: true
    };

    fetch(`${API_BASE_URL}/categories`, {
        method: 'POST',
        headers: { 'Content-Type': 'application/json' },
        body: JSON.stringify(group)
    })
    .then(response => response.json())
    .then(() => {
        loadGroups();
        showSuccess('Group added successfully');
    })
    .catch(error => {
        console.error('Error adding group:', error);
        showError('Failed to add group');
    });
}

function editGroup(groupId) {
    const group = state.groups.find(c => c.id === groupId);
    if (!group) return;

    const newHeader = prompt('Edit group header:', group.header);
    if (!newHeader || newHeader === group.header) return;

    group.header = newHeader;

    fetch(`${API_BASE_URL}/categories/${groupId}`, {
        method: 'PUT',
        headers: { 'Content-Type': 'application/json' },
        body: JSON.stringify(group)
    })
    .then(response => response.json())
    .then(() => {
        loadGroups();
        showSuccess('Group updated successfully');
    })
    .catch(error => {
        console.error('Error updating group:', error);
        showError('Failed to update group');
    });
}

function deleteGroup(groupId) {
    if (!confirm('Are you sure you want to delete this group?')) return;

    fetch(`${API_BASE_URL}/categories/${groupId}`, {
        method: 'DELETE'
    })
    .then(() => {
        loadGroups();
        showSuccess('Group deleted successfully');
    })
    .catch(error => {
        console.error('Error deleting group:', error);
        showError('Failed to delete group');
    });
}

// CRUD Operations - Events
function addEvent() {
    if (!state.selectedGroup) {
        showError('Please select a group first');
        return;
    }

    const time = prompt('Enter game time (e.g., "7:00 PM"):');
    if (!time) return;

    const event = {
        time: time,
        category: { id: state.selectedGroup.id },
        active: true
    };

    fetch(`${API_BASE_URL}/events`, {
        method: 'POST',
        headers: { 'Content-Type': 'application/json' },
        body: JSON.stringify(event)
    })
    .then(response => response.json())
    .then(() => {
        loadEvents(state.selectedGroup.id);
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
