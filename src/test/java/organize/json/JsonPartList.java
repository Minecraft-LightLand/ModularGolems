package organize.json;

import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import dev.xkmc.l2library.serial.SerialClass;

@SerialClass
public class JsonPartList extends JsonPart {

    @SerialClass.SerialField
    public JsonElement list;

    @SerialClass.SerialField
    public JsonElement common;

    @Override
    public void inject(JsonElement elem) {
        JsonArray dst = elem.getAsJsonArray();
        list.getAsJsonArray().forEach(e0 -> {
            dst.add(e0);
            common.getAsJsonObject().entrySet().forEach(e -> {
                JsonObject obj = e0.getAsJsonObject();
                if (!obj.has(e.getKey()))
                    obj.add(e.getKey(), e.getValue());
            });
        });
    }
}
