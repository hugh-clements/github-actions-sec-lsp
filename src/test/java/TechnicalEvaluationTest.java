import org.junit.jupiter.api.Test;
import org.server.diagnostic.DiagnosticService;

import java.io.IOException;

import static diagnostics.TestUtils.getModel;

class TechnicalEvaluationTest {

    private static final DiagnosticService diagnosticService = new DiagnosticService();

    @Test
    void testFile1() throws IOException {
        var model = getModel("src/test/resources/TechnicalEvaluation/file1.yaml");
        var diagnostics = diagnosticService.diagnose(model);
        System.err.println("File 1:");
        System.err.println(diagnostics);
    }

    @Test
    void testFile2() throws IOException {
        var model = getModel("src/test/resources/TechnicalEvaluation/file2.yaml");
        var diagnostics = diagnosticService.diagnose(model);
        System.err.println("File 2:");
        System.err.println(diagnostics);
    }

    @Test
    void testFile3() throws IOException {
        var model = getModel("src/test/resources/TechnicalEvaluation/file3.yaml");
        var diagnostics = diagnosticService.diagnose(model);
        System.err.println("File 3:");
        System.err.println(diagnostics);
    }

    @Test
    void testFile4() throws IOException {
        var model = getModel("src/test/resources/TechnicalEvaluation/file4.yaml");
        var diagnostics = diagnosticService.diagnose(model);
        System.err.println("File 4:");
        System.err.println(diagnostics);
    }

    @Test
    void testFile5() throws IOException {
        var model = getModel("src/test/resources/TechnicalEvaluation/file5.yaml");
        var diagnostics = diagnosticService.diagnose(model);
        System.err.println("File 5:");
        System.err.println(diagnostics);
    }
}
