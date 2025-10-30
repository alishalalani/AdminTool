# Fix all @Table annotations to use lowercase table names

$files = Get-ChildItem -Path "src\main\java\com\scheduletool\model" -Filter "*.java"

foreach ($file in $files) {
    $content = Get-Content $file.FullName -Raw
    
    # Fix simple table names (e.g., "Sport" -> "sport")
    $content = $content -replace '@Table\(name = "([A-Z][a-z]+)"\)', { 
        param($match)
        '@Table(name = "' + $match.Groups[1].Value.ToLower() + '")'
    }
    
    # Fix table names with underscores (e.g., "Event_Item" -> "event_item")
    $content = $content -replace '@Table\(name = "([A-Z][a-z]+_[A-Z][a-z]+)"\)', {
        param($match)
        '@Table(name = "' + $match.Groups[1].Value.ToLower() + '")'
    }
    
    # Fix table names with underscores and additional parameters
    $content = $content -replace '@Table\(name = "([A-Z][a-z]+_[A-Z][a-z]+)"', {
        param($match)
        '@Table(name = "' + $match.Groups[1].Value.ToLower() + '"'
    }
    
    Set-Content -Path $file.FullName -Value $content -NoNewline
    Write-Host "Fixed: $($file.Name)"
}

Write-Host "`nAll table names converted to lowercase!"

