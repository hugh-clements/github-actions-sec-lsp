package org.server.document;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.yaml.snakeyaml.Yaml;
import org.yaml.snakeyaml.nodes.MappingNode;
import org.yaml.snakeyaml.nodes.Node;
import org.yaml.snakeyaml.nodes.ScalarNode;
import org.yaml.snakeyaml.nodes.SequenceNode;

import java.io.StringReader;
import java.util.ArrayList;
import java.util.List;

/**
 * Class that contains the DocumentModel constructor method
 */
public class ModelConstructorService {

    Logger logger = LogManager.getLogger(getClass());

    /**
     * Method that parses YAML and then constructs a DocumentModel
     *
     * @return new Document model reflecting the current document in the client
     */
    public DocumentModel modelConstructor(String lang, String documentURI, String text) throws Exception {
        logger.info("Constructing model");
        var node = parseYAML(text);
        if (!(node instanceof MappingNode)) {
            logger.warn("Not a mapping node, setting model to null");
            return new DocumentModel(lang, documentURI, null);
        }
        var model = parseNode((MappingNode) node);
        return new DocumentModel(lang, documentURI, model);
    }

    public Node parseYAML(String text) {
        logger.info("Parsing String into YAML");
        Yaml yaml = new Yaml();
        return yaml.compose(new StringReader(text));
    }

    private DocumentModel.Model parseNode(MappingNode node) {
        logger.info("Parsing node into model");
        var modelBuilder = DocumentModel.Model.builder();

        for (var nodeTuple : node.getValue()) {
            ScalarNode scalar = (ScalarNode) nodeTuple.getKeyNode();
            switch (scalar.getValue()) {
                case "name":
                    modelBuilder.name(((ScalarNode) nodeTuple.getKeyNode()).getValue());
                case "runName":
                    modelBuilder.runName(((ScalarNode) nodeTuple.getKeyNode()).getValue());
                case "on":
                    modelBuilder.on(parseOn(nodeTuple.getValueNode()));
                case "defaults": modelBuilder.defaults(parseDefaults(nodeTuple.getValueNode()));
                case "jobs": {
                    assert nodeTuple.getValueNode() instanceof MappingNode;
                    modelBuilder.jobs(parseJobs((MappingNode) nodeTuple.getValueNode()));
                }
                case "permission": modelBuilder.permission(parsePermission(nodeTuple.getValueNode()));
                case "concurrency": modelBuilder.concurrency(parseConcurrency(nodeTuple.getValueNode()));
                default:
                    logger.error("Failed to parse NodeTuple");
            }
        }
        return modelBuilder.build();
    }

    private DocumentModel.OnObject parseOn(Node onNode) {
        List<DocumentModel.Event> event = null;
        List<WorkflowEvents.WorkflowEvent> workFlowEvent = null;


        return new DocumentModel.OnObject(event, workFlowEvent);
    }

    private DocumentModel.Concurrency parseConcurrency(Node concurrencyNode) {
        return new DocumentModel.Concurrency(null,null);
    }

    private Secrets.Permissions parsePermission(Node permissionNode) {
        return new Secrets.Permissions(null,null,null,null,null,null,null,null,null,null,null,null,null,null);
    }

    private List<DocumentModel.Job> parseJobs(MappingNode jobNode) {
        var jobs = new ArrayList<DocumentModel.Job>();
        jobNode.getValue().forEach(node -> {
            var jobName = ((ScalarNode) node.getKeyNode()).getValue();
            var jobBuilder = DocumentModel.Job.builder();
            jobBuilder.name(jobName);

            var jobDetails = ((MappingNode)node.getValueNode()).getValue();
            for (var jobTuple : jobDetails) {
                switch (((ScalarNode)jobTuple.getKeyNode()).getValue()) {
                    case "runs-on": jobBuilder.runsOn(parseRunners(jobTuple.getValueNode()));
                    case "steps": jobBuilder.steps();
                }
            }
            jobs.add(jobBuilder.build());
        });

        return jobs;
    }

    private List<DocumentModel.Runner> parseRunners(Node runnerNode) {
        var runners = new ArrayList<DocumentModel.Runner>();
        if (runnerNode instanceof ScalarNode) {
            runners.add(DocumentModel.Runner.toRunner(((ScalarNode) runnerNode).getValue()));
        } else if (runnerNode instanceof SequenceNode) {
            ((SequenceNode) runnerNode).getValue().forEach(runner -> {


                runners.add(DocumentModel.Runner.toRunner(((ScalarNode) runner).getValue()));
            } );
        } else {
            logger.error("Failed to parse Runners, Node is not SequenceNode or ScalarNode");
        }
        return runners;
    }

    private DocumentModel.Defaults parseDefaults(Node defaultsNode) {
        return new DocumentModel.Defaults(null,null);
    }
}