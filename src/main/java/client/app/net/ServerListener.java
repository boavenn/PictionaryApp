package client.app.net;

import com.google.gson.Gson;
import lombok.Getter;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.net.Socket;

public class ServerListener implements Runnable
{
    private DataOutputStream out;
    private DataInputStream in;
    private ConnectionManager connectionManager;
    private boolean connected = true;
    private @Getter boolean connectedToARoom = false;
    private Gson gson = new Gson();
    private int chosenID;

    public ServerListener(ConnectionManager connectionManager, Socket socket)
    {
        this.connectionManager = connectionManager;
        try
        {
            out = new DataOutputStream(socket.getOutputStream());
            in = new DataInputStream(socket.getInputStream());
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
            boolean nicknameSet = false;
            while(!nicknameSet)
            {
                connectionManager.setNickname();
                out.writeUTF(connectionManager.getNickname());
                nicknameSet = in.readBoolean();
                if(!nicknameSet)
                {
                    String str = in.readUTF();
                    connectionManager.getApp().getChatPanel().addErrorEntry(str);
                }
            }
            connectionManager.getApp().getChatPanel().addSystemEntry("Your nickname is '" + connectionManager.getNickname() + "'");

            while(connected)
            {
                byte messType = in.readByte();
                switch(messType)
                {
                    case 0: // server disconnected
                        connected = false;
                        connectionManager.getConnectionChecker().disconnect();
                        break;
                    case 1: // room creation response
                        boolean flag = in.readBoolean();
                        if(flag)
                        {
                            int roomID = in.readInt();
                            connectionManager.getApp().getToolPanel().showRoomInfo(roomID);
                            connectionManager.getApp().getChatPanel().addSystemEntry("Successfully created a room [" + roomID + "]");
                            String[] players = {connectionManager.getNickname()};
                            int[] points = {0};
                            connectionManager.getApp().getPlayersPanel().setPlayers(players, points);
                            connectionManager.getApp().getPlayersPanel().makeVisible();
                            connectedToARoom = true;
                        }
                        else
                            connectionManager.getApp().getChatPanel().addErrorEntry(in.readUTF());
                        break;
                    case 2: // room view response
                        flag = in.readBoolean();
                        if(flag)
                        {
                            String[] roomInfo = gson.fromJson(in.readUTF(), String[].class);
                            chosenID = connectionManager.getApp().showRoomList(roomInfo);
                            if (chosenID != -1)
                            {
                                sendRoomJoinRequest(chosenID);
                                connectionManager.getApp().getChatPanel().addSystemEntry("Trying to join a room [" + chosenID + "] ...");
                            }
                        }
                        else
                            connectionManager.getApp().getChatPanel().addErrorEntry(in.readUTF());
                        break;
                    case 3: // room join response
                        flag = in.readBoolean();
                        if(flag)
                        {
                            connectionManager.getApp().getToolPanel().showRoomInfo(chosenID);
                            connectionManager.getApp().getPlayersPanel().makeVisible();
                            connectionManager.getApp().getChatPanel().addSystemEntry("Successfully joined");
                            connectedToARoom = true;
                        }
                        else
                            connectionManager.getApp().getChatPanel().addErrorEntry(in.readUTF());
                        break;
                    case 4: // room status update
                        String[] players = gson.fromJson(in.readUTF(), String[].class);
                        int[] points = gson.fromJson(in.readUTF(), int[].class);
                        connectionManager.getApp().getPlayersPanel().setPlayers(players, points);
                        break;
                    case 5: // message
                        String message = in.readUTF();
                        connectionManager.getApp().getChatPanel().addSystemEntry(message);
                        break;
                }
            }
        } catch (IOException e)
        {
            e.printStackTrace();
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

    public void sendRoomCreationRequest()
    {
        try
        {
            out.writeByte(1);
        } catch (IOException e)
        {
            e.printStackTrace();
        }
    }

    public void sendRoomViewRequest()
    {
        try
        {
            out.writeByte(2);
        } catch (IOException e)
        {
            e.printStackTrace();
        }
    }

    public void sendRoomJoinRequest(int roomID)
    {
        try
        {
            out.writeByte(3);
            out.writeInt(roomID);
        } catch (IOException e)
        {
            e.printStackTrace();
        }
    }
}
