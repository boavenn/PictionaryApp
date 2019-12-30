package client.app.net.sierialization;

import com.google.gson.*;

import java.awt.*;
import java.lang.reflect.Type;

public class PointDeserializer implements JsonDeserializer<Point>
{
    @Override
    public Point deserialize(JsonElement jsonElement, Type type, JsonDeserializationContext jsonDeserializationContext) throws JsonParseException
    {
        JsonObject obj = jsonElement.getAsJsonObject();
        int x = obj.get("x").getAsInt();
        int y = obj.get("y").getAsInt();
        return new Point(x, y);
    }
}
