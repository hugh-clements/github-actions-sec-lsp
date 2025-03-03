package org.server.document;

import lombok.Builder;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.yaml.snakeyaml.nodes.Node;
import java.util.List;
import java.util.Map;

public record DocumentModel(String lang, String documentURI, org.server.document.DocumentModel.Model model) {

    static Logger logger = LogManager.getLogger(DocumentModel.class);

    @Builder
    public record Model(
            String name,
            String runName,
            OnObject on,
            Defaults defaults,
            List<Job> jobs,
            Map<Secrets.PermissionType, Secrets.PermissionLevel> permissions,
            Concurrency concurrency
    ) {
    }

    @Builder
    public record OnObject(
            List<Event> event,
            List<WorkflowEvents.WorkflowEvent> workFlowEvent
    ) {
    }

    @Builder
    public record Event(
            String eventName,
            Node filter,
            List<String> type,
            Node schedule,
            String condition,
            Node milestone
    ) {
    }

    /* RFU
    @Builder
    public record Filter(
            List<String> tags,
            List<String> tagIgnore,
            List<String> branches,
            List<String> branchIgnore,
            List<String> paths,
            List<String> pathIgnore
    ) {
    } */

    @Builder
    public record Job(
            String name,
            Map<Secrets.PermissionType, Secrets.PermissionLevel> permissions,
            String condition,
            List<String> needs,
            List<Runner> runsOn,
            Map<String, String> env,
            Concurrency concurrency,
            String group,
            Defaults defaults,
            List<String> labels,
            String environment,
            Node container,
            Node services,
            List<Node> steps,
            String uses,
            String with,
            Map<String,Secrets.Secret> secret,
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

    public enum Runner {
        self_hosted,
        ubuntu_latest,
        ubuntu_24_04,
        ubuntu_22_04,
        ubuntu_20_04,
        windows_2025,
        windows_2022,
        windows_2019,
        ubuntu_24_04_arm,
        ubuntu_22_04_arm,
        macos_13,
        macos_14,
        macos_15,
        macos_latest;

        public static Runner toRunner(String stringRunner) {
            return switch (stringRunner) {
                case "self-hosted" -> self_hosted;
                case "ubuntu-latest" -> ubuntu_latest;
                case "ubuntu-24-04" -> ubuntu_24_04;
                case "ubuntu-22-04" -> ubuntu_22_04;
                case "ubuntu_20_04" -> ubuntu_20_04;
                case "windows-2025" -> windows_2025;
                case "windows-2022" -> windows_2022;
                case "windows-2019" -> windows_2019;
                case "ubuntu-24-04-arm" -> ubuntu_24_04_arm;
                case "ubuntu-22-04-arm" -> ubuntu_22_04_arm;
                case "macos-13" -> macos_13;
                case "macos-14" -> macos_14;
                case "macos-15" -> macos_15;
                case "macos-latest" -> macos_latest;
                default -> {
                    logger.info(stringRunner);
                    throw new IllegalArgumentException("Unsupported Runner: " + stringRunner);
                }
            };
        }
    }
}