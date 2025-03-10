package org.server.diagnostic;

import org.eclipse.lsp4j.Diagnostic;
import org.server.document.DocumentModel;
import org.server.document.Located;

public class DiagnosticBuilderService {

    public Diagnostic getOverallDiagnostic(Located<DocumentModel> documentModelLocated, DiagnosticType diagnosticType) {
        var diagnostic = new Diagnostic();
        diagnostic.setRange(Located.locatedToRange(documentModelLocated));
        switch (diagnosticType) {
            case IncorrectLang -> {}
            case IncorrectDirectory -> {}
            case NotValidYAML -> {}
            default -> throw new IllegalArgumentException("Invalid diagnostic type: " + diagnosticType);
        }
        return diagnostic;
    }

    public enum DiagnosticType {
        IncorrectLang,
        IncorrectDirectory,
        NotValidYAML,

        ;

        public String getDiagnosticExplanation(DiagnosticType diagnosticType) {
            return toString();
        }

        public DiagnosticSeverity getSeverity(DiagnosticType diagnosticType) {
            return switch (diagnosticType) {
                case IncorrectLang, IncorrectDirectory, NotValidYAML -> DiagnosticSeverity.ERROR;
            };
        }
    }


}
