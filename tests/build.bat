
rem run this script from web folder

set SONAR_HOME=C:\bin\sonar-2.8
set SONAR_FLAGS=-Dsonar.language=web -Dsonar.dynamicAnalysis=false 
set DEBUG=-X 

call %M2_HOME%\bin\mvn install -Dmaven.test.skip
call xcopy /Y target\sonar-web-plugin-*-SNAPSHOT.jar %SONAR_HOME%\extensions\plugins
start "Sonar Server" /MIN %SONAR_HOME%\bin\windows-x86-64\StartSonar.bat 

set mvncommand=%M2_HOME%\bin\mvn sonar:sonar

:mvn
rem 'ping' in order to wait a few seconds
ping 127.0.0.1 -n 10 -w 1000 > nul
rem try mvn sonar
call %mvncommand% -f c:\workspaces\webplugin-test\testweb\alfresco-web-client\pom.xml %SONAR_FLAGS% %DEBUG% > sonar-alfresco.log
rem check if sonar was available
find "Sonar server can not be reached" *.log
rem previous command will set errorlevel to 0 if the log contained "sonar can not be reached"
IF %ERRORLEVEL% == 0 GOTO mvn

echo Error Level  %ERRORLEVEL%
