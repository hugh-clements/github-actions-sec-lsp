package diagnostics;

import org.junit.jupiter.api.Test;
import org.server.diagnostic.CodeInjectionDiagnosticProvider;
import org.server.diagnostic.OutdatedReferenceDiagnosticProvider;

import java.io.IOException;

import static diagnostics.UtilsTest.getModel;

public class CodeInjectionTest {

    private static final CodeInjectionDiagnosticProvider diagnosticProvider = new CodeInjectionDiagnosticProvider();

    @Test
    public void testNoIssues() throws IOException {
        var model = getModel("src/test/resources/Diagnostic/codeinjection1.yaml");
        var diagnostics = diagnosticProvider.diagnose(model.value());
        assert diagnostics.isEmpty();
    }

    @Test
    public void testSingleIssue() throws IOException {
        var model = getModel("src/test/resources/Diagnostic/codeinjection2.yaml");
        var diagnostics = diagnosticProvider.diagnose(model.value());

    }
}
