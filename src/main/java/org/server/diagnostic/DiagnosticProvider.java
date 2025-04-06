package org.server.diagnostic;

import org.eclipse.lsp4j.Diagnostic;
import org.server.document.DocumentModel;

import java.util.List;

/**
 * Interface to set Diagnostic provider classes for initialisation
 */
public interface DiagnosticProvider {
    /**
     * Method to get all diagnostics of class type
     * @param doc document model
     * @return list of diagnostics
     */
    List<Diagnostic> diagnose(DocumentModel doc);
}
