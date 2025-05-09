package org.server;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.eclipse.lsp4j.*;
import org.eclipse.lsp4j.services.TextDocumentService;
import org.server.diagnostic.DiagnosticService;
import org.server.document.DocumentModel;
import org.server.document.Located;
import org.server.document.ModelConstructorService;

import java.util.concurrent.CompletableFuture;

/**
 * Implementation of TextDocumentService that is responsible for Document sync
 */
public class ActionsDocumentService implements TextDocumentService {

    private Located<DocumentModel> documentModel;
    private final ModelConstructorService modelConstructorService;
    private final DiagnosticService diagnosticService;

    Logger logger = LogManager.getLogger(getClass());

    public ActionsDocumentService() {
        modelConstructorService = new ModelConstructorService();
        diagnosticService = new DiagnosticService();
    }

    @Override
    public void didOpen(DidOpenTextDocumentParams didOpenTextDocumentParams) {
        logger.warn("didOpen");
        TextDocumentItem textDocumentItem = didOpenTextDocumentParams.getTextDocument();
        try {
            this.documentModel = modelConstructorService.modelConstructor(
                    textDocumentItem.getLanguageId(),
                    textDocumentItem.getUri(),
                    textDocumentItem.getText());
        } catch (Exception e) {
            logger.error("Failed to construct document model on open", e);
        }

        CompletableFuture.runAsync(() -> ActionsLanguageServer.getClient().publishDiagnostics(
                new PublishDiagnosticsParams(didOpenTextDocumentParams.getTextDocument().getUri(),
                        diagnosticService.diagnose(documentModel))
        ));
    }

    @Override
    public void didChange(DidChangeTextDocumentParams didChangeTextDocumentParams) {
        logger.warn("didChange");
        String documentChangeString = didChangeTextDocumentParams.getContentChanges().getFirst().getText();
        try {
            this.documentModel = modelConstructorService.modelConstructor(
                    this.documentModel.value().lang()
                    ,this.documentModel.value().documentURI(),
                    documentChangeString);
        } catch (Exception e) {
            logger.error("Failed to construct document model on change", e);
        }

        CompletableFuture.runAsync(() -> ActionsLanguageServer.getClient().publishDiagnostics(
                new PublishDiagnosticsParams(didChangeTextDocumentParams.getTextDocument().getUri(),
                        diagnosticService.diagnose(documentModel))
        ));
    }

    @Override
    public void didClose(DidCloseTextDocumentParams didCloseTextDocumentParams) {
        logger.warn("didClose");
    }

    @Override
    public void didSave(DidSaveTextDocumentParams didSaveTextDocumentParams) {
        logger.warn("didSave");
    }
}
