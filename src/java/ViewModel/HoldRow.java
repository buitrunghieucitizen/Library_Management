package ViewModel;

/**
 * View model for displaying hold info in JSP (joins BookHold + Book + Student).
 */
public class HoldRow {

    private int holdID;
    private int studentID;
    private String studentName;
    private String studentEmail;
    private int bookID;
    private String bookName;
    private String holdDate;
    private String status;
    private String notifiedDate;
    private String expireDate;
    private int bookAvailable;

    public HoldRow() {
    }

    public HoldRow(int holdID, int studentID, String studentName, String studentEmail,
            int bookID, String bookName, String holdDate, String status,
            String notifiedDate, String expireDate, int bookAvailable) {
        this.holdID = holdID;
        this.studentID = studentID;
        this.studentName = studentName;
        this.studentEmail = studentEmail;
        this.bookID = bookID;
        this.bookName = bookName;
        this.holdDate = holdDate;
        this.status = status;
        this.notifiedDate = notifiedDate;
        this.expireDate = expireDate;
        this.bookAvailable = bookAvailable;
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

    public String getStudentName() {
        return studentName;
    }

    public void setStudentName(String studentName) {
        this.studentName = studentName;
    }

    public String getStudentEmail() {
        return studentEmail;
    }

    public void setStudentEmail(String studentEmail) {
        this.studentEmail = studentEmail;
    }

    public int getBookID() {
        return bookID;
    }

    public void setBookID(int bookID) {
        this.bookID = bookID;
    }

    public String getBookName() {
        return bookName;
    }

    public void setBookName(String bookName) {
        this.bookName = bookName;
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

    public int getBookAvailable() {
        return bookAvailable;
    }

    public void setBookAvailable(int bookAvailable) {
        this.bookAvailable = bookAvailable;
    }
}
