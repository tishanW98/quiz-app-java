package quiz_app_project;

import java.io.*;
import java.net.*;

public class Client {
    public static void main(String[] args) throws IOException {
        Socket socket = new Socket("localhost", 5000);

        DataInputStream dataInputStream = new DataInputStream(socket.getInputStream());
        DataOutputStream dataOutputStream = new DataOutputStream(socket.getOutputStream());

        BufferedReader bufferedReader = new BufferedReader(new InputStreamReader(System.in));

        String question = "";
        while ((question = dataInputStream.readUTF()) != null) {
            System.out.println(question);
            String answer = bufferedReader.readLine();
            dataOutputStream.writeUTF(answer);

            String response = dataInputStream.readUTF();
            System.out.println(response);
        }

        socket.close();
    }
}