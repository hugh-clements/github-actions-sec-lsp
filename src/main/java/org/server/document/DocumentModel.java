package org.server.document;

import java.util.List;

public class DocumentModel {

    public DocumentModel() {
        var test = new Model("testname","run-test", null,null,null, null,null);
    }

    public record Model(
            String name,
            String runName,
            OnObject on,
            Defaults defaults,
            List<Job> jobs,
            Secrets.Permissions permission,
            Concurrency concurrency
    ) {}

    public record OnObject(
            List<Event> event,
            List<WorkflowEvents.WorkflowEvent> workFlowEvent
    ) {}

    public record Event(
            String event,
            List<Filter> filter,
            List<String> type,
            Schedule schedule
    ) {}

    public record Filter(
            List<String> tags,
            List<String> tagIgnore,
            List<String> branches,
            List<String> branchIgnore,
            List<String> paths,
            List<String> pathIgnore
    ) {}

    public record Schedule(
            List<String> schedules
    ) {}

    public record Env(
        List<String> env
    ) {}

    public record Job(
            String id,
            String name,
            Secrets.Permissions permissions,
            String condition,
            List<String> needs,
            List<Runner> runsOn,
            Env env,
            Concurrency concurrency,
            String group,
            List<String> labels,
            String environment,
            Steps steps
    ) {}

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
        macos_latest
    }

    public record Concurrency(
            String group,
            Boolean cancelInProgress
    ) {}

    public record Defaults(
            String shell,
            String workingDirectory
    ) {}

    public record Steps(

    ) {}
}