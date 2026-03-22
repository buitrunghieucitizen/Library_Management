package Entities;

public class BookHold {

    private int holdID;
    private int studentID;
    private int bookID;
    private String holdDate;
    private String status;       // Waiting | Notified | Fulfilled | Cancelled | Expired
    private String notifiedDate;
    private String expireDate;
    private String note;

    public BookHold() {
    }

    public BookHold(int holdID, int studentID, int bookID, String holdDate,
            String status, String notifiedDate, String expireDate, String note) {
        this.holdID = holdID;
        this.studentID = studentID;
        this.bookID = bookID;
        this.holdDate = holdDate;
        this.status = status;
        this.notifiedDate = notifiedDate;
        this.expireDate = expireDate;
        this.note = note;
    }

    public int getHoldID() {
        return holdID;
    }

    public void setHoldID(int holdID) {
        this.holdID = holdID;
    }

    public int getStudentID() {
        return studentID;
    }

    public void setStudentID(int studentID) {
        this.studentID = studentID;
    }

    public int getBookID() {
        return bookID;
    }

    public void setBookID(int bookID) {
        this.bookID = bookID;
    }

    public String getHoldDate() {
        return holdDate;
    }

    public void setHoldDate(String holdDate) {
        this.holdDate = holdDate;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public String getNotifiedDate() {
        return notifiedDate;
    }

    public void setNotifiedDate(String notifiedDate) {
        this.notifiedDate = notifiedDate;
    }

    public String getExpireDate() {
        return expireDate;
    }

    public void setExpireDate(String expireDate) {
        this.expireDate = expireDate;
    }

    public String getNote() {
        return note;
    }

    public void setNote(String note) {
        this.note = note;
    }

    @Override
    public String toString() {
        return "BookHold{holdID=" + holdID + ", studentID=" + studentID
                + ", bookID=" + bookID + ", status=" + status + '}';
    }
}
