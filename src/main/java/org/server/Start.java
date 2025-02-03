package org.server;

import org.eclipse.lsp4j.jsonrpc.Launcher;
import org.eclipse.lsp4j.launch.LSPLauncher;
import org.eclipse.lsp4j.services.LanguageClient;

import java.io.InputStream;
import java.io.OutputStream;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.Future;

public class Start {

    void Main(String[] args) throws ExecutionException, InterruptedException {
        startServer(System.in, System.out);
    }

    private void startServer(InputStream in, OutputStream out) throws ExecutionException, InterruptedException {
        LanguageServer server = new LanguageServer();
        Launcher<LanguageClient> launcher = LSPLauncher.createServerLauncher(server, in, out);
        LanguageClient client = launcher.getRemoteProxy();
        server.connect(client);
        Future<Void> future = launcher.startListening();
        future.get();
    }
}
