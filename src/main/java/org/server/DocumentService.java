package org.server;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.eclipse.lsp4j.*;
import org.eclipse.lsp4j.services.TextDocumentService;
import org.server.document.DocumentModel;

import java.util.concurrent.CompletableFuture;

public class DocumentService implements TextDocumentService {

    private final LanguageServer LanguageServer;
    Logger logger = LogManager.getLogger();


    public DocumentService(LanguageServer LanguageServer) {
        this.LanguageServer = LanguageServer;
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
        //TODO something with the doc should happen here
        DocumentModel model = new DocumentModel();
    }

    @Override
    public void didChange(DidChangeTextDocumentParams didChangeTextDocumentParams) {
        logger.info("didChange");
        //TODO something with the doc should happen here
    }

    @Override
    public void didClose(DidCloseTextDocumentParams didCloseTextDocumentParams) {

    }

    @Override
    public void didSave(DidSaveTextDocumentParams didSaveTextDocumentParams) {

    }
}
