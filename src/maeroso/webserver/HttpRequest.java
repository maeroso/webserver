/*
    Aluno: Matheus Felipe Oliveira Aeroso
    RA: 1660098
 */

package maeroso.webserver;

import java.io.*;
import java.net.Socket;
import java.net.URI;
import java.util.StringTokenizer;

public class HttpRequest implements Runnable {

    private static final String CRLF = "\r\n";
    private final Socket socket;

    HttpRequest(Socket socket) {
        this.socket = socket;
    }

    @Override
    public void run() {
        try {
            processRequest();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private void processRequest() throws Exception {
        InputStream inputStream = socket.getInputStream();
        BufferedReader bufferedReader = new BufferedReader(new InputStreamReader(inputStream));
        DataOutputStream dataOutputStream = new DataOutputStream(socket.getOutputStream());

        String requestLine = bufferedReader.readLine();
        System.out.println(requestLine);

        String headerLine;

        while ((headerLine = bufferedReader.readLine()) != null && headerLine.length() != 0) {
            System.out.println(headerLine);
        }

        StringTokenizer stringTokenizer = new StringTokenizer(requestLine);
        stringTokenizer.nextToken();
        String fileName = stringTokenizer.nextToken();
        if (fileName.endsWith("/"))
            fileName += "index.html";
        fileName = "." + fileName;

        URI uri = new URI(fileName);
        fileName = uri.getPath();

        FileInputStream fileInputStream = null;
        boolean fileExists = true;

        try {
            fileInputStream = new FileInputStream(fileName);
        } catch (FileNotFoundException e) {
            fileExists = false;
        }

        String statusLine = null;
        String contentTypeLine = null;
        String entityBody = null;



        if (fileExists) {
            statusLine = "HTTP/1.1 200 OK" + CRLF;
            contentTypeLine = "Content-Type: " + contentType(fileName) + CRLF;
        } else {
            statusLine = "HTTP/1.1 404 Not Found" + CRLF;
            contentTypeLine = "Content-Type: text/html" + CRLF;
            entityBody = "<html><head><title>404 Not Found</title></head><body>Not Found</body></html>";
        }


        dataOutputStream.writeBytes(statusLine);
        dataOutputStream.writeBytes(contentTypeLine);
        dataOutputStream.writeBytes(CRLF);

        if (fileExists) {
            byte[] buffer = new byte[1024];
            int bytesCount;

            while ((bytesCount = fileInputStream.read(buffer)) != -1) {
                dataOutputStream.write(buffer, 0, bytesCount);
            }
            fileInputStream.close();
        } else {
            dataOutputStream.writeBytes(entityBody);
        }

        dataOutputStream.close();
        bufferedReader.close();
        inputStream.close();
    }

    private String contentType(String fileName) {
        if (fileName.matches("[\\W\\w]+\\.html?")) return "text/html";
        else if (fileName.matches("[\\W\\w]+\\.css")) return "text/css";
        else if (fileName.matches("[\\W\\w]+\\.js")) return "text/javascript";
        else if (fileName.matches("[\\W\\w]+\\.gif")) return "image/gif";
        else if (fileName.matches("[\\W\\w]+\\.png")) return "image/png";
        else if (fileName.matches("[\\W\\w]+\\.jpe?g")) return "image/jpeg";
        else return "application/octet-stream";
    }
}
