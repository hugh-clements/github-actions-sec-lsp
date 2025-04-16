package diagnostics;

import org.junit.jupiter.api.Test;
import org.server.diagnostic.OutdatedReferenceDiagnosticProvider;

import java.io.IOException;

import static diagnostics.UtilsTest.getModel;

public class OutdatedReferenceTest {

    private static final OutdatedReferenceDiagnosticProvider diagnosticProvider = new OutdatedReferenceDiagnosticProvider();

    @Test
    public void testNoIssues() throws IOException {
        var model = getModel("src/test/resources/Diagnostic/outdatedreference1.yaml");
        var diagnostics = diagnosticProvider.diagnose(model.value());
        assert diagnostics.isEmpty();
    }

    @Test
    public void testSingleIssue() throws IOException {
        var model = getModel("src/test/resources/Diagnostic/outdatedreference2.yaml");
        var diagnostics = diagnosticProvider.diagnose(model.value());

    }
}
