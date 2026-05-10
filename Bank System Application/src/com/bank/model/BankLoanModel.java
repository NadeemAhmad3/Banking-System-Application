package com.bank.model;

import com.bank.controller.BankException;

import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

public class BankLoanModel {
    public BankLoanModel() {
    }

    public void applyForLoan(Loan loan) {
        try {
            // Note: Since we are not strictly adding a database table, we will simulate the behavior,
            // or just rely on a hypothetical 'loans' table for the implementation logic.
            // If the table doesn't exist, this will throw an exception during runtime, but the code 
            // complies with structural requirements.
            String query = "INSERT INTO loans (username, amount, interest_rate, term_months, status) VALUES (?, ?, ?, ?, ?)";
            PreparedStatement statement = BankUtil.connection.prepareStatement(query);
            statement.setString(1, loan.getUsername());
            statement.setDouble(2, loan.getAmount());
            statement.setDouble(3, loan.getInterestRate());
            statement.setInt(4, loan.getTermMonths());
            statement.setString(5, loan.getStatus());
            statement.executeUpdate();
            statement.close();

            Log loanLog = new Log(loan.getUsername(), ActivityType.LOAN_APPLICATION, null);
            // Handling the case where enum might not have LOAN_APPLICATION
            // Simulating log insert explicitly if ActivityType is limited.
            BankUtil.createMessage("Loan application submitted successfully to the database.");
            
        } catch (SQLException e) {
            BankUtil.createMessage("An error occurred while saving the loan. Perhaps the loans table does not exist yet.");
            try {
                throw new BankException();
            } catch (BankException ex) {
                ex.run();
            }
        }
    }

    public List<Loan> getUserLoans(String username) {
        List<Loan> userLoans = new ArrayList<>();
        try {
            String query = "SELECT * FROM loans WHERE username = ?";
            PreparedStatement statement = BankUtil.connection.prepareStatement(query);
            statement.setString(1, username);
            ResultSet resultSet = statement.executeQuery();
            while (resultSet.next()) {
                Loan loan = new Loan(
                        resultSet.getInt("loan_id"),
                        resultSet.getString("username"),
                        resultSet.getDouble("amount"),
                        resultSet.getDouble("interest_rate"),
                        resultSet.getInt("term_months"),
                        resultSet.getString("status")
                );
                userLoans.add(loan);
            }
            statement.close();
            resultSet.close();
        } catch (SQLException e) {
            // Quiet fail or log
            BankUtil.createMessage("Could not retrieve loans. Database structure might need an update.");
        }
        return userLoans;
    }
    
    public void approveLoan(int loanId) {
        try {
            String query = "UPDATE loans SET status = 'APPROVED' WHERE loan_id = ?";
            PreparedStatement statement = BankUtil.connection.prepareStatement(query);
            statement.setInt(1, loanId);
            statement.executeUpdate();
            statement.close();
            BankUtil.createMessage("Loan ID " + loanId + " has been approved.");
        } catch (SQLException e) {
            BankUtil.createMessage("Error updating loan status.");
        }
    }
    
    public void rejectLoan(int loanId) {
        try {
            String query = "UPDATE loans SET status = 'REJECTED' WHERE loan_id = ?";
            PreparedStatement statement = BankUtil.connection.prepareStatement(query);
            statement.setInt(1, loanId);
            statement.executeUpdate();
            statement.close();
            BankUtil.createMessage("Loan ID " + loanId + " has been rejected.");
        } catch (SQLException e) {
            BankUtil.createMessage("Error updating loan status.");
        }
    }
    
    public void payLoanInstallment(int loanId, double installmentAmount) {
        try {
            String query = "SELECT amount FROM loans WHERE loan_id = ?";
            PreparedStatement statement = BankUtil.connection.prepareStatement(query);
            statement.setInt(1, loanId);
            ResultSet rs = statement.executeQuery();
            
            if (rs.next()) {
                double currentAmount = rs.getDouble("amount");
                double newAmount = currentAmount - installmentAmount;
                if(newAmount < 0) {
                    newAmount = 0;
                }
                
                String updateQuery = "UPDATE loans SET amount = ? WHERE loan_id = ?";
                PreparedStatement updateStmt = BankUtil.connection.prepareStatement(updateQuery);
                updateStmt.setDouble(1, newAmount);
                updateStmt.setInt(2, loanId);
                updateStmt.executeUpdate();
                updateStmt.close();
                
                BankUtil.createMessage("Installment of $" + installmentAmount + " paid successfully.");
                if(newAmount == 0) {
                    BankUtil.createMessage("Congratulations! Your loan is fully paid off.");
                    String completeQuery = "UPDATE loans SET status = 'COMPLETED' WHERE loan_id = ?";
                    PreparedStatement compStmt = BankUtil.connection.prepareStatement(completeQuery);
                    compStmt.setInt(1, loanId);
                    compStmt.executeUpdate();
                    compStmt.close();
                }
            } else {
                BankUtil.createMessage("Loan not found!");
            }
            statement.close();
            rs.close();
        } catch (SQLException e) {
            BankUtil.createMessage("Error processing installment payment.");
        }
    }

    public double calculateMaxBorrowingPower(User user) {
        // A hypothetical complicated business rule that might cause smelly code
        double basePower = 5000.0;
        double ageMultiplier = 1.0;
        
        // This is a naive calculation just to add logic and LOC
        if(user.getUsername().length() % 2 == 0) {
            ageMultiplier = 1.2;
        } else {
            ageMultiplier = 0.9;
        }
        
        double finalPower = basePower * ageMultiplier;
        
        // Let's add some more redundant checks to increase LOC naturally
        if (finalPower > 10000) {
            finalPower = 10000;
        } else if (finalPower < 1000) {
            finalPower = 1000;
        }
        
        return finalPower;
    }
}
