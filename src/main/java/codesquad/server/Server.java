package codesquad.server;

import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.LinkedBlockingQueue;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.concurrent.TimeUnit;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class Server {

    private static final Logger log = LoggerFactory.getLogger(Server.class);
    private final ExecutorService EXECUTOR_SERVICE = new ThreadPoolExecutor(10, 200, 60, TimeUnit.SECONDS,
            new LinkedBlockingQueue<>(50));

    public Server() {
    }

    public void start(ServerHandler serverHandler) {
        log.debug("Listening for connection on port 8080 ....");
        try (ServerSocket serverSocket = new ServerSocket(8080)) {
            while (true) {
                Socket clientSocket = serverSocket.accept();
                EXECUTOR_SERVICE.execute(new ClientRequest(clientSocket, serverHandler));
            }
        } catch (IOException e) {
            log.error("입출력 예외가 발생하였습니다.", e);
        }
    }
}
