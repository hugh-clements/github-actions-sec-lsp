package org.server.diagnostic;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.eclipse.lsp4j.Diagnostic;
import org.server.document.DocumentModel;
import org.server.document.Located;

import java.util.ArrayList;
import java.util.List;

import static org.server.diagnostic.DiagnosticBuilderService.getDiagnostic;
import static org.server.diagnostic.DiagnosticUtils.*;

public class ActionReferencingDiagnosticProvider implements DiagnosticProvider {

    static Logger logger = LogManager.getLogger(ActionReferencingDiagnosticProvider.class);

    @Override
    public List<Diagnostic> diagnose(DocumentModel document) {
        logger.info("Diagnosing Action Referencing");
        var diagnostics = new ArrayList<Diagnostic>();
        document.model().jobs().forEach(job -> {
            checkUnpinnedAction(diagnostics, job.uses(), document);
            job.steps().forEach(step -> checkUnpinnedAction(diagnostics, step.uses(), document));
        });
        return diagnostics;
    }

    public static void checkUnpinnedAction(List<Diagnostic> diagnostics,Located<String> uses, DocumentModel document) {
        if (uses == null) return;
        var value = uses.value();
        switch (value) {
            case String a when a.contains("docker://") -> {}
            case String b when b.contains("./") -> {}
            default -> {
                var split = value.split("[/@]");
                if (split.length < 2) return;
                if (!split[split.length-1].matches(commitHashRegex)) {
                    diagnostics.add(getDiagnostic(uses, DiagnosticBuilderService.DiagnosticType.UnpinnedAction));
                } else {
                    diagnostics.addAll(getRepojackableDiagnostic(document));
                }
            }
        }
    }

    public static List<Diagnostic> getRepojackableDiagnostic(DocumentModel document) {
        var diagnostics = new ArrayList<Diagnostic>();
        document.model().jobs().forEach( job -> {
            checkRepojackable(diagnostics,job.uses());
            job.steps().forEach( step -> checkRepojackable(diagnostics,step.uses()));
        });
        return diagnostics;
    }

    public static void checkRepojackable(List<Diagnostic> diagnostics, Located<String> uses) {
        if (uses == null) return;
        var usesAction = uses.value().split("[/@]");
        if (usesAction.length < 2) return;
        var statusCode = getRepoStatus(usesAction[0],usesAction[1]);
        if ((statusCode >= 300 && statusCode < 400) || statusCode == 404) {
            diagnostics.add(getDiagnostic(uses, DiagnosticBuilderService.DiagnosticType.Repojackable));
        }
    }

}
