package org.server;
import lombok.Getter;
import lombok.Setter;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.eclipse.lsp4j.*;
import org.eclipse.lsp4j.services.LanguageClient;
import org.eclipse.lsp4j.services.LanguageClientAware;
import org.eclipse.lsp4j.services.TextDocumentService;
import org.eclipse.lsp4j.services.WorkspaceService;

import java.util.concurrent.CompletableFuture;

/**
 * Language Server implementation class that is started by Start
 */
public class ActionsLanguageServer  implements org.eclipse.lsp4j.services.LanguageServer, LanguageClientAware {

    Logger logger = LogManager.getLogger(getClass());
    private final ActionsDocumentService actionsDocumentService;
    private final WorkspaceService workspaceService;
    @Setter @Getter
    private static LanguageClient client;

    public ActionsLanguageServer() {
        actionsDocumentService = new ActionsDocumentService();
        workspaceService = new ActionsWorkspaceService();
    }

    @Override
    public CompletableFuture<InitializeResult> initialize(InitializeParams initializeParams) {
        logger.info("Initializing LanguageServer");
        final InitializeResult capabilities = new InitializeResult(new ServerCapabilities());
        TextDocumentSyncOptions syncOptions = new TextDocumentSyncOptions();
        syncOptions.setChange(TextDocumentSyncKind.Full); //Syncing the whole file rather than the changes
        syncOptions.setOpenClose(true);
        capabilities.getCapabilities().setTextDocumentSync(syncOptions);
        capabilities.getCapabilities().setHoverProvider(false); //Line required for VSCode compatability
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
        return this.actionsDocumentService;
    }

    @Override
    public WorkspaceService getWorkspaceService() {
        return this.workspaceService;
    }

    @Override
    public void connect(LanguageClient languageClient) {
        setClient(languageClient);
    }

}
