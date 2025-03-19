package org.server.diagnostic;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.eclipse.lsp4j.Diagnostic;
import org.server.document.DocumentModel;

import java.util.ArrayList;
import java.util.List;

public class UnsafeInputAssignmentDiagnosticProvider implements DiagnosticProvider{

    static Logger logger = LogManager.getLogger(UnsafeInputAssignmentDiagnosticProvider.class);

    @Override
    public List<Diagnostic> diagnose(DocumentModel document) {
        var diagnostics = new ArrayList<Diagnostic>();
        document.model().jobs().forEach( job -> {

            job.steps().forEach( step -> {


            });
        });
        return diagnostics;
    }

    private void checkInputAssignment() {

    }
}
