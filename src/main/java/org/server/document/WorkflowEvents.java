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
            List<Located<Input>> inputs,
            Node output,
            Map<String, SecretsAndPermissions.Secret> secrets

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
            List<Located<Input>> inputs,
            Node output
    ) {}

    @Builder
    public record Input(
            String key,
            boolean required,
            String description,
            String defaultValue,
            InputType inputType,
            Node options
    ) {}


    public enum InputType {
        BOOLEAN,
        CHOICE,
        NUMBER,
        ENVIRONMENT,
        STRING;

        public static InputType toInputType(String stringType) {
            return switch (stringType) {
                case "boolean" -> InputType.BOOLEAN;
                case "choice" -> InputType.CHOICE;
                case "number" -> InputType.NUMBER;
                case "string" -> InputType.STRING;
                case "environment" -> InputType.ENVIRONMENT;
                default -> throw new IllegalArgumentException("Unsupported input type: " + stringType);
            };
        }
    }
}
