package model;

import org.junit.jupiter.api.Test;
import org.server.document.ModelConstructorService;
import org.yaml.snakeyaml.nodes.MappingNode;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;

class ParseYAMLTest {

    @Test
    void testParseYAML() throws IOException {
        String toParse = Files.readString(Path.of("src/test/resources/DocumentSync/testConcurrency1.yaml"));
        var modelConstructorService = new ModelConstructorService();
        var parsedYAML = modelConstructorService.parseYAML(toParse);
        assert parsedYAML instanceof MappingNode;
        System.out.println(((MappingNode) parsedYAML).getValue().get(3));
        System.out.println(((MappingNode) ((MappingNode) ((MappingNode) parsedYAML).getValue().get(3).getValueNode()).getValue().getFirst().getValueNode()).getValue());
    }

    @Test
    void testParseNotYAML() throws IOException {
        String toParse = Files.readString(Path.of("src/test/resources/DocumentSync/testNotYaml.yaml"));
        var modelConstructorService = new ModelConstructorService();
        var parsedYAML = modelConstructorService.parseYAML(toParse);
        System.out.println(parsedYAML);
    }

    @Test
    void testParseNotYAML2() throws IOException {
        String toParse = Files.readString(Path.of("src/test/resources/DocumentSync/testNotYaml2.yaml"));
        var modelConstructorService = new ModelConstructorService();
        var parsedYAML = modelConstructorService.parseYAML(toParse);
        assert (!(parsedYAML instanceof MappingNode));
    }

}
