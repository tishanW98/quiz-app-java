package quiz_app_project;

import javax.swing.*;
import java.awt.*;
import java.io.*;
import java.net.*;

public class Client {
    public static void main(String[] args) throws IOException {
        JFrame frame = new JFrame("Client");
        JTextArea textArea = new JTextArea();
        JTextField textField = new JTextField();
        frame.setLayout(new BorderLayout());
        frame.add(new JScrollPane(textArea), BorderLayout.CENTER);
        frame.add(textField, BorderLayout.SOUTH);
        frame.setSize(500, 500);
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        frame.setVisible(true);

        Socket socket = new Socket("localhost", 5000);

        DataInputStream dataInputStream = new DataInputStream(socket.getInputStream());
        DataOutputStream dataOutputStream = new DataOutputStream(socket.getOutputStream());

        textField.addActionListener(e -> {
            try {
                dataOutputStream.writeUTF(textField.getText());
                textField.setText("");
            } catch (IOException ioException) {
                ioException.printStackTrace();
            }
        });

        String question = "";
        while ((question = dataInputStream.readUTF()) != null) {
            textArea.append(question + "\n");
        }

        socket.close();
    }
}