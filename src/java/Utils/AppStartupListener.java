package Utils;

import jakarta.servlet.ServletContextEvent;
import jakarta.servlet.ServletContextListener;
import jakarta.servlet.annotation.WebListener;

@WebListener
public class AppStartupListener implements ServletContextListener {

    @Override
    public void contextInitialized(ServletContextEvent sce) {
        String realPath = sce.getServletContext().getRealPath("/");
        EnvLoader.setWebappPath(realPath);
        EnvLoader.load();
        System.out.println("[App] .env loaded. Google configured: " + GoogleOAuthConfig.isConfigured()
                + ", Email configured: " + EmailConfig.isConfigured());
    }

    @Override
    public void contextDestroyed(ServletContextEvent sce) {
    }
}
