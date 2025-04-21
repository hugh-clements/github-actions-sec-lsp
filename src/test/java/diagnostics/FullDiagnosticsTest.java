package diagnostics;

import org.junit.jupiter.api.Test;
import org.server.diagnostic.DiagnosticBuilderService;
import org.server.diagnostic.DiagnosticService;

import java.io.IOException;

import static diagnostics.TestUtils.getModel;

class FullDiagnosticsTest {

    private static final DiagnosticService diagnosticService = new DiagnosticService();

    @Test
    void testAllDiagnostics() throws IOException {
        var model = getModel("src/test/resources/Diagnostic/fulldiagnostics1.yaml");
        var diagnostics = diagnosticService.diagnose(model);
        System.err.println(diagnostics);
        assert diagnostics.size() == 5;
        assert diagnostics.stream().anyMatch(diagnostic -> diagnostic.getCode().getLeft().equals(DiagnosticBuilderService.DiagnosticType.CODE_INJECTION.toString()));
        assert diagnostics.stream().anyMatch(diagnostic -> diagnostic.getCode().getLeft().equals(DiagnosticBuilderService.DiagnosticType.UNPINNED_ACTION.toString()));
        assert diagnostics.stream().anyMatch(diagnostic -> diagnostic.getCode().getLeft().equals(DiagnosticBuilderService.DiagnosticType.UNSAFE_INPUT_ASSIGNMENT.toString()));
        assert diagnostics.stream().anyMatch(diagnostic -> diagnostic.getCode().getLeft().equals(DiagnosticBuilderService.DiagnosticType.RUNNER_HIJACKER.toString()));
        assert diagnostics.stream().anyMatch(diagnostic -> diagnostic.getCode().getLeft().equals(DiagnosticBuilderService.DiagnosticType.OUTDATED_REFERENCE.toString()));

    }

}
