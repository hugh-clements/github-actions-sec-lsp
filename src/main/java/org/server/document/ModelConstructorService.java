package org.server.document;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.yaml.snakeyaml.Yaml;
import org.yaml.snakeyaml.nodes.MappingNode;
import org.yaml.snakeyaml.nodes.Node;
import org.yaml.snakeyaml.nodes.ScalarNode;

import java.io.StringReader;
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
                case "jobs": jobs = null;
                case "permission": permission = null;
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

    private DocumentModel.Job parseJob(Node jobNode) {
        return new DocumentModel.Job(null,null,null,null,null,null,null,null,null,null,null,null,null,null,null,null,null,null,null);
    }

    private DocumentModel.Defaults parseDefaults(Node defaultsNode) {
        return new DocumentModel.Defaults(null,null);
    }
}