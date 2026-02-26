package Filter;

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

@WebFilter(urlPatterns = { "/books", "/students", "/authors", "/categories", "/publishers", "/index.jsp", "/" })
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

        if (loggedIn) {
            // Đã đăng nhập -> Cho phép đi tiếp
            chain.doFilter(request, response);
        } else {
            // Chưa đăng nhập -> Chuyển về trang đăng nhập
            res.sendRedirect(req.getContextPath() + "/LoginURL");
        }
    }

    @Override
    public void destroy() {
    }
}
