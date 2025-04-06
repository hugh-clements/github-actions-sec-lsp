package org.server.diagnostic;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.eclipse.lsp4j.Diagnostic;
import org.server.document.DocumentModel;
import org.server.document.Located;

import java.util.ArrayList;
import java.util.List;

import static org.server.diagnostic.DiagnosticUtils.atJobsSteps;


public class PWNRequestDiagnosticProvider implements DiagnosticProvider {

    static Logger logger = LogManager.getLogger(DiagnosticService.class);

    private static final String refPullRequestMergeRegex = "/refs/pull/[a-zA-Z0-9._-]+/merge$";

    @Override
    public List<Diagnostic> diagnose(DocumentModel document) {
        logger.info("Diagnosing PWN request");
        var diagnostics = new ArrayList<Diagnostic>();
        if (document.model().on().events()
                .stream().anyMatch(n -> n.eventName().equals("pull_request_target"))) {
            atJobsSteps(this::checkUsesWith,document, diagnostics);
        }
        return diagnostics;
    }


    private Located<String> checkUsesWith(Located<String> uses, DocumentModel.With with) {
        if (uses.value().contains("actions/checkout")) {
            var ref = with.mappings().entrySet().stream().filter(stringLocatedEntry -> stringLocatedEntry.getKey().equals("ref")).toList();
            if (ref.isEmpty()) {
                return null;
            }
            var refValue = ref.getFirst().getValue();
            if (refValue.value() == null) return null;
            //Check if is a pull request merge reference
            if (refValue.value().matches(refPullRequestMergeRegex) &&
            refValue.value().contains("github.event.pull_request.head")) {
                return refValue;
            }
        }
        return null;
    }
}
