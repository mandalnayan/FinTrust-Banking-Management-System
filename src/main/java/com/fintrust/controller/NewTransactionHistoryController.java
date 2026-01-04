//package com.fintrust.controller;
//
//import com.fintrust.dao.impl.TransactionDAO;
//import com.fintrust.model.Transaction;
//
//import org.zkoss.zk.ui.Component;
//import org.zkoss.zk.ui.Sessions;
//import org.zkoss.zk.ui.select.SelectorComposer;
//import org.zkoss.zk.ui.select.annotation.Listen;
//import org.zkoss.zk.ui.select.annotation.Wire;
//import org.zkoss.zul.*;
//
//import java.util.List;
//
//public class TransactionHistoryController extends SelectorComposer<Component> {
//
//    private static final long serialVersionUID = 1123456776543234L;
//
//    @Wire
//    private Listbox transactionListbox;
//
//    @Wire
//    private Datebox fromDate;
//
//    @Wire
//    private Datebox toDate;
//
//    private TransactionDAO transactionDAO = new TransactionDAO();
//
//    @Override
//    public void doAfterCompose(Component comp) throws Exception {
//        super.doAfterCompose(comp);
//        loadTransactions(null, null);
//    }
//
//    @Listen("onClick = #filterBtn")
//    public void filterByDateRange() {
//
//        java.util.Date from = fromDate.getValue();
//        java.util.Date to = toDate.getValue();
//
//        if (from == null || to == null) {
//            Messagebox.show("Please select both From and To dates.");
//            return;
//        }
//
//        if (to.before(from)) {
//            Messagebox.show("'To Date' must be after 'From Date'.");
//            return;
//        }
//
//        loadTransactions(
//                new java.sql.Date(from.getTime()),
//                new java.sql.Date(to.getTime())
//        );
//    }
//   
//    private void loadTransactions(java.sql.Date from, java.sql.Date to) {
//
//        Long userId = (Long) Sessions.getCurrent().getAttribute("user_id");
//
//        transactionListbox.getItems().clear();
//
//        List<Transaction> transactions =
//                transactionDAO.getTransactions(userId, from, to);
//
//        for (Transaction t : transactions) {
//            Listitem item = new Listitem();
//            item.appendChild(new Listcell(String.valueOf(t.getTransactionId())));
//            item.appendChild(new Listcell(String.valueOf(t.getAccountNumber())));
//            
//            if(t.getCounterparty_account_number()==null)
//            	item.appendChild(new Listcell("-"));
//            else
//                item.appendChild(new Listcell(String.valueOf(t.getCounterparty_account_number())));
//            String amountIcon = "+", amountColor = "green";
//            
//            if (t.getTxnType().equalsIgnoreCase("debit")) {
//            	amountIcon = "-";
//            	amountColor = "red";
//            }
//            
//            String amount = amountIcon + String.format("%.2f", t.getAmount()) + "₹"; 
//            Listcell amountLC = new Listcell(amount);
//            amountLC.setStyle("color:" + amountColor);
//            item.appendChild(amountLC);
//            item.appendChild(new Listcell(t.getStatus()));
//            item.appendChild(new Listcell(t.getCreatedAt().toString()));
//            item.appendChild(new Listcell(t.getMode()));
//            item.appendChild(new Listcell(" "));
//            item.appendChild(new Listcell(t.getTxnType()));
//            transactionListbox.appendChild(item);
//        }
//    }
//}
//




package com.fintrust.controller;
import java.util.Date ;
import java.util.HashMap;

import org.zkoss.zk.ui.Component;
import org.zkoss.zk.ui.Executions;
import org.zkoss.zk.ui.Sessions;
import org.zkoss.zk.ui.select.SelectorComposer;
import org.zkoss.zk.ui.select.annotation.Wire;
import org.zkoss.zk.ui.util.Clients;
import org.zkoss.zk.ui.select.annotation.Listen;
import org.zkoss.zul.*;

import com.fintrust.dao.impl.TransactionsDAOImpl;
import com.fintrust.db.DBConnection;
import com.fintrust.model.Transaction;
import com.itextpdf.text.BadElementException;
import com.itextpdf.text.Document;
import com.itextpdf.text.Element;
import com.itextpdf.text.Font;
import com.itextpdf.text.FontFactory;
import com.itextpdf.text.Paragraph;
import com.itextpdf.text.Phrase;
import com.itextpdf.text.pdf.PdfPCell;
import com.itextpdf.text.pdf.PdfPTable;
import com.itextpdf.text.pdf.PdfWriter;

import net.sf.jasperreports.engine.JasperCompileManager;
import net.sf.jasperreports.engine.JasperExportManager;
import net.sf.jasperreports.engine.JasperFillManager;
import net.sf.jasperreports.engine.JasperPrint;
import net.sf.jasperreports.engine.JasperReport;
import net.sf.jasperreports.engine.data.JRBeanCollectionDataSource;

import java.io.ByteArrayOutputStream;
import java.io.InputStream;
import java.math.BigDecimal;
import java.security.MessageDigest;
import java.sql.*;
import java.text.SimpleDateFormat;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;
import java.util.Map;

public class NewTransactionHistoryController extends SelectorComposer<Component> {
  
    @Wire
    private Listbox transactionListbox;

    @Wire
    private Datebox fromDate;

    @Wire
    private Button btnDownloadPdf;
    
    @Wire
    private Datebox toDate;

    @Override
    public void doAfterCompose(Component comp) throws Exception {
        super.doAfterCompose(comp);
        loadTransactionData(null, null);
    }

  
    @Listen("onClick = #filterBtn")
    public void filterByDateRange() {
    	Clients.showNotification("sdsdv");
        java.util.Date from = fromDate.getValue();
        java.util.Date to = toDate.getValue();
       
        if (from == null || to == null) {
            Messagebox.show(" Please select both From and To dates.", "Missing Dates", Messagebox.OK, Messagebox.EXCLAMATION);
            return;
        }

        if (to.before(from)) {
            Messagebox.show(" 'To Date' must be after 'From Date'.", "Invalid Range", Messagebox.OK, Messagebox.EXCLAMATION);
            return;
        }

        loadTransactionData(new java.sql.Date(from.getTime()), new java.sql.Date(to.getTime()));
    }

   
    private void loadTransactionData(java.sql.Date from, java.sql.Date to) {
    	 TransactionsDAOImpl transactionDAO = new TransactionsDAOImpl(DBConnection.getConnection());
        Long userId = (Long) Sessions.getCurrent().getAttribute("user_id");
    	transactionListbox.getItems().clear();
    
       List<Transaction> transactions;
	try {
		transactions = transactionDAO.allCurrentUserTransactions(from,to);
	
       
      for (Transaction t : transactions) {
          Listitem item = new Listitem();
          item.appendChild(new Listcell(String.valueOf(t.getTransactionId())));
          item.appendChild(new Listcell(String.valueOf(t.getAccountNumber())));
          
          if(t.getCounterpartyAccountNumber()==null)
          	item.appendChild(new Listcell("-"));
          else
           item.appendChild(new Listcell(String.valueOf(t.getCounterpartyAccountNumber())));
           String amountIcon = "+", amountColor = "green";
          
          if (t.getTxnType().equalsIgnoreCase("debit")) {
          	amountIcon = "-";
          	amountColor = "red";
          }
          
          String amount = amountIcon + String.format("%.2f", t.getAmount()) + "₹"; 
          Listcell amountLC = new Listcell(amount);
          amountLC.setStyle("color:" + amountColor);
          item.appendChild(amountLC);
          item.appendChild(new Listcell(t.getStatus()));
          item.appendChild(new Listcell(t.getCreatedAt().toString()));
          item.appendChild(new Listcell(t.getMode()));
          item.appendChild(new Listcell(" "));
          item.appendChild(new Listcell(t.getTxnType()));
          transactionListbox.appendChild(item);
          
      }
	} catch (SQLException e) {
		// TODO Auto-generated catch block
		e.printStackTrace();
	}
    }
  
    @Listen("onClick = #btnDownloadPdf")
    public  void generateStatement() throws Exception {
    	   System.out.println("sdf");
    	        Connection con = DBConnection.getConnection();
    	       // Long userId = (Long) Sessions.getCurrent().getAttribute("user_id");

    	        Map<String, Object> params = fetchUserHeader(con, 5L);
    	        List<Transaction> transactions = fetchLastMonthTransactions(con, 5L);

    	        JasperReport report =
    	                JasperCompileManager.compileReport("bank_statement.jrxml");

    	        JasperPrint print =
    	                JasperFillManager.fillReport(
    	                        report,
    	                        params,
    	                        new JRBeanCollectionDataSource(transactions)
    	                );

    	        JasperExportManager.exportReportToPdfFile(
    	                print,
    	                "Bank_Statement_Last_Month.pdf"
    	        );

    	        con.close();
    	    }

    	    // ================= USER + USER_DETAILS =================
    	    private static Map<String, Object> fetchUserHeader(Connection con, long userId)
    	            throws SQLException {

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
    	        map.put("BANK_NAME", "ABC Bank Ltd.");
    	        map.put("FULL_NAME", rs.getString("full_name"));
    	        map.put("EMAIL", rs.getString("email"));
    	        map.put("PHONE", rs.getString("phone"));
    	        map.put("ACCOUNT_NUMBER", rs.getString("account_number"));

    	        String address = rs.getString("city") + ", "
    	                + rs.getString("state") + ", "
    	                + rs.getString("country") + " - "
    	                + rs.getString("pincode");

    	        map.put("ADDRESS", address);

    	        LocalDate now = LocalDate.now();
    	        LocalDate start = now.minusMonths(1).withDayOfMonth(1);
    	        LocalDate end = start.withDayOfMonth(start.lengthOfMonth());

    	        map.put("STATEMENT_PERIOD",
    	                start.format(DateTimeFormatter.ofPattern("dd-MMM-yyyy"))
    	                        + " to " +
    	                        end.format(DateTimeFormatter.ofPattern("dd-MMM-yyyy"))
    	        );

    	        return map;
    	    }

    	    // ================= TRANSACTIONS =================
    	    private static List<Transaction> fetchLastMonthTransactions(
    	            Connection con, long userId) throws SQLException {

    	        List<Transaction> list = new ArrayList<>();

    	        String sql =
    	                "SELECT * FROM transactions " +
    	                "WHERE user_id = ? " +
    	                "AND created_at >= DATE_FORMAT(CURDATE() - INTERVAL 1 MONTH, '%Y-%m-01') " +
    	                "AND created_at < DATE_FORMAT(CURDATE(), '%Y-%m-01') " +
    	                "ORDER BY created_at";

    	        PreparedStatement ps = con.prepareStatement(sql);
    	        ps.setLong(1, userId);

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

    	    // ================= MAIN =================
    	    public static void main(String[] args) throws Exception {
    	    	   System.out.println("sdf");
    	    	   NewTransactionHistoryController ob= new NewTransactionHistoryController();
    	       ob.generateStatement();
    	        System.out.println("sdf");

    	        System.out.println("PDF generated successfully");
    	        System.out.println("sdf");

    	    }
    
    
    
 



}










