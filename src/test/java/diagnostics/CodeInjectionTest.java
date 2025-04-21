package diagnostics;

import org.junit.jupiter.api.Test;
import org.server.diagnostic.CodeInjectionDiagnosticProvider;
import org.server.diagnostic.DiagnosticBuilderService;

import java.io.IOException;

import static diagnostics.TestUtils.getModel;

class CodeInjectionTest {

    private static final CodeInjectionDiagnosticProvider diagnosticProvider = new CodeInjectionDiagnosticProvider();

    @Test
    void testNoIssues() throws IOException {
        var model = getModel("src/test/resources/Diagnostic/codeinjection1.yaml");
        var diagnostics = diagnosticProvider.diagnose(model.value());
        assert diagnostics.isEmpty();
    }

    @Test
    void testRegularUnsafe() throws IOException {
        var model = getModel("src/test/resources/Diagnostic/codeinjection2.yaml");
        var diagnostics = diagnosticProvider.diagnose(model.value());
        assert diagnostics.isEmpty();
    }

    @Test
    void testInputs() throws IOException {
        var model = getModel("src/test/resources/Diagnostic/codeinjection3.yaml");
        var diagnostics = diagnosticProvider.diagnose(model.value());
        assert diagnostics.stream().anyMatch(diagnostic ->
                diagnostic.getCode().getLeft().equals(DiagnosticBuilderService.DiagnosticType.CODE_INJECTION.toString())
                && diagnostic.getRelatedInformation() != null
        );

    }

    @Test
    void testMultipleInWith() throws IOException {
        var model = getModel("src/test/resources/Diagnostic/codeinjection4.yaml");
        var diagnostics = diagnosticProvider.diagnose(model.value());
        assert diagnostics.stream().allMatch(diagnostic -> diagnostic.getCode().getLeft().equals(DiagnosticBuilderService.DiagnosticType.CODE_INJECTION.toString()));
        assert diagnostics.size() == 1;
    }

    @Test
    void testMultiple() throws IOException {
        var model = getModel("src/test/resources/Diagnostic/codeinjection5.yaml");
        var diagnostics = diagnosticProvider.diagnose(model.value());
        assert diagnostics.stream().allMatch(diagnostic -> diagnostic.getCode().getLeft().equals(DiagnosticBuilderService.DiagnosticType.CODE_INJECTION.toString()));
        assert diagnostics.size() == 2;
    }

    @Test
    void testWorkflowDispatchInput() throws IOException {
        var model = getModel("src/test/resources/Diagnostic/codeinjection6.yaml");
        var diagnostics = diagnosticProvider.diagnose(model.value());
        assert diagnostics.stream().anyMatch(diagnostic ->
                diagnostic.getCode().getLeft().equals(DiagnosticBuilderService.DiagnosticType.CODE_INJECTION.toString())
                        && diagnostic.getRelatedInformation() != null
        );        assert diagnostics.size() == 2;
    }

}
