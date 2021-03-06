package client.app.shapes;

import lombok.Getter;

import java.awt.*;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;

public class Pencil extends Shape
{
    private ArrayList<Line> lines = new ArrayList<>();

    public Pencil(Color color, int stroke)
    {
        super(color, stroke, Type.PENCIL);
    }

    public Pencil(Color color, int stroke, ArrayList<Line> lines)
    {
        this(color, stroke);
        this.lines = lines;
    }

    @Override
    public void draw(Graphics2D g)
    {
        g.setStroke(new BasicStroke(stroke));
        for(Line l : lines)
            l.draw(g);
    }

    public void setFinalPoint(Point point)
    {
        if(finalPoint != null && finalPoint != point)
            startingPoint = finalPoint;
        finalPoint = point;
        lines.add(new Line(color, startingPoint, finalPoint, stroke));
    }
}
