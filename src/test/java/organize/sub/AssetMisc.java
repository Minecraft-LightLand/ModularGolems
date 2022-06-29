package organize.sub;

import com.google.common.io.Files;
import organize.ResourceOrganizer;

import java.io.File;

public class AssetMisc extends ResourceOrganizer {

	public AssetMisc() {
		super(Type.ASSETS, "assets", "");
	}

	@Override
	public void organize(File f) throws Exception {
		for (File fi : f.listFiles())
			process(fi, getTargetFolder(), "");
	}

	private void process(File f, String path, String pre) throws Exception {
		if (f.getName().startsWith("."))
			return;
		if (f.isDirectory()) {
			for (File fi : f.listFiles()) {
				String next = f.getName().startsWith("-") || f.getName().startsWith("@") ? path : path + f.getName() + "/";
				String npre = f.getName().startsWith("_") ? pre + f.getName() :
						f.getName().endsWith("_") ? f.getName() + pre :
								f.getName().startsWith("@") ? f.getName().substring(1)
										: pre;
				process(fi, next, npre);
			}
		} else {
			File t = new File(path + pre + f.getName());
			check(t);
			Files.copy(f, t);
		}
	}
}
