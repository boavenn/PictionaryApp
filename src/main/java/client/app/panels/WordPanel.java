package client.app.panels;

import client.app.net.ConnectionManager;

import java.awt.*;

public class WordPanel extends CustomPanel
{
    public WordPanel(int width, int height, Color bgColor, Color fgColor, ConnectionManager connectionManager)
    {
        super(width, height, bgColor, fgColor, connectionManager);
    }
}
