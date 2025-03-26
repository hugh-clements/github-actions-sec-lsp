package org.server.diagnostic;

import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonPrimitive;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.server.document.DocumentModel;
import org.server.document.Located;

import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.time.Duration;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;
import java.util.regex.Pattern;

public class DiagnosticUtils {

    static Logger logger = LogManager.getLogger(DiagnosticUtils.class);

    /**
     * Constant regex that matches the SHA commit hash
     */
    public static final String commitHashRegex = "/\b([a-f0-9]{40})\b/";

    public static final String[] untrustedInputs = {
            "/github\\.event\\.issue\\.title/",
            "/github\\.event\\.issue\\.body/",
            "/github\\.event\\.pull_request\\.title/",
            "/github\\.event\\.pull_request\\.body/",
            "/github\\.event\\.comment\\.body/",
            "/github\\.event\\.review\\.body/",
            "/github\\.event\\.pages\\.[\\w.-]*\\.page_name/",
            "/github\\.event\\.commits\\.[\\w.-]*\\.message/",
            "/github\\.event\\.head_commit\\.message/",
            "/github\\.event\\.head_commit\\.author\\.email/",
            "/github\\.event\\.head_commit\\.author\\.name/",
            "/github\\.event\\.commits\\.[\\w.-]*\\.author\\.email/",
            "/github\\.event\\.commits\\.[\\w.-]*\\.author\\.name/",
            "/github\\.event\\.pull_request\\.head\\.ref/",
            "/github\\.event\\.pull_request\\.head\\.label/",
            "/github\\.event\\.pull_request\\.head\\.repo\\.default_branch/",
            "/github\\.event\\.workflow_run\\.head_branch/",
            "/github\\.event\\.workflow_run\\.head_commit\\.message/",
            "/github\\.event\\.workflow_run\\.head_commit\\.author\\.email/",
            "/github\\.event\\.workflow_run\\.head_commit\\.author\\.name/",
            "/github\\.head_ref/",
            "/inputs\\.[\\w.-]*/",
    };


    /**
     * Helper method that retrieves the commit data from GitHub from the SAH hash
     *
     * @param owner      repository owner
     * @param repo       repository name
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
     *
     * @param owner repository owner
     * @param repo  repository name
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
     *
     * @param current date
     * @param latest  date
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

    public static List<Located<String>> getWithStrings(DocumentModel.With with) {
        var stringList = new ArrayList<Located<String>>();
        stringList.addAll(with.values());
        stringList.addAll(with.mappings().values());
        stringList.add(with.args());
        stringList.add(with.entrypoint());
        return stringList;
    }

    public static String getBetweenBraces(String input) {
        Pattern pattern = Pattern.compile("\\$\\{\\{(.*?)}}");
        var matcher = pattern.matcher(input);
        if (!matcher.find()) return null;
        return matcher.group(1);
    }
}

