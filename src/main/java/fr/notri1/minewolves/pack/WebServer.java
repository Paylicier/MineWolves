package fr.notri1.minewolves.pack;

import com.sun.net.httpserver.HttpExchange;
import com.sun.net.httpserver.HttpHandler;
import com.sun.net.httpserver.HttpServer;

import java.io.IOException;
import java.io.OutputStream;
import java.net.InetSocketAddress;
import java.nio.file.Files;
import java.nio.file.Path;

import static fr.notri1.minewolves.MineWolves.config;

public class WebServer {

    private HttpServer server;

    public void start() throws Exception {
        server = HttpServer.create(new InetSocketAddress(config.getWeb().getPort()), 0);
        server.createContext("/pack.zip", new PackHandler());
        server.setExecutor(null); // creates a default executor
        server.start();
        System.out.println("Web server started on port " + config.getWeb().getPort());
    }

    public void stop() {
        if (server != null) {
            server.stop(0);
        }
    }

    static class PackHandler implements HttpHandler {
        @Override
        public void handle(HttpExchange exchange) throws IOException {
            Path packFile = PackGenerator.getOutputPath();

            if (!Files.exists(packFile)) {
                String msg = "pack.zip not found";
                exchange.sendResponseHeaders(404, msg.length());
                try (OutputStream os = exchange.getResponseBody()) {
                    os.write(msg.getBytes());
                }
                return;
            }

            byte[] data = Files.readAllBytes(packFile);
            exchange.getResponseHeaders().set("Content-Type", "application/zip");
            exchange.sendResponseHeaders(200, data.length);
            try (OutputStream os = exchange.getResponseBody()) {
                os.write(data);
            }
        }
    }
}
