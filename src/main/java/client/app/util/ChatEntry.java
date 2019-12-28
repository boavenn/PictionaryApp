package client.app.util;

import client.app.panels.ChatPanel;
import lombok.Getter;
import lombok.Setter;

import javax.swing.*;
import java.awt.*;

public class ChatEntry extends JLabel
{
    private static @Setter @Getter int entryWidth;

    public ChatEntry(String who, String text)
    {
        this.setPreferredSize(new Dimension(entryWidth, 20));
        this.setText(who + ": " + text);
    }

    public String toString()
    {
        return this.getText();
    }
}
