package client.app.panels;

import client.app.util.ConnectionManager;

import javax.swing.*;
import java.awt.*;

public abstract class CustomPanel extends JPanel
{
    protected ConnectionManager connection_manager;

    public CustomPanel(int width, int height, Color bg_color, Color fg_color)
    {
        this.setPreferredSize(new Dimension(width, height));
        this.setBackground(bg_color);
        this.setForeground(fg_color);
        this.setBorder(BorderFactory.createLineBorder(Color.BLACK, 2));
    }
}
