package diagnostics;

import org.junit.jupiter.api.Test;
import org.server.diagnostic.DiagnosticBuilderService;
import org.server.diagnostic.WorkflowRunDiagnosticProvider;

import java.io.IOException;

import static diagnostics.UtilsTest.getModel;

class WorkflowRunTest {

    private static final WorkflowRunDiagnosticProvider diagnosticProvider = new WorkflowRunDiagnosticProvider();

    @Test
    void testNoIssueWorkflowRun() throws IOException {
        var model = getModel("src/test/resources/Diagnostic/workflowrun1.yaml");
        var diagnostics = diagnosticProvider.diagnose(model.value());
        assert diagnostics.isEmpty();
    }

    @Test
    void testNoIssueNoCheckout() throws IOException {
        var model = getModel("src/test/resources/Diagnostic/workflowrun3.yaml");
        var diagnostics = diagnosticProvider.diagnose(model.value());
        assert diagnostics.isEmpty();
    }

    @Test
    void testSingleIssue() throws IOException {
        var model = getModel("src/test/resources/Diagnostic/workflowrun2.yaml");
        var diagnostics = diagnosticProvider.diagnose(model.value());
        assert diagnostics.stream().anyMatch(diagnostic -> diagnostic.getCode().getLeft().equals(DiagnosticBuilderService.DiagnosticType.WORKFLOW_RUN.toString()));
    }
}
