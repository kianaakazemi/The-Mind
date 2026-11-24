package GUI;

import Client.ServerLogic;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

public class RoomFrame extends JFrame {
    public static RoomFrame instance;
    JPanel membersPanel = new JPanel();
    public RoomFrame(String title)  {
        super(title);
        setLayout(null);
        setSize(400, 650);

        membersPanel.setLayout(new GridLayout(11,1));
        membersPanel.setSize(300,300);
        add(membersPanel);
        membersPanel.setLocation(50,50);

        setLocationRelativeTo(null);
        setVisible(true);
        instance = this;

        JButton setMaxMembers = new JButton("Set max members");
        setMaxMembers.setSize(300,50);
        add(setMaxMembers);
        setMaxMembers.setLocation(50,500);
        setMaxMembers.addActionListener(I->{
            String playersCount = JOptionPane.showInputDialog("Enter new maximum players: ");
            try{
                int num = Integer.parseInt(playersCount);
                if(num <= 8){
                    ServerLogic.instance.setPLayersNum(playersCount);
                }
            }catch (Exception ignored){

            }
        });
        JButton startGame = new JButton("Start Game");
        startGame.setSize(300,50);
        add(startGame);
        startGame.setLocation(50,550);
        startGame.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                ServerLogic.instance.startGame();
            }
        });

    }

    public void setMembers(String[] members) {
        membersPanel.removeAll();
        int players = Integer.parseInt(members[0].split("/")[0]);
        int maximum = Integer.parseInt(members[0].split("/")[1]);
        for (int i = 1; i < members.length; i++) {
            membersPanel.add(new JLabel("\uD83D\uDC68 " +members[i]));
        }
        for (int i = 0; i < maximum-players; i++) {
            membersPanel.add(new JLabel("💻 Computer"));
        }
        membersPanel.add(new JLabel(members[0]));
        repaint();
        setVisible(true);

    }
}
