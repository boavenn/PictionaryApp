package client.app.shapes;

import java.awt.*;
import java.awt.geom.Line2D;

public class Line extends Shape
{
    public Line(Color color, Point startingPoint, Point finalPoint, int stroke)
    {
        super(color, startingPoint, finalPoint, stroke, Type.LINE);
    }

    public Line(Color color, int stroke)
    {
        super(color, stroke, Type.LINE);
    }

    @Override
    public void draw(Graphics2D g)
    {
        g.setStroke(new BasicStroke(stroke));
        g.draw(new Line2D.Double(startingPoint, finalPoint));
    }
}
