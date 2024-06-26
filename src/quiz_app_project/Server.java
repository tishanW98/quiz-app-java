package quiz_app_project;

import javax.swing.*;
import java.awt.*;
import java.io.*;
import java.net.*;
import java.util.Arrays;
import java.util.List;
import java.util.Map;
import quiz_app_project.Question;

public class Server {
    public static void main(String[] args) throws IOException {
        JFrame frame = new JFrame("Server");
        JTextArea textArea = new JTextArea();
        frame.add(new JScrollPane(textArea));
        frame.setSize(500, 500);
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        frame.setVisible(true);
        int port = 1234;
        ServerSocket serverSocket = null;
        
        List<Question> questions = Arrays.asList(
        		new Question("What is the capital of France?", Map.of("a", "London", "b", "Berlin", "c", "Paris", "d", "Rome"), "c"),
        		new Question("Who painted the Mona Lisa?", Map.of("a", "Leonardo da Vinci", "b", "Vincent van Gogh", "c", "Pablo Picasso", "d", "Michelangelo"), "a"),
        		new Question("What is the chemical symbol for water?", Map.of("a", "H2O", "b", "CO2", "c", "NaCl", "d", "O2"), "a"),
        		new Question("What is the largest planet in our solar system?", Map.of("a", "Earth", "b", "Venus", "c", "Jupiter", "d", "Saturn"), "c"),
        		new Question("Who wrote 'Romeo and Juliet'?", Map.of("a", "William Shakespeare", "b", "Jane Austen", "c", "Charles Dickens", "d", "Mark Twain"), "a"),
        		new Question("What is the capital of Japan?", Map.of("a", "Seoul", "b", "Tokyo", "c", "Beijing", "d", "Bangkok"), "b"),
        		new Question("What is the tallest mammal in the world?", Map.of("a", "Elephant", "b", "Giraffe", "c", "Hippopotamus", "d", "Rhino"), "b"),
        		new Question("What is the chemical symbol for gold?", Map.of("a", "Gd", "b", "Au", "c", "Ag", "d", "Pt"), "b"),
        		new Question("Which planet is known as the 'Red Planet'?", Map.of("a", "Jupiter", "b", "Venus", "c", "Mars", "d", "Mercury"), "c"),
        		new Question("Who was the first person to step on the moon?", Map.of("a", "Neil Armstrong", "b", "Buzz Aldrin", "c", "Yuri Gagarin", "d", "John Glenn"), "a"));


        try {
            serverSocket = new ServerSocket(port);
            textArea.append("Server started on port " + port + "\n");
            while (true) {
                Socket socket = serverSocket.accept();
                textArea.append("Client connected\n");
                new ClientHandler(socket, questions, textArea).start();
            }
        } catch (IOException e) {
            e.printStackTrace();
        } finally {
            if (serverSocket != null && !serverSocket.isClosed()) {
                try {
                    serverSocket.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }


        while (true) {
            Socket socket = serverSocket.accept();
            textArea.append("Client connected\n");
            new ClientHandler(socket, questions, textArea).start();
        }
    }
}

class ClientHandler extends Thread {
	final DataInputStream dataInputStream;
    final DataOutputStream dataOutputStream;
    final Socket socket;
    final List<Question> questions;
    private JTextArea textArea;

    public ClientHandler(Socket socket, List<Question> questions, JTextArea textArea) throws IOException {
        this.socket = socket;
        this.questions = questions;
        this.dataInputStream = new DataInputStream(socket.getInputStream());
        this.dataOutputStream = new DataOutputStream(socket.getOutputStream());
        this.textArea = textArea;
    }

    public void run() {
        try {
            int score = 0;
            for (Question question : questions) {
                dataOutputStream.writeUTF(question.question + " " + question.options);
                String answer = dataInputStream.readUTF();
                textArea.append("Client answered: " + answer + "\n");

                if (answer.equals(question.correctAnswer)) {
                    dataOutputStream.writeUTF("Correct answer");
                    score++;
                } else {
                    dataOutputStream.writeUTF("Incorrect answer. The correct answer is " + question.correctAnswer);
                }
            }
            dataOutputStream.writeUTF("Your score is " + score + "/" + questions.size());

            this.socket.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}