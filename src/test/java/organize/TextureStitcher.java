package organize;

import javax.imageio.ImageIO;
import java.awt.*;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;

public class TextureStitcher {

	public static void main(String[] args) throws IOException {
		File colors = new File("./temp/col/");
		File templates = new File("./temp/template/");
		for (File col : colors.listFiles()) {
			for (File tpl : templates.listFiles()) {
				String colName = col.getName().split("\\.")[0];
				String tplName = tpl.getName().split("\\.")[0];
				File write = new File("./temp/out/" + tplName + "/" + colName + ".png");
				draw(col, tpl, write);
			}
		}

	}

	private static void draw(File colorFile, File templateFile, File write) throws IOException {
		BufferedImage colorImg = ImageIO.read(colorFile);
		BufferedImage templateImg = ImageIO.read(templateFile);
		BufferedImage out = new BufferedImage(templateImg.getWidth(), templateImg.getHeight(), BufferedImage.TYPE_4BYTE_ABGR);
		float r = 0, g = 0, b = 0, n = 0;

		for (int i = 0; i < colorImg.getWidth(); i++) {
			for (int j = 0; j < colorImg.getHeight(); j++) {
				int col = colorImg.getRGB(i, j);
				Color color = new Color(col);
				float a = color.getAlpha() / 256f;
				r += color.getRed() * a / 256;
				g += color.getGreen() * a / 256;
				b += color.getBlue() * a / 256;
				n += a;
			}
		}
		float[] data = new float[3];
		Color.RGBtoHSB(Math.round(r / n * 256), Math.round(g / n * 256), Math.round(b / n * 256), data);
		int mix = Color.HSBtoRGB(data[0], data[1], 1);
		Color mixCol = new Color(mix);
		r = mixCol.getRed() / 256f;
		g = mixCol.getGreen() / 256f;
		b = mixCol.getBlue() / 256f;


		for (int i = 0; i < templateImg.getWidth(); i++) {
			for (int j = 0; j < templateImg.getHeight(); j++) {
				int col = templateImg.getRGB(i, j);
				Color color = new Color(col);
				if ((col & 0xff000000) == 0) {
					out.setRGB(i, j, col);
					continue;
				}
				Color next = new Color(Math.round(color.getRed() * r),
						Math.round(color.getGreen() * g),
						Math.round(color.getBlue() * b));
				col = next.getRGB();
				out.setRGB(i, j, col);
			}
		}

		if (!write.getParentFile().exists()) write.getParentFile().mkdirs();
		if (!write.exists()) write.createNewFile();
		ImageIO.write(out, "PNG", write);
	}

}
