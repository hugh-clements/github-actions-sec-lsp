package diagnostics;

import org.junit.jupiter.api.Test;
import org.server.diagnostic.DiagnosticBuilderService;
import org.server.diagnostic.DiagnosticService;
import org.server.document.ModelConstructorService;
import java.io.IOException;


import static diagnostics.UtilsTest.getModel;

public class RunnerHijackingTest {

    private static final DiagnosticService diagnosticService = new DiagnosticService();

    @Test
    public void testNoIssue() throws IOException {
        var model = getModel("");
        var diagnostics = diagnosticService.diagnose(model);
        assert diagnostics.isEmpty();
    }


    @Test
    public void testRunnerList() throws IOException {
        var model = getModel("");
        var diagnostics = diagnosticService.diagnose(model);
        assert diagnostics.size() == 1;
        assert diagnostics.getFirst().getCode().toString().equals(DiagnosticBuilderService.DiagnosticType.RunnerHijacker.toString());
    }


    @Test
    public void testRunnerList2() throws IOException {
        var model = getModel("");
        var diagnostics = diagnosticService.diagnose(model);
        assert diagnostics.size() == 2;
    }


    @Test
    public void testRunnerSingle() throws IOException {
        var model = getModel("");
        var diagnostics = diagnosticService.diagnose(model);
        assert diagnostics.size() == 1;
        assert diagnostics.getFirst().getCode().toString().equals(DiagnosticBuilderService.DiagnosticType.RunnerHijacker.toString());
    }
}
