package client.app;

import client.app.panels.*;

import javax.swing.*;
import java.awt.*;

public class App
{
    private JFrame frame;
    private final int WIDTH = 1024;
    private final int HEIGHT = 576;
    private final int GAP = 5;
    private final Color BG_COLOR = Color.BLACK;
    private final Color COMP_COLOR = Color.LIGHT_GRAY;
    private final String TITLE = "Pictionary";
    private final ToolPanel toolPanel;
    private final WordPanel wordPanel;
    private final PaintPanel paintPanel;
    private final PlayersPanel playersPanel;
    private final ChatPanel chatPanel;

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

        int width = WIDTH * 60 / 100;
        Section mid_section = new Section(width, HEIGHT);
        wordPanel = new WordPanel(width, HEIGHT * 15 / 100, COMP_COLOR, Color.BLACK);
        paintPanel = new PaintPanel(width, (HEIGHT - GAP) * 84 / 100, Color.WHITE, Color.BLACK);
        mid_section.add(wordPanel);
        mid_section.add(paintPanel);

        width = WIDTH * 15 / 100;
        Section left_section = new Section(width, HEIGHT);
        toolPanel = new ToolPanel(width, HEIGHT * 99 / 100, COMP_COLOR, Color.BLACK, paintPanel);
        left_section.add(toolPanel);

        width = WIDTH * 20 / 100;
        Section right_section = new Section(width, HEIGHT);
        playersPanel = new PlayersPanel(width, HEIGHT * 20 / 100, COMP_COLOR, Color.BLACK);
        chatPanel = new ChatPanel(width, (HEIGHT - GAP) * 79 / 100 - 1, COMP_COLOR, Color.BLACK);
        right_section.add(playersPanel);
        right_section.add(chatPanel);

        frame.add(left_section);
        frame.add(mid_section);
        frame.add(right_section);

        frame.pack();
        frame.setLocationRelativeTo(null);
        frame.setVisible(true);
    }
}
