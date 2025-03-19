package org.server.diagnostic;

import org.eclipse.lsp4j.Diagnostic;
import org.server.document.DocumentModel;

import java.util.List;

public interface DiagnosticProvider {

    List<Diagnostic> diagnose(DocumentModel document);
}
