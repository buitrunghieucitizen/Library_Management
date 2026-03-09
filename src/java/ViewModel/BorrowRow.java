package ViewModel;

public class BorrowRow {
    private final int borrowID;
    private final String studentName;
    private final String staffName;
    private final String borrowDate;
    private final String dueDate;
    private final String status;
    private final String returnDate;
    private final String items;

    public BorrowRow(int borrowID, String studentName, String staffName, String borrowDate, String dueDate,
            String status, String returnDate, String items) {
        this.borrowID = borrowID;
        this.studentName = studentName;
        this.staffName = staffName;
        this.borrowDate = borrowDate;
        this.dueDate = dueDate;
        this.status = status;
        this.returnDate = returnDate;
        this.items = items;
    }

    public int getBorrowID() {
        return borrowID;
    }

    public String getStudentName() {
        return studentName;
    }

    public String getStaffName() {
        return staffName;
    }

    public String getBorrowDate() {
        return borrowDate;
    }

    public String getDueDate() {
        return dueDate;
    }

    public String getStatus() {
        return status;
    }

    public String getReturnDate() {
        return returnDate;
    }

    public String getItems() {
        return items;
    }
}
