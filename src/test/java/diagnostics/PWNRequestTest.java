package diagnostics;

import org.junit.jupiter.api.Test;
import org.server.diagnostic.DiagnosticBuilderService;
import org.server.diagnostic.PWNRequestDiagnosticProvider;

import java.io.IOException;

import static diagnostics.UtilsTest.getModel;

public class PWNRequestTest {

    private static final PWNRequestDiagnosticProvider diagnosticProvider = new PWNRequestDiagnosticProvider();

    @Test
    public void testNoIssues() throws IOException {
        var model = getModel("src/test/resources/Diagnostic/pwnrequest1.yaml");
        var diagnostics = diagnosticProvider.diagnose(model.value());
        assert diagnostics.isEmpty();
    }

    @Test
    public void testSingleIssue() throws IOException {
        var model = getModel("src/test/resources/Diagnostic/pwnrequest2.yaml");
        var diagnostics = diagnosticProvider.diagnose(model.value());
        assert diagnostics.stream().anyMatch(diagnostic -> diagnostic.getCode().getLeft().equals(DiagnosticBuilderService.DiagnosticType.PWN_REQUEST.toString()));
    }
}
