import org.junit.jupiter.api.Test;
import org.server.document.ModelConstructorService;
import org.yaml.snakeyaml.nodes.MappingNode;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;

public class ParseYAMLTest {

    @Test
    public void testParseYAML1() throws IOException {
        String toParse = Files.readString(Path.of("src/test/resources/test1.yaml"));
        var modelConstructorService = new ModelConstructorService();
        var parsedYAML = modelConstructorService.parseYAML(toParse);
        assert parsedYAML instanceof MappingNode;
        System.out.println(((MappingNode) ((MappingNode) ((MappingNode) parsedYAML).getValue().get(3).getValueNode()).getValue().getFirst().getValueNode()).getValue());
    }

    @Test
    public void testParseYAML2() throws IOException {
        String toParse = Files.readString(Path.of("src/test/resources/test2.yaml"));
        var modelConstructorService = new ModelConstructorService();
        var parsedYAML = modelConstructorService.parseYAML(toParse);
        assert parsedYAML instanceof MappingNode;
        System.out.println(parsedYAML);
    }

    @Test
    public void testParseYAML3() throws IOException {
        String toParse = Files.readString(Path.of("src/test/resources/testPermissions1.yaml"));
        var modelConstructorService = new ModelConstructorService();
        var parsedYAML = modelConstructorService.parseYAML(toParse);
        assert parsedYAML instanceof MappingNode;
        System.out.println(((MappingNode) parsedYAML).getValue().get(3).getValueNode());
    }

    @Test
    public void testParseYAML4() throws IOException {
        String toParse = Files.readString(Path.of("src/test/resources/testPermissions2.yaml"));
        var modelConstructorService = new ModelConstructorService();
        var parsedYAML = modelConstructorService.parseYAML(toParse);
        System.out.println(( (MappingNode) ((MappingNode) parsedYAML).getValue().get(3).getValueNode()).getValue());
    }

    @Test
    public void testParseYAML5() throws IOException {
        String toParse = Files.readString(Path.of("src/test/resources/testPermissions3.yaml"));
        var modelConstructorService = new ModelConstructorService();
        var parsedYAML = modelConstructorService.parseYAML(toParse);
        System.out.println(((MappingNode) parsedYAML).getValue().get(3).getValueNode());
    }

    @Test
    public void testParseYAML6() throws IOException {
        String toParse = Files.readString(Path.of("src/test/resources/testSecrets1.yaml"));
        var modelConstructorService = new ModelConstructorService();
        var parsedYAML = modelConstructorService.parseYAML(toParse);
        System.out.println(((MappingNode) parsedYAML).getValue().get(2).getValueNode());
        System.out.println(((MappingNode) ((MappingNode) parsedYAML).getValue().get(2).getValueNode()).getValue().getFirst().getValueNode());
        System.out.println(((MappingNode) ((MappingNode) ((MappingNode) parsedYAML).getValue().get(2).getValueNode()).getValue().getFirst().getValueNode()).getValue());
    }

    @Test
    public void testParseYAML7() throws IOException {
        String toParse = Files.readString(Path.of("src/test/resources/testDefaults1.yaml"));
        var modelConstructorService = new ModelConstructorService();
        var parsedYAML = modelConstructorService.parseYAML(toParse);
        System.out.println(((MappingNode) ((MappingNode) parsedYAML).getValue().get(2).getValueNode()).getValue().getFirst());
    }



    @Test
    public void testParseYAML9() throws Exception {
        String toParse = Files.readString(Path.of("src/test/resources/testWorkflowEvents1.yaml"));
        var modelConstructorService = new ModelConstructorService();
        var parsedYAML = modelConstructorService.parseYAML(toParse);
        System.out.println(((MappingNode) ((MappingNode) parsedYAML).getValue().get(1).getValueNode()).getValue());
    }

}
