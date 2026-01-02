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

import org.zkoss.zk.ui.Component;
import org.zkoss.zk.ui.Sessions;
import org.zkoss.zk.ui.select.SelectorComposer;
import org.zkoss.zk.ui.select.annotation.Wire;
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



import java.io.ByteArrayOutputStream;
import java.security.MessageDigest;
import java.sql.*;
import java.text.SimpleDateFormat;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

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
		transactions = transactionDAO.allCurrentUserTransactions();
	
       
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
//    
//  @Listen("onClick = #filterBtn")
//  public void filterByDateRange() {
//
//      java.util.Date from = fromDate.getValue();
//      java.util.Date to = toDate.getValue();
//
//      if (from == null || to == null) {
//          Messagebox.show("Please select both From and To dates.");
//          return;
//      }
//
//      if (to.before(from)) {
//          Messagebox.show("'To Date' must be after 'From Date'.");
//          return;
//      }
//
//      loadTransactions(
//              new java.sql.Date(from.getTime()),
//              new java.sql.Date(to.getTime())
//      );
//  }
//     
//  
//  
//        if (from != null && to != null) {
//            query += " WHERE DATE(created_at) BETWEEN ? AND ?";
//        }
//          
//        query += " ORDER BY created_at DESC";
//
//        try (Connection con = DBConnection.getConnection();
//             PreparedStatement ps = con.prepareStatement(query)) {
//
//            if (from != null && to != null) {
//                ps.setDate(1, from);
//                ps.setDate(2, to);
//            }
//
//            ResultSet rs = ps.executeQuery();
//            List<Listitem> items = new ArrayList<>();
//
//            while (rs.next()) {
//                Listitem item = new Listitem();
//                item.appendChild(new Listcell(String.valueOf(rs.getLong("transaction_id"))));
//                item.appendChild(new Listcell(nullToDash(rs.getString("from_account"))));
//                item.appendChild(new Listcell(nullToDash(rs.getString("to_account"))));
//                item.appendChild(new Listcell(String.format("%.2f", rs.getDouble("amount"))));
//                item.appendChild(new Listcell(rs.getString("transaction_type")));
//                item.appendChild(new Listcell(rs.getString("status")));
//                item.appendChild(new Listcell(nullToDash(rs.getString("remarks"))));
//                item.appendChild(new Listcell(rs.getTimestamp("created_at").toString()));
//                items.add(item);
//            }
//
//            transactionListbox.getItems().addAll(items);
//
//            System.out.println("Transactions loaded: " + items.size());
//
//        } catch (SQLException e) {
//            e.printStackTrace();
//            Messagebox.show( e.getMessage(),
//                    "Database Error", Messagebox.OK, Messagebox.ERROR);
//        }
//    }

    private String nullToDash(String value) {
        return (value == null || value.isEmpty()) ? "-" : value;
    }
    
    
    @Listen("onClick=#btnDownloadPdf")
    public void downloadPdf() {
        // This method generates a bank-style statement PDF for last 3 months.
        // TODO: replace getLoggedAccountNumber() with your session/account retrieval logic.
        String accountNo = "ACC1001";      //getLoggedAccountNumber(); // e.g., "123456789012"//BY SESSION
        if (accountNo == null) {
            Messagebox.show("Account not found. Please login.", "Error", Messagebox.OK, Messagebox.EXCLAMATION);
            return;
        }

        try {
            // 1) Calculate date range (last 3 months)
            LocalDate endLocal = LocalDate.now();
            LocalDate startLocal = endLocal.minusMonths(3);
            java.sql.Date startDate = java.sql.Date.valueOf(startLocal);
            java.sql.Date endDate = java.sql.Date.valueOf(endLocal);

            // 2) Fetch customer/account details (replace SQL per your schema)
            String custName = "vikas maddheshiya";
            String custAddress = "ward numbe 6 , uttar pradesh";
            String branch = "Mumbai";
            String ifsc = "FIN54674367676675";
            try (Connection con = DBConnection.getConnection();
                 PreparedStatement ps = con.prepareStatement(
                         "SELECT cust_name, cust_address, branch_name, ifsc FROM account_master WHERE account_no = ?")) {
                ps.setString(1, accountNo);
                try (ResultSet rs = ps.executeQuery()) {
                    if (rs.next()) {
                        custName = rs.getString("cust_name");
                        custAddress = rs.getString("cust_address");
                        branch = rs.getString("branch_name");
                        ifsc = rs.getString("ifsc");
                    }
                }
            } catch (SQLException ex) {
                // If account_master not present, continue with defaults
                ex.printStackTrace();
            }

            // 3) Compute opening balance (sum up to day before startDate)
            double openingBalance = 0.0;
            try (Connection con = DBConnection.getConnection();
                 PreparedStatement ps = con.prepareStatement(
                         // Example logic: if 'to_account' is this account -> credit, if 'from_account' -> debit.
                         "SELECT SUM(CASE WHEN to_account = ? THEN amount WHEN from_account = ? THEN -amount ELSE 0 END) as bal " +
                                 "FROM transactions WHERE DATE(created_at) < ?")) {
                ps.setString(1, accountNo);
                ps.setString(2, accountNo);
                ps.setDate(3, startDate);
                try (ResultSet rs = ps.executeQuery()) {
                    if (rs.next()) openingBalance = rs.getDouble("bal");
                }
            }

            // 4) Fetch transactions in the range ordered by created_at ASC
            class Txn { Date date; String ref; String desc; double debit; double credit; }
            List<Txn> txns = new ArrayList<>();
            try (Connection con = DBConnection.getConnection();
                 PreparedStatement ps = con.prepareStatement(
                         "SELECT transaction_id, from_account, to_account, amount, transaction_type, remarks, created_at " +
                                 "FROM transactions WHERE DATE(created_at) BETWEEN ? AND ? AND (from_account = ? OR to_account = ?) ORDER BY created_at ASC")) {
                ps.setDate(1, startDate);
                ps.setDate(2, endDate);
                ps.setString(3, accountNo);
                ps.setString(4, accountNo);

                try (ResultSet rs = ps.executeQuery()) {
                    while (rs.next()) {
                        Txn t = new Txn();
                        t.date = (Date) new java.util.Date(rs.getDate("created_at").getTime());

                        String from = rs.getString("from_account");
                        String to = rs.getString("to_account");
                        double amt = rs.getDouble("amount");
                        // if this account is credited
                        if (accountNo.equalsIgnoreCase(to)) {
                            t.credit = amt;
                            t.debit = 0.0;
                        } else {
                            t.debit = amt;
                            t.credit = 0.0;
                        }
                        // make a readable narration
                        String txId = rs.getString("transaction_id");
                        String narr = rs.getString("remarks");
                        String type = rs.getString("transaction_type");
                        t.ref = txId == null ? "-" : txId;
                        t.desc = String.format(Locale.ENGLISH, "%s %s", (narr == null ? "" : narr), (type == null ? "" : "(" + type + ")")).trim();
                        txns.add(t);
                    }
                }
            }

            // 5) Totals and running balance
            double totalCredit = 0.0, totalDebit = 0.0;
            double running = openingBalance;
            for (Txn t : txns) {
                totalCredit += t.credit;
                totalDebit += t.debit;
            }
            double closingBalance = openingBalance + totalCredit - totalDebit;

            // 6) Build PDF using iText 5
            ByteArrayOutputStream baos = new ByteArrayOutputStream();
            Document doc = new Document(); // default A4
            PdfWriter.getInstance(doc, baos);
            doc.open();

            // Bank header
            Font h1 = FontFactory.getFont(FontFactory.HELVETICA_BOLD, 14);
            Font h2 = FontFactory.getFont(FontFactory.HELVETICA, 10);
            Paragraph bankName = new Paragraph("FinTrust Bank Pvt. Ltd.", h1);
            bankName.setAlignment(Element.ALIGN_LEFT);
            doc.add(bankName);

            Paragraph branchPara = new Paragraph(branch + " | IFSC: " + ifsc, h2);
            branchPara.setSpacingAfter(6f);
            doc.add(branchPara);

            // Customer block (two columns)
            PdfPTable custTable = new PdfPTable(2);
            custTable.setWidthPercentage(100);
            custTable.setSpacingBefore(6f);
            custTable.setSpacingAfter(8f);
            custTable.setWidths(new float[]{2f, 3f});

            PdfPCell c1 = new PdfPCell(new Phrase("Account Holder:", FontFactory.getFont(FontFactory.HELVETICA_BOLD, 9)));
            c1.setBorder(PdfPCell.NO_BORDER);
            custTable.addCell(c1);
            PdfPCell c2 = new PdfPCell(new Phrase(custName + "\n" + custAddress, FontFactory.getFont(FontFactory.HELVETICA, 9)));
            c2.setBorder(PdfPCell.NO_BORDER);
            custTable.addCell(c2);

            PdfPCell c3 = new PdfPCell(new Phrase("Account No:", FontFactory.getFont(FontFactory.HELVETICA_BOLD, 9)));
            c3.setBorder(PdfPCell.NO_BORDER);
            custTable.addCell(c3);
            PdfPCell c4 = new PdfPCell(new Phrase(accountNo, FontFactory.getFont(FontFactory.HELVETICA, 9)));
            c4.setBorder(PdfPCell.NO_BORDER);
            custTable.addCell(c4);

            PdfPCell c5 = new PdfPCell(new Phrase("Statement Period:", FontFactory.getFont(FontFactory.HELVETICA_BOLD, 9)));
            c5.setBorder(PdfPCell.NO_BORDER);
            custTable.addCell(c5);
            SimpleDateFormat df = new SimpleDateFormat("dd-MMM-yyyy");
            PdfPCell c6 = new PdfPCell(new Phrase(df.format(startDate) + " to " + df.format(endDate), FontFactory.getFont(FontFactory.HELVETICA, 9)));
            c6.setBorder(PdfPCell.NO_BORDER);
            custTable.addCell(c6);

            doc.add(custTable);

            // Summary Table
            PdfPTable sumTable = new PdfPTable(4);
            sumTable.setWidthPercentage(100);
            sumTable.setSpacingBefore(6f);
            sumTable.setSpacingAfter(8f);
            sumTable.addCell(getCell("Opening Balance", PdfPCell.NO_BORDER, true));
            sumTable.addCell(getCell(String.format("%.2f", openingBalance), PdfPCell.NO_BORDER, false));
            sumTable.addCell(getCell("Total Credit", PdfPCell.NO_BORDER, true));
            sumTable.addCell(getCell(String.format("%.2f", totalCredit), PdfPCell.NO_BORDER, false));
            sumTable.addCell(getCell("Total Debit", PdfPCell.NO_BORDER, true));
            sumTable.addCell(getCell(String.format("%.2f", totalDebit), PdfPCell.NO_BORDER, false));
            sumTable.addCell(getCell("Closing Balance", PdfPCell.NO_BORDER, true));
            sumTable.addCell(getCell(String.format("%.2f", closingBalance), PdfPCell.NO_BORDER, false));
            doc.add(sumTable);

            // Transaction table header
            PdfPTable t = new PdfPTable(new float[]{1.2f, 1.6f, 3f, 1.8f, 1.2f, 1.2f});
            t.setWidthPercentage(100);
            t.setSpacingBefore(6f);
            t.addCell(getCell("Date", PdfPCell.BOX, true));
            t.addCell(getCell("Ref No", PdfPCell.BOX, true));
            t.addCell(getCell("Description", PdfPCell.BOX, true));
            t.addCell(getCell("Debit", PdfPCell.BOX, true));
            t.addCell(getCell("Credit", PdfPCell.BOX, true));
            t.addCell(getCell("Balance", PdfPCell.BOX, true));

            // Populate transactions with running balance
            running = openingBalance;
            for (Txn tx : txns) {
                running = running + tx.credit - tx.debit;
                t.addCell(getCell(df.format(tx.date), PdfPCell.BOX, false));
                t.addCell(getCell(tx.ref, PdfPCell.BOX, false));
                t.addCell(getCell(tx.desc == null ? "-" : tx.desc, PdfPCell.BOX, false));
                t.addCell(getCell(tx.debit == 0.0 ? "-" : String.format("%.2f", tx.debit), PdfPCell.BOX, false));
                t.addCell(getCell(tx.credit == 0.0 ? "-" : String.format("%.2f", tx.credit), PdfPCell.BOX, false));
                t.addCell(getCell(String.format("%.2f", running), PdfPCell.BOX, false));
            }
            doc.add(t);

            // Footer: system generated + checksum
            doc.add(new Paragraph("\nThis is a system generated statement. No signature required.", FontFactory.getFont(FontFactory.HELVETICA_OBLIQUE, 8)));  //code to caclucate checksum

            // Compute checksum over key data for verification (not a digital signature)
            StringBuilder sb = new StringBuilder();
            sb.append("ACC:").append(accountNo).append("|FROM:").append(startDate).append("|TO:").append(endDate);
            for (Txn tx : txns) {
                sb.append("|").append(tx.ref).append(":").append(tx.desc).append(":").append(tx.debit).append(":").append(tx.credit).append(":").append(tx.date.getTime());
            }
            String checksum = sha256Hex(sb.toString());
            doc.add(new Paragraph("Checksum (SHA-256): " + checksum, FontFactory.getFont(FontFactory.HELVETICA, 7)));

            doc.close();

            // 7) Return PDF to browser
            Filedownload.save(baos.toByteArray(), "application/pdf", "Statement_" + accountNo + "_" + startLocal.toString() + "_" + endLocal.toString() + ".pdf");
            Messagebox.show("Statement generated.", "Success", Messagebox.OK, Messagebox.INFORMATION);

        } catch (Exception e) {
            e.printStackTrace();
            Messagebox.show("Error generating statement: " + e.getMessage(), "Error", Messagebox.OK, Messagebox.ERROR);
        }
    }

    // Helper to create cells quickly
        private PdfPCell getCell(String text, int border, boolean bold) {
        Font f = bold ? FontFactory.getFont(FontFactory.HELVETICA_BOLD, 9) : FontFactory.getFont(FontFactory.HELVETICA, 9);
        PdfPCell cell = new PdfPCell(new Phrase(text, f));
        cell.setBorder(border);
        cell.setPadding(4f);
        return cell;
    }

    // Simple SHA-256 hex
    private String sha256Hex(String input) {
        try {
            MessageDigest md = MessageDigest.getInstance("SHA-256");
            byte[] digest = md.digest(input.getBytes("UTF-8"));
            StringBuilder sb = new StringBuilder();
            for (byte b : digest) {
                sb.append(String.format("%02x", b));
            }
            return sb.toString();
        } catch (Exception e) {
            return "";
        }
    }

    // TODO: Replace with your actual session/account fetch logic
    private String getLoggedAccountNumber() {
        // Example: return (String) Sessions.getCurrent().getAttribute("accountNo");
        return "ACC1234567890";
    }

     public static void main(String[] args) {
		
	}

}










