package Entities;

public class Role {
    private int RoleID;
    private String RoleName;

    public Role() {
    }

    public Role(int RoleID, String RoleName) {
        this.RoleID = RoleID;
        this.RoleName = RoleName;
    }

    public Role(String RoleName) {
        this.RoleName = RoleName;
    }

    public int getRoleID() {
        return RoleID;
    }

    public void setRoleID(int RoleID) {
        this.RoleID = RoleID;
    }

    public String getRoleName() {
        return RoleName;
    }

    public void setRoleName(String RoleName) {
        this.RoleName = RoleName;
    }

    @Override
    public String toString() {
        return "Role{" + "RoleID=" + RoleID + ", RoleName=" + RoleName + '}';
    }
}
