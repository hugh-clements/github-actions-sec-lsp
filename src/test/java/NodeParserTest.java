import org.junit.jupiter.api.Test;
import org.server.document.ModelConstructorService;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;

public class NodeParserTest {

    @Test
    public void testNodeParser() throws IOException {
        String toParse = Files.readString(Path.of("src/test/resources/test1.yaml"));
        var modelConstructorService = new ModelConstructorService();
        var parsed = modelConstructorService.modelConstructor("yaml", "", toParse);
        Utils.prettyPrint(parsed);
    }

    @Test
    public void testNodeParser2() throws IOException {
        String toParse = Files.readString(Path.of("src/test/resources/test2.yaml"));
        var modelConstructorService = new ModelConstructorService();
        var parsed = modelConstructorService.modelConstructor("yaml", "", toParse);
        Utils.prettyPrint(parsed);
    }

    @Test
    public void testNodeParserConcurrency1() throws IOException {
        String toParse = Files.readString(Path.of("src/test/resources/testConcurrency1.yaml"));
        var modelConstructorService = new ModelConstructorService();
        var parsed = modelConstructorService.modelConstructor("yaml", "", toParse);
        Utils.prettyPrint(parsed);
    }

    @Test
    public void testNodeParserDefaults1() throws IOException {
        String toParse = Files.readString(Path.of("src/test/resources/testDefaults1.yaml"));
        var modelConstructorService = new ModelConstructorService();
        var parsed = modelConstructorService.modelConstructor("yaml", "", toParse);
        Utils.prettyPrint(parsed);
    }

    @Test
    public void testNodeParserEvents1() throws IOException {
        String toParse = Files.readString(Path.of("src/test/resources/testEvents1.yaml"));
        var modelConstructorService = new ModelConstructorService();
        var parsed = modelConstructorService.modelConstructor("yaml", "", toParse);
        Utils.prettyPrint(parsed);
    }

    @Test
    public void testNodeParserEvents2() throws IOException {
        String toParse = Files.readString(Path.of("src/test/resources/testEvents2.yaml"));
        var modelConstructorService = new ModelConstructorService();
        var parsed = modelConstructorService.modelConstructor("yaml", "", toParse);
        Utils.prettyPrint(parsed);
    }

    @Test
    public void testNodeParserPermissions1() throws IOException {
        String toParse = Files.readString(Path.of("src/test/resources/testPermissions1.yaml"));
        var modelConstructorService = new ModelConstructorService();
        var parsed = modelConstructorService.modelConstructor("yaml", "", toParse);
        Utils.prettyPrint(parsed);
    }

    @Test
    public void testNodeParserPermissions2() throws IOException {
        String toParse = Files.readString(Path.of("src/test/resources/testPermissions2.yaml"));
        var modelConstructorService = new ModelConstructorService();
        var parsed = modelConstructorService.modelConstructor("yaml", "", toParse);
        Utils.prettyPrint(parsed);
    }

    @Test
    public void testNodeParserPermissions3() throws IOException {
        String toParse = Files.readString(Path.of("src/test/resources/testPermissions3.yaml"));
        var modelConstructorService = new ModelConstructorService();
        var parsed = modelConstructorService.modelConstructor("yaml", "", toParse);
        Utils.prettyPrint(parsed);
    }

    @Test
    public void testNodeParserSecrets1() throws IOException {
        String toParse = Files.readString(Path.of("src/test/resources/testSecrets1.yaml"));
        var modelConstructorService = new ModelConstructorService();
        var parsed = modelConstructorService.modelConstructor("yaml", "", toParse);
        Utils.prettyPrint(parsed);
    }

    @Test
    public void testNodeParserWorkflowEvents1() throws IOException {
        String toParse = Files.readString(Path.of("src/test/resources/testWorkflowEvents1.yaml"));
        var modelConstructorService = new ModelConstructorService();
        var parsed = modelConstructorService.modelConstructor("yaml", "", toParse);
        Utils.prettyPrint(parsed);
    }



}
