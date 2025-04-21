package diagnostics;

import org.junit.jupiter.api.Test;
import org.server.diagnostic.ActionReferencingDiagnosticProvider;
import org.server.diagnostic.DiagnosticBuilderService;

import java.io.IOException;

import static diagnostics.TestUtils.getModel;

class RepojackableTest {

    private static final ActionReferencingDiagnosticProvider diagnosticProvider = new ActionReferencingDiagnosticProvider();

    @Test
    void testNoIssues() throws IOException {
        var model = getModel("src/test/resources/Diagnostic/repojackable1.yaml");
        var diagnostics = diagnosticProvider.diagnose(model.value());
        assert diagnostics.isEmpty();
    }

    @Test
    void testRepoDoesntExist() throws IOException {
        var model = getModel("src/test/resources/Diagnostic/repojackable2.yaml");
        var diagnostics = diagnosticProvider.diagnose(model.value());
        assert diagnostics.stream().anyMatch(diagnostic -> diagnostic.getCode().getLeft().equals(DiagnosticBuilderService.DiagnosticType.REPOJACKABLE.toString()));
    }

    @Test
    void testRedirect() throws IOException {
        var model = getModel("src/test/resources/Diagnostic/repojackable3.yaml");
        var diagnostics = diagnosticProvider.diagnose(model.value());
        assert diagnostics.stream().anyMatch(diagnostic -> diagnostic.getCode().getLeft().equals(DiagnosticBuilderService.DiagnosticType.REPOJACKABLE.toString()));
    }

}
