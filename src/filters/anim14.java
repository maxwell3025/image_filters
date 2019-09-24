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
import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Comparator;

import javax.imageio.ImageIO;
import javax.swing.JFrame;
import javax.swing.JPanel;

import graphicsutil.Sprite;
import graphicsutil.Surface;
import vectors.Matrix3D;
import vectors.Point2D;
import vectors.Point3D;

public class anim14 extends JPanel
		implements Runnable, KeyListener, MouseMotionListener, MouseListener, MouseWheelListener {
	/**
	 * 
	 */
	private static final long serialVersionUID = 9020477412984598531L;
	public final static float blurriness = 1f;
	public static final int pointamount = 8192;
	public static final Comparator<Sprite> depthcomparison = new Comparator<Sprite>() {

		public int compare(Sprite o1, Sprite o2) {
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
	double speedpow = 1;
	double truespeed = Math.pow(Math.E, speedpow);
	boolean[] ispressed = new boolean[550];
	int timemilis;
	ArrayList<Sprite> points = new ArrayList<Sprite>();
	ArrayList<Sprite> adjustedpoints = new ArrayList<Sprite>();
	ArrayList<Surface> surfaces = new ArrayList<Surface>();
	ArrayList<Surface> adjustedsurfaces = new ArrayList<Surface >();
	Point3D camerabase = new Point3D(0, 0, 0);
	Matrix3D view = Matrix3D.identity;
	Matrix3D inverseview = Matrix3D.identity;
	boolean mouselocked = false;

	public static void main(String[] args) {
		@SuppressWarnings("unused")
		anim14 anim = new anim14(1080, 720, 6, 4);

	} 

	public anim14(int width, int height, int arwidth, int arheight) {
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
		graphics.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
		graphics.setRenderingHint(RenderingHints.KEY_INTERPOLATION, RenderingHints.VALUE_INTERPOLATION_BICUBIC);
		graphics.setRenderingHint(RenderingHints.KEY_RENDERING, RenderingHints.VALUE_RENDER_QUALITY);
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
		new Thread(this).start();
	}

	public synchronized void paint(Graphics g) {
		g.drawImage(fscreen, 0, 0, screenwidth, screenheight, null);
	}

	public void graphicsupdate() {
		graphics.setComposite(AlphaComposite.getInstance(AlphaComposite.SRC_OVER, 1));
		particlecount = points.size();
		graphics.setColor(new Color(0, 0, 0, 255));
		graphics.fillRect(0, 0, screenwidth, screenheight);
		for (int i = 0; i < points.size(); i++) {
			Point3D buffer = points.get(i).pos;
			buffer = Point3D.add(buffer, camerabase.scale(-1));
			buffer = view.transform(buffer);
			adjustedpoints.add(new Sprite(buffer, points.get(i).tex, points.get(i).size));
		}
		adjustedpoints.sort(depthcomparison);
		for (int i = 0; i < adjustedpoints.size(); i++) {
			Sprite todraw = adjustedpoints.get(i);
			Point3D drawpos = todraw.pos;
			if (drawpos.z > -zoom) {
				Point2D drawplace = new Point2D(smallestdimension * drawpos.x * 0.5,
						smallestdimension * drawpos.y * 0.5);
				drawplace = drawplace.scale(1 / (drawpos.z + zoom));
				drawplace = Point2D.add(new Point2D(screenwidth / 2, screenheight / 2), drawplace);
				double radius = todraw.size / (drawpos.z + zoom);
				if (radius < 8192) {
					graphics.drawImage(todraw.tex, (int) (drawplace.x - radius), (int) (drawplace.y - radius),
							(int) (2 * radius) + 1, (int) (2 * radius) + 1, null);
				}
			}
		}
		graphics.setColor(Color.red);
		graphics.drawOval(screenwidth / 2 - 5, screenheight / 2 - 5, 10, 10);
		graphics.drawRect(screenwidth / 2 - 5, screenheight / 2, 3, 0);
		graphics.drawRect(screenwidth / 2 + 2, screenheight / 2, 3, 0);
		graphics.drawRect(screenwidth / 2, screenheight / 2 - 5, 0, 3);
		graphics.drawRect(screenwidth / 2, screenheight / 2 + 2, 0, 3);
		graphics.setComposite(AlphaComposite.getInstance(AlphaComposite.SRC_OVER, 0.5f));
		double radius = 64 / zoom;
		graphics.setColor(Color.white);
		graphics.fillOval((int) (screenwidth / 2 - radius), (int) (screenheight / 2 - radius),
				(int) Math.ceil(2 * radius) + 1, (int) Math.ceil(2 * radius) + 1);
		adjustedpoints.clear();
		layerer.setColor(Color.black);
		layerer.setComposite(AlphaComposite.getInstance(AlphaComposite.SRC_OVER, blurriness));
		layerer.fillRect(0, 0, screenwidth, screenheight);
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
			camerabase = Point3D.add(camerabase, inverseview.transform(new Point3D(0, 0, truespeed)));
		}
		if (ispressed[KeyEvent.VK_S]) {
			camerabase = Point3D.add(camerabase, inverseview.transform(new Point3D(0, 0, -truespeed)));
		}
		if (ispressed[KeyEvent.VK_SPACE]) {
			camerabase = Point3D.add(camerabase, inverseview.transform(new Point3D(0, -truespeed, 0)));
		}
		if (ispressed[KeyEvent.VK_SHIFT]) {
			camerabase = Point3D.add(camerabase, inverseview.transform(new Point3D(0, truespeed, 0)));
		}
		if (ispressed[KeyEvent.VK_A]) {
			camerabase = Point3D.add(camerabase, inverseview.transform(new Point3D(-truespeed, 0, 0)));
		}
		if (ispressed[KeyEvent.VK_D]) {
			camerabase = Point3D.add(camerabase, inverseview.transform(new Point3D(truespeed, 0, 0)));
		}
		if (ispressed[KeyEvent.VK_Z]) {
			Matrix3D turn = Matrix3D.rotz(Math.PI * truespeed);
			view = turn.transform(view);
		}
		if (ispressed[KeyEvent.VK_X]) {
			Matrix3D turn = Matrix3D.rotz(-Math.PI * truespeed);
			view = turn.transform(view);
		}
		inverseview = view.inverse();
	}

	public void run() {
		threadnum++;
		if (threadnum == 1) {
			BufferedImage tex = new BufferedImage(1, 1, BufferedImage.TYPE_INT_ARGB);
			try {
				tex = ImageIO.read(new File("C:/Users/Administrator/Documents/crystal5.png"));
			} catch (IOException e1) {
			}
			for (int i = 0; i < pointamount - 1; i++) {
				Point3D pos = new Point3D((Math.random() - 0.5) * 1024, (Math.random() - 0.5) * 1024,
						(Math.random() - 0.5) * 1024);
				while (pos.x*pos.x+pos.y*pos.y+pos.x+pos.z*pos.z> 128 * 128) {
					pos = new Point3D((Math.random() - 0.5) * 1024, (Math.random() - 0.5) * 1024,
							(Math.random() - 0.5) * 1024);
				}
				points.add(new Sprite(pos, tex, Math.random() * 1));
			}
			new Thread(this).start();
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
			new Thread(this).start();
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
				new Robot().mouseMove(screenwidth / 2 + frame.getX() + 3, screenheight / 2 + frame.getY() + 32);
			} catch (AWTException e1) {
			}
		}

	}

	public void mouseMoved(MouseEvent e) {
		if (mouselocked) {
			{
				Matrix3D turn = Matrix3D.roty((e.getX() - screenwidth / 2) * Math.PI * ((double) 1 / 512));
				view = turn.transform(view);
			}
			{
				Matrix3D turn = Matrix3D.rotx((e.getY() - screenheight / 2) * Math.PI * ((double) -1 / 512));
				view = turn.transform(view);
			}

			try {
				new Robot().mouseMove(screenwidth / 2 + frame.getX() + 3, screenheight / 2 + frame.getY() + 32);
			} catch (AWTException e1) {
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
			speedpow += e.getWheelRotation() * (double) -1 / 16;

			truespeed = Math.pow(Math.E, speedpow);
		}

	}
}
