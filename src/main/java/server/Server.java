package server;

import lombok.Getter;

import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.ArrayList;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class Server
{
    private ServerSocket serverSocket;
    private Socket newClient;
    private ExecutorService roomExecutor;
    private @Getter ArrayList<String> takenNicknames = new ArrayList<>();

    public static void main(String[] args)
    {
        Server s = new Server();
        s.startListening();
    }

    private Server()
    {
        try
        {
            serverSocket = new ServerSocket(999);
            roomExecutor = Executors.newFixedThreadPool(10);
        } catch (IOException e)
        {
            e.printStackTrace();
        }
    }

    private void startListening()
    {
        try
        {
            while(true)
            {
                newClient = serverSocket.accept();
                // prior to adding a new client we need to update rest of them to check if connection with somebody is lost
                // then we can add a new client to the list and start a new listening thread for him
            }
        } catch (IOException e)
        {
            e.printStackTrace();
        }
    }
}
