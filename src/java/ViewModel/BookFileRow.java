package ViewModel;

public class BookFileRow {
    private final int bookFileID;
    private final String bookName;
    private final String staffName;
    private final String fileName;
    private final String fileUrl;
    private final String fileType;
    private final long fileSize;
    private final String uploadAt;
    private final boolean active;

    public BookFileRow(int bookFileID, String bookName, String staffName, String fileName, String fileUrl,
            String fileType, long fileSize, String uploadAt, boolean active) {
        this.bookFileID = bookFileID;
        this.bookName = bookName;
        this.staffName = staffName;
        this.fileName = fileName;
        this.fileUrl = fileUrl;
        this.fileType = fileType;
        this.fileSize = fileSize;
        this.uploadAt = uploadAt;
        this.active = active;
    }

    public int getBookFileID() {
        return bookFileID;
    }

    public String getBookName() {
        return bookName;
    }

    public String getStaffName() {
        return staffName;
    }

    public String getFileName() {
        return fileName;
    }

    public String getFileUrl() {
        return fileUrl;
    }

    public String getFileType() {
        return fileType;
    }

    public long getFileSize() {
        return fileSize;
    }

    public String getUploadAt() {
        return uploadAt;
    }

    public boolean isActive() {
        return active;
    }
}
