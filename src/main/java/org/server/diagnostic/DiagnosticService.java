package org.server.diagnostic;

import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonPrimitive;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.eclipse.lsp4j.Diagnostic;
import org.server.document.DocumentModel;
import org.server.document.Located;

import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.util.ArrayList;
import java.util.List;
import java.util.function.Function;


public class DiagnosticService {

    static Logger logger = LogManager.getLogger(DiagnosticService.class);

    private final List<Function<DocumentModel,List<Diagnostic>>> diagnosticProviders;
    private static final DiagnosticBuilderService diagnosticBuilderService = new DiagnosticBuilderService();

    private static final String commitHashRegex = "/\b([a-f0-9]{40})\b/";

    public DiagnosticService() {
        diagnosticProviders = List.of(
                DiagnosticService::getCommandExecutionDiagnostic,
                DiagnosticService::getCodeInjectDiagnostic,
                DiagnosticService::getRepojackableDiagnostic,
                DiagnosticService::getPWNInjectionDiagnostic,
                DiagnosticService::getRunnerHijackingDiagnostic,
                DiagnosticService::getUnpinnedActionDiagnostic,
                DiagnosticService::getWorkflowRunDiagnostic,
                DiagnosticService::getUnsafeInputAssignmentDiagnostic
        );
    }

    /**
     * Calls all diagnostic methods to get all errors, warnings and information
     * @param document document model to diagnose
     * @return list of all diagnostics
     */
    public List<Diagnostic> diagnose(Located<DocumentModel> document) {
        logger.info("Diagnosing {}", document);
        var diagnosticList = new ArrayList<>(diagnosticProviders.stream().flatMap(method -> method.apply(document.value()).stream()).toList());
        diagnosticList.addAll(getGlobalDiagnostic(document));
        return diagnosticList;
    }

    /**
     * Helper method to global diagnostics
     * @param locatedDocument documentModel with locations
     * @return list of global diagnostics
     */
    public static List<Diagnostic> getGlobalDiagnostic(Located<DocumentModel> locatedDocument) {
        var document = locatedDocument.value();
        var list = new ArrayList<Diagnostic>();
        //First checking if the Document is in yaml
        if (!document.lang().equals("yaml")) {
            list.add(diagnosticBuilderService.getOverallDiagnostic(locatedDocument,DiagnosticBuilderService.DiagnosticType.IncorrectLang));
        }
        //Check if document is valid yaml
        if (document.model() == null) {
            list.add(diagnosticBuilderService.getOverallDiagnostic(locatedDocument, DiagnosticBuilderService.DiagnosticType.NotValidYAML));
        }
        //Check if document is in correct directory
        if (document.documentURI().contains("github/workflows/")) {
            list.add(diagnosticBuilderService.getOverallDiagnostic(locatedDocument,DiagnosticBuilderService.DiagnosticType.IncorrectDirectory));
        }
        return list;
    }

    public static List<Diagnostic> getCommandExecutionDiagnostic(DocumentModel document) {
        return null;
    }

    public static List<Diagnostic> getCodeInjectDiagnostic(DocumentModel document) {
        return null;
    }

    public static List<Diagnostic> getPWNInjectionDiagnostic(DocumentModel document) {
        return null;
    }

    public static List<Diagnostic> getUnsafeInputAssignmentDiagnostic(DocumentModel document) {
        return null;
    }

    public static List<Diagnostic> getWorkflowRunDiagnostic(DocumentModel document) {
        return null;
    }

    public static List<Diagnostic> getRepojackableDiagnostic(DocumentModel document) {
        var diagnostics = new ArrayList<Diagnostic>();
        return diagnostics;
    }

    public static List<Diagnostic> getRunnerHijackingDiagnostic(DocumentModel document) {
        var diagnostics = new ArrayList<Diagnostic>();
        document.model().jobs().forEach(job -> job.runsOn().forEach(runnerLocated -> {
            //Checking if any self-hosted runners are being used
            if (runnerLocated.value() != DocumentModel.Runner.self_hosted) return;
            diagnostics.add(diagnosticBuilderService.getSpecificDiagnostic(runnerLocated, DiagnosticBuilderService.DiagnosticType.RunnerHijacker));
        }));
        return diagnostics;
    }

    public static List<Diagnostic> getUnpinnedActionDiagnostic(DocumentModel document) {
        var diagnostics = new ArrayList<Diagnostic>();
        document.model().jobs().forEach(job -> {
            if (job.uses() == null) return;
            var jobUses = job.uses().value();
            var jobCommitHash = jobUses.split("@");
            if (!jobCommitHash[1].matches(commitHashRegex)) {
                diagnostics.add(diagnosticBuilderService.getSpecificDiagnostic(job.uses(), DiagnosticBuilderService.DiagnosticType.UnpinnedAction));
            }
            switch (jobUses) {
                case String a when a.contains("docker") -> {
                    return;
                }
                case String b when b.contains("./") -> {
                    return;
                }
                default -> {
                    var split = jobUses.split("[/@]");
                    var statusCode = validateGitHubCommitHash(split[0],split[1],split[split.length-1]);
                    if (statusCode == null || statusCode.get("status").getAsInt() != 200) {
                        diagnostics.add(diagnosticBuilderService.getSpecificDiagnostic(job.uses(), DiagnosticBuilderService.DiagnosticType.UnpinnedAction));
                    }
                }
            }
            job.steps().forEach(step -> {
                if (step.uses() == null) return;
                var stepUses = step.uses().value();
                var stepCommitHash = stepUses.split("@");
                if (!stepCommitHash[1].matches(commitHashRegex)) {
                    diagnostics.add(diagnosticBuilderService.getSpecificDiagnostic(step.uses(), DiagnosticBuilderService.DiagnosticType.UnpinnedAction));
                }
                switch (stepUses) {
                    case String a when a.contains("docker") -> {}
                    case String b when b.contains("./") -> {}
                    default -> {
                        var split = stepUses.split("[/@]");
                        var statusCode = validateGitHubCommitHash(split[0],split[1],split[split.length-1]);
                        if (statusCode == null || statusCode.get("status").getAsInt() != 200) {
                            diagnostics.add(diagnosticBuilderService.getSpecificDiagnostic(step.uses(), DiagnosticBuilderService.DiagnosticType.UnpinnedAction));
                        }
                    }
                }
            });
        });
        return diagnostics;
    }

    public static JsonObject validateGitHubCommitHash(String owner, String repo, String commitHash) {
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


}
