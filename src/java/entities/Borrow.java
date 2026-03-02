package entities;

public class Borrow {
    private int BorrowID;
    private int StudentID;
    private int StaffID;
    private String BorrowDate;
    private String DueDate;
    private String Status;
    private String ReturnDate;

    public Borrow() {
    }

    public Borrow(int BorrowID, int StudentID, int StaffID, String BorrowDate, String DueDate, String Status,
            String ReturnDate) {
        this.BorrowID = BorrowID;
        this.StudentID = StudentID;
        this.StaffID = StaffID;
        this.BorrowDate = BorrowDate;
        this.DueDate = DueDate;
        this.Status = Status;
        this.ReturnDate = ReturnDate;
    }

    public Borrow(int StudentID, int StaffID, String BorrowDate, String DueDate, String Status) {
        this.StudentID = StudentID;
        this.StaffID = StaffID;
        this.BorrowDate = BorrowDate;
        this.DueDate = DueDate;
        this.Status = Status;
    }

    public int getBorrowID() {
        return BorrowID;
    }

    public void setBorrowID(int BorrowID) {
        this.BorrowID = BorrowID;
    }

    public int getStudentID() {
        return StudentID;
    }

    public void setStudentID(int StudentID) {
        this.StudentID = StudentID;
    }

    public int getStaffID() {
        return StaffID;
    }

    public void setStaffID(int StaffID) {
        this.StaffID = StaffID;
    }

    public String getBorrowDate() {
        return BorrowDate;
    }

    public void setBorrowDate(String BorrowDate) {
        this.BorrowDate = BorrowDate;
    }

    public String getDueDate() {
        return DueDate;
    }

    public void setDueDate(String DueDate) {
        this.DueDate = DueDate;
    }

    public String getStatus() {
        return Status;
    }

    public void setStatus(String Status) {
        this.Status = Status;
    }

    public String getReturnDate() {
        return ReturnDate;
    }

    public void setReturnDate(String ReturnDate) {
        this.ReturnDate = ReturnDate;
    }

    @Override
    public String toString() {
        return "Borrow{" + "BorrowID=" + BorrowID + ", StudentID=" + StudentID + ", StaffID=" + StaffID
                + ", BorrowDate=" + BorrowDate + ", DueDate=" + DueDate + ", Status=" + Status
                + ", ReturnDate=" + ReturnDate + '}';
    }
}
