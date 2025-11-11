package com.fintrust.service;

import jakarta.mail.Message;
import jakarta.mail.MessagingException;
import jakarta.mail.Session;
import jakarta.mail.Transport;
import jakarta.mail.internet.InternetAddress;
import jakarta.mail.internet.MimeMessage;

import java.util.Properties;

/**
 * 1) What the MailSenderWrapper does — overview (one line) 
 * It prepares JavaMail (Jakarta Mail) settings, authenticates to an SMTP server (Gmail), creates an email message, and sends it.
 * 
 * 2. Step-by-step
 * Properties props = new Properties();

You create a small map (key/value) to tell JavaMail how to talk to the SMTP server.

props.put("mail.smtp.auth", "true");

This says “we will authenticate (username + password)” with the SMTP server.

props.put("mail.smtp.starttls.enable", "true");

This enables STARTTLS — it upgrades the connection to a secure (encrypted) one. For Gmail, this is needed when using port 587.

props.put("mail.smtp.host", host); and props.put("mail.smtp.port", port);

These tell which SMTP server and port to use. For Gmail: smtp.gmail.com and 587.

Session.getInstance(props, new Authenticator(){...});

Session is the main object from Jakarta Mail that holds those settings.

You pass an Authenticator that provides the username and password to the mail system when requested.

getPasswordAuthentication() returns the credentials (email + app password).

IMPORTANT: for Gmail you must use an App Password (or OAuth2) — not your normal account password (if 2SV is enabled or Google blocks less-secure apps).
 */

public class MailSenderWrapper {

    private final Session session;

    public MailSenderWrapper(String host, String port, String user, String password) {
        Properties props = new Properties();
        props.put("mail.smtp.auth", "true");
        props.put("mail.smtp.starttls.enable", "true");
        props.put("mail.smtp.host", host);
        props.put("mail.smtp.port", port);

        session = Session.getInstance(props, new jakarta.mail.Authenticator() {
            protected jakarta.mail.PasswordAuthentication getPasswordAuthentication() {
                return new jakarta.mail.PasswordAuthentication(user, password);
            }
        });
    }

    
    /**
     * new MimeMessage(session)

Create the email object tied to the Session (so it uses the SMTP settings and auth).

msg.setFrom(...)

Sets the “From” address shown to the recipient. This can be your Gmail address or a custom noreply@... — some SMTP servers require the From to match authenticated user.

msg.setRecipients(...)

Sets the recipient email(s).

msg.setSubject(...) and msg.setText(...)

Set subject and plain-text body of the email.

Transport.send(msg);

This actually opens the network connection and sends the email using the session settings + credentials.
     * @param toEmail
     * @param subject
     * @param body
     * @throws MessagingException
     */
    
    
    
    public void sendSimple(String toEmail, String subject, String body) throws MessagingException {
        MimeMessage msg = new MimeMessage(session);
        msg.setFrom(new InternetAddress("noreply@example.com"));
        msg.setRecipients(Message.RecipientType.TO, InternetAddress.parse(toEmail));
        msg.setSubject(subject);
        msg.setText(body);
        Transport.send(msg);
    }
}
