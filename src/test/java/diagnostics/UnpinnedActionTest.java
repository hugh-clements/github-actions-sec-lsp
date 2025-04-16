package diagnostics;

import org.junit.jupiter.api.Test;
import org.server.diagnostic.ActionReferencingDiagnosticProvider;
import org.server.diagnostic.DiagnosticBuilderService;

import java.io.IOException;

import static diagnostics.UtilsTest.getModel;

public class UnpinnedActionTest {

    private static final ActionReferencingDiagnosticProvider diagnosticProvider = new ActionReferencingDiagnosticProvider();

    @Test
    public void testNoIssues() throws IOException {
        var model = getModel("src/test/resources/Diagnostic/unpinnedaction1.yaml");
        var diagnostics = diagnosticProvider.diagnose(model.value());
        assert diagnostics.isEmpty();
    }

    @Test
    public void testSingleIssue() throws IOException {
        var model = getModel("src/test/resources/Diagnostic/unpinnedaction2.yaml");
        var diagnostics = diagnosticProvider.diagnose(model.value());
        System.err.println(diagnostics);
        assert diagnostics.stream().anyMatch(diagnostic -> diagnostic.getCode().getLeft().equals(DiagnosticBuilderService.DiagnosticType.UNPINNED_ACTION.toString()));
    }

    @Test
    public void testMultipleIssues() throws IOException {
        var model = getModel("src/test/resources/Diagnostic/unpinnedaction3.yaml");
        var diagnostics = diagnosticProvider.diagnose(model.value());
        assert diagnostics.stream().anyMatch(diagnostic -> diagnostic.getCode().getLeft().equals(DiagnosticBuilderService.DiagnosticType.UNPINNED_ACTION.toString()));
    }
}
