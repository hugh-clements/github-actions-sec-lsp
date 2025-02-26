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
        String name = null;
        String runName = null;
        DocumentModel.OnObject on = null;
        DocumentModel.Defaults defaults = null;
        List<DocumentModel.Job> jobs = null;
        Secrets.Permissions permission = null;
        DocumentModel.Concurrency concurrency = null;

        for (var nodeTuple : node.getValue()) {
            ScalarNode scalar = (ScalarNode) nodeTuple.getKeyNode();
            switch (scalar.getValue()) {
                case "name":
                    name = ((ScalarNode) nodeTuple.getKeyNode()).getValue();
                case "runName":
                    runName = ((ScalarNode) nodeTuple.getKeyNode()).getValue();
                case "on":
                    on = parseOn(nodeTuple.getValueNode());
                case "defaults": defaults = parseDefaults(nodeTuple.getValueNode());
                case "jobs": {
                    assert nodeTuple.getValueNode() instanceof MappingNode;
                    jobs = parseJobs((MappingNode) nodeTuple.getValueNode());
                }
                case "permission": permission = parsePermission(nodeTuple.getValueNode());
                case "concurrency": concurrency = parseConcurrency(nodeTuple.getValueNode());
                default:
                    logger.error("Failed to parse NodeTuple");
            }
        }
        return new DocumentModel.Model(name, runName, on, defaults, jobs, permission, concurrency);
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
            Secrets.Permissions permissions = null;
            String condition = null;
            List<String> needs = null;
            List<DocumentModel.Runner> runsOn = null;
            DocumentModel.Env env = null;
            DocumentModel.Concurrency concurrency = null;
            String group = null;
            DocumentModel.Defaults defaults = null;
            List<String> labels = null;
            String environment = null;
            DocumentModel.Container container = null;
            DocumentModel.Services services = null;
            DocumentModel.Steps steps = null;
            String uses = null;
            String with = null;
            List<Secrets.Secret> secret = null;
            List<String> other = null;


            var jobDetails = ((MappingNode)node.getValueNode()).getValue();
            for (var jobTuple : jobDetails) {
                switch (((ScalarNode)jobTuple.getKeyNode()).getValue()) {
                    case "runs-on": runsOn = parseRunners(jobTuple.getValueNode());
                    case "steps":
                }
            }


            var job = new DocumentModel.Job(jobName,permissions,condition,needs,runsOn,env,concurrency,group,defaults,labels,environment,container,services,steps,uses,with,secret,other);
            jobs.add(job);
        });

        return jobs;
    }

    private List<DocumentModel.Runner> parseRunners(Node runnerNode) {
        var runners = new ArrayList<DocumentModel.Runner>();
        if (runnerNode instanceof ScalarNode) {
            runners.add(DocumentModel.Runner.toRunner(((ScalarNode) runnerNode).getValue()));
        } else if (runnerNode instanceof SequenceNode) {
        } else {
            ((SequenceNode) runnerNode).getValue().forEach(runner -> {


                runners.add(DocumentModel.Runner.toRunner(((ScalarNode) runner).getValue()));
            } );
            logger.error("Failed to parse Runners, Node is not SequenceNode or ScalarNode");
        }
        return runners;
    }

    private DocumentModel.Defaults parseDefaults(Node defaultsNode) {
        return new DocumentModel.Defaults(null,null);
    }
}