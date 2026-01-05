package com.fintrust.viewModel;

import org.zkoss.zk.ui.select.SelectorComposer;
import org.zkoss.zk.ui.select.annotation.Wire;
import org.zkoss.zul.Iframe;
import org.zkoss.zul.Window;
import java.io.*;
import org.zkoss.util.media.*;
import org.zkoss.zk.ui.Executions;


public class PdfComposer extends SelectorComposer<Window>{
	
	@Wire
	Iframe pdfFrame;
	
	
	@Override
	public void doAfterCompose(Window comp) throws Exception {
		super.doAfterCompose(comp);
		
		InputStream fileStream = Executions.getCurrent().getDesktop().getWebApp().getResourceAsStream("/report.pdf");

        AMedia media = new AMedia("report.pdf", "pdf", "application/pdf",fileStream);

        pdfFrame.setContent(media);
	}

}
