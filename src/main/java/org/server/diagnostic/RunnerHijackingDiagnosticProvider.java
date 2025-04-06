package org.server.diagnostic;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.eclipse.lsp4j.Diagnostic;
import org.server.document.DocumentModel;

import java.util.ArrayList;
import java.util.List;

import static org.server.diagnostic.DiagnosticBuilderService.getDiagnostic;

public class RunnerHijackingDiagnosticProvider implements DiagnosticProvider{

    static Logger logger = LogManager.getLogger(RunnerHijackingDiagnosticProvider.class);

    @Override
    public List<Diagnostic> diagnose(DocumentModel document) {
        logger.info("Diagnosing Runner Hijacking");
        var diagnostics = new ArrayList<Diagnostic>();
        document.model().jobs().forEach(job -> job.runsOn().forEach(runnerLocated -> {
            //Checking if any self-hosted runners are being used
            if (runnerLocated.value() != DocumentModel.Runner.self_hosted) return;
            diagnostics.add(getDiagnostic(runnerLocated, DiagnosticBuilderService.DiagnosticType.RunnerHijacker));
        }));
        return diagnostics;
    }
}
