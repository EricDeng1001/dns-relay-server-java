package dns;

import java.io.IOException;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.nio.file.Paths;
import java.util.concurrent.*;

public class Server implements Runnable {
    private final ExecutorService threadPool = Executors.newFixedThreadPool(Runtime.getRuntime().availableProcessors());
    private final DatagramSocket serverSocket;

    Server(String dnsFilePathString, int port) throws Error {
        try {
            ARecordRepo.readCSV(Paths.get(dnsFilePathString));
            serverSocket = new DatagramSocket(port);
            serverSocket.setSoTimeout(0);
        } catch (IOException e) {
            e.printStackTrace();
            throw new Error();
        }
    }

    public void run() {
        byte[] buffer;
        DatagramPacket packet;

        while (true) {
            try {
                // prepare packet's buffer
                buffer = new byte[512];
                packet = new DatagramPacket(buffer, buffer.length);

                serverSocket.receive(packet);

                RequestHandler requestHandler = new RequestHandler(serverSocket, packet);
                threadPool.execute(requestHandler);
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }



}
