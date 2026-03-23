package ViewModel;

import java.util.List;

public class PageSlice<T> {
    private final List<T> items;
    private final int page;
    private final int totalPages;
    private final int totalItems;

    public PageSlice(List<T> items, int page, int totalPages, int totalItems) {
        this.items = items;
        this.page = page;
        this.totalPages = totalPages;
        this.totalItems = totalItems;
    }

    public List<T> getItems() {
        return items;
    }

    public int getPage() {
        return page;
    }

    public int getTotalPages() {
        return totalPages;
    }

    public int getTotalItems() {
        return totalItems;
    }
}
