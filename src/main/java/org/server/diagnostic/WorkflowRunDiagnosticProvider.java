package org.server.diagnostic;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.eclipse.lsp4j.Diagnostic;
import org.server.document.DocumentModel;
import org.server.document.Located;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Objects;

import static org.server.diagnostic.DiagnosticUtils.*;

public class WorkflowRunDiagnosticProvider implements DiagnosticProvider {

    static Logger logger = LogManager.getLogger(WorkflowRunDiagnosticProvider.class);

    @Override
    public List<Diagnostic> diagnose(DocumentModel document) {
        logger.info("Diagnosing Workflow Run");
        var diagnostics = new ArrayList<Diagnostic>();
        var workflowEvents = document.model().on().workflowEvents();
        if (workflowEvents == null) return diagnostics;
        if (workflowEvents
                .stream().anyMatch(n -> n.workflowRun() != null)) {
            atJobsSteps(this::checkUsesWith,document, diagnostics, DiagnosticBuilderService.DiagnosticType.WORKFLOW_RUN);
        }
        return diagnostics;
    }

    private Located<String> checkUsesWith(Located<String> uses, DocumentModel.With with) {
        if (isNull(uses, with)) return null;
        if (uses.value().contains("actions/checkout")) {
            return getWithStrings(with).entrySet().stream()
                    .filter(entry -> "ref".equals(entry.getKey()))
                    .map(Map.Entry::getValue)
                    .filter(Objects::nonNull)
                    .filter(val -> val.value() != null && val.value().contains("github.event.workflow_run"))
                    .findFirst()
                    .orElse(null);
        }
        return null;
    }


}
