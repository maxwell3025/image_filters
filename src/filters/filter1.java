package filters;

import java.awt.Color;
import java.awt.Graphics2D;
import java.awt.image.BufferedImage;

import javax.swing.JOptionPane;

public class filter1 {
	BufferedImage out;

	public void filter(imageloader i) {
		double strength = Double.parseDouble(JOptionPane.showInputDialog("how intense should the output be?"));
		out = new BufferedImage(i.subject.getWidth() - 2, i.subject.getHeight() - 2, BufferedImage.TYPE_INT_RGB);
		Graphics2D drawer = out.createGraphics();
		for (int x = 1; x < i.subject.getWidth() - 1; x++) {
			for (int y = 1; y < i.subject.getHeight() - 1; y++) {
				int xdif = (atpoint(i.red, x - 1, y) - atpoint(i.red, x, y))
						+ (atpoint(i.red, x, y) - atpoint(i.red, x + 1, y));
				int ydif = (atpoint(i.red, x, y - 1) - atpoint(i.red, x, y))
						+ (atpoint(i.red, x, y) - atpoint(i.red, x, y + 1));
				double rdist = Math.sqrt(xdif * xdif + ydif + ydif) * strength;
				xdif = (atpoint(i.green, x - 1, y) - atpoint(i.green, x, y))
						+ (atpoint(i.green, x, y) - atpoint(i.green, x + 1, y));
				ydif = (atpoint(i.green, x, y - 1) - atpoint(i.green, x, y))
						+ (atpoint(i.green, x, y) - atpoint(i.green, x, y + 1));
				double gdist = Math.sqrt(xdif * xdif + ydif + ydif) * strength;
				xdif = (atpoint(i.blue, x - 1, y) - atpoint(i.blue, x, y))
						+ (atpoint(i.blue, x, y) - atpoint(i.blue, x + 1, y));
				ydif = (atpoint(i.blue, x, y - 1) - atpoint(i.blue, x, y))
						+ (atpoint(i.blue, x, y) - atpoint(i.blue, x, y + 1));
				double bdist = Math.sqrt(xdif * xdif + ydif + ydif) * strength;
				drawer.setColor(new Color(clip255((int) rdist), clip255((int) gdist), clip255((int) bdist)));
				drawer.drawRect(x - 1, y - 1, 0, 0);
			}
		}
	}

	public int atpoint(int[][] tofind, int x, int y) {
		double halfsizex = tofind[0].length*0.5;
		double halfsizey = tofind.length*0.5;
		double xasdoub = (x - halfsizex) /(double) halfsizex;
		double yasdoub = (y - halfsizey) / (double)halfsizey;
		double newx = xasdoub;//xasdoub*xasdoub-yasdoub*yasdoub;
				double newy =yasdoub;//xasdoub*yasdoub*2;
		try {
			return tofind[(int) (newx*halfsizex+halfsizex)][(int) (newy*halfsizey+halfsizey)];
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
		filter1 filt = new filter1();
		filt.filter(i);
		i.write(filt.out);

	}

}
