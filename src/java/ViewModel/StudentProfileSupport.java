package ViewModel;

public class StudentProfileSupport {
    private final boolean avatarUrlSupported;
    private final boolean classNameSupported;
    private final boolean facultyNameSupported;
    private final boolean accountStatusSupported;
    private final boolean createdAtSupported;

    public StudentProfileSupport(boolean avatarUrlSupported, boolean classNameSupported,
            boolean facultyNameSupported, boolean accountStatusSupported,
            boolean createdAtSupported) {
        this.avatarUrlSupported = avatarUrlSupported;
        this.classNameSupported = classNameSupported;
        this.facultyNameSupported = facultyNameSupported;
        this.accountStatusSupported = accountStatusSupported;
        this.createdAtSupported = createdAtSupported;
    }

    public boolean isAvatarUrlSupported() {
        return avatarUrlSupported;
    }

    public boolean isClassNameSupported() {
        return classNameSupported;
    }

    public boolean isFacultyNameSupported() {
        return facultyNameSupported;
    }

    public boolean isAccountStatusSupported() {
        return accountStatusSupported;
    }

    public boolean isCreatedAtSupported() {
        return createdAtSupported;
    }
}
