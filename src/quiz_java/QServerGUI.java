package quiz_java;

import javax.swing.*;
import java.awt.*;
import java.io.*;
import java.net.*;
import java.util.List;
import java.util.*;


public class QServerGUI {
    private static final int PORT = 5678;
    private static final String[] QUESTIONS = {
    	    "What does CSS stand for?",
    	    "Which data structure operates in a Last In, First Out (LIFO) manner?",
    	    "What is the main purpose of a constructor in Java?",
    	    "What does SQL stand for?",
    	    "Which of the following is not a valid Java keyword?"
    	};

    	private static final String[][] CHOICES = {
    	    {"Cascading Style Sheets", "Computer Style Sheets", "Creative Style Sheets", "Coded Style Sheets"},
    	    {"Stack", "Queue", "Linked List", "Tree"},
    	    {"To initialize an object", "To perform mathematical calculations", "To declare variables", "To handle exceptions"},
    	    {"Structured Query Language", "Sequential Query Language", "Simple Query Language", "Static Query Language"},
    	    {"alloc", "const", "new", "switch"}
    	};

    	private static final String[] ANSWERS = {
    	    "Cascading Style Sheets",
    	    "Stack",
    	    "To initialize an object",
    	    "Structured Query Language",
    	    "alloc"
    	};

    private static int clientCounter = 0;
    private JFrame frame;
    private JPanel clientPanel; // Panel to send client text areas
    private Map<Integer, JTextArea> clientTextAreas; // Map to track text areas
    private JTextArea serverTextArea;

    public QServerGUI() {
        frame = new JFrame("Question Server");
        clientPanel = new JPanel();
        clientPanel.setLayout(new BoxLayout(clientPanel, BoxLayout.Y_AXIS));
        JScrollPane scrollPane = new JScrollPane(clientPanel);
        serverTextArea = new JTextArea(6, 50);
        serverTextArea.setEditable(false);
        serverTextArea.setBorder(BorderFactory.createTitledBorder("Server"));
        frame.getContentPane().add(new JScrollPane(serverTextArea), BorderLayout.NORTH);
        frame.getContentPane().add(scrollPane, BorderLayout.CENTER);
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        frame.setPreferredSize(new Dimension(600, 800)); // Set preferred size for the frame
        frame.pack();
        frame.setVisible(true);
        clientTextAreas = new HashMap<>();
    }

    public void startServer() {
        SwingWorker<Void, String> worker = new SwingWorker<Void, String>() {
            @Override
            protected Void doInBackground() throws Exception {
                try (ServerSocket serverSocket = new ServerSocket(PORT)) {
                    publish("Server started. Waiting for clients...\n");
                    while (true) { // Keep listening for new clients
                        Socket clientSocket = serverSocket.accept(); // Accept client
                        new Thread(new ClientHandler(clientSocket)).start(); // Handle the client in a new thread
                    }
                } catch (IOException e) {
                    e.printStackTrace();
                }
                return null;
            }

            @Override
            protected void process(List<String> chunks) {
            	for (String text : chunks) {
                    serverTextArea.append(text);
                }
            }
        };
        worker.execute();
    }

    // Inner class to handle each client connection
    private class ClientHandler implements Runnable {
        private Socket clientSocket;
        private int clientId;
        private JTextArea clientTextArea;
        private int totalQuestions = ANSWERS.length;
        private int score = 0;

        public ClientHandler(Socket clientSocket) {
            this.clientSocket = clientSocket;
            this.clientId = ++clientCounter;
            // Create a text area for this client
            clientTextArea = new JTextArea(6, 40);
            clientTextArea.setEditable(false);
            clientTextArea.setBorder(BorderFactory.createTitledBorder("Client " + clientId));
            // Add the text area to the client panel
            SwingUtilities.invokeLater(() -> {
                clientPanel.add(clientTextArea);
                clientPanel.revalidate();
            });
            // Store the text area in the map
            clientTextAreas.put(clientId, clientTextArea);
        }

        @Override
        public void run() {
        	
            try (PrintWriter out = new PrintWriter(clientSocket.getOutputStream(), true);
                 BufferedReader in = new BufferedReader(new InputStreamReader(clientSocket.getInputStream()))) {

                publish("Client connected.\n");

                for (int i = 0; i < QUESTIONS.length; i++) {
                    out.println(QUESTIONS[i]); // Send question to client
                    out.println(String.join(",", CHOICES[i])); // Send choices to client
                    String answer = in.readLine(); // Read answer from client
                    publish("Client answered: " + answer + "\n");
                    if (ANSWERS[i].equalsIgnoreCase(answer)) {
                        out.println("Correct");
                        score++; // Increment the score if the answer is correct
                        publish("The answer is correct.");
                    } else {
                        out.println("Incorrect");
                        publish("The answer is incorrect.");
                    }
                }

                out.println("END"); // Signal the end of the quiz
                out.println(score + "/" + totalQuestions);
                publish("Total correct answers: " + score);
                clientSocket.close(); // Close the client socket
            } catch (IOException e) {
                e.printStackTrace();
            }
        }

        // Helper method to publish updates to the SwingWorker
        private void publish(String text) {
            String message = text + "\n";
            SwingUtilities.invokeLater(() -> clientTextArea.append(message));
        }
    }

    public static void main(String[] args) {
        SwingUtilities.invokeLater(() -> {			//EDT
            QServerGUI server = new QServerGUI();
            server.startServer();
        });
    }
}

