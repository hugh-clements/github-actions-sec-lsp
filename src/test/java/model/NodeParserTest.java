package model;

import org.junit.jupiter.api.Test;
import org.server.document.ModelConstructorService;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;

class NodeParserTest {

    @Test
    void testNodeParser() throws IOException {
        String toParse = Files.readString(Path.of("src/test/resources/DocumentSync/test1.yaml"));
        var modelConstructorService = new ModelConstructorService();
        var parsed = modelConstructorService.modelConstructor("yaml", "", toParse);
        Utils.prettyPrint(parsed.value());
    }

    @Test
    void testNodeParser2() throws IOException {
        String toParse = Files.readString(Path.of("src/test/resources/DocumentSync/test2.yaml"));
        var modelConstructorService = new ModelConstructorService();
        var parsed = modelConstructorService.modelConstructor("yaml", "", toParse);
        Utils.prettyPrint(parsed.value());
    }

    @Test
    void testNodeParserConcurrency1() throws IOException {
        String toParse = Files.readString(Path.of("src/test/resources/DocumentSync/testConcurrency1.yaml"));
        var modelConstructorService = new ModelConstructorService();
        var parsed = modelConstructorService.modelConstructor("yaml", "", toParse);
        Utils.prettyPrint(parsed.value());
    }

    @Test
    void testNodeParserDefaults1() throws IOException {
        String toParse = Files.readString(Path.of("src/test/resources/DocumentSync/testDefaults1.yaml"));
        var modelConstructorService = new ModelConstructorService();
        var parsed = modelConstructorService.modelConstructor("yaml", "", toParse);
        Utils.prettyPrint(parsed.value());
    }

    @Test
    void testNodeParserEvents1() throws IOException {
        String toParse = Files.readString(Path.of("src/test/resources/DocumentSync/testEvents1.yaml"));
        var modelConstructorService = new ModelConstructorService();
        var parsed = modelConstructorService.modelConstructor("yaml", "", toParse);
        Utils.prettyPrint(parsed.value());
    }

    @Test
    void testNodeParserEvents2() throws IOException {
        String toParse = Files.readString(Path.of("src/test/resources/DocumentSync/testEvents2.yaml"));
        var modelConstructorService = new ModelConstructorService();
        var parsed = modelConstructorService.modelConstructor("yaml", "", toParse);
        Utils.prettyPrint(parsed.value());
    }

    @Test
    void testNodeParserPermissions1() throws IOException {
        String toParse = Files.readString(Path.of("src/test/resources/DocumentSync/testPermissions1.yaml"));
        var modelConstructorService = new ModelConstructorService();
        var parsed = modelConstructorService.modelConstructor("yaml", "", toParse);
        Utils.prettyPrint(parsed.value());
    }

    @Test
    void testNodeParserPermissions2() throws IOException {
        String toParse = Files.readString(Path.of("src/test/resources/DocumentSync/testPermissions2.yaml"));
        var modelConstructorService = new ModelConstructorService();
        var parsed = modelConstructorService.modelConstructor("yaml", "", toParse);
        Utils.prettyPrint(parsed.value());
    }

    @Test
    void testNodeParserPermissions3() throws IOException {
        String toParse = Files.readString(Path.of("src/test/resources/DocumentSync/testPermissions3.yaml"));
        var modelConstructorService = new ModelConstructorService();
        var parsed = modelConstructorService.modelConstructor("yaml", "", toParse);
        Utils.prettyPrint(parsed.value());
    }

    @Test
    void testNodeParserSecrets1() throws IOException {
        String toParse = Files.readString(Path.of("src/test/resources/DocumentSync/testSecrets1.yaml"));
        var modelConstructorService = new ModelConstructorService();
        var parsed = modelConstructorService.modelConstructor("yaml", "", toParse);
        Utils.prettyPrint(parsed.value());
    }

    @Test
    void testNodeParserSecrets2() throws IOException {
        String toParse = Files.readString(Path.of("src/test/resources/DocumentSync/testSecrets2.yaml"));
        var modelConstructorService = new ModelConstructorService();
        var parsed = modelConstructorService.modelConstructor("yaml", "", toParse);
        Utils.prettyPrint(parsed.value());
    }

    @Test
    void testNodeParserWorkflowEvents1() throws IOException {
        String toParse = Files.readString(Path.of("src/test/resources/DocumentSync/testWorkflowEvents1.yaml"));
        var modelConstructorService = new ModelConstructorService();
        var parsed = modelConstructorService.modelConstructor("yaml", "", toParse);
        Utils.prettyPrint(parsed.value());
    }

    @Test
    void testNodeParserSteps1() throws IOException {
        String toParse = Files.readString(Path.of("src/test/resources/DocumentSync/testSteps1.yaml"));
        var modelConstructorService = new ModelConstructorService();
        var parsed = modelConstructorService.modelConstructor("yaml", "", toParse);
        Utils.prettyPrint(parsed.value());
    }

    @Test
    void testNodeParserSteps2() throws IOException {
        String toParse = Files.readString(Path.of("src/test/resources/DocumentSync/testSteps2.yaml"));
        var modelConstructorService = new ModelConstructorService();
        var parsed = modelConstructorService.modelConstructor("yaml", "", toParse);
        Utils.prettyPrint(parsed.value());
    }

    @Test
    void testNodeParserSteps3() throws IOException {
        String toParse = Files.readString(Path.of("src/test/resources/DocumentSync/testSteps3.yaml"));
        var modelConstructorService = new ModelConstructorService();
        var parsed = modelConstructorService.modelConstructor("yaml", "", toParse);
        Utils.prettyPrint(parsed.value());
    }

    @Test
    void testNodeParserSteps4() throws IOException {
        String toParse = Files.readString(Path.of("src/test/resources/DocumentSync/testSteps4.yaml"));
        var modelConstructorService = new ModelConstructorService();
        var parsed = modelConstructorService.modelConstructor("yaml", "", toParse);
        Utils.prettyPrint(parsed.value());
    }

    @Test
    void testNodeParserSteps5() throws IOException {
        String toParse = Files.readString(Path.of("src/test/resources/DocumentSync/testSteps5.yaml"));
        var modelConstructorService = new ModelConstructorService();
        var parsed = modelConstructorService.modelConstructor("yaml", "", toParse);
        Utils.prettyPrint(parsed.value());
    }

    @Test
    void testNodeParserInputs1() throws IOException {
        String toParse = Files.readString(Path.of("src/test/resources/DocumentSync/testInputs1.yaml"));
        var modelConstructorService = new ModelConstructorService();
        var parsed = modelConstructorService.modelConstructor("yaml", "", toParse);
        Utils.prettyPrint(parsed.value());
    }

    @Test
    void testNodeParserInputs2() throws IOException {
        String toParse = Files.readString(Path.of("src/test/resources/DocumentSync/testInputs2.yaml"));
        var modelConstructorService = new ModelConstructorService();
        var parsed = modelConstructorService.modelConstructor("yaml", "", toParse);
        Utils.prettyPrint(parsed.value());
    }

    @Test
    void testNodeParserMatrix1() throws IOException {
        String toParse = Files.readString(Path.of("src/test/resources/DocumentSync/testMatrix1.yaml"));
        var modelConstructorService = new ModelConstructorService();
        var parsed = modelConstructorService.modelConstructor("yaml", "", toParse);
        Utils.prettyPrint(parsed.value());
    }

}
