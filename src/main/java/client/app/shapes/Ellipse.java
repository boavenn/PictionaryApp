package client.app.shapes;

import java.awt.*;
import java.awt.geom.Ellipse2D;

public class Ellipse extends Shape
{
    public Ellipse(Color color, Point startingPoint, Point finalPoint, int stroke)
    {
        super(color, startingPoint, finalPoint, stroke, Type.ELLIPSE);
    }

    public Ellipse(Color color, int stroke)
    {
        super(color, stroke, Type.ELLIPSE);
    }

    @Override
    public void draw(Graphics2D g)
    {
        double x = Math.min(startingPoint.x, finalPoint.x);
        double y = Math.min(startingPoint.y, finalPoint.y);
        double w = Math.abs(startingPoint.x - finalPoint.x);
        double h = Math.abs(startingPoint.y - finalPoint.y);
        g.setStroke(new BasicStroke(stroke));
        g.draw(new Ellipse2D.Double(x, y, w, h));
    }
}
