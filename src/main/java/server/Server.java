package server;

import lombok.Getter;

import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.ArrayList;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.atomic.AtomicInteger;

public class Server
{
    private @Getter final int ROOM_MAX = 10;
    private ServerSocket serverSocket;
    private @Getter ExecutorService clientExecutor = Executors.newFixedThreadPool(10);
    private @Getter ConcurrentHashMap<Integer, ClientListener> clientListeners = new ConcurrentHashMap<>();
    private @Getter ConcurrentHashMap<Integer, Boolean> takenClientIDs = new ConcurrentHashMap<>();

    private @Getter ConcurrentHashMap<Integer, Room> rooms = new ConcurrentHashMap<>();
    private @Getter ConcurrentHashMap<Integer, Boolean> takenRoomIDs = new ConcurrentHashMap<>();
    private @Getter AtomicInteger actualRoomsSize = new AtomicInteger(0);

    private @Getter ArrayList<String> takenNicknames = new ArrayList<>();

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
            takenClientIDs.put(i, false);

        try
        {
            serverSocket = new ServerSocket(9999);
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
                int id = assignClientID();
                ClientListener newClientListener = new ClientListener(this, newClient, id);
                clientListeners.put(id, newClientListener);
                clientExecutor.submit(newClientListener);
            }
        } catch (IOException e)
        {
            e.printStackTrace();
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

    public int assignClientID()
    {
        for (Map.Entry<Integer, Boolean> entry : takenClientIDs.entrySet())
        {
            if (!entry.getValue())
            {
                entry.setValue(true);
                return entry.getKey();
            }
        }
        return -1;
    }

    public void addRoom(Room room, int roomID)
    {
        rooms.put(roomID, room);
    }

    public void removeRoom(Room room)
    {
        actualRoomsSize.decrementAndGet();
        takenRoomIDs.replace(room.getId(), false);
        rooms.remove(room.getId());
    }
}
