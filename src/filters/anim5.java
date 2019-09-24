package filters;

import java.awt.AlphaComposite;
import java.awt.Color;
import java.awt.Font;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;
import java.awt.event.MouseEvent;
import java.awt.event.MouseMotionListener;
import java.awt.image.BufferedImage;

import javax.swing.JFrame;
import vectors.Point2D;

public class anim5 extends JFrame implements Runnable, KeyListener, MouseMotionListener {
	/**
	 * 
	 */
	private static final long serialVersionUID = 9020477412984598531L;
	final static float blurriness = 1f;
	final static int depspeed = 1024;
	public static final int ropecount = 4;
	BufferedImage screen;
	BufferedImage comblayers;
	BufferedImage fscreen;
	Graphics2D graphics;
	Graphics2D copier;
	Graphics2D layerer;
	int screenwidth;
	int screenheight;
	int arraywidth;
	int arrayheight;
	int particlecount;
	double xgap;
	double ygap;
	boolean spawning = false;
	int timemilis;
	Point2D targ = new Point2D(0, 0);
	rope[] ropes = new rope[ropecount];

	public static void main(String[] args) {
		anim5 a = new anim5(1080, 720, 6, 4);
		new Thread(a).start();

	}

	public anim5(int width, int height, int arwidth, int arheight) {
		setDefaultCloseOperation(3);
		setResizable(false);
		screenwidth = width;
		screenheight = height;
		for (int i = 0; i < ropecount; i++) {
			ropes[i] = new rope(256, new Point2D(Math.random() * screenwidth, Math.random() * screenheight));
		}
		addKeyListener(this);
		addMouseMotionListener(this);
		setSize(screenwidth, screenheight);
		setLocationRelativeTo(null);
		screen = new BufferedImage(width, height, BufferedImage.TYPE_INT_ARGB);
		fscreen = new BufferedImage(width, height, BufferedImage.TYPE_INT_ARGB);
		comblayers = new BufferedImage(width, height, BufferedImage.TYPE_INT_ARGB);
		graphics = screen.createGraphics();
		copier = fscreen.createGraphics();
		layerer = comblayers.createGraphics();
		setVisible(true);
	}

	public synchronized void paint(Graphics g) {
		g.drawImage(fscreen, 0, 0, screenwidth, screenheight, null);
	}

	public void update() {
		graphics.setColor(Color.black);
		graphics.fillRect(0, 0, screenwidth, screenheight);

		graphics.setColor(Color.white);
		for (rope a : ropes) {
			a.update(targ, graphics);
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

	public void mouseDragged(MouseEvent e) {
		targ = new Point2D(e.getX(), e.getY());

	}

	public void mouseMoved(MouseEvent e) {
		// TODO Auto-generated method stub

	}

	public class rope {
		Point2D base;
		Point2D[] tail;

		public rope(int length, Point2D b) {
			tail = new Point2D[length];
			base = b;
			for (int i = 0; i < tail.length; i++) {
				tail[i] = new Point2D(Math.random(), Math.random());
			}
		}

		public void update(Point2D targ, Graphics2D drawer) {
			tail[0] = targ;
			for (int i = 1; i < tail.length; i++) {
				Point2D cur = tail[i];
				Point2D base = tail[i - 1];
				Point2D dif = Point2D.add(cur, base.scale(-1));
				tail[i] = Point2D.add(base, dif.scale(2 / dif.dist()));
			}
			tail[tail.length - 1] = base;
			for (int i = tail.length - 2; i >= 0; i--) {
				Point2D cur = tail[i];
				Point2D base = tail[i + 1];
				Point2D dif = Point2D.add(cur, base.scale(-1));
				tail[i] = Point2D.add(base, dif.scale(2 / dif.dist()));
			}
			// for(int i = 0;i<tail.length-1;i++){
			// drawer.drawLine((int)tail[i].x, (int)tail[i].y, (int)tail[i+1].x,
			// (int)tail[i+1].y);
			// }
			for (int i = 0; i < tail.length; i++) {
				drawer.setColor(Color.getHSBColor((float) i / 64, 1, 1));
				drawer.fillOval((int) tail[i].x - i / 16 - 2, (int) tail[i].y - i / 16 - 2, i / 8 + 4, i / 8 + 4);
			}
		}

	}

}
