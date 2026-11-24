package GUI;

import Client.ServerLogic;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

public class MainFrame extends JFrame {
    public static MainFrame instance;
    private String username;
    private JPanel roomsPanel;

    public MainFrame() {
        super("Mind");
        instance = this;
        askForUsername();
        createGUI();
        setDefaultCloseOperation(EXIT_ON_CLOSE);
        setResizable(false);
        setVisible(true);
    }

    private void createGUI() {
        setLayout(null);
        setSize(400, 650);
        JLabel label = new JLabel("Hello " + username);
        add(label);
        label.setLocation(50, -10);
        label.setSize(300, 100);
        roomsPanel = new JPanel();
        roomsPanel.setBackground(Color.DARK_GRAY);
        add(roomsPanel);
        roomsPanel.setLayout(new GridLayout(10, 1));
        roomsPanel.setLocation(50, 50);
        roomsPanel.setSize(300, 400);
        JButton refresh = new JButton("REFRESH");
        refresh.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                updateRooms();
            }
        });
        refresh.setSize(300,50);
        add(refresh);
        refresh.setLocation(50,500);
        JButton host = new JButton("HOST");
        host.setSize(300,50);
        add(host);
        host.setLocation(50,550);
        host.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                ServerLogic.instance.createRoom();
            }
        });
        updateRooms();
        setLocationRelativeTo(null);
    }

    private void updateRooms() {
        ServerLogic.instance.updateRooms();
    }

    public void setRooms(String... rooms) {
        roomsPanel.removeAll();
        for (int i = 0; i < rooms.length; i += 3) {
            roomsPanel.add(new RoomButton(rooms[i], rooms[i + 1], rooms[i + 2]));
        }

        roomsPanel.setVisible(true);
        setVisible(true);
    }

    private void askForUsername() {
        username = JOptionPane.showInputDialog("Enter username: ");
        while (username == null || username.equals("")) {
            if(username == null ){
                System.exit(0);
            }
            username = JOptionPane.showInputDialog("Enter username: ");
        }
        ServerLogic.instance.login(username);
    }
}
