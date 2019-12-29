package server;

import com.google.gson.Gson;
import lombok.Getter;

import java.io.IOException;
import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class Room implements Runnable
{
    private final Server server;
    private ExecutorService playerExecutor;
    private @Getter HashMap<Player, PlayerListener> playerListeners = new HashMap<>();
    private final int PLAYERS_MAX = 4;
    private int numOfConnectedPlayers = 0;
    private @Getter int id;
    private Gson gson = new Gson();

    public Room(Server server, Player player, int id)
    {
        this.server = server;
        this.id = id;
        playerExecutor = Executors.newFixedThreadPool(PLAYERS_MAX);
        addPlayer(player);
    }

    @Override
    public void run()
    {
        try
        {
            while (numOfConnectedPlayers > 0)
            {
                Thread.sleep(200);
            }
        } catch (InterruptedException e)
        {
            e.printStackTrace();
        }
        server.removeRoom(this);
    }

    public void addPlayer(Player player)
    {
        PlayerListener playerListener = new PlayerListener(player, this);
        playerListeners.put(player, playerListener);
        playerExecutor.submit(playerListener);
        numOfConnectedPlayers++;
        sendMessageToAllExcept(player,"Player '" + player.getNickname() + "' joined.");
        sendStatusUpdateToAll();
    }

    public void removePlayer(Player player)
    {
        playerListeners.remove(player);
        ClientListener clientListener = new ClientListener(server, player.getSocket(), player.getId(), player.getNickname());
        server.getClientListeners().put(player.getId(), clientListener);
        server.getClientExecutor().submit(clientListener);
        numOfConnectedPlayers--;
        sendMessageToAllExcept(player, "Player '" + player.getNickname() + "' left.");
        sendStatusUpdateToAllExcept(player);
    }

    public void sendStatusUpdateToAllExcept(Player player)
    {
        try
        {
            for (Map.Entry<Player, PlayerListener> entry : playerListeners.entrySet())
            {
                if(!entry.getKey().equals(player))
                {
                    entry.getValue().getOut().writeByte(4); // room status update
                    entry.getValue().getOut().writeUTF(gson.toJson(getPlayerNicknames()));
                    entry.getValue().getOut().writeUTF(gson.toJson(getPlayerPoints()));
                }
            }
        } catch (IOException e)
        {
            e.printStackTrace();
        }
    }

    public void sendStatusUpdateToAll()
    {
        try
        {
            for (Map.Entry<Player, PlayerListener> entry : playerListeners.entrySet())
            {
                entry.getValue().getOut().writeByte(4); // room status update
                entry.getValue().getOut().writeUTF(gson.toJson(getPlayerNicknames()));
                entry.getValue().getOut().writeUTF(gson.toJson(getPlayerPoints()));
            }
        } catch (IOException e)
        {
            e.printStackTrace();
        }
    }

    public void sendMessageToAllExcept(Player player, String text)
    {
        try
        {
            for (Map.Entry<Player, PlayerListener> entry : playerListeners.entrySet())
                if (!entry.getKey().equals(player))
                {
                    entry.getValue().getOut().writeByte(5);
                    entry.getValue().getOut().writeUTF(text);
                }
        } catch (IOException e)
        {
            e.printStackTrace();
        }
    }

    public void sendMessageToAll(String text)
    {
        try
        {
            for (Map.Entry<Player, PlayerListener> entry : playerListeners.entrySet())
            {
                entry.getValue().getOut().writeByte(5);
                entry.getValue().getOut().writeUTF(text);
            }
        } catch (IOException e)
        {
            e.printStackTrace();
        }
    }

    @Override
    public String toString()
    {
        StringBuilder str = new StringBuilder(id + ". [");
        for(Map.Entry<Player, PlayerListener> entry : playerListeners.entrySet())
            str.append(entry.getKey().getNickname()).append(", ");
        str.append("]");
        return str.toString();
    }

    public String[] getPlayerNicknames()
    {
        String[] temp = new String[playerListeners.size()];
        int i = 0;
        for(Map.Entry<Player, PlayerListener> entry : playerListeners.entrySet())
            temp[i++] = entry.getKey().getNickname();
        return temp;
    }

    public int[] getPlayerPoints()
    {
        int[] temp = new int[playerListeners.size()];
        int i = 0;
        for(Map.Entry<Player, PlayerListener> entry : playerListeners.entrySet())
            temp[i++] = entry.getKey().getPoints();
        return temp;
    }

    public boolean isFull()
    {
        return numOfConnectedPlayers == PLAYERS_MAX;
    }
}
