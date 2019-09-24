package filters;

import java.awt.*;
import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;
import java.awt.image.BufferedImage;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;
import java.util.zip.ZipEntry;
import java.util.zip.ZipOutputStream;

import javax.imageio.ImageIO;
import javax.swing.JFrame;
import javax.swing.JPanel;

import perlin.Perlin2D;
import vectors.Point2D;

public class anim7 extends JPanel implements Runnable, KeyListener {
	/**
	 * 
	 */
	private static final long serialVersionUID = 9020477412984598531L;
	final static boolean fpscor = false;
	final static float blurriness = 1f;
	final static double gravityforce = 1;
	final static int depspeed = 128;
	final static double avgsize = 4;
	final static double sizerange = 0;
	final static double planetspeed = 10;
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
	int usedfps = 128;
	int threadnum = 0;
	double xgap;
	double ygap;
	boolean[] topress = new boolean[500];
	int[] wait = new int[1000];
	List<Asteroid> particles = new ArrayList<Asteroid>();
	int timemilis;

	public static void main(String[] args) {
		anim7 a = new anim7(1080, 720, 6, 4);
		new Thread(a).start();
		new Thread(a).start();

	}

	public anim7(int width, int height, int arwidth, int arheight) {

		frame.setDefaultCloseOperation(3);
		frame.setResizable(false);
		screenwidth = width;
		screenheight = height;
		arraywidth = arwidth;
		arrayheight = arheight;
		addKeyListener(this);
		frame.addKeyListener(this);
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
	}

	public synchronized void paint(Graphics g) {
		g.drawImage(fscreen, 0, 0, screenwidth, screenheight, null);
	}

	public void update() {
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
			a.update(planetspeed / usedfps);
		}

		for (int subj = 0; subj < particles.size(); subj++) {
			for (int based = 0; based < particles.size(); based++) {
				if (subj != based) {
					Asteroid subject = particles.get(subj);
					Asteroid compared = particles.get(based);
					if (Point2D.add(compared.pos, subject.pos.scale(-1)).dist() < compared.rad + subject.rad) {
						particles.remove(based);

						double combarea = subject.area + compared.area;
						Point2D newpos = Point2D.add(subject.pos.scale(subject.area), compared.pos.scale(compared.area))
								.scale(1 / combarea);
						Asteroid newaster = new Asteroid(newpos, subject.area + compared.area);
						newaster.vel = Point2D.add(subject.vel, compared.vel);
						newaster.updaterad();
						if (subj < particles.size()) {
							particles.set(subj, newaster);
						} else {
							particles.add(subj, newaster);
						}
						based = 0;
						subj = 0;
						particlecount--;
					}
				} else {
					continue;
				}
			}
		}
		for (int subj = 0; subj < particles.size(); subj++) {
			for (int based = 0; based < particles.size(); based++) {
				if (subj != based) {
					Asteroid subject = particles.get(subj);
					Asteroid compared = particles.get(based);

					Point2D dif = Point2D.add(subject.pos.scale(-1), compared.pos);
					double dist = dif.dist();
					subject.vel = Point2D.add(subject.vel, dif.scale((gravityforce * planetspeed / usedfps)
							* ((subject.area * compared.area) / (dist * dist))));

				} else {
					continue;
				}
			}
		}

		for (Asteroid a : particles) {
			double radius = a.rad;
			if (radius > 1) {
				graphics.setColor(Color.white);
			} else {
				graphics.setColor(new Color((int) (radius * 255), (int) (radius * 255), (int) (radius * 255)));
			}
			graphics.drawOval((int) (a.pos.x - radius), (int) (a.pos.y - radius), (int) (radius * 2),
					(int) (radius * 2));

		}

		for (Asteroid a : particles) {
			if (a.area > 16) {
				graphics.setColor(Color.green);
				graphics.setFont(new Font("arial", Font.PLAIN, 10));
				FontMetrics fm = graphics.getFontMetrics();
				String todisplay = "size: " + a.area;
				int stwidth = fm.stringWidth(todisplay);
				graphics.drawString(todisplay, (int) (a.pos.x - stwidth / 2), (int) (a.pos.y - 5));
				;
			}
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
				if (fpscor) {
					usedfps = fps;
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

}
