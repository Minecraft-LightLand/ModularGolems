package organize.json;

import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import dev.xkmc.l2library.serial.SerialClass;

@SerialClass
public class JsonPartMap extends JsonPart {

    @SerialClass.SerialField
    public JsonElement map;

    @SerialClass.SerialField
    public JsonElement common;

    @Override
    public void inject(JsonElement elem) {
        JsonObject dst = elem.getAsJsonObject();
        map.getAsJsonObject().entrySet().forEach(ent -> {
            dst.add(ent.getKey(), ent.getValue());
            if (common != null) {
                common.getAsJsonObject().entrySet().forEach(e -> {
                    JsonObject obj = ent.getValue().getAsJsonObject();
                    if (!obj.has(e.getKey()))
                        obj.add(e.getKey(), e.getValue());
                });
            }
        });
    }
}
