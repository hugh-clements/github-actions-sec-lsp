package org.server.diagnostic;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.eclipse.lsp4j.Diagnostic;
import org.server.document.DocumentModel;

import java.util.LinkedList;
import java.util.List;

import static org.server.diagnostic.DiagnosticUtils.getBetweenBraces;

public class CommandExecutionDiagnosticProvider implements DiagnosticProvider {

    static Logger logger = LogManager.getLogger(CommandExecutionDiagnosticProvider.class);

    @Override
    public List<Diagnostic> diagnose(DocumentModel document) {
        logger.info("Diagnosing Command Execution");
        var diagnostics = new LinkedList<Diagnostic>();
        atSteps(document, diagnostics);
        return diagnostics;
    }

    private void atSteps(DocumentModel document, List<Diagnostic> diagnostics) {
        document.model().jobs().forEach(job -> job.steps().forEach(step -> {
            var run = step.run();
            if (run == null) return;
            var runBraces = getBetweenBraces(run.value());
            if (runBraces == null) return;
            //TODO check against unsafe inputs
            diagnostics.add(DiagnosticBuilderService.getDiagnostic(run, DiagnosticBuilderService.DiagnosticType.CommandExecution));
        }));

    }
}
