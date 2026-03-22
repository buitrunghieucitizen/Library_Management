package Utils;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

public class EnvLoader {

    private static final Map<String, String> envMap = new HashMap<>();
    private static boolean loaded = false;
    private static String webappPath = null;

    public static void setWebappPath(String path) {
        webappPath = path;
    }

    public static synchronized void load() {
        if (loaded) {
            return;
        }
        loaded = true;

        String catalinaBase = System.getProperty("catalina.base", "");
        String userDir = System.getProperty("user.dir", "");

        String[] paths = {
            catalinaBase + "/conf/.env",
            catalinaBase + "/.env",
            userDir + "/.env",};

        // Thêm webapp paths nếu có
        String[] extraPaths;
        if (webappPath != null) {
            extraPaths = new String[]{
                webappPath + "WEB-INF/.env",
                webappPath + "../../../.env",
                // build/web → lên 2 cấp = project root
                new File(webappPath).getParentFile().getParentFile().getAbsolutePath() + "/.env",};
        } else {
            extraPaths = new String[0];
        }

        File envFile = null;

        for (String path : paths) {
            File f = new File(path);
            System.out.println("[EnvLoader] Trying: " + f.getAbsolutePath() + " -> " + (f.exists() ? "FOUND" : "not found"));
            if (f.exists() && f.isFile()) {
                envFile = f;
                break;
            }
        }

        if (envFile == null) {
            for (String path : extraPaths) {
                File f = new File(path);
                System.out.println("[EnvLoader] Trying: " + f.getAbsolutePath() + " -> " + (f.exists() ? "FOUND" : "not found"));
                if (f.exists() && f.isFile()) {
                    envFile = f;
                    break;
                }
            }
        }

        if (envFile == null) {
            System.out.println("[EnvLoader] .env not found. Using system env/defaults.");
            return;
        }

        System.out.println("[EnvLoader] Loading: " + envFile.getAbsolutePath());

        try (BufferedReader reader = new BufferedReader(new FileReader(envFile))) {
            String line;
            while ((line = reader.readLine()) != null) {
                line = line.trim();
                if (line.isEmpty() || line.startsWith("#")) {
                    continue;
                }

                int eq = line.indexOf('=');
                if (eq <= 0) {
                    continue;
                }

                String key = line.substring(0, eq).trim();
                String value = line.substring(eq + 1).trim();

                if (value.length() >= 2
                        && ((value.startsWith("\"") && value.endsWith("\""))
                        || (value.startsWith("'") && value.endsWith("'")))) {
                    value = value.substring(1, value.length() - 1);
                }

                envMap.put(key, value);
                if (System.getProperty(key) == null) {
                    System.setProperty(key, value);
                }
            }
            System.out.println("[EnvLoader] Loaded " + envMap.size() + " keys.");
        } catch (IOException e) {
            System.err.println("[EnvLoader] Error: " + e.getMessage());
        }
    }

    public static String get(String key, String defaultValue) {
        String env = System.getenv(key);
        if (env != null && !env.isBlank()) {
            return env;
        }

        String prop = System.getProperty(key);
        if (prop != null && !prop.isBlank()) {
            return prop;
        }

        String val = envMap.get(key);
        if (val != null && !val.isBlank()) {
            return val;
        }

        return defaultValue;
    }

    public static String get(String key) {
        return get(key, "");
    }
}
