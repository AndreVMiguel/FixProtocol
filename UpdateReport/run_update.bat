@echo off

REM === Compila e executa com Maven ===
mvn compile exec:java -Dexec.mainClass=FixExecutionHandler

pause