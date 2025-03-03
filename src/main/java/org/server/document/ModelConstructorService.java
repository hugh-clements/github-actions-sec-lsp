package org.server.document;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.yaml.snakeyaml.Yaml;
import org.yaml.snakeyaml.nodes.MappingNode;
import org.yaml.snakeyaml.nodes.Node;
import org.yaml.snakeyaml.nodes.ScalarNode;
import org.yaml.snakeyaml.nodes.SequenceNode;
import java.io.StringReader;
import java.util.*;

/**
 * Class that contains the DocumentModel constructor method
 */
public class ModelConstructorService {

    Logger logger = LogManager.getLogger(getClass());

    /**
     * Method that parses YAML and then constructs a DocumentModel
     * @return new Document model reflecting the current document in the client
     */
    public DocumentModel modelConstructor(String lang, String documentURI, String text) {
        logger.info("Constructing model");
        var node = parseYAML(text);
        if (!(node instanceof MappingNode)) {
            logger.warn("Not a mapping node, setting model to null");
            return new DocumentModel(lang, documentURI, null);
        }
        var model = parseNode((MappingNode) node);
        return new DocumentModel(lang, documentURI, model);
    }

    /**
     * Parse plain text into snakeYAML nodes
     * @param text plain text document
     * @return Node structure that represents document state
     */
    public Node parseYAML(String text) {
        logger.info("Parsing String into YAML");
        Yaml yaml = new Yaml();
        return yaml.compose(new StringReader(text));
    }

    /**
     * Parse the SnakeYAML Nodes into the document model
     * @param mappingNode Top level MappingNode that represents the parsed YAML
     * @return Completed document model representing the current document state
     */
    private DocumentModel.Model parseNode(MappingNode mappingNode) {
        logger.info("Parsing node into model");
        var modelBuilder = DocumentModel.Model.builder();
        mappingToMap(mappingNode).forEach( (string, value) -> {
            switch (string) {
                case "name" ->
                    modelBuilder.name(parseToString((ScalarNode) value));
                case "run-name" ->
                    modelBuilder.runName(parseToString((ScalarNode) value));
                case "on" ->
                    modelBuilder.on(parseOn(value));
                case "defaults" -> modelBuilder.defaults(parseDefaults((MappingNode) value));
                case "jobs" -> modelBuilder.jobs(parseJobs((MappingNode) value));
                case "permissions" -> modelBuilder.permissions(parsePermissions(value));
                case "concurrency" -> modelBuilder.concurrency(parseConcurrency((MappingNode) value));
                default ->
                    logger.error("Failed to parse NodeTuple: " + string);
            }
        });
        return modelBuilder.build();
    }

    /** Parse the "on" keyword */
    private DocumentModel.OnObject parseOn(Node onNode) {
        return switch (onNode) {
            case ScalarNode s -> new DocumentModel.OnObject(parseSimpleEvent(s),null);
            case SequenceNode sq -> new DocumentModel.OnObject(parseSimpleEventSequence(sq),null);
            case MappingNode m -> parseEventMapping(m);
            default -> throw new IllegalArgumentException("Failed to parse 'On' node, unexpected node type");
        };
    }

    public DocumentModel.OnObject parseEventMapping(MappingNode eventMappingNode) {
        List<DocumentModel.Event> events = new ArrayList<>();
        List<WorkflowEvents.WorkflowEvent> workflowEvents = new ArrayList<>();
        mappingToMap(eventMappingNode).forEach((s, n) -> {
            switch (s) {
                case "workflow_call","workflow_run","workflow_dispatch" -> workflowEvents.add(parseWorkflowEvent((MappingNode) n));
                default -> events.add(parseEvent((MappingNode) n));
            }
        });
        return new DocumentModel.OnObject(events, workflowEvents);
    }

    public DocumentModel.Event parseEvent(MappingNode eventNode) {
        var builder = DocumentModel.Event.builder();
        builder.eventName(getSingletonMapKey(eventNode));
        mappingToMap(getSingletonMapValue(eventNode)).forEach( (s, n) -> {
            switch (s) {
                case "type" -> builder.type(getStringFromSequenceOrScalar(n));
                case "schedule" -> builder.schedule(n);
                case "condition" -> parseToString((ScalarNode) n);
                case "milestone" -> builder.milestone(n);
                default -> builder.filter(n);
            }
        });
        return builder.build();
    }

    public WorkflowEvents.WorkflowEvent parseWorkflowEvent(MappingNode eventNode) {
        var builder = WorkflowEvents.WorkflowEvent.builder();
        switch (getSingletonMapKey(eventNode)) {
            case "workflow_call" -> {
                var callBuilder = WorkflowEvents.WorkflowCall.builder();
                mappingToMap(getSingletonMapValue(eventNode)).forEach( (s,n) -> {
                    switch (s) {
                        case "inputs" -> callBuilder.input(n);
                        case "outputs" -> callBuilder.output(n);
                        case "secrets" -> callBuilder.secrets(parseSecrets((MappingNode) n));
                    }
                });
                builder.workflowCall(callBuilder.build());
            }
            case "workflow_run" -> {
                var runBuilder = WorkflowEvents.WorkflowRun.builder();
                mappingToMap(getSingletonMapValue(eventNode)).forEach( (s,n) -> {
                    switch (s) {
                        case "workflows" -> runBuilder.workflows(getStringFromSequenceOrScalar(n));
                        case "types" -> runBuilder.types(getStringFromSequenceOrScalar(n));
                        case "branches" -> runBuilder.branches(n);
                        case "branches-ignore" -> runBuilder.branchesIgnore(n);
                    }
                });
                builder.workflowRun(runBuilder.build());
            }
            case "workflow_dispatch" -> {
                var dispatchBuilder = WorkflowEvents.WorkflowDispatch.builder();
                mappingToMap(getSingletonMapValue(eventNode)).forEach( (s,n) -> {
                    switch (s) {
                        case "input" -> dispatchBuilder.input(n);
                        case "output" -> dispatchBuilder.output(n);
                    }
                });
                builder.workflowDispatch(dispatchBuilder.build());
            }
        }
        return builder.build();
    }

    public List<DocumentModel.Event> parseSimpleEvent(ScalarNode eventNode) {
        var builder = DocumentModel.Event.builder();
        builder.eventName(eventNode.getValue());
        return Collections.singletonList(builder.build());
    }

    public List<DocumentModel.Event> parseSimpleEventSequence(SequenceNode eventSequenceNode) {
        List<DocumentModel.Event> event = new ArrayList<>();
        eventSequenceNode.getValue().forEach( s -> {
            var builder = DocumentModel.Event.builder();
            builder.eventName(((ScalarNode)s).getValue());
            event.add(builder.build());
        });
        return event;
    }

    /** Parse the "concurrency" keyword */
    private DocumentModel.Concurrency parseConcurrency(MappingNode concurrencyNode) {
        var builder = DocumentModel.Concurrency.builder();
        mappingToMap(concurrencyNode).forEach( (s, n) -> {
           switch (s) {
               case "group" -> builder.group(((ScalarNode)n).getValue());
               case "cancel-in-progress" -> builder.cancelInProgress(Boolean.parseBoolean(((ScalarNode)n).getValue()));
           }
        });
        return builder.build();
    }

    /** Parse the "jobs" keyword */
    private List<DocumentModel.Job> parseJobs(MappingNode jobNode) {
        var jobs = new ArrayList<DocumentModel.Job>();
        mappingToMap(jobNode).forEach((jobName, node) -> {
            var jobBuilder = DocumentModel.Job.builder();
            jobBuilder.name(jobName);
            var jobDetails = mappingToMap((MappingNode) node);
            jobDetails.forEach((key, value) -> {
                switch (key) {
                    case "permissions" ->
                        jobBuilder.permissions(parsePermissions(value));
                    case "if" -> jobBuilder.condition(((ScalarNode)value).getValue());
                    case "needs" -> {
                        if (value instanceof SequenceNode) jobBuilder.needs(parseToStringList((SequenceNode) value));
                        if (value instanceof ScalarNode) jobBuilder.needs(Collections.singletonList(parseToString((ScalarNode) value)));
                    }
                    case "runs-on" -> jobBuilder.runsOn(parseRunners(value));
                    case "env" -> jobBuilder.env(parseToStringMap((MappingNode) value));
                    case "concurrency" -> jobBuilder.concurrency(parseConcurrency((MappingNode) value));
                    case "group" -> jobBuilder.group(parseToString((ScalarNode) value));
                    case "defaults" -> jobBuilder.defaults(parseDefaults((MappingNode) value));
                    case "labels" -> jobBuilder.labels(parseToStringList((SequenceNode) value));
                    case "environment" -> jobBuilder.environment(parseToString((ScalarNode) value));
                    case "container" -> jobBuilder.container(value);
                    case "services" -> jobBuilder.services(value);
                    case "steps" -> jobBuilder.steps(((SequenceNode) value).getValue());
                    case "uses" -> jobBuilder.uses(parseToString((ScalarNode) value));
                    case "with" -> jobBuilder.with(parseToString((ScalarNode) value));
                    case "secret" -> jobBuilder.secret(parseSecrets((MappingNode) value));
                    default -> jobBuilder.other(value);
                }
            });
            jobs.add(jobBuilder.build());
        });
        return jobs;
    }

    /** Parse the "permissions" keyword */
    private Map<Secrets.PermissionType, Secrets.PermissionLevel> parsePermissions(Node permissionNode) {
        switch (permissionNode) {
            case ScalarNode s -> {
                return parseScalarPermissions(s.getValue());
            }
            case MappingNode m -> {
                return parseMappingPermissions(m);
            }
            default -> throw new IllegalArgumentException("Unexpected Node type when parsing permissions");
        }
    }

    public Map<Secrets.PermissionType, Secrets.PermissionLevel> parseScalarPermissions(String permissionLevel) {
        var level = switch (permissionLevel) {
            case "read-all" -> Secrets.PermissionLevel.READ;
            case "write-all" -> Secrets.PermissionLevel.WRITE;
            case "{}" -> Secrets.PermissionLevel.NONE;
            default -> throw new IllegalArgumentException("Failed to parse permissions inside ScalarNode");
        };
        var map = new HashMap<Secrets.PermissionType,Secrets.PermissionLevel>();
        for (var value : Secrets.PermissionType.values()) {
            map.put(value, level);
        }
        return map;
    }

    public Map<Secrets.PermissionType, Secrets.PermissionLevel> parseMappingPermissions(MappingNode mappingNode) {
        var map = new HashMap<Secrets.PermissionType,Secrets.PermissionLevel>();
        mappingToMap(mappingNode).forEach((string,node) -> {
            var level = switch (((ScalarNode)node).getValue()) {
                case "read-all" -> Secrets.PermissionLevel.READ;
                case "write-all" -> Secrets.PermissionLevel.WRITE;
                case "{}" -> Secrets.PermissionLevel.NONE;
                default -> throw new IllegalStateException("Failed to parse permissionLevel inside MappingNode");
            };
            map.put(Secrets.PermissionType.stringToPermissionType(string),level);
        });
        return map;
    }

    /** Parse the "runner" keyword */
    private List<DocumentModel.Runner> parseRunners(Node runnerNode) {
        var runners = new ArrayList<DocumentModel.Runner>();
        if (runnerNode instanceof ScalarNode) {
            runners.add(DocumentModel.Runner.toRunner(((ScalarNode) runnerNode).getValue()));
        } else if (runnerNode instanceof SequenceNode) {
            ((SequenceNode) runnerNode).getValue().forEach(runner -> runners.add(DocumentModel.Runner.toRunner(((ScalarNode) runner).getValue())));
        } else {
            System.err.println("Failed Node: " + runnerNode);
            throw new IllegalArgumentException("Failed to parse Runners, Node is not SequenceNode or ScalarNode");
        }
        return runners;
    }

    /** Parse the "defaults" keyword */
    private DocumentModel.Defaults parseDefaults(MappingNode defaultsNode) {
        var tupleList = ((MappingNode) defaultsNode.getValue().getFirst().getValueNode()).getValue();
        var shell = ((ScalarNode) tupleList.getFirst().getValueNode()).getValue();
        var working_dir = ((ScalarNode) tupleList.getLast().getValueNode()).getValue();
        return new DocumentModel.Defaults(shell,working_dir);
    }

    /** Parse the "secrets" keyword */
    private Map<String,Secrets.Secret> parseSecrets(MappingNode secretNode) {
        var secrets = new HashMap<String,Secrets.Secret>();
        var name = getSingletonMapKey(secretNode);
        mappingToMap(secretNode).forEach((string,node) -> {
            var builder = Secrets.Secret.builder();
            switch (string) {
                case "description" -> builder.description(((ScalarNode) node).getValue());
                case "required" -> builder.required(Boolean.parseBoolean(((ScalarNode) node).getValue()));
            }
            secrets.put(name,builder.build());
        });

        return secrets;
    }

    /**
     * Helper method to convert node to its String value
     * @param node ScalarNode that contains the String value
     * @return String result
     */
    private String parseToString(ScalarNode node) {
        return node.getValue();
    }

    /**
     * Helper method to convert node into a list of strings
     * @param node Sequence node that contains ScalarNodes
     * @return List of Strings from the ScalarNodes
     */
    private List<String> parseToStringList(SequenceNode node) {
        return node.getValue().stream().map( n -> parseToString((ScalarNode) n)).toList();
    }

    /**
     * Helper method to convert nodes to a Map object
     * @param mappingNode object that has a String key and a Node value
     * @return Map<String, Node>
     */
    private Map<String, Node> mappingToMap(MappingNode mappingNode) {
        var newMap = new HashMap<String, Node>();
        mappingNode.getValue().forEach(node -> newMap.put(((ScalarNode)node.getKeyNode()).getValue(), node.getValueNode()));
        return newMap;
    }

    /**
     * Helper method to convert nodes to a Map object
     * @param mappingNode object that has a String key and value
     * @return Map<String, String>
     */
    private Map<String, String> parseToStringMap(MappingNode mappingNode) {
        var newMap = new HashMap<String, String>();
        mappingNode.getValue().forEach(node -> newMap.put(parseToString((ScalarNode) node.getKeyNode()),parseToString((ScalarNode) node.getValueNode())));
        return newMap;
    }

    private String getSingletonMapKey(MappingNode mappingNode) {
        return ((ScalarNode) mappingNode.getValue().getFirst().getKeyNode()).getValue();
    }

    private MappingNode getSingletonMapValue(MappingNode mappingNode) {
        return (MappingNode) mappingNode.getValue().getFirst().getValueNode();
    }

    private List<String> getStringFromSequenceOrScalar(Node node) {
        var list = new ArrayList<String>();
        if (node instanceof SequenceNode) {
            ((SequenceNode)node).getValue().forEach(n -> list.add(((ScalarNode) n).getValue()));
        } else if (node instanceof ScalarNode) {
            list.add(((ScalarNode) node).getValue());
        }
        return list;
    }
}