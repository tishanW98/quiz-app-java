package quiz_app_project;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.*;
import java.net.Socket;

public class ClientGUI {
    private JFrame frame;
    private JTextArea questionArea;
    private JTextField answerField;
    private JButton submitButton;
    private DataInputStream dataInputStream;
    private DataOutputStream dataOutputStream;

    public ClientGUI(String address, int port) throws IOException {
        initializeNetwork(address, port);
        initializeGUI();
    }

    private void initializeNetwork(String address, int port) throws IOException {
        Socket socket = new Socket(address, port);
        dataInputStream = new DataInputStream(socket.getInputStream());
        dataOutputStream = new DataOutputStream(socket.getOutputStream());
    }

    private void initializeGUI() {
        frame = new JFrame("Quiz Client");
        questionArea = new JTextArea(5, 20);
        questionArea.setEditable(false);
        JScrollPane scrollPane = new JScrollPane(questionArea);
        answerField = new JTextField(20);
        submitButton = new JButton("Submit Answer");

        submitButton.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                try {
                    String answer = answerField.getText();
                    dataOutputStream.writeUTF(answer);
                    answerField.setText("");

                    String response = dataInputStream.readUTF();
                    questionArea.setText(response);

                    if (response.startsWith("Your score is")) {
                        submitButton.setEnabled(false);
                        answerField.setEditable(false);
                    }
                } catch (IOException ex) {
                    ex.printStackTrace();
                }
            }
        });

        JPanel panel = new JPanel();
        panel.add(scrollPane);
        panel.add(answerField);
        panel.add(submitButton);

        frame.setContentPane(panel);
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        frame.pack();
        frame.setVisible(true);

        // Start the quiz by reading the first question
        readNextQuestion();
    }

    private void readNextQuestion() {
        try {
            String question = dataInputStream.readUTF();
            questionArea.setText(question);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public static void main(String[] args) {
        SwingUtilities.invokeLater(new Runnable() {
            public void run() {
                try {
                    new ClientGUI("localhost", 5000);
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        });
    }
}
