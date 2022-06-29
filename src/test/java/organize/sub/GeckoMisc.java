package organize.sub;

import com.google.common.io.Files;
import organize.ResourceOrganizer;

import java.io.File;

public class GeckoMisc extends ResourceOrganizer {

	public GeckoMisc() {
		super(Type.ASSETS, "gecko", "");
	}

	@Override
	public void organize(File f) throws Exception {
		for (File fi : f.listFiles())
			process(fi);
	}

	private void process(File f) throws Exception {
		if (f.getName().startsWith("."))
			return;
		if (f.isDirectory()) {
			for (File fi : f.listFiles()) {
				process(fi);
			}
		} else {
			String name = f.getName();
			String path = getTargetFolder();
			if (name.endsWith("animation.json"))
				path += "animations/";
			else if (name.endsWith("geo.json"))
				path += "geo/";
			else path += "textures/gecko/";
			File t = new File(path + f.getName());
			check(t);
			Files.copy(f, t);
		}
	}
}
