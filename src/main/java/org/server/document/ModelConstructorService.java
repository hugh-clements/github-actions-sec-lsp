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

        mappingToMap(mappingNode).forEach( (nodeTuple, value) -> {
            switch (nodeTuple) {
                case "name" ->
                    modelBuilder.name(parseToString((ScalarNode) value));
                case "runName" ->
                    modelBuilder.runName(parseToString((ScalarNode) value));
                case "on" ->
                    modelBuilder.on(parseOn(value));
                case "defaults" -> modelBuilder.defaults(parseDefaults(value));
                case "jobs" ->
                    modelBuilder.jobs(parseJobs((MappingNode) value));
                case "permission" -> modelBuilder.permission(parsePermissions(value));
                case "concurrency" -> modelBuilder.concurrency(parseConcurrency(value));
                default ->
                    logger.error("Failed to parse NodeTuple");
            }
        });
        return modelBuilder.build();
    }

    /** Parse the "on" keyword */
    private DocumentModel.OnObject parseOn(Node onNode) {
        List<DocumentModel.Event> event = null;
        List<WorkflowEvents.WorkflowEvent> workFlowEvent = null;


        return new DocumentModel.OnObject(event, workFlowEvent);
    }

    /** Parse the "concurrency" keyword */
    private DocumentModel.Concurrency parseConcurrency(Node concurrencyNode) {
        return new DocumentModel.Concurrency(null,null);
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
                    case "if" -> jobBuilder.condition(value);
                    case "needs" -> {
                        if (value instanceof SequenceNode) jobBuilder.needs(parseToStringList((SequenceNode) value));
                        if (value instanceof ScalarNode) jobBuilder.needs(Collections.singletonList(parseToString((ScalarNode) value)));
                    }
                    case "runs-on" -> jobBuilder.runsOn(parseRunners(value));
                    case "env" -> {
                        jobBuilder.env(parseToStringMap((MappingNode) value));
                    }
                    case "concurrency" -> {}
                    case "group" -> {}
                    case "defaults" -> {}
                    case "labels" -> {}
                    case "environment" -> {}
                    case "container" -> {}
                    case "services" -> {}
                    case "steps" ->
                        jobBuilder.steps(((SequenceNode) value).getValue());
                    case "uses" -> {}
                    case "with" -> {}
                    case "secret" -> {}
                    default -> {}
                }
            });
            jobs.add(jobBuilder.build());
        });
        return jobs;
    }

    /** Parse the "on" keyword */
    private Secrets.Permissions parsePermissions(Node runnersNode) {
        var permissionBuilder = Secrets.Permissions.builder();


        return permissionBuilder.build();
    }

    /** Parse the "runner" keyword */
    private List<DocumentModel.Runner> parseRunners(Node runnerNode) {
        var runners = new ArrayList<DocumentModel.Runner>();
        if (runnerNode instanceof ScalarNode) {
            runners.add(DocumentModel.Runner.toRunner(((ScalarNode) runnerNode).getValue()));
        } else if (runnerNode instanceof SequenceNode) {
            ((SequenceNode) runnerNode).getValue().forEach(runner -> runners.add(DocumentModel.Runner.toRunner(((ScalarNode) runner).getValue())));
        } else {
            logger.error("Failed to parse Runners, Node is not SequenceNode or ScalarNode");
        }
        return runners;
    }

    /** Parse the "defaults" keyword */
    private DocumentModel.Defaults parseDefaults(Node defaultsNode) {
        return new DocumentModel.Defaults(null,null);
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
        mappingNode.getValue().forEach(node -> newMap.put(((ScalarNode)node.getKeyNode()).getValue(), node.getKeyNode()));
        return newMap;
    }

    private Map<String, String> parseToStringMap(MappingNode mappingNode) {
        var newMap = new HashMap<String, String>();
        mappingNode.getValue().forEach(node -> newMap.put(parseToString((ScalarNode) node.getKeyNode()),parseToString((ScalarNode) node.getValueNode())));
        return newMap;
    }

}