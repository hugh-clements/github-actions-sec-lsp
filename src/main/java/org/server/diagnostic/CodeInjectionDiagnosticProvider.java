package org.server.diagnostic;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.eclipse.lsp4j.Diagnostic;
import org.server.document.DocumentModel;
import org.server.document.Located;
import java.util.ArrayList;
import java.util.List;

import static org.server.diagnostic.DiagnosticBuilderService.getDiagnostic;
import static org.server.diagnostic.DiagnosticBuilderService.getDiagnosticWithRelatedInfo;
import static org.server.diagnostic.DiagnosticUtils.*;

public class CodeInjectionDiagnosticProvider implements DiagnosticProvider {

    static Logger logger = LogManager.getLogger(CodeInjectionDiagnosticProvider.class);
    private DocumentModel documentModel;
    private String documentUri;

    @Override
    public List<Diagnostic> diagnose(DocumentModel document) {
        logger.info("Diagnosing Code Injection");
        this.documentModel = document;
        this.documentUri = documentModel.documentURI();
        var diagnostics = new ArrayList<Diagnostic>();
        atJobsStepsDiagnosticPassthrough(this::checkUsesWith,document, diagnostics);
        return diagnostics;
    }


    private void checkUsesWith(Located<String> uses, DocumentModel.With with, List<Diagnostic> diagnostics) {
        if (isNull(uses,with)) return;
        if (!uses.value().contains("actions/github-script")) return;
        getWithStrings(with).forEach( (_, value) -> {
            if (value == null) return;
            var betweenBraces = getBetweenBraces(value.value());
            if (betweenBraces.isEmpty()) return;
            var inputs = getIfInputStar(betweenBraces);
            if (!inputs.isEmpty()) {
                inputs.forEach(input -> {
                    var sliced = input.substring("inputs.".length());
                    var inputsAssigned = searchInputAssignment(sliced,documentModel);
                    if (inputsAssigned == null) {
                        diagnostics.add(getDiagnostic(value, DiagnosticBuilderService.DiagnosticType.CODE_INJECTION));
                        return;
                    }
                    diagnostics.add(getDiagnosticWithRelatedInfo(
                        value, DiagnosticBuilderService.DiagnosticType.CODE_INJECTION, inputsAssigned, "Unsafe input is set here.", documentUri));
                });
            }
            if (isUnsafeInput(betweenBraces)) {
                diagnostics.add(getDiagnostic(value, DiagnosticBuilderService.DiagnosticType.CODE_INJECTION));
            }
        });
    }
}
