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
import java.util.Comparator;

import javax.swing.JFrame;
import javax.swing.JPanel;

import vectors.Matrix3D;
import vectors.Point2D;
import vectors.Point3D;

public class anim13 extends JPanel
		implements Runnable, KeyListener, MouseMotionListener, MouseListener, MouseWheelListener {
	/**
	 * 
	 */
	private static final long serialVersionUID = 9020477412984598531L;
	public final static float blurriness = 1f;
	public static final int globecolors = 16000;
	public static final Comparator<RenderPoint> depthcomparison = new Comparator<RenderPoint>() {

		public int compare(RenderPoint o1, RenderPoint o2) {
			return (int) Math.signum(o2.pos.z - o1.pos.z);
		}

	};
	JFrame frame = new JFrame();
	BufferedImage screen;
	BufferedImage comblayers;
	BufferedImage fscreen;
	Graphics2D graphics;
	Graphics2D copier;
	Graphics2D layerer;
	int screenwidth;
	int screenheight;
	int smallestdimension;
	int particlecount = 1;
	int fps = 0;
	int threadnum = 0;
	int screenarea;

	int[] wait = new int[1000];
	int mousex = 0;
	int mousey = 0;
	double zoom = 0;
	double movementspeed = (double) 1 / 256;
	boolean[] ispressed = new boolean[550];
	int timemilis;
	ArrayList<RenderPoint> points = new ArrayList<RenderPoint>();
	ArrayList<RenderPoint> adjustedpoints = new ArrayList<RenderPoint>();
	Point3D camerabase = new Point3D(0, 0, 0);
	Matrix3D view = Matrix3D.identity;
	Matrix3D inverseview = Matrix3D.identity;
	boolean mouselocked = false;

	public static void main(String[] args) {
		anim13 a = new anim13(1080, 720, 6, 4);
		new Thread(a).start();
		new Thread(a).start();
		new Thread(a).start();

	}

	public anim13(int width, int height, int arwidth, int arheight) {
		frame.setDefaultCloseOperation(3);
		frame.setResizable(false);
		screenwidth = width;
		screenheight = height;
		smallestdimension = Math.min(screenwidth, screenheight);
		frame.add(this);
		frame.setSize(screenwidth, screenheight);
		frame.setLocationRelativeTo(null);
		screen = new BufferedImage(width, height, BufferedImage.TYPE_INT_ARGB);
		fscreen = new BufferedImage(width, height, BufferedImage.TYPE_INT_ARGB);
		comblayers = new BufferedImage(width, height, BufferedImage.TYPE_INT_ARGB);
		graphics = screen.createGraphics();
		copier = fscreen.createGraphics();
		layerer = comblayers.createGraphics();
		screenarea = screenwidth * screenheight;
		this.setPreferredSize(new Dimension(screenwidth, screenheight));
		frame.pack();
		addKeyListener(this);
		addMouseMotionListener(this);
		addMouseListener(this);
		addMouseWheelListener(this);
		frame.addKeyListener(this);
		frame.addMouseMotionListener(this);
		frame.addMouseListener(this);
		frame.addMouseWheelListener(this);
		frame.setVisible(true);
	}

	public synchronized void paint(Graphics g) {
		g.drawImage(fscreen, 0, 0, screenwidth, screenheight, null);
	}

	public void graphicsupdate() {
		graphics.setComposite(AlphaComposite.getInstance(AlphaComposite.SRC, 1));
		particlecount = points.size();
		graphics.setColor(Color.black);
		graphics.fillRect(0, 0, screenwidth, screenheight);
		for (int i = 0; i < points.size(); i++) {
			Point3D todraw = points.get(i).pos;
			todraw = Point3D.add(todraw, camerabase.scale(-1));
			todraw = view.transform(todraw);
			adjustedpoints.add(new RenderPoint(todraw, points.get(i).col));
		}
		adjustedpoints.sort(depthcomparison);
		for (int i = 1; i < adjustedpoints.size(); i++) {
			Point3D todraw = adjustedpoints.get(i).pos;
			if (todraw.z > -zoom) {
				Point2D drawplace = new Point2D(smallestdimension * todraw.x * 0.5, smallestdimension * todraw.y * 0.5);
				drawplace = drawplace.scale(1 / (todraw.z + zoom));
				drawplace = Point2D.add(new Point2D(screenwidth / 2, screenheight / 2), drawplace);
				double radius = 64 / (todraw.z + zoom); 
				graphics.setColor(adjustedpoints.get(i).col);
				graphics.fillOval((int) (drawplace.x - radius), (int) (drawplace.y - radius), (int) (2 * radius),
						(int) (2 * radius));
				graphics.drawOval((int) (drawplace.x - radius), (int) (drawplace.y - radius), (int) (2 * radius),
						(int) (2 * radius));
			}
		}
		graphics.setComposite(AlphaComposite.getInstance(AlphaComposite.SRC_OVER, 0.5f));
		double radius = 64 / zoom;
		graphics.setColor(Color.white);
		graphics.fillOval((int) (screenwidth / 2 - radius), (int) (screenheight / 2 - radius),
				(int) Math.ceil(2 * radius) + 1, (int) Math.ceil(2 * radius) + 1);
		adjustedpoints.clear();

		layerer.setComposite(AlphaComposite.getInstance(AlphaComposite.SRC_OVER, blurriness));
		layerer.drawImage(screen, 0, 0, null);

		copier.clearRect(0, 0, screenwidth, screenheight);
		copier.drawImage(comblayers, 0, 0, null);
		copier.setColor(Color.green);
		copier.setFont(new Font("arial", Font.PLAIN, 20));
		copier.drawString("particle count:" + String.valueOf(particlecount), 0, 20);
		copier.drawString("fps:" + String.valueOf(fps), 0, 40);
	}

	public void movementupdate() {

		if (ispressed[KeyEvent.VK_W]) {
			camerabase = Point3D.add(camerabase, inverseview.transform(new Point3D(0, 0, movementspeed)));
		}
		if (ispressed[KeyEvent.VK_S]) {
			camerabase = Point3D.add(camerabase, inverseview.transform(new Point3D(0, 0, -movementspeed)));
		}
		if (ispressed[KeyEvent.VK_SPACE]) {
			camerabase = Point3D.add(camerabase, inverseview.transform(new Point3D(0, -movementspeed, 0)));
		}
		if (ispressed[KeyEvent.VK_SHIFT]) {
			camerabase = Point3D.add(camerabase, inverseview.transform(new Point3D(0, movementspeed, 0)));
		}
		if (ispressed[KeyEvent.VK_A]) {
			camerabase = Point3D.add(camerabase, inverseview.transform(new Point3D(-movementspeed, 0, 0)));
		}
		if (ispressed[KeyEvent.VK_D]) {
			camerabase = Point3D.add(camerabase, inverseview.transform(new Point3D(movementspeed, 0, 0)));
		}
		if (ispressed[KeyEvent.VK_Z]) {
			Matrix3D turn = Matrix3D.rotz(Math.PI * movementspeed);
			view = turn.transform(view);
		}
		if (ispressed[KeyEvent.VK_X]) {
			Matrix3D turn = Matrix3D.rotz(-Math.PI * movementspeed);
			view = turn.transform(view);
		}
		inverseview = view.inverse();
	}

	public void run() {
		threadnum++;
		if (threadnum == 1) {
			{
				Point3D to_add = new Point3D(Math.random() - 0.5, Math.random() - 0.5, Math.random() - 0.5);
				if (to_add.dist() > 0.25) {
					to_add = to_add.scale(0.25 / to_add.dist());
				}
				points.add(new RenderPoint(to_add, Color.red));
			}

			for (int i = 0; i < globecolors - 1; i++) {
				Point3D to_add = new Point3D(Math.random() - 0.5, Math.random() - 0.5, Math.random() - 0.5);

				while (to_add.dist() < 0.5) {
					to_add = new Point3D(Math.random() - 0.5, Math.random() - 0.5, Math.random() - 0.5);
				}
				to_add = Point3D.add(to_add, points.get(i).pos);
				points.add(new RenderPoint(to_add,
						Color.getHSBColor((float) ((i + 1) * ((double) 1 / globecolors)), 1, 1)));
			}
			while (true) {
				try {
					Thread.sleep(1);
				} catch (InterruptedException e) {
				}

				graphicsupdate();
				repaint();
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

				movementupdate();

			}
		}
		if (threadnum == 3) {
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
				timemilis++;
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

	public void keyTyped(KeyEvent e) {

	}

	public void keyPressed(KeyEvent e) {
		ispressed[e.getKeyCode()] = true;
		if (e.getKeyCode() == KeyEvent.VK_E) {
			mouselocked = !mouselocked;
			if (mouselocked) {
				BufferedImage cursorImg = new BufferedImage(16, 16, BufferedImage.TYPE_INT_ARGB);
				Cursor blankCursor = Toolkit.getDefaultToolkit().createCustomCursor(cursorImg, new Point(0, 0),
						"blank cursor");
				frame.getContentPane().setCursor(blankCursor);
				try {
					new Robot().mouseMove(screenwidth / 2 + getX(), screenheight / 2 + getY());
				} catch (AWTException e1) {
				}
			} else {
				Cursor blankCursor = new Cursor(Cursor.DEFAULT_CURSOR);
				frame.getContentPane().setCursor(blankCursor);
			}
		}
		if (e.getKeyCode() == KeyEvent.VK_ESCAPE) {
			System.exit(3);
		}
	}

	public void keyReleased(KeyEvent e) {
		ispressed[e.getKeyCode()] = false;
	}

	public void mouseDragged(MouseEvent e) {
		if (mouselocked) {

			{
				Matrix3D turn = Matrix3D.roty((e.getX() - screenwidth / 2) * Math.PI * ((double) 1 / 256));
				view = turn.transform(view);
			}
			{
				Matrix3D turn = Matrix3D.rotx((e.getY() - screenheight / 2) * Math.PI * ((double) -1 / 256));
				view = turn.transform(view);
			}
			try {
				System.out.println("hi");
				new Robot().mouseMove(screenwidth / 2 + getX(), screenheight / 2 + getY());
			} catch (AWTException e1) {
				System.out.println("hi");
			}
		}

	}

	public void mouseMoved(MouseEvent e) {
		System.out.println("hi");
		if (mouselocked) {
			{
				Matrix3D turn = Matrix3D.roty((e.getX() - screenwidth / 2) * Math.PI * ((double) 1 / 256));
				view = turn.transform(view);
			}
			{
				Matrix3D turn = Matrix3D.rotx((e.getY() - screenheight / 2) * Math.PI * ((double) -1 / 256));
				view = turn.transform(view);
			}

			try {
				System.out.println("hi");
				new Robot().mouseMove(screenwidth / 2 + frame.getX() + 3, screenheight / 2 + frame.getY() + 32);
			} catch (AWTException e1) {
				System.out.println("hi");
			}
		}
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
		if (ispressed[KeyEvent.VK_CONTROL]) {
			zoom += e.getWheelRotation() * (double) 1 / 32;

		} else {
			movementspeed += e.getWheelRotation() * (double) -1 / 2048;
			if (ispressed[KeyEvent.VK_TAB]) {
				movementspeed += e.getWheelRotation() * -0.5;
			}
		}

	}

	public class RenderPoint {
		Color col;
		Point3D pos;

		public RenderPoint(Point3D Position, Color Color) {
			col = Color;
			pos = Position;
		}
	}
}
