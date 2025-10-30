# Fix all entity files with correct table names

$mapping = @{
    'Sport' = 'sport'
    'League' = 'league'
    'Team' = 'team'
    'Player' = 'player'
    'Category' = 'category'
    'Event' = 'event'
    'Schedule' = 'schedule'
    'Venue' = 'venue'
    'Location' = 'location'
    'Source' = 'source'
    'Sportsbook' = 'sportsbook'
    'CategoryType' = 'category_type'
    'EventItem' = 'event_item'
    'EventScore' = 'event_score'
    'EventScoreItem' = 'event_score_item'
    'EventTime' = 'event_time'
    'EventVenue' = 'event_venue'
    'EventItemLeagueTeam' = 'event_item_league_team'
    'LeagueTeam' = 'league_team'
    'LeaguePlayer' = 'league_player'
    'LeaguePosition' = 'league_position'
    'LeagueTeamPlayer' = 'league_team_player'
    'LeagueEquivalent' = 'league_equivalent'
    'ScheduleCategory' = 'schedule_category'
    'LineserverSportsbook' = 'lineserver_sportsbook'
    'PresetMessage' = 'preset_message'
}

foreach ($className in $mapping.Keys) {
    $tableName = $mapping[$className]
    $file = "src\main\java\com\scheduletool\model\$className.java"
    
    if (Test-Path $file) {
        $content = Get-Content $file -Raw
        
        # Remove any existing @Table annotation and PowerShell code
        $content = $content -replace '@Table\(name = "[^"]+"\)', ''
        $content = $content -replace 'param\(\$match\)[^\n]*\n', ''
        $content = $content -replace "'\@Table\(name = '[^\n]*\n", ''
        $content = $content -replace '\+ \$match\.Groups\[1\]\.Value\.ToLower\(\) \+ .*\n', ''
        
        # Add correct @Table annotation before "public class"
        $content = $content -replace '(@Entity\s+)\s+\s+(public class)', "`$1`n@Table(name = `"$tableName`")`n`$2"
        
        $content | Set-Content $file -NoNewline
        Write-Host "Fixed: $className.java -> table: $tableName"
    }
}

Write-Host "`nAll entity files fixed with correct table names!"

