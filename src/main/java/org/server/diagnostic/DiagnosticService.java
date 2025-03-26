package org.server.diagnostic;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.eclipse.lsp4j.Diagnostic;
import org.server.document.DocumentModel;
import org.server.document.Located;

import java.util.ArrayList;
import java.util.List;

import static org.server.diagnostic.DiagnosticBuilderService.getDiagnostic;

public class DiagnosticService {

    static Logger logger = LogManager.getLogger(DiagnosticService.class);
    private final List<DiagnosticProvider> diagnosticProviders;


    public DiagnosticService() {
        diagnosticProviders = List.of(
                new RunnerHijackingDiagnostic(),
                new ActionReferencingDiagnosticProvider(),
                new UnsafeInputAssignmentDiagnosticProvider(),
                new WorkflowRunDiagnosticProvider(),
                new PWNRequestDiagnosticProvider(),
                new CodeInjectionDiagnosticProvider(),
                new CommandExecutionDiagnosticProvider()
        );
    }

    /**
     * Calls all diagnostic methods to get all errors, warnings and information
     * @param document document model to diagnose
     * @return list of all diagnostics
     */
    public List<Diagnostic> diagnose(Located<DocumentModel> document) {
        logger.info("Diagnosing {}", document);
        var fileScopedDiagnostics = getFileScopedDiagnostics(document);
        if (!fileScopedDiagnostics.isEmpty()) {
            return fileScopedDiagnostics;
        }
        return new ArrayList<>(diagnosticProviders.stream()
                .flatMap(instance -> instance.diagnose(document.value()).stream())
                .toList());
    }

    /**
     * Helper method to global diagnostics
     * @param locatedDocument documentModel with locations
     * @return list of global diagnostics
     */
    public static List<Diagnostic> getFileScopedDiagnostics(Located<DocumentModel> locatedDocument) {
        var document = locatedDocument.value();
        var list = new ArrayList<Diagnostic>();
        //First checking if the Document is in yaml
        if (!document.lang().equals("yaml")) {
            list.add(getDiagnostic(locatedDocument,DiagnosticBuilderService.DiagnosticType.IncorrectLang));
        }
        //Check if document is valid yaml
        if (document.model() == null) {
            list.add(getDiagnostic(locatedDocument, DiagnosticBuilderService.DiagnosticType.NotValidYAML));
        }
        //Check if document is in correct directory
        if (document.documentURI().contains("github/workflows/")) {
            list.add(getDiagnostic(locatedDocument,DiagnosticBuilderService.DiagnosticType.IncorrectDirectory));
        }
        return list;
    }
}
