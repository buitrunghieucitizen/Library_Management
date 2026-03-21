package Utils;

import jakarta.websocket.Endpoint;
import jakarta.websocket.server.ServerApplicationConfig;
import jakarta.websocket.server.ServerEndpointConfig;
import java.util.HashSet;
import java.util.Set;

/**
 * Đăng ký WebSocket endpoints với Tomcat. Tomcat tự động scan class implement
 * ServerApplicationConfig.
 */
public class WebSocketConfig implements ServerApplicationConfig {

    @Override
    public Set<Class<?>> getAnnotatedEndpointClasses(Set<Class<?>> scanned) {
        // Trả về tất cả annotated endpoints tìm được
        Set<Class<?>> results = new HashSet<>();
        for (Class<?> clazz : scanned) {
            if (clazz.getPackageName().startsWith("Utils")) {
                results.add(clazz);
            }
        }
        return results;
    }

    @Override
    public Set<ServerEndpointConfig> getEndpointConfigs(Set<Class<? extends Endpoint>> endpointClasses) {
        return new HashSet<>();
    }
}
