package Entities;

public class Staff {
    private int StaffID;
    private String StaffName;

    public Staff() {
    }

    public Staff(int StaffID, String StaffName) {
        this.StaffID = StaffID;
        this.StaffName = StaffName;
    }

    public Staff(String StaffName) {
        this.StaffName = StaffName;
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

    @Override
    public String toString() {
        return "Staff{" + "StaffID=" + StaffID + ", StaffName=" + StaffName + '}';
    }
}
