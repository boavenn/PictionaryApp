package client.app.shapes;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.awt.*;

@Data
@AllArgsConstructor
@NoArgsConstructor
public abstract class Shape
{
    protected Color color;
    protected Point startingPoint;
    protected Point finalPoint;
    protected int stroke;
    protected Type type;

    public enum Type
    {
        RECTANGLE, ELLIPSE, LINE, PENCIL
    }

    public Shape(Color color, int stroke, Type type)
    {
        this.color = color;
        this.stroke = stroke;
        this.type = type;
    }

    public abstract void draw(Graphics2D g);

    public boolean isReady()
    {
        return startingPoint != null && finalPoint != null;
    }
}
