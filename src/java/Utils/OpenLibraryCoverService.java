package Utils;

import com.google.gson.Gson;
import java.io.IOException;
import java.net.HttpURLConnection;
import java.net.URI;
import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;
import java.text.Normalizer;
import java.util.Collections;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Locale;
import java.util.Set;
import org.apache.http.client.fluent.Request;

public final class OpenLibraryCoverService {

    private static final Gson GSON = new Gson();
    private static final String SEARCH_ENDPOINT = "https://openlibrary.org/search.json";
    private static final String COVER_ENDPOINT = "https://covers.openlibrary.org/b/id/";
    private static final String OLID_COVER_ENDPOINT = "https://covers.openlibrary.org/b/olid/";
    private static final int SEARCH_LIMIT = 8;
    private static final String SEARCH_FIELDS = "title,author_name,cover_i,key,editions,editions.key";

    private OpenLibraryCoverService() {
    }

    public static String findCoverUrl(String title, String author) throws IOException {
        String normalizedTitle = normalize(title);
        String normalizedAuthor = normalize(author);
        if (normalizedTitle.isEmpty()) {
            return null;
        }

        SearchDoc bestMatch = findBestMatch(title, author, normalizedTitle, normalizedAuthor);
        if (bestMatch == null) {
            return null;
        }
        return toCoverUrl(bestMatch);
    }

    private static SearchDoc findBestMatch(String title, String author,
            String normalizedTitle, String normalizedAuthor) throws IOException {
        SearchDoc bestMatch = chooseBestMatch(search(title, author), normalizedTitle, normalizedAuthor);
        if (bestMatch != null || normalizedAuthor.isEmpty()) {
            return bestMatch;
        }
        return chooseBestMatch(search(title, null), normalizedTitle, normalizedAuthor);
    }

    private static List<SearchDoc> search(String title, String author) throws IOException {
        StringBuilder url = new StringBuilder(SEARCH_ENDPOINT)
                .append("?limit=").append(SEARCH_LIMIT)
                .append("&fields=").append(urlEncode(SEARCH_FIELDS))
                .append("&title=").append(urlEncode(title));
        if (!isBlank(author)) {
            url.append("&author=").append(urlEncode(author));
        }

        String response = Request.Get(url.toString())
                .connectTimeout(5000)
                .socketTimeout(5000)
                .userAgent("LibraryManager/1.0")
                .execute()
                .returnContent()
                .asString(StandardCharsets.UTF_8);

        SearchResponse payload = GSON.fromJson(response, SearchResponse.class);
        if (payload == null || payload.docs == null) {
            return Collections.emptyList();
        }
        return payload.docs;
    }

    private static SearchDoc chooseBestMatch(List<SearchDoc> docs,
            String normalizedTitle, String normalizedAuthor) {
        SearchDoc bestMatch = null;
        int bestScore = Integer.MIN_VALUE;

        for (SearchDoc doc : docs) {
            if (doc == null || !hasCoverCandidate(doc)) {
                continue;
            }

            int score = scoreTitle(normalizedTitle, normalize(doc.title));
            score += scoreAuthor(normalizedAuthor, doc.author_name);

            if (score > bestScore) {
                bestScore = score;
                bestMatch = doc;
            }
        }
        return bestMatch;
    }

    private static String toCoverUrl(SearchDoc doc) throws IOException {
        if (doc.cover_i != null && doc.cover_i > 0) {
            return COVER_ENDPOINT + doc.cover_i + "-L.jpg?default=false";
        }

        String editionOlid = extractEditionOlid(doc);
        if (editionOlid == null) {
            return null;
        }

        String olidCoverUrl = OLID_COVER_ENDPOINT + editionOlid + "-L.jpg?default=false";
        return coverExists(olidCoverUrl) ? olidCoverUrl : null;
    }

    private static boolean hasCoverCandidate(SearchDoc doc) {
        return (doc.cover_i != null && doc.cover_i > 0)
                || extractEditionOlid(doc) != null;
    }

    private static String extractEditionOlid(SearchDoc doc) {
        if (doc == null || doc.editions == null || doc.editions.docs == null) {
            return null;
        }
        for (EditionDoc edition : doc.editions.docs) {
            if (edition == null || isBlank(edition.key)) {
                continue;
            }
            String key = edition.key.trim();
            int slashIndex = key.lastIndexOf('/');
            return slashIndex >= 0 ? key.substring(slashIndex + 1) : key;
        }
        return null;
    }

    private static boolean coverExists(String url) throws IOException {
        HttpURLConnection connection = (HttpURLConnection) URI.create(url).toURL().openConnection();
        connection.setRequestMethod("HEAD");
        connection.setConnectTimeout(4000);
        connection.setReadTimeout(4000);
        connection.setInstanceFollowRedirects(true);
        try {
            int status = connection.getResponseCode();
            return status >= 200 && status < 300;
        } finally {
            connection.disconnect();
        }
    }

    private static int scoreTitle(String expectedTitle, String candidateTitle) {
        if (candidateTitle.isEmpty()) {
            return Integer.MIN_VALUE / 4;
        }
        if (candidateTitle.equals(expectedTitle)) {
            return 500;
        }
        if (candidateTitle.startsWith(expectedTitle) || expectedTitle.startsWith(candidateTitle)) {
            return 360;
        }
        if (candidateTitle.contains(expectedTitle) || expectedTitle.contains(candidateTitle)) {
            return 280;
        }

        Set<String> expectedTokens = tokenize(expectedTitle);
        Set<String> candidateTokens = tokenize(candidateTitle);
        int commonTokens = 0;
        for (String token : expectedTokens) {
            if (candidateTokens.contains(token)) {
                commonTokens++;
            }
        }
        return commonTokens * 40 - Math.abs(expectedTokens.size() - candidateTokens.size()) * 5;
    }

    private static int scoreAuthor(String expectedAuthor, List<String> candidateAuthors) {
        if (expectedAuthor.isEmpty() || candidateAuthors == null || candidateAuthors.isEmpty()) {
            return 0;
        }

        int bestScore = 0;
        for (String author : candidateAuthors) {
            String normalizedCandidate = normalize(author);
            if (normalizedCandidate.isEmpty()) {
                continue;
            }
            if (normalizedCandidate.equals(expectedAuthor)) {
                return 140;
            }
            if (normalizedCandidate.contains(expectedAuthor) || expectedAuthor.contains(normalizedCandidate)) {
                bestScore = Math.max(bestScore, 100);
                continue;
            }

            Set<String> expectedTokens = tokenize(expectedAuthor);
            Set<String> candidateTokens = tokenize(normalizedCandidate);
            int commonTokens = 0;
            for (String token : expectedTokens) {
                if (candidateTokens.contains(token)) {
                    commonTokens++;
                }
            }
            bestScore = Math.max(bestScore, commonTokens * 20);
        }
        return bestScore;
    }

    private static Set<String> tokenize(String value) {
        if (value.isEmpty()) {
            return Collections.emptySet();
        }
        Set<String> tokens = new LinkedHashSet<>();
        for (String token : value.split(" ")) {
            if (!token.isBlank()) {
                tokens.add(token);
            }
        }
        return tokens;
    }

    private static String normalize(String value) {
        if (isBlank(value)) {
            return "";
        }

        String normalized = Normalizer.normalize(value, Normalizer.Form.NFD)
                .replaceAll("\\p{M}+", "")
                .toLowerCase(Locale.ROOT)
                .replaceAll("[^a-z0-9]+", " ")
                .trim();
        return normalized.replaceAll("\\s+", " ");
    }

    private static String urlEncode(String value) {
        return URLEncoder.encode(value, StandardCharsets.UTF_8);
    }

    private static boolean isBlank(String value) {
        return value == null || value.trim().isEmpty();
    }

    private static final class SearchResponse {

        private List<SearchDoc> docs;
    }

    private static final class SearchDoc {

        private String title;
        private List<String> author_name;
        private Integer cover_i;
        private Editions editions;
    }

    private static final class Editions {

        private List<EditionDoc> docs;
    }

    private static final class EditionDoc {

        private String key;
    }
}
