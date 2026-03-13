@REM ----------------------------------------------------------------------------
@REM Maven Wrapper for Windows
@REM ----------------------------------------------------------------------------
@echo off
setlocal

set MAVEN_PROJECTBASEDIR=%~dp0
if "%MAVEN_PROJECTBASEDIR%"=="" set MAVEN_PROJECTBASEDIR=.
set MAVEN_PROJECTBASEDIR=%MAVEN_PROJECTBASEDIR:~0,-1%

set WRAPPER_JAR="%MAVEN_PROJECTBASEDIR%\.mvn\wrapper\maven-wrapper.jar"
set WRAPPER_PROPERTIES="%MAVEN_PROJECTBASEDIR%\.mvn\wrapper\maven-wrapper.properties"

if not exist %WRAPPER_JAR% (
  powershell -NoProfile -Command ^
    "$p='%WRAPPER_PROPERTIES%';" ^
    "$props=Get-Content $p | Where-Object {$_ -match '='};" ^
    "$map=@{}; foreach($line in $props){$k,$v=$line -split '=',2; $map[$k]=$v};" ^
    "$url=$map['wrapperUrl'];" ^
    "New-Item -ItemType Directory -Force -Path (Split-Path '%WRAPPER_JAR%') | Out-Null;" ^
    "Invoke-WebRequest -UseBasicParsing -Uri $url -OutFile '%WRAPPER_JAR%';"
)

for /f "tokens=*" %%i in ('powershell -NoProfile -Command ^
  "$props=Get-Content %WRAPPER_PROPERTIES% | Where-Object {$_ -match '^distributionUrl='};" ^
  "$props -replace '^distributionUrl=','' " ^
') do set MAVEN_WRAPPER_DISTRIBUTION_URL=%%i

set MAVEN_OPTS=%MAVEN_OPTS%

java -classpath %WRAPPER_JAR% -Dmaven.multiModuleProjectDirectory="%MAVEN_PROJECTBASEDIR%" ^
  org.apache.maven.wrapper.MavenWrapperMain -Dmaven.home="" -Dmaven.wrapper.properties="%WRAPPER_PROPERTIES%" %*

endlocal
