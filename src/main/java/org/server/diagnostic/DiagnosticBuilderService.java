package org.server.diagnostic;

import org.eclipse.lsp4j.Diagnostic;
import org.eclipse.lsp4j.DiagnosticSeverity;
import org.server.document.Located;

/**
 * Service to build the diagnostic to send to the client
 */
public class DiagnosticBuilderService {

    /**
     * Creates diagnostic from provided data
     * @param located accepts any part of the model that can be located
     * @param diagnosticType the type of diagnostic to create
     * @return LSP spec diagnostic from LSP4J
     */
    public static Diagnostic getDiagnostic(Located<?> located, DiagnosticType diagnosticType) {
        var diagnostic = new Diagnostic();
        diagnostic.setRange(Located.locatedToRange(located));
        diagnostic.setSeverity(getSeverity(diagnosticType));
        diagnostic.setMessage(getDiagnosticMessage(diagnosticType));
        diagnostic.setCode(diagnosticType.toString());
        return diagnostic;
    }

    /**
     * Diagnostic types list
     */
    public enum DiagnosticType {
        IncorrectLang,
        IncorrectDirectory,
        NotValidYAML,
        CommandExecution,
        CodeInjection,
        Repojackable,
        PWNRequest,
        RunnerHijacker,
        PermissionControl,
        UnpinnedAction,
        WorkflowRun,
        UnsafeInputAssignment,
        OutdatedReference
    }

    /**
     * Gets the message based on the diagnostic type
     * @param diagnosticType type to get message for
     * @return String diagnostic message
     */
    public static String getDiagnosticMessage(DiagnosticType diagnosticType) {
        return switch (diagnosticType) {
            case IncorrectLang -> "Document is not in YAML format, all GitHub workflow files must be in YAML";
            case IncorrectDirectory -> "Workflow is not in the correct directory, all GitHub workflow files must be '.github/workflows'";
            case NotValidYAML -> "The document is invalid YAML, all Github workflow files must be in valid YAML";
            case CommandExecution -> "null";
            case CodeInjection -> "null";
            case Repojackable -> "null";
            case PWNRequest -> "null";
            case RunnerHijacker -> "Using self-hosted runners in public repositories can allow 3rd-party users to run code on the host machine. " +
                    "This should be mitigated by making the repository private or using GitHub's provided runners.";
            case PermissionControl -> "Setting global permissions can be unsafe as unwanted permissions can be given to certain actions or workflows. " +
                    "This could allow an attacker to ";
            case UnpinnedAction -> "null";
            case WorkflowRun -> "null";
            case UnsafeInputAssignment -> "null";
            case OutdatedReference -> "null";
        };
    }

    /**
     * Gets the severity from the diagnostic type
     * @param diagnosticType type to get severity from
     * @return Severity from LSP4J matching the LSP spec
     */
    public static DiagnosticSeverity getSeverity(DiagnosticType diagnosticType) {
        return switch (diagnosticType) {
            case PWNRequest, PermissionControl, RunnerHijacker, OutdatedReference -> DiagnosticSeverity.Warning;
            case IncorrectLang, IncorrectDirectory, NotValidYAML, CommandExecution, CodeInjection, UnsafeInputAssignment, WorkflowRun, Repojackable, UnpinnedAction-> DiagnosticSeverity.Error;
        };
    }
}



