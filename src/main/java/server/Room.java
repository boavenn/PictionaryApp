package server;

import lombok.Getter;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;

public class Room implements Runnable
{
    private final Server server;
    private ExecutorService playerExecutor;
    private @Getter HashMap<Player, Future<?>> playerStatus = new HashMap<>();
    private @Getter HashMap<Player, PlayerListener> playerListeners = new HashMap<>();
    private final int PLAYERS_MAX = 4;
    private int numOfConnectedPlayers = 0;
    private @Getter int id;

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
                Thread.sleep(1000);
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
        playerStatus.put(player, playerExecutor.submit(playerListener));
        numOfConnectedPlayers++;
    }

    public void removePlayer(Player player)
    {
        playerStatus.remove(player);
        playerListeners.remove(player);
        ClientListener clientListener = new ClientListener(server, player.getSocket(), player.getId(), player.getNickname());
        server.getClientListeners().put(player.getId(), clientListener);
        server.getClientStatus().put(player.getId(), server.getClientExecutor().submit(clientListener));
        numOfConnectedPlayers--;
    }

    public boolean isFull()
    {
        return !(numOfConnectedPlayers < PLAYERS_MAX);
    }

    public boolean isEmpty()
    {
        return numOfConnectedPlayers == 0;
    }
}
