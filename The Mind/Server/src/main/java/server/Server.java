package server;

import model.Account;

import java.io.IOException;
import java.io.PrintWriter;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.Scanner;

public class Server {
    private final ServerSocket serverSocket;
    public static Server instance;

    public Server(int port) throws IOException {
        serverSocket = new ServerSocket(port);
        instance = this;
    }

    public void acceptRequests() throws IOException {
        System.out.println("Waiting for connections...");
        while (true){
            Socket socket = serverSocket.accept();
            System.out.println("Someone connected...");
            Scanner scanner = new Scanner(socket.getInputStream());
            PrintWriter writer = new PrintWriter(socket.getOutputStream(), true);
            ClientHandler clientHandler = new ClientHandler(writer,scanner);
            clientHandler.start();
        }
    }

    public Account login(String username, ClientHandler clientHandler) {
        return Account.createNewSession(username,clientHandler);
    }
}
