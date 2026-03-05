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
import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;
import java.util.Arrays;
import java.util.List;

@WebFilter(urlPatterns = {"/*"})
public class AuthFilter implements Filter {

    private static final List<String> PUBLIC_PATHS = Arrays.asList(
            "/LoginURL",
            "/login.jsp",
            "/register",
            "/register.jsp",
            "/index.html"
    );

    @Override
    public void init(FilterConfig filterConfig) throws ServletException {
    }

    @Override
    public void doFilter(ServletRequest request, ServletResponse response, FilterChain chain)
            throws IOException, ServletException {
        request.setCharacterEncoding("UTF-8");
        response.setCharacterEncoding("UTF-8");

        HttpServletRequest req = (HttpServletRequest) request;
        HttpServletResponse res = (HttpServletResponse) response;
        String contextPath = req.getContextPath();
        String path = req.getRequestURI().substring(contextPath.length());

        if (isStaticResource(path)) {
            chain.doFilter(request, response);
            return;
        }

        if (isPublicPath(path)) {
            chain.doFilter(request, response);
            return;
        }

        HttpSession session = req.getSession(false);
        boolean loggedIn = session != null && session.getAttribute("staff") != null;

        if (!loggedIn) {
            res.sendRedirect(contextPath + "/LoginURL");
            return;
        }

        boolean isAdmin = RoleUtils.isAdmin(req);
        boolean isStaff = RoleUtils.isStaff(req);
        boolean isStudentOnly = RoleUtils.isStudentOnly(req);

        if (isStudentPortalPath(path)) {
            if (isStudentOnly) {
                chain.doFilter(request, response);
            } else {
                res.sendRedirect(contextPath + "/index.jsp");
            }
            return;
        }

        if (path.startsWith("/admin")) {
            if (isAdmin || isStaff) {
                chain.doFilter(request, response);
            } else if (isStudentOnly) {
                res.sendRedirect(contextPath + "/home");
            } else {
                String errorMessage = URLEncoder.encode("Truy cập bị từ chối", StandardCharsets.UTF_8);
                res.sendRedirect(contextPath + "/index.jsp?error=" + errorMessage);
            }
            return;
        }

        if (isAdmin || isStaff) {
            chain.doFilter(request, response);
            return;
        }

        if (isStudentOnly) {
            if (path.equals("/books") || path.equals("/borrows") || path.equals("/index.jsp")
                    || path.equals("/") || path.equals("/logout")) {
                chain.doFilter(request, response);
            } else {
                res.sendRedirect(contextPath + "/home");
            }
            return;
        }

        chain.doFilter(request, response);
    }

    @Override
    public void destroy() {
    }

    private boolean isStaticResource(String path) {
        return path.endsWith(".css")
                || path.endsWith(".js")
                || path.endsWith(".png")
                || path.endsWith(".jpg")
                || path.endsWith(".jpeg")
                || path.endsWith(".gif")
                || path.endsWith(".ico")
                || path.endsWith(".svg")
                || path.endsWith(".woff")
                || path.endsWith(".woff2")
                || path.startsWith("/uploads/");
    }

    private boolean isPublicPath(String path) {
        for (String publicPath : PUBLIC_PATHS) {
            if (path.equals(publicPath) || path.startsWith(publicPath + "/")) {
                return true;
            }
        }
        return false;
    }

    private boolean isStudentPortalPath(String path) {
        return path.equals("/home") || path.startsWith("/home/");
    }
}
