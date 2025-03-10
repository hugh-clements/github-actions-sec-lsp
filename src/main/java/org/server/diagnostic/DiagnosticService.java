package org.server.diagnostic;

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

    Logger logger = LogManager.getLogger(getClass());

    private final List<Function<DocumentModel,List<Diagnostic>>> diagnosticProviders;
    private static final DiagnosticBuilderService diagnosticBuilderService = new DiagnosticBuilderService();

    public DiagnosticService() {
        diagnosticProviders = List.of(
                DiagnosticService::getCommandExecutionDiagnostic,
                DiagnosticService::getRepojackableDiagnostic,
                DiagnosticService::getPWNInjectionDiagnostic,
                DiagnosticService::getRunnerHijackingDiagnostic,
                DiagnosticService::getPermissionControlDiagnostic,
                DiagnosticService::getUnpinnedActionDiagnostic,
                DiagnosticService::getWorkflowRunDiagnostic,
                DiagnosticService::getUnsafeInputAssignmentDiagnostic
        );
    }

    public List<Diagnostic> diagnose(Located<DocumentModel> document) {
        logger.info("Diagnosing {}", document);
        var diagnosticList = new ArrayList<>(diagnosticProviders.stream().flatMap(method -> method.apply(document.value()).stream()).toList());
        diagnosticList.addAll(getGlobalDiagnostic(document));
        return diagnosticList;
    }

    public static List<Diagnostic> getGlobalDiagnostic(Located<DocumentModel> locatedDocument) {
        var document = locatedDocument.value();
        var list = new ArrayList<Diagnostic>();
        //First checking if the Document is in yaml
        if (!document.lang().equals("yaml")) {
            list.add(diagnosticBuilderService.getOverallDiagnostic(locatedDocument,DiagnosticBuilderService.DiagnosticType.IncorrectLang));
        }
        //Check if document is valid yaml
        if (document.model() == null) {

        }
        //Check if document is in correct directory
        return list;
    }

    public static List<Diagnostic> getCommandExecutionDiagnostic(DocumentModel document) {
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
        //Used for API call to GitHub to check repository
        var client = HttpClient.newHttpClient();
        var request = HttpRequest.newBuilder()
                .uri(URI.create("")).GET().build();
        var response = client.sendAsync(request, HttpResponse.BodyHandlers.ofString());
        //Get status code
        //Try and check when the repo was created renamed etc
        return null;
    }

    public static List<Diagnostic> getRunnerHijackingDiagnostic(DocumentModel document) {
        return null;
    }

    public static List<Diagnostic> getUnpinnedActionDiagnostic(DocumentModel document) {
        return null;
    }

    public static List<Diagnostic> getPermissionControlDiagnostic(DocumentModel document) {
        return null;
    }

}
