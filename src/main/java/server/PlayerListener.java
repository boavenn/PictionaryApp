package server;

import lombok.Getter;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;

public class PlayerListener implements Runnable
{
    private Player player;
    private @Getter DataOutputStream out;
    private @Getter DataInputStream in;
    private Room room;
    private boolean connected = true;

    public PlayerListener(Player player, Room room)
    {
        this.player = player;
        this.room = room;
        try
        {
            out = new DataOutputStream(player.getSocket().getOutputStream());
            in = new DataInputStream(player.getSocket().getInputStream());
        } catch (IOException e)
        {
            e.printStackTrace();
        }
    }

    @Override
    public void run()
    {
        try
        {
            while(connected)
            {
                byte messType = in.readByte();
                switch (messType)
                {
                    case 0:
                        room.removePlayer(player);
                        connected = false;
                        break;
                }
            }
        } catch (IOException e)
        {
            room.removePlayer(player);
            e.printStackTrace();
        }
    }
}
