package ViewModel;

import Entities.Staff;

public class StaffListRow {
    private final Staff staff;
    private final String roleNames;

    public StaffListRow(Staff staff, String roleNames) {
        this.staff = staff;
        this.roleNames = roleNames;
    }

    public Staff getStaff() {
        return staff;
    }

    public String getRoleNames() {
        return roleNames;
    }
}
