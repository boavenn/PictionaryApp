package client.app.util;

import client.app.panels.ChatPanel;
import lombok.Getter;
import lombok.Setter;

import javax.swing.*;
import java.awt.*;

public class ChatEntry extends JLabel
{
    private static @Setter @Getter int entryWidth;

    public enum Type
    {
        USER("black"),
        SYSTEM("blue"),
        ERROR("red");

        private final @Getter String color;

        Type(String color)
        {
            this.color = color;
        }
    }

    public ChatEntry(String who, String text, Type type)
    {
        this.setPreferredSize(new Dimension(entryWidth, 20));
        ChatPanel.MyCellRenderer.setColor(type.getColor());
        this.setText(who + ": " + text);
    }

    public String toString()
    {
        return this.getText();
    }
}
