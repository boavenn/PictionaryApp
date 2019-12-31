package server;

import com.google.gson.Gson;
import lombok.Getter;

import java.io.BufferedReader;
import java.io.FileInputStream;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;
import java.util.Random;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class Room implements Runnable
{
    private final Server server;
    private Gson gson = new Gson();
    private ExecutorService playerExecutor;
    private @Getter HashMap<Player, PlayerListener> playerListeners = new HashMap<>();
    private final int PLAYERS_MAX = 4;
    private int numOfConnectedPlayers = 0;
    private @Getter int id;
    private @Getter boolean sessionRunning = false;
    private ArrayList<String> words = new ArrayList<>();
    private @Getter String wordToGuess = "";
    private Player drawingPlayer;

    public Room(Server server, Player player, int id)
    {
        initWords();
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
                Thread.sleep(500);
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
        sendServerMessageToAllExcept(player,"Player '" + player.getNickname() + "' joined.");
        sendStatusUpdateToAll();
        if(numOfConnectedPlayers > 1)
        {
            if(!sessionRunning)
            {
                chooseDrawingPlayerAndWordToGuess();
                sessionRunning = true;
            }
            else
                sendDrawingStatusToAll(drawingPlayer.getNickname());
        }
    }

    public void removePlayer(Player player, boolean backToClient)
    {
        playerListeners.remove(player);
        if(backToClient)
        {
            ClientListener clientListener = new ClientListener(server, player.getSocket(), player.getId(), player.getNickname());
            server.getClientListeners().put(player.getId(), clientListener);
            server.getClientExecutor().submit(clientListener);
        }
        else
        {
            server.getTakenNicknames().remove(player.getNickname());
            server.getClientListeners().remove(id);
        }
        numOfConnectedPlayers--;
        sendServerMessageToAllExcept(player, "Player '" + player.getNickname() + "' left.");
        sendStatusUpdateToAllExcept(player);
        if(player.isDrawing()) // if that player is drawing we clear the paintpanel of other players
        {
            sendClearRequestToAllExcept(player);
            if(numOfConnectedPlayers > 1)
                chooseDrawingPlayerAndWordToGuess();
        }
        if(numOfConnectedPlayers < 2)
            sessionRunning = false;
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

    public void sendServerMessageToAllExcept(Player player, String text)
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

    public void sendServerMessageTo(Player player, String text)
    {
        try
        {
            playerListeners.get(player).getOut().writeByte(5);
            playerListeners.get(player).getOut().writeUTF(text);
        } catch (IOException e)
        {
            e.printStackTrace();
        }
    }

    public void sendTextMessageToAllFrom(Player player, String text)
    {
        try
        {
            for (Map.Entry<Player, PlayerListener> entry : playerListeners.entrySet())
                if (!entry.getKey().equals(player))
                {
                    entry.getValue().getOut().writeByte(6);
                    entry.getValue().getOut().writeUTF(player.getNickname());
                    entry.getValue().getOut().writeUTF(text);
                }
        } catch (IOException e)
        {
            e.printStackTrace();
        }
    }

    public void sendShapeToAllExcept(Player player, String[] shape, int chunks)
    {
        try
        {
            for (Map.Entry<Player, PlayerListener> entry : playerListeners.entrySet())
            {
                if(!entry.getKey().equals(player))
                {
                    entry.getValue().getOut().writeByte(7);
                    entry.getValue().getOut().writeInt(chunks);
                    for(int i = 0; i < chunks; i++)
                        entry.getValue().getOut().writeUTF(shape[i]);
                }
            }
        } catch (IOException e)
        {
            e.printStackTrace();
        }
    }

    public void sendUndoRequestToAllExcept(Player player)
    {
        try
        {
            for (Map.Entry<Player, PlayerListener> entry : playerListeners.entrySet())
                if(!entry.getKey().equals(player))
                    entry.getValue().getOut().writeByte(8);
        } catch (IOException e)
        {
            e.printStackTrace();
        }
    }

    public void sendRedoRequestToAllExcept(Player player)
    {
        try
        {
            for (Map.Entry<Player, PlayerListener> entry : playerListeners.entrySet())
                if(!entry.getKey().equals(player))
                    entry.getValue().getOut().writeByte(9);
        } catch (IOException e)
        {
            e.printStackTrace();
        }
    }

    public void sendClearRequestToAllExcept(Player player)
    {
        try
        {
            for (Map.Entry<Player, PlayerListener> entry : playerListeners.entrySet())
                if(!entry.getKey().equals(player))
                    entry.getValue().getOut().writeByte(10);
        } catch (IOException e)
        {
            e.printStackTrace();
        }
    }

    public boolean checkIfGuessed(String word)
    {
        return word.toLowerCase().equals(wordToGuess.toLowerCase());
    }

    public void chooseDrawingPlayerAndWordToGuess()
    {
        ArrayList<Player> temp = new ArrayList<>();
        for (Map.Entry<Player, PlayerListener> entry : playerListeners.entrySet())
            if(!entry.getKey().isDrawing())
                temp.add(entry.getKey());
            else
                entry.getKey().setDrawing(false);

        Random r = new Random();
        int idx = r.nextInt(temp.size());
        drawingPlayer = temp.get(idx);
        drawingPlayer.setDrawing(true);
        wordToGuess = chooseWordToGuess();

        sendDrawingStatusToAll(drawingPlayer.getNickname());
    }

    public void sendDrawingStatusToAll(String whoIsDrawing)
    {
        try
        {
            for (Map.Entry<Player, PlayerListener> entry : playerListeners.entrySet())
            {
                entry.getValue().getOut().writeByte(11);
                entry.getValue().getOut().writeBoolean(entry.getKey().isDrawing());
                if(entry.getKey().isDrawing())
                    entry.getValue().getOut().writeUTF(wordToGuess);
                else
                    entry.getValue().getOut().writeUTF(whoIsDrawing);
            }
        } catch (IOException e)
        {
            e.printStackTrace();
        }
    }

    private String chooseWordToGuess()
    {
        Random r = new Random();
        int idx = r.nextInt(words.size());
        wordToGuess = words.get(idx);
        return wordToGuess;
    }

    private void initWords()
    {
        try(BufferedReader in = new BufferedReader(new FileReader("src/main/java/server/words.txt")))
        {
            int size = Integer.parseInt(in.readLine());
            for(int i = 0; i < size; i++)
                words.add(in.readLine());
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
