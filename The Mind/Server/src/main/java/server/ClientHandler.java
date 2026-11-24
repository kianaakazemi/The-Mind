package server;

import model.Account;

import server.Game;

import java.io.PrintWriter;
import java.util.ArrayList;
import java.util.Scanner;

public class ClientHandler extends Thread {
    private static final String SPLITTER = "<SPLITTER>";
    private final Scanner scanner;
    private final PrintWriter writer;
    private Account account;

    public ClientHandler(PrintWriter writer, Scanner scanner) {
        this.scanner = scanner;
        this.writer = writer;
    }

    @Override
    public void run() {
        send("hello");
        while (true) {
            parseCommand(scanner.nextLine());
        }
    }

    private void parseCommand(String command) {
        System.out.println(command);
        String[] args = command.split(SPLITTER);
        switch (args[0]) {
            case "LOGIN": {
                Account account = Server.instance.login(args[1], this);
                this.account = account;
                send("TOKEN", account.getToken());
                break;
            }
            case "GET_ROOMS":
                synchronized (Game.games) {
                    ArrayList<String> gamesInfo = new ArrayList<>();
                    gamesInfo.add("ROOMS");
                    for (Game game : Game.games) {
                        if (game.started)
                            continue;
                        gamesInfo.add(game.getId());
                        gamesInfo.add(game.getHostName());
                        gamesInfo.add(game.getPlayersCount() + "/" + game.getSize());
                    }
                    send(gamesInfo.toArray(new String[0]));
                }
                break;
            case "CREATE_ROOM": {
                Game game = new Game(args[1]);
                send("JOIN", game.getHostName(), game.getId());
                game.sendNewData();
                break;
            }
            case "JOIN": {
                Game game = Game.getGame(args[1]);
                if (game == null) {
                    send("ERROR", "Room Not Found!");
                } else if (game.isFull()) {
                    send("ERROR", "server.Game is full!");
                } else if (game.started) {
                    send("ERROR", "server.Game already started!");
                } else {
                    Account account = Account.find(args[2]);
                    send("JOIN", game.getHostName(), game.getId());
                    game.join(account);
                    game.sendNewData();
                }
                break;
            }
            case "START_GAME": {
                Account account = Account.find(args[1]);
                Game game = account.getGame();
                if (game == null) {
                    send("ERROR", "Room Not Found!");
                } else if (game.getOwner() != account) {
                    send("ERROR", "You are not host!");
                } else {
                    game.start();
                }
                break;
            }
            case "SET_PLAYERS": {
                Account account = Account.find(args[2]);
                Game game = account.getGame();
                if (game == null) {
                    send("ERROR", "Room Not Found!");
                } else if (game.getOwner() != account) {
                    send("ERROR", "You are not host!");
                } else {
                    game.setPlayersNum(Integer.parseInt(args[1]));
                }
                break;
            }
            case "PLAY_CARD": {
                Account account = Account.find(args[2]);
                Game game = account.getGame();
                if (game == null) {
                    send("ERROR", "Room Not Found!");
                } else {
                    game.play(account, Integer.parseInt(args[1]));
                }
                break;
            }
            case "NINJA": {
                Account account = Account.find(args[1]);
                Game game = account.getGame();
                if (game == null) {
                    send("ERROR", "Room Not Found!");
                } else {
                    game.ninja(account);
                }
                break;
            }
        }
    }

    public void send(String... args) {
        String text = "";
        for (int i = 0; i < args.length; i++) {
            text += args[i];
            if (i != args.length - 1)
                text += SPLITTER;
        }
        writer.println(text);
        writer.flush();
    }
}
