/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package Entities;

/**
 *
 * @author Admin
 */
public class Fine {
    private int fineID;
    private int borrowID;
    private double amount;
    private String reason;
    private String createdDate;
    private String paidDate;
    private String status;

    public Fine() {}

    public Fine(int fineID, int borrowID, double amount, String reason, 
                String createdDate, String paidDate, String status) {
        this.fineID = fineID;
        this.borrowID = borrowID;
        this.amount = amount;
        this.reason = reason;
        this.createdDate = createdDate;
        this.paidDate = paidDate;
        this.status = status;
    }

    public int getFineID() { return fineID; }
    public void setFineID(int fineID) { this.fineID = fineID; }
    public int getBorrowID() { return borrowID; }
    public void setBorrowID(int borrowID) { this.borrowID = borrowID; }
    public double getAmount() { return amount; }
    public void setAmount(double amount) { this.amount = amount; }
    public String getReason() { return reason; }
    public void setReason(String reason) { this.reason = reason; }
    public String getCreatedDate() { return createdDate; }
    public void setCreatedDate(String createdDate) { this.createdDate = createdDate; }
    public String getPaidDate() { return paidDate; }
    public void setPaidDate(String paidDate) { this.paidDate = paidDate; }
    public String getStatus() { return status; }
    public void setStatus(String status) { this.status = status; }
}
