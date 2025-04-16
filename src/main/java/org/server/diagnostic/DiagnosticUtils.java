package org.server.diagnostic;

import com.google.gson.JsonArray;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.eclipse.lsp4j.Diagnostic;
import org.server.document.DocumentModel;
import org.server.document.Located;

import java.io.IOException;
import java.net.ConnectException;
import java.net.SocketTimeoutException;
import java.net.URI;
import java.net.UnknownHostException;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.time.Duration;
import java.time.LocalDateTime;
import java.time.OffsetDateTime;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.function.BiFunction;
import java.util.regex.Pattern;

import static org.server.diagnostic.DiagnosticBuilderService.getDiagnostic;

/**
 * Helper class for diagnostic providers to avoid repeating code
 */
public class DiagnosticUtils {

    private DiagnosticUtils() {
        throw new IllegalStateException("Utility class");
    }

    static Logger logger = LogManager.getLogger(DiagnosticUtils.class);

    /**
     * Constant regex that matches the SHA commit hash
     */
    protected static final String COMMIT_HASH_REGEX = "\\b[0-9a-f]{5,40}\\b";

    private static final String NETWORK_ERROR = "Network error: Unknown host";
    private static final String TIMEOUT_ERROR = "Timeout error: Timed out";
    private static final String UNKNOWN_ERROR = "Unknown error";

    protected static final String[] untrustedInputs = {
            "github\\.event\\.issue\\.title",
            "github\\.event\\.issue\\.body",
            "github\\.event\\.pull_request\\.title",
            "github\\.event\\.pull_request\\.body",
            "github\\.event\\.comment\\.body",
            "github\\.event\\.review\\.body",
            "github\\.event\\.pages\\.[\\w.-]*\\.page_name",
            "github\\.event\\.commits\\.[\\w.-]*\\.message",
            "github\\.event\\.head_commit\\.message",
            "github\\.event\\.head_commit\\.author\\.email",
            "github\\.event\\.head_commit\\.author\\.name",
            "github\\.event\\.commits\\.[\\w.-]*\\.author\\.email",
            "github\\.event\\.commits\\.[\\w.-]*\\.author\\.name",
            "github\\.event\\.pull_request\\.head\\.ref",
            "github\\.event\\.pull_request\\.head\\.label",
            "github\\.event\\.pull_request\\.head\\.repo\\.default_branch",
            "github\\.event\\.workflow_run\\.head_branch",
            "github\\.event\\.workflow_run\\.head_commit\\.message",
            "github\\.event\\.workflow_run\\.head_commit\\.author\\.email",
            "github\\.event\\.workflow_run\\.head_commit\\.author\\.name",
            "github\\.head_ref",
            "inputs\\.[\\w.-]*" //TODO maybe remove as not treated the same
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
        var client = HttpClient.newHttpClient();
        var uri = URI.create("https://api.github.com/repos/" + owner + "/" + repo + "/commits/" + commitHash);
        var request = HttpRequest.newBuilder()
                .header("User-Agent", "Security-LSP")
                .uri(uri).GET().build();
        try {
            var response = client.send(request, HttpResponse.BodyHandlers.ofString());
            if (response.statusCode() != 200) {
                return new JsonObject();
            }
            var jsonElement = JsonParser.parseString(response.body());
            return jsonElement.getAsJsonObject();
        } catch (UnknownHostException | ConnectException e) {
            logger.error(NETWORK_ERROR, e);
            return null;
        } catch (SocketTimeoutException e) {
            logger.error(TIMEOUT_ERROR, e);
            return null;
        } catch (Exception e) {
            logger.error(UNKNOWN_ERROR, e);
            return null;
        } finally {
            client.close();
        }
    }


    /**
     * Helper method to get all repository commits
     *
     * @param owner repository owner
     * @param repo  repository name
     * @return Json Object result fromm GitHub API
     */
    public static JsonArray getRepoCommits(String owner, String repo) {
        var client = HttpClient.newHttpClient();
        var uri = URI.create("https://api.github.com/repos/" + owner + "/" + repo + "/commits?sha=main");
        var request = HttpRequest.newBuilder()
                .header("User-Agent", "Security-LSP")
                .uri(uri).GET().build();
        try {
            var response = client.send(request, HttpResponse.BodyHandlers.ofString());
            if (response.statusCode() != 200) {
                return new JsonArray();
            }
            var jsonElement = JsonParser.parseString(response.body());
            return jsonElement.getAsJsonArray();
        } catch (UnknownHostException | ConnectException e) {
            logger.error(NETWORK_ERROR, e);
            return null;
        } catch (SocketTimeoutException e) {
            logger.error(TIMEOUT_ERROR, e);
            return null;
        } catch (Exception e) {
            logger.error(UNKNOWN_ERROR, e);
            return null;
        } finally {
            client.close();
        }
    }

    /**
     * Helper method to get the status code from a repository request
     * @param owner repo owner
     * @param repo name
     * @return status code
     */
    public static int getRepoStatus(String owner, String repo) {
        var client = HttpClient.newHttpClient();
        var uri = URI.create("https://api.github.com/repos/" + owner + "/" + repo);
        var request = HttpRequest.newBuilder()
                .header("User-Agent", "Security-LSP")
                .uri(uri).GET().build();
        try {
            var response = client.send(request, HttpResponse.BodyHandlers.ofString());
            return response.statusCode();
        } catch (UnknownHostException | ConnectException e) {
                logger.error(NETWORK_ERROR, e);
                return 0;
        }catch (SocketTimeoutException e) {
            logger.error("Network error: Timeout", e);
            return 0;
        } catch (InterruptedException | IOException e ) {
            logger.error("Unexpected error", e);
            return 0;
        }
    }


    /**
     * Helper method that returns if the current date is older than 3 months compared to the latest date
     * @param current date
     * @param latest  date
     * @return true if the difference is 3 months or more
     */
    public static Boolean olderThan3Months(String current, String latest) {
        LocalDateTime currentDate = OffsetDateTime.parse(current).toLocalDateTime();
        LocalDateTime latestDate = OffsetDateTime.parse(latest).toLocalDateTime();
        Duration between = Duration.between(currentDate, latestDate);
        return between.compareTo(Duration.ofDays(90)) >= 0;
    }

    /**
     * Helper method that returns all Strings present inside a With block
     * @param with With block from the DocumentModel
     * @return Strings
     */
    public static List<Located<String>> getWithStrings(DocumentModel.With with) {
        var stringList = new ArrayList<Located<String>>();
        stringList.addAll(with.values());
        stringList.addAll(with.mappings().values());
        stringList.add(with.args());
        stringList.add(with.entrypoint());
        return stringList;
    }

    /**
     * Helper method to extract a String from inside ${{ }}
     * @param input String to extract from
     * @return extracted String
     */
    public static String getBetweenBraces(String input) {
        Pattern pattern = Pattern.compile("\\$\\{\\{(.*?)}}");
        var matcher = pattern.matcher(input);
        if (!matcher.find()) return null;
        return matcher.group(1);
    }

    /**
     * Helper method to apply a function at each Job and Step
     * @param checkUsesWith method that takes in the Uses and With blocks
     * @param document documentModel containing Jobs and Steps
     * @param diagnosticType type of diagnostic to create
     * @param diagnostics list of diagnostics from Jobs and Steps
     */
    public static void atJobsSteps(
            BiFunction<Located<String>, DocumentModel.With, Located<String>> checkUsesWith,
            DocumentModel document, List<Diagnostic> diagnostics, DiagnosticBuilderService.DiagnosticType diagnosticType) {
        document.model().jobs().forEach(job -> {
            var jobWithString = checkUsesWith.apply(job.uses(), job.with());
            if (jobWithString != null) {
                diagnostics.add(getDiagnostic(jobWithString, diagnosticType));
            }
            job.steps().forEach(step -> {
                var stepWithString = checkUsesWith.apply(step.uses(), step.with());
                if (stepWithString == null) return;
                diagnostics.add(getDiagnostic(stepWithString, diagnosticType));
            });
        });
    }

    /**
     * Helper method to check if a string value is unsafe/untrusted
     * @param input string to check
     * @return true if the input string does match the untrusted inputs
     */
    public static Boolean isUnsafeInput(String input) {
        return Arrays.stream(untrustedInputs).anyMatch(untrustedInput -> Pattern.matches(untrustedInput, input));
    }
}

