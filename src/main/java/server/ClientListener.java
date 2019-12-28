package server;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.net.Socket;

public class ClientListener implements Runnable
{
    private Server server;
    private DataInputStream in;
    private DataOutputStream out;
    boolean connectedToARoom = false;
    boolean connected = true;
    private String nickname;
    private int id;

    public ClientListener(Server server, Socket client, int id)
    {
        this.server = server;
        this.id = id;
        try
        {
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
        boolean nicknameSet = false;
        try
        {
            while(!nicknameSet)
            {
                String str = in.readUTF();
                if (!server.getTakenNicknames().contains(str))
                {
                    out.writeBoolean(true);
                    server.getTakenNicknames().add(str);
                    nickname = str;
                    nicknameSet = true;
                }
                else
                {
                    out.writeBoolean(false);
                    out.writeUTF("This nickname is already taken");
                }
            }

            while(!connectedToARoom && connected)
            {
                byte messType = in.readByte();
                switch (messType)
                {
                    case 0:
                        connected = false;
                        server.getTakenNicknames().remove(nickname);
                        server.getTakenPlayerIDs().replace(id, false);
                        server.getClientStatus().remove(id);
                        server.getClientListeners().remove(id);
                        break;
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
            if(nicknameSet)
                server.getTakenNicknames().remove(nickname);
            server.getTakenPlayerIDs().replace(id, false);
            server.getClientStatus().remove(id);
            server.getClientListeners().remove(id);
        }
    }

    public void sendQuitMessage()
    {
        try
        {
            out.writeByte(0);
        } catch (IOException e)
        {
            e.printStackTrace();
        }
    }
}
