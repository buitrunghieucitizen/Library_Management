package ViewModel;

public class OrderRow {
    private final int orderID;
    private final String studentName;
    private final String staffName;
    private final String orderDate;
    private final double totalAmount;
    private final String status;
    private final String items;

    public OrderRow(int orderID, String studentName, String staffName, String orderDate,
            double totalAmount, String status, String items) {
        this.orderID = orderID;
        this.studentName = studentName;
        this.staffName = staffName;
        this.orderDate = orderDate;
        this.totalAmount = totalAmount;
        this.status = status;
        this.items = items;
    }

    public int getOrderID() {
        return orderID;
    }

    public String getStudentName() {
        return studentName;
    }

    public String getStaffName() {
        return staffName;
    }

    public String getOrderDate() {
        return orderDate;
    }

    public double getTotalAmount() {
        return totalAmount;
    }

    public String getStatus() {
        return status;
    }

    public String getItems() {
        return items;
    }
}
