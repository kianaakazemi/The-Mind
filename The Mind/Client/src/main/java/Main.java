
import Client.ServerLogic;
import GUI.MainFrame;
import config.Config;

import javax.swing.*;
import java.io.IOException;

public class Main {
    public static void main(String[] args) throws IOException {
        Integer port = Config.getConfig().getProperty(Integer.class, "port");
        String host = Config.getConfig().getProperty(String.class, "host");
        ServerLogic.createServerConnection(host,port);
        new MainFrame();
    }
}
