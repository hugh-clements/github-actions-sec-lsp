package org.server.diagnostic;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.eclipse.lsp4j.Diagnostic;
import org.server.document.DocumentModel;
import org.server.document.Located;

import java.util.ArrayList;
import java.util.List;

import static org.server.diagnostic.DiagnosticBuilderService.getDiagnostic;
import static org.server.diagnostic.DiagnosticUtils.getCommitFromHash;
import static org.server.diagnostic.DiagnosticUtils.olderThan3Months;

public class OutdatedReferenceDiagnosticProvider implements DiagnosticProvider {

    static Logger logger = LogManager.getLogger(OutdatedReferenceDiagnosticProvider.class);

    private static final String COMMIT = "commit";
    private static final String AUTHOR = "author";
    private static final String DATE = "date";

    @Override
    public List<Diagnostic> diagnose(DocumentModel doc) {
        logger.info("Diagnosing outdated reference");
        var diagnostics = new ArrayList<Diagnostic>();
        doc.model().jobs().forEach(job -> {
           checkOutdatedReference(diagnostics,job.uses());
           if (job.steps() == null) return;
           job.steps().forEach(step -> checkOutdatedReference(diagnostics,step.uses()));
        });
        return diagnostics;
    }

    private void checkOutdatedReference(List<Diagnostic> diagnostics, Located<String> uses) {
        if (uses == null) return;
        if (!uses.value().contains("@")) return;
        var usesAction = uses.value().replace(".github/workflows/", "").split("[/@]");
        if (usesAction.length < 2) return;
        var currentCommit = getCommitFromHash(usesAction[0],usesAction[1],usesAction[usesAction.length-1]);
        if (currentCommit ==  null) return;
        if (currentCommit.entrySet().isEmpty() || !currentCommit.getAsJsonObject(COMMIT).getAsJsonObject("verification").getAsJsonPrimitive("verified").getAsBoolean()) {
            diagnostics.add(getDiagnostic(uses, DiagnosticBuilderService.DiagnosticType.OUTDATED_REFERENCE));
            return;
        }
        var allCommits = DiagnosticUtils.getRepoCommits(usesAction[0],usesAction[1]);
        if (allCommits == null || allCommits.isEmpty()) return;
        var latestCommitDate = allCommits.getAsJsonArray().get(0).getAsJsonObject()
                .getAsJsonObject(COMMIT)
                .getAsJsonObject(AUTHOR)
                .get(DATE).getAsString();
        var currentCommitDate = currentCommit
                .getAsJsonObject(COMMIT)
                .getAsJsonObject(AUTHOR)
                .get(DATE).getAsString();
        if (olderThan3Months(currentCommitDate, latestCommitDate)) {
            diagnostics.add(getDiagnostic(uses, DiagnosticBuilderService.DiagnosticType.OUTDATED_REFERENCE));
        }
    }
}
