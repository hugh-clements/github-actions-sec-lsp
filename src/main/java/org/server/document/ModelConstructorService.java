package org.server.document;

/**
 * Class that contains the DocumentModel constructor method
 */
public class ModelConstructorService {
    /**
     * Method that parses YAML and then constructs a DocumentModel
     * @return new Document model reflecting the current document in the client
     */
    public DocumentModel modelConstructor() {

        return new DocumentModel();
    }
}
