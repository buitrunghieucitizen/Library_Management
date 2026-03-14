package Utils;

public final class EmailConfig {

    private static final String DEFAULT_SMTP_HOST = "smtp.gmail.com";
    private static final String DEFAULT_SMTP_PORT = "587";
    private static final String DEFAULT_FROM_NAME = "Library Manager";

    private EmailConfig() {
    }

    public static String getSmtpHost() {
        return read("MAIL_SMTP_HOST", DEFAULT_SMTP_HOST);
    }

    public static String getSmtpPort() {
        return read("MAIL_SMTP_PORT", DEFAULT_SMTP_PORT);
    }

    public static boolean isStartTlsEnabled() {
        return Boolean.parseBoolean(read("MAIL_SMTP_STARTTLS", "true"));
    }

    public static String getUsername() {
        return read("MAIL_USERNAME", "");
    }

    public static String getPassword() {
        return read("MAIL_PASSWORD", "");
    }

    public static String getFromName() {
        return read("MAIL_FROM_NAME", DEFAULT_FROM_NAME);
    }

    public static String getFromEmail() {
        String explicitFrom = read("MAIL_FROM_EMAIL", "");
        if (!explicitFrom.isBlank()) {
            return explicitFrom;
        }
        return getUsername();
    }

    public static boolean isConfigured() {
        return !getUsername().isBlank() && !getPassword().isBlank();
    }

    private static String read(String key, String defaultValue) {
        String value = System.getProperty(key);
        if (value == null || value.isBlank()) {
            value = System.getenv(key);
        }
        if (value == null || value.isBlank()) {
            return defaultValue;
        }
        return value.trim();
    }
}
