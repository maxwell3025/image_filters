package filters;

import java.awt.image.BufferedImage;

import javax.swing.JOptionPane;

public class filter3 {
	BufferedImage out;

	public void filter(imageloader i) {
		Double.parseDouble(JOptionPane.showInputDialog("how intense should the output be?"));
		out = new BufferedImage(i.subject.getWidth() - 2, i.subject.getHeight() - 2, BufferedImage.TYPE_INT_RGB);
		out.createGraphics();
		for (int x = 1; x < i.subject.getWidth() - 1; x++) {
			for (int y = 1; y < i.subject.getHeight() - 1; y++) {
			}
		}
	}

	public int clip255(int in) {
		if (in < 0) {
			return 0;
		}
		if (in > 255) {
			return 255;
		} else {
			return in;
		}
	}

	public static void main(String[] args) {
		imageloader i = new imageloader();
		filter1 filt = new filter1();
		filt.filter(i);
		i.write(filt.out);

	}


}
