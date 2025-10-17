package com.example.main;

import java.util.*;
import com.example.*;

public class App {
    public static void main(String[] args){
        Transaction transaction = new Transaction();
        Gmail gmail = new Gmail(null, null);
        Account acc = new Account();
        @SuppressWarnings("resource")
        Scanner sc = new Scanner(System.in);
        int attempt = 0;
        
        while (attempt < 3) {
        // Check Username and Password
        System.out.println("==============================\nEnter your Username: ");
        String email = sc.nextLine();
        System.out.println("==============================\nEnter your 4 Digit Pin: ");
        String pin = sc.nextLine();
    
        int customerId = acc.login(email, pin);
    
        if(customerId != -1){
        int accountId = acc.Ac(customerId);
            //ATM operation Menu
            while(true){
                System.out.println("==============================");
                System.out.println("[1] Check Balance");
                System.out.println("[2] Withdraw");
                System.out.println("[3] Transfer");
                System.out.println("[4] View Transaction History");
                System.out.println("[5] Exit");

                System.out.print("==============================\nSelect Operation: ");
                int choice = sc.nextInt();

                switch (choice) {           
                    case 1://Show Balance
                    transaction.showBalance(customerId);
                        break;
                    case 2://Withdraw
                        transaction.acctype(customerId);
                            System.out.println("\nEnter amount to withdraw: ");
                            double amount = sc.nextDouble();
                            
                            transaction.withdraw(customerId, amount);
                            transaction.insertTransaction("Withdrawal", amount, accountId);
                            gmail.sendEmailwithdraw(email, accountId);
                            break;
                    case 3://Transfer
                        System.out.println("\nEnter accountId to transfer: ");
                        int Id = sc.nextInt();
                        System.out.println("\nEnter amount to transfer: ");
                        double c = sc.nextDouble();
                        transaction.transfermoney(customerId, c);
                        gmail.sendEmailTransferS(email, accountId, Id);
                        transaction.accepttransfer(Id, c);
                        String mail = gmail.reciever(Id);
                        gmail.sendEmailTransferR(accountId, Id, mail);
                        transaction.insertTransaction("Transfer", c, accountId);
                        break;
                    case 4://View Transaction History
                        System.out.println("Transaction History:");
                        for (String transac : transaction.TransactionHistory(accountId)) {
                            System.out.println(transac);
                        }
                        break;
                    case 5://Exit
                    System.out.println("==============================\nThank you for using the ATM\n==============================");
                        System.exit(0);
                        break;
                    default:
                        System.out.println("==============================\nInvalid Option, Please Try Again");
                }
            }
        } 
        else {
            System.out.println("==============================\nIncorrect Username or PIN.");
            attempt++;
            }
        }
    }
    
}
