package server;

import java.io.IOException;

public class PlayerListener implements Runnable
{
    private Player player;
    private Room room;
    private boolean connected = true;

    public PlayerListener(Player player, Room room)
    {
        this.player = player;
        this.room = room;
    }

    @Override
    public void run()
    {
        try
        {
            while(connected)
            {
                byte messType = player.getInputStream().readByte();
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
