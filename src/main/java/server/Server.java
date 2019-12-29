package server;

import lombok.Getter;

import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;

public class Server
{
    private @Getter final int ROOM_MAX = 10;
    private ServerSocket serverSocket;
    private @Getter ExecutorService clientExecutor = Executors.newFixedThreadPool(10);
    private @Getter ExecutorService roomExecutor = Executors.newFixedThreadPool(ROOM_MAX);
    private @Getter HashMap<Integer, Future<?>> clientStatus = new HashMap<>();
    private @Getter HashMap<Integer, ClientListener> clientListeners = new HashMap<>();
    private @Getter ArrayList<String> takenNicknames = new ArrayList<>();
    private @Getter HashMap<Integer, Future<?>> roomStatus = new HashMap<>();
    private @Getter HashMap<Integer, Room> rooms = new HashMap<>();
    private @Getter HashMap<Integer, Boolean> takenRoomIDs = new HashMap<>();
    private @Getter HashMap<Integer, Boolean> takenPlayerIDs = new HashMap<>();

    public static void main(String[] args)
    {
        Server s = new Server();
        s.startListening();
    }

    private Server()
    {
        for(int i = 1; i <= ROOM_MAX; i++)
            takenRoomIDs.put(i, false);
        for(int i = 1; i <= 100; i++)
            takenPlayerIDs.put(i, false);

        try
        {
            serverSocket = new ServerSocket(999);
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
                Socket newClient = serverSocket.accept();
                int id = assignPlayerID();
                ClientListener newClientListener = new ClientListener(this, newClient, id, "");
                clientListeners.put(id, newClientListener);
                clientStatus.put(id, clientExecutor.submit(newClientListener));
            }
        } catch (IOException e)
        {
            e.printStackTrace();
        }
    }

    private void sendQuitMessage()
    {
        for (Map.Entry<Integer, ClientListener> entry : clientListeners.entrySet())
        {
            entry.getValue().sendQuitMessage();
        }
    }

    public int assignRoomID()
    {
        for (Map.Entry<Integer, Boolean> entry : takenRoomIDs.entrySet())
        {
            if (!entry.getValue())
            {
                entry.setValue(true);
                return entry.getKey();
            }
        }
        return -1;
    }

    public int assignPlayerID()
    {
        for (Map.Entry<Integer, Boolean> entry : takenPlayerIDs.entrySet())
        {
            if (!entry.getValue())
            {
                entry.setValue(true);
                return entry.getKey();
            }
        }
        return -1;
    }

    public void removeClient(int id)
    {
        clientStatus.remove(id);
        clientListeners.remove(id);
    }

    public void removeRoom(Room room)
    {
        rooms.remove(room.getId());
        roomStatus.remove(room.getId());
        takenRoomIDs.replace(room.getId(), false);
    }
}
