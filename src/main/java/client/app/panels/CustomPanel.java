package client.app.panels;

import client.app.net.ConnectionManager;

import javax.swing.*;
import java.awt.*;

public abstract class CustomPanel extends JPanel
{
    protected ConnectionManager connectionManager;

    public CustomPanel(int width, int height, Color bgColor, Color fgColor)
    {
        this.setPreferredSize(new Dimension(width, height));
        this.setBackground(bgColor);
        this.setForeground(fgColor);
        this.setBorder(BorderFactory.createLineBorder(Color.BLACK, 2));
    }
}
