package Utils;

import Entities.GoogleAccount;
import com.google.gson.Gson;
import com.google.gson.JsonObject;
import java.io.IOException;
import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;
import org.apache.http.client.fluent.Form;
import org.apache.http.client.fluent.Request;

public final class GoogleOAuthService {

    private static final Gson GSON = new Gson();

    private GoogleOAuthService() {
    }

    public static String buildAuthorizationUrl() {
        if (!GoogleOAuthConfig.isConfigured()) {
            return "#";
        }

        StringBuilder builder = new StringBuilder(GoogleOAuthConfig.AUTHORIZATION_ENDPOINT);
        builder.append("?scope=").append(urlEncode(GoogleOAuthConfig.SCOPE));
        builder.append("&redirect_uri=").append(urlEncode(GoogleOAuthConfig.REDIRECT_URI));
        builder.append("&response_type=code");
        builder.append("&client_id=").append(urlEncode(GoogleOAuthConfig.CLIENT_ID));
        builder.append("&prompt=select_account");
        builder.append("&include_granted_scopes=true");
        return builder.toString();
    }

    public static String getToken(String code) throws IOException {
        String response = Request.Post(GoogleOAuthConfig.TOKEN_ENDPOINT)
                .bodyForm(
                        Form.form()
                                .add("client_id", GoogleOAuthConfig.CLIENT_ID)
                                .add("client_secret", GoogleOAuthConfig.CLIENT_SECRET)
                                .add("redirect_uri", GoogleOAuthConfig.REDIRECT_URI)
                                .add("code", code)
                                .add("grant_type", GoogleOAuthConfig.GRANT_TYPE)
                                .build()
                )
                .execute()
                .returnContent()
                .asString();

        JsonObject tokenPayload = GSON.fromJson(response, JsonObject.class);
        if (tokenPayload == null || !tokenPayload.has("access_token")) {
            throw new IOException("Google token response does not contain access_token.");
        }
        return tokenPayload.get("access_token").getAsString();
    }

    public static GoogleAccount getUserInfo(String accessToken) throws IOException {
        String response = Request.Get(GoogleOAuthConfig.USER_INFO_ENDPOINT)
                .addHeader("Authorization", "Bearer " + accessToken)
                .execute()
                .returnContent()
                .asString();
        GoogleAccount account = GSON.fromJson(response, GoogleAccount.class);
        if (account == null || account.getEmail() == null || account.getEmail().trim().isEmpty()) {
            throw new IOException("Google user info response does not contain email.");
        }
        return account;
    }

    private static String urlEncode(String value) {
        return URLEncoder.encode(value, StandardCharsets.UTF_8);
    }
}
