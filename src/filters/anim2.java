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

public class anim2 extends JFrame implements Runnable, KeyListener {
	/**
	 * 
	 */
	private static final long serialVersionUID = 9020477412984598531L;
	final static float blurriness = 1f;
	final static int depspeed = 1024;
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
	List<particle> particles = new ArrayList<particle>();
	int timemilis;

	public static void main(String[] args) {
		anim2 a = new anim2(1080, 720, 6, 4);
		new Thread(a).start();

	}

	public anim2(int width, int height, int arwidth, int arheight) {

		setDefaultCloseOperation(3);
		setResizable(false);
		screenwidth = width;
		screenheight = height;
		arraywidth = arwidth;
		arrayheight = arheight;
		addKeyListener(this);
		xgap = (double) screenwidth / arraywidth;
		ygap = (double) screenheight / arrayheight;
		setSize(screenwidth, screenheight);
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
		g.drawImage(fscreen, 0, 0, screenwidth, screenheight, null);
	}

	public void update() {
		graphics.setColor(Color.black);
		graphics.fillRect(0, 0, screenwidth, screenheight);
		for (particle a : particles) {
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
			int radius = Math.max((int) (a.vel.dist() * 10), 1);
			radius = 1;
			graphics.setColor(Color.white);
			graphics.drawLine((int) a.pos.x - radius, (int) a.pos.y - radius, (int) a.prevpos.x - radius,
					(int) a.prevpos.y - radius);

		}
		int lag = 0;
		for (int i = 0; i < particles.size() - lag; i++) {
			if (particles.get(i).vel.dist() < 0.001) {
				particles.remove(i);
				particlecount--;
				i--;
				lag++;
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
		while (true) {
			try {
				Thread.sleep(1);
			} catch (InterruptedException e) {
			}
			timemilis++;
			if (spawning) {
				for (int i = 0; i < depspeed; i++) {
					particles.add(new particle(new Point2D(Math.random() * screenwidth, Math.random() * screenheight)));
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
			return in;
		}
	}

	public void keyTyped(KeyEvent e) {

	}

	public void keyPressed(KeyEvent e) {
		if (e.getKeyCode() == KeyEvent.VK_SPACE) {
			spawning = !spawning;

		}
		if (e.getKeyCode() == KeyEvent.VK_ENTER) {
			for (particle a : particles) {
				a.vel = Point2D.add(new Point2D(Math.random() - 0.5, Math.random() - 0.5), a.vel);
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
