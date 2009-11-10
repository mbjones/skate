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
        session = Session.getInstance(sessionProps);
    }
    
    private void sendMessage(String subject, String body, String recipient, String sender) {
        Message message = new MimeMessage(session);
        try {
            message.setFrom(new InternetAddress(sender));
            InternetAddress to[] = new InternetAddress[1];
            to[0] = new InternetAddress(recipient);
            message.setRecipients(Message.RecipientType.TO, to);
            message.setSubject(subject);
            message.setContent(body, "text/plain");
            Transport.send(message);        
        } catch (AddressException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        } catch (MessagingException e) {
            // TODO Auto-generated catch block
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
    }
}
