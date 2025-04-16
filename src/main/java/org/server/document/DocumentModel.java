package org.server.document;

import lombok.Builder;
import lombok.Singular;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.yaml.snakeyaml.nodes.Node;

import java.util.List;
import java.util.Map;

/**
 * Data Structure that represents the client file state
 * @param lang document language
 * @param documentURI document path
 * @param model Data structure that represents the YAML document structure
 */
public record DocumentModel(String lang, String documentURI, org.server.document.DocumentModel.Model model) {

    static Logger logger = LogManager.getLogger(DocumentModel.class);

    @Builder
    public record Model(
            String name,
            String runName,
            OnObject on,
            Defaults defaults,
            Map<String, String> env,
            List<Job> jobs,
            Map<SecretsAndPermissions.PermissionType, SecretsAndPermissions.PermissionLevel> permissions,
            Concurrency concurrency
    ) {
    }

    @Builder
    public record OnObject(
            List<Event> events,
            List<WorkflowEvents.WorkflowEvent> workflowEvents
    ) {
    }

    @Builder
    public record Event(
            String eventName,
            @Singular List<Node> filters,
            List<String> type,
            Node schedule,
            String condition,
            Node milestone
    ) {
    }

    @Builder
    public record Job(
            String jobId,
            String jobName,
            Map<SecretsAndPermissions.PermissionType, SecretsAndPermissions.PermissionLevel> permissions,
            String condition,
            List<String> needs,
            List<Located<Runner>> runsOn,
            Map<String, String> env,
            Concurrency concurrency,
            String group,
            Defaults defaults,
            List<String> labels,
            String environment,
            Node container,
            Node services,
            List<Step> steps,
            Located<String> uses,
            With with,
            String passSecretTokenOrInherited,
            Node other
    ) {
    }

    @Builder
    public record Concurrency(
            String group,
            Boolean cancelInProgress
    ) {
    }

    @Builder
    public record Defaults(
            String shell,
            String workingDirectory
    ) {
    }

    @Builder
    public record Step(
            String id,
            String condition,
            String name,
            Located<String> uses,
            Located<String> run,
            String workingDirectory,
            String shell,
            With with,
            Map<String, String> env,
            Boolean continueOnError,
            Node other
    ) {}

    public enum Runner {
        SELF_HOSTED,
        UBUNTU_LATEST,
        UBUNTU_24_04,
        UBUNTU_22_04,
        UBUNTU_20_04,
        WINDOWS_2025,
        WINDOWS_2022,
        WINDOWS_2019,
        UBUNTU_24_04_ARM,
        UBUNTU_22_04_ARM,
        MACOS_13,
        MACOS_14,
        MACOS_15,
        MACOS_LATEST;

        public static Runner toRunner(String stringRunner) {
            return switch (stringRunner) {
                case "self-hosted" -> SELF_HOSTED;
                case "ubuntu-latest" -> UBUNTU_LATEST;
                case "ubuntu-24-04" -> UBUNTU_24_04;
                case "ubuntu-22-04" -> UBUNTU_22_04;
                case "ubuntu_20_04" -> UBUNTU_20_04;
                case "windows-2025" -> WINDOWS_2025;
                case "windows-2022" -> WINDOWS_2022;
                case "windows-2019" -> WINDOWS_2019;
                case "ubuntu-24-04-arm" -> UBUNTU_24_04_ARM;
                case "ubuntu-22-04-arm" -> UBUNTU_22_04_ARM;
                case "macos-13" -> MACOS_13;
                case "macos-14" -> MACOS_14;
                case "macos-15" -> MACOS_15;
                case "macos-latest" -> MACOS_LATEST;
                default -> {
                    logger.info(stringRunner);
                    throw new IllegalArgumentException("Unsupported Runner: " + stringRunner);
                }
            };
        }
    }

    @Builder
    public record With(
            @Singular List<Located<String>> values,
            Located<String> entrypoint,
            Located<String> args,
            @Singular Map<String, Located<String>> mappings
    ) {}
}