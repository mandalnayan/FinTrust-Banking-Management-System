



package com.fintrust.transaction;
import java.util.Date ;
import java.util.HashMap;

import org.zkoss.zk.ui.Component;
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
import java.math.BigDecimal;
import java.security.MessageDigest;
import java.sql.*;
import java.text.SimpleDateFormat;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;
import java.util.Map;

public class UpdatedTransactionHistoryController extends SelectorComposer<Component> {
  
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
		transactions = transactionDAO.allCurrentUserTransactions(null,null);
	
       
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


    private String nullToDash(String value) {
        return (value == null || value.isEmpty()) ? "-" : value;
    }
    
   
//    @Listen("onClick=#btnDownloadPdf")




    @Listen("onClick=#btnDownloadPdf")
    public void downloadJasperPdf() {
            Clients.showNotification("cliecked");
        try {
            Long userId = (Long) Sessions.getCurrent().getAttribute("user_id");
            if (userId == null) {
                Messagebox.show("Session expired. Please login.", "Error", Messagebox.OK, Messagebox.EXCLAMATION);
                return;
            }

            // Date range (last 3 months)
            LocalDate end = LocalDate.now();
            LocalDate start = end.minusMonths(3);
          System.out.println("1");
            TransactionsDAOImpl transactionDAO = new TransactionsDAOImpl(DBConnection.getConnection());
            List<Transaction> txns = transactionDAO.allCurrentUserTransactions(null,null);
            System.out.println("1");
            BigDecimal running = BigDecimal.ZERO;
            BigDecimal totalCredit = BigDecimal.ZERO;
            BigDecimal totalDebit = BigDecimal.ZERO;
            System.out.println("1");
            List<Map<String, Object>> data = new ArrayList<>();
            for (Transaction t : txns) {
                BigDecimal debit = BigDecimal.ZERO;
                BigDecimal credit = BigDecimal.ZERO;
                System.out.println("1");
                if ("debit".equalsIgnoreCase(t.getTxnType())) {
                    debit = t.getAmount();
                    totalDebit = totalDebit.add(debit);
                    running = running.subtract(debit);
                } else if ("credit".equalsIgnoreCase(t.getTxnType())) {
                    credit = t.getAmount();
                    totalCredit = totalCredit.add(credit);
                    running = running.add(credit);
                }
                System.out.println("1");
                Map<String, Object> row = new HashMap<>();
                row.put("date", t.getCreatedAt());
                row.put("ref", String.valueOf(t.getTransactionId()));
                row.put("description", t.getDescription());
                row.put("debit", debit);
                row.put("credit", credit);
                row.put("balance", running);
                System.out.println("1");
                data.add(row);
            }
            System.out.println("1");
            JasperReport jasperReport = JasperCompileManager.compileReport("BankStatement.jrxml");

            Map<String, Object> params = new HashMap<>();
            params.put("accountNo", "ACC" + userId);
            params.put("customerName", "Vikas Maddheshiya");
            params.put("customerAddress", "Ward No 6, Uttar Pradesh");
            params.put("statementPeriod", start + " to " + end);
            params.put("openingBalance", BigDecimal.ZERO); // starting from 0 or fetch real opening balance
            params.put("totalCredit", totalCredit);
            params.put("totalDebit", totalDebit);
            params.put("closingBalance", running);
            System.out.println("1");
            JRBeanCollectionDataSource ds = new JRBeanCollectionDataSource(data);
            JasperPrint jasperPrint = JasperFillManager.fillReport(jasperReport, params, ds);

            ByteArrayOutputStream baos = new ByteArrayOutputStream();
            JasperExportManager.exportReportToPdfStream(jasperPrint, baos);

            Filedownload.save(baos.toByteArray(), "application/pdf",
                    "Statement_" + userId + "_" + start + "_" + end + ".pdf");
            System.out.println("1");
        } catch (Exception e) {
            e.printStackTrace();
            Messagebox.show("Error generating statement: " + e.getMessage(), "Error", Messagebox.OK, Messagebox.ERROR);
        }
    }


}




