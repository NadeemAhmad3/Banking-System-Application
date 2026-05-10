package com.bank.view;

import com.bank.model.BankUtil;
import com.bank.model.Loan;

import java.util.List;
import java.util.Scanner;

public class BankLoanView {
    public BankLoanView() {
    }

    public void displayLoanMenu() {
        System.out.println("\n");
        BankUtil.createHeader("Loan Services Menu");
        System.out.println("1- Apply for a new Loan");
        System.out.println("2- View My Loans");
        System.out.println("3- Pay Loan Installment");
        System.out.println("4- Loan Calculator");
        System.out.println("5- Return to User Profile");
        System.out.println();
        BankUtil.showTakeFunctionNumber();
    }

    public void displayLoanApplicationForm() {
        System.out.println("\n");
        BankUtil.createHeader("Loan Application Form");
        System.out.println("Please note: The standard interest rate is currently 5.5%");
    }

    public double getRequestedAmount() {
        System.out.print("Enter requested loan amount ($): ");
        double amount = 0;
        try {
            amount = Double.parseDouble(BankUtil.scanner.nextLine());
        } catch (NumberFormatException e) {
            BankUtil.createMessage("Invalid amount format. Defaulting to $0.0");
        }
        return amount;
    }

    public int getTermMonths() {
        System.out.print("Enter preferred term duration in months (e.g., 12, 24, 36, 60): ");
        int months = 0;
        try {
            months = Integer.parseInt(BankUtil.scanner.nextLine());
        } catch (NumberFormatException e) {
            BankUtil.createMessage("Invalid input for term months. Defaulting to 12.");
            months = 12;
        }
        return months;
    }

    public void displayLoanCalculatorMenu() {
        System.out.println("\n");
        BankUtil.createHeader("Loan Calculator");
        System.out.println("Use this tool to estimate your monthly payments.");
    }

    public void displayCalculatorResult(Loan mockLoan) {
        BankUtil.createMessage("Estimation Results");
        System.out.println("Requested Amount: $" + mockLoan.getAmount());
        System.out.println("Term period: " + mockLoan.getTermMonths() + " months");
        System.out.println("Interest Rate: " + mockLoan.getInterestRate() + "%");
        System.out.println("Estimated Monthly Payment: $" + String.format("%.2f", mockLoan.calculateMonthlyPayment()));
        System.out.println("Total Amount to be Paid: $" + String.format("%.2f", mockLoan.calculateTotalRepayment()));
        BankUtil.createMessage("End of Estimation");
    }

    public void showUserLoans(List<Loan> loans) {
        System.out.println("\n");
        BankUtil.createHeader("My Active & Past Loans");
        if (loans == null || loans.isEmpty()) {
            BankUtil.createMessage("You currently have no loans associated with your account.");
        } else {
            for (Loan loan : loans) {
                System.out.println(loan.toString());
            }
        }
    }

    public int getLoanIdForPayment() {
        System.out.print("Enter the Loan ID you wish to make a payment towards: ");
        try {
            return Integer.parseInt(BankUtil.scanner.nextLine());
        } catch (NumberFormatException e) {
            BankUtil.createMessage("Invalid input.");
            return -1;
        }
    }

    public double getPaymentAmount() {
        System.out.print("Enter payment amount ($): ");
        try {
            return Double.parseDouble(BankUtil.scanner.nextLine());
        } catch (NumberFormatException e) {
            BankUtil.createMessage("Invalid input.");
            return 0.0;
        }
    }
}
