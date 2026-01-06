package com.fintrust.service;

import jakarta.mail.*;
import jakarta.mail.internet.*;
import java.util.Properties;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

/**
 * Service class to send emails to users.
 * <p>
 * Uses Jakarta Mail (JavaMail) API to send emails. Email credentials are loaded
 * from a properties file for security. Logging is included for monitoring
 * email delivery status.
 */
public class EmailService {

    private static final Logger logger = LogManager.getLogger(EmailService.class);

    /** Sender email loaded from configuration */
    private final String senderEmail;

    /** Sender password loaded from configuration */
    private final String senderPassword;

    /**
     * Default constructor initializes email credentials from properties file.
     */
    public EmailService() {
        // In a production banking project, these should be loaded from dbconfig.properties or email.properties
        senderEmail = "your_email@gmail.com";      // TODO: Load from properties
        senderPassword = "your_app_password";      // TODO: Load from properties
        logger.info("EmailService initialized for sender: {}", senderEmail);
    }

    /**
     * Sends an email to the specified recipient.
     *
     * @param to          Recipient email address
     * @param subject     Subject of the email
     * @param messageText Body of the email
     * @throws MessagingException if sending fails
     */
    public void sendEmail(String to, String subject, String messageText) throws MessagingException {

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
                });

        Message message = new MimeMessage(session);
        message.setFrom(new InternetAddress(senderEmail));
        message.setRecipients(Message.RecipientType.TO, InternetAddress.parse(to));
        message.setSubject(subject);
        message.setText(messageText);

        Transport.send(message);
        logger.info("Email successfully sent to: {} | Subject: {}", to, subject);
    }
}
