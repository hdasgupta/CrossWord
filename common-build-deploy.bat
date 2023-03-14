@echo off
setlocal enabledelayedexpansion

set argCount=0

set ports=

for %%x in (%*) do Set /A argCount+=1
set argIndex=2 

set tag=%1  && shift /1
set toRemove= 

set outPort=

:routine
	set sourcePort=%1  && shift /1
  	set hostPort=%1  && shift /1
	set ports=%ports% -p !hostPort:%toRemove% =!:!sourcePort:%toRemove% =!
	
	set /a "argIndex=%argIndex%+2"

	if %sourcePort% == 80 (set outPort=%hostPort%)
	if %sourcePort% == 8080 (set outPort=%hostPort%)

	if %argIndex% lss %argCount% (goto :routine) else (goto :outrotine)

:outrotine

	set mvnCommand=.\mvnw clean install
	set dockerBuildCommand=docker build -t !tag:%toRemove% =! .
	set dockerStopCommand=docker stop -t 10 !tag:%toRemove% =!
	set dockerDropCommand=docker rm -f !tag:%toRemove% =!
	set dockerCreateCommand=docker create!ports:%toRemove% =! --name !tag:%toRemove% =! !tag:%toRemove% =!
	set dockerRunCommand=docker start !tag:%toRemove% =!
	set appURL=http://localhost:!outPort:%toRemove% =!

	echo Executing Maven Build Command... && %mvnCommand% && echo Executing Docker Image Build Command... && %dockerBuildCommand% && echo Deleting Container (If Exists) && %dockerStopCommand% & %dockerDropCommand% & echo Executing Docker Component Create Command... && %dockerCreateCommand% && echo Executing Docker Container Run Command && %dockerRunCommand% && echo Docker Component Created Successfully. && TIMEOUT 30 && echo Opening %appURL%... && start %appURL%

