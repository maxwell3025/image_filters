package filters;

import java.awt.*;
import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;
import java.awt.event.MouseEvent;
import java.awt.event.MouseMotionListener;
import java.awt.image.BufferedImage;
import java.util.ArrayList;
import java.util.List;
import javax.swing.JFrame;
import javax.swing.JPanel;

import perlin.Perlin2D;
import vectors.Point2D;

public class anim15 extends JPanel implements Runnable, KeyListener, MouseMotionListener {
	/**
	 * 
	 */
	private static final long serialVersionUID = 9020477412984598531L;
	final static float blurriness = 1f;
	final static double gravityforce = -0.1;
	final static int depspeed = 1024;
	final static double avgsize = 16;
	final static double sizerange = 0;
	final static double planetspeed = 1;
	JFrame frame = new JFrame();
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
	int fps = 0;
	int threadnum = 0;
	double xgap;
	double ygap;
	boolean[] topress = new boolean[500];
	int[] wait = new int[1000];
	List<Asteroid> particles = new ArrayList<Asteroid>();
	int timemilis;
	Point2D mouse = new Point2D(0,0);
	Point2D prevmouse = new Point2D(0,0);

	public static void main(String[] args) {
		anim15 a = new anim15(1080, 720, 6, 4);
		new Thread(a).start();
		new Thread(a).start();

	}

	public anim15(int width, int height, int arwidth, int arheight) {

		frame.setDefaultCloseOperation(3);
		frame.setResizable(false);
		screenwidth = width;
		screenheight = height;
		arraywidth = arwidth;
		arrayheight = arheight;
		addKeyListener(this);
		frame.addKeyListener(this);
		addMouseMotionListener(this);
		frame.addMouseMotionListener(this);
		xgap = (double) screenwidth / arraywidth;
		ygap = (double) screenheight / arrayheight;
		this.setPreferredSize(new Dimension(screenwidth, screenheight));
		screen = new BufferedImage(width, height, BufferedImage.TYPE_INT_ARGB);
		fscreen = new BufferedImage(width, height, BufferedImage.TYPE_INT_ARGB);
		comblayers = new BufferedImage(width, height, BufferedImage.TYPE_INT_ARGB);
		graphics = screen.createGraphics();
		copier = fscreen.createGraphics();
		layerer = comblayers.createGraphics();
		xperlinframe = Perlin2D.randomArray(arwidth, arheight, 1);
		yperlinframe = Perlin2D.randomArray(arwidth, arheight, 1);
		frame.add(this);
		frame.pack();
		frame.setLocationRelativeTo(null);
		frame.setVisible(true);
		particles.add(new Asteroid(new Point2D(0, 0), 1));
	}

	public synchronized void paint(Graphics g) {
		g.drawImage(fscreen, 0, 0, screenwidth, screenheight, null);
	}

	public void update() {
			particles.set(0, new Asteroid(mouse, prevmouse,
					avgsize + (Math.random() - 0.5) * sizerange));
		graphics.setColor(Color.black);
		graphics.fillRect(0, 0, screenwidth, screenheight);
		if (topress[KeyEvent.VK_SPACE]) {
			for (int i = 0; i < depspeed; i++) {
				particles.add(new Asteroid(new Point2D(Math.random() * screenwidth, Math.random() * screenheight),
						avgsize + (Math.random() - 0.5) * sizerange));
				particlecount++;
			}
			topress[KeyEvent.VK_SPACE] = false;
		}
		

		for (Asteroid a : particles) {
			a.vel.scale(0.9);
			if (a.vel.dist() > 10) {
				a.vel = a.vel.scale(10 / a.vel.dist());
			}
			// a.vel.y += 1;
			a.update(planetspeed);
		}
		for (Asteroid a : particles) {
			if (a.pos.x < 0) {
				a.pos.x = Math.abs(a.pos.x);
				a.vel.x *= -0;
			}
			if (a.pos.y < 0) {
				a.pos.y = Math.abs(a.pos.y);
				a.vel.y *= -0;
			}
			if (a.pos.x > screenwidth) {
				a.pos.x = screenwidth * 2 - a.pos.x;
				a.vel.x *= -0;
			}
			if (a.pos.y > screenheight) {
				a.pos.y = screenheight * 2 - a.pos.y;
				a.vel.y *= -0;
			}
		}
		for (int subj = 0; subj < particles.size(); subj++) {
			for (int based = 0; based < particles.size(); based++) {

				if (subj != based) {
					Asteroid subject = particles.get(subj);
					Asteroid compared = particles.get(based);
					Point2D dif = Point2D.add(subject.pos.scale(-1), compared.pos);
					double dist = dif.dist()/600;
					subject.vel = Point2D.add(subject.vel.scale(1 - 1 / (dist)),
							compared.vel.scale(1 / (dist)));
				} else {
					continue;
				}
			}
		}

		for (Asteroid a : particles) {
			double radius = a.rad;
			if (radius > 1) {
				graphics.setColor(Color.getHSBColor((float) (a.vel.dist() / 128), 1, 1));
			} else {
				graphics.setColor(new Color((int) (radius * 255), (int) (radius * 255), (int) (radius * 255)));
			}
			graphics.drawOval((int) (a.pos.x - radius), (int) (a.pos.y - radius), (int) (radius * 2),
					(int) (radius * 2));
			graphics.fillOval((int) (a.pos.x - radius), (int) (a.pos.y - radius), (int) (radius * 2),
					(int) (radius * 2));

		}
		layerer.setComposite(AlphaComposite.getInstance(AlphaComposite.SRC_OVER, blurriness));
		layerer.drawImage(screen, 0, 0, null);

		copier.clearRect(0, 0, screenwidth, screenheight);
		copier.drawImage(comblayers, 0, 0, null);
		copier.setColor(Color.green);
		copier.setFont(new Font("arial", Font.PLAIN, 20));
		copier.drawString("particle count:" + String.valueOf(particlecount), 0, 20);
		copier.drawString("fps:" + String.valueOf(fps), 0, 40);
	}

	public void run() {
		threadnum++;
		if (threadnum == 1) {
			while (true) {
				try {
					Thread.sleep(1);
				} catch (InterruptedException e) {
				}

				update();
				repaint();
				timemilis++;
				for (int i = 0; i < 1000; i++) {
					wait[i]++;
				}

			}
		}
		if (threadnum == 2) {
			while (true) {
				try {
					Thread.sleep(1);
				} catch (InterruptedException e) {
				}
				fps = wait[1];
				for (int i = 1; i < 1000; i++) {
					wait[i - 1] = wait[i];
				}
				wait[999] = 0;
			}

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
			topress[KeyEvent.VK_SPACE] = true;

		}
		if (e.getKeyCode() == KeyEvent.VK_ENTER) {
			for (Asteroid a : particles) {
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

	public void mouseDragged(MouseEvent e) {
		// TODO Auto-generated method stub

	}

	public void mouseMoved(MouseEvent e) {
		prevmouse = mouse;
		mouse = new Point2D(e.getX(), e.getY());
	}

}
