package client.app;

import client.app.net.ConnectionManager;
import client.app.panels.*;
import lombok.Getter;

import javax.swing.*;
import java.awt.*;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;

public class App
{
    private JFrame frame;
    private final int WIDTH = 1024;
    private final int HEIGHT = 576;
    private final int GAP = 5;
    private final Color BG_COLOR = Color.BLACK;
    private final Color COMP_COLOR = Color.LIGHT_GRAY;
    private final String TITLE = "Pictionary";
    private final @Getter ToolPanel toolPanel;
    private final @Getter WordPanel wordPanel;
    private final @Getter PaintPanel paintPanel;
    private final @Getter PlayersPanel playersPanel;
    private final @Getter JoinCreatePanel joinCreatePanel;
    private final @Getter ChatPanel chatPanel;
    private final ConnectionManager connectionManager;

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
        frame.addWindowListener(new WindowAdapter()
        {
            @Override
            public void windowClosing(WindowEvent e)
            {
                if(connectionManager.isConnected())
                {
                    if(connectionManager.isConnectedToARoom())
                        connectionManager.sendRoomQuitMessage();
                    else
                        connectionManager.sendQuitMessage();
                }
            }
        });
        frame.setLayout(new FlowLayout(FlowLayout.CENTER, GAP, GAP));
        frame.setResizable(false);
        frame.getContentPane().setBackground(BG_COLOR);

        connectionManager = new ConnectionManager(this);

        int width = WIDTH * 15 / 100;
        Section left_section = new Section(width, HEIGHT);
        toolPanel = new ToolPanel(width, HEIGHT * 99 / 100, COMP_COLOR, Color.BLACK, connectionManager);
        left_section.add(toolPanel);

        width = WIDTH * 60 / 100;
        Section mid_section = new Section(width, HEIGHT);
        wordPanel = new WordPanel(width, HEIGHT * 15 / 100, COMP_COLOR, Color.BLACK, connectionManager);
        paintPanel = new PaintPanel(width, (HEIGHT - GAP) * 84 / 100, Color.WHITE, Color.BLACK, connectionManager);
        mid_section.add(wordPanel);
        mid_section.add(paintPanel);

        width = WIDTH * 20 / 100;
        Section right_section = new Section(width, HEIGHT);
        playersPanel = new PlayersPanel(width, HEIGHT * 20 / 100, COMP_COLOR, Color.BLACK, connectionManager);
        joinCreatePanel = new JoinCreatePanel(width, HEIGHT * 20 / 100, COMP_COLOR, Color.BLACK, connectionManager);
        chatPanel = new ChatPanel(width, (HEIGHT - GAP) * 79 / 100 - 1, COMP_COLOR, Color.BLACK, connectionManager);
        right_section.add(playersPanel);
        right_section.add(joinCreatePanel);
        right_section.add(chatPanel);

        toolPanel.setPaintPanel(paintPanel);
        joinCreatePanel.setPlayersPanel(playersPanel);
        joinCreatePanel.setPaintPanel(paintPanel);
        playersPanel.setJoinCreatePanel(joinCreatePanel);

        frame.add(left_section);
        frame.add(mid_section);
        frame.add(right_section);

        joinCreatePanel.makeVisible();

        frame.pack();
        frame.setLocationRelativeTo(null);
        frame.setVisible(true);
    }

    public int showRoomList(String[] list)
    {
        String temp = (String) JOptionPane.showInputDialog(null, "Choose a room to join",
                "Room list", JOptionPane.QUESTION_MESSAGE, null, list, list[0]);
        int id;
        if(temp != null)
            id = Integer.parseInt(temp.substring(0, temp.indexOf('.')));
        else
            id = -1;

        return id;
    }
}
