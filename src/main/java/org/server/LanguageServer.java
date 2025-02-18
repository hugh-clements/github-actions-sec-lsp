package org.server;
import lombok.Getter;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
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

    Logger logger = LogManager.getLogger();
    private final DocumentService documentService;
    private final WorkspaceService workspaceService;

    public LanguageServer() {
        documentService = new DocumentService(this);
        workspaceService = new org.server.WorkspaceService();
    }

    @Override
    public CompletableFuture<InitializeResult> initialize(InitializeParams initializeParams) {
        logger.info("Initializing LanguageServer");
        final InitializeResult capabilities = new InitializeResult(new ServerCapabilities());
        //TODO: ensure all required capabilities are present
        capabilities.getCapabilities().setDiagnosticProvider(new DiagnosticRegistrationOptions());
        capabilities.getCapabilities().setHoverProvider(true);
        return CompletableFuture.supplyAsync(() -> capabilities);
    }

    @Override
    public CompletableFuture<Object> shutdown() {
        logger.info("Shutting down LanguageServer");
        return CompletableFuture.supplyAsync(() -> Boolean.TRUE);
    }

    @Override
    public void exit() {
        logger.info("Exiting LanguageServer");
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
        logger.info("Connecting to LanguageClient");
    }
}
