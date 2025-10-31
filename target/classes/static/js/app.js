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

// Helper function to format date without timezone issues
function formatDateLocal(dateString) {
    if (!dateString) return 'No date';
    // Parse the date as local time (YYYY-MM-DD)
    const [year, month, day] = dateString.split('-').map(Number);
    const date = new Date(year, month - 1, day); // month is 0-indexed
    return date.toLocaleDateString();
}

// Initialize app
document.addEventListener('DOMContentLoaded', () => {
    console.log('Intel Odds Schedule Manager initialized');
    initializeDatePicker();
    initializeResizeHandle();
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

// Resize handle initialization
function initializeResizeHandle() {
    const resizeHandle = document.getElementById('resize-handle');
    const centerPanel = document.getElementById('center-panel');
    const mainContainer = document.querySelector('.main-container');

    let isResizing = false;
    let startX = 0;
    let startWidth = 0;

    resizeHandle.addEventListener('mousedown', (e) => {
        isResizing = true;
        startX = e.clientX;
        startWidth = centerPanel.offsetWidth;

        // Add visual feedback
        document.body.style.cursor = 'col-resize';
        document.body.style.userSelect = 'none';

        e.preventDefault();
    });

    document.addEventListener('mousemove', (e) => {
        if (!isResizing) return;

        const deltaX = e.clientX - startX;
        const newWidth = startWidth + deltaX;

        // Get container width to calculate constraints
        const containerWidth = mainContainer.offsetWidth;
        const sidebarWidth = 280; // Left sidebar width
        const minCenterWidth = 300;
        const minRightWidth = 300;
        const maxCenterWidth = containerWidth - sidebarWidth - minRightWidth - 8; // 8px for resize handle

        // Apply constraints
        if (newWidth >= minCenterWidth && newWidth <= maxCenterWidth) {
            centerPanel.style.flex = 'none';
            centerPanel.style.width = `${newWidth}px`;
        }
    });

    document.addEventListener('mouseup', () => {
        if (isResizing) {
            isResizing = false;
            document.body.style.cursor = '';
            document.body.style.userSelect = '';
        }
    });

    // Double-click to reset to default
    resizeHandle.addEventListener('dblclick', () => {
        centerPanel.style.flex = '1';
        centerPanel.style.width = '';
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

    sportSelect.innerHTML = '<option value="">Select Sport</option>' +
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

function clearLeagues() {
    const leaguesList = document.getElementById('leagues-list');
    leaguesList.innerHTML = `
        <div class="empty-state">
            <svg class="empty-icon" viewBox="0 0 24 24" fill="none" stroke="currentColor">
                <circle cx="12" cy="12" r="10"/>
            </svg>
            <p>Select a sport to view leagues</p>
        </div>
    `;
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
async function loadGroups() {
    if (!state.selectedLeague) {
        clearGroups();
        return;
    }

    if (!state.selectedDate) {
        clearGroups();
        return;
    }

    const url = `${API_BASE_URL}/categories/league/${state.selectedLeague.id}/date/${state.selectedDate}`;
    console.log('Loading groups from URL:', url);
    console.log('Selected date:', state.selectedDate);

    try {
        const response = await fetch(url);
        if (!response.ok) {
            throw new Error(`HTTP error! status: ${response.status}`);
        }
        const groups = await response.json();

        console.log('Loaded groups for league', state.selectedLeague.id, 'and date', state.selectedDate, ':', groups);
        console.log('Number of groups returned:', groups.length);

        // Log each group's details
        groups.forEach(group => {
            console.log(`  Group ID: ${group.id}, Date: ${group.date}, Header: ${group.header}`);
        });

        state.groups = groups;
        await displayGroups(groups);
    } catch (error) {
        console.error('Error loading groups:', error);
        showError('Failed to load groups');
        document.getElementById('groups-container').innerHTML = '<div class="empty-state"><p>Error loading groups</p></div>';
    }
}

async function displayGroups(groups) {
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

    // Fetch game counts for all groups
    const groupsWithCounts = await Promise.all(groups.map(async (group) => {
        try {
            const response = await fetch(`${API_BASE_URL}/games/group/${group.id}/count`);
            const count = await response.json();
            return { ...group, gameCount: count };
        } catch (error) {
            console.error(`Error fetching count for group ${group.id}:`, error);
            return { ...group, gameCount: 0 };
        }
    }));

    container.innerHTML = groupsWithCounts.map(group => `
        <div class="group-card ${state.selectedGroup && state.selectedGroup.id === group.id ? 'active' : ''}"
             onclick="selectGroup(${group.id})">
            <div class="group-header">
                <div class="group-title-row">
                    <div class="group-title">${group.header || group.name || 'Unnamed Group'}</div>
                    <div class="group-meta">
                        <div class="group-meta-item">
                            <svg viewBox="0 0 24 24" fill="none" stroke="currentColor">
                                <rect x="3" y="4" width="18" height="18" rx="2" ry="2"/>
                                <line x1="16" y1="2" x2="16" y2="6"/>
                                <line x1="8" y1="2" x2="8" y2="6"/>
                                <line x1="3" y1="10" x2="21" y2="10"/>
                            </svg>
                            <span>${formatDateLocal(group.date)}</span>
                        </div>
                        <div class="group-meta-item">
                            <span>ID: ${group.id}</span>
                        </div>
                        <div class="group-meta-item group-game-count">
                            <span>${group.gameCount} ${group.gameCount === 1 ? 'game' : 'games'}</span>
                        </div>
                    </div>
                </div>
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
    fetch(`${API_BASE_URL}/games/group/${groupId}`)
        .then(response => {
            if (!response.ok) {
                throw new Error(`HTTP error! status: ${response.status}`);
            }
            return response.json();
        })
        .then(games => {
            console.log('Loaded games for group', groupId, ':', games);
            state.events = games;
            displayEvents(games);
        })
        .catch(error => {
            console.error('Error loading games:', error);
            showError('Failed to load games');
            document.getElementById('events-container').innerHTML = '<div class="empty-state"><p>Error loading games</p></div>';
        });
}

function displayEvents(games) {
    const container = document.getElementById('events-container');
    const countBadge = document.getElementById('event-count');

    countBadge.textContent = games.length;

    if (games.length === 0) {
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

    container.innerHTML = games.map(game => {
        // Debug logging
        console.log('Game data:', {
            eventId: game.eventId,
            score1: game.score1,
            score2: game.score2,
            timer: game.timer,
            period: game.period
        });

        // Format the time - check TBA first!
        let timeDisplay = 'TBD';
        if (game.tba === 1 || game.tba === true) {
            timeDisplay = 'TBA';
        } else if (game.time) {
            const gameTime = new Date(game.time);
            timeDisplay = gameTime.toLocaleTimeString('en-US', {
                hour: 'numeric',
                minute: '2-digit',
                hour12: true
            });
        }

        // Build score display
        const hasScore = game.score1 !== null || game.score2 !== null;
        const score1Display = game.score1 !== null ? game.score1 : '-';
        const score2Display = game.score2 !== null ? game.score2 : '-';

        // Build status display - timer and period come from backend
        const hasStatus = game.timer || game.period;
        const timerDisplay = game.timer || '';
        const periodDisplay = game.period || '';

        return `
            <div class="event-card" data-event-id="${game.eventId}" data-league-id="${game.leagueId || ''}">
                <div class="event-teams">
                    <div class="team-row">
                        <div class="team-name editable-team"
                             data-participant-id="${game.awayParticipantId || ''}"
                             data-team-id="${game.awayTeamId || ''}"
                             onclick="editTeam(this, ${game.leagueId}, ${game.awayParticipantId})"
                             title="Click to edit team">
                            ${game.awayTeam || 'TBD'}
                        </div>
                        ${hasScore ? `<div class="team-score">${score1Display}</div>` : ''}
                        <div class="event-time editable-time"
                             onclick="editTime(this, ${game.eventId}, '${game.time || ''}', ${game.tba || 0})"
                             title="Click to edit time">
                            ${timeDisplay}
                        </div>
                    </div>
                    <div class="team-row">
                        <div class="team-name editable-team"
                             data-participant-id="${game.homeParticipantId || ''}"
                             data-team-id="${game.homeTeamId || ''}"
                             onclick="editTeam(this, ${game.leagueId}, ${game.homeParticipantId})"
                             title="Click to edit team">
                            ${game.homeTeam || 'TBD'}
                        </div>
                        ${hasScore ? `<div class="team-score">${score2Display}</div>` : ''}
                        <div class="event-number">#${game.number || game.eventId}</div>
                    </div>
                </div>
                ${hasStatus ? `
                <div class="game-status-row">
                    ${timerDisplay ? `<div class="status-item"><span class="status-label">Time:</span> ${timerDisplay}</div>` : ''}
                    ${periodDisplay ? `<div class="status-item"><span class="status-label">Period:</span> ${periodDisplay}</div>` : ''}
                </div>
                ` : ''}
            </div>
        `;
    }).join('');
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
async function addGroup() {
    if (!state.selectedLeague) {
        showError('Please select a league first');
        return;
    }

    // Fetch event group types
    let eventGroupTypes = [];
    try {
        const response = await fetch(`${API_BASE_URL}/event-group-types`);
        eventGroupTypes = await response.json();
    } catch (error) {
        console.error('Error loading event group types:', error);
    }

    // Create modal
    const modal = document.createElement('div');
    modal.className = 'modal-overlay';
    modal.innerHTML = `
        <div class="modal-content add-group-modal">
            <div class="modal-header">
                <h3>Add Group</h3>
                <button class="modal-close" onclick="closeAddGroupModal()">&times;</button>
            </div>
            <div class="modal-body">
                <div class="form-group">
                    <label for="group-header">Group Header:</label>
                    <input type="text"
                           id="group-header"
                           class="form-input"
                           placeholder='e.g., "NFL - Thursday October 22nd"'>
                </div>

                <div class="form-group">
                    <label for="group-start-date">Start Date:</label>
                    <input type="date"
                           id="group-start-date"
                           class="form-input"
                           value="${state.selectedDate}">
                </div>

                <div class="form-group">
                    <label for="group-end-date">End Date:</label>
                    <input type="date"
                           id="group-end-date"
                           class="form-input">
                </div>

                <div class="form-group">
                    <label for="group-type">Group Type:</label>
                    <select id="group-type" class="form-input">
                        <option value="">Select Type...</option>
                        ${eventGroupTypes.map(type => `<option value="${type.id}">${type.name}</option>`).join('')}
                    </select>
                </div>

                <div class="form-group">
                    <label for="group-description">Description:</label>
                    <textarea id="group-description"
                              class="form-input"
                              rows="3"
                              placeholder="Enter description..."></textarea>
                </div>

                <div class="form-group checkbox-group">
                    <label>
                        <input type="checkbox" id="group-exclude">
                        Exclude
                    </label>
                    <label>
                        <input type="checkbox" id="group-override">
                        Override
                    </label>
                </div>

                <div class="modal-actions">
                    <button class="btn-secondary" onclick="closeAddGroupModal()">Cancel</button>
                    <button class="btn-primary" onclick="saveNewGroup()">Add Group</button>
                </div>
            </div>
        </div>
    `;

    document.body.appendChild(modal);
}

function closeAddGroupModal() {
    const modal = document.querySelector('.modal-overlay');
    if (modal) {
        modal.remove();
    }
}

async function saveNewGroup() {
    const header = document.getElementById('group-header').value.trim();
    const startDate = document.getElementById('group-start-date').value;
    const endDate = document.getElementById('group-end-date').value;
    const groupTypeId = document.getElementById('group-type').value;
    const description = document.getElementById('group-description').value.trim();
    const exclude = document.getElementById('group-exclude').checked;
    const override = document.getElementById('group-override').checked;

    if (!header) {
        showError('Please enter a group header');
        return;
    }

    if (!startDate) {
        showError('Please select a start date');
        return;
    }

    const group = {
        header: header,
        date: startDate,
        endDate: endDate || null,
        description: description || null,
        exclude: exclude,
        override: override,
        league: { id: state.selectedLeague.id }
    };

    // Add event group type if selected
    if (groupTypeId) {
        group.eventGroupType = { id: parseInt(groupTypeId) };
    }

    console.log('Creating group:', group);

    try {
        const response = await fetch(`${API_BASE_URL}/categories`, {
            method: 'POST',
            headers: { 'Content-Type': 'application/json' },
            body: JSON.stringify(group)
        });

        console.log('Create group response status:', response.status);
        if (!response.ok) {
            const text = await response.text();
            console.error('Error response:', text);
            throw new Error(`HTTP error! status: ${response.status}, body: ${text}`);
        }

        const createdGroup = await response.json();
        console.log('Group created successfully:', createdGroup);
        console.log('Created group details - ID:', createdGroup.id, 'Date:', createdGroup.date, 'Header:', createdGroup.header);
        closeAddGroupModal();

        // If the created group's date is before the selected date, update the selected date
        if (createdGroup.date < state.selectedDate) {
            console.log('Created group date is before selected date, updating filter to:', createdGroup.date);
            state.selectedDate = createdGroup.date;
            document.getElementById('schedule-date').value = createdGroup.date;
        }

        // Reload groups and wait for it to complete
        await loadGroups();

        // Scroll to the newly created group
        setTimeout(() => {
            const groupCard = document.querySelector(`.group-card[onclick*="${createdGroup.id}"]`);
            if (groupCard) {
                groupCard.scrollIntoView({ behavior: 'smooth', block: 'center' });
                // Briefly highlight the new group
                groupCard.style.backgroundColor = '#e3f2fd';
                setTimeout(() => {
                    groupCard.style.backgroundColor = '';
                }, 2000);
            }
        }, 100);

        showSuccess('Group added successfully');
    } catch (error) {
        console.error('Error adding group:', error);
        showError('Failed to add group: ' + error.message);
    }
}

async function editGroup(groupId) {
    const group = state.groups.find(c => c.id === groupId);
    if (!group) return;

    const newHeader = prompt('Edit group header:', group.header);
    if (!newHeader || newHeader === group.header) return;

    group.header = newHeader;

    try {
        const response = await fetch(`${API_BASE_URL}/categories/${groupId}`, {
            method: 'PUT',
            headers: { 'Content-Type': 'application/json' },
            body: JSON.stringify(group)
        });
        await response.json();
        await loadGroups();
        showSuccess('Group updated successfully');
    } catch (error) {
        console.error('Error updating group:', error);
        showError('Failed to update group');
    }
}

async function deleteGroup(groupId) {
    if (!confirm('Are you sure you want to delete this group?')) return;

    try {
        await fetch(`${API_BASE_URL}/categories/${groupId}`, {
            method: 'DELETE'
        });
        await loadGroups();
        showSuccess('Group deleted successfully');
    } catch (error) {
        console.error('Error deleting group:', error);
        showError('Failed to delete group');
    }
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

// Edit time function
async function editTime(element, eventId, currentTime, currentTba) {
    const modal = document.createElement('div');
    modal.className = 'modal-overlay';

    // Parse current time if it exists
    let hour = '12';
    let minute = '00';
    let ampm = 'PM';

    if (currentTime && !currentTba) {
        const date = new Date(currentTime);
        let h = date.getHours();
        const m = date.getMinutes();

        ampm = h >= 12 ? 'PM' : 'AM';
        h = h % 12;
        h = h ? h : 12; // the hour '0' should be '12'

        hour = h.toString();
        minute = m.toString().padStart(2, '0');
    }

    modal.innerHTML = `
        <div class="modal-content time-edit-modal">
            <div class="modal-header">
                <h3>Edit Time</h3>
                <button class="undo-button" onclick="undoTimeChanges('${hour}', '${minute}', '${ampm}')">Undo Changes</button>
                <button class="modal-close" onclick="closeTimeModal()">&times;</button>
            </div>
            <div class="modal-body">
                <div class="time-input-container">
                    <input type="number"
                           id="hour-input"
                           class="time-number-input"
                           value="${hour}"
                           min="1"
                           max="12"
                           placeholder="HH">
                    <span class="time-separator">:</span>
                    <input type="number"
                           id="minute-input"
                           class="time-number-input"
                           value="${minute}"
                           min="0"
                           max="59"
                           placeholder="MM">
                    <select id="ampm-select" class="ampm-select">
                        <option value="AM" ${ampm === 'AM' ? 'selected' : ''}>AM</option>
                        <option value="PM" ${ampm === 'PM' ? 'selected' : ''}>PM</option>
                    </select>
                </div>
                <button class="tba-time-button" onclick="setTBA(${eventId})">Set as TBA</button>
                <div class="modal-actions">
                    <button class="btn-primary" onclick="saveTime(${eventId})">OK</button>
                </div>
            </div>
        </div>
    `;

    document.body.appendChild(modal);

    // Focus on hour input
    setTimeout(() => {
        document.getElementById('hour-input').focus();
        document.getElementById('hour-input').select();
    }, 100);

    // Close on overlay click
    modal.addEventListener('click', (e) => {
        if (e.target === modal) {
            closeTimeModal();
        }
    });

    // Handle Enter key on inputs
    ['hour-input', 'minute-input'].forEach(id => {
        document.getElementById(id).addEventListener('keypress', (e) => {
            if (e.key === 'Enter') {
                saveTime(eventId);
            }
        });
    });

    // Auto-format minute input to always be 2 digits
    document.getElementById('minute-input').addEventListener('blur', (e) => {
        const val = e.target.value;
        if (val && val.length === 1) {
            e.target.value = '0' + val;
        }
    });
}

// Save time from modal
async function saveTime(eventId) {
    const hour = parseInt(document.getElementById('hour-input').value);
    const minute = parseInt(document.getElementById('minute-input').value);
    const ampm = document.getElementById('ampm-select').value;

    // Validate inputs
    if (isNaN(hour) || hour < 1 || hour > 12) {
        showError('Hour must be between 1 and 12');
        return;
    }

    if (isNaN(minute) || minute < 0 || minute > 59) {
        showError('Minute must be between 0 and 59');
        return;
    }

    // Convert to 24-hour format
    let hours24 = hour;
    if (ampm === 'PM' && hour !== 12) {
        hours24 = hour + 12;
    } else if (ampm === 'AM' && hour === 12) {
        hours24 = 0;
    }

    // Create ISO time string
    const today = new Date();
    today.setHours(hours24, minute, 0, 0);
    const time = today.toISOString();

    try {
        const response = await fetch(`${API_BASE_URL}/games/event/${eventId}/time`, {
            method: 'PUT',
            headers: {
                'Content-Type': 'application/json'
            },
            body: JSON.stringify({ time, tba: 0 })
        });

        if (response.ok) {
            closeTimeModal();
            showSuccess('Time updated successfully');
            if (state.selectedGroup) {
                loadEvents(state.selectedGroup.id);
            }
        } else {
            showError('Failed to update time');
        }
    } catch (error) {
        console.error('Error updating time:', error);
        showError('Failed to update time');
    }
}

// Set time as TBA
async function setTBA(eventId) {
    console.log('setTBA called with eventId:', eventId);
    try {
        const payload = { time: new Date().toISOString(), tba: 1 };
        console.log('Sending TBA request:', payload);

        const response = await fetch(`${API_BASE_URL}/games/event/${eventId}/time`, {
            method: 'PUT',
            headers: {
                'Content-Type': 'application/json'
            },
            body: JSON.stringify(payload)
        });

        console.log('TBA response status:', response.status);

        if (response.ok) {
            closeTimeModal();
            showSuccess('Time set to TBA');
            if (state.selectedGroup) {
                loadEvents(state.selectedGroup.id);
            }
        } else {
            const errorText = await response.text();
            console.error('TBA error response:', errorText);
            showError('Failed to set time to TBA');
        }
    } catch (error) {
        console.error('Error setting TBA:', error);
        showError('Failed to set time to TBA: ' + error.message);
    }
}

// Undo time changes
function undoTimeChanges(originalHour, originalMinute, originalAmpm) {
    document.getElementById('hour-input').value = originalHour;
    document.getElementById('minute-input').value = originalMinute;
    document.getElementById('ampm-select').value = originalAmpm;
}

// Close time modal
function closeTimeModal() {
    const modal = document.querySelector('.modal-overlay');
    if (modal) {
        modal.remove();
    }
}

// Edit team function
async function editTeam(element, leagueId, participantId) {
    if (!leagueId || !participantId) {
        showError('Cannot edit team: missing league or participant information');
        return;
    }

    try {
        // Fetch teams for this league
        const response = await fetch(`${API_BASE_URL}/games/league/${leagueId}/teams`);
        const teams = await response.json();

        console.log('Fetched teams:', teams);
        console.log('Total teams:', teams.length);

        // Filter out teams without names
        const validTeams = teams.filter(team => team.teamName && team.teamName.trim() !== '');
        console.log('Valid teams with names:', validTeams.length);

        if (validTeams.length === 0) {
            showError('No teams found for this league');
            return;
        }

        // Show team selection modal
        showTeamSelectionModal(validTeams, participantId);
    } catch (error) {
        console.error('Error fetching teams:', error);
        showError('Failed to load teams');
    }
}

// Show team selection modal
function showTeamSelectionModal(teams, participantId) {
    console.log ("teams lebgth: ", teams.length);
    // Create modal overlay
    const modal = document.createElement('div');
    modal.className = 'modal-overlay';
    modal.innerHTML = `
        <div class="modal-content team-selection-modal">
            <div class="modal-header">
                <h3>Select Team</h3>
                <button class="tbd-button" onclick="selectTBD(${participantId})">TBD</button>
                <button class="modal-close" onclick="closeTeamModal()">&times;</button>
            </div>
            <div class="modal-body">
                <input type="text"
                       id="team-search"
                       class="team-search-input"
                       placeholder="Search teams..."
                       onkeyup="filterTeams()">
                <div class="team-list" id="team-list">
                    ${teams.map(team => `
                        <div class="team-item"
                             data-team-name="${team.teamName.toLowerCase()}"
                             onclick="selectTeam(${participantId}, ${team.leagueTeamId})">
                            ${team.teamName}
                        </div>
                    `).join('')}
                </div>
            </div>
        </div>
    `;

    document.body.appendChild(modal);

    // Focus on search input
    setTimeout(() => {
        document.getElementById('team-search').focus();
    }, 100);

    // Close on overlay click
    modal.addEventListener('click', (e) => {
        if (e.target === modal) {
            closeTeamModal();
        }
    });
}

// Filter teams based on search
function filterTeams() {
    const searchTerm = document.getElementById('team-search').value.toLowerCase();
    const teamItems = document.querySelectorAll('.team-item');

    teamItems.forEach(item => {
        const teamName = item.getAttribute('data-team-name');
        if (teamName.includes(searchTerm)) {
            item.style.display = 'block';
        } else {
            item.style.display = 'none';
        }
    });
}

// Select team
async function selectTeam(participantId, leagueTeamId) {
    try {
        const response = await fetch(`${API_BASE_URL}/games/participant/${participantId}/team`, {
            method: 'PUT',
            headers: {
                'Content-Type': 'application/json'
            },
            body: JSON.stringify({ leagueTeamId })
        });

        if (response.ok) {
            closeTeamModal();
            showSuccess('Team updated successfully');
            // Reload events for the current group
            if (state.selectedGroup) {
                loadEvents(state.selectedGroup.id);
            }
        } else {
            showError('Failed to update team');
        }
    } catch (error) {
        console.error('Error updating team:', error);
        showError('Failed to update team');
    }
}

// Select TBD
async function selectTBD(participantId) {
    try {
        // Delete the participant's team assignment by setting leagueTeamId to null
        const response = await fetch(`${API_BASE_URL}/games/participant/${participantId}/team`, {
            method: 'DELETE'
        });

        if (response.ok) {
            closeTeamModal();
            showSuccess('Team set to TBD');
            // Reload events for the current group
            if (state.selectedGroup) {
                loadEvents(state.selectedGroup.id);
            }
        } else {
            showError('Failed to set team to TBD');
        }
    } catch (error) {
        console.error('Error setting team to TBD:', error);
        showError('Failed to set team to TBD');
    }
}

// Close team modal
function closeTeamModal() {
    const modal = document.querySelector('.modal-overlay');
    if (modal) {
        modal.remove();
    }
}
