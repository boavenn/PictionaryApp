package client.app.net;

import lombok.Getter;
import lombok.Setter;

import java.io.IOException;
import java.net.InetAddress;
import java.net.Socket;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;

public class ConnectionChecker implements Runnable
{
    private ConnectionManager connectionManager;
    private Socket socket;
    private @Getter ServerListener serverListener;
    private ExecutorService serverListenerExecutor = Executors.newSingleThreadExecutor();
    private Future<?> serverListenerStatus;
    private @Getter @Setter boolean done = false;
    private @Getter boolean connected = false;

    public ConnectionChecker(ConnectionManager connectionManager)
    {
        this.connectionManager = connectionManager;
    }

    @Override
    public void run()
    {
        try
        {
            while(!done)
            {
                Thread.sleep(1000);
                if (!connected)
                {
                    if (tryToConnect())
                    {
                        connected = true;
                        connectionManager.getApp().getToolPanel().changeConnectionStatus();
                        serverListener = new ServerListener(connectionManager, socket);
                        serverListenerStatus = serverListenerExecutor.submit(serverListener);
                    }
                } else
                {
                    if(serverListenerStatus.isDone())
                        connected = false;
                }
            }
        } catch (InterruptedException e)
        {
            e.printStackTrace();
        }
    }

    public boolean tryToConnect()
    {
        try
        {
            socket = new Socket(InetAddress.getByName(connectionManager.getSERVER_ADDRESS()), 999);
        } catch (IOException e)
        {
            return false;
        }
        return true;
    }

    public void disconnect()
    {
        connected = false;
        connectionManager.getApp().getToolPanel().changeConnectionStatus();
    }
}
