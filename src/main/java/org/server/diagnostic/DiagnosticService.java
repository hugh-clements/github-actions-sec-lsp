package org.server.diagnostic;

import lombok.NonNull;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.eclipse.lsp4j.Diagnostic;
import org.server.document.DocumentModel;

import java.util.ArrayList;
import java.util.List;

public class DiagnosticService {

    Logger logger = LogManager.getLogger(getClass());
    private DocumentModel documentModel;
    private List<Diagnostic> diagnostics;
    private DiagnosticBuilderService diagnosticBuilderService;

    public List<Diagnostic> diagnose(DocumentModel document) {
        this.documentModel = document;
        this.diagnostics = new ArrayList<>();
        diagnosticBuilderService = new DiagnosticBuilderService();
        try {
            addDiagnostic(getOverallDiagnostic());

        } catch (NullPointerException e) {
            logger.warn("Diagnostic is not present");
        }
        return diagnostics;
    }

    public void addDiagnostic(@NonNull Diagnostic diagnostic) {
        diagnostics.add(diagnostic);
    }

    public Diagnostic getOverallDiagnostic() {
        //First checking if the Document is in yaml
        if (!documentModel.lang().equals("yaml")) return null;
        var diagnostic = new Diagnostic();

        return null;
    }

    public Diagnostic getCommandExecutionDiagnostic() {
        return null;
    }

    public Diagnostic getPWNInjectionDiagnostic() {
        return null;
    }

    public Diagnostic getUnsafeInputAssignmentDiagnostic() {
        return null;
    }

    public Diagnostic getWorkflowRunDiagnostic() {
        return null;
    }

    public Diagnostic getRepojackableDiagnostic() {
        return null;
    }

    public Diagnostic getRunnerHighjackingDiagnostic() {
        return null;
    }

    public Diagnostic getUnpinnedActionDiagnostic() {
        return null;
    }

    public Diagnostic getPermissionControlDiagnostic() {
        return null;
    }

}
