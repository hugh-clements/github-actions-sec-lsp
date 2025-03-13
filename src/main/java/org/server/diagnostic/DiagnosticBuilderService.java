package org.server.diagnostic;

import org.eclipse.lsp4j.Diagnostic;
import org.eclipse.lsp4j.DiagnosticSeverity;
import org.server.document.DocumentModel;
import org.server.document.Located;

public class DiagnosticBuilderService {

    public Diagnostic getOverallDiagnostic(Located<DocumentModel> documentModelLocated, DiagnosticType diagnosticType) {
        var diagnostic = new Diagnostic();
        diagnostic.setRange(Located.locatedToRange(documentModelLocated));
        diagnostic.setSeverity(getSeverity(diagnosticType));
        diagnostic.setMessage(getDiagnosticExplanation(diagnosticType));
        diagnostic.setCode(diagnosticType.toString());
        return diagnostic;
    }

    public Diagnostic getSpecificDiagnostic(Located<?> located, DiagnosticType diagnosticType) {
        var diagnostic = new Diagnostic();
        diagnostic.setRange(Located.locatedToRange(located));
        diagnostic.setSeverity(getSeverity(diagnosticType));
        diagnostic.setMessage(getDiagnosticExplanation(diagnosticType));
        diagnostic.setCode(diagnosticType.toString());
        return diagnostic;
    }

    public enum DiagnosticType {
        IncorrectLang,
        IncorrectDirectory,
        NotValidYAML,
        CommandExecution,
        CodeInject,
        Repojackable,
        PWNInjection,
        RunnerHijacker,
        PermissionControl,
        UnpinnedAction,
        WorkflowRun,
        UnsafeInputAssignment
    }

    public String getDiagnosticExplanation(DiagnosticType diagnosticType) {
        return switch (diagnosticType) {
            case IncorrectLang -> "Document is not in YAML format, all GitHub workflow files must be in YAML";
            case IncorrectDirectory -> "Workflow is not in the correct directory, all GitHub workflow files must be '.github/workflows'";
            case NotValidYAML -> "The document is invalid YAML, all Github workflow files must be in valid YAML";
            case CommandExecution -> "null";
            case CodeInject -> "null";
            case Repojackable -> "null";
            case PWNInjection -> "null";
            case RunnerHijacker -> "Using self-hosted runners in public repositories can allow 3rd-party users to run code on the host machine. " +
                    "This should be mitigated by making the repository private or using GitHub's provided runners.";
            case PermissionControl -> "Setting global permissions can be unsafe as unwanted permissions can be given to certain actions or workflows. " +
                    "This could allow an attacker to ";
            case UnpinnedAction -> "null";
            case WorkflowRun -> "null";
            case UnsafeInputAssignment -> "null";
        };
    }

    public DiagnosticSeverity getSeverity(DiagnosticType diagnosticType) {
        return switch (diagnosticType) {
            case PWNInjection, PermissionControl, RunnerHijacker -> DiagnosticSeverity.Warning;
            case IncorrectLang, IncorrectDirectory, NotValidYAML, CommandExecution, CodeInject, UnsafeInputAssignment, WorkflowRun, Repojackable, UnpinnedAction-> DiagnosticSeverity.Error;
        };
    }
}



