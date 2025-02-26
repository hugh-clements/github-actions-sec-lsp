import org.junit.jupiter.api.Test;
import org.server.document.ModelConstructorService;
import org.yaml.snakeyaml.nodes.MappingNode;
import org.yaml.snakeyaml.nodes.NodeTuple;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;

public class ParseYAMLTest {

    @Test
    public void testParseYAML() throws IOException {
        String toParse = Files.readString(Path.of("src/test/resources/test1.yaml"));
        var modelConstructorService = new ModelConstructorService();
        var parsedYAML = modelConstructorService.parseYAML(toParse);
        assert parsedYAML instanceof MappingNode;
        System.out.println(((MappingNode) ((MappingNode) ((MappingNode) parsedYAML).getValue().get(3).getValueNode()).getValue().getFirst().getValueNode()).getValue());
    }
}
