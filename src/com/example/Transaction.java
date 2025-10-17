package com.example;

import java.sql.*;
import java.util.*;

interface InnerTransaction {
    void showBalance(int customerId);
    void withdraw(int customerId, double amount);
    void transfermoney(int customerId, double amount);
    int accepttransfer(int accountId, double amount);
    List<String> TransactionHistory(int accountId);
    
}

abstract class Bal implements InnerTransaction{
    private Connection connection;
    public Bal() {
        // Initialize the connection (assuming you have a DBConnection class)
        this.connection = DBConnection.getConnection();
    }

    // Method to retrieve and display balance
    public void showBalance(int customerId) {
        String balquery = "SELECT Balance FROM account WHERE CustomerID = ?";

        if (this.connection == null) {
            System.out.println("Failed to connect to the database.");
            return;
        }

        try (PreparedStatement pstmt = this.connection.prepareStatement(balquery)) {
            pstmt.setInt(1, customerId);  // Set the account ID in the query

            // Execute the query
            ResultSet resultSet = pstmt.executeQuery();

            if (resultSet.next()) {
                double balance = resultSet.getDouble("Balance");
                System.out.println("Your current balance is: Php" + balance);
            } else {
                System.out.println("Account not found.");
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }
}

abstract class Withdraw extends Bal {
    private Connection connection;

    public Withdraw() {
        this.connection = DBConnection.getConnection();
    }

    @SuppressWarnings("resource")
    //Method to check the users account type
    public String acctype(int customerId){
        Scanner a = new Scanner(System.in);
        String accounttype = "";
        try (PreparedStatement pstmt = this.connection.prepareStatement(
            "SELECT AccountType FROM account WHERE CustomerID = ?")) {
            pstmt.setInt(1, customerId);  // Set the account ID in the query

            // Execute the query
            ResultSet resultSet = pstmt.executeQuery();

            if (resultSet.next()) {
                accounttype = resultSet.getString("AccountType");
                System.out.print("Choose Account Type (Savings/Credit/Checking): ");
                String type = a.nextLine().toUpperCase();
                    if (accounttype.equals(type)) {
                        System.out.println("==============================\n" + type + " Account");
                    } else{
                        System.out.println("Invalid Account Type");
                        System.exit(customerId);}
            } else {
                System.out.println("Account not found.");
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
                return accounttype;
    }

    //Method to withdraw
    public void withdraw(int customerId, double amount){
            // Check current balance
            try (PreparedStatement pstmt = connection.prepareStatement(
                    "SELECT Balance FROM account WHERE CustomerID = ?")) {
                pstmt.setInt(1, customerId);
                ResultSet resultSet = pstmt.executeQuery(); 

                if (resultSet.next() && resultSet.getDouble("Balance") >= amount) {
                    double currentBalance = resultSet.getDouble("Balance");
                    double newBalance = currentBalance - amount;
                    // Deduct amount and update balance
                    try (PreparedStatement updateStmt = connection.prepareStatement(
                            "UPDATE account SET Balance = ? WHERE CustomerID = ?")) {
                        updateStmt.setDouble(1, newBalance);
                        updateStmt.setInt(2, customerId);
                        if (updateStmt.executeUpdate() > 0) {
                            System.out.println("Withdrawal successful.");
                        } else {
                            System.out.println("Withdraw failed");
                        }
                    }
                } else {
                    System.out.println("Insufficient balance or account not found.");
                }

        } catch (SQLException e) {
            e.printStackTrace();
        }
    }
}

abstract class Transfer extends Withdraw{
    private Connection connection;
    //Method to Transfer
    public Transfer() {
        this.connection = DBConnection.getConnection();
    }

    public void transfermoney(int customerId, double amount){

        // Check current balance
        try (PreparedStatement pstmt = connection.prepareStatement(
            "SELECT Balance FROM account WHERE CustomerID = ?")) {
        pstmt.setInt(1, customerId);
        ResultSet resultSet = pstmt.executeQuery(); 

        if (resultSet.next() && resultSet.getDouble("Balance") >= amount) {
            double currentBal = resultSet.getDouble("Balance");
            double newBal = currentBal - amount;
            // Deduct amount and update balance
            try (PreparedStatement updateStmt = connection.prepareStatement(
                    "UPDATE account SET Balance = ? WHERE CustomerID = ?")) {
                updateStmt.setDouble(1, newBal);
                updateStmt.setInt(2, customerId);
                updateStmt.executeUpdate();

            }
        } else {
            System.out.println("Insufficient balance or account not found.");
        }

} catch (SQLException e) {
    e.printStackTrace();
} 
    }
    //Method to receive transfer
    public int accepttransfer(int accountId, double amount){
        int customer = -1;
        // Check current balance
        try (PreparedStatement pstmt = connection.prepareStatement(
            "SELECT Balance  FROM account WHERE AccountID = ?")) {
        pstmt.setInt(1, accountId);
        ResultSet resultSet = pstmt.executeQuery(); 

        if (resultSet.next()) {
            double currentB = resultSet.getDouble("Balance");
            double newB = currentB + amount;
            // Deduct amount and update balance
            try (PreparedStatement updateStmt = connection.prepareStatement(
                    "UPDATE account SET Balance = ? WHERE AccountID = ?")) {
                updateStmt.setDouble(1, newB);
                updateStmt.setInt(2, accountId);
                if (updateStmt.executeUpdate() > 0) {
                    System.out.println("Transfer successful.");
                } else {
                    System.out.println("Transfer failed");
                }
            }
        } else {
            System.out.println("Insufficient balance or account not found.");
        }

} catch (SQLException e) {
    e.printStackTrace();
}
    return customer;
    }
}

public class Transaction extends Transfer {
    //Method to insert in transaction in database
    public void insertTransaction(String transactionType, double amount, int accountId) {
        String query = "INSERT INTO transaction (TransactionType, Amount, AccountID) VALUES (?, ?, ?)";
        try (Connection connection = DBConnection.getConnection();
             PreparedStatement pstmt = connection.prepareStatement(query)) {

            pstmt.setString(1, transactionType);
            pstmt.setDouble(2, amount);
            pstmt.setInt(3, accountId);
            pstmt.executeUpdate();

            System.out.println("Transaction recorded successfully.");
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

        // Retrieve transaction history for a specific account
        public List<String> TransactionHistory(int accountId) {
            String query = "SELECT * FROM transaction WHERE AccountID = ?";
            List<String> transac = new ArrayList<>();
    
            try (Connection conn = DBConnection.getConnection();
                 PreparedStatement pstmt = conn.prepareStatement(query)) {
    
                pstmt.setInt(1, accountId);
                ResultSet resultSet = pstmt.executeQuery();
    
                while (resultSet.next()) {
                    String transaction = "ID: " + resultSet.getInt("TransactionID") +
                            ", Type: " + resultSet.getString("TransactionType") +
                            ", Amount: Php" + resultSet.getDouble("Amount") +
                            ", Date: " + resultSet.getTimestamp("TransactionDate");
                    transac.add(transaction);
                }
    
            } catch (Exception e) {
                e.printStackTrace();
            }
            return transac;
        }
    }