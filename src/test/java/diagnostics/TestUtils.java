package diagnostics;

import org.server.document.DocumentModel;
import org.server.document.Located;
import org.server.document.ModelConstructorService;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;

public class TestUtils {

    /**
     * Method that gets the model from a file
     * @param stringPath path to the file
     * @return DocumentModel with location
     * @throws IOException if the path does not exist
     */
    public static Located<DocumentModel> getModel(String stringPath) throws IOException {
        Path path = Path.of(stringPath);
        if (!Files.exists(path)) {
            throw new IOException("File does not exist: " + stringPath);
        }
        var toParse = Files.readString(path);
        var modelConstructorService = new ModelConstructorService();
        return modelConstructorService.modelConstructor("yaml",".github/workflows/",toParse);
    }
}
