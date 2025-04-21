package org.server;

import org.eclipse.lsp4j.*;
import org.eclipse.lsp4j.jsonrpc.messages.Either;

import java.util.List;
import java.util.concurrent.CompletableFuture;

/**
 * Workspace sync class
 */
public class ActionsWorkspaceService implements org.eclipse.lsp4j.services.WorkspaceService {

    @Override
    public CompletableFuture<Either<List<? extends SymbolInformation>, List<? extends WorkspaceSymbol>>> symbol(WorkspaceSymbolParams params) {
        return null;
    }

    @Override
    public void didChangeConfiguration(DidChangeConfigurationParams didChangeConfigurationParams) {/*This is a required method by the interface that is not used*/}

    @Override
    public void didChangeWorkspaceFolders(DidChangeWorkspaceFoldersParams params) {
        org.eclipse.lsp4j.services.WorkspaceService.super.didChangeWorkspaceFolders(params);
    }


    @Override
    public void didChangeWatchedFiles(DidChangeWatchedFilesParams didChangeWatchedFilesParams) {/*This is a required method by the interface that is not used*/}

}
