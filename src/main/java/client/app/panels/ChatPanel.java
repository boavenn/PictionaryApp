package client.app.panels;

import client.app.util.ChatEntry;
import lombok.Getter;
import lombok.Setter;

import javax.swing.*;
import java.awt.*;

public class ChatPanel extends CustomPanel
{
    private DefaultListModel<ChatEntry> listModel;

    public ChatPanel(int width, int height, Color bgColor, Color fgColor)
    {
        super(width, height, bgColor, fgColor);
        ChatEntry.setEntryWidth(width - 65);

        listModel = new DefaultListModel<>();
        JList<ChatEntry> chat = new JList<>(listModel);
        chat.setFont(new Font("monospace", Font.BOLD, 12));
        chat.setCellRenderer(new MyCellRenderer(ChatEntry.getEntryWidth()));

        JScrollPane scroll_pane = new JScrollPane(chat);
        scroll_pane.setPreferredSize(new Dimension(width - 20, height - 40 ));
        scroll_pane.setBorder(BorderFactory.createLineBorder(Color.BLACK, 1));
        this.add(scroll_pane);

        JTextField input_area = new JTextField();
        input_area.setPreferredSize(new Dimension(width - 20, 20));
        input_area.setBorder(BorderFactory.createLineBorder(Color.BLACK, 1));
        input_area.addActionListener(e ->
        {
            if(!input_area.getText().isEmpty())
            {
                listModel.addElement(new ChatEntry("#you", input_area.getText(), ChatEntry.Type.USER));
                input_area.setText("");
            }
        });
        this.add(input_area);
    }

    public static class MyCellRenderer extends DefaultListCellRenderer
    {
        private static final String HTML_1 = "<html><body style='width: ";
        private static final String HTML_2 = "px;";
        private static final String HTML_3 = "color:";
        private static final String HTML_4 = "'>";
        private static final String HTML_5 = "</html>";
        private static @Setter String color;
        private int width;

        public MyCellRenderer(int width)
        {
            this.width = width;
        }

        @Override
        public Component getListCellRendererComponent(JList<?> list, Object value, int index, boolean isSelected, boolean cellHasFocus)
        {
            String text = HTML_1 + width + HTML_2 + HTML_3 + color + HTML_4 + value.toString() + HTML_5;
            return super.getListCellRendererComponent(list, text, index, isSelected, cellHasFocus);
        }
    }
}
