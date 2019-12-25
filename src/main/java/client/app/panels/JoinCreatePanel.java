package client.app.panels;

import lombok.Setter;

import javax.swing.*;
import java.awt.*;

public class JoinCreatePanel extends CustomPanel
{
    private @Setter PlayersPanel playersPanel;
    private @Setter PaintPanel paintPanel;

    public JoinCreatePanel(int width, int height, Color bgColor, Color fgColor)
    {
        super(width, height, bgColor, fgColor);

        JButton create = new JButton("Create a room");
        create.setPreferredSize(new Dimension(width - 30, height / 2 - 10));
        create.setBackground(Color.WHITE);
        create.setFont(new Font("monospace", Font.BOLD, 16));
        create.setFocusPainted(false);
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
