package client.app.net.sierialization;

import com.google.gson.*;

import java.awt.*;
import java.lang.reflect.Type;

public class PointSerializer implements JsonSerializer<Point>
{
    @Override
    public JsonElement serialize(Point point, Type type, JsonSerializationContext jsonSerializationContext)
    {
        JsonObject obj = new JsonObject();
        obj.addProperty("x", point.x);
        obj.addProperty("y", point.y);
        return obj;
    }
}
