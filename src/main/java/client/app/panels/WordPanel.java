package client.app.panels;

import client.app.net.ConnectionManager;
import lombok.Getter;

import javax.swing.*;
import java.awt.*;

public class WordPanel extends CustomPanel
{
    private @Getter JLabel jLabel;

    public WordPanel(int width, int height, Color bgColor, Color fgColor, ConnectionManager connectionManager)
    {
        super(width, height, bgColor, fgColor, connectionManager);
        jLabel = new JLabel("Join a room or create one");
        jLabel.setPreferredSize(new Dimension(width - 20, height - 20));
        jLabel.setHorizontalAlignment(SwingConstants.CENTER);
        jLabel.setFont(new Font("monospace", Font.BOLD, 32));
        this.add(jLabel);
    }
}
