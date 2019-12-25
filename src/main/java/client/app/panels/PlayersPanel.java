package client.app.panels;

import lombok.Setter;

import java.awt.*;

public class PlayersPanel extends CustomPanel
{
    private @Setter JoinCreatePanel joinCreatePanel;

    public PlayersPanel(int width, int height, Color bgColor, Color fgColor)
    {
        super(width, height, bgColor, fgColor);
    }

    public void makeVisible()
    {
        setVisible(true);
        joinCreatePanel.setVisible(false);
    }
}
