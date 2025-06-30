# FixProtocol

# FIX Message Generator & Handler

Este projeto Java gera e processa mensagens FIX no formato usado pela B3 (mercado de ações). Utiliza a biblioteca [QuickFIX/J](https://www.quickfixj.org/) e é gerenciado com Maven.

## 📦 Funcionalidades

### Parte 1 – Geração de Massa FIX

A classe `FixExecutionReportGenerator`:

- Gera 5000 mensagens do tipo `ExecutionReport` (parciais e totais).
- Campos aleatórios respeitam as regras da B3.
- Salva todas as mensagens em `execution_reports.fix`.

### Parte 2 – Manipulação e Enriquecimento da Massa

A classe `FixExecutionHandler`:

- Lê `execution_reports.fix`
- Gera dois arquivos:
  - **AllMsgs.csv**: com métricas por mensagem (quantidade, notional etc.)
  - **FullFill.txt**: apenas execuções totais, enriquecidas com tags `1010` e `1011`

---

## 🛠️ Como executar

### 1. Pré-requisitos

- JDK 8+
- Maven
- Variáveis de ambiente configuradas

### 2. Clonar e executar

```bash
git clone https://github.com/AndreVMiguel/FixProtocol.git
```

 - Dentro de cada modulo existe um arquivo .bat para a execução do modulo, caso queira fazer manualmente primeiro set as variaveis de ambientes como no exemplo abaixo

```bash
set TOTAL_MESSAGES=5000
set TOTAL_ACCOUNTS=10
set TOTAL_SYMBOLS=10
set ACCOUNTS=ACC001,ACC002,ACC003,ACC004,ACC005,ACC006,ACC007,ACC008,ACC009,ACC010
set SYMBOLS=PETR4,VALE3,ITUB4,BBDC4,ABEV3,BBAS3,WEGE3,MGLU3,LREN3,RENT3
set TRADERS=JOSE,MARIA,PAULO,ANA,CARLOS
```

 - Em seguida execute o comando para o gerador

```bash
cd .\ReportGenerator\
mvn compile exec:java -Dexec.mainClass=FixExecutionReportGenerator
```

 - ou o comando abaixo para o update do report

```bash
cd .\UpdateReport\
mvn compile exec:java -Dexec.mainClass=FixExecutionHandler
```