package diagnostics;

import org.junit.jupiter.api.Test;
import org.server.diagnostic.DiagnosticBuilderService;
import org.server.diagnostic.UnsafeInputAssignmentDiagnosticProvider;

import java.io.IOException;

import static diagnostics.UtilsTest.getModel;

public class UnsafeInputTest {

    private static final UnsafeInputAssignmentDiagnosticProvider diagnosticProvider = new UnsafeInputAssignmentDiagnosticProvider();

    @Test
    public void testNoIssues() throws IOException {
        var model = getModel("src/test/resources/Diagnostic/unsafeinput1.yaml");
        var diagnostics = diagnosticProvider.diagnose(model.value());
        assert diagnostics.isEmpty();
    }

    @Test
    public void testSingleIssue() throws IOException {
        var model = getModel("src/test/resources/Diagnostic/unsafeinput2.yaml");
        var diagnostics = diagnosticProvider.diagnose(model.value());
        assert diagnostics.stream().anyMatch(diagnostic -> diagnostic.getCode().getLeft().equals(DiagnosticBuilderService.DiagnosticType.WorkflowRun.toString()));
    }
}

