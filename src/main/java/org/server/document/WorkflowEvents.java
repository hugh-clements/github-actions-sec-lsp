package org.server.document;

import java.util.List;

public class WorkflowEvents {

    public record WorkflowEvent(
            WorkflowCall workflowCall,
            WorkflowRun workflowRun,
            WorkflowDispatch workflowDispatch
    ) {}

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

    public record WorkflowRun(
            List<String> type,
            List<String> branch,
            List<String> branchIgnore,
            List<String> workflow
    ) {}

    public record WorkflowDispatch(
        Input input,
        Output output
    ) {}
}
