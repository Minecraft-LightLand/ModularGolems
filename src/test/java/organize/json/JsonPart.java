package organize.json;

import com.google.gson.JsonElement;
import dev.xkmc.l2library.serial.SerialClass;

@SerialClass
public abstract class JsonPart {

    @SerialClass.SerialField
    public String file;
    @SerialClass.SerialField
    public String path;

    public abstract void inject(JsonElement elem);

    public void add(JsonElement je) {
        String[] keys = path.split("\\.");
        for (String key : keys) {
            je = je.getAsJsonObject().get(key);
        }
        inject(je);
    }
}
