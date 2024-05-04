# Quiz App Project

This project is a simple quiz application implemented using Java socket programming. The application consists of a server and a client. The server sends quiz questions to the client, receives the client's answers, and sends back a response indicating whether the answer was correct or not.

## Getting Started

These instructions will get you a copy of the project up and running on your local machine for development and testing purposes.

### Prerequisites

- Java Development Kit (JDK) 9 or later
- An Integrated Development Environment (IDE) like Eclipse or IntelliJ IDEA

### Installing

1. Clone the repository to your local machine.
2. Open the project in your IDE.
3. Make sure that your IDE's project structure and build path are correctly set up to use the correct JDK.

### Running the Application

1. Run the `Server.java` file. This will start the server, which will listen for incoming client connections and handle the quiz logic.
2. Run the `Client.java` file. This will start the client, which will connect to the server and start the quiz.

Remember to start the server before starting the client, as the client needs the server to be up and running to connect to it.

## Built With

- Java - The programming language used
- Java Sockets - Used for communication between the server and client

