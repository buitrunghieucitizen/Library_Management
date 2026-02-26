package Entities;

public class StaffRole {
    private int StaffID;
    private int RoleID;

    public StaffRole() {
    }

    public StaffRole(int StaffID, int RoleID) {
        this.StaffID = StaffID;
        this.RoleID = RoleID;
    }

    public int getStaffID() {
        return StaffID;
    }

    public void setStaffID(int StaffID) {
        this.StaffID = StaffID;
    }

    public int getRoleID() {
        return RoleID;
    }

    public void setRoleID(int RoleID) {
        this.RoleID = RoleID;
    }

    @Override
    public String toString() {
        return "StaffRole{" + "StaffID=" + StaffID + ", RoleID=" + RoleID + '}';
    }
}
