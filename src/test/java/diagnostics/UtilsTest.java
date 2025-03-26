package diagnostics;

import org.server.document.DocumentModel;
import org.server.document.Located;
import org.server.document.ModelConstructorService;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;

public class UtilsTest {


    public static Located<DocumentModel> getModel(String stringPath) throws IOException {
        var toParse = Files.readString(Path.of(stringPath));
        var modelConstructorService = new ModelConstructorService();
        return modelConstructorService.modelConstructor("yaml","github/workflows/",toParse);
    }
}
