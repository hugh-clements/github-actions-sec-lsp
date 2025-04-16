package org.server.diagnostic;


import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.eclipse.lsp4j.Diagnostic;
import org.server.document.DocumentModel;
import java.util.ArrayList;
import java.util.List;

import static org.server.diagnostic.DiagnosticBuilderService.getDiagnostic;
import static org.server.diagnostic.DiagnosticUtils.*;

public class UnsafeInputAssignmentDiagnosticProvider implements DiagnosticProvider{

    static Logger logger = LogManager.getLogger(UnsafeInputAssignmentDiagnosticProvider.class);

    @Override
    public List<Diagnostic> diagnose(DocumentModel document) {
        logger.info("Diagnosing Unsafe Input Assignment");
        var diagnostics = new ArrayList<Diagnostic>();
        document.model().jobs().forEach( job -> {
            checkInputAssignment(diagnostics, job.with());
            job.steps().forEach( step -> checkInputAssignment(diagnostics, step.with()));
        });
        return diagnostics;
    }

    private void checkInputAssignment(List<Diagnostic> diagnostics, DocumentModel.With with) {
       getWithStrings(with).forEach( withString -> {
           var betweenBraces = getBetweenBraces(withString.value());
           if (betweenBraces == null) return;
           if (isUnsafeInput(betweenBraces))
               diagnostics.add(getDiagnostic(withString, DiagnosticBuilderService.DiagnosticType.UNSAFE_INPUT_ASSIGNMENT));
       });
    }

}