package com.example;

import java.sql.*;

public class Account{
    private Connection connection;
    public Account() {
        // Initialize the connection from DatabaseConnectionManager
        this.connection = DBConnection.getConnection();
        if (this.connection == null) {
            System.out.println("Failed to establish a database connection.");
        }
    }

        //Method to select cutomerId and check email and pin
        public int login(String email, String pin) {
            int customerId = -1;
                    String loginQuery = "SELECT CustomerID FROM customer WHERE Email = ? AND PIN = ?";

            
                    try(PreparedStatement pstmt = connection.prepareStatement(loginQuery)){
                    
                    pstmt.setString(1, email);
                    pstmt.setString(2, pin);
                    ResultSet resultSet = pstmt.executeQuery();
                    
            if (resultSet.next()) {
                customerId = resultSet.getInt("CustomerID");
                System.out.println("Login successful.");
            } else {
                System.out.println("No user found with the given email.");
            }
}
            catch (SQLException e) {
                e.printStackTrace();
            }
                return customerId;
    }
    //Method to select accountId
    public int Ac(int customerId){
        int accountId = -1;
        try(PreparedStatement pstmt = connection.prepareStatement(
            "SELECT AccountID FROM account WHERE CustomerID = ?")){
                
                pstmt.setInt(1, customerId);
                ResultSet resultSet = pstmt.executeQuery(); 
                
        if (resultSet.next()) {
            accountId = resultSet.getInt("AccountID");
        } else {
            System.out.println("No user found with the given email.");
        }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return accountId;
    }
}