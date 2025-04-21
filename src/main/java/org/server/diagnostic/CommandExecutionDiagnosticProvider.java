package org.server.diagnostic;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.eclipse.lsp4j.Diagnostic;
import org.server.document.DocumentModel;
import org.server.document.Located;

import java.util.LinkedList;
import java.util.List;

import static org.server.diagnostic.DiagnosticBuilderService.getDiagnostic;
import static org.server.diagnostic.DiagnosticBuilderService.getDiagnosticWithRelatedInfo;
import static org.server.diagnostic.DiagnosticUtils.*;

public class CommandExecutionDiagnosticProvider implements DiagnosticProvider {

    static Logger logger = LogManager.getLogger(CommandExecutionDiagnosticProvider.class);


    @Override
    public List<Diagnostic> diagnose(DocumentModel document) {
        logger.info("Diagnosing Command Execution");
        var diagnostics = new LinkedList<Diagnostic>();
        atSteps(document, diagnostics,document.documentURI());
        return diagnostics;
    }


    private void atSteps(DocumentModel document, List<Diagnostic> diagnostics, String documentUri) {
        document.model().jobs().forEach(job -> {
            if (job.steps() == null) return;
            job.steps().forEach(step -> {
                var run = step.run();
                if (run == null) return;
                getDiagnostics(run, document, diagnostics, documentUri);
            });
                });

    }


    private void getDiagnostics(Located<String> run, DocumentModel document, List<Diagnostic> diagnostics, String documentUri) {
        var runBraces = getBetweenBraces(run.value());
        if (runBraces.isEmpty()) return;
        var inputs = getIfInputStar(runBraces);
        if (!inputs.isEmpty()) {
            inputs.forEach(input -> {
                var sliced = input.substring("inputs.".length());
                var inputsAssigned = searchInputAssignment(sliced,document);
                if (inputsAssigned == null) {
                    diagnostics.add(getDiagnostic(run, DiagnosticBuilderService.DiagnosticType.COMMAND_EXECUTION));
                    return;
                }
                diagnostics.add(getDiagnosticWithRelatedInfo(
                        run, DiagnosticBuilderService.DiagnosticType.COMMAND_EXECUTION, inputsAssigned, String.format("Unsafe input: %s is set here.", input), documentUri));
            });
        }
        if (isUnsafeInput(runBraces)) {
            diagnostics.add(getDiagnostic(run, DiagnosticBuilderService.DiagnosticType.COMMAND_EXECUTION));
        }
    }
}
