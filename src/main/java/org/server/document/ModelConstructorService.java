package org.server.document;

import org.yaml.snakeyaml.Yaml;
import org.yaml.snakeyaml.nodes.MappingNode;

import java.io.StringReader;

/**
 * Class that contains the DocumentModel constructor method
 */
public class ModelConstructorService {
    /**
     * Method that parses YAML and then constructs a DocumentModel
     * @return new Document model reflecting the current document in the client
     */

    public DocumentModel modelConstructor(String lang, String documentURI, String text) throws Exception {
        Yaml yaml = new Yaml();
        var composed = yaml.compose(new StringReader(text));
        if (!(composed instanceof MappingNode)) {
            throw new Exception("Root node is not a mapping node");
        }

        var model = new DocumentModel.Model("test",null,null,null,null,null,null);
        return new DocumentModel(lang, documentURI, model);
    }
}
