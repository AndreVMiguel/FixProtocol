import quickfix.Message;
import quickfix.field.Account;
import quickfix.field.AvgPx;
import quickfix.field.ClOrdID;
import quickfix.field.CumQty;
import quickfix.field.ExecID;
import quickfix.field.ExecType;
import quickfix.field.LastPx;
import quickfix.field.LastQty;
import quickfix.field.LeavesQty;
import quickfix.field.OrdStatus;
import quickfix.field.OrderID;
import quickfix.field.OrderQty;
import quickfix.field.Price;
import quickfix.field.Side;
import quickfix.field.Symbol;
import quickfix.field.TransactTime;
import quickfix.fix44.ExecutionReport;

import java.io.BufferedWriter;
import java.io.FileWriter;
import java.io.IOException;
import java.time.LocalDateTime;
import java.util.HashSet;
import java.util.Random;
import java.util.Set;
import java.util.UUID;

public class FixExecutionReportGenerator {

    private static final String SOH = "\u0001";
    private static final Random random = new Random();

    public static void main(String[] args) throws IOException {
        Config config = loadConfig();

        Set<String> orderIds = new HashSet<>();
        Set<String> clOrdIds = new HashSet<>();

        BufferedWriter writer = new BufferedWriter(new FileWriter("../execution_reports.fix"));

        for (int i = 0; i < config.totalMessages; i++) {
            boolean isTotal = i < (config.totalMessages / 2);
            Message msg = generateExecutionReport(orderIds, clOrdIds, isTotal, config);
            writer.write(msg.toString().replace("\u0001", SOH));
            writer.newLine();
        }

        writer.close();
        System.out.println("Arquivo 'execution_reports.fix' gerado com sucesso.");
    }

    private static Config loadConfig() throws IOException {
        Config config = new Config();
        config.totalMessages = Integer.parseInt(System.getenv().get("TOTAL_MESSAGES"));
        config.totalAccounts = Integer.parseInt(System.getenv().get("TOTAL_ACCOUNTS"));
        config.totalSymbols = Integer.parseInt(System.getenv().get("TOTAL_SYMBOLS"));

        config.accounts = System.getenv("ACCOUNTS")
                .split(",");

        config.symbols = System.getenv("SYMBOLS")
                .split(",");

        config.traders =System.getenv("TRADERS")
                .split(",");

        return config;
    }

    private static Message generateExecutionReport(Set<String> orderIds, Set<String> clOrdIds, boolean isTotal, Config config) {
        String orderId = generateUniqueId(orderIds);
        String clOrdId = generateUniqueId(clOrdIds);

        String account = config.accounts[random.nextInt(config.totalAccounts)];
        String symbol = config.symbols[random.nextInt(config.totalSymbols)];
        char side = random.nextBoolean() ? Side.BUY : Side.SELL;
        double lastPrice = 10 + (500 - 10) * random.nextDouble();
        double price = 10 + (500 - 10) * random.nextDouble();
        int totalQty = 100 + random.nextInt(900);

        int lastQty = isTotal ? 0 : random.nextInt(totalQty);
        int qty = isTotal ? totalQty : random.nextInt(totalQty-1)+1;
        int cumQty = isTotal ? totalQty : random.nextInt(qty);
        int leavesQty = qty - cumQty;
        double avgPx = (price + lastPrice)/2;

        ExecutionReport report = new ExecutionReport(
                new OrderID(orderId),
                new ExecID(UUID.randomUUID().toString()),
                new ExecType(ExecType.FILL),
                new OrdStatus(isTotal ? OrdStatus.FILLED : OrdStatus.PARTIALLY_FILLED),
                new Side(side),
                new LeavesQty(leavesQty),
                new CumQty(cumQty),
                new AvgPx(avgPx)
        );

        report.set(new ClOrdID(clOrdId));
        report.set(new Symbol(symbol));
        report.set(new Account(account));
        report.set(new OrderQty(qty));
        report.set(new Price(price));
        report.set(new LastQty(lastQty));
        report.set(new LastPx(lastPrice));
        report.set(new TransactTime(LocalDateTime.now()));

        report.setString(448, config.traders[random.nextInt(config.traders.length)]);
        report.setInt(452, 1);

        return report;
    }

    private static String generateUniqueId(Set<String> existing) {
        String id;
        do {
            id = UUID.randomUUID().toString();
        } while (!existing.add(id));
        return id;
    }

    private static class Config {
        int totalMessages;
        int totalAccounts;
        int totalSymbols;
        String[] accounts;
        String[] symbols;
        String[] traders;
    }
}
