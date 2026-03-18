package Entities;

public class Staff {
    private int StaffID;
    private String StaffName;
    private String Username;
    private String Email;
    private String Password;

    public Staff() {
    }

    public Staff(int StaffID, String StaffName, String Username, String Password) {
        this(StaffID, StaffName, Username, null, Password);
    }

    public Staff(int StaffID, String StaffName, String Username, String Email, String Password) {
        this.StaffID = StaffID;
        this.StaffName = StaffName;
        this.Username = Username;
        this.Email = Email;
        this.Password = Password;
    }

    public Staff(String StaffName, String Username, String Password) {
        this(StaffName, Username, null, Password);
    }

    public Staff(String StaffName, String Username, String Email, String Password) {
        this.StaffName = StaffName;
        this.Username = Username;
        this.Email = Email;
        this.Password = Password;
    }

    public int getStaffID() {
        return StaffID;
    }

    public void setStaffID(int StaffID) {
        this.StaffID = StaffID;
    }

    public String getStaffName() {
        return StaffName;
    }

    public void setStaffName(String StaffName) {
        this.StaffName = StaffName;
    }

    public String getUsername() {
        return Username;
    }

    public void setUsername(String Username) {
        this.Username = Username;
    }

    public String getPassword() {
        return Password;
    }

    public String getEmail() {
        return Email;
    }

    public void setEmail(String Email) {
        this.Email = Email;
    }

    public void setPassword(String Password) {
        this.Password = Password;
    }

    @Override
    public String toString() {
        return "Staff{" + "StaffID=" + StaffID + ", StaffName=" + StaffName
                + ", Username=" + Username + ", Email=" + Email + '}';
    }
}
