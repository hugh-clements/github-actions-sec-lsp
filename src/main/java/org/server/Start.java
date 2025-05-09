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

/**
 * Class used to start the Language Server
 */
public class Start {

    private static final Logger logger = LogManager.getLogger(Start.class);


    public static void main(String[] args) throws Exception {
        logger.warn("Starting app...");
        try {
            startServer(System.in, System.out);
        } catch (Exception e) {
            logger.error("Failed to start server",e);
        }
    }

    /**
     * Method to initialise and start the language server
     * @param in to listen to messages
     * @param out to output messages
     * @throws ExecutionException exception
     * @throws InterruptedException exception
     */
    public static void startServer(InputStream in, OutputStream out) throws ExecutionException, InterruptedException {
        logger.info("Starting server...");
        ActionsLanguageServer server = new ActionsLanguageServer();
        Launcher<LanguageClient> launcher = LSPLauncher.createServerLauncher(server, in, out);
        LanguageClient client = launcher.getRemoteProxy();
        server.connect(client);
        Future<Void> future = launcher.startListening();
        future.get();
    }

}
