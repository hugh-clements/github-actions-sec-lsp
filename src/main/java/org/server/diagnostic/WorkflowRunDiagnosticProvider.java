package org.server.diagnostic;

import org.eclipse.lsp4j.Diagnostic;
import org.server.document.DocumentModel;
import org.server.document.Located;

import java.util.ArrayList;
import java.util.List;

import static org.server.diagnostic.DiagnosticUtils.getWithStrings;

public class WorkflowRunDiagnosticProvider implements DiagnosticProvider {

    @Override
    public List<Diagnostic> diagnose(DocumentModel document) {
        var diagnostics = new ArrayList<Diagnostic>();
        document.model().on().workflowEvents().forEach(workflowEvent -> {
            var workflowRun = workflowEvent.workflowRun();
            if (workflowRun != null) return;
            checkUsesWith(document, diagnostics);
        } );


        return diagnostics;
    }

    private void checkUsesWith(DocumentModel document, List<Diagnostic> diagnostics) {
        document.model().jobs().forEach(job -> {

            job.steps().forEach(step -> {

            });
        });
    }

    private boolean checkUsesWith(Located<String> uses, DocumentModel.With with) {
        //uses.value().contains("actions/checkout") && getWithStrings(with)
                //TODO make this return true if it matches the snyk one
        //});

        return false;
    }
}
