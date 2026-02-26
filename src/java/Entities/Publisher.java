package Entities;

public class Publisher {
    private int PublisherID;
    private String PublisherName;

    public Publisher() {
    }

    public Publisher(int PublisherID, String PublisherName) {
        this.PublisherID = PublisherID;
        this.PublisherName = PublisherName;
    }

    public Publisher(String PublisherName) {
        this.PublisherName = PublisherName;
    }

    public int getPublisherID() {
        return PublisherID;
    }

    public void setPublisherID(int PublisherID) {
        this.PublisherID = PublisherID;
    }

    public String getPublisherName() {
        return PublisherName;
    }

    public void setPublisherName(String PublisherName) {
        this.PublisherName = PublisherName;
    }

    @Override
    public String toString() {
        return "Publisher{" + "PublisherID=" + PublisherID + ", PublisherName=" + PublisherName + '}';
    }
}
