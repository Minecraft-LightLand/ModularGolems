package organize.sub;

import com.google.common.io.Files;
import organize.ResourceOrganizer;

import java.io.File;

public class DataMisc extends ResourceOrganizer {

    public DataMisc() {
        super(Type.DATA, "data", "");
    }

    @Override
    public void organize(File f) throws Exception {
        for (File fi : f.listFiles())
            process(fi, getResourceFolder(true) + type + "/");
    }

    private void process(File f, String pre) throws Exception {
        if (f.getName().startsWith("."))
            return;
        if (f.isDirectory()) {
            for (File fi : f.listFiles())
                process(fi, pre + f.getName() + "/");
        } else {
            File t = new File(pre + f.getName());
            check(t);
            Files.copy(f, t);
        }
    }
}
