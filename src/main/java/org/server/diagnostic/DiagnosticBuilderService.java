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
        INCORRECT_LANG,
        INCORRECT_DIRECTORY,
        NOT_VALID_YAML,
        COMMAND_EXECUTION,
        CODE_INJECTION,
        REPOJACKABLE,
        PWN_REQUEST,
        RUNNER_HIJACKER,
        PERMISSION_CONTROL,
        UNPINNED_ACTION,
        WORKFLOW_RUN,
        UNSAFE_INPUT_ASSIGNMENT,
        OUTDATED_REFERENCE
    }

    /**
     * Gets the message based on the diagnostic type
     * @param diagnosticType type to get message for
     * @return String diagnostic message
     */
    public static String getDiagnosticMessage(DiagnosticType diagnosticType) {
        //TODO Finish updating descriptions
        return switch (diagnosticType) {
            case INCORRECT_LANG -> "Document is not in YAML format, all GitHub workflow files must be in YAML";
            case INCORRECT_DIRECTORY -> "Workflow is not in the correct directory, all GitHub workflow files must be '.github/workflows'";
            case NOT_VALID_YAML -> "The document is invalid YAML, all Github workflow files must be in valid YAML";
            case COMMAND_EXECUTION -> "null";
            case CODE_INJECTION -> "null";
            case REPOJACKABLE -> "null";
            case PWN_REQUEST -> "null";
            case RUNNER_HIJACKER -> "Using self-hosted runners in public repositories can allow 3rd-party users to run code on the host machine. " +
                    "This should be mitigated by making the repository private or using GitHub's provided runners.";
            case PERMISSION_CONTROL -> "Setting global permissions can be unsafe as unwanted permissions can be given to certain actions or workflows. " +
                    "This could allow an attacker to ";
            case UNPINNED_ACTION -> "null";
            case WORKFLOW_RUN -> "null";
            case UNSAFE_INPUT_ASSIGNMENT -> "null";
            case OUTDATED_REFERENCE -> "null";
        };
    }

    /**
     * Gets the severity from the diagnostic type
     * @param diagnosticType type to get severity from
     * @return Severity from LSP4J matching the LSP spec
     */
    public static DiagnosticSeverity getSeverity(DiagnosticType diagnosticType) {
        return switch (diagnosticType) {
            case PWN_REQUEST, PERMISSION_CONTROL, RUNNER_HIJACKER, OUTDATED_REFERENCE -> DiagnosticSeverity.Warning;
            case INCORRECT_LANG, INCORRECT_DIRECTORY, NOT_VALID_YAML, COMMAND_EXECUTION, CODE_INJECTION,
                 UNSAFE_INPUT_ASSIGNMENT,
                 WORKFLOW_RUN,
                 REPOJACKABLE, UNPINNED_ACTION -> DiagnosticSeverity.Error;
        };
    }
}



