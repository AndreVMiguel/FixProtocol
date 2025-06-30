import quickfix.Message;
import quickfix.field.ExecType;
import quickfix.field.OrdStatus;
import quickfix.field.Side;
import quickfix.fix44.ExecutionReport;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

public class FixExecutionHandler {

    private static final String SOH = "\u0001";

    public static void main(String[] args) throws Exception {
        List<String> lines = readFixMessages("../execution_reports.fix");
        generateAllMsgsCsv(lines);
        generateFullFillTxt(lines);
        System.out.println("Arquivos AllMsgs.csv e FullFill.txt gerados com sucesso.");
    }

    private static List<String> readFixMessages(String path) throws IOException {
        List<String> lines = new ArrayList<>();
        try (BufferedReader reader = new BufferedReader(new FileReader(path))) {
            String line;
            while ((line = reader.readLine()) != null) {
                lines.add(line);
            }
        }
        return lines;
    }

    private static void generateAllMsgsCsv(List<String> lines) throws Exception {
        try (BufferedWriter csvWriter = new BufferedWriter(new FileWriter("../AllMsgs.csv"))) {
            csvWriter.write("Horario;Conta;Instrumento;Lado;Qtd Ordem;Qtd Execucao Atual;Qtd Exec Acumulada;Preco Executado;Notional Ordem;Notional Exec Atual;Notional Exec Acumulada;Entering Trader");
            csvWriter.newLine();

            for (String line : lines) {
                String fixMsg = line.replace(SOH, "\u0001");
                Message message = new ExecutionReport();
                message.fromString(fixMsg, null, false);

                String transactTime = message.getString(60);
                String account = message.getString(1);
                String symbol = message.getString(55);
                char side = message.getChar(54);
                int orderQty = message.getInt(38);
                int lastQty = message.getInt(32);
                int cumQty = message.getInt(14);
                double avgPx = message.getDouble(6);
                double notionalOrdem = orderQty * avgPx;
                double notionalAtual = lastQty * avgPx;
                double notionalAcumulado = cumQty * avgPx;
                String trader = message.getString(448);

                csvWriter.write(String.join(";",
                        transactTime,
                        account,
                        symbol,
                        side == Side.BUY ? "Buy" : "Sell",
                        String.valueOf(orderQty),
                        String.valueOf(lastQty),
                        String.valueOf(cumQty),
                        String.valueOf(avgPx),
                        String.valueOf(notionalOrdem),
                        String.valueOf(notionalAtual),
                        String.valueOf(notionalAcumulado),
                        trader));
                csvWriter.newLine();
            }
        }
    }

    private static void generateFullFillTxt(List<String> lines) throws Exception {
        try (BufferedWriter txtWriter = new BufferedWriter(new FileWriter("../FullFill.txt"))) {
            for (String line : lines) {
                String fixMsg = line.replace(SOH, "\u0001");
                Message message = new ExecutionReport();
                message.fromString(fixMsg, null, false);

                char ordStatus = message.getChar(39);
                char execType = message.getChar(150);

                if (ordStatus == OrdStatus.FILLED && execType == ExecType.FILL) {
                    int orderQty = message.getInt(38);
                    double avgPx = message.getDouble(6);
                    double notionalOrdem = orderQty * avgPx;
                    String trader = message.getString(448);

                    message.setDouble(1010, notionalOrdem);
                    message.setString(1011, trader);

                    txtWriter.write(message.toString().replace("\u0001", SOH));
                    txtWriter.newLine();
                }
            }
        }
    }
}