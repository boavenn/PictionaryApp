package client.app.net.sierialization;

import client.app.shapes.Ellipse;
import client.app.shapes.Line;
import client.app.shapes.Pencil;
import client.app.shapes.Rectangle;
import client.app.shapes.Shape;
import com.google.gson.*;
import com.google.gson.reflect.TypeToken;

import java.awt.*;
import java.lang.reflect.Type;
import java.util.ArrayList;

public class ShapeDeserializer implements JsonDeserializer<Shape>
{
    @Override
    public Shape deserialize(JsonElement jsonElement, Type type, JsonDeserializationContext jsonDeserializationContext) throws JsonParseException
    {
        JsonObject obj = jsonElement.getAsJsonObject();
        Color color = jsonDeserializationContext.deserialize(obj.get("color"), Color.class);
        Point startingPoint = jsonDeserializationContext.deserialize(obj.get("startingPoint"), Point.class);
        Point finalPoint = jsonDeserializationContext.deserialize(obj.get("finalPoint"), Point.class);
        int stroke = obj.get("stroke").getAsInt();
        String shapeType = obj.get("type").getAsString();

        switch (shapeType)
        {
            case "PENCIL":
                Type listType = new TypeToken<ArrayList<Line>>(){}.getType();
                ArrayList<Line> lines = jsonDeserializationContext.deserialize(obj.get("lines"), listType);
                return new Pencil(color, stroke, lines);
            case "LINE":
                return new Line(color, startingPoint, finalPoint, stroke);
            case "ELLIPSE":
                return new Ellipse(color, startingPoint, finalPoint, stroke);
            case "RECTANGLE":
                return new Rectangle(color, startingPoint, finalPoint, stroke);
            default:
                throw new RuntimeException("Unknown class");
        }
    }
}
