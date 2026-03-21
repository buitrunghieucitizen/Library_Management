/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package Entities;

/**
 *
 * @author Administrator
 */
public class Book {
    private int BookID;
    private String BookName;
    private int Quantity;
    private int Available;
    private int CategoryID;
    private int PublisherID;
    private String Description;
    private String ShelfLocation;
    private String ImageUrl;

    public Book(int BookID, String BookName, int Quantity, int Available, int CategoryID, int PublisherID) {
        this(BookID, BookName, Quantity, Available, CategoryID, PublisherID, null, null, null);
    }

    public Book(int BookID, String BookName, int Quantity, int Available, int CategoryID, int PublisherID, String ImageUrl) {
        this(BookID, BookName, Quantity, Available, CategoryID, PublisherID, null, null, ImageUrl);
    }

    public Book(int BookID, String BookName, int Quantity, int Available, int CategoryID, int PublisherID,
            String Description, String ShelfLocation, String ImageUrl) {
        this.BookID = BookID;
        this.BookName = BookName;
        this.Quantity = Quantity;
        this.Available = Available;
        this.CategoryID = CategoryID;
        this.PublisherID = PublisherID;
        this.Description = Description;
        this.ShelfLocation = ShelfLocation;
        this.ImageUrl = ImageUrl;
    }

    public Book(String BookName, int Quantity, int Available, int CategoryID, int PublisherID) {
        this(BookName, Quantity, Available, CategoryID, PublisherID, null, null, null);
    }

    public Book(String BookName, int Quantity, int Available, int CategoryID, int PublisherID, String ImageUrl) {
        this(BookName, Quantity, Available, CategoryID, PublisherID, null, null, ImageUrl);
    }

    public Book(String BookName, int Quantity, int Available, int CategoryID, int PublisherID,
            String Description, String ShelfLocation, String ImageUrl) {
        this.BookName = BookName;
        this.Quantity = Quantity;
        this.Available = Available;
        this.CategoryID = CategoryID;
        this.PublisherID = PublisherID;
        this.Description = Description;
        this.ShelfLocation = ShelfLocation;
        this.ImageUrl = ImageUrl;
    }

    public int getBookID() {
        return BookID;
    }

    public void setBookID(int BookID) {
        this.BookID = BookID;
    }

    public String getBookName() {
        return BookName;
    }

    public void setBookName(String BookName) {
        this.BookName = BookName;
    }

    public int getQuantity() {
        return Quantity;
    }

    public void setQuantity(int Quantity) {
        this.Quantity = Quantity;
    }

    public int getAvailable() {
        return Available;
    }

    public void setAvailable(int Available) {
        this.Available = Available;
    }

    public int getCategoryID() {
        return CategoryID;
    }

    public void setCategoryID(int CategoryID) {
        this.CategoryID = CategoryID;
    }

    public int getPublisherID() {
        return PublisherID;
    }

    public void setPublisherID(int PublisherID) {
        this.PublisherID = PublisherID;
    }

    public String getDescription() {
        return Description;
    }

    public void setDescription(String Description) {
        this.Description = Description;
    }

    public String getShelfLocation() {
        return ShelfLocation;
    }

    public void setShelfLocation(String ShelfLocation) {
        this.ShelfLocation = ShelfLocation;
    }

    public String getImageUrl() {
        return ImageUrl;
    }

    public void setImageUrl(String ImageUrl) {
        this.ImageUrl = ImageUrl;
    }

    @Override
    public String toString() {
        return "Book{" + "BookID=" + BookID + ", BookName=" + BookName + ", Quantity=" + Quantity
                + ", Available=" + Available + ", CategoryID=" + CategoryID
                + ", PublisherID=" + PublisherID + ", Description=" + Description
                + ", ShelfLocation=" + ShelfLocation + ", ImageUrl=" + ImageUrl + '}';
    }
}
