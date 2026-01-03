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
    public void downloadBankStatement() {

        Long userId = (Long) Sessions.getCurrent().getAttribute("user_id");
        java.util.Date to = new java.util.Date();

        // 3 months ago
        LocalDate fromLocal = LocalDate.now().minusMonths(3);
        java.util.Date from = java.sql.Date.valueOf(fromLocal);

        TransactionsDAOImpl dao = new TransactionsDAOImpl(DBConnection.getConnection());
        List<Transaction> transactions;
        try {
            transactions = dao.allCurrentUserTransactions(new java.sql.Date(from.getTime()), new java.sql.Date(to.getTime()));

            // Prepare safe fields for JasperReports
            List<Map<String, Object>> reportData = new ArrayList<>();
            for (Transaction t : transactions) {
                Map<String, Object> map = new HashMap<>();
                map.put("transactionId", t.getTransactionId());
                map.put("accountNumberSafe", t.getAccountNumber() != null ? t.getAccountNumber().toString() : "-");
                map.put("counterpartyAccountNumberSafe", t.getCounterpartyAccountNumber() != null ? t.getCounterpartyAccountNumber().toString() : "-");
                map.put("amountFormatted", (t.getTxnType().equalsIgnoreCase("debit") ? "-" : "+") + String.format("%.2f₹", t.getAmount()));
                map.put("status", t.getStatus());
                map.put("createdAt", t.getCreatedAt());
                map.put("mode", t.getMode());
                map.put("txnType", t.getTxnType());
                map.put("balanceAfterFormatted", t.getBalanceAfter() != null ? String.format("%.2f₹", t.getBalanceAfter()) : "-");
                reportData.add(map);
            }

            // Compile Jasper report from the jrxml file path inside your web app
            String jrxmlPath = Executions.getCurrent().getDesktop().getWebApp().getRealPath("/WEB-INF/resources/bank_statement.jrxml");
            if (jrxmlPath == null) {
                Messagebox.show("Report template path not found!", "Error", Messagebox.OK, Messagebox.ERROR);
                return;
            }
            JasperReport jasperReport = JasperCompileManager.compileReport(jrxmlPath);

            JRBeanCollectionDataSource dataSource = new JRBeanCollectionDataSource(reportData);

            Map<String, Object> parameters = new HashMap<>();
            parameters.put("REPORT_TITLE", "Last 3 Months Bank Statement");
            parameters.put("FROM_DATE", from);
            parameters.put("TO_DATE", to);
            parameters.put("USER_NAME", Sessions.getCurrent().getAttribute("user_name"));
            parameters.put("ACCOUNT_NUMBER", Sessions.getCurrent().getAttribute("primary_account_id").toString());

            JasperPrint jasperPrint = JasperFillManager.fillReport(jasperReport, parameters, dataSource);

            // Export to PDF
            ByteArrayOutputStream pdfStream = new ByteArrayOutputStream();
            JasperExportManager.exportReportToPdfStream(jasperPrint, pdfStream);

            // Send PDF to client browser
            Filedownload.save(pdfStream.toByteArray(), "application/pdf", "Last3MonthsStatement.pdf");

        } catch (Exception e) {
            e.printStackTrace();
            Messagebox.show("Failed to generate PDF: " + e.getMessage(), "Error", Messagebox.OK, Messagebox.ERROR);
        }
    }




}










