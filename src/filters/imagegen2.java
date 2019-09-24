package filters;

import java.awt.AlphaComposite;
import java.awt.Color;
import java.awt.Dimension;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.image.BufferedImage;
import java.io.IOException;

import javax.imageio.ImageIO;
import javax.swing.JColorChooser;
import javax.swing.JFileChooser;
import javax.swing.JOptionPane;
import javax.swing.JPanel;

import perlin.Perlin2D;
import vectors.Point2D;

public class imagegen2 {
	static BufferedImage out;

	public static void main(String[] args) {
		Point2D[][][] axes;

		Color drawas = JColorChooser.showDialog(null, "choose a color", null);
		int width = Integer.parseInt(JOptionPane.showInputDialog(null, "width?(pixels)"));
		int height = Integer.parseInt(JOptionPane.showInputDialog(null, "height?(pixels)"));
		int arrwidth = Integer.parseInt(JOptionPane.showInputDialog(null, "x-scale?"));
		int arrheight = Integer.parseInt(JOptionPane.showInputDialog(null, "y-scale?"));
		String[] options = { "none", "poly", "cosine", "triple cosine", "inverse", "nearest neigbor" };
		int stacked = Integer.parseInt(JOptionPane.showInputDialog(null, "do you want it to be fraactalized?"));
		int interpolationtype = JOptionPane.showOptionDialog(null, "choose how to interpolate", null,
				JOptionPane.DEFAULT_OPTION, JOptionPane.PLAIN_MESSAGE, null, options, null);
		double intensity = Double.parseDouble(JOptionPane.showInputDialog(null, "intensity?"));
		axes = new Point2D[stacked][][];
		for(int i = 0;i<stacked; i++){
		axes[i] = Perlin2D.randomArray(arrwidth*(1<<i) + 1, arrheight*(1<<i) + 1, intensity/(1<<(2*i)));
		}
		
		double[][] image = new double[width][height];
		double xscale = (double) arrwidth / width;
		double yscale = (double) arrheight / height;
		out = new BufferedImage(width, height, BufferedImage.TYPE_INT_ARGB);
		Graphics2D drawer = out.createGraphics();
		for (int x = 0; x < image.length; x++) {
			for (int y = 0; y < image[x].length; y++) {
				image[x][y]=0;
				for(int i = 0;i<stacked;i++){
				image[x][y] += Perlin2D.CalcPerlin2D(axes[i], x * xscale*(1<<i), y * yscale*(1<<i), interpolationtype);
					
				}
				image[x][y]=(1+image[x][y])*0.5;
			}
		}
		drawer.setComposite(AlphaComposite.getInstance(AlphaComposite.CLEAR));

		drawer.setComposite(AlphaComposite.getInstance(AlphaComposite.SRC));
		for (int x = 1; x < image.length; x++) {
			for (int y = 1; y < image[x].length; y++) {
				double xdif = image[x][y]-image[x-1][y]*10;

				double ydif = image[x][y]-image[x][y-1]*10;
				drawer.setColor(drawas);
				drawer.drawRect(x+(int)xdif, y+(int)ydif, 0, 0);
			}
		}
		JFileChooser j = new JFileChooser();
		j.showSaveDialog(null);
		try {
			ImageIO.write(out, "png", j.getSelectedFile());
		} catch (IOException e) {
		}

		JOptionPane.showMessageDialog(null, new imagedisplayer(out, 720, "finished"), null, JOptionPane.PLAIN_MESSAGE);

	}

	public static class imagedisplayer extends JPanel {
		private static final long serialVersionUID = 1L;
		BufferedImage todisplay;
		double maximum;
		String msg;

		imagedisplayer(BufferedImage b, int maxsize, String caption) {
			msg = caption;
			todisplay = b;
			maximum = (double) Math.max(todisplay.getHeight(), todisplay.getWidth()) / maxsize;
			setPreferredSize(new Dimension((int) (todisplay.getWidth() / maximum),
					(int) (todisplay.getHeight() / maximum) + 10));
		}

		public void paintComponent(Graphics g) {
			super.paintComponent(g);
			g.drawImage(todisplay, (int) ((this.getWidth() - todisplay.getWidth() / maximum) * 0.5), 0,
					(int) (todisplay.getWidth() / maximum), (int) (todisplay.getHeight() / maximum), this);

		}
	}

	public static int clip255(int in) {
		if (in < 0) {
			return 0;
		}
		if (in > 255) {
			return 255;
		} else {
			return in;
		}
	}

	public static double interpolationfunc(double in, int type) {
		if (type == 0) {
			return in;
		} else if (type == 1) {
			return in * in * in * (in * (in * 6 - 15) + 10);
		} else if (type == 2) {
			return 0.5 * (-Math.cos(in * Math.PI) + 1);
		} else if (type == 3) {
			return 0.5 * (-Math.cos(in * Math.PI * 3) + 1);
		} else if (type == 4) {
			return 1 - type;
		} else if (type == 5) {
			if (in < 0.5) {
				return 0;
			} else {
				return 1;
			}
		} else {
			return in;
		}
	}
}
