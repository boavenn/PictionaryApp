package client.app.net;

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
}
