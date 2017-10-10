/*
    Aluno: Matheus Felipe Oliveira Aeroso
    RA: 1660098
 */

package maeroso.webserver;

import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;

public class WebServer {

    public static void main(String[] args) {
        // write your code here
        int port = 8080;

        ServerSocket serverSocket;

        try {
            serverSocket = new ServerSocket(port);

            System.out.println("\u001B[1mListening for requests on port \u001B[32m" + port + "\u001B[0m\u001B[1m...");
            while (true) {
                Socket clientSocket = serverSocket.accept();

                HttpRequest request = new HttpRequest(clientSocket);

                Thread thread = new Thread(request);

                thread.start();
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
