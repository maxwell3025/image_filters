package filters;

import java.awt.Color;
import java.awt.Graphics2D;
import java.awt.image.BufferedImage;

import javax.swing.JOptionPane;

public class filter4 {
	BufferedImage out;
	int segments;
	int intensity;
	double[][] pointsx;
	double[][] pointsy;

	public void filter(imageloader i) {
		segments = Integer.parseInt(JOptionPane.showInputDialog("how many waves do you want?"));
		intensity = Integer.parseInt(JOptionPane.showInputDialog("how intense do you want?"));
		pointsx = new double[segments + 1][segments + 1];
		pointsy = new double[segments + 1][segments + 1];
		for (int x = 0; x < pointsx.length; x++) {
			for (int y = 0; y < pointsx[x].length; y++) {
				pointsx[x][y] = Math.random() - 0.5;
				pointsy[x][y] = Math.random() - 0.5;
			}
		}
		for (int x = 0; x < pointsx.length; x++) {
			for (int y = 0; y < pointsx[x].length; y++) {
				System.out.print(pointsx[x][y] + "/");
				System.out.print(pointsy[x][y] + ",");
			}
			System.out.println();
		}
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
		int selectedx = x;
		int selectedy = y;
		int imagewidth = tofind.length;
		int imageheight = tofind[0].length;
		double xamount = (double) x / imagewidth * segments;
		double yamount = (double) y / imageheight * segments;
		double xgrad = xamount - Math.floor(xamount);
		double ygrad = yamount - Math.floor(yamount);
		double xshift = 0;
		double yshift = 0;
		xshift = lerp(
				lerp(pointsx[(int) Math.floor(yamount)][(int) Math.floor(xamount)],
						pointsx[(int) Math.floor(yamount)][(int) Math.ceil(xamount)], xgrad),
				lerp(pointsx[(int) Math.ceil(yamount)][(int) Math.floor(xamount)],
						pointsx[(int) Math.ceil(yamount)][(int) Math.ceil(xamount)], xgrad),
				ygrad);
		yshift = lerp(
				lerp(pointsy[(int) Math.floor(yamount)][(int) Math.floor(xamount)],
						pointsy[(int) Math.floor(yamount)][(int) Math.ceil(xamount)], xgrad),
				lerp(pointsy[(int) Math.ceil(yamount)][(int) Math.floor(xamount)],
						pointsy[(int) Math.ceil(yamount)][(int) Math.ceil(xamount)], xgrad),
				ygrad);

		try {
			return tofind[(int) (selectedx + xshift * intensity)][(int) (selectedy + yshift * intensity)];
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

	public double lerp(double start, double end, double dist) {
		return start * (1 - tocos(dist)) + end * tocos(dist);
	}

	public double tocos(double in) {
		return (1 - Math.cos(in * Math.PI*11)) * 0.5;
	}

	public static void main(String[] args) {
		imageloader i = new imageloader();
		filter4 filt = new filter4();
		filt.filter(i);
		i.write(filt.out);

	}

}
