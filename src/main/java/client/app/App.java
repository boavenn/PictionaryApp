package client.app;

import client.app.panels.*;

import javax.swing.*;
import javax.tools.Tool;
import java.awt.*;

public class App
{
    private JFrame frame;
    private final int WIDTH = 1024;
    private final int HEIGHT = 576;
    private final int GAP = 5;
    private final Color BG_COLOR = Color.WHITE;
    private final String TITLE = "Pictionary";
    private final ToolPanel tool_panel;
    private final WordPanel word_panel;
    private final PaintPanel paint_panel;
    private final PlayersPanel players_panel;
    private final ChatPanel chat_panel;

    private class Section extends JPanel
    {
        Section(int width, int height)
        {
            setPreferredSize(new Dimension(width, height));
            setLayout(new FlowLayout(FlowLayout.CENTER, GAP, GAP));
            setBackground(BG_COLOR);
        }
    }

    public App()
    {
        frame = new JFrame(TITLE);
        frame.setSize(new Dimension(WIDTH, HEIGHT));
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        frame.setLayout(new FlowLayout(FlowLayout.CENTER, GAP, GAP));
        frame.setResizable(false);
        frame.getContentPane().setBackground(BG_COLOR);

        int width = WIDTH * 15 / 100;
        Section left_section = new Section(width, HEIGHT);
        tool_panel = new ToolPanel(width, HEIGHT * 99 / 100, Color.LIGHT_GRAY, Color.BLACK);
        left_section.add(tool_panel);
        frame.add(left_section);

        width = WIDTH * 60 / 100;
        Section mid_section = new Section(width, HEIGHT);
        word_panel = new WordPanel(width, HEIGHT * 15 / 100, Color.LIGHT_GRAY, Color.BLACK);
        paint_panel = new PaintPanel(width, (HEIGHT - GAP) * 84 / 100, Color.LIGHT_GRAY, Color.BLACK);
        mid_section.add(word_panel);
        mid_section.add(paint_panel);
        frame.add(mid_section);

        width = WIDTH * 20 / 100;
        Section right_section = new Section(width, HEIGHT);
        players_panel = new PlayersPanel(width, HEIGHT * 20 / 100, Color.LIGHT_GRAY, Color.BLACK);
        chat_panel = new ChatPanel(width, (HEIGHT - GAP) * 79 / 100 - 1, Color.LIGHT_GRAY, Color.BLACK);
        right_section.add(players_panel);
        right_section.add(chat_panel);
        frame.add(right_section);

        frame.pack();
        frame.setVisible(true);
    }
}
