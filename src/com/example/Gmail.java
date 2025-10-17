package com.example;

import javax.mail.*;
import javax.mail.internet.*;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.Properties;

class GmailSender {
    private String username;
    private String password;
    private Properties props;
        public GmailSender(String username, String password) {
            // Sender's email credentials
            this.username = "vaultxbank@gmail.com"; // Replace with your Gmail email;
            this.password = "hbnf eaql zulw zlji";   // Replace with your App Password
        
            // SMTP server configuration
            props = new Properties();
            props.put("mail.smtp.auth", "true");
            props.put("mail.smtp.starttls.enable", "true");
            props.put("mail.smtp.host", "smtp.gmail.com");
            props.put("mail.smtp.port", "587"); // TLS port
        }

        public String sendEmailwithdraw(String email, int accountId) {
            // Create a session
            Session session = Session.getInstance(props, new Authenticator() {
                @Override
                protected PasswordAuthentication getPasswordAuthentication() {
                    return new PasswordAuthentication(username, password);
                }
            });

        // Recipient's email
        // Email confirmation of successful withdrawal
        try(Connection connection = DBConnection.getConnection();
        PreparedStatement pstmt = connection.prepareStatement(
            "SELECT * FROM transaction WHERE TransactionType = 'Withdrawal' AND AccountID = ? ORDER BY TransactionDate DESC LIMIT 1")){
                
                pstmt.setInt(1, accountId);

                ResultSet resultSet = pstmt.executeQuery(); 

                if (resultSet.next()) {
                    // Create a MimeMessage object
                    Message message = new MimeMessage(session);

                    // Set From: header field
                    message.setFrom(new InternetAddress(username));

                    // Set To: header field
                    message.setRecipients(Message.RecipientType.TO, InternetAddress.parse(email));

                    // Set Subject: header field
                    message.setSubject("Withdrawal");
                            
                    String sendtransaction = "\nID: " + resultSet.getInt("TransactionID") +
                            "\nType: " + resultSet.getString("TransactionType") +
                            "\nAmount: Php" + resultSet.getDouble("Amount") +
                            "\nDate: " + resultSet.getTimestamp("TransactionDate")+
                            "\n AccountID: " + resultSet.getInt("AccountID");

                    // Set the content of the email
                    message.setText("The withdrawal is successful." + sendtransaction);

                    // Send the email
                    Transport.send(message);

                } else {
                    System.out.println("No user found with the given email.");
                }
        } catch (SQLException | MessagingException e) {
            e.printStackTrace();
            System.err.println("Failed to send email");
        }
                return email;
    }
  }

public class Gmail extends GmailSender{
    private String username;
    private String password;
    private Properties props;
            
    public Gmail(String username, String password) {
        super(username, password);
            this.username = "vaultxbank@gmail.com"; // Replace with your Gmail email;
            this.password = "hbnf eaql zulw zlji";   // Replace with your App Password

            props = new Properties();
            props.put("mail.smtp.auth", "true");
            props.put("mail.smtp.starttls.enable", "true");
            props.put("mail.smtp.host", "smtp.gmail.com");
            props.put("mail.smtp.port", "587"); // TLS port
    }
        public String sendEmailTransferS(String email, int fromaccountId, int toaccountId){
            // Create a session
            Session session = Session.getInstance(props, new Authenticator() {
                @Override
                protected PasswordAuthentication getPasswordAuthentication() {
                    return new PasswordAuthentication(username, password);
            }
        });

    // Recipient's email
    //Email confirmation os successful transfer for Sender
    try(Connection connection = DBConnection.getConnection();
    PreparedStatement pstmt = connection.prepareStatement(
        "SELECT * FROM transaction WHERE TransactionType = 'Transfer' AND AccountID = ? ORDER BY TransactionDate DESC LIMIT 1")){
            
            pstmt.setInt(1, fromaccountId);

            ResultSet resultSet = pstmt.executeQuery(); 

            if (resultSet.next()) {
                // Create a MimeMessage object
                Message message = new MimeMessage(session);

                // Set From: header field
                message.setFrom(new InternetAddress(username));

                // Set To: header field
                message.setRecipients(Message.RecipientType.TO, InternetAddress.parse(email));

                // Set Subject: header field
                message.setSubject("Transfer Sender");
                        
                String sendsender = "\nID: " + resultSet.getInt("TransactionID") +
                        "\nType: " + resultSet.getString("TransactionType") +
                        "\nAmount: Php" + resultSet.getDouble("Amount") +
                        "\nDate: " + resultSet.getTimestamp("TransactionDate")+
                        "\nFrom AccountID: " + fromaccountId+
                        "\nTo AccountID: " + toaccountId;

                // Set the content of the email
                message.setText("Transfer Successful.\nYou transfer:\n " + sendsender);

                // Send the email
                Transport.send(message);

            } else {
                System.out.println("No user found with the given email.");
            }
    } catch (SQLException | MessagingException e) {
        e.printStackTrace();
        System.err.println("Failed to send email");
    }
            return email;

}

public String reciever(int tId){
    String mail = "";
    @SuppressWarnings("unused")
    Session session = Session.getInstance(props, new Authenticator() {
        @Override
        protected PasswordAuthentication getPasswordAuthentication() {
            return new PasswordAuthentication(username, password);
        }
    });

    try (Connection connection = DBConnection.getConnection();
        PreparedStatement Pstmt = connection.prepareStatement(
                "SELECT CustomerID FROM account WHERE AccountID = ?")) {
                    Pstmt.setInt(1, tId);
                    ResultSet rset = Pstmt.executeQuery();
                if (rset.next()){
                    int customerId = rset.getInt("CustomerID");
                    try(PreparedStatement pstmt = connection.prepareStatement("SELECT Email FROM customer WHERE CustomerID =?")){
                        pstmt.setInt(1, customerId);
                        ResultSet resultSet = pstmt.executeQuery();
                        if(resultSet.next()){
                            mail = resultSet.getString("Email");
                        }
                    }
                }
            } catch (SQLException e) {
                e.printStackTrace();
                System.err.println("Failed to send email");
                }
                return mail; 
}

public String sendEmailTransferR(int fromId, int toId, String mail){
        // Create a session
        Session session = Session.getInstance(props, new Authenticator() {
            @Override
            protected PasswordAuthentication getPasswordAuthentication() {
                return new PasswordAuthentication(username, password);
            }
        });

        // Recipient's email
        //Email confirmation os successful transfer for Receiver
                            try(Connection connection = DBConnection.getConnection();
                            PreparedStatement stmt = connection.prepareStatement(
                                "SELECT * FROM transaction WHERE TransactionType = 'Transfer' AND AccountID = ? ORDER BY TransactionDate DESC LIMIT 1")){
                                    
                                    stmt.setInt(1, toId);

                                    ResultSet rs = stmt.executeQuery(); 

                                    if (rs.next()) {
                                        // Create a MimeMessage object
                                        Message message = new MimeMessage(session);

                                        // Set From: header field
                                        message.setFrom(new InternetAddress(username));

                                        // Set To: header field
                                        message.setRecipients(Message.RecipientType.TO, InternetAddress.parse(mail));

                                        // Set Subject: header field
                                        message.setSubject("Transfer Received");
                                                
                                        String sendreceiver = "\nID: " + rs.getInt("TransactionID") +
                                                "\nType: " + rs.getString("TransactionType") +
                                                "\nAmount: Php" + rs.getDouble("Amount") +
                                                "\nDate: " + rs.getTimestamp("TransactionDate")+
                                                "\nFrom AccountID: " + fromId+
                                                "\nTo AccountID: " + toId;

                                        // Set the content of the email
                                        message.setText("Transfer Successful.\n You received: \n" + sendreceiver);

                                        // Send the email
                                        Transport.send(message);
                                } else {
                            System.out.println("No user found with the given email.");
                }
    } catch (SQLException | MessagingException e) {
        e.printStackTrace();
        System.err.println("Failed to send email");
    }
    return mail;
}
}