@echo off
setlocal enabledelayedexpansion

set ROOT_DIR=%~dp0..
set OUT_DIR=%ROOT_DIR%\out
set BUILD_DIR=%ROOT_DIR%\build
set INPUT_DIR=%BUILD_DIR%\jpackage-input
set DIST_DIR=%ROOT_DIR%\dist
set APP_NAME=PIMS
set MAIN_CLASS=ui.Main
set MAIN_JAR=pims.jar
set JDBC_JAR=%ROOT_DIR%\lib\mysql-connector-j.jar

if not exist "%JDBC_JAR%" (
    echo Missing JDBC driver: %JDBC_JAR%
    exit /b 1
)

if exist "%OUT_DIR%" rmdir /s /q "%OUT_DIR%"
if exist "%INPUT_DIR%" rmdir /s /q "%INPUT_DIR%"
if exist "%DIST_DIR%\%APP_NAME%" rmdir /s /q "%DIST_DIR%\%APP_NAME%"
if exist "%DIST_DIR%\%APP_NAME%-1.0.exe" del /q "%DIST_DIR%\%APP_NAME%-1.0.exe"

mkdir "%OUT_DIR%"
mkdir "%INPUT_DIR%"
mkdir "%DIST_DIR%"

echo Compiling application...
for /r "%ROOT_DIR%\src" %%f in (*.java) do (
    set SOURCES=!SOURCES! "%%f"
)

javac -cp "%JDBC_JAR%" -d "%OUT_DIR%" !SOURCES!
if errorlevel 1 exit /b 1

echo Creating runnable jar...
jar --create --file "%INPUT_DIR%\%MAIN_JAR%" --main-class "%MAIN_CLASS%" -C "%OUT_DIR%" .
if errorlevel 1 exit /b 1

copy "%JDBC_JAR%" "%INPUT_DIR%\" >nul
if errorlevel 1 exit /b 1

echo Packaging Windows executable...
jpackage ^
    --type exe ^
    --name "%APP_NAME%" ^
    --app-version "1.0" ^
    --input "%INPUT_DIR%" ^
    --main-jar "%MAIN_JAR%" ^
    --main-class "%MAIN_CLASS%" ^
    --dest "%DIST_DIR%"

if errorlevel 1 exit /b 1

echo Windows executable created at: %DIST_DIR%\%APP_NAME%-1.0.exe
