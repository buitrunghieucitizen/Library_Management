package ViewModel;

public class BookFieldSupport {
    private final boolean descriptionSupported;
    private final boolean shelfLocationSupported;
    private final boolean imageUrlSupported;

    public BookFieldSupport(boolean descriptionSupported, boolean shelfLocationSupported,
            boolean imageUrlSupported) {
        this.descriptionSupported = descriptionSupported;
        this.shelfLocationSupported = shelfLocationSupported;
        this.imageUrlSupported = imageUrlSupported;
    }

    public boolean isDescriptionSupported() {
        return descriptionSupported;
    }

    public boolean isShelfLocationSupported() {
        return shelfLocationSupported;
    }

    public boolean isImageUrlSupported() {
        return imageUrlSupported;
    }
}
