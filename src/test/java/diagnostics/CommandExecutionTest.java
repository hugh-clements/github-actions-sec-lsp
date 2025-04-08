package diagnostics;

import org.junit.jupiter.api.Test;
import org.server.diagnostic.CodeInjectionDiagnosticProvider;
import org.server.diagnostic.CommandExecutionDiagnosticProvider;

import java.io.IOException;

import static diagnostics.UtilsTest.getModel;

public class CommandExecutionTest {

    private static final CommandExecutionDiagnosticProvider diagnosticProvider = new CommandExecutionDiagnosticProvider()#;

    @Test
    public void testNoIssues() throws IOException {
        var model = getModel("src/test/resources/Diagnostic/commandexecution1.yaml");
        var diagnostics = diagnosticProvider.diagnose(model.value());
        assert diagnostics.isEmpty();
    }

    @Test
    public void testSingleIssue() throws IOException {
        var model = getModel("src/test/resources/Diagnostic/commandexecution2.yaml");
        var diagnostics = diagnosticProvider.diagnose(model.value());

    }
}
