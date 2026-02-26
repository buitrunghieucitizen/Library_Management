package Entities;

public class Student {
    private int StudentID;
    private String StudentName;
    private String Email;
    private String Phone;

    public Student() {
    }

    public Student(int StudentID, String StudentName, String Email, String Phone) {
        this.StudentID = StudentID;
        this.StudentName = StudentName;
        this.Email = Email;
        this.Phone = Phone;
    }

    public Student(String StudentName, String Email, String Phone) {
        this.StudentName = StudentName;
        this.Email = Email;
        this.Phone = Phone;
    }

    public int getStudentID() {
        return StudentID;
    }

    public void setStudentID(int StudentID) {
        this.StudentID = StudentID;
    }

    public String getStudentName() {
        return StudentName;
    }

    public void setStudentName(String StudentName) {
        this.StudentName = StudentName;
    }

    public String getEmail() {
        return Email;
    }

    public void setEmail(String Email) {
        this.Email = Email;
    }

    public String getPhone() {
        return Phone;
    }

    public void setPhone(String Phone) {
        this.Phone = Phone;
    }

    @Override
    public String toString() {
        return "Student{" + "StudentID=" + StudentID + ", StudentName=" + StudentName + ", Email=" + Email + ", Phone="
                + Phone + '}';
    }
}
