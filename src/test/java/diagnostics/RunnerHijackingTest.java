package diagnostics;

import org.junit.jupiter.api.Test;
import org.server.diagnostic.DiagnosticBuilderService;
import org.server.diagnostic.RunnerHijackingDiagnosticProvider;

import java.io.IOException;

import static diagnostics.TestUtils.getModel;

class RunnerHijackingTest {

    private static final RunnerHijackingDiagnosticProvider diagnosticService = new RunnerHijackingDiagnosticProvider();

    @Test
    void testNoIssue() throws IOException {
        var model = getModel("src/test/resources/Diagnostic/runnerhijacking1.yaml");
        var diagnostics = diagnosticService.diagnose(model.value());
        assert diagnostics.isEmpty();
    }

    @Test
    void testSingleRunner() throws IOException {
        var model = getModel("src/test/resources/Diagnostic/runnerhijacking2.yaml");
        var diagnostics = diagnosticService.diagnose(model.value());
        assert diagnostics.size() == 1;
        assert diagnostics.getFirst().getCode().getLeft().equals(DiagnosticBuilderService.DiagnosticType.RUNNER_HIJACKER.toString());

    }


    @Test
    void testRunnerList() throws IOException {
        var model = getModel("src/test/resources/Diagnostic/runnerhijacking3.yaml");
        var diagnostics = diagnosticService.diagnose(model.value());
        assert diagnostics.size() == 1;
        assert diagnostics.getFirst().getCode().getLeft().equals(DiagnosticBuilderService.DiagnosticType.RUNNER_HIJACKER.toString());
    }


    @Test
    void testRunnerList2() throws IOException {
        var model = getModel("src/test/resources/Diagnostic/runnerhijacking4.yaml");
        var diagnostics = diagnosticService.diagnose(model.value());
        assert diagnostics.size() == 2;
        assert diagnostics.getFirst().getCode().getLeft().equals(DiagnosticBuilderService.DiagnosticType.RUNNER_HIJACKER.toString());

    }
}
