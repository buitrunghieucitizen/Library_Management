package ViewModel;

import Entities.Staff;
import java.util.List;

public class StaffListRow {
    private final Staff staff;
    private final List<Integer> roleIds;
    private final List<String> roleLabels;
    private final String roleNames;

    public StaffListRow(Staff staff, List<Integer> roleIds, List<String> roleLabels) {
        this.staff = staff;
        this.roleIds = roleIds == null ? List.of() : List.copyOf(roleIds);
        this.roleLabels = roleLabels == null ? List.of() : List.copyOf(roleLabels);
        this.roleNames = this.roleLabels.isEmpty() ? "Chưa gán vai trò" : String.join(", ", this.roleLabels);
    }

    public Staff getStaff() {
        return staff;
    }

    public List<Integer> getRoleIds() {
        return roleIds;
    }

    public List<String> getRoleLabels() {
        return roleLabels;
    }

    public String getRoleNames() {
        return roleNames;
    }

    public int getRoleCount() {
        return roleIds.size();
    }

    public boolean hasRole(int roleId) {
        return roleIds.contains(roleId);
    }

    public boolean hasAnyRole(int... candidateRoleIds) {
        if (candidateRoleIds == null) {
            return false;
        }
        for (int candidateRoleId : candidateRoleIds) {
            if (hasRole(candidateRoleId)) {
                return true;
            }
        }
        return false;
    }

    public boolean hasEmail() {
        return staff != null && staff.getEmail() != null && !staff.getEmail().trim().isEmpty();
    }

    public boolean isMissingEmail() {
        return !hasEmail();
    }

    public boolean isMultiRole() {
        return getRoleCount() > 1;
    }

    public String getDisplayName() {
        if (staff == null || staff.getStaffName() == null || staff.getStaffName().trim().isEmpty()) {
            return getUsernameDisplay();
        }
        return staff.getStaffName().trim();
    }

    public String getUsernameDisplay() {
        if (staff == null || staff.getUsername() == null || staff.getUsername().trim().isEmpty()) {
            return "unknown";
        }
        return staff.getUsername().trim();
    }

    public String getEmailDisplay() {
        return hasEmail() ? staff.getEmail().trim() : "Chưa cập nhật";
    }

    public String getDisplayInitial() {
        String source = getDisplayName();
        if (source.isEmpty()) {
            return "#";
        }
        return source.substring(0, 1).toUpperCase();
    }
}
