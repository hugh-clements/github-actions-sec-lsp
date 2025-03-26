package org.server.diagnostic;

import org.eclipse.lsp4j.Diagnostic;
import org.server.document.DocumentModel;
import org.server.document.Located;

import java.util.ArrayList;
import java.util.List;

import static org.server.diagnostic.DiagnosticBuilderService.getDiagnostic;

public class CodeInjectionDiagnosticProvider implements DiagnosticProvider {

    @Override
    public List<Diagnostic> diagnose(DocumentModel document) {
        var diagnostics = new ArrayList<Diagnostic>();
        atJobSteps(document, diagnostics);
        return diagnostics;
    }

    private void atJobSteps(DocumentModel document, List<Diagnostic> diagnostics) {
        document.model().jobs().forEach(job -> {
            var jobWithString= checkUsesWith(job.uses(), job.with());
            if (jobWithString != null) {
                diagnostics.add(getDiagnostic(jobWithString, DiagnosticBuilderService.DiagnosticType.WorkflowRun));
            }
            job.steps().forEach(step -> {
                var stepWithString = checkUsesWith(step.uses(), step.with());
                if (stepWithString == null) return;
                diagnostics.add(getDiagnostic(stepWithString, DiagnosticBuilderService.DiagnosticType.WorkflowRun));
            });
        });
    }

    private Located<String> checkUsesWith(Located<String> uses, DocumentModel.With with) {
        return null;
    }
}
