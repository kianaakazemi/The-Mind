package server;

import model.Account;

import java.util.*;

public class Game implements Runnable {
    private static int idCounter;
    public static final ArrayList<Game> games = new ArrayList<>();
    private String id;
    private Account host;
    private final ArrayList<Account> accounts;
    private final HashMap<Account, ArrayList<Integer>> cards;
    private final ArrayList<Integer> computerCards = new ArrayList<>();
    private int ninja = 2;
    private int level = 1;
    private int size = 8;
    boolean started = false;
    boolean end = false;
    int hearts;
    int lastPlayedCard = -1;

    public Game(String token) {
        host = Account.find(token);
        id = String.valueOf(idCounter++);
        accounts = new ArrayList<>();
        accounts.add(host);
        host.setGame(this);
        games.add(this);
        cards = new HashMap<>();
    }

    public static Game getGame(String id) {
        for (Game game : games) {
            if (game.id.equals(id)) {
                return game;
            }
        }
        return null;
    }

    public int getSize() {
        return size;
    }

    public String getHostName() {
        return host.getName();
    }

    public String getId() {
        return id;
    }

    public int getPlayersCount() {
        return accounts.size();
    }

    public void leave(Account account) {
        accounts.remove(account);
        sendNewData();
    }

    public void join(Account account) {
        account.setGame(this);
        accounts.add(account);
        sendNewData();
    }

    public void sendNewData() {
        synchronized (accounts) {
            if (!started) {
                String[] accountsNames = new String[accounts.size() + 2];
                accountsNames[0] = "ROOM_MEMBERS";
                accountsNames[1] = accounts.size() + "/" + size;
                for (int i = 0; i < accounts.size(); i++) {
                    accountsNames[i + 2] = accounts.get(i).getName();
                }
                for (Account a : accounts) {
                    a.getClientHandler().send(accountsNames);
                }
            } else {
                for (Account a : accounts) {
                    a.getClientHandler().send(gameInfo(a));
                }
            }
        }
    }

    private String[] gameInfo(Account a) {
        ArrayList<String> answer = new ArrayList<>();
        answer.add("GAME_DATA");
        answer.add(String.valueOf(hearts));
        answer.add(String.valueOf(lastPlayedCard));
        answer.add(String.valueOf(ninja));
        answer.add(String.valueOf(level));
        answer.add(accounts.size() + "/" + (size - accounts.size()));
        int remainingCards = 0;
        for (Account account : accounts) {
            remainingCards += cards.get(account).size();
        }
        remainingCards += computerCards.size();
        answer.add(String.valueOf(remainingCards));
        for (Integer card : cards.get(a)) {
            answer.add(String.valueOf(card));
        }
        return answer.toArray(new String[0]);
    }

    public boolean isFull() {
        return size == accounts.size();
    }

    public Account getOwner() {
        return host;
    }

    public void setPlayersNum(int playersNum) {
        size = playersNum;
        if (size < accounts.size())
            size = accounts.size();
        sendNewData();
    }

    public void start() {
        Thread thread = new Thread(this);
        thread.start();
        hearts = size;
        started = true;

        drawCards();

    }

    private void drawCards() {
        ArrayList<Integer> allCards = new ArrayList<>();
        for (int i = 1; i <= 100; i++) {
            allCards.add(i);
        }
        Collections.shuffle(allCards);

        for (Account account : accounts) {
            ArrayList<Integer> userCards = new ArrayList<>();
            for (int i = 0; i < level; i++) {
                userCards.add(allCards.remove(0));
            }
            Collections.sort(userCards);
            if (cards.containsKey(account))
                cards.replace(account, userCards);
            else
                cards.put(account, userCards);
        }
        for (int i = 0; i < level * (size - accounts.size()); i++) {
            computerCards.add(allCards.remove(0));
        }
        Collections.sort(computerCards);
        sendNewData();
    }


    public void play(Account account, Integer card) {
        if (end)
            return;
        if (account != null)
            cards.get(account).remove(card);
        else
            computerCards.remove(card);
        lastPlayedCard = card;
        checkLost(account);
        checkWin();
        sendNewData();
    }

    private void checkLost(Account lastPlayer) {
        HashMap<String, Integer> wrongs = new HashMap<>();
        for (Account account : accounts) {
            if (cards.get(account).size() != 0 && cards.get(account).get(0) < lastPlayedCard) {
                wrongs.put(account.getName(), cards.get(account).remove(0));
            }
        }
        for (Integer i : computerCards) {
            if (i < lastPlayedCard) {
                wrongs.put("Computer", i);
            }
        }
        if (wrongs.size() == 0) {
            sendMessageToAll((lastPlayer != null ? lastPlayer.getName() : "Computer") + " Played " + lastPlayedCard + " And it was correct!");
            return;
        }
        hearts--;
        if (hearts == 0) {
            end = true;
        }
        String message = (lastPlayer != null ? lastPlayer.getName() : "Computer") + " Played " + lastPlayedCard + " But ";
        for (Map.Entry<String, Integer> wrong : wrongs.entrySet()) {
            message += wrong.getKey() + " Had " + wrong.getValue() + ", ";
        }

        if (hearts == 0) {
            message += " You Lost!!!!";
        }

        sendMessageToAll(message);

        sendNewData();
    }

    private void sendMessageToAll(String message) {
        synchronized (accounts) {
            for (Account a : accounts) {
                a.getClientHandler().send("MESSAGE", message);
            }
        }
    }

    private void checkWin() {
        for (Account account : accounts) {
            if (cards.get(account).size() != 0)
                return;
        }
        giveRewards();
        level++;
        drawCards();
        sendNewData();
    }

    private void giveRewards() {
        if (level == 3 || level == 6 || level == 9) {
            hearts++;
        }
        if (level == 2 || level == 5 || level == 8) {
            ninja++;
        }
        if (level == 12) {
            end = true;
            sendMessageToAll("server.Game ended! You Won!!!");
        }
    }

    public void ninja(Account account) {
        if (end || ninja == 0)
            return;
        ninja--;
        ArrayList<Integer> playedCards = new ArrayList<>();
        String message = account.getName() + " Played ninja! Played cards: ";
        for (Account a : accounts) {
            if (cards.get(a).size() != 0) {
                int newCard = cards.get(a).remove(0);
                playedCards.add(newCard);
                message += newCard + " ";
            }
        }
        for (int i = 0; i < size - accounts.size(); i++) {
            if (computerCards.size() == 0)
                break;
            int newCard = computerCards.remove(0);
            playedCards.add(newCard);
            message += newCard + " ";
        }
        lastPlayedCard = Collections.max(playedCards);
        sendMessageToAll(message);
        checkWin();
        sendNewData();
    }

    @Override
    public void run() {
        Random random = new Random();
        if (accounts.size() == size) {
            return;
        }

        try {
            while (true) {
                Thread.sleep(400);
                if (computerCards.size() == 0)
                    continue;
                int num = random.nextInt(computerCards.get(0));
                if (num == 0) {
                    play(null, computerCards.get(0));
                    Thread.sleep(100);
                }
            }
        } catch (Exception e) {
        }
    }
}
