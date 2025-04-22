package org.server.diagnostic;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.eclipse.lsp4j.Diagnostic;
import org.server.document.DocumentModel;
import org.server.document.Located;
import java.util.ArrayList;
import java.util.List;

import static org.server.diagnostic.DiagnosticBuilderService.getDiagnostic;

/**
 * Service that handles the diagnosis of issues in the model
 */
public class DiagnosticService {

    static Logger logger = LogManager.getLogger(DiagnosticService.class);
    private final List<DiagnosticProvider> diagnosticProviders;

    /**
     * Instantiating a diagnostic provider for each rule
     */
    public DiagnosticService() {
        diagnosticProviders = List.of(
                new RunnerHijackingDiagnosticProvider(),
                new UnsafeInputAssignmentDiagnosticProvider(),
                new ActionReferencingDiagnosticProvider(),
                new WorkflowRunDiagnosticProvider(),
                new PWNRequestDiagnosticProvider(),
                new CodeInjectionDiagnosticProvider(),
                new CommandExecutionDiagnosticProvider(),
                new OutdatedReferenceDiagnosticProvider()
        );
    }

    /**
     * Calls all diagnostic methods to get all errors, warnings and information
     * @param document document model to diagnose
     * @return list of all diagnostics
     */
    public List<Diagnostic> diagnose(Located<DocumentModel> document) {
        logger.info("Diagnosing all");
        var fileScopedDiagnostics = getFileScopedDiagnostics(document);
        if (!fileScopedDiagnostics.isEmpty()) {
            return fileScopedDiagnostics;
        }
        return diagnosticProviders.stream()
                .flatMap(instance -> instance.diagnose(document.value()).stream())
                .toList();
    }

    /**
     * Helper method to get global or file-scoped diagnostics
     * @param locatedDocument documentModel with locations
     * @return list of global diagnostics
     */
    public static List<Diagnostic> getFileScopedDiagnostics(Located<DocumentModel> locatedDocument) {
        var document = locatedDocument.value();
        var list = new ArrayList<Diagnostic>();
        //First checking if the Document is in YAML
        if (!document.lang().equals("yaml")) {
            list.add(getDiagnostic(locatedDocument,DiagnosticBuilderService.DiagnosticType.INCORRECT_LANG));
        }
        //Check if a document is valid YAML
        if (document.model() == null) {
            list.add(getDiagnostic(locatedDocument, DiagnosticBuilderService.DiagnosticType.NOT_VALID_YAML));
        }
        //Check if a document is in the correct directory
        if (!document.documentURI().contains("github/workflows/")) {
            list.add(getDiagnostic(locatedDocument,DiagnosticBuilderService.DiagnosticType.INCORRECT_DIRECTORY));
        }
        return list;
    }
}
