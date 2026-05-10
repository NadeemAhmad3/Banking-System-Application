package com.bank.controller;

import com.bank.model.BankLoanModel;
import com.bank.model.BankUtil;
import com.bank.model.Loan;
import com.bank.model.User;
import com.bank.view.BankLoanView;

import java.util.List;

public class BankLoan {
    private final BankLoanView view;
    private final BankLoanModel model;
    private final User currentUser;

    public BankLoan(User currentUser) {
        this.view = new BankLoanView();
        this.model = new BankLoanModel();
        this.currentUser = currentUser;
    }

    public void run() {
        boolean exit = false;
        while (!exit) {
            view.displayLoanMenu();
            String choice = BankUtil.scanner.nextLine();
            switch (choice) {
                case "1":
                    handleLoanApplication();
                    break;
                case "2":
                    handleViewLoans();
                    break;
                case "3":
                    handlePayInstallment();
                    break;
                case "4":
                    handleLoanCalculator();
                    break;
                case "5":
                    exit = true;
                    break;
                default:
                    BankUtil.createMessage("Invalid Choice! Please enter a number from 1 to 5.");
                    break;
            }
        }
    }

    private void handleLoanApplication() {
        view.displayLoanApplicationForm();
        double amount = view.getRequestedAmount();
        
        if (amount <= 0) {
            BankUtil.createMessage("Amount must be greater than zero.");
            return;
        }

        double maxBorrowing = model.calculateMaxBorrowingPower(currentUser);
        if (amount > maxBorrowing) {
            BankUtil.createMessage("Sorry, your requested amount exceeds your max borrowing power of $" + maxBorrowing);
            return;
        }

        int term = view.getTermMonths();
        if (term <= 0 || term > 120) {
            BankUtil.createMessage("Invalid term duration. Must be between 1 and 120 months.");
            return;
        }

        Loan newLoan = new Loan(currentUser.getUsername(), amount, 5.5, term, "PENDING");
        model.applyForLoan(newLoan);
    }

    private void handleViewLoans() {
        List<Loan> userLoans = model.getUserLoans(currentUser.getUsername());
        view.showUserLoans(userLoans);
    }

    private void handlePayInstallment() {
        List<Loan> userLoans = model.getUserLoans(currentUser.getUsername());
        if(userLoans.isEmpty()) {
            BankUtil.createMessage("You have no active loans to pay off.");
            return;
        }
        
        view.showUserLoans(userLoans);
        int loanId = view.getLoanIdForPayment();
        if (loanId == -1) return;

        double paymentAmount = view.getPaymentAmount();
        if (paymentAmount <= 0) {
            BankUtil.createMessage("Payment amount must be positive.");
            return;
        }

        model.payLoanInstallment(loanId, paymentAmount);
    }

    private void handleLoanCalculator() {
        view.displayLoanCalculatorMenu();
        double amount = view.getRequestedAmount();
        int term = view.getTermMonths();
        
        if(amount > 0 && term > 0) {
            Loan mockLoan = new Loan("mockUser", amount, 5.5, term, "MOCK");
            view.displayCalculatorResult(mockLoan);
        } else {
            BankUtil.createMessage("Calculation aborted due to invalid inputs.");
        }
    }
}
