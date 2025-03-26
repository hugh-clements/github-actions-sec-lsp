package org.server.diagnostic;

import org.eclipse.lsp4j.Diagnostic;
import org.server.document.DocumentModel;

import java.util.ArrayList;
import java.util.List;

public class CodeInjectionDiagnosticProvider implements DiagnosticProvider {

    @Override
    public List<Diagnostic> diagnose(DocumentModel document) {
        var diagnostics = new ArrayList<Diagnostic>();
        atJobSteps(document, diagnostics);
        return diagnostics;
    }

    private void atJobSteps(DocumentModel document, List<Diagnostic> diagnostics) {
        document.model().jobs().forEach(job -> {

            job.steps().forEach(step -> {


            });
        });
    }
}
