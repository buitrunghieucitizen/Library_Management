package Filter;

import jakarta.servlet.*;
import jakarta.servlet.annotation.WebFilter;
import jakarta.servlet.http.*;
import java.io.IOException;
import java.util.List;

@WebFilter(urlPatterns = {"/*"})
public class AuthFilter implements Filter {

    private static final List<String> PUBLIC_PATHS = List.of(
            "/LoginURL",
            "/register",
            "/login.jsp",
            "/register.jsp"
    );

    // Tất cả path của staff/admin (cả cũ lẫn mới)
    private static final List<String> STAFF_PATHS = List.of(
            "/admin",
            "/students",
            "/authors",
            "/categories",
            "/publishers",
            "/borrows",
            "/index.jsp"
    );

    @Override
    public void init(FilterConfig filterConfig) throws ServletException {}

    @Override
    public void doFilter(ServletRequest request, ServletResponse response, FilterChain chain)
            throws IOException, ServletException {

        HttpServletRequest req  = (HttpServletRequest) request;
        HttpServletResponse res = (HttpServletResponse) response;

        String uri  = req.getRequestURI();
        String ctx  = req.getContextPath();
        String path = uri.substring(ctx.length());

        // 1. Bỏ qua tài nguyên tĩnh
        if (path.endsWith(".css") || path.endsWith(".js")
                || path.endsWith(".png") || path.endsWith(".jpg")
                || path.endsWith(".ico") || path.endsWith(".woff2")) {
            chain.doFilter(request, response);
            return;
        }

        // 2. Bỏ qua public paths
        for (String pub : PUBLIC_PATHS) {
            if (path.equals(pub) || path.startsWith(pub + "/")) {
                chain.doFilter(request, response);
                return;
            }
        }

        // 3. Kiểm tra session
        HttpSession session  = req.getSession(false);
        boolean loggedIn     = session != null && session.getAttribute("staff") != null;

        if (!loggedIn) {
            res.sendRedirect(ctx + "/LoginURL");
            return;
        }

        @SuppressWarnings("unchecked")
        List<Integer> roles = (List<Integer>) session.getAttribute("roles");

        boolean isStaff   = roles != null &&
                roles.stream().anyMatch(r -> r == 1 || r == 2 || r == 3 || r == 4 || r == 7);
        boolean isStudent = roles != null && roles.contains(8);

        // 4. Chặn student vào trang staff
        boolean accessingStaffPage = STAFF_PATHS.stream()
                .anyMatch(sp -> path.equals(sp) || path.startsWith(sp + "/"));

        if (accessingStaffPage && !isStaff) {
            res.sendRedirect(ctx + "/home");
            return;
        }

        // 5. Chặn staff vào trang student
        if (path.equals("/home") || path.startsWith("/home/")) {
            if (!isStudent) {
                res.sendRedirect(ctx + "/index.jsp");
                return;
            }
        }

        chain.doFilter(request, response);
    }

    @Override
    public void destroy() {}
}