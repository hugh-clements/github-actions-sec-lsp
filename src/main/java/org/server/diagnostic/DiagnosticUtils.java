package org.server.diagnostic;

import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonPrimitive;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.time.Duration;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.Locale;

public class DiagnosticUtils {

    static Logger logger = LogManager.getLogger(DiagnosticUtils.class);

    /** Constant regex that matches the SHA commit hash */
    public static final String commitHashRegex = "/\b([a-f0-9]{40})\b/";


    /**
     * Helper method that retrieves the commit data from GitHub from the SAH hash
     * @param owner repository owner
     * @param repo repository name
     * @param commitHash commit hash to get
     * @return Json object result fromm GitHub API
     */
    public static JsonObject getCommitFromHash(String owner, String repo, String commitHash) {
        //TODO add handling of no internet connection
        var client = HttpClient.newHttpClient();
        var request = HttpRequest.newBuilder()
                .header("User-Agent", "Security-LSP")
                .uri(URI.create(" https://api.github.com/repos/" + owner + "/" + repo + "/commits/" + commitHash)).GET().build();
        try {
            JsonElement element = new JsonPrimitive(client.send(request, HttpResponse.BodyHandlers.ofString()).body());
            return element.getAsJsonObject();
        } catch (Exception e) {
            logger.error(e);
            return null;
        }
    }


    /**
     * Helper method to get all repository commits
     * @param owner repository owner
     * @param repo repository name
     * @return Json Object result fromm GitHub API
     */
    public static JsonObject getRepoCommits(String owner, String repo) {
        //TODO add handling of no internet connection
        var client = HttpClient.newHttpClient();
        var request = HttpRequest.newBuilder()
                .header("User-Agent", "Security-LSP")
                .uri(URI.create(" https://api.github.com/repos/" + owner + "/" + repo + "/commits")).GET().build();
        try {
            JsonElement element = new JsonPrimitive(client.send(request, HttpResponse.BodyHandlers.ofString()).body());
            return element.getAsJsonObject();
        } catch (Exception e) {
            logger.error(e);
            return null;
        }
    }


    /**
     * Helper method that returns if the current date is older than 3 months compared to the latest date
     * @param current date
     * @param latest date
     * @return true if the difference is 3 months or more
     */
    public static Boolean olderName3Months(String current, String latest) {
        DateTimeFormatter formatter =
                DateTimeFormatter.ofPattern("yyyy-MM-dd'T'HH:mm:ss.SSS'Z'", Locale.ENGLISH);
        LocalDateTime currentDate = LocalDateTime.parse(current, formatter);
        LocalDateTime latestDate = LocalDateTime.parse(latest, formatter);
        Duration between = Duration.between(currentDate, latestDate);
        return between.compareTo(Duration.ofDays(30)) >= 0;
    }



}
