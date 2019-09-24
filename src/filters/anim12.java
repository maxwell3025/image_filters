package filters;

import java.awt.*;
import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import java.awt.event.MouseMotionListener;
import java.awt.event.MouseWheelEvent;
import java.awt.event.MouseWheelListener;
import java.awt.image.BufferedImage;
import java.util.ArrayList;

import javax.swing.JFrame;

import vectors.Matrix3D;
import vectors.Point2D;
import vectors.Point3D;

public class anim12 extends JFrame
		implements Runnable, KeyListener, MouseMotionListener, MouseListener, MouseWheelListener {
	/**
	 * 
	 */
	private static final long serialVersionUID = 9020477412984598531L;
	public final static float blurriness = 1f;
	public static final int globecolors = 8192;
	BufferedImage screen;
	BufferedImage comblayers;
	BufferedImage fscreen;
	Graphics2D graphics;
	Graphics2D copier;
	Graphics2D layerer;
	int screenwidth;
	int screenheight;
	int particlecount = 1;
	int fps = 0;
	int threadnum = 0;
	int screenarea;
	int[] wait = new int[1000];
	int mousex = 0;
	int mousey = 0;
	double zoom = 1;
	double movementspeed = (double) 1 / 256;
	boolean[] ispressed = new boolean[500];
	int timemilis;
	ArrayList<Point3D> points = new ArrayList<Point3D>();
	boolean mouselocked = false;

	public static void main(String[] args) {
		anim12 a = new anim12(1080, 1080, 6, 4);
		new Thread(a).start();
		new Thread(a).start();

	}

	public anim12(int width, int height, int arwidth, int arheight) {

		setDefaultCloseOperation(3);
		setResizable(false);
		screenwidth = width;
		screenheight = height;
		addKeyListener(this);
		addMouseMotionListener(this);
		addMouseListener(this);
		addMouseWheelListener(this);
		setSize(screenwidth, screenheight);
		setLocationRelativeTo(null);
		screen = new BufferedImage(width, height, BufferedImage.TYPE_INT_ARGB);
		fscreen = new BufferedImage(width, height, BufferedImage.TYPE_INT_ARGB);
		comblayers = new BufferedImage(width, height, BufferedImage.TYPE_INT_ARGB);
		graphics = screen.createGraphics();
		copier = fscreen.createGraphics();
		layerer = comblayers.createGraphics();
		screenarea = screenwidth * screenheight;
		setVisible(true);
	}

	public synchronized void paint(Graphics g) {
		g.drawImage(fscreen, 0, 0, screenwidth, screenheight, null);
	}

	public void update() {
		if (timemilis % 4 == 0 && ispressed[KeyEvent.VK_SHIFT]) {
			points.add(new Point3D(0, 0, -1));
		}
		particlecount = points.size();

		graphics.setColor(Color.black);
		graphics.fillRect(0, 0, screenwidth, screenheight);
		if (ispressed[KeyEvent.VK_W]) {
			Matrix3D turn = Matrix3D.rotx(Math.PI * -movementspeed);
			for (int i = 0; i < points.size(); i++) {
				points.set(i, turn.transform(points.get(i)));
			}
		}
		if (ispressed[KeyEvent.VK_A]) {
			Matrix3D turn = Matrix3D.roty(Math.PI * movementspeed);
			for (int i = 0; i < points.size(); i++) {
				points.set(i, turn.transform(points.get(i)));
			}
		}
		if (ispressed[KeyEvent.VK_S]) {
			Matrix3D turn = Matrix3D.rotx(Math.PI * movementspeed);
			for (int i = 0; i < points.size(); i++) {
				points.set(i, turn.transform(points.get(i)));
			}
		}
		if (ispressed[KeyEvent.VK_D]) {
			Matrix3D turn = Matrix3D.roty(Math.PI * -movementspeed);
			for (int i = 0; i < points.size(); i++) {
				points.set(i, turn.transform(points.get(i)));
			}
		}
		graphics.setColor(Color.white);
		for (int i = globecolors; i < points.size(); i++) {
			if (points.get(i).z > -0.1) {
				Point3D todraw = points.get(i);
				Point2D drawplace = new Point2D(screenwidth * todraw.x * 0.5, screenheight * todraw.y * 0.5);
				drawplace = drawplace.scale(1 / (todraw.z + zoom));
				drawplace = Point2D.add(new Point2D(screenwidth / 2, screenheight / 2), drawplace);
				int radius = (int) Math.ceil(4 / (todraw.z + zoom));
				graphics.fillOval((int) drawplace.x - radius, (int) drawplace.y - radius, 2 * radius, 2 * radius);
			}
		}

		for (int i = 0; i < globecolors; i++) {
			graphics.setColor(Color.getHSBColor((float) (0.33 + (double) i / globecolors * 0.33), 1, 1));
			Point3D todraw = points.get(i);
			Point2D drawplace = new Point2D(screenwidth * todraw.x * 0.5, screenheight * todraw.y * 0.5);
			drawplace = drawplace.scale(1 / (todraw.z + zoom));
			drawplace = Point2D.add(new Point2D(screenwidth / 2, screenheight / 2), drawplace);
			int radius = (int) Math.ceil(4 / (todraw.z + zoom));
			graphics.fillOval((int) drawplace.x - radius, (int) drawplace.y - radius, 2 * radius, 2 * radius);
		}
		graphics.setColor(Color.white);
		for (int i = globecolors; i < points.size(); i++) {

			if (points.get(i).z <= -0.1) {
				Point3D todraw = points.get(i);
				Point2D drawplace = new Point2D(screenwidth * todraw.x * 0.5, screenheight * todraw.y * 0.5);
				drawplace = drawplace.scale(1 / (todraw.z + zoom));
				drawplace = Point2D.add(new Point2D(screenwidth / 2, screenheight / 2), drawplace);
				int radius = (int) Math.ceil(4 / (todraw.z + zoom));
				graphics.fillOval((int) drawplace.x - radius, (int) drawplace.y - radius, 2 * radius, 2 * radius);
			}
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
			{
				Point3D to_add = new Point3D(Math.random() - 0.5, Math.random() - 0.5, Math.random() - 0.5);
				to_add = to_add.scale(0.9 / to_add.dist());
				points.add(to_add);
			}
			for (int i = 0; i < globecolors - 1; i++) {
				Point3D to_add = new Point3D(Math.random() - 0.5, Math.random() - 0.5, Math.random() - 0.5);
				to_add = Point3D.add(to_add.scale(0.0625), points.get(i));
				to_add = to_add.scale(0.9 / to_add.dist());
				points.add(to_add);
			}
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
		ispressed[e.getKeyCode()] = true;
		if (e.getKeyCode() == KeyEvent.VK_A) {

		}
	}

	public void keyReleased(KeyEvent e) {
		ispressed[e.getKeyCode()] = false;
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
		Matrix3D turn = Matrix3D.rotz((e.getX() - mousex) * Math.PI * ((double) 1 / 256));
		for (int i = 0; i < points.size(); i++) {
			points.set(i, turn.transform(points.get(i)));
		}
		mousex = e.getX();
		mousey = e.getY();

	}

	public void mouseMoved(MouseEvent e) {
		mousex = e.getX();
		mousey = e.getY();
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

	public void mouseWheelMoved(MouseWheelEvent e) {
		if (ispressed[KeyEvent.VK_SHIFT]) {
			zoom += e.getWheelRotation() * (double) 1 / 32;
		} else {
			movementspeed += e.getWheelRotation() * (double) -1 / 2048;
		}

	}
}
