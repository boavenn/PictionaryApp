package client.app.panels;

import client.app.net.ConnectionManager;
import client.app.shapes.Shape;
import client.app.shapes.Ellipse;
import client.app.shapes.Line;
import client.app.shapes.Pencil;
import client.app.shapes.Rectangle;
import lombok.Setter;

import javax.swing.*;
import java.awt.*;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.awt.image.BufferedImage;
import java.util.ArrayList;

public class PaintPanel extends CustomPanel
{
    private ArrayList<Shape> shapes = new ArrayList<>();
    private ArrayList<Shape> removedShapes = new ArrayList<>();
    private @Setter Shape currentShape;
    private BufferedImage img;
    private final int IMG_WIDTH;
    private final int IMG_HEIGHT;

    public PaintPanel(int width, int height, Color bgColor, Color fgColor, ConnectionManager connectionManager)
    {
        super(width, height, bgColor, fgColor, connectionManager);
        IMG_WIDTH = getPreferredSize().width;
        IMG_HEIGHT = getPreferredSize().height;

        img = new BufferedImage(IMG_WIDTH, IMG_HEIGHT, BufferedImage.TYPE_INT_RGB);
        this.setBorder(BorderFactory.createLineBorder(Color.BLACK, 2));
        this.setPreferredSize(new Dimension(IMG_WIDTH, IMG_HEIGHT));
        this.setBackground(Color.GRAY);

        addMouseMotionListener(new MouseAdapter()
        {
            @Override
            public void mouseDragged(MouseEvent e)
            {
                if(currentShape != null)
                {
                    currentShape.setFinalPoint(new Point(e.getX(), e.getY()));
                    repaint();
                }
            }
        });

        addMouseListener(new MouseAdapter()
        {
            @Override
            public void mousePressed(MouseEvent e)
            {
                if(currentShape != null && e.getButton() == 1)
                {
                    currentShape.setStartingPoint(new Point(e.getX(), e.getY()));
                    currentShape.setFinalPoint(new Point(e.getX(), e.getY()));
                }
            }

            @Override
            public void mouseReleased(MouseEvent e)
            {
                if(currentShape != null && e.getButton() == 1)
                {
                    shapes.add(currentShape);
                    repaint();
                    reuse(currentShape);
                }
            }
        });
    }

    protected void paintComponent(Graphics g)
    {
        super.paintComponent(g);
        Graphics2D g2d = img.createGraphics();
        g2d.setColor(Color.WHITE);
        g2d.fillRect(0, 0, IMG_WIDTH, IMG_HEIGHT);

        for (Shape s : shapes)
        {
            g2d.setColor(s.getColor());
            s.draw(g2d);
        }

        if (currentShape != null && currentShape.isReady())
        {
            g2d.setColor(currentShape.getColor());
            currentShape.draw(g2d);
        }

        g.drawImage(img, 0, 0, null);
    }

//    public void setShapes(ArrayList<Shape> shapes)
//    {
//        this.shapes = shapes;
//        repaint();
//    }

    public void clear()
    {
        if(!shapes.isEmpty())
        {
            shapes.clear();
            repaint();
        }

        if(!removedShapes.isEmpty())
        {
            removedShapes.clear();
        }
    }

    public void undo()
    {
        if(!shapes.isEmpty())
        {
            removedShapes.add(shapes.get(shapes.size() - 1));
            shapes.remove(shapes.size() - 1);
            repaint();
        }
    }

    public void redo()
    {
        if(!removedShapes.isEmpty())
        {
            shapes.add(removedShapes.get(removedShapes.size() - 1));
            removedShapes.remove(removedShapes.get(removedShapes.size() - 1));
            repaint();
        }
    }

    private void reuse(Shape shape)
    {
        if(shape instanceof Rectangle)
            currentShape = new Rectangle(shape.getColor(), shape.getStroke());
        else if(shape instanceof Ellipse)
            currentShape = new Ellipse(shape.getColor(), shape.getStroke());
        else if(shape instanceof Line)
            currentShape = new Line(shape.getColor(), shape.getStroke());
        else if(shape instanceof Pencil)
            currentShape = new Pencil(shape.getColor(), shape.getStroke());
    }

    public void setCurrentShapeColor(Color color)
    {
        if(currentShape != null)
            currentShape.setColor(color);
    }

    public void setCurrentShapeStroke(int stroke)
    {
        if(currentShape != null)
            currentShape.setStroke(stroke);
    }
}
