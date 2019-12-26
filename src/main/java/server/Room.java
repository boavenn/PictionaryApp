package server;

import lombok.Getter;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;

public class Room extends Thread
{
    private final Server server;
    private ExecutorService playerExecutor;
    private HashMap<Player, Future<?>> playerStatus = new HashMap<>();
    private final int PLAYERS_MAX = 4;
    private int numOfConnectedPlayers = 0;

    public Room(Server server)
    {
        this.server = server;
        playerExecutor = Executors.newFixedThreadPool(PLAYERS_MAX);
    }

    @Override
    public void run()
    {

    }

    public void addPlayer(Player player)
    {
        playerStatus.put(player, playerExecutor.submit(new PlayerListener(player)));
        server.getTakenNicknames().add(player.getNickname());
        numOfConnectedPlayers++;
    }

    public void updatePlayerStatus()
    {
        Iterator<Map.Entry<Player, Future<?>>> itr = playerStatus.entrySet().iterator();
        while(itr.hasNext())
        {
            Map.Entry<Player, Future<?>> entry = itr.next();
            if(entry.getValue().isDone())
            {
                itr.remove();
                server.getTakenNicknames().remove(entry.getKey().getNickname());
                numOfConnectedPlayers--;
            }
        }
    }

    public boolean isFull()
    {
//        updatePlayerStatus();
        return !(numOfConnectedPlayers < PLAYERS_MAX);
    }
}
