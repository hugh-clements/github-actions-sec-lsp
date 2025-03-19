package org.server.diagnostic;

import org.eclipse.lsp4j.Diagnostic;
import org.server.document.DocumentModel;

import java.util.ArrayList;
import java.util.List;

import static org.server.diagnostic.DiagnosticBuilderService.getDiagnostic;

public class RunnerHijackingDiagnostic implements DiagnosticProvider{
    @Override
    public List<Diagnostic> diagnose(DocumentModel document) {
        var diagnostics = new ArrayList<Diagnostic>();
        document.model().jobs().forEach(job -> job.runsOn().forEach(runnerLocated -> {
            //Checking if any self-hosted runners are being used
            if (runnerLocated.value() != DocumentModel.Runner.self_hosted) return;
            diagnostics.add(getDiagnostic(runnerLocated, DiagnosticBuilderService.DiagnosticType.RunnerHijacker));
        }));
        return diagnostics;
    }
}
