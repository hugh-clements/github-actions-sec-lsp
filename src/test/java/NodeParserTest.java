import com.google.gson.GsonBuilder;
import com.google.gson.TypeAdapter;
import com.google.gson.stream.JsonReader;
import com.google.gson.stream.JsonWriter;
import org.junit.jupiter.api.Test;
import org.server.document.ModelConstructorService;
import org.yaml.snakeyaml.nodes.MappingNode;
import org.yaml.snakeyaml.nodes.Node;
import org.yaml.snakeyaml.nodes.ScalarNode;
import org.yaml.snakeyaml.nodes.SequenceNode;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;

public class NodeParserTest {

    @Test
    public void testNodeParser() throws IOException {
        String toParse = Files.readString(Path.of("src/test/resources/test1.yaml"));
        var modelConstructorService = new ModelConstructorService();
        var parsed = modelConstructorService.modelConstructor("yaml", "", toParse);

        /** Used to pretty print the DocumentModel**/
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
        }).setPrettyPrinting().create().toJson(parsed));
    }
}
