package entities;

public class BookFile {
    private int BookFileID;
    private int BookID;
    private int StaffID;
    private String FileName;
    private String FileUrl;
    private String FileType;
    private long FileSize;
    private String UploadAt;
    private boolean IsActive;

    public BookFile() {
    }

    public BookFile(int BookFileID, int BookID, int StaffID, String FileName, String FileUrl,
            String FileType, long FileSize, String UploadAt, boolean IsActive) {
        this.BookFileID = BookFileID;
        this.BookID = BookID;
        this.StaffID = StaffID;
        this.FileName = FileName;
        this.FileUrl = FileUrl;
        this.FileType = FileType;
        this.FileSize = FileSize;
        this.UploadAt = UploadAt;
        this.IsActive = IsActive;
    }

    public BookFile(int BookID, int StaffID, String FileName, String FileUrl, String FileType, long FileSize) {
        this.BookID = BookID;
        this.StaffID = StaffID;
        this.FileName = FileName;
        this.FileUrl = FileUrl;
        this.FileType = FileType;
        this.FileSize = FileSize;
        this.IsActive = true;
    }

    public int getBookFileID() {
        return BookFileID;
    }

    public void setBookFileID(int BookFileID) {
        this.BookFileID = BookFileID;
    }

    public int getBookID() {
        return BookID;
    }

    public void setBookID(int BookID) {
        this.BookID = BookID;
    }

    public int getStaffID() {
        return StaffID;
    }

    public void setStaffID(int StaffID) {
        this.StaffID = StaffID;
    }

    public String getFileName() {
        return FileName;
    }

    public void setFileName(String FileName) {
        this.FileName = FileName;
    }

    public String getFileUrl() {
        return FileUrl;
    }

    public void setFileUrl(String FileUrl) {
        this.FileUrl = FileUrl;
    }

    public String getFileType() {
        return FileType;
    }

    public void setFileType(String FileType) {
        this.FileType = FileType;
    }

    public long getFileSize() {
        return FileSize;
    }

    public void setFileSize(long FileSize) {
        this.FileSize = FileSize;
    }

    public String getUploadAt() {
        return UploadAt;
    }

    public void setUploadAt(String UploadAt) {
        this.UploadAt = UploadAt;
    }

    public boolean getIsActive() {
        return IsActive;
    }

    public void setIsActive(boolean IsActive) {
        this.IsActive = IsActive;
    }

    @Override
    public String toString() {
        return "BookFile{" + "BookFileID=" + BookFileID + ", BookID=" + BookID + ", StaffID=" + StaffID
                + ", FileName=" + FileName + ", FileUrl=" + FileUrl + ", FileType=" + FileType
                + ", FileSize=" + FileSize + ", UploadAt=" + UploadAt + ", IsActive=" + IsActive + '}';
    }
}
