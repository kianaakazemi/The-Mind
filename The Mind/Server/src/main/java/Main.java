import config.Config;
import server.Server;

import java.io.IOException;

public class Main {
    public static void main(String[] args) throws IOException {
        System.out.println("server.Server started!");
        Integer port = Config.getConfig().getProperty(Integer.class, "port");
        Server server = new Server(port);
        server.acceptRequests();
    }
}
