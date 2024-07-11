package codesquad;

import codesquad.config.BeanFactory;
import codesquad.handler.AnnotationHandlerMapping;
import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.LinkedBlockingQueue;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.concurrent.TimeUnit;

public final class Server {

    private static final ExecutorService EXECUTOR_SERVICE = new ThreadPoolExecutor(10, 200, 60, TimeUnit.SECONDS,
            new LinkedBlockingQueue<>(50));

    public static void run(ServerSocket serverSocket) throws IOException {
        // 컨텍스트를 초기화합니다.
        BeanFactory beanFactory = new BeanFactory();
        beanFactory.start();

        // 핸들러 매퍼를 초기화합니다.
        AnnotationHandlerMapping annotationHandlerMapping = new AnnotationHandlerMapping();
        annotationHandlerMapping.init(beanFactory);

        while (true) {
            Socket clientSocket = serverSocket.accept();
            EXECUTOR_SERVICE.execute(new ClientRequest(clientSocket, annotationHandlerMapping));
        }
    }

    private Server() {
    }
}
