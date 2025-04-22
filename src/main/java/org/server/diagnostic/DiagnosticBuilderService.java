package org.server.diagnostic;


import org.eclipse.lsp4j.*;
import org.server.document.Located;
import java.util.ArrayList;

import static org.server.document.Located.locatedToRange;

/**
 * Service to build the diagnostic to send to the client
 */
public class DiagnosticBuilderService {

    /**
     *
     */
    private static final String LSP_READ_ME_URL = "https://github.com/hugh-clements/github-actions-sec-lsp/blob/main/README.md";
    /**
     * Creates diagnostic from provided data
     * @param located accepts any part of the model that can be located
     * @param diagnosticType the type of diagnostic to create
     * @return LSP spec diagnostic from LSP4J
     */
    public static Diagnostic getDiagnostic(Located<?> located, DiagnosticType diagnosticType) {
        var diagnostic = new Diagnostic();
        diagnostic.setSource("GitHub Actions Security LSP");
        diagnostic.setRange(locatedToRange(located));
        diagnostic.setSeverity(getSeverity(diagnosticType));
        diagnostic.setMessage(getDiagnosticMessage(diagnosticType));
        diagnostic.setCode(diagnosticType.toString());
        var description = new DiagnosticCodeDescription();
        description.setHref(LSP_READ_ME_URL + "#" + diagnosticType.toString().toLowerCase());
        diagnostic.setCodeDescription(description);
        return diagnostic;
    }


    /**
     * Creates diagnostic from provided data, also adding an extra location
     * @param locatedMain accepts any part of the model that can be located
     * @param diagnosticType accepts any part of the model that can be located
     * @param locatedRelated located object of related location
     * @return LSP spec diagnostic from LSP4J with related diagnostic information
     */
    public static Diagnostic getDiagnosticWithRelatedInfo(Located<?> locatedMain, DiagnosticType diagnosticType, Located<?> locatedRelated, String relatedMessage, String uri) {
        var relatedInfoList = new ArrayList<DiagnosticRelatedInformation>();
        var relatedInfo = new DiagnosticRelatedInformation();
        var location = new Location();
        location.setRange(locatedToRange(locatedRelated));
        location.setUri(uri);
        relatedInfo.setLocation(location);
        relatedInfo.setMessage(relatedMessage);
        relatedInfoList.add(relatedInfo);
        var diagnostic = getDiagnostic(locatedMain, diagnosticType);
        diagnostic.setRelatedInformation(relatedInfoList);
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
     * @param diagnosticType type to get a message for
     * @return String diagnostic message
     */
    public static String getDiagnosticMessage(DiagnosticType diagnosticType) {
        return switch (diagnosticType) {
            case INCORRECT_LANG -> "Document is not in YAML format; all GitHub workflow files must be in YAML.";
            case INCORRECT_DIRECTORY -> "Workflow is not in the correct directory; all GitHub workflow files must reside in '.github/workflows'.";
            case NOT_VALID_YAML -> "The document is invalid YAML; all GitHub workflow files must be valid and parsable YAML.";
            case COMMAND_EXECUTION -> "An attacker-controlled input may reach a command execution context, allowing arbitrary code to run during workflow execution.";
            case CODE_INJECTION -> "A value originating from an untrusted input has been interpolated directly into a script field, which can lead to code injection.";
            case REPOJACKABLE -> "The referenced repository or owner appears vulnerable to 'RepoJacking' due to a rename or transfer, allowing potential attacker takeover.";
            case PWN_REQUEST -> "An action triggered by 'pull_request_target' that also uses 'actions/checkout' on the pull request branch can lead to repository compromise, " +
                    "as external contributors control the pull request branch.";
            case RUNNER_HIJACKER -> "Using self-hosted runners in public repositories can allow 3rd-party users to execute code on the host machine. " +
                    "Mitigation: restrict to private repositories or use GitHub-hosted runners.";
            case PERMISSION_CONTROL -> "Using global permissions may unintentionally grant excessive privileges to workflows or actions, " +
                    "increasing the risk of unwanted operations and security breaches.";
            case UNPINNED_ACTION -> "Referencing actions by branch or tag allows code to change without notice. To avoid supply chain attacks, reference actions using a specific commit hash.";
            case WORKFLOW_RUN -> "An action triggered by another workflow ('workflow_run') can execute code from attacker-controlled branches if the trigger is misconfigured." +
                    " Ensure all triggered actions treat such sources as untrusted.";
            case UNSAFE_INPUT_ASSIGNMENT -> "Attacker-controlled inputs can be passed into 'with' fields unsanitized, leading to code injection or command execution. " +
                    "Always validate external inputs before use.";
            case OUTDATED_REFERENCE -> "The action or repository reference points to an outdated commit, which might lack security patches. Update to a newer, reviewed commit.";
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



