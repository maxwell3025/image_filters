package filters;

import java.awt.Color;
import java.awt.Graphics2D;
import java.awt.image.BufferedImage;

public class filter6 {
	BufferedImage out;
	static final int setbrightness=128;
	public void filter(imageloader i) {
		out = new BufferedImage(i.subject.getWidth() - 2, i.subject.getHeight() - 2, BufferedImage.TYPE_INT_RGB);
		Graphics2D drawer = out.createGraphics();

		for (int x = 1; x < i.subject.getWidth() - 1; x++) {
			for (int y = 1; y < i.subject.getHeight() - 1; y++) {
				int red = atpoint(i.red,x,y);
				int blue = atpoint(i.blue,x,y);
				int green = atpoint(i.green,x,y);
				int brightness = (red+blue+green)/3+1;
				drawer.setColor(new Color(clip255((red*setbrightness)/brightness),clip255((green*setbrightness)/brightness),clip255((blue*setbrightness)/brightness)));
				drawer.drawRect(x, y, 0, 0);
			}
		}

	}

	public int atpoint(int[][] tofind, int x, int y) {
		try {
			return tofind[x][y];
		} catch (ArrayIndexOutOfBoundsException e) {

		}
		return 0;
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
		filter6 filt = new filter6();
		filt.filter(i);
		i.write(filt.out);

	}

}
