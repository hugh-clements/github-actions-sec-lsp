package org.server.document;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.yaml.snakeyaml.Yaml;
import org.yaml.snakeyaml.error.YAMLException;
import org.yaml.snakeyaml.nodes.MappingNode;
import org.yaml.snakeyaml.nodes.Node;
import org.yaml.snakeyaml.nodes.ScalarNode;
import org.yaml.snakeyaml.nodes.SequenceNode;

import java.io.StringReader;
import java.util.*;

import static org.server.document.Located.locate;

/**
 * Service that constructs a new DocumentModel to reflect the client state
 */
public class ModelConstructorService {

    private final Logger logger = LogManager.getLogger(getClass());


    /**
     * Method that parses YAML and then constructs a DocumentModel
     *
     * @return new Document model reflecting the current document in the client
     */
    public Located<DocumentModel> modelConstructor(String lang, String documentURI, String text) {
        logger.info("Constructing model");
        //Checking if the lang is YAML
        if (!Objects.equals(lang, "yaml")) return new Located<>(1, 1, 1, 1, new DocumentModel(lang, null, null));
        var node = parseYAML(text);
        if (!(node instanceof MappingNode)) {
            logger.warn("Not a mapping node, setting model to null");
            return new Located<>(1, 1, 1, 1, new DocumentModel(lang, documentURI, null));
        }
        var model = parseNode((MappingNode) node);
        return locate(node, new DocumentModel(lang, documentURI, model));
    }

    /**
     * Parse plain text into snakeYAML nodes
     * @param text plain text document
     * @return Node structure that represents document state
     */
    public Node parseYAML(String text) {
        logger.info("Parsing String into YAML");
        Yaml yaml = new Yaml();
        Node node;
        try {
            node = yaml.compose(new StringReader(text));
        } catch (YAMLException e) {
            logger.warn("Document is not valid YAML");
            return null;
        }
        return node;
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
                    modelBuilder.name(parseToString(value));
                case "run-name" ->
                    modelBuilder.runName(parseToString(value));
                case "on" ->
                    modelBuilder.on(parseOn(value));
                case "defaults" -> modelBuilder.defaults(parseDefaults((MappingNode) value));
                case "jobs" -> modelBuilder.jobs(parseJobs((MappingNode) value));
                case "permissions" -> modelBuilder.permissions(parsePermissions(value));
                case "concurrency" -> modelBuilder.concurrency(parseConcurrency((MappingNode) value));
                case "env" -> modelBuilder.env(parseToStringMap((MappingNode) value));
                default -> logger.error("Failed to parse NodeTuple: {}", string);
            }
        });
        return modelBuilder.build();
    }

    /** Parsing the "on" keyword */
    private DocumentModel.OnObject parseOn(Node onNode) {
        return switch (onNode) {
            case ScalarNode s -> new DocumentModel.OnObject(parseSimpleEvent(s),null);
            case SequenceNode sq -> new DocumentModel.OnObject(parseSimpleEventSequence(sq),null);
            case MappingNode m -> parseEventMapping(m);
            default -> throw new IllegalArgumentException("Failed to parse 'On' node, unexpected node type");
        };
    }

    /** Parsing Mapping of Events **/
    public DocumentModel.OnObject parseEventMapping(MappingNode eventMappingNode) {
        logger.info("Parsing Event mapping");
        List<DocumentModel.Event> events = new ArrayList<>();
        List<WorkflowEvents.WorkflowEvent> workflowEvents = new ArrayList<>();
        mappingToMap(eventMappingNode).forEach((s, n) -> {
            switch (s) {
                case "workflow_call","workflow_run","workflow_dispatch" -> workflowEvents.add(parseWorkflowEvent(s, n));
                default -> events.add(parseEvent(s, n));
            }
        });
        return new DocumentModel.OnObject(events, workflowEvents);
    }

    /** Parsing Event **/
    public DocumentModel.Event parseEvent(String name, Node eventNode) {
        logger.info("Parsing Event");
        var builder = DocumentModel.Event.builder().eventName(name);
        if (eventNode instanceof ScalarNode) {
            return builder.build();
        }
        var eventValue = getSingletonMapValue((MappingNode) eventNode);
        if (eventValue instanceof SequenceNode) {
            builder.filter(eventValue);
            return builder.build();
        }
        mappingToMap(eventValue).forEach( (s, n) -> {
            switch (s) {
                case "type" -> builder.type(getStringFromSequenceOrScalar(n));
                case "schedule" -> builder.schedule(n);
                case "condition" -> parseToString(n);
                case "milestone" -> builder.milestone(n);
                default -> builder.filter(n);
            }
        });
        return builder.build();
    }

    /** Parsing WorkflowEvent **/
    public WorkflowEvents.WorkflowEvent parseWorkflowEvent(String name, Node node) {
        logger.info("Parsing WorkflowEvent");
        var builder = WorkflowEvents.WorkflowEvent.builder();
        switch (name) {
            case "workflow_call" -> {
                var callBuilder = WorkflowEvents.WorkflowCall.builder();
                if (node instanceof ScalarNode) {
                    builder.workflowCall(callBuilder.build()).build();
                    break;
                }
                mappingToMap(node).forEach((s, n) -> {
                    switch (s) {
                        case "inputs" -> callBuilder.input(n);
                        case "outputs" -> callBuilder.output(n);
                        case "secrets" -> callBuilder.secrets(parseSecrets(n));
                        default -> throw new IllegalArgumentException("Failed to parse 'WorkflowEvent' node, unexpected node type");
                    }
                });
                builder.workflowCall(callBuilder.build());
            }
            case "workflow_run" -> {
                var runBuilder = WorkflowEvents.WorkflowRun.builder();
                if (node instanceof ScalarNode) {
                    builder.workflowRun(runBuilder.build()).build();
                    break;
                }
                mappingToMap(node).forEach( (s, n) -> {
                    switch (s) {
                        case "workflows" -> runBuilder.workflows(getStringFromSequenceOrScalar(n));
                        case "types" -> runBuilder.types(getStringFromSequenceOrScalar(n));
                        case "branches" -> runBuilder.branches(n);
                        case "branches-ignore" -> runBuilder.branchesIgnore(n);
                        default -> throw new IllegalArgumentException("Failed to parse 'WorkflowEvent' node: " + s);
                    }
                });
                builder.workflowRun(runBuilder.build());
            }
            case "workflow_dispatch" -> {
                var dispatchBuilder = WorkflowEvents.WorkflowDispatch.builder();
                if (node instanceof ScalarNode) {
                    builder.workflowDispatch(dispatchBuilder.build()).build();
                    break;
                }
                mappingToMap(getSingletonMapValue((MappingNode) node)).forEach( (s, n) -> {
                    if (s.equals("input")) {
                        dispatchBuilder.input(n);
                    } else if (s.equals("output")) {
                        dispatchBuilder.output(n);
                    }
                });
                builder.workflowDispatch(dispatchBuilder.build());
            }
            default -> throw new IllegalArgumentException("Failed to parse 'WorkflowEvent' node, unexpected node type");
        }
        return builder.build();
    }

    /** Parsing ScalarNode Event with no arguments **/
    public List<DocumentModel.Event> parseSimpleEvent(ScalarNode eventNode) {
        logger.info("Parsing simple Event");
        var builder = DocumentModel.Event.builder();
        builder.eventName(eventNode.getValue());
        return Collections.singletonList(builder.build());
    }

    /** Parsing a Sequence of Events with no arguments **/
    public List<DocumentModel.Event> parseSimpleEventSequence(SequenceNode eventSequenceNode) {
        logger.info("Parsing simple Event sequence");
        List<DocumentModel.Event> event = new ArrayList<>();
        eventSequenceNode.getValue().forEach( s -> {
            var builder = DocumentModel.Event.builder();
            builder.eventName(((ScalarNode)s).getValue());
            event.add(builder.build());
        });
        return event;
    }

    /** Parsing the "concurrency" keyword */
    private DocumentModel.Concurrency parseConcurrency(MappingNode concurrencyNode) {
        logger.info("Parsing concurrency");
        var builder = DocumentModel.Concurrency.builder();
        mappingToMap(concurrencyNode).forEach( (s, n) -> {
            if (s.equals("group")) {
                builder.group(((ScalarNode) n).getValue());
            } else if (s.equals("cancel-in-progress")) {
                builder.cancelInProgress(Boolean.parseBoolean(((ScalarNode) n).getValue()));
            }
        });
        return builder.build();
    }

    /** Parsing the "jobs" keyword */
    private List<DocumentModel.Job> parseJobs(MappingNode jobNode) {
        logger.info("Parsing jobs");
        var jobs = new ArrayList<DocumentModel.Job>();
        mappingToMap(jobNode).forEach((jobName, node) -> {
            var jobBuilder = DocumentModel.Job.builder();
            jobBuilder.jobId(jobName);
            var jobDetails = mappingToMap(node);
            jobDetails.forEach((key, value) -> {
                switch (key) {
                    case "name" -> jobBuilder.jobName(parseToString(value));
                    case "permissions" ->
                        jobBuilder.permissions(parsePermissions(value));
                    case "if" -> jobBuilder.condition(((ScalarNode)value).getValue());
                    case "needs" -> {
                        if (value instanceof SequenceNode sequenceNode) jobBuilder.needs(parseToStringList(sequenceNode));
                        if (value instanceof ScalarNode) jobBuilder.needs(Collections.singletonList(parseToString(value)));
                    }
                    case "runs-on" -> jobBuilder.runsOn(parseRunners(value));
                    case "env" -> jobBuilder.env(parseToStringMap((MappingNode) value));
                    case "concurrency" -> jobBuilder.concurrency(parseConcurrency((MappingNode) value));
                    case "group" -> jobBuilder.group(parseToString(value));
                    case "defaults" -> jobBuilder.defaults(parseDefaults((MappingNode) value));
                    case "labels" -> jobBuilder.labels(parseToStringList((SequenceNode) value));
                    case "environment" -> jobBuilder.environment(parseToString(value));
                    case "container" -> jobBuilder.container(value);
                    case "services" -> jobBuilder.services(value);
                    case "steps" -> jobBuilder.steps(parseSteps(value));
                    case "uses" -> jobBuilder.uses(locate(value,parseToString(value)));
                    case "with" -> jobBuilder.with(parseWith(value));
                    case "secrets" -> jobBuilder.passSecretTokenOrInherited(parsePassSecret(value));
                    default -> jobBuilder.other(value);
                }
            });
            jobs.add(jobBuilder.build());
        });
        return jobs;
    }

    /** Parsing the "permissions" keyword */
    private Map<SecretsAndPermissions.PermissionType, SecretsAndPermissions.PermissionLevel> parsePermissions(Node permissionNode) {
        logger.info("Parsing permissions");
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

    /** Parsing ScalarNode of permissions **/
    public Map<SecretsAndPermissions.PermissionType, SecretsAndPermissions.PermissionLevel> parseScalarPermissions(String permissionLevel) {
        logger.info("Parsing scalar permissions");
        var level = switch (permissionLevel) {
            case "read-all" -> SecretsAndPermissions.PermissionLevel.READ;
            case "write-all" -> SecretsAndPermissions.PermissionLevel.WRITE;
            case "{}" -> SecretsAndPermissions.PermissionLevel.NONE;
            default -> throw new IllegalArgumentException("Failed to parse permissions inside ScalarNode");
        };
        var map = new HashMap<SecretsAndPermissions.PermissionType, SecretsAndPermissions.PermissionLevel>();
        for (var value : SecretsAndPermissions.PermissionType.values()) {
            map.put(value, level);
        }
        return map;
    }

    /** Parsing MappingNode of permissions **/
    public Map<SecretsAndPermissions.PermissionType, SecretsAndPermissions.PermissionLevel> parseMappingPermissions(MappingNode mappingNode) {
        logger.info("Parsing mapping permissions");
        var map = new HashMap<SecretsAndPermissions.PermissionType, SecretsAndPermissions.PermissionLevel>();
        mappingToMap(mappingNode).forEach((string,node) -> {
            var level = switch (((ScalarNode)node).getValue()) {
                case "read" -> SecretsAndPermissions.PermissionLevel.READ;
                case "write" -> SecretsAndPermissions.PermissionLevel.WRITE;
                case "none" -> SecretsAndPermissions.PermissionLevel.NONE;
                default -> throw new IllegalStateException("Failed to parse permissionLevel inside MappingNode");
            };
            map.put(SecretsAndPermissions.PermissionType.stringToPermissionType(string),level);
        });
        return map;
    }

    /** Parsing the "runner" keyword */
    private List<Located<DocumentModel.Runner>> parseRunners(Node runnerNode) {
        logger.info("Parsing runners");
        var runners = new ArrayList<Located<DocumentModel.Runner>>();
        switch (runnerNode) {
            case ScalarNode scalarNode ->
                    runners.add(locate(runnerNode, DocumentModel.Runner.toRunner(scalarNode.getValue())));
            case SequenceNode sequenceNode ->
                    sequenceNode.getValue().forEach(runner -> runners.add(locate(runnerNode, DocumentModel.Runner.toRunner(((ScalarNode) runner).getValue()))));
            case null, default -> {
                logger.error("Failed Node: {}", runnerNode);
                throw new IllegalArgumentException("Failed to parse Runners, Node is not SequenceNode or ScalarNode");
            }
        }
        return runners;
    }

    /** Parsing the "defaults" keyword */
    private DocumentModel.Defaults parseDefaults(MappingNode defaultsNode) {
        logger.info("Parsing defaults");
        var tupleList = ((MappingNode) defaultsNode.getValue().getFirst().getValueNode()).getValue();
        var shell = ((ScalarNode) tupleList.getFirst().getValueNode()).getValue();
        var workingDir = ((ScalarNode) tupleList.getLast().getValueNode()).getValue();
        return new DocumentModel.Defaults(shell,workingDir);
    }

    /** Parsing "steps" **/
    private List<DocumentModel.Step> parseSteps(Node stepsNode) {
        logger.info("Parsing steps");
        var steps = new ArrayList<DocumentModel.Step>();
        switch (stepsNode) {
            case ScalarNode _-> {
                steps.add(DocumentModel.Step.builder().build());
                return steps;
            }
            case SequenceNode sq -> {
                sq.getValue().forEach(map -> {
                    var builder = DocumentModel.Step.builder();
                    mappingToMap(map).forEach( (key, value) -> {
                        switch (key) {
                            case "id" -> builder.id(parseToString(value));
                            case "if" -> builder.condition(parseToString(value));
                            case "name" -> builder.name(parseToString(value));
                            case "uses" -> builder.uses(locate(value,parseToString(value)));
                            case "run" -> builder.run(locate(value,parseToString(value)));
                            case "working-directory" -> builder.workingDirectory(parseToString(value));
                            case "shell" -> builder.shell(parseToString(value));
                            case "with" -> builder.with(parseWith(value));
                            case "env" -> builder.env(parseToStringMap((MappingNode) value));
                            default -> throw new IllegalArgumentException("Failed to parse steps inside Steps");
                        }
                    });
                    steps.add(builder.build());
                });
                return steps;
            }
            default -> throw new IllegalArgumentException("Unexpected node type in Steps: " + stepsNode);
        }
    }

    /** Parsing the "with" keyword **/
    private DocumentModel.With parseWith(Node withNode) {
        logger.info("Parsing with");
        var builder = DocumentModel.With.builder();
        switch (withNode) {
            case ScalarNode s -> builder.value(locate(s,s.getValue()));
            case SequenceNode sq -> sq.getValue().forEach(node -> {
                switch (node) {
                    case MappingNode m -> builder.mapping(getSingletonMapKey(m), locate(m,getSingletonMapValueString(m)));
                    case ScalarNode s -> builder.value(locate(s,parseToString(s)));
                    //TODO add sequence node
                    default -> throw new IllegalArgumentException("Unexpected node type in With SequenceNode: " + node);
                }
            });
            case MappingNode m -> mappingToMap(m).forEach( (string, node) -> builder.mapping(string,locate(node, parseToString(node))));
            default -> throw new IllegalArgumentException("Unexpected node type in With: " + withNode);
        }
        return builder.build();
    }

    /** Parsing the "secrets" keyword */
    private Map<String, SecretsAndPermissions.Secret> parseSecrets(Node secretNode) {
        logger.info("Parsing secrets");
        var secrets = new HashMap<String, SecretsAndPermissions.Secret>();
        mappingToMap(secretNode).forEach( (secretName, secretValue) -> {
            var builder = SecretsAndPermissions.Secret.builder();
            mappingToMap(secretValue).forEach( (arg, value) -> {
                if (arg.equals("required")) {
                    builder.required(Boolean.parseBoolean(parseToString(value)));
                } else if (arg.equals("description")) {
                    builder.description(parseToString(value));
                }
            });
            secrets.put(secretName, builder.build());
        });
        return secrets;
    }

    /** Parsing the "secrets" keyword in jobs */
    private String parsePassSecret(Node secretPassNode) {
        return switch (secretPassNode) {
            case ScalarNode s -> s.getValue();
            case MappingNode m -> getSingletonMapValueString(m);
            default -> throw new IllegalArgumentException("Unexpected node type: " + secretPassNode);
        };
    }

    /**
     * Helper method to convert node to its String value
     * @param node ScalarNode that contains the String value
     * @return String result
     */
    private String parseToString(Node node) {
        return ((ScalarNode) node).getValue();
    }

    /**
     * Helper method to convert node into a list of strings
     * @param node Sequence node that contains ScalarNodes
     * @return List of Strings from the ScalarNodes
     */
    private List<String> parseToStringList(SequenceNode node) {
        return node.getValue().stream().map(this::parseToString).toList();
    }

    /**
     * Helper method to convert nodes to a Map object
     * @param node object that has a String key and a Node value
     * @return Map<String, Node>
     */
    private Map<String, Node> mappingToMap(Node node) {
        var newMap = new HashMap<String, Node>();
        switch (node) {
            case ScalarNode s -> {
                if (s.getValue() == null) return newMap;
                newMap.put(s.getValue(),null);
            }
            case MappingNode m -> m.getValue().forEach(n -> newMap.put(((ScalarNode)n.getKeyNode()).getValue(), n.getValueNode()));
            default -> throw new IllegalArgumentException("Unexpected value: " + node);
        }
        return newMap;
    }

    /**
     * Helper method to convert nodes to a Map object
     * @param mappingNode object that has a String key and value
     * @return Map<String, String>
     */
    private Map<String, String> parseToStringMap(MappingNode mappingNode) {
        var newMap = new HashMap<String, String>();
        mappingNode.getValue().forEach(node -> newMap.put(parseToString(node.getKeyNode()),parseToString(node.getValueNode())));
        return newMap;
    }

    /**
     * Helper method to get the String key from a singleton Map NodeTuple
     * @param mappingNode singleton map
     * @return String contained in the NodeTuple key
     */
    private String getSingletonMapKey(MappingNode mappingNode) {
        return ((ScalarNode) mappingNode.getValue().getFirst().getKeyNode()).getValue();
    }

    /**
     * Helper method to get the map from the value node of a singleton Map NodeTuple
     * @param mappingNode singleton map
     * @return Node contained in the NodeTuple value
     */
    private Node getSingletonMapValue(MappingNode mappingNode) {
        return mappingNode.getValue().getFirst().getValueNode();
    }

    /**
     * Helper method to get the String from the value node of a singleton Map NodeTuple
     * @param mappingNode singleton map
     * @return String contained in the NodeTuple value
     */
    private String getSingletonMapValueString(MappingNode mappingNode) {
        return ((ScalarNode) mappingNode.getValue().getFirst().getValueNode()).getValue();
    }

    /**
     * Helper method to get a String list from a SequenceNode of ScalarNodes or a ScalarNode
     * @param node SequenceNode of ScalarNodes or ScalarNode
     * @return List of String values contained in Node
     */
    private List<String> getStringFromSequenceOrScalar(Node node) {
        var list = new ArrayList<String>();
        if (node instanceof SequenceNode sequenceNode) {
            sequenceNode.getValue().forEach(n -> list.add(((ScalarNode) n).getValue()));
        } else if (node instanceof ScalarNode scalarNode) {
            list.add(scalarNode.getValue());
        }
        return list;
    }
}