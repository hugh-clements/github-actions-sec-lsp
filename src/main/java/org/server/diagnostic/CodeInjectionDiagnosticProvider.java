package org.server.diagnostic;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.eclipse.lsp4j.Diagnostic;
import org.server.document.DocumentModel;
import org.server.document.Located;

import java.util.ArrayList;
import java.util.List;

import static org.server.diagnostic.DiagnosticUtils.atJobsSteps;

public class CodeInjectionDiagnosticProvider implements DiagnosticProvider {

    static Logger logger = LogManager.getLogger(CodeInjectionDiagnosticProvider.class);

    @Override
    public List<Diagnostic> diagnose(DocumentModel document) {
        logger.info("Diagnosing Code Injection");
        var diagnostics = new ArrayList<Diagnostic>();
        atJobsSteps(this::checkUsesWith,document, diagnostics, DiagnosticBuilderService.DiagnosticType.CODE_INJECTION);
        return diagnostics;
    }


    private Located<String> checkUsesWith(Located<String> uses, DocumentModel.With with) {
        //TODO deal with inputs here
        return null;
    }
}
