package org.server.document;

import lombok.Builder;
import org.yaml.snakeyaml.nodes.Node;
import java.util.List;
import java.util.Map;

public record DocumentModel(String lang, String documentURI, org.server.document.DocumentModel.Model model) {

    //TODO CHECK IT ALL AGAIN AKFNJLAEFNLJAHFLIANFJLKAFHkANFEkl

    @Builder
    public record Model(
            String name,
            String runName,
            OnObject on,
            Defaults defaults,
            List<Job> jobs,
            Secrets.Permissions permission,
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
            String event,
            List<Filter> filter,
            List<String> type,
            List<String> schedule
    ) {
    }

    @Builder
    public record Filter(
            List<String> tags,
            List<String> tagIgnore,
            List<String> branches,
            List<String> branchIgnore,
            List<String> paths,
            List<String> pathIgnore
    ) {
    }

    @Builder
    public record Job(
            String name,
            Secrets.Permissions permissions,
            Node condition,
            List<String> needs,
            List<Runner> runsOn,
            Map<String, String> env,
            Concurrency concurrency,
            String group,
            Defaults defaults,
            List<String> labels,
            String environment,
            Container container,
            Services services,
            List<Node> steps,
            String uses,
            String with,
            List<Secrets.Secret> secret,
            List<String> other
    ) {
    }

    public record Services(
            List<String> services
    ) {}

    public record Container(
            List<String> container
    ) {}

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
                case "self_hosted" -> self_hosted;
                case "ubuntu_latest" -> ubuntu_latest;
                case "ubuntu_24_04" -> ubuntu_24_04;
                case "ubuntu_22_04" -> ubuntu_22_04;
                case "ubuntu_20_04" -> ubuntu_20_04;
                case "windows_2025" -> windows_2025;
                case "windows_2022" -> windows_2022;
                case "windows_2019" -> windows_2019;
                case "ubuntu_24_04_arm" -> ubuntu_24_04_arm;
                case "ubuntu_22_04_arm" -> ubuntu_22_04_arm;
                case "macos_13" -> macos_13;
                case "macos_14" -> macos_14;
                case "macos_15" -> macos_15;
                case "macos_latest" -> macos_latest;
                default -> throw new IllegalArgumentException("Unknown runner " + stringRunner);
            };
        }
    }
}