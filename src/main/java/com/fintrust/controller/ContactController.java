package com.fintrust.controller;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.StandardCopyOption;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import org.zkoss.zk.ui.Component;
import org.zkoss.zk.ui.Executions;
import org.zkoss.zk.ui.event.Events;
import org.zkoss.zk.ui.event.UploadEvent;
import org.zkoss.util.media.Media;

import org.zkoss.zk.ui.select.SelectorComposer;
import org.zkoss.zk.ui.select.annotation.Wire;
import org.zkoss.zul.Button;
import org.zkoss.zul.Combobox;
import org.zkoss.zul.ListModelList;
import org.zkoss.zul.Listbox;
import org.zkoss.zul.Listcell;
import org.zkoss.zul.Listitem;
import org.zkoss.zul.Messagebox;
import org.zkoss.zul.Radiogroup;
import org.zkoss.zul.Textbox;

import com.fintrust.model.ContactModel;
import com.fintrust.service.ContactService;

public class ContactController extends SelectorComposer<Component> {

    @Wire
    private Textbox fullName, mobile, email, customerId, subject, description, trackId;

    @Wire
    private Combobox category, subcategory, preferredContactMethod, preferredTime;

    @Wire
    private Radiogroup priority;

    @Wire
    private Button fileUpload, clear, submitBtn, trackBtn;

    @Wire
    private Listbox fileList;

    private List<Media> uploadedFiles = new ArrayList<>();
    private ContactService service = new ContactService();


    @Override
    public void doAfterCompose(Component comp) throws Exception {
        super.doAfterCompose(comp);

        category.setModel(new ListModelList<>(Arrays.asList(
                "Account Issues", "Card Issues", "NetBanking", 
                "UPI/Payments", "Fraud", "Other"
        )));

        subcategory.setModel(new ListModelList<>(Arrays.asList(
                "Password Reset", "Card Block", "Account Locked", "Technical Issue"
        )));

        preferredContactMethod.setModel(new ListModelList<>(Arrays.asList(
                "Phone", "Email", "SMS", "WhatsApp"
        )));

        preferredTime.setModel(new ListModelList<>(Arrays.asList(
                "Morning", "Afternoon", "Evening"
        )));

        fileUpload.addEventListener(Events.ON_UPLOAD, evt -> handleFileUpload((UploadEvent) evt));
        submitBtn.addEventListener(Events.ON_CLICK, evt -> submitTicket());
        clear.addEventListener(Events.ON_CLICK, evt -> clearForm());
        trackBtn.addEventListener(Events.ON_CLICK, evt -> trackTicket());
    }


   
    private void handleFileUpload(UploadEvent event) {
        Media media = event.getMedia();
        uploadedFiles.add(media);

        saveFileToFolder(media);
        addFileToListbox(media);

        Messagebox.show("File uploaded: " + media.getName());
    }


    private void saveFileToFolder(Media media) {

        String realPath = Executions.getCurrent().getDesktop().getWebApp()
                .getRealPath("/uploads/");

        File folder = new File(realPath);
        if (!folder.exists()) {
            folder.mkdirs();
        }

        File file = new File(folder, media.getName());
        try {
            Files.copy(media.getStreamData(), file.toPath(), StandardCopyOption.REPLACE_EXISTING);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }


    private void addFileToListbox(Media media) {

        Listitem row = new Listitem();

        row.appendChild(new Listcell(media.getName()));

        Button viewBtn = new Button("View");

        viewBtn.addEventListener(Events.ON_CLICK, e -> {
            Executions.getCurrent().sendRedirect("/fintrust_db/uploads/" + media.getName(), "_blank");
        });

        Listcell actionCell = new Listcell();
        actionCell.appendChild(viewBtn);
        row.appendChild(actionCell);

        fileList.appendChild(row);
    }



    
    private void submitTicket() {
        ContactModel model = new ContactModel();

        model.setCustomerName(fullName.getValue());
        model.setMobile(mobile.getValue());
        model.setEmail(email.getValue());
        model.setCustomerId(customerId.getValue());
        model.setCategory(category.getValue());
        model.setSubcategory(subcategory.getValue());
        model.setPriority(priority.getSelectedItem().getLabel());
        model.setSubject(subject.getValue());
        model.setDescription(description.getValue());
        model.setPreferredContactMethod(preferredContactMethod.getValue());
        model.setPreferredTime(preferredTime.getValue());

        List<String> fileNames = uploadedFiles.stream()
                .map(Media::getName)
                .toList();

        model.setAttachments(fileNames);

        service.saveTicket(model);

        Messagebox.show("Ticket Submitted Successfully!");
        clearForm();
    }



 
    private void trackTicket() {

        if (trackId.getValue().isEmpty()) {
            Messagebox.show("Please enter Ticket ID.");
            return;
        }

        try {
            long id = Long.parseLong(trackId.getValue());
            ContactModel ticket = service.getTicketById(id);

            if (ticket == null) {
                Messagebox.show("Ticket not found!");
                return;
            }

            String msg =
                    "Ticket ID: " + ticket.getId() + "\n" +
                    "Name: " + ticket.getCustomerName() + "\n" +
                    "Category: " + ticket.getCategory() + "\n" +
                    "Status: " + ticket.getStatus() + "\n" +
                    "Created At: " + ticket.getCreatedAt();

            Messagebox.show(msg, "Ticket Details", Messagebox.OK, Messagebox.INFORMATION);

        } catch (NumberFormatException e) {
            Messagebox.show("Enter a valid numeric Ticket ID.");
        }
    }



    
    private void clearForm() {

        fullName.setValue("");
        mobile.setValue("");
        email.setValue("");
        customerId.setValue("");
        subject.setValue("");
        description.setValue("");

        category.setSelectedIndex(-1);
        subcategory.setSelectedIndex(-1);
        preferredContactMethod.setSelectedIndex(-1);
        preferredTime.setSelectedIndex(-1);
        priority.setSelectedIndex(1);

        uploadedFiles.clear();
        fileList.getItems().clear();
    }
}
