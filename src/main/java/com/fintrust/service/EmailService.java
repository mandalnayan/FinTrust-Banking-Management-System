package com.fintrust.service;

import jakarta.mail.*;
import jakarta.mail.internet.*;
import java.util.Properties;

import org.zkoss.zul.Messagebox;

public class EmailService {
    private final String senderEmail = "nayanm417@gmail.com";  
    private final String senderPassword = "zxrhrgrielkhqxml";  

    public void sendEmail(String to, String subject, String messageText) throws Exception {

        Properties props = new Properties();
        props.put("mail.smtp.auth", "true");
        props.put("mail.smtp.starttls.enable", "true");
        props.put("mail.smtp.host", "smtp.gmail.com");
        props.put("mail.smtp.port", "587");

        Session session = Session.getInstance(props, 
            new Authenticator() {
                protected PasswordAuthentication getPasswordAuthentication() {
                    return new PasswordAuthentication(senderEmail, senderPassword);
                }
            }
        );

        Message message = new MimeMessage(session);
        message.setFrom(new InternetAddress(senderEmail));
        message.setRecipients(Message.RecipientType.TO, InternetAddress.parse(to));
        message.setSubject(subject);
        message.setText(messageText);

        Transport.send(message);
        //Messagebox.show("Email send to customer regarding the status of account update request");
    }
}