package organize;

import javax.imageio.ImageIO;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;

public class TextureStitcher {

	public static void main(String[] args) throws IOException {
		File file = new File("./temp/in.png");
		BufferedImage img = ImageIO.read(file);
		BufferedImage out = new BufferedImage(128, 128, BufferedImage.TYPE_4BYTE_ABGR);
		for (int i = 0; i < 8; i++) {
			for (int j = 0; j < 8; j++) {
				out.getGraphics().drawImage(img, i * 16, j * 16, null);
			}
		}

		File write = new File("./temp/out.png");
		if (!write.exists()) write.createNewFile();
		ImageIO.write(out, "PNG", write);
	}

}
