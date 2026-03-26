package Controller;

import Entities.Book;
import Entities.Borrow;
import Entities.BorrowItem;
import Entities.Fine;
import Entities.OrderDetail;
import Entities.Orders;
import Model.DAOBook;
import Model.DAOBorrow;
import Model.DAOBorrowItem;
import Model.DAOFine;
import Model.DAOOrderDetail;
import Model.DAOOrders;
import Model.DAOStaff;
import Model.DAOStudent;
import Utils.RoleUtils;
import ViewModel.BorrowRow;
import ViewModel.OrderRow;
import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.sql.SQLException;
import java.time.DayOfWeek;
import java.time.LocalDate;
import java.time.YearMonth;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.time.temporal.TemporalAdjusters;

@WebServlet(name = "AdminDashboardController", urlPatterns = {"/admin/dashboard"})
public class AdminDashboardController extends HttpServlet {

    private static final DateTimeFormatter DATE_FORMATTER = DateTimeFormatter.ofPattern("dd/MM/yyyy");
    private static final int LOW_STOCK_THRESHOLD = 3;
    private static final int RECENT_ITEMS_LIMIT = 5;
    private static final String STATUS_ORDER_WAITING = "H\u00e0ng ch\u1edd";
    private static final String STATUS_ORDER_READY = "S\u1eb5n s\u00e0ng";
    private static final String STATUS_ORDER_DELIVERED = "\u0110\u00e3 giao";
    private static final String STATUS_ORDER_CANCELLED = "\u0110\u00e3 h\u1ee7y";

    private final DAOBook daoBook = new DAOBook();
    private final DAOStudent daoStudent = new DAOStudent();
    private final DAOStaff daoStaff = new DAOStaff();
    private final DAOBorrow daoBorrow = new DAOBorrow();
    private final DAOBorrowItem daoBorrowItem = new DAOBorrowItem();
    private final DAOFine daoFine = new DAOFine();
    private final DAOOrders daoOrders = new DAOOrders();
    private final DAOOrderDetail daoOrderDetail = new DAOOrderDetail();

    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        boolean isAdminDashboard = RoleUtils.isAdmin(request);

        request.setAttribute("isAdminDashboard", isAdminDashboard);
        request.setAttribute("dashboardLabel", isAdminDashboard ? "BẢNG ĐIỀU KHIỂN" : "TRUNG TÂM VẬN HÀNH");
        request.setAttribute("dashboardTitle", isAdminDashboard ? "Bảng điều khiển quản trị" : "Bảng điều khiển nhân viên");
        request.setAttribute("dashboardIntro", isAdminDashboard
                ? "Tổng quan hệ thống thư viện để theo dõi kho sách, người dùng và giao dịch quan trọng."
                : "Tổng quan vận hành để theo dõi mượn trả, xử lý đơn và tình trạng kho sách.");
        request.setAttribute("dashboardRoleLabel", isAdminDashboard
                ? "Toàn quyền quản trị"
                : "Điều phối vận hành");
        request.setAttribute("managedAreas", isAdminDashboard ? 9 : 5);
        request.setAttribute("todayLabel", LocalDate.now().format(DATE_FORMATTER));

        try {
            populateStats(request, isAdminDashboard);
        } catch (SQLException e) {
            request.setAttribute("dashboardLoadError", "Không tải được dữ liệu bảng điều khiển: " + e.getMessage());
        }

        request.getRequestDispatcher("/WEB-INF/views/admin/dashboard.jsp").forward(request, response);
    }

    private void populateStats(HttpServletRequest request, boolean isAdminDashboard) throws SQLException {
        List<Book> books = daoBook.getAll();
        List<Borrow> borrows = daoBorrow.getAll();
        List<BorrowItem> borrowItems = daoBorrowItem.getAll();
        List<Orders> orders = daoOrders.getAll();
        List<OrderDetail> orderDetails = daoOrderDetail.getAll();
        List<Fine> paidFines = daoFine.getPaidFines();
        List<BorrowRow> borrowRows = daoBorrow.getBorrowRows();
        List<OrderRow> orderRows = daoOrders.getOrderRows();

        int totalBooks = books.size();
        int totalCopies = sumTotalCopies(books);
        int availableCopies = sumAvailableCopies(books);
        int borrowedCopies = Math.max(0, totalCopies - availableCopies);
        int totalStudents = daoStudent.getAll().size();
        int activeBorrows = countBorrowsByStatus(borrows, "Borrowing", "Overdue", "ReturnRequested");
        int overdueBorrows = countBorrowsByStatus(borrows, "Overdue");
        int pendingOrders = countOrdersByStatus(orders, "Pending", STATUS_ORDER_WAITING, STATUS_ORDER_READY);
        int readyOrders = countOrdersByStatus(orders, "Approved", STATUS_ORDER_READY);
        int completedOrders = countOrdersByStatus(orders, STATUS_ORDER_DELIVERED);
        int cancelledOrders = countOrdersByStatus(orders, "Rejected", STATUS_ORDER_CANCELLED);
        int lowStockCount = countLowStockBooks(books, LOW_STOCK_THRESHOLD);
        int outOfStockCount = countOutOfStockBooks(books);
        int stockCoverageValue = totalCopies == 0 ? 0 : calculatePercent(availableCopies, totalCopies);
        int borrowHealthValue = activeBorrows == 0 ? 100 : calculatePercent(Math.max(0, activeBorrows - overdueBorrows), activeBorrows);
        int orderFulfillmentValue = orders.isEmpty() ? 100 : calculatePercent(completedOrders, orders.size());

        request.setAttribute("totalBooks", totalBooks);
        request.setAttribute("totalCopies", totalCopies);
        request.setAttribute("availableCopies", availableCopies);
        request.setAttribute("borrowedCopies", borrowedCopies);
        request.setAttribute("totalStudents", totalStudents);
        request.setAttribute("activeBorrows", activeBorrows);
        request.setAttribute("overdueBorrows", overdueBorrows);
        request.setAttribute("pendingOrders", pendingOrders);
        request.setAttribute("readyOrders", readyOrders);
        request.setAttribute("completedOrders", completedOrders);
        request.setAttribute("cancelledOrders", cancelledOrders);
        request.setAttribute("lowStockCount", lowStockCount);
        request.setAttribute("outOfStockCount", outOfStockCount);
        request.setAttribute("priorityCount", overdueBorrows + pendingOrders + lowStockCount);
        request.setAttribute("totalTransactions", borrows.size() + orders.size());
        request.setAttribute("stockCoverageValue", stockCoverageValue);
        request.setAttribute("stockCoverageLabel", stockCoverageValue + "%");
        request.setAttribute("borrowHealthValue", borrowHealthValue);
        request.setAttribute("borrowHealthLabel", borrowHealthValue + "%");
        request.setAttribute("orderFulfillmentValue", orderFulfillmentValue);
        request.setAttribute("orderFulfillmentLabel", orderFulfillmentValue + "%");
        request.setAttribute("recentBorrowRows", slice(borrowRows, RECENT_ITEMS_LIMIT));
        request.setAttribute("recentOrderRows", slice(orderRows, RECENT_ITEMS_LIMIT));
        request.setAttribute("lowStockBooks", selectLowStockBooks(books, LOW_STOCK_THRESHOLD, RECENT_ITEMS_LIMIT));
        request.setAttribute("borrowBuyChartJson",
                buildBorrowBuyChartJson(borrows, borrowItems, orders, orderDetails));
        request.setAttribute("revenueChartJson", buildRevenueChartJson(orders, paidFines));

        if (isAdminDashboard) {
            int totalStaff = daoStaff.getAll().size();
            request.setAttribute("totalStaff", totalStaff);
            request.setAttribute("studentsPerStaffLabel",
                    totalStaff == 0 ? "--" : String.format(Locale.ROOT, "%.1f", (double) totalStudents / totalStaff));
        }
    }

    private int sumTotalCopies(List<Book> books) {
        int total = 0;
        for (Book book : books) {
            total += book.getQuantity();
        }
        return total;
    }

    private int sumAvailableCopies(List<Book> books) {
        int total = 0;
        for (Book book : books) {
            total += book.getAvailable();
        }
        return total;
    }

    private int countLowStockBooks(List<Book> books, int threshold) {
        int total = 0;
        for (Book book : books) {
            if (book.getAvailable() <= threshold) {
                total++;
            }
        }
        return total;
    }

    private int countOutOfStockBooks(List<Book> books) {
        int total = 0;
        for (Book book : books) {
            if (book.getAvailable() <= 0) {
                total++;
            }
        }
        return total;
    }

    private int calculatePercent(int part, int whole) {
        if (whole <= 0) {
            return 0;
        }
        return (int) Math.round(part * 100.0 / whole);
    }

    private int countBorrowsByStatus(List<Borrow> borrows, String... targetStatuses) {
        int total = 0;
        for (Borrow borrow : borrows) {
            if (matchesStatus(borrow.getStatus(), targetStatuses)) {
                total++;
            }
        }
        return total;
    }

    private int countOrdersByStatus(List<Orders> orders, String... targetStatuses) {
        int total = 0;
        for (Orders order : orders) {
            if (matchesStatus(order.getStatus(), targetStatuses)) {
                total++;
            }
        }
        return total;
    }

    private List<Book> selectLowStockBooks(List<Book> books, int threshold, int limit) {
        List<Book> selectedBooks = new ArrayList<>();
        for (Book book : books) {
            if (book.getAvailable() <= threshold) {
                selectedBooks.add(book);
            }
        }

        selectedBooks.sort(
                Comparator.comparingInt(Book::getAvailable)
                        .thenComparingInt(Book::getQuantity)
                        .thenComparingInt(Book::getBookID));

        return slice(selectedBooks, limit);
    }

    private <T> List<T> slice(List<T> source, int limit) {
        if (source == null || source.isEmpty() || limit <= 0) {
            return new ArrayList<>();
        }
        return new ArrayList<>(source.subList(0, Math.min(limit, source.size())));
    }

    private boolean matchesStatus(String value, String... targetStatuses) {
        if (value == null || targetStatuses == null) {
            return false;
        }

        String normalizedValue = value.trim().toLowerCase(Locale.ROOT);
        for (String targetStatus : targetStatuses) {
            if (targetStatus != null && normalizedValue.equals(targetStatus.trim().toLowerCase(Locale.ROOT))) {
                return true;
            }
        }
        return false;
    }

    private String buildBorrowBuyChartJson(List<Borrow> borrows, List<BorrowItem> borrowItems,
            List<Orders> orders, List<OrderDetail> orderDetails) {
        LocalDate today = LocalDate.now();
        YearMonth currentMonth = YearMonth.from(today);
        LocalDate weekStart = today.with(TemporalAdjusters.previousOrSame(DayOfWeek.MONDAY));
        LocalDate weekEnd = weekStart.plusDays(6);

        int[] yearlyBorrowData = new int[12];
        int[] yearlyOrderData = new int[12];
        int[] monthlyBorrowData = new int[currentMonth.lengthOfMonth()];
        int[] monthlyOrderData = new int[currentMonth.lengthOfMonth()];
        int[] weeklyBorrowData = new int[7];
        int[] weeklyOrderData = new int[7];

        Map<Integer, Integer> borrowQuantities = aggregateBorrowQuantities(borrowItems);
        Map<Integer, Integer> orderQuantities = aggregateOrderQuantities(orderDetails);

        for (Borrow borrow : borrows) {
            LocalDate borrowDate = parseDate(borrow.getBorrowDate());
            if (borrowDate == null) {
                continue;
            }

            int quantity = Math.max(0, borrowQuantities.getOrDefault(borrow.getBorrowID(), 0));
            collectRangeData(borrowDate, quantity, today, currentMonth, weekStart, weekEnd,
                    yearlyBorrowData, monthlyBorrowData, weeklyBorrowData);
        }

        for (Orders order : orders) {
            LocalDate orderDate = parseDate(order.getOrderDate());
            if (orderDate == null) {
                continue;
            }

            int quantity = Math.max(0, orderQuantities.getOrDefault(order.getOrderID(), 0));
            collectRangeData(orderDate, quantity, today, currentMonth, weekStart, weekEnd,
                    yearlyOrderData, monthlyOrderData, weeklyOrderData);
        }

        StringBuilder json = new StringBuilder(1024);
        json.append('{');
        json.append("\"defaultRange\":\"year\",");
        json.append("\"seriesNames\":{");
        json.append("\"borrow\":\"Mượn\",");
        json.append("\"order\":\"Mua\"");
        json.append("},");
        json.append("\"unitLabel\":\"cuốn\",");
        json.append("\"yAxisTitle\":\"Số lượng (cuốn)\",");
        json.append("\"ranges\":{");
        appendRangeJson(json, "year", "Năm nay", buildYearCategories(), yearlyBorrowData, yearlyOrderData);
        json.append(',');
        appendRangeJson(json, "month", "Tháng này", buildMonthCategories(currentMonth), monthlyBorrowData, monthlyOrderData);
        json.append(',');
        appendRangeJson(json, "week", "Tuần này", buildWeekCategories(), weeklyBorrowData, weeklyOrderData);
        json.append("}}");
        return json.toString();
    }

    private String buildRevenueChartJson(List<Orders> orders, List<Fine> paidFines) {
        LocalDate today = LocalDate.now();
        YearMonth currentMonth = YearMonth.from(today);
        LocalDate weekStart = today.with(TemporalAdjusters.previousOrSame(DayOfWeek.MONDAY));
        LocalDate weekEnd = weekStart.plusDays(6);

        RevenueSeries yearlySeries = new RevenueSeries(12);
        RevenueSeries monthlySeries = new RevenueSeries(currentMonth.lengthOfMonth());
        RevenueSeries weeklySeries = new RevenueSeries(7);

        for (Orders order : orders) {
            if (!isCollectedOrderStatus(order.getStatus())) {
                continue;
            }

            LocalDate orderDate = parseDate(order.getOrderDate());
            if (orderDate == null) {
                continue;
            }

            collectRevenueRangeData(orderDate, Math.max(0d, order.getTotalAmount()), true,
                    today, currentMonth, weekStart, weekEnd, yearlySeries, monthlySeries, weeklySeries);
        }

        for (Fine fine : paidFines) {
            LocalDate paidDate = parseDate(fine.getPaidDate());
            if (paidDate == null) {
                continue;
            }

            collectRevenueRangeData(paidDate, Math.max(0d, fine.getAmount()), false,
                    today, currentMonth, weekStart, weekEnd, yearlySeries, monthlySeries, weeklySeries);
        }

        StringBuilder json = new StringBuilder(2048);
        json.append('{');
        json.append("\"defaultRange\":\"year\",");
        json.append("\"seriesNames\":{");
        json.append("\"total\":\"Tổng doanh thu\",");
        json.append("\"order\":\"Mua sách\",");
        json.append("\"borrow\":\"Phí/phạt mượn\"");
        json.append("},");
        json.append("\"unitLabel\":\"VNĐ\",");
        json.append("\"yAxisTitle\":\"Doanh thu (VNĐ)\",");
        json.append("\"ranges\":{");
        appendRevenueRangeJson(json, "year", "Năm nay", buildYearCategories(), yearlySeries);
        json.append(',');
        appendRevenueRangeJson(json, "month", "Tháng này", buildMonthCategories(currentMonth), monthlySeries);
        json.append(',');
        appendRevenueRangeJson(json, "week", "Tuần này", buildWeekCategories(), weeklySeries);
        json.append("}}");
        return json.toString();
    }

    private Map<Integer, Integer> aggregateBorrowQuantities(List<BorrowItem> borrowItems) {
        Map<Integer, Integer> quantities = new HashMap<>();
        if (borrowItems == null) {
            return quantities;
        }

        for (BorrowItem borrowItem : borrowItems) {
            quantities.merge(borrowItem.getBorrowID(), Math.max(0, borrowItem.getQuantity()), Integer::sum);
        }
        return quantities;
    }

    private Map<Integer, Integer> aggregateOrderQuantities(List<OrderDetail> orderDetails) {
        Map<Integer, Integer> quantities = new HashMap<>();
        if (orderDetails == null) {
            return quantities;
        }

        for (OrderDetail orderDetail : orderDetails) {
            quantities.merge(orderDetail.getOrderID(), Math.max(0, orderDetail.getQuantity()), Integer::sum);
        }
        return quantities;
    }

    private void collectRangeData(LocalDate date, int quantity, LocalDate today, YearMonth currentMonth,
            LocalDate weekStart, LocalDate weekEnd, int[] yearlyData, int[] monthlyData, int[] weeklyData) {
        if (date.getYear() == today.getYear()) {
            yearlyData[date.getMonthValue() - 1] += quantity;
        }

        if (YearMonth.from(date).equals(currentMonth)) {
            monthlyData[date.getDayOfMonth() - 1] += quantity;
        }

        if (!date.isBefore(weekStart) && !date.isAfter(weekEnd)) {
            weeklyData[date.getDayOfWeek().getValue() - 1] += quantity;
        }
    }

    private void collectRevenueRangeData(LocalDate date, double amount, boolean isOrder,
            LocalDate today, YearMonth currentMonth, LocalDate weekStart, LocalDate weekEnd,
            RevenueSeries yearlySeries, RevenueSeries monthlySeries, RevenueSeries weeklySeries) {
        if (date.getYear() == today.getYear()) {
            yearlySeries.add(date.getMonthValue() - 1, amount, isOrder);
        }

        if (YearMonth.from(date).equals(currentMonth)) {
            monthlySeries.add(date.getDayOfMonth() - 1, amount, isOrder);
        }

        if (!date.isBefore(weekStart) && !date.isAfter(weekEnd)) {
            weeklySeries.add(date.getDayOfWeek().getValue() - 1, amount, isOrder);
        }
    }

    private LocalDate parseDate(String value) {
        if (value == null) {
            return null;
        }

        String trimmedValue = value.trim();
        if (trimmedValue.isEmpty()) {
            return null;
        }

        if (trimmedValue.length() >= 10) {
            trimmedValue = trimmedValue.substring(0, 10);
        }

        try {
            return LocalDate.parse(trimmedValue);
        } catch (Exception ex) {
            return null;
        }
    }

    private String[] buildYearCategories() {
        return new String[]{
            "Th.1", "Th.2", "Th.3", "Th.4", "Th.5", "Th.6",
            "Th.7", "Th.8", "Th.9", "Th.10", "Th.11", "Th.12"
        };
    }

    private String[] buildMonthCategories(YearMonth currentMonth) {
        String[] categories = new String[currentMonth.lengthOfMonth()];
        for (int day = 1; day <= currentMonth.lengthOfMonth(); day++) {
            categories[day - 1] = String.valueOf(day);
        }
        return categories;
    }

    private String[] buildWeekCategories() {
        return new String[]{"T2", "T3", "T4", "T5", "T6", "T7", "CN"};
    }

    private boolean isCollectedOrderStatus(String status) {
        return matchesStatus(status, "Approved", STATUS_ORDER_DELIVERED);
    }

    private void appendRangeJson(StringBuilder json, String key, String label, String[] categories,
            int[] borrowData, int[] orderData) {
        json.append('"').append(jsonEscape(key)).append("\":{");
        json.append("\"label\":\"").append(jsonEscape(label)).append("\",");
        json.append("\"categories\":");
        appendJsonStringArray(json, categories);
        json.append(',');
        json.append("\"borrowData\":");
        appendJsonIntArray(json, borrowData);
        json.append(',');
        json.append("\"orderData\":");
        appendJsonIntArray(json, orderData);
        json.append('}');
    }

    private void appendRevenueRangeJson(StringBuilder json, String key, String label, String[] categories,
            RevenueSeries series) {
        json.append('"').append(jsonEscape(key)).append("\":{");
        json.append("\"label\":\"").append(jsonEscape(label)).append("\",");
        json.append("\"categories\":");
        appendJsonStringArray(json, categories);
        json.append(',');
        json.append("\"summary\":{");
        json.append("\"totalRevenue\":").append(formatJsonNumber(series.getTotalRevenue())).append(',');
        json.append("\"orderRevenue\":").append(formatJsonNumber(series.getOrderRevenue())).append(',');
        json.append("\"borrowRevenue\":").append(formatJsonNumber(series.getBorrowRevenue())).append(',');
        json.append("\"collectedOrders\":").append(series.getCollectedOrderCount()).append(',');
        json.append("\"paidFines\":").append(series.getPaidFineCount());
        json.append("},");
        json.append("\"totalData\":");
        appendJsonDoubleArray(json, series.totalData);
        json.append(',');
        json.append("\"orderData\":");
        appendJsonDoubleArray(json, series.orderData);
        json.append(',');
        json.append("\"borrowData\":");
        appendJsonDoubleArray(json, series.borrowData);
        json.append(',');
        json.append("\"orderCountData\":");
        appendJsonIntArray(json, series.orderCountData);
        json.append(',');
        json.append("\"borrowCountData\":");
        appendJsonIntArray(json, series.borrowCountData);
        json.append('}');
    }

    private void appendJsonStringArray(StringBuilder json, String[] values) {
        json.append('[');
        for (int i = 0; i < values.length; i++) {
            if (i > 0) {
                json.append(',');
            }
            json.append('"').append(jsonEscape(values[i])).append('"');
        }
        json.append(']');
    }

    private void appendJsonIntArray(StringBuilder json, int[] values) {
        json.append('[');
        for (int i = 0; i < values.length; i++) {
            if (i > 0) {
                json.append(',');
            }
            json.append(values[i]);
        }
        json.append(']');
    }

    private void appendJsonDoubleArray(StringBuilder json, double[] values) {
        json.append('[');
        for (int i = 0; i < values.length; i++) {
            if (i > 0) {
                json.append(',');
            }
            json.append(formatJsonNumber(values[i]));
        }
        json.append(']');
    }

    private String formatJsonNumber(double value) {
        if (Double.isNaN(value) || Double.isInfinite(value)) {
            return "0";
        }

        long roundedValue = Math.round(value);
        if (Math.abs(value - roundedValue) < 0.000001d) {
            return Long.toString(roundedValue);
        }

        return Double.toString(value);
    }

    private String jsonEscape(String value) {
        if (value == null) {
            return "";
        }

        StringBuilder escaped = new StringBuilder(value.length() + 16);
        for (int i = 0; i < value.length(); i++) {
            char current = value.charAt(i);
            switch (current) {
                case '\\':
                    escaped.append("\\\\");
                    break;
                case '"':
                    escaped.append("\\\"");
                    break;
                case '\b':
                    escaped.append("\\b");
                    break;
                case '\f':
                    escaped.append("\\f");
                    break;
                case '\n':
                    escaped.append("\\n");
                    break;
                case '\r':
                    escaped.append("\\r");
                    break;
                case '\t':
                    escaped.append("\\t");
                    break;
                default:
                    if (current < 0x20) {
                        escaped.append(String.format(Locale.ROOT, "\\u%04x", (int) current));
                    } else {
                        escaped.append(current);
                    }
                    break;
            }
        }
        return escaped.toString();
    }

    private static final class RevenueSeries {

        private final double[] totalData;
        private final double[] orderData;
        private final double[] borrowData;
        private final int[] orderCountData;
        private final int[] borrowCountData;

        private RevenueSeries(int size) {
            this.totalData = new double[size];
            this.orderData = new double[size];
            this.borrowData = new double[size];
            this.orderCountData = new int[size];
            this.borrowCountData = new int[size];
        }

        private void add(int index, double amount, boolean isOrder) {
            if (index < 0 || index >= totalData.length) {
                return;
            }

            totalData[index] += amount;
            if (isOrder) {
                orderData[index] += amount;
                orderCountData[index]++;
                return;
            }

            borrowData[index] += amount;
            borrowCountData[index]++;
        }

        private double getTotalRevenue() {
            return sum(totalData);
        }

        private double getOrderRevenue() {
            return sum(orderData);
        }

        private double getBorrowRevenue() {
            return sum(borrowData);
        }

        private int getCollectedOrderCount() {
            return sum(orderCountData);
        }

        private int getPaidFineCount() {
            return sum(borrowCountData);
        }

        private double sum(double[] values) {
            double total = 0;
            for (double value : values) {
                total += value;
            }
            return total;
        }

        private int sum(int[] values) {
            int total = 0;
            for (int value : values) {
                total += value;
            }
            return total;
        }
    }
}
