package Entities;

public class OrderDetail {
    private int OrderID;
    private int BookID;
    private int Quantity;
    private double UnitPrice;

    public OrderDetail() {
    }

    public OrderDetail(int OrderID, int BookID, int Quantity, double UnitPrice) {
        this.OrderID = OrderID;
        this.BookID = BookID;
        this.Quantity = Quantity;
        this.UnitPrice = UnitPrice;
    }

    public int getOrderID() {
        return OrderID;
    }

    public void setOrderID(int OrderID) {
        this.OrderID = OrderID;
    }

    public int getBookID() {
        return BookID;
    }

    public void setBookID(int BookID) {
        this.BookID = BookID;
    }

    public int getQuantity() {
        return Quantity;
    }

    public void setQuantity(int Quantity) {
        this.Quantity = Quantity;
    }

    public double getUnitPrice() {
        return UnitPrice;
    }

    public void setUnitPrice(double UnitPrice) {
        this.UnitPrice = UnitPrice;
    }

    @Override
    public String toString() {
        return "OrderDetail{" + "OrderID=" + OrderID + ", BookID=" + BookID + ", Quantity=" + Quantity + ", UnitPrice="
                + UnitPrice + '}';
    }
}
