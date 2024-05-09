package pl.projekt.tui.ssh;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.SneakyThrows;
import lombok.extern.slf4j.Slf4j;
import org.apache.sshd.common.keyprovider.FileKeyPairProvider;
import org.apache.sshd.server.SshServer;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;

@Slf4j
public class ServerSSH {

    private static final SshServer sshServer = SshServer.setUpDefaultServer();

    private static final ExecutorService executorService = Executors.newSingleThreadExecutor();
    private static final Object lock = new Object();

    public static void main(String[] args) {
        try {
            startServer();
        } catch (IOException e) {
            log.error("Error occurred while starting SSH server: {}", e.getMessage());
        }
    }

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

        Runnable task = () -> {
            try {
                synchronized (lock) {
                    while (!Thread.currentThread().isInterrupted()) {
                        lock.wait(10000);
                    }
                }
            } catch (InterruptedException e) {
                log.info("Server has been stopped.");
                Thread.currentThread().interrupt();
            }
        };

        executorService.submit(task);

        boolean executorTerminated = false;
        try {
            executorTerminated = executorService.awaitTermination(Long.MAX_VALUE, TimeUnit.NANOSECONDS);
        } catch (InterruptedException e) {
            log.error("Error occurred: {}", e.getMessage());
            Thread.currentThread().interrupt();
        }

        if (executorTerminated) {
            log.info("ExecutorService has terminated.");
        } else {
            log.info("ExecutorService has not terminated.");
        }

        executorService.shutdown();
        sshServer.stop();
    }

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
}
