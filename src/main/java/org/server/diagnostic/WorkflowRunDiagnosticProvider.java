package org.server.diagnostic;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.eclipse.lsp4j.Diagnostic;
import org.server.document.DocumentModel;
import org.server.document.Located;

import java.util.ArrayList;
import java.util.List;

import static org.server.diagnostic.DiagnosticUtils.atJobsSteps;
import static org.server.diagnostic.DiagnosticUtils.getWithStrings;

public class WorkflowRunDiagnosticProvider implements DiagnosticProvider {

    static Logger logger = LogManager.getLogger(WorkflowRunDiagnosticProvider.class);

    @Override
    public List<Diagnostic> diagnose(DocumentModel document) {
        logger.info("Diagnosing Workflow Run");
        var diagnostics = new ArrayList<Diagnostic>();
        if (document.model().on().workflowEvents()
                .stream().anyMatch(n -> n.workflowRun() != null)) {
            atJobsSteps(this::checkUsesWith,document, diagnostics, DiagnosticBuilderService.DiagnosticType.WORKFLOW_RUN);
        }
        return diagnostics;
    }

    private Located<String> checkUsesWith(Located<String> uses, DocumentModel.With with) {
        if (uses.value().contains("actions/checkout")) {
            return getWithStrings(with).stream()
                    .filter(withString -> {
                        var ref = withString.value().split("@");
                        return ref.length > 1 && ref[1].contains("github.event.workflow_run");
                    })
                    .findFirst()
                    .orElse(null);
        }
        return null;
    }
}
