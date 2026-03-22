package Utils;

public final class GoogleOAuthConfig {

    public static final String CLIENT_ID = EnvLoader.get("GOOGLE_CLIENT_ID", "");
    public static final String CLIENT_SECRET = EnvLoader.get("GOOGLE_CLIENT_SECRET", "");
    public static final String REDIRECT_URI = EnvLoader.get("GOOGLE_REDIRECT_URI",
            "http://localhost:8080/libraryManager/LoginURL");

    public static final String GRANT_TYPE = "authorization_code";
    public static final String TOKEN_ENDPOINT = "https://oauth2.googleapis.com/token";
    public static final String USER_INFO_ENDPOINT = "https://www.googleapis.com/oauth2/v2/userinfo";
    public static final String AUTHORIZATION_ENDPOINT = "https://accounts.google.com/o/oauth2/v2/auth";
    public static final String SCOPE = "email profile openid";

    private GoogleOAuthConfig() {
    }

    public static boolean isConfigured() {
        return !isBlank(CLIENT_ID) && !isBlank(CLIENT_SECRET);
    }

    private static boolean isBlank(String value) {
        return value == null || value.trim().isEmpty();
    }
}
