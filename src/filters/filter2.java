package filters;

import java.awt.Color; 
import java.awt.Graphics2D;
import java.awt.image.BufferedImage;

public class filter2 {
	BufferedImage out;

	public void filter(imageloader i) {
		out = new BufferedImage(i.subject.getWidth() - 2, i.subject.getHeight() - 2, BufferedImage.TYPE_INT_RGB);
		Graphics2D drawer = out.createGraphics();
		for (int x = 0; x < i.subject.getWidth(); x++) {
			for (int y = 0; y < i.subject.getHeight(); y++) {
				drawer.setColor(atpoint(i.palette, x, y));
				drawer.drawRect(x - 1, y - 1, 0, 0);
			}
		}
	}

	public Color atpoint(Color[][] tofind, int x, int y) {
		double halfsizex = tofind[0].length * 0.5;
		double halfsizey = tofind.length * 0.5;
		double xasdoub = (x - halfsizex) / (double) halfsizex;
		double yasdoub = (y - halfsizey) / (double) halfsizey;
		complexnumber cur = new complexnumber(xasdoub,yasdoub);
		cur=complexnumber.pow(cur,16);
		
		double newx =cur.imag; 
		double newy =cur.real;
		try {
			return tofind[(int) (newx * halfsizex + halfsizex)][(int) (newy * halfsizey + halfsizey)];
		} catch (ArrayIndexOutOfBoundsException e) {

		}
		return Color.black;
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
		filter2 filt = new filter2();
		filt.filter(i);
		i.write(filt.out);

	}

}
