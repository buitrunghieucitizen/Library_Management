package Utils;

public final class EmailConfig {

    private static final String DEFAULT_SMTP_HOST = "smtp.gmail.com";
    private static final String DEFAULT_SMTP_PORT = "587";
    private static final String DEFAULT_FROM_NAME = "Library Manager";

    private EmailConfig() {
    }

    public static String getSmtpHost() {
        return EnvLoader.get("MAIL_SMTP_HOST", DEFAULT_SMTP_HOST);
    }

    public static String getSmtpPort() {
        return EnvLoader.get("MAIL_SMTP_PORT", DEFAULT_SMTP_PORT);
    }

    public static boolean isStartTlsEnabled() {
        return Boolean.parseBoolean(EnvLoader.get("MAIL_SMTP_STARTTLS", "true"));
    }

    public static String getUsername() {
        return EnvLoader.get("MAIL_USERNAME", "");
    }

    public static String getPassword() {
        return EnvLoader.get("MAIL_PASSWORD", "");
    }

    public static String getFromName() {
        return EnvLoader.get("MAIL_FROM_NAME", DEFAULT_FROM_NAME);
    }

    public static String getFromEmail() {
        String from = EnvLoader.get("MAIL_FROM_EMAIL", "");
        return from.isBlank() ? getUsername() : from;
    }

    public static boolean isConfigured() {
        return !getUsername().isBlank() && !getPassword().isBlank();
    }
}
