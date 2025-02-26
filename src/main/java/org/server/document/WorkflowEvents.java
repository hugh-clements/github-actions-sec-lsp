package org.server.document;

import lombok.Builder;

import java.util.List;

public class WorkflowEvents {

    @Builder
    public record WorkflowEvent(
            WorkflowCall workflowCall,
            WorkflowRun workflowRun,
            WorkflowDispatch workflowDispatch
    ) {}

    @Builder
    public record WorkflowCall(
        Input input,
        Output output,
        List<Secrets.Secret> secret

    ) {}

    public record Input(
            List<String> inputs
    ) {}
    public record Output(
            List<String> outputs
    ) {}

    @Builder
    public record WorkflowRun(
            List<String> type,
            List<String> branch,
            List<String> branchIgnore,
            List<String> workflow
    ) {}

    @Builder
    public record WorkflowDispatch(
        Input input,
        Output output
    ) {}
}
