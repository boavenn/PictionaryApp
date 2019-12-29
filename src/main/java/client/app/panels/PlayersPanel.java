package client.app.panels;

import client.app.net.ConnectionManager;
import lombok.Getter;
import lombok.Setter;

import javax.swing.*;
import java.awt.*;
import java.util.ArrayList;

public class PlayersPanel extends CustomPanel
{
    private @Setter JoinCreatePanel joinCreatePanel;
    private ArrayList<JLabel> jlabels = new ArrayList<>();
    private JButton exit;

    public PlayersPanel(int width, int height, Color bgColor, Color fgColor, ConnectionManager connectionManager)
    {
        super(width, height, bgColor, fgColor, connectionManager);
        for(int i = 0; i < 4; i++)
        {
            jlabels.add(new JLabel(""));
            jlabels.get(i).setPreferredSize(new Dimension(width - 30, 15));
            this.add(jlabels.get(i));
        }
        exit = new JButton("EXIT ROOM");
        exit.setPreferredSize(new Dimension(width - 30, 20));
        exit.addActionListener(e ->
        {
            if(connectionManager.isConnected())
            {
                connectionManager.sendQuitMessage();
                joinCreatePanel.makeVisible();
            }
        });
        this.add(exit);
    }

    public void makeVisible()
    {
        setVisible(true);
        joinCreatePanel.setVisible(false);
    }

    public void setPlayers(String[] players, int[] points)
    {
        int i = 0;
        for(; i < players.length; i++)
            jlabels.get(i).setText((i + 1) + ". " + players[i] + "[" + points[i] + "]");
        while(i < 4)
        {
            jlabels.get(i).setText((i + 1) + ". ");
            i++;
        }
    }
}
