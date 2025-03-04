import com.google.gson.GsonBuilder;
import com.google.gson.TypeAdapter;
import com.google.gson.stream.JsonReader;
import com.google.gson.stream.JsonWriter;
import org.server.document.DocumentModel;
import org.yaml.snakeyaml.nodes.MappingNode;
import org.yaml.snakeyaml.nodes.Node;
import org.yaml.snakeyaml.nodes.ScalarNode;
import org.yaml.snakeyaml.nodes.SequenceNode;

import java.io.IOException;

/**
 * Test helper class
 */
public class Utils {

    /**
     * Helper method to pretty print DocumentModels making SnakeYaml Nodes cleaner
     * @param documentModel to print
     */
    public static void prettyPrint(DocumentModel documentModel) {
        System.out.println(new GsonBuilder().registerTypeAdapter(Node.class, new TypeAdapter<Node>() {
            @Override
            public void write(JsonWriter jsonWriter, Node node) throws IOException {
                switch (node) {
                    case ScalarNode s -> jsonWriter.value("Node: " + s.getValue());
                    case SequenceNode sq -> jsonWriter.value("Node: " + sq.getValue());
                    case MappingNode m -> jsonWriter.value("Node: " + m.getValue());
                    case null, default -> jsonWriter.nullValue();
                }
            }
            @Override
            public Node read(JsonReader jsonReader) throws IOException {
                return null;
            }
        }).setPrettyPrinting().create().toJson(documentModel));
    }
}
