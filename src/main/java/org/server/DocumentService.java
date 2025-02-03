package org.server;

import org.eclipse.lsp4j.*;
import org.eclipse.lsp4j.services.TextDocumentService;

import java.util.concurrent.CompletableFuture;

public class DocumentService implements TextDocumentService {

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
