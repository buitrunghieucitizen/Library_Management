package Controller;

import Controller.borrow.BorrowAdminHandler;
import Controller.borrow.BorrowHelper;
import Controller.borrow.BorrowStudentHandler;
import Controller.borrow.BorrowTransactionService;
import Model.DAOBook;
import Model.DAOBookPrice;
import Model.DAOBorrow;
import Model.DAOBorrowItem;
import Model.DAOOrderDetail;
import Model.DAOOrders;
import Model.DAOStudent;
import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.sql.SQLException;

@WebServlet(name = "BorrowController", urlPatterns = {"/borrows", "/admin/borrows"})
public class BorrowController extends HttpServlet {

    private final DAOStudent daoStudent = new DAOStudent();
    private final DAOBook daoBook = new DAOBook();
    private final DAOBorrow daoBorrow = new DAOBorrow();
    private final DAOBorrowItem daoBorrowItem = new DAOBorrowItem();
    private final DAOOrders daoOrders = new DAOOrders();
    private final DAOOrderDetail daoOrderDetail = new DAOOrderDetail();
    private final DAOBookPrice daoBookPrice = new DAOBookPrice();

    private final BorrowHelper helper = new BorrowHelper(daoStudent);
    private final BorrowTransactionService transactionService = new BorrowTransactionService(
            daoBook, daoBorrow, daoBorrowItem, daoOrders, daoOrderDetail, daoBookPrice);
    private final BorrowStudentHandler studentHandler = new BorrowStudentHandler(
            daoBook, daoBorrow, daoOrders, daoOrderDetail, daoBookPrice, helper, transactionService);
    private final BorrowAdminHandler adminHandler = new BorrowAdminHandler(
            daoStudent, daoBook, daoBorrow, daoBorrowItem, helper, transactionService);

    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        if (helper.shouldRedirectToAdminRoute(req)) {
            resp.sendRedirect(req.getContextPath() + BorrowHelper.ADMIN_BORROWS_PATH + "?action=list");
            return;
        }

        String action = normalizeAction(req.getParameter("action"));

        try {
            switch (action) {
                case "create":
                    adminHandler.showCreate(req, resp);
                    break;
                case "checkout":
                    studentHandler.showCheckout(req, resp);
                    break;
                case "checkoutSuccess":
                    studentHandler.showCheckoutSuccess(req, resp);
                    break;
                case "list":
                default:
                    if (helper.isAdminSection(req)) {
                        adminHandler.showList(req, resp);
                    } else {
                        studentHandler.showList(req, resp);
                    }
                    break;
            }
        } catch (SQLException e) {
            throw new ServletException(e);
        }
    }

    @Override
    protected void doPost(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        req.setCharacterEncoding("UTF-8");
        if (helper.shouldRedirectToAdminRoute(req)) {
            resp.sendRedirect(req.getContextPath() + BorrowHelper.ADMIN_BORROWS_PATH + "?action=list");
            return;
        }

        String action = normalizeAction(req.getParameter("action"));

        try {
            switch (action) {
                case "create":
                    adminHandler.createBorrow(req, resp);
                    break;
                case "return":
                    adminHandler.returnBorrow(req, resp);
                    break;
                case "borrow":
                    studentHandler.borrowAsStudent(req, resp);
                    break;
                case "buy":
                    studentHandler.buyBookAsStudent(req, resp);
                    break;
                case "addBuyList":
                    studentHandler.addToBuyList(req, resp);
                    break;
                case "removeBuyItem":
                    studentHandler.removeFromBuyList(req, resp);
                    break;
                case "updateBuyQty":
                    studentHandler.updateBuyListQuantity(req, resp);
                    break;
                case "orderBuyItem":
                    studentHandler.orderOneFromBuyList(req, resp);
                    break;
                case "orderBuyAll":
                    studentHandler.orderAllFromBuyList(req, resp);
                    break;
                case "requestReturn":
                    studentHandler.requestReturnAsStudent(req, resp);
                    break;
                default:
                    resp.sendRedirect(req.getContextPath() + helper.getListPath(req) + "?action=list");
                    break;
            }
        } catch (SQLException e) {
            throw new ServletException(e);
        }
    }

    private String normalizeAction(String action) {
        if (action == null || action.trim().isEmpty()) {
            return "list";
        }
        return action.trim();
    }
}
