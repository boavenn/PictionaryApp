package client.app.panels;

import client.app.net.ConnectionManager;
import lombok.Setter;

import javax.swing.*;
import java.awt.*;

public class JoinCreatePanel extends CustomPanel
{
    private @Setter PlayersPanel playersPanel;
    private @Setter PaintPanel paintPanel;

    public JoinCreatePanel(int width, int height, Color bgColor, Color fgColor, ConnectionManager connectionManager)
    {
        super(width, height, bgColor, fgColor, connectionManager);

        JButton create = new JButton("Create a room");
        create.setPreferredSize(new Dimension(width - 30, height / 2 - 10));
        create.setBackground(Color.WHITE);
        create.setFont(new Font("monospace", Font.BOLD, 16));
        create.setFocusPainted(false);
        create.addActionListener(e ->
        {
            if(connectionManager.isConnected())
                connectionManager.sendRoomCreationRequest();
        });
        this.add(create);

        JButton join = new JButton("Join a room");
        join.setPreferredSize(new Dimension(width - 30, height / 2 - 10));
        join.setBackground(Color.WHITE);
        join.setFont(new Font("monospace", Font.BOLD, 16));
        join.setFocusPainted(false);
        this.add(join);
    }

    public void makeVisible()
    {
        setVisible(true);
        playersPanel.setVisible(false);
    }
}
