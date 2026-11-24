package GUI;

import Client.ServerLogic;

import javax.swing.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

public class RoomButton extends JButton {
    private final String id;

    public RoomButton(String id, String creator, String capacity) {
        super(id + " - " + creator + " ( " + capacity + " )");
        this.id = id;
        addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                joinGame();
            }
        });
    }

    private void joinGame() {
        ServerLogic.instance.join(id);
    }
}
