package Entities;

public class Staff {

    private int staffID;
    private String staffName;
    private String username;
    private String email;
    private String password;

    public Staff() {
    }

    public Staff(int staffID, String staffName, String username, String password) {
        this(staffID, staffName, username, null, password);
    }

    public Staff(int staffID, String staffName, String username, String email, String password) {
        this.staffID = staffID;
        this.staffName = staffName;
        this.username = username;
        this.email = email;
        this.password = password;
    }

    public Staff(String staffName, String username, String password) {
        this(staffName, username, null, password);
    }

    public Staff(String staffName, String username, String email, String password) {
        this.staffName = staffName;
        this.username = username;
        this.email = email;
        this.password = password;
    }

    public int getStaffID() {
        return staffID;
    }

    public void setStaffID(int staffID) {
        this.staffID = staffID;
    }

    public String getStaffName() {
        return staffName;
    }

    public void setStaffName(String staffName) {
        this.staffName = staffName;
    }

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    @Override
    public String toString() {
        return "Staff{staffID=" + staffID + ", staffName=" + staffName
                + ", username=" + username + ", email=" + email + "}";
    }
}
