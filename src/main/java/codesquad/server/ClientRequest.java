package codesquad.server;

import codesquad.server.message.HttpRequest;
import codesquad.server.message.HttpResponse;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.net.Socket;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class ClientRequest implements Runnable {

    private static final Logger log = LoggerFactory.getLogger(ClientRequest.class);

    private final Socket clientSocket;
    private final ServerHandler serverHandler;

    public ClientRequest(Socket clientSocket, ServerHandler serverHandler) {
        this.clientSocket = clientSocket;
        this.serverHandler = serverHandler;
    }

    @Override
    public void run() {
        try (
                InputStream inputStream = clientSocket.getInputStream();
                OutputStream clientOutput = clientSocket.getOutputStream()
        ) {
            process(inputStream, clientOutput);
        } catch (IOException e) {
            log.error("입출력 예외가 발생했습니다.", e);
        }
    }

    private void process(InputStream clientInput, OutputStream clientOutput) throws IOException {
        log.debug("Client connected");

        BufferedReader br = new BufferedReader(new InputStreamReader(clientInput));
        HttpRequest httpRequest = HttpRequest.parse(br);
        log.debug("HTTP 요청 메시지 해석={}", httpRequest);

        HttpResponse httpResponse = serverHandler.handle(httpRequest);
        httpResponse.write(clientOutput);
        clientOutput.flush();
        log.debug("HTTP 응답 메시지 출력={}", httpRequest);

        clientSocket.close();
        log.debug("Client disconnected");
    }
}
