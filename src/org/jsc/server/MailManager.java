package org.jsc.server;

import java.util.Properties;

import javax.mail.Message;
import javax.mail.MessagingException;
import javax.mail.Session;
import javax.mail.Transport;
import javax.mail.internet.AddressException;
import javax.mail.internet.InternetAddress;
import javax.mail.internet.MimeMessage;

/**
 * Manage Mail that is to be sent out on behalf of the application by setting up
 * needed mail transport and connections.
 * 
 * @author Matt Jones
 *
 */
public class MailManager {
    private Session session;
    
    /**
     * Construct a new MailManager with server information set from the properties file.
     */
    public MailManager() {
        Properties sessionProps = new Properties();
        sessionProps.put("mail.smtp.host", ServerConstants.getString("SMTP_SERVER"));
        sessionProps.put("mail.smtp.auth", (new Boolean(ServerConstants.getString("SMTP_AUTH")).booleanValue()));

        session = Session.getInstance(sessionProps);
    }
    
    /**
     * Send a message over SMTP.
     * @param subject subject of the message to be sent
     * @param body text of the body of the message
     * @param recipient recipient email address
     * @param sender sender email address
     */
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
    
    /**
     * Mail method used for testing only.
     * @param args none for this method
     */
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
