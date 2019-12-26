package server;

import java.io.IOException;

public class PlayerListener implements Runnable
{
    private Player player;
    private boolean connected = false;

    public PlayerListener(Player player)
    {
        this.player = player;
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
                    // here service of different requests will happen
                }
            }
        } catch (IOException e)
        {
            e.printStackTrace();
        }
    }
}
