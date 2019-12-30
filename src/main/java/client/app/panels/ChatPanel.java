package client.app.panels;

import client.app.net.ConnectionManager;
import client.app.util.ChatEntry;
import lombok.Setter;

import javax.swing.*;
import java.awt.*;

public class ChatPanel extends CustomPanel
{
    private DefaultListModel<ChatEntry> listModel;

    public ChatPanel(int width, int height, Color bgColor, Color fgColor, ConnectionManager connectionManager)
    {
        super(width, height, bgColor, fgColor, connectionManager);
        ChatEntry.setEntryWidth(width - 65);

        listModel = new DefaultListModel<>();
        JList<ChatEntry> chat = new JList<>(listModel);
        chat.setFont(new Font("monospace", Font.BOLD, 12));
        chat.setCellRenderer(new MyCellRenderer(ChatEntry.getEntryWidth()));

        JScrollPane scrollPane = new JScrollPane(chat);
        scrollPane.setPreferredSize(new Dimension(width - 20, height - 40 ));
        scrollPane.setBorder(BorderFactory.createLineBorder(Color.BLACK, 1));
        this.add(scrollPane);

        JTextField input_area = new JTextField();
        input_area.setPreferredSize(new Dimension(width - 20, 20));
        input_area.setBorder(BorderFactory.createLineBorder(Color.BLACK, 1));
        input_area.addActionListener(e ->
        {
            if(!input_area.getText().isEmpty())
            {
                String text = input_area.getText();
                listModel.addElement(new ChatEntry(">>", text));
                input_area.setText("");
                if(connectionManager.isConnected() && connectionManager.isConnectedToARoom())
                    connectionManager.sendTextMessage(text);
            }
        });
        this.add(input_area);
    }

    public void addSystemEntry(String text)
    {
        listModel.addElement(new ChatEntry("~SYSTEM", text));
    }

    public void addErrorEntry(String text)
    {
        listModel.addElement(new ChatEntry("~ERROR", text));
    }

    public void addUserEntry(String who, String text)
    {
        listModel.addElement(new ChatEntry("#" + who, text));
    }

    public static class MyCellRenderer extends DefaultListCellRenderer
    {
        private static final String HTML_1 = "<html><body style='width: ";
        private static final String HTML_2 = "px'>";
        private static final String HTML_3 = "</html>";
        private int width;

        public MyCellRenderer(int width)
        {
            this.width = width;
        }

        @Override
        public Component getListCellRendererComponent(JList<?> list, Object value, int index, boolean isSelected, boolean cellHasFocus)
        {
            String text = HTML_1 + width + HTML_2 + value.toString() + HTML_3;
            return super.getListCellRendererComponent(list, text, index, isSelected, cellHasFocus);
        }
    }
}
