package diagnostics;

import org.junit.jupiter.api.Test;
import org.server.diagnostic.ActionReferencingDiagnosticProvider;
import org.server.diagnostic.DiagnosticBuilderService;

import java.io.IOException;

import static diagnostics.UtilsTest.getModel;

public class RepojackableTest {

    private static final ActionReferencingDiagnosticProvider diagnosticProvider = new ActionReferencingDiagnosticProvider();

    @Test
    public void testNoIssues() throws IOException {
        var model = getModel("src/test/resources/Diagnostic/repojackable1.yaml");
        var diagnostics = diagnosticProvider.diagnose(model.value());
        assert diagnostics.isEmpty();
    }

    @Test
    public void testSingleIssue() throws IOException {
        var model = getModel("src/test/resources/Diagnostic/repojackable2.yaml");
        var diagnostics = diagnosticProvider.diagnose(model.value());
        System.err.println(diagnostics);
        assert diagnostics.stream().anyMatch(diagnostic -> diagnostic.getCode().getLeft().equals(DiagnosticBuilderService.DiagnosticType.Repojackable.toString()));
    }

    @Test
    public void testMultipleIssues() throws IOException {
        var model = getModel("src/test/resources/Diagnostic/repojackable3.yaml");
        var diagnostics = diagnosticProvider.diagnose(model.value());
        System.err.println(diagnostics);
        assert diagnostics.stream().anyMatch(diagnostic -> diagnostic.getCode().getLeft().equals(DiagnosticBuilderService.DiagnosticType.Repojackable.toString()));
    }
}
