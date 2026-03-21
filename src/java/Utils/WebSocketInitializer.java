package Utils;

import jakarta.servlet.ServletContextEvent;
import jakarta.servlet.ServletContextListener;
import jakarta.servlet.annotation.WebListener;
import jakarta.websocket.server.ServerContainer;

@WebListener
public class WebSocketInitializer implements ServletContextListener {

    @Override
    public void contextInitialized(ServletContextEvent sce) {
        System.out.println("[WS] === INIT START ===");
        ServerContainer c = (ServerContainer) sce.getServletContext()
                .getAttribute("jakarta.websocket.server.ServerContainer");
        if (c == null) {
            c = (ServerContainer) sce.getServletContext()
                    .getAttribute("javax.websocket.server.ServerContainer");
        }
        if (c == null) {
            System.err.println("[WS] ServerContainer NULL!");
            return;
        }
        System.out.println("[WS] ServerContainer found: " + c.getClass().getName());

        try {
            System.out.println("[WS] Registering AdminEndpoint...");
            c.addEndpoint(NotificationBroadcaster.AdminEndpoint.class);
            System.out.println("[WS] AdminEndpoint OK");
        } catch (Exception e) {
            System.err.println("[WS] AdminEndpoint FAILED: " + e.getClass().getName() + ": " + e.getMessage());
            e.printStackTrace();
        }

        try {
            System.out.println("[WS] Registering StudentEndpoint...");
            c.addEndpoint(NotificationBroadcaster.StudentEndpoint.class);
            System.out.println("[WS] StudentEndpoint OK");
        } catch (Exception e) {
            System.err.println("[WS] StudentEndpoint FAILED: " + e.getClass().getName() + ": " + e.getMessage());
            e.printStackTrace();
        }

        System.out.println("[WS] === INIT DONE ===");
    }

    @Override
    public void contextDestroyed(ServletContextEvent sce) {
    }
}
