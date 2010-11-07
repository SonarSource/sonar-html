
rem run this script from webscanner folder

set SONAR_FLAGS=-Dsonar.web.sourceDirectory=src/main/webapp -Dsonar.language=web -Dsonar.dynamicAnalysis=false -Dsonar.jdbc.url=jdbc:postgresql://localhost/sonar -Dsonar.jdbc.driver=org.postgresql.Driver -Dsonar.jdbc.username=sonar -Dsonar.jdbc.password=sonar
set DEBUG=-X 

call C:\bin\sonar-2.3\bin\windows-x86-32\StopNTService.bat
call mvn install -Dmaven.test.skip
call xcopy /Y target\sonar-web-plugin-0.2-SNAPSHOT.jar c:\bin\sonar-2.3\extensions\plugins
call C:\bin\sonar-2.3\bin\windows-x86-32\StartNTService.bat

set mvncommand=mvn sonar:sonar

:mvn
rem 'ping' in order to wait a few seconds
ping 127.0.0.1 -n 10 -w 1000 > nul
rem try mvn sonar
call %mvncommand% -f C:\workspaces\tenderned\src\tenderned-web\pom.xml %SONAR_FLAGS% %DEBUG% > sonar-html.log
rem check if sonar was available
find "[INFO] Sonar server can not be reached" *.log
rem previous command will set errorlevel to 0 if the log contained "sonar can not be reached"
IF %ERRORLEVEL% == 0 GOTO mvn

echo Error Level  %ERRORLEVEL%
