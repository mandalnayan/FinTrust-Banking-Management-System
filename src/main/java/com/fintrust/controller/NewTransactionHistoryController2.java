package com.fintrust.controller;

import java.io.ByteArrayOutputStream;
import java.io.File;
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
import org.zkoss.zkex.zul.Jasperreport;
import org.zkoss.zul.*;

import com.fintrust.dao.impl.TransactionsDAOImpl;
import com.fintrust.db.DBConnection;
import com.fintrust.model.Transaction;

import net.sf.jasperreports.engine.*;
import net.sf.jasperreports.engine.data.JRBeanCollectionDataSource;

public class NewTransactionHistoryController2 extends SelectorComposer<Component> {

    @Wire
    private Listbox transactionListbox;

    @Wire
    private Datebox fromDate;

    @Wire
    private Datebox toDate;

    @Wire
    private Button btnDownloadPdf;
    
    @Wire 
    private Jasperreport report; 

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
                String remaningAmount = t.getBalanceAfter() + "";
               
                Listcell remaningAmountCell = new Listcell(!remaningAmount.equals(null) ? remaningAmount : "Failed to load");
                remaningAmountCell.setStyle("color:" + color);
                item.appendChild(remaningAmountCell);
                
                transactionListbox.appendChild(item);
            }

        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    // ================= DOWNLOAD PDF =================
    @Listen("onClick = #btnDownloadPdf")
    public void generateStatement() throws Exception {
    	Window win = (Window) Executions.createComponents("/transactionReport.zul", null, null);
		win.doModal();
    	
    	
//    	report.setVisible(true);
//    	
//        java.util.Date from = fromDate.getValue();
//        java.util.Date to = toDate.getValue();
//
//        if (from == null || to == null) {
//            Messagebox.show("Please select From and To dates before downloading.");
//            return;
//        }
//
//        if (to.before(from)) {
//            Messagebox.show("'To Date' must be after 'From Date'.");
//            return;
//        }
//
//        java.sql.Date sqlFrom = new java.sql.Date(from.getTime());
//        java.sql.Date sqlTo = new java.sql.Date(to.getTime());
//
//        Connection con = DBConnection.getConnection();
//        Long userId = (Long) Sessions.getCurrent().getAttribute("user_id");
//        TransactionsDAOImpl dao =
//                new TransactionsDAOImpl(DBConnection.getConnection());
//        Map<String, Object> params =
//                dao.fetchUserHeader(con, userId, sqlFrom, sqlTo);
//        
//        List<Transaction> transactions =
//        		dao.fetchTransactionsByDateRange(con, userId, sqlFrom, sqlTo);
//        
//        JRBeanCollectionDataSource dataSource =
//                new JRBeanCollectionDataSource(transactions);
//
//        File jrxmlFile = new File(
//                Sessions.getCurrent()
//                        .getWebApp()
//                        .getRealPath("/WEB-INF/resources/transactions_jasper_report.jrxml")
//        );
//
//        String jasperPath =
//                Sessions.getCurrent()
//                        .getWebApp()
//                        .getRealPath("/WEB-INF/resources/transactions_jasper_report.jasper");
//
//        JasperCompileManager.compileReportToFile(
//                jrxmlFile.getAbsolutePath(),
//                jasperPath
//        );
//
//        Map<String, Object> parameters = new HashMap<>();
//        
//        // Column header
//        parameters.put("column1", "Transaction Id");
//        parameters.put("column2", "From Account");
//        parameters.put("column3", "CounterParty Account");
//        parameters.put("column4", "Transaction Type");
//        parameters.put("column5", "Balance"); 
//        
//        parameters.put("name", "Nayan Mandal");
//        parameters.put("address", "Delhi, NCR 878654");
//        parameters.put("duration", "01-Jan-2025 to 31-Jan-2025");
//        parameters.put("accountNo", 123456789012l);
//        
//        // Summary information
//        parameters.put("totalBalance", 5670.9);
//
//        report.setSrc("/WEB-INF/resources/transactions_jasper_report.jasper");
//        report.setParameters(parameters);
//        report.setDatasource(dataSource);
//        report.setType("pdf");
        
    }

 
}
