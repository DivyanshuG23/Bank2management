import java.io.*;
import java.util.*;

public class Bank2ManagementSystem {
    private static final String FILE_NAME = "accounts.csv";
    private static List<Account> accounts = new ArrayList<>();
    private static Scanner sc = new Scanner(System.in);

    public static void main(String[] args) {
        loadAccounts();

        while (true) {
            System.out.println("\n--- Bank Management System ---");
            System.out.println("1. Create Account");
            System.out.println("2. Deposit");
            System.out.println("3. Withdraw");
            System.out.println("4. Show Accounts");
            System.out.println("5. Exit");
            System.out.print("Enter choice: ");

            if (!sc.hasNextInt()) {
                System.out.println("Invalid input! Enter a number.");
                sc.next();
                continue;
            }

            int choice = sc.nextInt();

            switch (choice) {
                case 1 -> createAccount();
                case 2 -> deposit();
                case 3 -> withdraw();
                case 4 -> showAccounts();
                case 5 -> {
                    saveAccounts();
                    System.out.println("Exiting... All data saved.");
                    System.exit(0);
                }
                default -> System.out.println("Invalid choice!");
            }
        }
    }

    private static void createAccount() {
        System.out.print("Account No: ");
        String accNo = sc.next();
        if (findAccount(accNo) != null) {
            System.out.println("Account already exists!");
            return;
        }

        System.out.print("Name: ");
        String name = sc.next();
        System.out.print("Initial Balance: ");
        double bal = sc.nextDouble();

        accounts.add(new SavingsAccount(accNo, name, bal));
        System.out.println("Account Created! Balance = " + bal);
        saveAccounts();
    }

    private static void deposit() {
        System.out.print("Enter Account No: ");
        String accNo = sc.next();
        Account acc = findAccount(accNo);
        if (acc == null) {
            System.out.println("Account not found!");
            return;
        }
        System.out.print("Enter Amount to Deposit: ");
        double amt = sc.nextDouble();
        acc.deposit(amt);
        System.out.println("Deposit Successful! Updated Balance = " + acc.getBalance());
        saveAccounts();
    }

    private static void withdraw() {
        System.out.print("Enter Account No: ");
        String accNo = sc.next();
        Account acc = findAccount(accNo);
        if (acc == null) {
            System.out.println("Account not found!");
            return;
        }
        System.out.print("Enter Amount to Withdraw: ");
        double amt = sc.nextDouble();
        acc.withdraw(amt);
        System.out.println("Updated Balance = " + acc.getBalance());
        saveAccounts();
    }

    private static void showAccounts() {
        if (accounts.isEmpty()) {
            System.out.println("No accounts found.");
        } else {
            System.out.println("\n--- Account List ---");
            System.out.printf("%-10s %-15s %-10s%n", "AccountNo", "Name", "Balance");
            for (Account acc : accounts) {
                System.out.printf("%-10s %-15s %-10.2f%n", acc.getAccNo(), acc.getName(), acc.getBalance());
            }
        }
    }

    private static Account findAccount(String accNo) {
        for (Account acc : accounts) {
            if (acc.getAccNo().equals(accNo)) return acc;
        }
        return null;
    }

    private static void saveAccounts() {
        try (PrintWriter pw = new PrintWriter(new FileWriter(FILE_NAME))) {
            pw.println("AccountNo,Name,Balance");
            for (Account acc : accounts) {
                pw.println(acc.getAccNo() + "," + acc.getName() + "," + acc.getBalance());
            }
        } catch (IOException e) {
            System.out.println("Error saving accounts: " + e.getMessage());
        }
    }

    private static void loadAccounts() {
        File file = new File(FILE_NAME);
        if (!file.exists()) return;

        try (Scanner fileScanner = new Scanner(file)) {
            boolean firstLine = true;
            while (fileScanner.hasNextLine()) {
                String line = fileScanner.nextLine().trim();
                if (line.isEmpty()) continue;
                if (firstLine) { firstLine = false; continue; } // skip header
                String[] data = line.split(",");
                if (data.length == 3) {
                    String accNo = data[0];
                    String name = data[1];
                    double bal = Double.parseDouble(data[2]);
                    accounts.add(new SavingsAccount(accNo, name, bal));
                }
            }
        } catch (IOException | NumberFormatException e) {
            System.out.println("Error loading accounts: " + e.getMessage());
        }
    }
}

abstract class Account {
    private String accNo, name;
    protected double balance;

    public Account(String accNo, String name, double balance) {
        this.accNo = accNo;
        this.name = name;
        this.balance = balance;
    }

    public String getAccNo() { return accNo; }
    public String getName() { return name; }
    public double getBalance() { return balance; }

    public abstract void deposit(double amt);
    public abstract void withdraw(double amt);
}

class SavingsAccount extends Account {
    public SavingsAccount(String accNo, String name, double balance) {
        super(accNo, name, balance);
    }

    @Override
    public void deposit(double amt) {
        if (amt > 0) balance += amt;
        else System.out.println("Invalid deposit amount!");
    }

    @Override
    public void withdraw(double amt) {
        if (amt <= 0) System.out.println("Invalid withdrawal amount!");
        else if (balance >= amt) balance -= amt;
        else System.out.println("Insufficient Balance!");
}
}
