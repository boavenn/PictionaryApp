package client.app.shapes;

import java.awt.*;
import java.awt.geom.Rectangle2D;

public class Rectangle extends Shape
{
    public Rectangle(Color color, Point startingPoint, Point finalPoint, int stroke)
    {
        super(color, startingPoint, finalPoint, stroke, Type.RECTANGLE);
    }

    public Rectangle(Color color, int stroke)
    {
        super(color, stroke, Type.RECTANGLE);
    }

    @Override
    public void draw(Graphics2D g)
    {
        double x = Math.min(startingPoint.x, finalPoint.x);
        double y = Math.min(startingPoint.y, finalPoint.y);
        double w = Math.abs(startingPoint.x - finalPoint.x);
        double h = Math.abs(startingPoint.y - finalPoint.y);
        g.setStroke(new BasicStroke(stroke));
        g.draw(new Rectangle2D.Double(x, y, w, h));
    }
}
