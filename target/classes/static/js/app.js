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
async function loadEvents(groupId) {
    try {
        const response = await fetch(`${API_BASE_URL}/games/group/${groupId}`);
        if (!response.ok) {
            throw new Error(`HTTP error! status: ${response.status}`);
        }
        const games = await response.json();
        console.log('Loaded games for group', groupId, ':', games);
        state.events = games;
        displayEvents(games);
    } catch (error) {
        console.error('Error loading games:', error);
        showError('Failed to load games');
        document.getElementById('events-container').innerHTML = '<div class="empty-state"><p>Error loading games</p></div>';
    }
}

function displayEvents(games) {
    const container = document.getElementById('events-container');
    const countBadge = document.getElementById('event-count');

    // Separate active and inactive games
    const activeGames = games.filter(game => game.active !== false);
    const inactiveGames = games.filter(game => game.active === false);

    countBadge.textContent = activeGames.length;

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

    // Sort games by time
    const sortGames = (gamesToSort) => {
        return [...gamesToSort].sort((a, b) => {
            // Handle TBA games - put them at the end
            if (a.tba && !b.tba) return 1;
            if (!a.tba && b.tba) return -1;
            if (a.tba && b.tba) return 0;

            // Sort by time
            const timeA = a.time ? new Date(a.time).getTime() : 0;
            const timeB = b.time ? new Date(b.time).getTime() : 0;
            return timeA - timeB;
        });
    };

    const sortedActiveGames = sortGames(activeGames);
    const sortedInactiveGames = sortGames(inactiveGames);

    // Function to render a game card
    const renderGameCard = (game, isInactive = false) => {
        // Debug logging
        console.log('Game data:', {
            eventId: game.eventId,
            score1: game.score1,
            score2: game.score2,
            timer: game.timer,
            period: game.period
        });

        // Format the date and time
        let dateDisplay = '';
        let timeDisplay = 'TBD';

        // Format the time - check TBA first!
        if (game.tba === 1 || game.tba === true) {
            timeDisplay = 'TBA';
            // For TBA games, use the event.date field for the date
            if (game.date) {
                const [year, month, day] = game.date.split('-').map(Number);
                const gameDate = new Date(year, month - 1, day);
                dateDisplay = gameDate.toLocaleDateString('en-US', {
                    month: 'numeric',
                    day: 'numeric'
                });
            }
        } else if (game.time) {
            // For games with a time, extract both date and time from the time field
            const gameTime = new Date(game.time);
            dateDisplay = gameTime.toLocaleDateString('en-US', {
                month: 'numeric',
                day: 'numeric'
            });
            timeDisplay = gameTime.toLocaleTimeString('en-US', {
                hour: 'numeric',
                minute: '2-digit',
                hour12: true
            });
        } else {
            // No time and not TBA - use event.date if available
            if (game.date) {
                const [year, month, day] = game.date.split('-').map(Number);
                const gameDate = new Date(year, month - 1, day);
                dateDisplay = gameDate.toLocaleDateString('en-US', {
                    month: 'numeric',
                    day: 'numeric'
                });
            }
        }

        // Combine date and time for display
        const dateTimeDisplay = dateDisplay ? `${dateDisplay} ${timeDisplay}` : timeDisplay;

        // Build score display
        const hasScore = game.score1 !== null || game.score2 !== null;
        const score1Display = game.score1 !== null ? game.score1 : '-';
        const score2Display = game.score2 !== null ? game.score2 : '-';

        // Build status display - timer and period come from backend
        const hasStatus = game.timer || game.period;
        const timerDisplay = game.timer || '';
        const periodDisplay = game.period || '';

        return `
            <div class="event-card ${isInactive ? 'inactive' : ''}" data-event-id="${game.eventId}" data-league-id="${game.leagueId || ''}">
                <div class="event-card-header">
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
                                 onclick="editTime(this, ${game.eventId}, '${game.time || ''}', ${game.tba || 0}, '${game.date || ''}')"
                                 title="Click to edit time">
                                ${dateTimeDisplay}
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
                    <div class="event-actions">
                        <button class="icon-btn-small" onclick="editGame(${game.eventId})" title="Edit Game">
                            <svg viewBox="0 0 24 24" fill="none" stroke="currentColor">
                                <path d="M11 4H4a2 2 0 0 0-2 2v14a2 2 0 0 0 2 2h14a2 2 0 0 0 2-2v-7"/>
                                <path d="M18.5 2.5a2.121 2.121 0 0 1 3 3L12 15l-4 1 1-4 9.5-9.5z"/>
                            </svg>
                        </button>
                        <button class="icon-btn-small" onclick="deleteGame(${game.eventId})" title="Delete Game">
                            <svg viewBox="0 0 24 24" fill="none" stroke="currentColor">
                                <polyline points="3 6 5 6 21 6"/>
                                <path d="M19 6v14a2 2 0 0 1-2 2H7a2 2 0 0 1-2-2V6m3 0V4a2 2 0 0 1 2-2h4a2 2 0 0 1 2 2v2"/>
                            </svg>
                        </button>
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
    };

    // Build the HTML with active games first, then inactive games
    let html = '';

    // Add active games
    if (sortedActiveGames.length > 0) {
        html += sortedActiveGames.map(game => renderGameCard(game, false)).join('');
    }

    // Add inactive games section if there are any
    if (sortedInactiveGames.length > 0) {
        html += `
            <div class="deactivated-section">
                <div class="deactivated-label">Deactivated Games</div>
            </div>
        `;
        html += sortedInactiveGames.map(game => renderGameCard(game, true)).join('');
    }

    container.innerHTML = html;
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
                    <textarea id="group-header"
                              class="form-input"
                              rows="2"
                              placeholder='e.g., "NFL - Thursday October 22nd"'></textarea>
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

    // Load event group types if not already loaded
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
                <h3>Edit Group</h3>
                <button class="modal-close" onclick="closeEditGroupModal()">&times;</button>
            </div>
            <div class="modal-body">
                <div class="form-group">
                    <label for="edit-group-header">Group Header:</label>
                    <textarea id="edit-group-header"
                              class="form-input"
                              rows="2"
                              placeholder='e.g., "NFL - Thursday October 22nd"'>${group.header || ''}</textarea>
                </div>

                <div class="form-group">
                    <label for="edit-group-start-date">Start Date:</label>
                    <input type="date"
                           id="edit-group-start-date"
                           class="form-input"
                           value="${group.startDate || ''}">
                </div>

                <div class="form-group">
                    <label for="edit-group-end-date">End Date:</label>
                    <input type="date"
                           id="edit-group-end-date"
                           class="form-input"
                           value="${group.endDate || ''}">
                </div>

                <div class="form-group">
                    <label for="edit-group-type">Group Type:</label>
                    <select id="edit-group-type" class="form-input">
                        <option value="">Select Type...</option>
                        ${eventGroupTypes.map(type =>
                            `<option value="${type.id}" ${group.eventGroupType && group.eventGroupType.id === type.id ? 'selected' : ''}>${type.name}</option>`
                        ).join('')}
                    </select>
                </div>

                <div class="form-group">
                    <label for="edit-group-description">Description:</label>
                    <textarea id="edit-group-description"
                              class="form-input"
                              rows="3"
                              placeholder="Enter description...">${group.description || ''}</textarea>
                </div>

                <div class="form-group checkbox-group">
                    <label>
                        <input type="checkbox" id="edit-group-exclude" ${group.exclude ? 'checked' : ''}>
                        Exclude
                    </label>
                    <label>
                        <input type="checkbox" id="edit-group-override" ${group.override ? 'checked' : ''}>
                        Override
                    </label>
                </div>

                <div class="modal-actions">
                    <button class="btn-secondary" onclick="closeEditGroupModal()">Cancel</button>
                    <button class="btn-primary" onclick="saveEditedGroup(${groupId})">Update Group</button>
                </div>
            </div>
        </div>
    `;

    document.body.appendChild(modal);
}

function closeEditGroupModal() {
    const modal = document.querySelector('.modal-overlay');
    if (modal) {
        modal.remove();
    }
}

async function saveEditedGroup(groupId) {
    const header = document.getElementById('edit-group-header').value.trim();
    const startDate = document.getElementById('edit-group-start-date').value;
    const endDate = document.getElementById('edit-group-end-date').value;
    const groupTypeId = document.getElementById('edit-group-type').value;
    const description = document.getElementById('edit-group-description').value.trim();
    const exclude = document.getElementById('edit-group-exclude').checked;
    const override = document.getElementById('edit-group-override').checked;

    if (!header) {
        showError('Please enter a group header');
        return;
    }

    if (!startDate) {
        showError('Please select a start date');
        return;
    }

    const groupData = {
        header: header,
        startDate: startDate,
        endDate: endDate || null,
        eventGroupTypeId: groupTypeId ? parseInt(groupTypeId) : null,
        description: description || null,
        exclude: exclude,
        override: override
    };

    try {
        const response = await fetch(`${API_BASE_URL}/categories/${groupId}`, {
            method: 'PUT',
            headers: { 'Content-Type': 'application/json' },
            body: JSON.stringify(groupData)
        });

        if (!response.ok) {
            throw new Error('Failed to update group');
        }

        closeEditGroupModal();
        await loadGroups();

        // Re-select the edited group
        setTimeout(() => {
            selectGroup(groupId);
        }, 100);

        showSuccess('Group updated successfully');
    } catch (error) {
        console.error('Error updating group:', error);
        showError('Failed to update group: ' + error.message);
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
async function addEvent() {
    if (!state.selectedGroup) {
        showError('Please select a group first');
        return;
    }

    // Show add game modal
    await showAddGameModal();
}

async function editGame(eventId) {
    // Find the game in the current events
    const game = state.events.find(e => e.eventId === eventId);
    if (!game) {
        showError('Game not found');
        return;
    }

    const group = state.selectedGroup;
    const leagueId = game.leagueId || (group.league ? group.league.id : null);

    if (!leagueId) {
        showError('No league associated with this game');
        return;
    }

    // Fetch teams and store globally for later use
    const teams = await fetch(`${API_BASE_URL}/games/league/${leagueId}/teams`).then(r => r.json());
    window.currentGameTeams = teams;

    // Parse the time if it exists
    let timeValue = '';
    let dateValue = game.date || '';
    if (game.time) {
        const gameTime = new Date(game.time);
        // Format time as HH:MM for the time input
        timeValue = gameTime.toTimeString().substring(0, 5);
        // Use the date from the time field
        dateValue = gameTime.toISOString().split('T')[0];
    }

    const modal = document.createElement('div');
    modal.className = 'modal-overlay';
    modal.innerHTML = `
        <div class="modal-content add-group-modal">
            <div class="modal-header">
                <h3>Edit Game</h3>
                <button class="modal-close" onclick="closeEditGameModal()">&times;</button>
            </div>
            <div class="modal-body">
                <div style="display: grid; grid-template-columns: 1fr 1fr; gap: 1rem;">
                    <div class="form-group">
                        <label for="edit-game-date">Date:</label>
                        <input type="date" id="edit-game-date" class="form-input" value="${dateValue}">
                    </div>
                    <div class="form-group">
                        <label for="edit-game-time">Time:</label>
                        <input type="time" id="edit-game-time" class="form-input" value="${timeValue}">
                    </div>
                </div>

                <div class="form-group">
                    <label>
                        <input type="checkbox" id="edit-game-tba" ${game.tba ? 'checked' : ''}> Time TBA
                    </label>
                </div>

                <div class="form-group">
                    <label for="edit-event-number">Event Number:</label>
                    <input type="number" id="edit-event-number" class="form-input" value="${game.number || ''}" placeholder="e.g., 101">
                </div>

                <div class="form-group">
                    <label for="edit-away-team">Away Team:</label>
                    <input type="text"
                           id="edit-away-team"
                           class="form-input searchable-team"
                           value="${game.awayTeam || ''}"
                           placeholder="Type to search teams..."
                           autocomplete="off"
                           data-team-id="${game.awayTeamId || ''}"
                           data-participant-id="${game.awayParticipantId || ''}">
                    <div id="edit-away-team-dropdown" class="team-dropdown" style="display: none;"></div>
                </div>

                <div class="form-group">
                    <label for="edit-home-team">Home Team:</label>
                    <input type="text"
                           id="edit-home-team"
                           class="form-input searchable-team"
                           value="${game.homeTeam || ''}"
                           placeholder="Type to search teams..."
                           autocomplete="off"
                           data-team-id="${game.homeTeamId || ''}"
                           data-participant-id="${game.homeParticipantId || ''}">
                    <div id="edit-home-team-dropdown" class="team-dropdown" style="display: none;"></div>
                </div>

                <div class="modal-actions">
                    <button class="btn-secondary" onclick="closeEditGameModal()">Cancel</button>
                    <button class="btn-primary" onclick="saveEditedGame(${eventId})">Update Game</button>
                </div>
            </div>
        </div>
    `;

    document.body.appendChild(modal);

    // Setup team search for edit modal
    setupTeamSearch('edit-away-team', 'edit-away-team-dropdown', teams);
    setupTeamSearch('edit-home-team', 'edit-home-team-dropdown', teams);
}

function closeEditGameModal() {
    const modal = document.querySelector('.modal-overlay');
    if (modal) {
        modal.remove();
    }
}

async function saveEditedGame(eventId) {
    const date = document.getElementById('edit-game-date').value;
    const time = document.getElementById('edit-game-time').value;
    const tba = document.getElementById('edit-game-tba').checked;
    const eventNumber = document.getElementById('edit-event-number').value;
    const awayTeamInput = document.getElementById('edit-away-team');
    const homeTeamInput = document.getElementById('edit-home-team');
    const awayTeamId = awayTeamInput.dataset.teamId;
    const homeTeamId = homeTeamInput.dataset.teamId;
    const awayParticipantId = awayTeamInput.dataset.participantId;
    const homeParticipantId = homeTeamInput.dataset.participantId;

    if (!date) {
        showError('Please select a date');
        return;
    }

    if (!tba && !time) {
        showError('Please enter a time or check TBA');
        return;
    }

    try {
        // Get the current event to preserve other fields
        const game = state.events.find(e => e.eventId === eventId);

        // Update event number if changed
        if (eventNumber && game) {
            const eventResponse = await fetch(`${API_BASE_URL}/events/${eventId}`);
            const event = await eventResponse.json();
            event.number = parseInt(eventNumber);

            await fetch(`${API_BASE_URL}/events/${eventId}`, {
                method: 'PUT',
                headers: { 'Content-Type': 'application/json' },
                body: JSON.stringify(event)
            });
        }

        // Update time
        const timeString = tba ? null : `${date}T${time}:00`;
        await fetch(`${API_BASE_URL}/games/event/${eventId}/time`, {
            method: 'PUT',
            headers: { 'Content-Type': 'application/json' },
            body: JSON.stringify({
                time: timeString,
                tba: tba ? 1 : 0
            })
        });

        // Update teams if they have changed
        if (awayTeamId && awayParticipantId) {
            await fetch(`${API_BASE_URL}/games/participant/${awayParticipantId}/team`, {
                method: 'PUT',
                headers: { 'Content-Type': 'application/json' },
                body: JSON.stringify({ leagueTeamId: parseInt(awayTeamId) })
            });
        }

        if (homeTeamId && homeParticipantId) {
            await fetch(`${API_BASE_URL}/games/participant/${homeParticipantId}/team`, {
                method: 'PUT',
                headers: { 'Content-Type': 'application/json' },
                body: JSON.stringify({ leagueTeamId: parseInt(homeTeamId) })
            });
        }

        closeEditGameModal();
        await loadEvents(state.selectedGroup.id);
        showSuccess('Game updated successfully');
    } catch (error) {
        console.error('Error updating game:', error);
        showError('Failed to update game: ' + error.message);
    }
}

async function deleteGame(eventId) {
    if (!confirm('Are you sure you want to delete this game?')) return;

    try {
        await fetch(`${API_BASE_URL}/events/${eventId}`, {
            method: 'DELETE'
        });
        await loadEvents(state.selectedGroup.id);
        showSuccess('Game deleted successfully');
    } catch (error) {
        console.error('Error deleting game:', error);
        showError('Failed to delete game');
    }
}

async function showAddGameModal() {
    const group = state.selectedGroup;
    const leagueId = group.league ? group.league.id : null;

    if (!leagueId) {
        showError('No league associated with this group');
        return;
    }

    // Fetch teams and store globally for later use
    const teams = await fetch(`${API_BASE_URL}/games/league/${leagueId}/teams`).then(r => r.json());
    window.currentGameTeams = teams; // Store for saveNewGame function

    const modal = document.createElement('div');
    modal.className = 'modal-overlay';
    modal.innerHTML = `
        <div class="modal-content add-group-modal">
            <div class="modal-header">
                <h3>Add Game</h3>
                <button class="modal-close" onclick="closeAddGameModal()">&times;</button>
            </div>
            <div class="modal-body">
                <div style="display: grid; grid-template-columns: 1fr 1fr; gap: 1rem;">
                    <div class="form-group">
                        <label for="game-date">Date:</label>
                        <input type="date" id="game-date" class="form-input" value="${group.date}">
                    </div>
                    <div class="form-group">
                        <label for="game-time">Time:</label>
                        <input type="time" id="game-time" class="form-input">
                    </div>
                </div>

                <div class="form-group">
                    <label>
                        <input type="checkbox" id="game-tba"> Time TBA
                    </label>
                </div>

                <div class="form-group">
                    <label for="event-number">Event Number:</label>
                    <input type="number" id="event-number" class="form-input" placeholder="e.g., 101">
                    <small style="color: #666; font-size: 0.85rem;">First team will be #N, second team will be #N+1</small>
                </div>

                <div style="display: grid; grid-template-columns: 60px 1fr; gap: 1rem; align-items: start;">
                    <div style="text-align: center; padding-top: 1.8rem;">
                        <div id="away-team-number" style="font-size: 1.1rem; font-weight: 600; color: #666;">-</div>
                        <div style="font-size: 0.7rem; color: #999; margin-top: 0.25rem;">AWAY</div>
                    </div>
                    <div class="form-group" style="position: relative;">
                        <label for="away-team">Away Team:</label>
                        <input type="text"
                               id="away-team"
                               class="form-input searchable-team"
                               placeholder="Type to search teams..."
                               autocomplete="off">
                        <div id="away-team-dropdown" class="team-dropdown" style="display: none;"></div>
                    </div>
                </div>

                <div style="display: grid; grid-template-columns: 60px 1fr; gap: 1rem; align-items: start;">
                    <div style="text-align: center; padding-top: 1.8rem;">
                        <div id="home-team-number" style="font-size: 1.1rem; font-weight: 600; color: #666;">-</div>
                        <div style="font-size: 0.7rem; color: #999; margin-top: 0.25rem;">HOME</div>
                    </div>
                    <div class="form-group" style="position: relative;">
                        <label for="home-team">Home Team:</label>
                        <input type="text"
                               id="home-team"
                               class="form-input searchable-team"
                               placeholder="Type to search teams..."
                               autocomplete="off">
                        <div id="home-team-dropdown" class="team-dropdown" style="display: none;"></div>
                    </div>
                </div>

                <div class="form-group" style="margin-top: 1rem; padding-top: 1rem; border-top: 1px solid #e0e0e0;">
                    <label style="font-weight: 600; margin-bottom: 0.75rem; display: block;">Venue</label>

                    <div style="display: flex; gap: 1.5rem; margin-bottom: 0.75rem;">
                        <label style="display: flex; align-items: center; gap: 0.5rem;">
                            <input type="checkbox" id="venue-neutral"> Neutral Site
                        </label>
                        <label style="display: flex; align-items: center; gap: 0.5rem;">
                            <input type="checkbox" id="venue-override"> Override
                        </label>
                    </div>

                    <div class="form-group">
                        <label for="venue-name">Venue Name:</label>
                        <input type="text" id="venue-name" class="form-input" placeholder="Enter venue name (optional)">
                    </div>

                    <div class="form-group">
                        <label for="venue-city">Venue City:</label>
                        <input type="text" id="venue-city" class="form-input" placeholder="Enter venue city (optional)">
                    </div>
                </div>

                <div class="modal-actions">
                    <button class="btn-secondary" onclick="closeAddGameModal()">Cancel</button>
                    <button class="btn-primary" onclick="saveNewGame()">Add Game</button>
                </div>
            </div>
        </div>
    `;

    document.body.appendChild(modal);

    // Initialize searchable team dropdowns
    initSearchableTeamDropdown('away-team', 'away-team-dropdown', teams);
    initSearchableTeamDropdown('home-team', 'home-team-dropdown', teams);

    // Update event numbers when event number changes
    const eventNumberInput = document.getElementById('event-number');
    eventNumberInput.addEventListener('input', updateEventNumbers);

    // Initial update
    updateEventNumbers();
}

function closeAddGameModal() {
    const modal = document.querySelector('.modal-overlay');
    if (modal) {
        modal.remove();
    }
}

function initSearchableTeamDropdown(inputId, dropdownId, teams) {
    const input = document.getElementById(inputId);
    const dropdown = document.getElementById(dropdownId);
    let selectedIndex = -1;
    let filteredTeams = [];

    function showDropdown(teamsToShow) {
        filteredTeams = teamsToShow;
        if (filteredTeams.length === 0) {
            dropdown.style.display = 'none';
            return;
        }

        dropdown.innerHTML = filteredTeams.map((team, index) =>
            `<div class="team-option" data-index="${index}" data-id="${team.leagueTeamId}">
                ${team.teamName}
            </div>`
        ).join('');

        dropdown.style.display = 'block';
        selectedIndex = -1;
    }

    function hideDropdown() {
        dropdown.style.display = 'none';
        selectedIndex = -1;
    }

    function selectTeam(team) {
        input.value = team.teamName;
        input.dataset.teamId = team.leagueTeamId;
        hideDropdown();
    }

    function highlightOption(index) {
        const options = dropdown.querySelectorAll('.team-option');
        options.forEach((opt, i) => {
            opt.classList.toggle('highlighted', i === index);
        });
    }

    // Input event - filter teams
    input.addEventListener('input', (e) => {
        const searchTerm = e.target.value.toLowerCase();
        if (!searchTerm) {
            showDropdown(teams);
        } else {
            const filtered = teams.filter(team =>
                team.teamName.toLowerCase().includes(searchTerm)
            );
            showDropdown(filtered);
        }
    });

    // Focus event - show all teams
    input.addEventListener('focus', () => {
        showDropdown(teams);
    });

    // Click outside to close
    document.addEventListener('click', (e) => {
        if (!input.contains(e.target) && !dropdown.contains(e.target)) {
            hideDropdown();
        }
    });

    // Keyboard navigation
    input.addEventListener('keydown', (e) => {
        const options = dropdown.querySelectorAll('.team-option');

        if (e.key === 'ArrowDown') {
            e.preventDefault();
            selectedIndex = Math.min(selectedIndex + 1, filteredTeams.length - 1);
            highlightOption(selectedIndex);
            options[selectedIndex]?.scrollIntoView({ block: 'nearest' });
        } else if (e.key === 'ArrowUp') {
            e.preventDefault();
            selectedIndex = Math.max(selectedIndex - 1, 0);
            highlightOption(selectedIndex);
            options[selectedIndex]?.scrollIntoView({ block: 'nearest' });
        } else if (e.key === 'Enter') {
            e.preventDefault();
            if (selectedIndex >= 0 && filteredTeams[selectedIndex]) {
                selectTeam(filteredTeams[selectedIndex]);
            } else if (filteredTeams.length === 1) {
                // If only one option, select it
                selectTeam(filteredTeams[0]);
            }
        } else if (e.key === 'Escape') {
            hideDropdown();
        }
    });

    // Click on option
    dropdown.addEventListener('click', (e) => {
        const option = e.target.closest('.team-option');
        if (option) {
            const index = parseInt(option.dataset.index);
            selectTeam(filteredTeams[index]);
        }
    });

    // Hover on option
    dropdown.addEventListener('mouseover', (e) => {
        const option = e.target.closest('.team-option');
        if (option) {
            selectedIndex = parseInt(option.dataset.index);
            highlightOption(selectedIndex);
        }
    });
}

function updateEventNumbers() {
    const eventNumber = document.getElementById('event-number').value;
    const awayNumberEl = document.getElementById('away-team-number');
    const homeNumberEl = document.getElementById('home-team-number');

    if (eventNumber && !isNaN(eventNumber)) {
        const num = parseInt(eventNumber);
        awayNumberEl.textContent = num;
        homeNumberEl.textContent = num + 1;
    } else {
        awayNumberEl.textContent = '-';
        homeNumberEl.textContent = '-';
    }
}

async function saveNewGame() {
    const date = document.getElementById('game-date').value;
    const time = document.getElementById('game-time').value;
    const tba = document.getElementById('game-tba').checked ? 1 : 0;
    const eventNumber = parseInt(document.getElementById('event-number').value);
    const awayTeamInput = document.getElementById('away-team');
    const homeTeamInput = document.getElementById('home-team');
    const venueName = document.getElementById('venue-name').value;
    const venueCity = document.getElementById('venue-city').value;
    const neutral = document.getElementById('venue-neutral').checked;
    const override = document.getElementById('venue-override').checked;

    // Validation
    if (!date) {
        showError('Please select a date');
        return;
    }
    if (!eventNumber) {
        showError('Please enter an event number');
        return;
    }
    if (!tba && !time) {
        showError('Please enter a time or check TBA');
        return;
    }

    // Get team IDs from dataset (set by the searchable dropdown)
    const awayTeamId = awayTeamInput.dataset.teamId ? parseInt(awayTeamInput.dataset.teamId) : null;
    const homeTeamId = homeTeamInput.dataset.teamId ? parseInt(homeTeamInput.dataset.teamId) : null;

    const gameData = {
        groupId: state.selectedGroup.id,
        leagueId: state.selectedGroup.league ? state.selectedGroup.league.id : null,
        date: date,
        time: time,
        tba: tba,
        eventNumber: eventNumber,
        homeTeamId: homeTeamId,
        awayTeamId: awayTeamId,
        venueName: venueName || null,
        venueCity: venueCity || null,
        neutral: neutral,
        override: override
    };

    try {
        console.log('Creating game with data:', gameData);
        const response = await fetch(`${API_BASE_URL}/games`, {
            method: 'POST',
            headers: { 'Content-Type': 'application/json' },
            body: JSON.stringify(gameData)
        });

        if (!response.ok) {
            const errorText = await response.text();
            console.error('Server error:', errorText);
            throw new Error('Failed to create game: ' + errorText);
        }

        const createdGame = await response.json();
        console.log('Game created successfully:', createdGame);

        closeAddGameModal();

        // Reload events
        console.log('Reloading events for group:', state.selectedGroup.id);
        await loadEvents(state.selectedGroup.id);

        // Scroll to and highlight the newly created game
        setTimeout(() => {
            const eventCard = document.querySelector(`.event-card[data-event-id="${createdGame.eventId}"]`);
            if (eventCard) {
                eventCard.scrollIntoView({ behavior: 'smooth', block: 'center' });
                // Briefly highlight the new game
                eventCard.style.backgroundColor = '#dbeafe';
                eventCard.style.transition = 'background-color 0.3s ease';
                setTimeout(() => {
                    eventCard.style.backgroundColor = '';
                }, 2000);
            }
        }, 100);

        showSuccess('Game added successfully');
    } catch (error) {
        console.error('Error creating game:', error);
        showError('Failed to add game: ' + error.message);
    }
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
async function editTime(element, eventId, currentTime, currentTba, eventDate) {
    const modal = document.createElement('div');
    modal.className = 'modal-overlay';

    // Store the event date for later use
    modal.dataset.eventDate = eventDate;
    modal.dataset.eventId = eventId;

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

    // Get the event date from the modal
    const modal = document.querySelector('.modal-overlay');
    const eventDate = modal.dataset.eventDate;

    // Parse the date string (format: YYYY-MM-DD) and create a date in local timezone
    const [year, month, day] = eventDate.split('-').map(Number);
    const dateTime = new Date(year, month - 1, day, hours24, minute, 0, 0);
    const time = dateTime.toISOString();

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
                await loadEvents(state.selectedGroup.id);
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
