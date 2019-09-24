package filters;

import java.awt.Color;
import java.awt.image.BufferedImage;
import java.io.IOException;

import javax.imageio.ImageIO;
import javax.swing.JFileChooser;

public class imageloader {
	BufferedImage subject;
	int[][] red;
	int[][] green;
	int[][] blue;
	Color[][] palette;

	public imageloader() {
		try {
			JFileChooser j = new JFileChooser();
			j.showOpenDialog(null);
			if (j.isVisible() == false) {
				System.exit(0);
			}
			subject = ImageIO.read(j.getSelectedFile());
			red = new int[subject.getWidth()][subject.getHeight()];
			green = new int[subject.getWidth()][subject.getHeight()];
			blue = new int[subject.getWidth()][subject.getHeight()];
			palette = new Color[subject.getWidth()][subject.getHeight()];
			for (int x = 0; x < subject.getWidth(); x++) {
				for (int y = 0; y < subject.getHeight(); y++) {
					red[x][y]= new Color(subject.getRGB(x, y)).getRed();
					blue[x][y]= new Color(subject.getRGB(x, y)).getBlue();
					green[x][y]= new Color(subject.getRGB(x, y)).getGreen();
					palette[x][y]= new Color(subject.getRGB(x, y));
				}
			}
		} catch (IllegalArgumentException e) {
		} catch (IOException e) {
		}
	}

	public void write(BufferedImage out) {
		JFileChooser j = new JFileChooser();
		j.showSaveDialog(null);
		try {
			ImageIO.write(out, "png", j.getSelectedFile());
		} catch (IOException e) {
		}
	}
}
