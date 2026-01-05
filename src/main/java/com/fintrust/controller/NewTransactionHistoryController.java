package com.fintrust.controller;

import java.io.ByteArrayOutputStream;
import java.io.InputStream;
import java.sql.*;
import java.time.format.DateTimeFormatter;
import java.util.*;

import org.zkoss.util.media.AMedia;
import org.zkoss.zk.ui.Component;
import org.zkoss.zk.ui.Executions;
import org.zkoss.zk.ui.Sessions;
import org.zkoss.zk.ui.select.SelectorComposer;
import org.zkoss.zk.ui.select.annotation.Listen;
import org.zkoss.zk.ui.select.annotation.Wire;
import org.zkoss.zk.ui.util.Clients;
import org.zkoss.zul.*;

import com.fintrust.dao.impl.TransactionsDAOImpl;
import com.fintrust.db.DBConnection;
import com.fintrust.model.Transaction;

import net.sf.jasperreports.engine.*;
import net.sf.jasperreports.engine.data.JRBeanCollectionDataSource;

public class NewTransactionHistoryController extends SelectorComposer<Component> {

    @Wire
    private Listbox transactionListbox;

    @Wire
    private Datebox fromDate;

    @Wire
    private Datebox toDate;

    @Wire
    private Button btnDownloadPdf;

    @Override
    public void doAfterCompose(Component comp) throws Exception {
        super.doAfterCompose(comp);
        loadTransactionData(null, null);
    }

    // ================= FILTER LISTBOX =================
    @Listen("onClick = #filterBtn")
    public void filterByDateRange() {

        java.util.Date from = fromDate.getValue();
        java.util.Date to = toDate.getValue();

        if (from == null || to == null) {
            Messagebox.show("Please select both From and To dates.");
            return;
        }

        if (to.before(from)) {
            Messagebox.show("'To Date' must be after 'From Date'.");
            return;
        }

        loadTransactionData(
                new java.sql.Date(from.getTime()),
                new java.sql.Date(to.getTime())
        );
    }

    private void loadTransactionData(java.sql.Date from, java.sql.Date to) {

        TransactionsDAOImpl dao =
                new TransactionsDAOImpl(DBConnection.getConnection());

        transactionListbox.getItems().clear();

        try {
            List<Transaction> transactions =
                    dao.allCurrentUserTransactions(from, to);

            for (Transaction t : transactions) {

                Listitem item = new Listitem();
                item.appendChild(new Listcell(String.valueOf(t.getTransactionId())));
                item.appendChild(new Listcell(String.valueOf(t.getAccountNumber())));
                item.appendChild(new Listcell(
                        t.getCounterpartyAccountNumber() == null ? "-" :
                                String.valueOf(t.getCounterpartyAccountNumber())
                ));

                String sign = t.getTxnType().equalsIgnoreCase("debit") ? "-" : "+";
                String color = t.getTxnType().equalsIgnoreCase("debit") ? "red" : "green";

                Listcell amt = new Listcell(sign + t.getAmount() + " ₹");
                amt.setStyle("color:" + color);
                item.appendChild(amt);

                item.appendChild(new Listcell(t.getStatus()));
                item.appendChild(new Listcell(t.getCreatedAt().toString()));
                item.appendChild(new Listcell(t.getMode()));
                item.appendChild(new Listcell(t.getTxnType()));

                transactionListbox.appendChild(item);
            }

        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    // ================= DOWNLOAD PDF =================
    @Listen("onClick = #btnDownloadPdf")
    public void generateStatement() throws Exception {

        java.util.Date from = fromDate.getValue();
        java.util.Date to = toDate.getValue();

        if (from == null || to == null) {
            Messagebox.show("Please select From and To dates before downloading.");
            return;
        }

        if (to.before(from)) {
            Messagebox.show("'To Date' must be after 'From Date'.");
            return;
        }

        java.sql.Date sqlFrom = new java.sql.Date(from.getTime());
        java.sql.Date sqlTo = new java.sql.Date(to.getTime());

        Connection con = DBConnection.getConnection();
        Long userId = (Long) Sessions.getCurrent().getAttribute("user_id");

        Map<String, Object> params =
                fetchUserHeader(con, userId, sqlFrom, sqlTo);

        List<Transaction> transactions =
                fetchTransactionsByDateRange(con, userId, sqlFrom, sqlTo);

        InputStream reportStream =
                Executions.getCurrent()
                        .getDesktop()
                        .getWebApp()
                        .getResourceAsStream("/reports/bank_statement.jrxml");

        if (reportStream == null) {
            throw new RuntimeException("bank_statement.jrxml not found");
        }

        JasperReport report =
                JasperCompileManager.compileReport(reportStream);

        JasperPrint print =
                JasperFillManager.fillReport(
                        report,
                        params,
                        new JRBeanCollectionDataSource(transactions)
                );

        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        JasperExportManager.exportReportToPdfStream(print, baos);

        AMedia media = new AMedia(
                "Bank_Statement.pdf",
                "pdf",
                "application/pdf",
                baos.toByteArray()
        );

        Filedownload.save(media);
        con.close();
    }

    // ================= HEADER DATA =================
    private static Map<String, Object> fetchUserHeader(
            Connection con,
            long userId,
            java.sql.Date from,
            java.sql.Date to
    ) throws SQLException {

        String sql =
                "SELECT u.full_name, u.email, u.phone, t.account_number, " +
                "ud.country, ud.state, ud.city, ud.pincode " +
                "FROM users u " +
                "LEFT JOIN transactions t ON u.user_id = t.user_id " +
                "LEFT JOIN user_details ud ON u.user_id = ud.user_id " +
                "WHERE u.user_id = ? LIMIT 1";

        PreparedStatement ps = con.prepareStatement(sql);
        ps.setLong(1, userId);
        ResultSet rs = ps.executeQuery();
        rs.next();

        Map<String, Object> map = new HashMap<>();
        map.put("BANK_NAME", "FinTrust Bank Pvt. Ltd.");
        map.put("FULL_NAME", rs.getString("full_name"));
        map.put("EMAIL", rs.getString("email"));
        map.put("PHONE", rs.getString("phone"));
        map.put("ACCOUNT_NUMBER", rs.getString("account_number"));

        String address = rs.getString("city") + ", " +
                         rs.getString("state") + ", " +
                         rs.getString("country") + " - " +
                         rs.getString("pincode");

        map.put("ADDRESS", address);

        DateTimeFormatter fmt =
                DateTimeFormatter.ofPattern("dd-MMM-yyyy");

        map.put("STATEMENT_PERIOD",
                from.toLocalDate().format(fmt) +
                        " to " +
                        to.toLocalDate().format(fmt)
        );

        return map;
    }

    // ================= TRANSACTIONS =================
    private static List<Transaction> fetchTransactionsByDateRange(
            Connection con,
            long userId,
            java.sql.Date from,
            java.sql.Date to
    ) throws SQLException {

        List<Transaction> list = new ArrayList<>();

        String sql =
                "SELECT * FROM transactions " +
                "WHERE user_id = ? " +
                "AND DATE(created_at) BETWEEN ? AND ? " +
                "ORDER BY created_at";

        PreparedStatement ps = con.prepareStatement(sql);
        ps.setLong(1, userId);
        ps.setDate(2, from);
        ps.setDate(3, to);

        ResultSet rs = ps.executeQuery();

        while (rs.next()) {
            Transaction t = new Transaction();
            t.setTransactionId(rs.getLong("transaction_id"));
            t.setTxnReference(rs.getString("txn_reference"));
            t.setTxnType(rs.getString("txn_type"));
            t.setMode(rs.getString("mode"));
            t.setAmount(rs.getBigDecimal("amount"));
            t.setBalanceAfter(rs.getBigDecimal("balance_after"));
            t.setDescription(rs.getString("description"));
            t.setStatus(rs.getString("status"));

            Timestamp ts = rs.getTimestamp("created_at");
            if (ts != null) {
                t.setCreatedAt(ts.toLocalDateTime());
            }

            list.add(t);
        }
        return list;
    }
}
