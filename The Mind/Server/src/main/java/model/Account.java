package model;

import server.ClientHandler;
import server.Game;

import java.security.SecureRandom;
import java.util.Base64;
import java.util.HashMap;

public class Account {
    private static HashMap<String,Account> accounts = new HashMap<>();
    private final ClientHandler clientHandler;
    private String username;
    private String token;
    private Game game;

    private Account(String username, String token, ClientHandler clientHandler) {
        this.username = username;
        this.token = token;
        this.clientHandler = clientHandler;
    }

    public static Account createNewSession(String username, ClientHandler clientHandler){
        String token = generateSafeToken();
        Account account = new Account(username,token,clientHandler);
        accounts.put(token,account);
        return account;
    }

    private static String generateSafeToken() {
        SecureRandom random = new SecureRandom();
        byte[] bytes = new byte[20];
        random.nextBytes(bytes);
        Base64.Encoder encoder = Base64.getUrlEncoder().withoutPadding();
        return encoder.encodeToString(bytes);
    }

    public static Account find(String token) {
        return accounts.get(token);
    }

    public String getName() {
        return username;
    }

    public void setGame(Game game) {
        this.game = game;
    }

    public String getToken() {
        return token;

    }

    public void leaveGame() {
        if(game != null){
            game.leave(this);
        }
        game = null;

    }

    public ClientHandler getClientHandler() {
        return clientHandler;
    }

    public Game getGame() {
        return game;
    }
}
