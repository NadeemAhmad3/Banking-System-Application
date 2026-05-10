package com.bank.model;

public class Loan {
    private int loanId;
    private String username;
    private double amount;
    private double interestRate;
    private int termMonths;
    private String status;

    public Loan(String username, double amount, double interestRate, int termMonths, String status) {
        this.username = username;
        this.amount = amount;
        this.interestRate = interestRate;
        this.termMonths = termMonths;
        this.status = status;
    }

    public Loan(int loanId, String username, double amount, double interestRate, int termMonths, String status) {
        this.loanId = loanId;
        this.username = username;
        this.amount = amount;
        this.interestRate = interestRate;
        this.termMonths = termMonths;
        this.status = status;
    }

    public int getLoanId() {
        return loanId;
    }

    public void setLoanId(int loanId) {
        this.loanId = loanId;
    }

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public double getAmount() {
        return amount;
    }

    public void setAmount(double amount) {
        this.amount = amount;
    }

    public double getInterestRate() {
        return interestRate;
    }

    public void setInterestRate(double interestRate) {
        this.interestRate = interestRate;
    }

    public int getTermMonths() {
        return termMonths;
    }

    public void setTermMonths(int termMonths) {
        this.termMonths = termMonths;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public double calculateMonthlyPayment() {
        double monthlyRate = (interestRate / 100) / 12;
        return (amount * monthlyRate) / (1 - Math.pow(1 + monthlyRate, -termMonths));
    }
    
    public double calculateTotalRepayment() {
        return calculateMonthlyPayment() * termMonths;
    }

    @Override
    public String toString() {
        return "Loan Details: \n" +
                "Loan ID: " + loanId + "\n" +
                "User: " + username + "\n" +
                "Amount: $" + amount + "\n" +
                "Interest Rate: " + interestRate + "%\n" +
                "Term: " + termMonths + " months\n" +
                "Monthly Payment: $" + String.format("%.2f", calculateMonthlyPayment()) + "\n" +
                "Status: " + status + "\n" +
                "...........................................................";
    }
}
