package diagnostics;

import org.junit.jupiter.api.Test;
import org.server.diagnostic.CommandExecutionDiagnosticProvider;
import org.server.diagnostic.DiagnosticBuilderService;

import java.io.IOException;

import static diagnostics.TestUtils.getModel;

class CommandExecutionTest {

    private static final CommandExecutionDiagnosticProvider diagnosticProvider = new CommandExecutionDiagnosticProvider();

    @Test
    void testNoIssues() throws IOException {
        var model = getModel("src/test/resources/Diagnostic/commandexecution1.yaml");
        var diagnostics = diagnosticProvider.diagnose(model.value());
        assert diagnostics.isEmpty();
    }

    @Test
    void testSingleInWithStep() throws IOException {
        var model = getModel("src/test/resources/Diagnostic/commandexecution2.yaml");
        var diagnostics = diagnosticProvider.diagnose(model.value());
        assert diagnostics.stream().anyMatch(diagnostic ->
                diagnostic.getCode().getLeft().equals(DiagnosticBuilderService.DiagnosticType.COMMAND_EXECUTION.toString()));
        assert diagnostics.size() == 1;
    }

    @Test
    void testSingleInputs() throws IOException {
        var model = getModel("src/test/resources/Diagnostic/commandexecution3.yaml");
        var diagnostics = diagnosticProvider.diagnose(model.value());
        assert diagnostics.stream().anyMatch(diagnostic ->
                diagnostic.getCode().getLeft().equals(DiagnosticBuilderService.DiagnosticType.COMMAND_EXECUTION.toString())
                        && diagnostic.getRelatedInformation() != null
        );        assert diagnostics.size() == 1;
    }
}
