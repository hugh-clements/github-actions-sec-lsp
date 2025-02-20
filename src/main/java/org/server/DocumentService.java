package org.server;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.eclipse.lsp4j.*;
import org.eclipse.lsp4j.services.TextDocumentService;
import org.server.diagnostic.DiagnosticService;
import org.server.document.DocumentModel;
import org.server.document.ModelConstructorService;

import java.util.concurrent.CompletableFuture;

public class DocumentService implements TextDocumentService {

    private final LanguageServer languageServer;
    Logger logger = LogManager.getLogger();
    private DocumentModel documentModel;
    private ModelConstructorService modelConstructorService;
    private DiagnosticService diagnosticService;


    public DocumentService(LanguageServer languageServer) {
        this.languageServer = languageServer;
        modelConstructorService = new ModelConstructorService();
        diagnosticService = new DiagnosticService();
    }

    @Override
    public CompletableFuture<Hover> hover(HoverParams params) {
        logger.info("hover");
        return TextDocumentService.super.hover(params);
    }

    @Override
    public CompletableFuture<DocumentDiagnosticReport> diagnostic(DocumentDiagnosticParams params) {
        logger.info("diagnostic");
        return TextDocumentService.super.diagnostic(params);
    }

    @Override
    public void didOpen(DidOpenTextDocumentParams didOpenTextDocumentParams) {
        logger.info("didOpen");

        this.documentModel = modelConstructorService.modelConstructor();

        CompletableFuture.runAsync(() -> {
            languageServer.client.publishDiagnostics(
                    new PublishDiagnosticsParams(didOpenTextDocumentParams.getTextDocument().getUri(),
                            diagnosticService.diagnose(documentModel))
            );
        });
    }

    @Override
    public void didChange(DidChangeTextDocumentParams didChangeTextDocumentParams) {
        logger.info("didChange");

        this.documentModel = modelConstructorService.modelConstructor();

        CompletableFuture.runAsync(() -> {
            languageServer.client.publishDiagnostics(
                    new PublishDiagnosticsParams(didChangeTextDocumentParams.getTextDocument().getUri(),
                            diagnosticService.diagnose(documentModel))
            );
        });
    }

    @Override
    public void didClose(DidCloseTextDocumentParams didCloseTextDocumentParams) {

    }

    @Override
    public void didSave(DidSaveTextDocumentParams didSaveTextDocumentParams) {

    }
}
