package org.server.diagnostic;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.eclipse.lsp4j.Diagnostic;
import org.server.document.DocumentModel;
import org.server.document.Located;

import java.util.ArrayList;
import java.util.List;

import static org.server.diagnostic.DiagnosticUtils.atJobsSteps;
import static org.server.diagnostic.DiagnosticUtils.isNull;


public class PWNRequestDiagnosticProvider implements DiagnosticProvider {

    static Logger logger = LogManager.getLogger(PWNRequestDiagnosticProvider.class);

    private static final String REF_PULL_REQUEST_MERGE_REGEX = "/refs/pull/[a-zA-Z0-9._-]+/merge$";

    @Override
    public List<Diagnostic> diagnose(DocumentModel document) {
        logger.info("Diagnosing PWN request");
        var diagnostics = new ArrayList<Diagnostic>();
        if (document.model().on().events()
                .stream().anyMatch(n -> n.eventName().equals("pull_request_target"))) {
            atJobsSteps(this::checkUsesWith,document, diagnostics, DiagnosticBuilderService.DiagnosticType.PWN_REQUEST);
        }
        return diagnostics;
    }


    private Located<String> checkUsesWith(Located<String> uses, DocumentModel.With with) {
       if (isNull(uses,with)) return null;
       if (uses.value().contains("actions/checkout")) {
            var ref = with.mappings().entrySet().stream().filter(stringLocatedEntry -> stringLocatedEntry.getKey().equals("ref")).toList();
            if (ref.isEmpty()) {
                return null;
            }
            var refValue = ref.getFirst().getValue();
            if (refValue.value() == null) return null;
            //Check if is a pull request merge reference
            if (refValue.value().matches(REF_PULL_REQUEST_MERGE_REGEX) ||
            refValue.value().contains("github.event.pull_request.head")) {
                return refValue;
            }
        }
        return null;
    }
}
