@echo off
REM === Definição das variáveis de ambiente ===
set TOTAL_MESSAGES=5000
set TOTAL_ACCOUNTS=10
set TOTAL_SYMBOLS=10
set ACCOUNTS=ACC001,ACC002,ACC003,ACC004,ACC005,ACC006,ACC007,ACC008,ACC009,ACC010
set SYMBOLS=PETR4,VALE3,ITUB4,BBDC4,ABEV3,BBAS3,WEGE3,MGLU3,LREN3,RENT3
set TRADERS=JOSE,MARIA,PAULO,ANA,CARLOS

REM === Compila e executa com Maven ===
mvn compile exec:java -Dexec.mainClass=FixExecutionReportGenerator

pause