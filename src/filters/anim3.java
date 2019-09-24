package filters;

import java.awt.AlphaComposite;
import java.awt.Color;
import java.awt.Font;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;
import java.awt.image.BufferedImage;
import java.util.ArrayList;
import java.util.List;

import javax.swing.JFrame;

import perlin.Perlin2D;
import vectors.Point2D;

public class anim3 extends JFrame implements Runnable, KeyListener {
	/**
	 * 
	 */
	private static final long serialVersionUID = 1645563191076831704L;
	final static float blurriness = 1f;
	final static double avgsize = 10;
	final static double sizerange = 0;
	BufferedImage screen;
	BufferedImage comblayers;
	BufferedImage fscreen;
	Graphics2D graphics;
	Graphics2D copier;
	Graphics2D layerer;
	Point2D[][] xperlinframe;
	Point2D[][] yperlinframe;
	int screenwidth;
	int screenheight;
	int arraywidth;
	int arrayheight;
	int particlecount;
	double xgap;
	double ygap;
	boolean spawning = false;
	List<ball> particles = new ArrayList<ball>();
	int timemilis;

	public static void main(String[] args) {
		anim3 a = new anim3(720, 480, 6, 4, 720, 1080);
		new Thread(a).start();

	}

	public anim3(int width, int height, int arwidth, int arheight, int truewidth, int trueheight) {

		setDefaultCloseOperation(3);
		setResizable(false);
		screenwidth = width;
		screenheight = height;
		arraywidth = arwidth;
		arrayheight = arheight;
		addKeyListener(this);
		xgap = (double) screenwidth / arraywidth;
		ygap = (double) screenheight / arrayheight;
		setSize(trueheight, truewidth);
		setLocationRelativeTo(null);
		screen = new BufferedImage(width, height, BufferedImage.TYPE_INT_ARGB);
		fscreen = new BufferedImage(width, height, BufferedImage.TYPE_INT_ARGB);
		comblayers = new BufferedImage(width, height, BufferedImage.TYPE_INT_ARGB);
		graphics = screen.createGraphics();
		copier = fscreen.createGraphics();
		layerer = comblayers.createGraphics();
		xperlinframe = Perlin2D.randomArray(arwidth, arheight, 1);
		yperlinframe = Perlin2D.randomArray(arwidth, arheight, 1);
		setVisible(true);
	}

	public synchronized void paint(Graphics g) {
		g.drawImage(fscreen, 0, 0, getWidth(), getHeight(), null);
	}

	public void update() {
		graphics.setColor(Color.black);
		graphics.fillRect(0, 0, screenwidth, screenheight);
		for (ball a : particles) {
			a.update();
			Point2D clippedpos = new Point2D(rem(a.pos.x - 0.01, screenwidth - 0.02) + 0.01,
					rem(a.pos.y - 0.01, screenheight - 0.02) + 0.01);
			a.prevpos = new Point2D(a.pos.x, a.pos.y);
			a.pos = clippedpos;
			if (Point2D.add(a.pos, a.prevpos.scale(-1)).dist() > 100) {
				a.prevpos = new Point2D(a.pos.x, a.pos.y);
			}

			double xdif = 0;
			double ydif = 0;
			try {
				xdif = Perlin2D.Perlin2Drepeating(xperlinframe, a.pos.x / xgap, a.pos.y / ygap, 1);
				ydif = Perlin2D.Perlin2Drepeating(yperlinframe, a.pos.x / xgap, a.pos.y / ygap, 1);

			} catch (ArrayIndexOutOfBoundsException e) {
				System.out.println(a.pos.x);
				System.out.println(a.pos.y);
				System.exit(0);
			}
			Point2D vchange = new Point2D(xdif, ydif);
			a.vel = Point2D.add(vchange, a.vel);

		}
		for (int x = 0; x < screenwidth; x++) {
			for (int y = 0; y < screenheight; y++) {
				double dist = 0;
				for (ball a : particles) {
					dist += a.rad / Point2D.add(a.pos.scale(-1), new Point2D(x, y)).dist();
				}
				int shade = clip255((int) (dist * 255));
				graphics.setColor(new Color(shade, shade, shade));
				graphics.drawRect(x, y, 0, 0);
			}
		}

		layerer.setComposite(AlphaComposite.getInstance(AlphaComposite.SRC_OVER, blurriness));
		layerer.drawImage(screen, 0, 0, null);
		copier.clearRect(0, 0, screenwidth, screenheight);
		copier.drawImage(comblayers, 0, 0, null);
		copier.setColor(Color.green);
		copier.setFont(new Font("arial", Font.PLAIN, 20));
		copier.drawString(String.valueOf(particlecount), 3, 48);
	}

	public void run() {
		particles.add(new ball(new Point2D(Math.random() * screenwidth, Math.random() * screenheight), -20));
		while (true) {
			try {
				Thread.sleep(1);
			} catch (InterruptedException e) {
			}
			timemilis++;
			if (spawning) {
				if (timemilis % 5 == 0) {
					particles.add(new ball(new Point2D(Math.random() * screenwidth, Math.random() * screenheight),
							avgsize + (Math.random() - 0.5) * sizerange));
					particlecount++;
				}
			}

			update();
			repaint();
		}

	}

	public static int clip255(int in) {
		if (in < 0) {
			return 0;
		}
		if (in > 255) {
			return 255;
		} else {
			return 255-in;
		}
	}

	public void keyTyped(KeyEvent e) {

	}

	public void keyPressed(KeyEvent e) {
		if (e.getKeyCode() == KeyEvent.VK_SPACE) {
			spawning = !spawning;

		}
		if (e.getKeyCode() == KeyEvent.VK_ENTER) {
			for (ball a : particles) {
				a.vel = new Point2D(Math.random() - 0.5, Math.random() - 0.5).scale(10);
			}
		}
	}

	public void keyReleased(KeyEvent e) {

	}

	public static double rem(double a, double b) {
		double downscaled = a / b;
		double out = (downscaled - Math.floor(downscaled)) * b;
		if (out < 0) {
			return 0;
		}
		if (out > b) {
			return b;
		}
		return out;
	}

}
