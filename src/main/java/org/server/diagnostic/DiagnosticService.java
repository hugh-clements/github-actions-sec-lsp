package org.server.diagnostic;

import org.eclipse.lsp4j.Diagnostic;
import org.server.document.DocumentModel;

import java.util.ArrayList;
import java.util.List;

public class DiagnosticService {


    public List<Diagnostic> diagnose(DocumentModel document) {
        List<Diagnostic> diagnostics = new ArrayList<>();

        return diagnostics;
    }

}
