package com.meuprojeto.util;

import jakarta.mail.*;
import jakarta.mail.internet.*;
import java.util.Properties;
import java.io.File;

public class EmailSender {
    public static void sendEmailWithAttachments(String host, String port,
            final String userName, final String password, String toAddress,
            String subject, String message, File[] attachFiles)
            throws AddressException, MessagingException {

        Properties properties = new Properties();
        properties.put("mail.smtp.host", host);
        properties.put("mail.smtp.port", port);
        properties.put("mail.smtp.auth", "true");
        properties.put("mail.smtp.starttls.enable", "true");

        Authenticator auth = new Authenticator() {
            public PasswordAuthentication getPasswordAuthentication() {
                return new PasswordAuthentication(userName, password);
            }
        };

        Session session = Session.getInstance(properties, auth);

        Message msg = new MimeMessage(session);
        msg.setFrom(new InternetAddress(userName));
        msg.setRecipients(Message.RecipientType.TO, InternetAddress.parse(toAddress));
        msg.setSubject(subject);
        msg.setSentDate(new java.util.Date());

        MimeBodyPart messageBodyPart = new MimeBodyPart();
        messageBodyPart.setContent(message, "text/html");

        Multipart multipart = new MimeMultipart();
        multipart.addBodyPart(messageBodyPart);

        if (attachFiles != null && attachFiles.length > 0) {
            for (File filePath : attachFiles) {
                MimeBodyPart attachPart = new MimeBodyPart();
                try {
                    attachPart.attachFile(filePath);
                } catch (Exception ex) {
                    ex.printStackTrace();
                }
                multipart.addBodyPart(attachPart);
            }
        }

        msg.setContent(multipart);
        Transport.send(msg);
    }    
}
