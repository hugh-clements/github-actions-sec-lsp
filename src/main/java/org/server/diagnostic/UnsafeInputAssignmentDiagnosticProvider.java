package org.server.diagnostic;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.eclipse.lsp4j.Diagnostic;
import org.server.document.DocumentModel;

import java.util.ArrayList;
import java.util.List;

import static org.server.diagnostic.DiagnosticBuilderService.getDiagnostic;
import static org.server.diagnostic.DiagnosticUtils.getBetweenBraces;
import static org.server.diagnostic.DiagnosticUtils.getWithStrings;

public class UnsafeInputAssignmentDiagnosticProvider implements DiagnosticProvider{

    static Logger logger = LogManager.getLogger(UnsafeInputAssignmentDiagnosticProvider.class);

    @Override
    public List<Diagnostic> diagnose(DocumentModel document) {
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
           //TODO figure out what is an unsafe input and add diagnostic if that is the case
           diagnostics.add(getDiagnostic(withString, DiagnosticBuilderService.DiagnosticType.UnsafeInputAssignment));
       } );
    }
}
