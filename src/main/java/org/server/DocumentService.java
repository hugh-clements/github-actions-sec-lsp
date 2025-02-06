package org.server;

import org.eclipse.lsp4j.*;
import org.eclipse.lsp4j.services.TextDocumentService;

import java.util.Collections;
import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.CompletableFuture;

public class DocumentService implements TextDocumentService {

    private final Map<String, DocumentModel> docs = Collections.synchronizedMap(new HashMap<>());
    private final LanguageServer LanguageServer;

    public DocumentService(LanguageServer LanguageServer) {
        this.LanguageServer = LanguageServer;
    }

    @Override
    public CompletableFuture<Hover> hover(HoverParams params) {
        return TextDocumentService.super.hover(params);
    }

    @Override
    public CompletableFuture<DocumentDiagnosticReport> diagnostic(DocumentDiagnosticParams params) {
        return TextDocumentService.super.diagnostic(params);
    }

    @Override
    public void didOpen(DidOpenTextDocumentParams didOpenTextDocumentParams) {
        DocumentModel model = new DocumentModel(didOpenTextDocumentParams.getTextDocument().getText());
        this.docs.put(didOpenTextDocumentParams.getTextDocument().getUri(),
                model);
    }

    @Override
    public void didChange(DidChangeTextDocumentParams didChangeTextDocumentParams) {

    }

    @Override
    public void didClose(DidCloseTextDocumentParams didCloseTextDocumentParams) {

    }

    @Override
    public void didSave(DidSaveTextDocumentParams didSaveTextDocumentParams) {

    }
}
