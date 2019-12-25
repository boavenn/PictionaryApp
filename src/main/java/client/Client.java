package client;

import client.app.App;

import javax.swing.*;

public class Client
{
    public static void main(String[] args)
    {
        SwingUtilities.invokeLater(new Runnable()
        {
            @Override
            public void run()
            {
                new App();
            }
        });
    }
}
