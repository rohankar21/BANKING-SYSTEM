import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;

import java.util.Scanner;

public class testing {
    public static void main(String[] args) {
        Scanner scanner = new Scanner(System.in);

        System.out.println("Enter customer name: ");
        String customerName = scanner.nextLine();

        System.out.println("Enter customer ID: ");
        String customerId = scanner.nextLine();

        BankAccount obj = new BankAccount(customerName, customerId);
        obj.showMenu();

        scanner.close();
    }
}

class BankAccount {
    int balance;
    int previousTransaction;
    String customerName;
    String customerId;
    Connection con;

    BankAccount(String cname, String cid) {
        customerName = cname;
        customerId = cid;
        try {
            con = DriverManager.getConnection("jdbc:mysql://localhost:3306/BankDB", "root", "root");
            addUserToDatabase();
        } catch (Exception e) {
            System.out.println("Database connection error: " + e.getMessage());
        }
    }

    void addUserToDatabase() {
        try {
            String query = "INSERT INTO users (customer_name, customer_id, balance) VALUES (?, ?, ?)";
            PreparedStatement pstmt = con.prepareStatement(query);
            pstmt.setString(1, customerName);
            pstmt.setString(2, customerId);
            pstmt.setInt(3, balance);
            pstmt.executeUpdate();
            System.out.println("User added to database successfully.");
        } catch (Exception e) {
            System.out.println("Error adding user to database: " + e.getMessage());
        }
    }

    void deposit(int amount) {
        if (amount != 0) {
            balance += amount;
            previousTransaction = amount;
            updateBalanceInDatabase();
        }
    }

    void withdraw(int amount) {
        if (amount != 0) {
            balance -= amount;
            previousTransaction = -amount;
            updateBalanceInDatabase();
        }
    }

    void updateBalanceInDatabase() {
        try {
            String query = "UPDATE users SET balance = ? WHERE customer_id = ?";
            PreparedStatement pstmt = con.prepareStatement(query);
            pstmt.setInt(1, balance);
            pstmt.setString(2, customerId);
            pstmt.executeUpdate();
        } catch (Exception e) {
            System.out.println("Error updating balance in database: " + e.getMessage());
        }
    }

    void getPreviousTransaction() {
        if (previousTransaction > 0) {
            System.out.println("Deposited: " + previousTransaction);
        } else if (previousTransaction < 0) {
            System.out.println("Withdrawn: " + Math.abs(previousTransaction));
        } else {
            System.out.println("No Transaction Occurred");
        }
    }

    void showMenu() {
        int option = 0;
        Scanner scanner = new Scanner(System.in);

        System.out.println("Welcome " + customerName);
        System.out.println("Your ID is " + customerId);
        System.out.println("\n");

        System.out.println("1 : Check Your Balance");
        System.out.println("2 : Deposit");
        System.out.println("3 : Withdraw");
        System.out.println("4 : Previous Transaction");
        System.out.println("5 : Exit The System");

        try {
            do {
                System.out.println("=========================================================");
                System.out.println("Enter Your Option");
                System.out.println("=========================================================");
                option = scanner.nextInt();
                System.out.println("\n");

                switch (option) {
                    case 1:
                        System.out.println("-------------------------------------------------------");
                        System.out.println("Balance = " + balance);
                        System.out.println("-------------------------------------------------------");
                        System.out.println("\n");
                        break;

                    case 2:
                        System.out.println("-------------------------------------------------------");
                        System.out.println("Enter an amount to deposit ");
                        System.out.println("-------------------------------------------------------");

                        int amount = scanner.nextInt();
                        deposit(amount);
                        System.out.println("\n");
                        break;

                    case 3:
                        System.out.println("-------------------------------------------------------");
                        System.out.println("Enter an amount to withdraw ");
                        System.out.println("-------------------------------------------------------");

                        int amount2 = scanner.nextInt();
                        withdraw(amount2);
                        System.out.println("\n");
                        break;

                    case 4:
                        System.out.println("-------------------------------------------------------");
                        getPreviousTransaction();
                        System.out.println("-------------------------------------------------------");
                        System.out.println("\n");
                        break;

                    case 5:
                        System.out.println("=========================================================");
                        break;

                    default:
                        System.out.println("Invalid Option!! Please Enter Correct Option...");
                        break;
                }
            } while (option != 5);
            System.out.println("Thank You for Using our Services.....!!");
        } finally {
            scanner.close();
        }
    }
}
