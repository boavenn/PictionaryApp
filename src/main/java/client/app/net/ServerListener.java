package client.app.net;

import client.app.net.sierialization.PointDeserializer;
import client.app.net.sierialization.PointSerializer;
import client.app.net.sierialization.ShapeDeserializer;
import client.app.shapes.Line;
import client.app.shapes.Pencil;
import client.app.shapes.Shape;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import lombok.Getter;
import lombok.Setter;

import java.awt.*;
import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.net.Socket;
import java.util.ArrayList;

public class ServerListener implements Runnable
{
    private DataOutputStream out;
    private DataInputStream in;
    private ConnectionManager connectionManager;
    private boolean connected = true;
    private @Setter @Getter boolean connectedToARoom = false;
    private @Getter boolean drawing = false;
    private Gson gson;
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

        GsonBuilder gb = new GsonBuilder();
        gb.registerTypeAdapter(Point.class, new PointSerializer());
        gb.registerTypeAdapter(Point.class, new PointDeserializer());
        gb.registerTypeAdapter(Shape.class, new ShapeDeserializer());
        gson = gb.create();
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
                    case 5: // server message
                        String message = in.readUTF();
                        connectionManager.getApp().getChatPanel().addSystemEntry(message);
                        break;
                    case 6: // text message
                        String who = in.readUTF();
                        message = in.readUTF();
                        connectionManager.getApp().getChatPanel().addUserEntry(who, message);
                        break;
                    case 7: // got new shape
                        int chunks = in.readInt();
                        StringBuilder str = new StringBuilder();
                        for(int i = 0; i < chunks; i++)
                            str.append(in.readUTF());
                        Shape shape = gson.fromJson(str.toString(), Shape.class);
                        connectionManager.getApp().getPaintPanel().addNewShape(shape);
                        break;
                    case 8: // undo request
                        connectionManager.getApp().getPaintPanel().undo();
                        break;
                    case 9: // redo request
                        connectionManager.getApp().getPaintPanel().redo();
                        break;
                    case 10: // clear request
                        connectionManager.getApp().getPaintPanel().clear();
                        break;
                    case 11: // drawing status
                        drawing = in.readBoolean();
                        String whoIsDrawing = in.readUTF();
                        connectionManager.getApp().getChatPanel().addSystemEntry("'" + whoIsDrawing + "' is drawing now.");
                        break;
                }
            }
        } catch (IOException e)
        {
            e.printStackTrace();
        }
    }

    public void sendTextMessage(String text)
    {
        try
        {
            out.writeByte(6);
            out.writeUTF(text);
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
            connectedToARoom = false;
        } catch (IOException e)
        {
            e.printStackTrace();
        }
    }

    public void sendRoomQuitMessage()
    {
        try
        {
            out.writeByte(1);
            connectedToARoom = false;
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

    public void sendNewShape(Shape shape)
    {
        final int limit = 10_000;
        try
        {
            out.writeByte(7);
            String str = gson.toJson(shape);
            int chunks = str.length() / limit + 1;
            out.writeInt(chunks);
            int j = 0;
            for(int i = 0; i < chunks - 1; i++, j++)
                out.writeUTF(str.substring(i * limit, (i + 1) * limit));
            out.writeUTF(str.substring(j * limit));
        } catch (IOException e)
        {
            e.printStackTrace();
        }
    }

    public void sendUndo()
    {
        try
        {
            out.writeByte(8);
        } catch (IOException e)
        {
            e.printStackTrace();
        }
    }

    public void sendRedo()
    {
        try
        {
            out.writeByte(9);
        } catch (IOException e)
        {
            e.printStackTrace();
        }
    }

    public void sendClear()
    {
        try
        {
            out.writeByte(10);
        } catch (IOException e)
        {
            e.printStackTrace();
        }
    }
}
