package org.server.document;

import lombok.Builder;
import org.yaml.snakeyaml.nodes.Node;

import java.util.List;
import java.util.Map;

public class WorkflowEvents {

    @Builder
    public record WorkflowEvent(
            WorkflowCall workflowCall,
            WorkflowRun workflowRun,
            WorkflowDispatch workflowDispatch
    ) {}

    @Builder
    public record WorkflowCall(
        Node input,
        Node output,
        Map<String,Secrets.Secret> secrets

    ) {}

    @Builder
    public record WorkflowRun(
            List<String> workflows,
            List<String> types,
            Node branches,
            Node branchesIgnore
    ) {}

    @Builder
    public record WorkflowDispatch(
        Node input,
        Node output
    ) {}
}
