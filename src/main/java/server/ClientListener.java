package server;

import javax.xml.crypto.Data;
import java.io.DataInputStream;
import java.io.DataOutput;
import java.io.DataOutputStream;
import java.io.IOException;
import java.net.Socket;

public class ClientListener implements Runnable
{
    private Socket client;
    private DataInputStream in;
    private DataOutputStream out;
    boolean connectedToARoom = false;

    public ClientListener(Socket client)
    {
        try
        {
            this.client = client;
            this.out = new DataOutputStream(client.getOutputStream());
            this.in = new DataInputStream(client.getInputStream());
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
            while(!connectedToARoom)
            {
                byte messType = in.readByte();
                switch (messType)
                {
                    case 1: // room creation
                        // update rooms <- maybe do it in server directly?
                        // create a new one
                        break;
                    case 2: // joining a room / sending client a list of available rooms
                        // update rooms
                        // send the list of rooms
                        break;
                    case 3: // client chose a room
                        // update chosen room and check once more if the room is available
                        // if yes ask client for a nickname and add him to the room
                        break;
                    case 4: // client sent a nickname
                        // check if player with such name already exist
                        // if no add this player to the list of players
                        // set connectedToARoom = true
                        break;
                }
            }
        } catch (IOException e)
        {
            e.printStackTrace();
        }
    }
}
