package client.app.panels;

import client.app.net.ConnectionManager;
import client.app.shapes.Shape;
import client.app.shapes.Ellipse;
import client.app.shapes.Line;
import client.app.shapes.Pencil;
import client.app.shapes.Rectangle;
import lombok.Setter;

import javax.swing.*;
import javax.swing.event.PopupMenuEvent;
import javax.swing.event.PopupMenuListener;
import java.awt.*;

public class ToolPanel extends CustomPanel
{
    private @Setter PaintPanel paintPanel;
    private JColorChooser colorChooser;
    private int currentStroke = 1;
    private final int ICON_SIZE = 40;
    private JLabel connectionStatus;
    private JLabel roomID;

    public ToolPanel(int width, int height, Color bgColor, Color fgColor, ConnectionManager connectionManager)
    {
        super(width, height, bgColor, fgColor, connectionManager);

        colorChooser = new JColorChooser();
        colorChooser.setPreviewPanel(new JPanel());
        colorChooser.setColor(Color.BLACK);
        colorChooser.setBackground(Color.DARK_GRAY);
        colorChooser.setPreferredSize(new Dimension(600, 220));

        ButtonGroup tools = new ButtonGroup();
        JRadioButton rectangle = new JRadioButton();
        rectangle.setIcon(new ImageIcon("src/main/java/client/res/RectangleOff.png"));
        rectangle.setSelectedIcon(new ImageIcon("src/main/java/client/res/RectangleOn.png"));
        rectangle.setRolloverIcon(new ImageIcon("src/main/java/client/res/RectangleOn.png"));
        rectangle.addActionListener(e ->
        {
            if(connectionManager.isConnected() && connectionManager.isConnectedToARoom() && connectionManager.isDrawing())
                paintPanel.setCurrentShape(new Rectangle(colorChooser.getColor(), currentStroke));
        });
        rectangle.setBackground(bgColor);
        rectangle.setToolTipText("Rectangle");
        tools.add(rectangle);

        JRadioButton ellipse = new JRadioButton();
        ellipse.setIcon(new ImageIcon("src/main/java/client/res/EllipseOff.png"));
        ellipse.setSelectedIcon(new ImageIcon("src/main/java/client/res/EllipseOn.png"));
        ellipse.setRolloverIcon(new ImageIcon("src/main/java/client/res/EllipseOn.png"));
        ellipse.addActionListener(e ->
        {
            if(connectionManager.isConnected() && connectionManager.isConnectedToARoom() && connectionManager.isDrawing())
                paintPanel.setCurrentShape(new Ellipse(colorChooser.getColor(), currentStroke));
        });
        ellipse.setBackground(bgColor);
        ellipse.setToolTipText("Ellipse");
        tools.add(ellipse);

        JRadioButton line = new JRadioButton();
        line.setIcon(new ImageIcon("src/main/java/client/res/LineOff.png"));
        line.setSelectedIcon(new ImageIcon("src/main/java/client/res/LineOn.png"));
        line.setRolloverIcon(new ImageIcon("src/main/java/client/res/LineOn.png"));
        line.addActionListener(e ->
        {
            if(connectionManager.isConnected() && connectionManager.isConnectedToARoom() && connectionManager.isDrawing())
                paintPanel.setCurrentShape(new Line(colorChooser.getColor(), currentStroke));
        });
        line.setBackground(bgColor);
        line.setToolTipText("Line");
        tools.add(line);

        JRadioButton pencil = new JRadioButton();
        pencil.setIcon(new ImageIcon("src/main/java/client/res/PencilOff.png"));
        pencil.setSelectedIcon(new ImageIcon("src/main/java/client/res/PencilOn.png"));
        pencil.setRolloverIcon(new ImageIcon("src/main/java/client/res/PencilOn.png"));
        pencil.addActionListener(e ->
        {
            if(connectionManager.isConnected() && connectionManager.isConnectedToARoom() && connectionManager.isDrawing())
                paintPanel.setCurrentShape(new Pencil(colorChooser.getColor(), currentStroke));
        });
        pencil.setBackground(bgColor);
        pencil.setToolTipText("Pencil");
        tools.add(pencil);

        JPanel radioPanel = new JPanel();
        radioPanel.setLayout(new GridLayout(2, 2));
        radioPanel.add(rectangle);
        radioPanel.add(ellipse);
        radioPanel.add(line);
        radioPanel.add(pencil);
        this.add(radioPanel);

        JPopupMenu colors = new JPopupMenu();
        colors.add(colorChooser);
        colors.addPopupMenuListener(new PopupMenuListener()
        {
            @Override
            public void popupMenuWillBecomeVisible(PopupMenuEvent e) { }

            @Override
            public void popupMenuWillBecomeInvisible(PopupMenuEvent e) { }

            @Override
            public void popupMenuCanceled(PopupMenuEvent e)
            {
                if(connectionManager.isConnected() && connectionManager.isConnectedToARoom() && connectionManager.isDrawing())
                    paintPanel.setCurrentShapeColor(colorChooser.getColor());
            }
        });

        JButton colorsButton = new JButton("Choose color");
        colorsButton.addActionListener(e ->
        {
            if(connectionManager.isConnected() && connectionManager.isConnectedToARoom() && connectionManager.isDrawing())
                colors.show(colorsButton, colorsButton.getX(), colorsButton.getY() - 200);
        });
        colorsButton.setBackground(Color.WHITE);
        colorsButton.setFocusPainted(false);
        this.add(colorsButton);

        JPanel stroke_changer = new JPanel();
        stroke_changer.setBackground(bgColor);

        JSpinner spinner = new JSpinner(new SpinnerNumberModel(1, 1, 30, 1));
        JFormattedTextField temp = ((JSpinner.DefaultEditor) spinner.getEditor()).getTextField();
        temp.setEditable(false);
        spinner.addChangeListener(e ->
        {
            if(connectionManager.isConnected() && connectionManager.isConnectedToARoom() && connectionManager.isDrawing())
            {
                currentStroke = (int) spinner.getValue();
                paintPanel.setCurrentShapeStroke(currentStroke);
            }
        });

        JLabel label = new JLabel("Set stroke");
        label.setForeground(Color.BLACK);
        stroke_changer.add(label);
        stroke_changer.add(spinner);
        this.add(stroke_changer);

        JButton undo = new JButton();
        undo.setPreferredSize(new Dimension(ICON_SIZE, ICON_SIZE));
        undo.setIcon(new ImageIcon("src/main/java/client/res/Undo.png"));
        undo.addActionListener(e ->
        {
            if(connectionManager.isConnected() && connectionManager.isConnectedToARoom() && connectionManager.isDrawing())
                paintPanel.undo();
        });
        undo.setBackground(Color.WHITE);
        undo.setToolTipText("Undo");
        undo.setFocusPainted(false);
        this.add(undo);

        JButton redo = new JButton();
        redo.setPreferredSize(new Dimension(ICON_SIZE, ICON_SIZE));
        redo.setIcon(new ImageIcon("src/main/java/client/res/Redo.png"));
        redo.addActionListener(e ->
        {
            if(connectionManager.isConnected() && connectionManager.isConnectedToARoom() && connectionManager.isDrawing())
                paintPanel.redo();
        });
        redo.setBackground(Color.WHITE);
        redo.setToolTipText("Redo");
        redo.setFocusPainted(false);
        this.add(redo);

        JButton clear = new JButton();
        clear.setPreferredSize(new Dimension(ICON_SIZE, ICON_SIZE));
        clear.setIcon(new ImageIcon("src/main/java/client/res/Clear.png"));
        clear.addActionListener(e ->
        {
            if(connectionManager.isConnected() && connectionManager.isConnectedToARoom() && connectionManager.isDrawing())
                paintPanel.clear();
        });
        clear.setBackground(Color.WHITE);
        clear.setToolTipText("Clear");
        clear.setFocusPainted(false);
        this.add(clear);

        connectionStatus = new JLabel("No connection");
        connectionStatus.setForeground(Color.RED);
        connectionStatus.setPreferredSize(new Dimension(150, 20));
        connectionStatus.setHorizontalAlignment(SwingConstants.CENTER);
        this.add(connectionStatus);

        roomID = new JLabel();
        roomID.setVisible(false);
        roomID.setPreferredSize(new Dimension(150, 20));
        roomID.setHorizontalAlignment(SwingConstants.CENTER);
        this.add(roomID);
    }

    public void changeConnectionStatus()
    {
        if(connectionStatus.getForeground().equals(Color.RED))
        {
            connectionStatus.setText("Connected");
            connectionStatus.setForeground(Color.GREEN);
        } else
        {
            connectionStatus.setText("No connection");
            connectionStatus.setForeground(Color.RED);
        }
    }

    public void showRoomInfo(int id)
    {
        roomID.setVisible(true);
        roomID.setText("Room ID = " + id);
    }

    public void hideRoomInfo()
    {
        roomID.setVisible(false);
    }
}
