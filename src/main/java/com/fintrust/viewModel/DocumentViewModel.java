package com.fintrust.viewModel;

import org.zkoss.bind.annotation.Command;
import org.zkoss.bind.annotation.NotifyChange;

public class DocumentViewModel {

    private String filePath; // bind to pdfviewer src

    public String getFilePath() {
        return filePath;
    }

    @Command
    @NotifyChange("filePath")
    public void showPDF(String path) {
        // Here you can add validation if needed
        this.filePath = path;
        System.out.println("Loading PDF: " + path);
    }
}
