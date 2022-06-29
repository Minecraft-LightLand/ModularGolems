package organize.sub;

import com.google.common.collect.Maps;
import com.google.common.io.Files;
import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonParser;
import dev.xkmc.l2library.serial.codec.JsonCodec;
import organize.ResourceOrganizer;
import organize.json.JsonPart;

import java.io.File;
import java.io.FileReader;
import java.io.PrintStream;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class ConfigFileOrganizer extends ResourceOrganizer {

    public ConfigFileOrganizer() {
        super(Type.DATA, "difficulty_config", "difficulty_config/");
    }

    private Map<String, List<JsonPart>> parts;

    @Override
    public void organize(File f) throws Exception {
        parts = Maps.newLinkedHashMap();
        addParts(new File(f.getPath() + "/-parts/"));
        generate(new File(f.getPath() + "/-template"));
        process("", f, (name, file) -> {
            String fs = getTargetFolder() + name;
            File ti = new File(fs + ".json");
            check(ti);
            if (parts.containsKey(name)) {
                JsonElement je = new JsonParser().parse(new FileReader(file));
                parts.get(name).forEach(e -> e.add(je));
                PrintStream ps = new PrintStream(ti);
                ps.println(je.toString());
                ps.close();
            } else
                Files.copy(file, ti);
        }, false);
    }

    private void generate(File file) throws Exception {
        if (!file.exists())
            return;
        File info = new File(file.getPath() + "/-info.json");
        if (!info.exists())
            return;
        Map<String, String> map = new HashMap<>();
        process("", file, (name, f) -> {
            map.put(name, readFile(f.getPath()));
        }, false);
        JsonElement elem = new JsonParser().parse(new FileReader(info));
        for (Map.Entry<String, JsonElement> layer_0 : elem.getAsJsonObject().entrySet()) {
            List<Pair> list = new ArrayList<>();
            if (layer_0.getKey().startsWith("-")) {
                JsonArray arr = layer_0.getValue().getAsJsonObject().get("-list").getAsJsonArray();
                for (JsonElement e : arr) {
                    list.add(new Pair(e.getAsString(), map));
                }
            } else {
                list.add(new Pair(layer_0.getKey(), map));
            }
            for (Map.Entry<String, JsonElement> layer_1 : layer_0.getValue().getAsJsonObject().entrySet()) {
                String _name = layer_1.getKey();
                if (_name.startsWith("-"))
                    continue;
                for (Pair pair : list) {
                    String name = _name;
                    if (name.endsWith("_"))
                        name = name + pair.name;
                    else if (name.startsWith("_"))
                        name = pair.name + name;
                    else name = name + "_" + pair.name;
                    File dst = new File(getTargetFolder() + name + ".json");
                    check(dst);
                    String ans = pair.template;
                    for (Map.Entry<String, JsonElement> layer_2 : layer_1.getValue().getAsJsonObject().entrySet()) {
                        ans = ans.replaceAll("\\^" + layer_2.getKey(), layer_2.getValue().getAsString());
                    }
                    ans = ans.replaceAll("\\^m", MODID);
                    ans = ans.replaceAll("\\^n", _name);
                    PrintStream ps = new PrintStream(dst);
                    ps.println(ans);
                    ps.close();
                }
            }
        }
    }

    private void addParts(File f) throws Exception {
        if (f.isDirectory()) {
            for (File fi : f.listFiles()) {
                String file = fi.getName();
                if (file.startsWith("-") || file.startsWith("."))
                    continue;
                addParts(fi);
            }
            return;
        }
        JsonElement je = new JsonParser().parse(new FileReader(f));
        JsonPart part = JsonCodec.from(je, JsonPart.class, null);
        List<JsonPart> list = parts.getOrDefault(part.file, new ArrayList<>());
        list.add(part);
        parts.put(part.file, list);
    }

    private void process(String prefix, File f, ExcCons cons, boolean skip_dash) throws Exception {
        String filename = f.getName();
        if (skip_dash && filename.startsWith("-") || filename.startsWith("."))
            return;
        filename = f.isDirectory() ? filename : filename.split("\\.")[0];
        String name = filename.startsWith("_") ? prefix + filename : filename.endsWith("_") ? filename + prefix : filename;
        if (f.isDirectory()) {
            for (File fi : f.listFiles()) {
                String file = fi.getName();
                if (file.startsWith("-") || file.startsWith("."))
                    continue;
                process(name, fi, cons, true);
            }
            return;
        }
        cons.accept(name, f);
    }

    private static class Pair {

        private final String name;
        private final String template;

        private Pair(String name, Map<String, String> map) {
            this.name = name;
            this.template = map.get(name);
        }
    }

    private interface ExcCons {

        void accept(String name, File file) throws Exception;

    }

}
