package Utils;

public final class GoogleOAuthConfig {

    private static final String DEFAULT_CLIENT_ID
            = "";

    private static final String DEFAULT_CLIENT_SECRET
            = "";

    private static final String DEFAULT_REDIRECT_URI
            = "http://localhost:8080/LibraryManager/LoginURL";

    public static final String CLIENT_ID = readSetting(
            "GOOGLE_CLIENT_ID",
            DEFAULT_CLIENT_ID
    );

    public static final String CLIENT_SECRET = readSetting(
            "GOOGLE_CLIENT_SECRET",
            DEFAULT_CLIENT_SECRET
    );

    public static final String REDIRECT_URI = readSetting(
            "GOOGLE_REDIRECT_URI",
            DEFAULT_REDIRECT_URI
    );

    public static final String GRANT_TYPE = "authorization_code";
    public static final String TOKEN_ENDPOINT = "https://oauth2.googleapis.com/token";
    public static final String USER_INFO_ENDPOINT = "https://www.googleapis.com/oauth2/v2/userinfo";
    public static final String AUTHORIZATION_ENDPOINT = "https://accounts.google.com/o/oauth2/v2/auth";
    public static final String SCOPE = "email profile openid";

    private GoogleOAuthConfig() {
    }

    public static boolean isConfigured() {
        return !isBlank(CLIENT_ID) && !isBlank(CLIENT_SECRET) && !isBlank(REDIRECT_URI);
    }

    private static String readSetting(String key, String fallback) {
        String envValue = System.getenv(key);
        if (!isBlank(envValue)) {
            return envValue.trim();
        }

        String propertyValue = System.getProperty(key);
        if (!isBlank(propertyValue)) {
            return propertyValue.trim();
        }

        return fallback;
    }

    private static boolean isBlank(String value) {
        return value == null || value.trim().isEmpty();
    }
}
