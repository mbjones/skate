package org.jsc.server;

import java.util.Properties;

import javax.mail.Message;
import javax.mail.MessagingException;
import javax.mail.Session;
import javax.mail.Transport;
import javax.mail.internet.AddressException;
import javax.mail.internet.InternetAddress;
import javax.mail.internet.MimeMessage;

public class MailManager {
    private Session session;
    
    public MailManager() {
        Properties sessionProps = new Properties();
        sessionProps.put("mail.smtp.host", ServerConstants.getString("SMTP_SERVER"));
        sessionProps.put("mail.smtp.auth", true);

        session = Session.getInstance(sessionProps);
    }
    
    public void sendMessage(String subject, String body, String recipient, String sender) {
        Message message = new MimeMessage(session);
        try {
            message.setFrom(new InternetAddress(sender));
            InternetAddress to[] = new InternetAddress[1];
            to[0] = new InternetAddress(recipient);
            message.setRecipients(Message.RecipientType.TO, to);
            message.setSubject(subject);
            message.setContent(body, "text/plain");

            Transport tr = session.getTransport("smtp");
            tr.connect(ServerConstants.getString("SMTP_SERVER"), 
                    ServerConstants.getString("SMTP_USER"),
                    ServerConstants.getString("SMTP_PASS"));
            message.saveChanges();
            tr.sendMessage(message, message.getAllRecipients());
            tr.close();
        } catch (AddressException e) {
            e.printStackTrace();
        } catch (MessagingException e) {
            e.printStackTrace();
        }
    }
    
    public static void main(String[] args) {
        MailManager manager = new MailManager();
        String subject = "Test email to be sent";
        String body = "This is a test.";
        String recipient = "mbjones.89@gmail.com";
        String sender = "registrar@juneauskatingclub.org";
        manager.sendMessage(subject, body, recipient, sender);
        System.out.println("Message sent.");
    }
}
