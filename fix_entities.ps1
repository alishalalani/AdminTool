# Fix corrupted entity files by removing PowerShell code and adding proper @Table annotations

$files = Get-ChildItem -Path "src\main\java\com\scheduletool\model" -Filter "*.java"

foreach ($file in $files) {
    $lines = Get-Content $file.FullName
    $newLines = @()
    $skipNext = $false
    
    for ($i = 0; $i -lt $lines.Count; $i++) {
        $line = $lines[$i]
        
        # Skip lines with PowerShell code
        if ($line -match 'param\(\$match\)' -or $line -match "'\@Table\(name" -or $line -match '\+ \$match\.Groups') {
            $skipNext = $true
            continue
        }
        
        # If we skipped PowerShell code and now see "public class", add @Table annotation
        if ($skipNext -and $line -match 'public class (\w+)') {
            $className = $matches[1]
            # Convert class name to lowercase table name (handle underscores)
            $tableName = $className -creplace '([A-Z])', '_$1' -replace '^_', '' -replace '([a-z])([A-Z])', '$1_$2'
            $tableName = $tableName.ToLower()
            
            $newLines += "@Table(name = `"$tableName`")"
            $newLines += $line
            $skipNext = $false
        }
        else {
            $newLines += $line
        }
    }
    
    $newLines | Set-Content $file.FullName
    Write-Host "Fixed: $($file.Name)"
}

Write-Host "`nAll entity files fixed!"

