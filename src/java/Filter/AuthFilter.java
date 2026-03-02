package Filter;

import Utils.RoleUtils;
import jakarta.servlet.Filter;
import jakarta.servlet.FilterChain;
import jakarta.servlet.FilterConfig;
import jakarta.servlet.ServletException;
import jakarta.servlet.ServletRequest;
import jakarta.servlet.ServletResponse;
import jakarta.servlet.annotation.WebFilter;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.HttpSession;

import java.io.IOException;
import java.util.List;

@WebFilter(urlPatterns = { "/books", "/borrows", "/admin/*", "/index.jsp", "/", "/logout" })
public class AuthFilter implements Filter {

    @Override
    public void init(FilterConfig filterConfig) throws ServletException {
    }

    @Override
    public void doFilter(ServletRequest request, ServletResponse response, FilterChain chain)
            throws IOException, ServletException {

        HttpServletRequest req = (HttpServletRequest) request;
        HttpServletResponse res = (HttpServletResponse) response;
        HttpSession session = req.getSession(false);

        // Bỏ qua tài nguyên tĩnh (css, js, ảnh)
        String path = req.getRequestURI();
        if (path.endsWith(".css") || path.endsWith(".js") || path.endsWith(".png") || path.endsWith(".jpg")) {
            chain.doFilter(request, response);
            return;
        }

        // Kiểm tra xem đã có 'staff' trong session chưa
        boolean loggedIn = session != null && session.getAttribute("staff") != null;

        if (!loggedIn) {
            res.sendRedirect(req.getContextPath() + "/LoginURL");
            return;
        }

        // Kiểm tra phân quyền theo URL
        List<Integer> roles = (List<Integer>) session.getAttribute("roles");
        if (roles == null) roles = new java.util.ArrayList<>();

        boolean isAdmin = roles.contains(RoleUtils.ROLE_ADMIN);
        boolean isStaff = roles.contains(RoleUtils.ROLE_STAFF) || roles.contains(RoleUtils.ROLE_STAFF_ALT);
        boolean isStudent = roles.contains(RoleUtils.ROLE_STUDENT) || roles.contains(RoleUtils.ROLE_STUDENT_ALT);

        String servletPath = req.getServletPath();

        // Admin có quyền truy cập tất cả
        if (isAdmin) {
            chain.doFilter(request, response);
            return;
        }

        // Phân quyền cho Staff
        if (isStaff) {
            if (servletPath.equals("/books") || servletPath.equals("/borrows")
                    || servletPath.equals("/admin/books") || servletPath.equals("/admin/students")
                    || servletPath.equals("/admin/borrows") || servletPath.equals("/admin/orders")
                    || servletPath.equals("/admin/bookfiles") || servletPath.equals("/index.jsp")
                    || servletPath.equals("/") || servletPath.equals("/logout")) {
                chain.doFilter(request, response);
            } else {
                res.sendRedirect(req.getContextPath() + "/index.jsp?error=Access Denied");
            }
            return;
        }

        // Phân quyền cho Student
        if (isStudent) {
            if (servletPath.equals("/books") || servletPath.equals("/borrows") || servletPath.equals("/index.jsp")
                || servletPath.equals("/") || servletPath.equals("/logout")) {
                chain.doFilter(request, response);
            } else {
                res.sendRedirect(req.getContextPath() + "/index.jsp?error=Access Denied");
            }
            return;
        }

        // Mặc định cho phép nếu không khớp role nào (có thể là trang công khai hoặc lỗi cấu hình)
        chain.doFilter(request, response);
    }

    @Override
    public void destroy() {
    }
}
