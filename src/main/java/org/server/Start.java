package org.server;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.eclipse.lsp4j.jsonrpc.Launcher;
import org.eclipse.lsp4j.launch.LSPLauncher;
import org.eclipse.lsp4j.services.LanguageClient;

import java.io.InputStream;
import java.io.OutputStream;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.Future;

public class Start {

    Logger logger = LogManager.getLogger(getClass());

    void main(String[] args) throws ExecutionException, InterruptedException {
        startServer(System.in, System.out);
    }

    private void startServer(InputStream in, OutputStream out) throws ExecutionException, InterruptedException {
        logger.info("Starting server...");
        LanguageServer server = new LanguageServer();
        Launcher<LanguageClient> launcher = LSPLauncher.createServerLauncher(server, in, out);
        LanguageClient client = launcher.getRemoteProxy();
        server.client = client;
        server.connect(client);
        Future<Void> future = launcher.startListening();
        future.get();
    }

}
