package diagnostics;

import org.junit.jupiter.api.Test;
import org.server.diagnostic.DiagnosticBuilderService;
import org.server.diagnostic.OutdatedReferenceDiagnosticProvider;

import java.io.IOException;

import static diagnostics.TestUtils.getModel;

class OutdatedReferenceTest {

    private static final OutdatedReferenceDiagnosticProvider diagnosticProvider = new OutdatedReferenceDiagnosticProvider();

    @Test
    void testNoReference() throws IOException {
        var model = getModel("src/test/resources/Diagnostic/outdatedreference1.yaml");
        var diagnostics = diagnosticProvider.diagnose(model.value());
        assert diagnostics.isEmpty();
    }

    @Test
    void testUpToDateReference() throws IOException {
        var model = getModel("src/test/resources/Diagnostic/outdatedreference2.yaml");
        var diagnostics = diagnosticProvider.diagnose(model.value());
        assert diagnostics.isEmpty();
    }

    @Test
    void testSingleIssue() throws IOException {
        var model = getModel("src/test/resources/Diagnostic/outdatedreference3.yaml");
        var diagnostics = diagnosticProvider.diagnose(model.value());
        assert diagnostics.stream().anyMatch(diagnostic -> diagnostic.getCode().getLeft().equals(DiagnosticBuilderService.DiagnosticType.OUTDATED_REFERENCE.toString()));

    }
}
