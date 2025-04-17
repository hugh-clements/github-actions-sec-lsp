package diagnostics;

import org.junit.jupiter.api.Test;
import org.server.diagnostic.DiagnosticBuilderService;
import org.server.diagnostic.UnsafeInputAssignmentDiagnosticProvider;

import java.io.IOException;

import static diagnostics.UtilsTest.getModel;

class UnsafeInputTest {

    private static final UnsafeInputAssignmentDiagnosticProvider diagnosticProvider = new UnsafeInputAssignmentDiagnosticProvider();

    @Test
    void testNoIssues() throws IOException {
        var model = getModel("src/test/resources/Diagnostic/unsafeinput1.yaml");
        var diagnostics = diagnosticProvider.diagnose(model.value());
        System.err.println(diagnostics);
        assert diagnostics.isEmpty();
    }

    @Test
    void testSingleIssueSimple() throws IOException {
        var model = getModel("src/test/resources/Diagnostic/unsafeinput2.yaml");
        var diagnostics = diagnosticProvider.diagnose(model.value());
        System.err.println(diagnostics);
        assert diagnostics.stream().anyMatch(diagnostic -> diagnostic.getCode().getLeft().equals(DiagnosticBuilderService.DiagnosticType.WORKFLOW_RUN.toString()));
    }

    @Test
    void testSingleIssueComplexe() throws IOException {
        var model = getModel("src/test/resources/Diagnostic/unsafeinput3.yaml");
        var diagnostics = diagnosticProvider.diagnose(model.value());
        System.err.println(diagnostics);
        assert diagnostics.stream().anyMatch(diagnostic -> diagnostic.getCode().getLeft().equals(DiagnosticBuilderService.DiagnosticType.WORKFLOW_RUN.toString()));
    }

    @Test
    void testMultipleIssue() throws IOException {
        var model = getModel("src/test/resources/Diagnostic/unsafeinput4.yaml");
        var diagnostics = diagnosticProvider.diagnose(model.value());
        System.err.println(diagnostics);
        assert diagnostics.stream().allMatch(diagnostic -> diagnostic.getCode().getLeft().equals(DiagnosticBuilderService.DiagnosticType.WORKFLOW_RUN.toString()));
    }
}

