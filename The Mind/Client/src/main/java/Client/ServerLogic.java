package Client;

import GUI.GameFrame;
import GUI.MainFrame;
import GUI.RoomFrame;

import javax.swing.*;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.Socket;

public class ServerLogic implements Runnable {
    private static final String SPLITTER = "<SPLITTER>";
    private final Socket socket;
    public static ServerLogic instance;
    private final BufferedReader reader;
    private final PrintWriter writer;
    private final Thread thread;
    private String token;
    private RoomFrame roomFrame;

    public static void createServerConnection(String host, int port) throws IOException {
        if (instance == null)
            instance = new ServerLogic(host, port);
    }

    private ServerLogic(String host, int port) throws IOException {
        socket = new Socket(host, port);
        reader = new BufferedReader(new InputStreamReader(socket.getInputStream()));
        writer = new PrintWriter(socket.getOutputStream(), true);
        thread = new Thread(this);
        thread.start();
    }

    public void login(String username) {
        send("LOGIN", username);
    }


    @Override
    public void run() {
        try {
            while (true) {
                String command = reader.readLine();
                parse(command);
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private void parse(String command) {
        System.out.println(command);
        String[] args = command.split(SPLITTER);
        switch (args[0]) {
            case "TOKEN":
                System.out.println("Token: " + args[1]);
                setToken(args[1]);
                break;
            case "ROOMS":
                String[] rooms = new String[args.length - 1];
                System.arraycopy(args, 1, rooms, 0, args.length - 1);
                MainFrame.instance.setRooms(rooms);
                break;
            case "JOIN":
                synchronized (RoomFrame.class) {
                    String host = args[1];
                    MainFrame.instance.setVisible(false);
                    new RoomFrame(host + "'s room!");
                }
                break;
            case "ROOM_MEMBERS":
                synchronized (RoomFrame.class) {
                    String[] members = new String[args.length - 1];
                    System.arraycopy(args, 1, members, 0, args.length - 1);
                    RoomFrame.instance.setMembers(members);
                }
                break;
            case "ERROR":
                JOptionPane.showMessageDialog(null, args[1], "Error!", JOptionPane.ERROR_MESSAGE);
                break;
            case "GAME_DATA":
                RoomFrame.instance.setVisible(false);
                GameFrame.instance.updateData(args);
                break;
            case "MESSAGE":
                GameFrame.instance.setMessages(args[1]);
                break;
        }
    }

    private void setToken(String token) {
        this.token = token;
    }

    private void send(String... args) {
        String text = "";
        for (int i = 0; i < args.length; i++) {
            text += args[i];
            if (i != args.length - 1)
                text += SPLITTER;
        }
        writer.println(text);
        writer.flush();
    }

    public void updateRooms() {
        send("GET_ROOMS");
    }

    public void createRoom() {
        send("CREATE_ROOM", token);
    }

    public void join(String id) {
        send("JOIN", id, token);
    }

    public void startGame() {
        send("START_GAME", token);
    }

    public void setPLayersNum(String playersCount) {
        send("SET_PLAYERS", playersCount, token);
    }

    public void playCard(Integer num) {
        send("PLAY_CARD", String.valueOf(num), token);
    }

    public void playNinja() {
        send("NINJA", token);
    }
}
