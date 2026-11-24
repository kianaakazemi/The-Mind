package GUI;

import Client.ServerLogic;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.ArrayList;
import java.util.Collections;

public class GameFrame extends JFrame {
    public static GameFrame instance = new GameFrame();
    private JLabel hearts = new JLabel("Hearts: ");
    private JLabel ninjas = new JLabel("Ninjas: ");
    private JLabel lastCard = new JLabel("Last card: ");
    private JLabel level = new JLabel("Level: ");
    private JLabel playersNum = new JLabel("Players/Robots: ");
    private JLabel cardsCount = new JLabel("Remaining Cards: ");
    private JButton playNinja = new JButton("Play Ninja");


    private JTextArea messages = new JTextArea("Nothing yet!");

    private JPanel cardsPanel = new JPanel();


    private GameFrame() {
        super("Game");
        createGUI();
        setResizable(false);
        setVisible(true);
        setLocationRelativeTo(null);
        addWindowListener(new java.awt.event.WindowAdapter() {
            @Override
            public void windowClosing(java.awt.event.WindowEvent windowEvent) {
                MainFrame.instance.setVisible(true);
            }
        });
    }

    private void createGUI() {
        setLayout(null);
        setSize(400, 650);
        hearts.setSize(100, 20);
        ninjas.setSize(100, 20);
        lastCard.setSize(100, 20);
        level.setSize(100, 20);
        playersNum.setSize(200, 20);
        messages.setSize(300, 200);
        playNinja.setSize(100, 40);
        cardsCount.setSize(300, 40);

        messages.setEditable(false);

        hearts.setLocation(50, 20);
        ninjas.setLocation(50, 50);
        lastCard.setLocation(50, 80);
        level.setLocation(50, 110);
        playersNum.setLocation(50, 140);
        cardsCount.setLocation(50, 170);

        messages.setLocation(50, 200);


        playNinja.setLocation(150, 45);


        playNinja.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                ServerLogic.instance.playNinja();
            }
        });


        add(hearts);
        add(ninjas);
        add(lastCard);
        add(level);
        add(playersNum);
        add(messages);
        add(playNinja);
        add(cardsCount);

        cardsPanel.setLayout(new GridLayout(2, 6));
        add(cardsPanel);
        cardsPanel.setLocation(25, 500);
        cardsPanel.setSize(325, 50);
    }

    public void setMessages(String text) {
        messages.setText(text);
    }

    public void updateData(String[] args) {
        hearts.setText("Hearts: " + args[1]);
        lastCard.setText("Last Card: " + args[2]);
        ninjas.setText("Ninjas: " + args[3]);
        level.setText("Level: " + args[4]);
        playersNum.setText("Players/Robots: " + args[5]);
        cardsCount.setText("Remaining Cards: " + args[6]);
        cardsPanel.removeAll();
        ArrayList<Integer> nums = new ArrayList<>();
        for (int i = 7; i < args.length; i++) {
            nums.add(Integer.valueOf(args[i]));
        }
        Collections.sort(nums);
        for (Integer num : nums) {
            JButton button = new JButton(String.valueOf(num));
            cardsPanel.add(button);
            button.addActionListener(I -> ServerLogic.instance.playCard(num));
        }
        repaint();
        setVisible(true);
    }
}
