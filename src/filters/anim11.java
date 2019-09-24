package filters;

import java.awt.*;
import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import java.awt.event.MouseMotionListener;
import java.awt.image.BufferedImage;
import java.util.ArrayList;
import javax.swing.JFrame;
import javax.swing.SwingUtilities;

import vectors.Point2D;

public class anim11 extends JFrame implements Runnable, KeyListener, MouseMotionListener, MouseListener {
	/**
	 * 
	 */
	private static final long serialVersionUID = 9020477412984598531L;
	public final static float blurriness = 1f;
	public static final int arraysize = 8192*4;
	public static final double cycletime = 8192*16;
	public static final int pointcolorchange=128;
	BufferedImage screen;
	BufferedImage comblayers;
	BufferedImage fscreen;
	Graphics2D graphics;
	Graphics2D copier;
	Graphics2D layerer;
	int screenwidth;
	int screenheight;
	int particlecount=1;
	int fps = 0;
	int threadnum = 0;
	int screenarea;
	double xgap;
	double ygap;
	Point2D dispersalpoint = new Point2D(0,0);
	int[] wait = new int[1000];
	Grain[] particles = new Grain[arraysize];
	int[] crystal;
	int crystalsize = 0;
	int timemilis;

	public static void main(String[] args) {
		anim11 a = new anim11(1920, 1080, 6, 4);
		new Thread(a).start();
		new Thread(a).start();

	}

	public anim11(int width, int height, int arwidth, int arheight) {

		setDefaultCloseOperation(3);
		setResizable(false);
		screenwidth = width;
		screenheight = height;
		addKeyListener(this);
		addMouseMotionListener(this);
		addMouseListener(this);
		setSize(screenwidth, screenheight);
		setLocationRelativeTo(null);
		screen = new BufferedImage(width, height, BufferedImage.TYPE_INT_ARGB);
		fscreen = new BufferedImage(width, height, BufferedImage.TYPE_INT_ARGB);
		comblayers = new BufferedImage(width, height, BufferedImage.TYPE_INT_ARGB);
		graphics = screen.createGraphics();
		copier = fscreen.createGraphics();
		layerer = comblayers.createGraphics();
		screenarea = screenwidth * screenheight;
		crystal = new int[screenarea];
		setVisible(true);
	}

	public synchronized void paint(Graphics g) {
		g.drawImage(fscreen, 0, 0, screenwidth, screenheight, null);
	}

	public void update() {
		graphics.setColor(Color.black);
		graphics.fillRect(0, 0, screenwidth, screenheight);

		for (int i = 0; i < arraysize; i++) {
			particles[i].update();
			if (particles[i].x < 0) {
				particles[i].x = screenwidth - 1;
			}
			if (particles[i].x > screenwidth - 1) {
				particles[i].x = 0;
			}
			if (particles[i].y < 0) {
				particles[i].y = screenheight - 1;
			}
			if (particles[i].y > screenheight - 1) {
				particles[i].y = 0;
			}
		}
		for (int i = 0; i < arraysize; i++) {
			Grain g = particles[i];
			boolean stuck = false;
			if (g.x == 0) {
				stuck |= crystal[g.y * screenwidth + screenwidth - 1] != -1;
			} else {
				stuck |= crystal[g.y * screenwidth + g.x - 1] != -1;
			}
			if (g.x == screenwidth - 1) {
				stuck |= crystal[g.y * screenwidth] != -1;
			} else {
				stuck |= crystal[g.y * screenwidth + g.x + 1] != -1;
			}
			if (g.y == 0) {
				stuck |= crystal[(screenheight - 1) * screenwidth + g.x] != -1;
			} else {
				stuck |= crystal[(g.y - 1) * screenwidth + g.x] != -1;
			}
			if (g.y == screenheight - 1) {
				stuck |= crystal[g.x] != -1;
			} else {
				stuck |= crystal[(g.y + 1) * screenwidth + g.x] != -1;
			}
			if (stuck) {
				particlecount++;
				crystalsize++;
				crystal[screenwidth * g.y + g.x] = crystalsize;
				particles[i] = new Grain((int) dispersalpoint.x, (int) dispersalpoint.y);
			}
		}
		for (int i = 0; i < screenarea; i++) {
			if (crystal[i] != -1) {
				graphics.setColor(Color.getHSBColor((float)(crystal[i] / cycletime), 1, 1));
				graphics.drawRect(i % screenwidth, i / screenwidth, 0, 0);
			}
		}
		for (int i = 0; i < arraysize; i++) {
			int col = clip255(new Color(screen.getRGB(particles[i].x, particles[i].y)).getBlue()+pointcolorchange);
			graphics.setColor(new Color(col,col,col));
			graphics.drawRect(particles[i].x, particles[i].y, 0, 0);
		}
		layerer.setComposite(AlphaComposite.getInstance(AlphaComposite.SRC_OVER, blurriness));
		layerer.drawImage(screen, 0, 0, null);

		copier.clearRect(0, 0, screenwidth, screenheight);
		copier.drawImage(comblayers, 0, 0, null);
		copier.setColor(Color.green);
		copier.setFont(new Font("arial", Font.PLAIN, 20));
		copier.drawString("particle count:" + String.valueOf(particlecount), 3, 48);
		copier.drawString("fps:" + String.valueOf(fps), 3, 68);
	}

	public void run() {
		threadnum++;
		if (threadnum == 1) {
			for (int i = 0; i < screenarea; i++) {
				crystal[i] = -1;
			}
			for(int i = 0;i<arraysize;i++){
				particles[i]= new Grain((int)(Math.random()*screenwidth),(int)(Math.random()*screenheight));
			}
			crystal[(screenheight/2*screenwidth)+screenwidth/2] = 0;

			// TODO
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

	public class Grain {
		int x;
		int y;

		public Grain(int x, int y) {
			this.x = x;
			this.y = y;
		}

		public void update() {
			x+=(int)(Math.random()*5)-2;
			y+=(int)(Math.random()*5)-2;
		}
	}

	public void mouseDragged(MouseEvent e) {
		if (SwingUtilities.isLeftMouseButton(e)) {
			dispersalpoint = new Point2D(e.getX(), e.getY());
		}
		if (SwingUtilities.isRightMouseButton(e)) {
		}

	}

	public void mouseMoved(MouseEvent e) {
	}

	public void mouseClicked(MouseEvent e) {

	}

	public void mousePressed(MouseEvent e) {

	}

	public void mouseReleased(MouseEvent e) {

	}

	public void mouseEntered(MouseEvent e) {

	}

	public void mouseExited(MouseEvent e) {

	}
}
