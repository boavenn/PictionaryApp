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
                    case 0: // disconnection from a room only
                        room.removePlayer(player, true);
                        connected = false;
                        break;
                    case 1: // disconnection from the whole app
                        room.removePlayer(player, false);
                        connected = false;
                        break;
                    case 6: // text message to send others
                        room.sendTextMessageToAllFrom(player, in.readUTF());
                        break;
                    case 7: // new shape
                        String shape = in.readUTF();
                        room.sendShapeToAllExcept(player, shape);
                        break;
                    case 8: // undo
                        room.sendUndoRequestToAllExcept(player);
                        break;
                    case 9: // redo
                        room.sendRedoRequestToAllExcept(player);
                        break;
                    case 10: // clear
                        room.sendClearRequestToAllExcept(player);
                        break;
                }
            }
        } catch (IOException e)
        {
            room.removePlayer(player, false);
            e.printStackTrace();
        }
    }
}
