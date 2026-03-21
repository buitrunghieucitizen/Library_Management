package Entities;

public class Student {
    private int StudentID;
    private String StudentName;
    private String Email;
    private String Phone;
    private String AvatarUrl;
    private String ClassName;
    private String FacultyName;
    private String AccountStatus;
    private String CreatedAt;

    public Student() {
    }

    public Student(int StudentID, String StudentName, String Email, String Phone) {
        this(StudentID, StudentName, Email, Phone, null, null, null, null, null);
    }

    public Student(int StudentID, String StudentName, String Email, String Phone,
            String AvatarUrl, String ClassName, String FacultyName,
            String AccountStatus, String CreatedAt) {
        this.StudentID = StudentID;
        this.StudentName = StudentName;
        this.Email = Email;
        this.Phone = Phone;
        this.AvatarUrl = AvatarUrl;
        this.ClassName = ClassName;
        this.FacultyName = FacultyName;
        this.AccountStatus = AccountStatus;
        this.CreatedAt = CreatedAt;
    }

    public Student(String StudentName, String Email, String Phone) {
        this(StudentName, Email, Phone, null, null, null, null, null);
    }

    public Student(String StudentName, String Email, String Phone, String AvatarUrl,
            String ClassName, String FacultyName, String AccountStatus, String CreatedAt) {
        this.StudentName = StudentName;
        this.Email = Email;
        this.Phone = Phone;
        this.AvatarUrl = AvatarUrl;
        this.ClassName = ClassName;
        this.FacultyName = FacultyName;
        this.AccountStatus = AccountStatus;
        this.CreatedAt = CreatedAt;
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

    public String getAvatarUrl() {
        return AvatarUrl;
    }

    public void setAvatarUrl(String AvatarUrl) {
        this.AvatarUrl = AvatarUrl;
    }

    public String getClassName() {
        return ClassName;
    }

    public void setClassName(String ClassName) {
        this.ClassName = ClassName;
    }

    public String getFacultyName() {
        return FacultyName;
    }

    public void setFacultyName(String FacultyName) {
        this.FacultyName = FacultyName;
    }

    public String getAccountStatus() {
        return AccountStatus;
    }

    public void setAccountStatus(String AccountStatus) {
        this.AccountStatus = AccountStatus;
    }

    public String getCreatedAt() {
        return CreatedAt;
    }

    public void setCreatedAt(String CreatedAt) {
        this.CreatedAt = CreatedAt;
    }

    @Override
    public String toString() {
        return "Student{" + "StudentID=" + StudentID + ", StudentName=" + StudentName
                + ", Email=" + Email + ", Phone=" + Phone + ", AvatarUrl=" + AvatarUrl
                + ", ClassName=" + ClassName + ", FacultyName=" + FacultyName
                + ", AccountStatus=" + AccountStatus + ", CreatedAt=" + CreatedAt + '}';
    }
}
