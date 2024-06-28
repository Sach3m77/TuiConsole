package pl.projekt.tui.ssh;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.SneakyThrows;
import lombok.extern.slf4j.Slf4j;
import org.apache.sshd.common.keyprovider.FileKeyPairProvider;
import org.apache.sshd.server.SshServer;

import java.io.*;
import java.net.ServerSocket;
import java.net.Socket;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;

/**
 * SSH server implementation using Apache SSHD.
 * This class starts an SSH server and a Telnet server for managing SSH connections.
 */
@Slf4j
public class Server {

    private static final SshServer sshServer = SshServer.setUpDefaultServer();
    private static final ExecutorService executorService = Executors.newFixedThreadPool(2);

    /**
     * Main method to start the SSH and Telnet servers.
     * @param args Command-line arguments (not used)
     */
    public static void main(String[] args) {
        try {
            startServer();
            startTelnetServer(23);
        } catch (IOException e) {
            log.error("Error occurred while starting servers: {}", e.getMessage());
        }
    }

    /**
     * Starts the SSH server with configured settings.
     * Reads credentials from a JSON file and sets up authentication.
     * Starts a background thread to monitor server status.
     * Adds a shutdown hook to gracefully stop the server.
     * @throws IOException If there is an error starting the SSH server or reading files
     */
    private static void startServer() throws IOException {
        final Path keyFilePath = Paths.get("hostkey.ser");
        final Path jsonFilePath = Paths.get("auth.json");
        final Map<String, String> credentials = readCredentialsFromJsonFile(jsonFilePath);

        if (!Files.exists(keyFilePath) || !Files.exists(jsonFilePath)) {
            log.error("Key file or JSON file does not exist.");
            return;
        }

        sshServer.setPort(22);
        sshServer.setKeyPairProvider(new FileKeyPairProvider(keyFilePath));
        sshServer.setPasswordAuthenticator((username, password, session) -> {
            String retrievedPassword = credentials.get(username);
            return retrievedPassword != null && retrievedPassword.equals(password);
        });

        sshServer.setShellFactory(new ClientSSHShellFactory());
        sshServer.start();

        log.info("SSH server was started on port {}", sshServer.getPort());

        executorService.submit(() -> {
            try {
                while (!Thread.currentThread().isInterrupted()) {
                    Thread.sleep(10000);
                }
            } catch (InterruptedException e) {
                log.info("Server has been stopped.");
                Thread.currentThread().interrupt();
            }
        });

        Runtime.getRuntime().addShutdownHook(new Thread(() -> {
            try {
                sshServer.stop();
                executorService.shutdownNow();
                if (!executorService.awaitTermination(5, TimeUnit.SECONDS)) {
                    log.warn("ExecutorService did not terminate in the specified time.");
                }
                log.info("Shutdown complete.");
            } catch (InterruptedException e) {
                log.error("Error during shutdown: {}", e.getMessage());
                Thread.currentThread().interrupt();
            } catch (IOException e) {
                log.error("Error stopping SSH server: {}", e.getMessage());
            }
        }));
    }

    /**
     * Reads credentials (username and password) from a JSON file.
     * @param jsonFilePath Path to the JSON file containing credentials
     * @return Map of usernames to passwords
     */
    @SneakyThrows
    private static Map<String, String> readCredentialsFromJsonFile(final Path jsonFilePath) {
        Map<String, String> credentials = new HashMap<>();
        ObjectMapper objectMapper = new ObjectMapper();
        try {
            JsonNode rootNode = objectMapper.readTree(jsonFilePath.toFile());
            Iterator<Map.Entry<String, JsonNode>> fields = rootNode.fields();
            while (fields.hasNext()) {
                Map.Entry<String, JsonNode> entry = fields.next();
                if (entry.getValue().isTextual()) {
                    credentials.put(entry.getKey(), entry.getValue().asText());
                } else {
                    log.warn("Invalid data format found for key: {}", entry.getKey());
                }
            }
        } catch (IOException e) {
            log.error("An error occurred while reading JSON file: {}", e.getMessage());
        }
        return credentials;
    }

    /**
     * Starts a Telnet server on the specified port.
     * Allows clients to connect and interact via a simple text-based protocol.
     * @param port Port number for the Telnet server
     */
    private static void startTelnetServer(int port) {
        executorService.submit(() -> {
            try (ServerSocket serverSocket = new ServerSocket(port)) {
                log.info("Telnet server started on port {}", port);

                while (!Thread.currentThread().isInterrupted()) {
                    try (Socket clientSocket = serverSocket.accept(); InputStream in = clientSocket.getInputStream(); OutputStream out = clientSocket.getOutputStream()) {

                        ClientHandler telnetServer = new ClientHandler();
                        telnetServer.setInputStream(in);
                        telnetServer.setOutputStream(out);
                        telnetServer.getTuiManager().resizeUI(telnetServer.getScreenWidth(), telnetServer.getScreenHeight());
                        telnetServer.init();
                        telnetServer.startThreads();

                    } catch (IOException e) {
                        log.error("Error handling client connection: {}", e.getMessage());
                    }
                }
            } catch (IOException e) {
                log.error("Could not start Telnet server: {}", e.getMessage());
            }
        });
    }
}
