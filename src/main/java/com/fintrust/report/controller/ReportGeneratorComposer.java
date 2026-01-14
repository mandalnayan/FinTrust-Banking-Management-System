package com.fintrust.report.controller;

import java.io.File;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.zkoss.zk.ui.Sessions;
import org.zkoss.zk.ui.select.SelectorComposer;
import org.zkoss.zk.ui.select.annotation.Wire;
import org.zkoss.zkex.zul.Jasperreport;
import org.zkoss.zul.Window;

import com.fintrust.dao.impl.TransactionsDAOImpl;
import com.fintrust.db.DBConnection;
import com.fintrust.model.Transaction;

import net.sf.jasperreports.engine.JRException;
import net.sf.jasperreports.engine.JasperCompileManager;
import net.sf.jasperreports.engine.data.JRBeanCollectionDataSource;

public class ReportGeneratorComposer extends SelectorComposer<Window> {

    @Wire
    private Jasperreport report;

    @Override
    public void doAfterCompose(Window comp) throws Exception {
        super.doAfterCompose(comp);

        TransactionsDAOImpl dataBeanList = new TransactionsDAOImpl(DBConnection.getConnection());
        List<Transaction> dataList = dataBeanList.allCurrentUserTransactions(null, null);

        JRBeanCollectionDataSource dataSource =
                new JRBeanCollectionDataSource(dataList);

        File jrxmlFile = new File(
                Sessions.getCurrent()
                        .getWebApp()
                        .getRealPath("/reports/transactions_jasper_report.jrxml")
        );

        String jasperPath =
                Sessions.getCurrent()
                        .getWebApp()
                        .getRealPath("/reports/transactions_jasper_report.jasper");

        JasperCompileManager.compileReportToFile(
                jrxmlFile.getAbsolutePath(),
                jasperPath
        );

        Map<String, Object> parameters = new HashMap<>();
        
        // Column header
        parameters.put("column1", "Transaction Id");
        parameters.put("column2", "From Account");
        parameters.put("column3", "CounterParty Account");
        parameters.put("column4", "Transaction Type");
        parameters.put("column5", "Balance"); 
        
        parameters.put("name", "Nayan Mandal");
        parameters.put("address", "Delhi, NCR 878654");
        parameters.put("duration", "01-Jan-2025 to 31-Jan-2025");
        parameters.put("accountNo", 123456789012l);
        
        // Summary information
        parameters.put("totalBalance", 5670.9);

        report.setSrc("/reports/transactions_jasper_report.jasper");
        report.setParameters(parameters);
        report.setDatasource(dataSource);
        report.setType("pdf");
    }
    
    public static void main(String args[]) {
    	try {
			JasperCompileManager.compileReportToFile(
			        "transactions_jasper_report.jrxml",
			        "transactions_jasper_report.jasper"
			    );
		} catch (JRException e) {
			
			e.printStackTrace();
		}
    }
}

