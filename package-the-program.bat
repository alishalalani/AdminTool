@echo Started: %date% %time%
jpackage --type msi --app-version "4.2" --win-console --win-shortcut --win-shortcut-prompt --input .\dist --dest . --main-jar .\ScheduleTool.jar --main-class scheduletool.Main --name "ScheduleTool"
@echo Completed: %date% %time%
pause