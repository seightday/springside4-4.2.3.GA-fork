@echo off
echo [Pre-Requirement] Makesure install JDK 6.0+ and set the JAVA_HOME.
echo [Pre-Requirement] Makesure install Maven 3.0.3+ and set the PATH.

set MVN=mvn
set MAVEN_OPTS=%MAVEN_OPTS% -XX:MaxPermSize=128m

echo [Step 1] Install all springside modules and archetype to local maven repository.
cd modules
call %MVN% clean deploy -Dmaven.test.skip=true
if errorlevel 1 goto error

cd ..\support\maven-archetype 
call %MVN% clean deploy -Dmaven.test.skip=true
pause