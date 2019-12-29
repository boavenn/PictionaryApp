package server;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.net.Socket;

public class ClientListener implements Runnable
{
    private Socket socket;
    private Server server;
    private DataInputStream in;
    private DataOutputStream out;
    boolean connectedToARoom = false;
    boolean connected = true;
    private String nickname;
    private int id;

    public ClientListener(Server server, Socket client, int id, String nickname)
    {
        this.server = server;
        this.socket = client;
        this.id = id;
        this.nickname = nickname;
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
        boolean nicknameSet = true;
        if(nickname.isEmpty())
            nicknameSet = false;
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
                        server.removeClient(id);
                        break;
                    case 1: // room creation request
                        out.writeByte(1);
                        synchronized (this)
                        {
                            if(server.getRooms().size() < server.getROOM_MAX())
                            {
                                out.writeBoolean(true);
                                int roomID = server.assignRoomID();
                                out.writeInt(roomID);
                                Room room = new Room(server, new Player(nickname, socket, id), roomID);
                                server.getRooms().put(roomID, room);
                                server.getRoomStatus().put(roomID, server.getRoomExecutor().submit(room));
                                server.removeClient(id);
                                connectedToARoom = true;
                            }
                            else
                            {
                                out.writeBoolean(false);
                                out.writeUTF("Not enough space for making a new room");
                            }
                        }
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
            server.removeClient(id);
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
