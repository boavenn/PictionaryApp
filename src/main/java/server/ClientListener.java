package server;

import com.google.gson.Gson;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.net.Socket;
import java.util.Map;

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
    private Gson gson = new Gson();

    public ClientListener(Server server, Socket client, int id)
    {
        this.server = server;
        this.socket = client;
        this.id = id;
        this.nickname = "";
        try
        {
            this.out = new DataOutputStream(client.getOutputStream());
            this.in = new DataInputStream(client.getInputStream());
        } catch (IOException e)
        {
            e.printStackTrace();
        }
    }

    public ClientListener(Server server, Socket client, int id, String nickname)
    {
        this(server, client, id);
        this.nickname = nickname;
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
                    case 0: // disconnection
                        connected = false;
                        server.getTakenNicknames().remove(nickname);
                        server.getClientListeners().remove(id);
                        break;
                    case 1: // room creation request
                        synchronized (this)
                        {
                            out.writeByte(1);
                            if(server.getRooms().size() < server.getROOM_MAX())
                            {
                                out.writeBoolean(true);
                                int roomID = server.assignRoomID();
                                out.writeInt(roomID);
                                Room room = new Room(server, new Player(nickname, socket, id), roomID);
                                server.getRooms().put(roomID, room);
                                server.getRoomExecutor().submit(room);
                                server.getClientListeners().remove(id);
                                connectedToARoom = true;
                            }
                            else
                            {
                                out.writeBoolean(false);
                                out.writeUTF("Not enough space for making a new room");
                            }
                        }
                        break;
                    case 2: // room view request
                        synchronized (this)
                        {
                            out.writeByte(2);
                            if(server.getRooms().size() > 0)
                            {
                                out.writeBoolean(true);
                                String[] roomInfo = new String[server.getRooms().size()];
                                int i = 0;
                                for (Map.Entry<Integer, Room> entry : server.getRooms().entrySet())
                                    roomInfo[i++] = entry.getValue().toString();
                                out.writeUTF(gson.toJson(roomInfo));
                            }
                            else
                            {
                                out.writeBoolean(false);
                                out.writeUTF("No rooms available");
                            }
                        }
                        break;
                    case 3: // room join request
                        int roomID = in.readInt();
                        synchronized (this)
                        {
                            out.writeByte(3);
                            if(!server.getRooms().get(roomID).isFull())
                            {
                                out.writeBoolean(true);
                                server.getRooms().get(roomID).addPlayer(new Player(nickname, socket, id));
                                server.getClientListeners().remove(id);
                                connectedToARoom = true;
                            }
                            else
                            {
                                out.writeBoolean(false);
                                out.writeUTF("Chosen room is already full");
                            }
                        }
                        break;
                }
            }
        } catch (IOException e)
        {
            e.printStackTrace();
            if(nicknameSet)
                server.getTakenNicknames().remove(nickname);
            server.getTakenClientIDs().replace(id, false);
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
