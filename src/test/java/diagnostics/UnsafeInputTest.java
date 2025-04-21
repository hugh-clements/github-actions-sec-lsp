package diagnostics;

import org.junit.jupiter.api.Test;
import org.server.diagnostic.DiagnosticBuilderService;
import org.server.diagnostic.UnsafeInputAssignmentDiagnosticProvider;

import java.io.IOException;

import static diagnostics.TestUtils.getModel;

class UnsafeInputTest {

    private static final UnsafeInputAssignmentDiagnosticProvider diagnosticProvider = new UnsafeInputAssignmentDiagnosticProvider();

    @Test
    void testNoIssues() throws IOException {
        var model = getModel("src/test/resources/Diagnostic/unsafeinput1.yaml");
        var diagnostics = diagnosticProvider.diagnose(model.value());
        assert diagnostics.isEmpty();
    }

    @Test
    void testSafeInput() throws IOException {
        var model = getModel("src/test/resources/Diagnostic/unsafeinput3.yaml");
        var diagnostics = diagnosticProvider.diagnose(model.value());
        assert diagnostics.isEmpty();
    }

    @Test
    void testSingleIssueSimple() throws IOException {
        var model = getModel("src/test/resources/Diagnostic/unsafeinput2.yaml");
        var diagnostics = diagnosticProvider.diagnose(model.value());
        assert diagnostics.stream().anyMatch(diagnostic -> diagnostic.getCode().getLeft().equals(DiagnosticBuilderService.DiagnosticType.UNSAFE_INPUT_ASSIGNMENT.toString()));
    }


    @Test
    void testMultipleIssue() throws IOException {
        var model = getModel("src/test/resources/Diagnostic/unsafeinput4.yaml");
        var diagnostics = diagnosticProvider.diagnose(model.value());
        assert diagnostics.stream().allMatch(diagnostic -> diagnostic.getCode().getLeft().equals(DiagnosticBuilderService.DiagnosticType.UNSAFE_INPUT_ASSIGNMENT.toString()));
        assert diagnostics.size() == 5;
    }

    @Test
    void testInputsStar() throws IOException {
        var model = getModel("src/test/resources/Diagnostic/unsafeinput5.yaml");
        var diagnostics = diagnosticProvider.diagnose(model.value());
        assert diagnostics.stream().allMatch(diagnostic -> diagnostic.getCode().getLeft().equals(DiagnosticBuilderService.DiagnosticType.UNSAFE_INPUT_ASSIGNMENT.toString()));
        assert diagnostics.size() == 1;
    }
}
