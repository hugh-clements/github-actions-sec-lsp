package org.server;
import org.eclipse.lsp4j.DiagnosticRegistrationOptions;
import org.eclipse.lsp4j.InitializeParams;
import org.eclipse.lsp4j.InitializeResult;
import org.eclipse.lsp4j.ServerCapabilities;
import org.eclipse.lsp4j.services.LanguageClient;
import org.eclipse.lsp4j.services.LanguageClientAware;
import org.eclipse.lsp4j.services.TextDocumentService;
import org.eclipse.lsp4j.services.WorkspaceService;

import java.util.concurrent.CompletableFuture;

public class LanguageServer  implements org.eclipse.lsp4j.services.LanguageServer, LanguageClientAware {

    private DocumentService documentService;
    private WorkspaceService workspaceService;

    public LanguageServer() {
        documentService = new DocumentService();
        workspaceService = new org.server.WorkspaceService();
    }

    @Override
    public CompletableFuture<InitializeResult> initialize(InitializeParams initializeParams) {
        final InitializeResult capabilities = new InitializeResult(new ServerCapabilities());
        //TODO: ensure all required capabilities are present
        capabilities.getCapabilities().setDiagnosticProvider(new DiagnosticRegistrationOptions());
        capabilities.getCapabilities().setHoverProvider(true);
        return CompletableFuture.supplyAsync(() -> capabilities);
    }

    @Override
    public CompletableFuture<Object> shutdown() {
        return CompletableFuture.supplyAsync(() -> Boolean.TRUE);
    }

    @Override
    public void exit() {

    }

    @Override
    public TextDocumentService getTextDocumentService() {
        return this.documentService;
    }

    @Override
    public WorkspaceService getWorkspaceService() {
        return this.workspaceService;
    }

    @Override
    public void connect(LanguageClient languageClient) {

    }
}
