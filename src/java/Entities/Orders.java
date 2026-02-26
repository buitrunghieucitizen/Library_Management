package Entities;

public class Orders {
    private int OrderID;
    private int StudentID;
    private int StaffID;
    private String OrderDate;
    private double TotalAmount;
    private String Status;

    public Orders() {
    }

    public Orders(int OrderID, int StudentID, int StaffID, String OrderDate, double TotalAmount, String Status) {
        this.OrderID = OrderID;
        this.StudentID = StudentID;
        this.StaffID = StaffID;
        this.OrderDate = OrderDate;
        this.TotalAmount = TotalAmount;
        this.Status = Status;
    }

    public Orders(int StudentID, int StaffID, String OrderDate, double TotalAmount, String Status) {
        this.StudentID = StudentID;
        this.StaffID = StaffID;
        this.OrderDate = OrderDate;
        this.TotalAmount = TotalAmount;
        this.Status = Status;
    }

    public int getOrderID() {
        return OrderID;
    }

    public void setOrderID(int OrderID) {
        this.OrderID = OrderID;
    }

    public int getStudentID() {
        return StudentID;
    }

    public void setStudentID(int StudentID) {
        this.StudentID = StudentID;
    }

    public int getStaffID() {
        return StaffID;
    }

    public void setStaffID(int StaffID) {
        this.StaffID = StaffID;
    }

    public String getOrderDate() {
        return OrderDate;
    }

    public void setOrderDate(String OrderDate) {
        this.OrderDate = OrderDate;
    }

    public double getTotalAmount() {
        return TotalAmount;
    }

    public void setTotalAmount(double TotalAmount) {
        this.TotalAmount = TotalAmount;
    }

    public String getStatus() {
        return Status;
    }

    public void setStatus(String Status) {
        this.Status = Status;
    }

    @Override
    public String toString() {
        return "Orders{" + "OrderID=" + OrderID + ", StudentID=" + StudentID + ", StaffID=" + StaffID
                + ", OrderDate=" + OrderDate + ", TotalAmount=" + TotalAmount + ", Status=" + Status + '}';
    }
}
