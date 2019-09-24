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
import javax.swing.JPanel;
import javax.swing.SwingUtilities;

import vectors.Point2D;

public class anim10 extends JPanel implements Runnable, KeyListener, MouseMotionListener, MouseListener {
	/**
	 * 
	 */
	private static final long serialVersionUID = 9020477412984598531L;
	final static float blurriness = 1f;
	final static double particlesize = 2;
	final static double acidsize = 1;
	final static double sizechange = 0.000;
	final static double movemententropy = Math.PI;
	final static double stickiness = 1;
	final static int acidlevel = 0;
	final static double dissolvechance = 0.5;
	final static int screenparticles = 128;
	final static double colorchange = (double) 1 / 8196;
	JFrame frame;
	BufferedImage screen;
	BufferedImage comblayers;
	BufferedImage fscreen;
	Graphics2D graphics;
	Graphics2D copier;
	Graphics2D layerer;
	int screenwidth;
	int screenheight;
	int particlecount;
	int fps = 0;
	int threadnum = 0;
	double xgap;
	double ygap;
	Point2D dispersalpoint;
	int[] wait = new int[1000];
	ArrayList<Grain> grains = new ArrayList<Grain>();
	ArrayList<Grain> finishedgrains = new ArrayList<Grain>();
	ArrayList<Grain> acid = new ArrayList<Grain>();
	boolean dispersing = false;
	boolean toclear = false;
	double grainnum = 1 + sizechange;
	double globalsize;
	Grain to_stick = null;
	int timemilis;
	boolean overlayshown = true;

	public static void main(String[] args) {
		anim10 a = new anim10(1080, 720);
		try {
			Thread.sleep(20);
			new Thread(a).start();
			Thread.sleep(20);
			new Thread(a).start();
			Thread.sleep(20);
			new Thread(a).start();
		} catch (InterruptedException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

	}

	public anim10(int width, int height) {
		screenwidth = width;
		screenheight = height;
		frame = new JFrame();
		frame.setDefaultCloseOperation(3);
		frame.setResizable(false);
		frame.add(this);
		this.setPreferredSize(new Dimension(screenwidth, screenheight));
		dispersalpoint = new Point2D(screenwidth/2,screenheight/2);
		addKeyListener(this);
		addMouseMotionListener(this);
		addMouseListener(this);
		frame.addKeyListener(this);
		frame.addMouseMotionListener(this);
		frame.addMouseListener(this);
		setSize(screenwidth, screenheight);
		frame.pack();
		frame.setLocationRelativeTo(null);
		screen = new BufferedImage(screenwidth, screenheight, BufferedImage.TYPE_INT_ARGB);
		fscreen = new BufferedImage(screenwidth, screenheight, BufferedImage.TYPE_INT_ARGB);
		comblayers = new BufferedImage(screenwidth, screenheight, BufferedImage.TYPE_INT_ARGB);
		graphics = screen.createGraphics();
		copier = fscreen.createGraphics();
		layerer = comblayers.createGraphics();
		frame.setVisible(true);
	}

	public synchronized void paint(Graphics g) {
		g.drawImage(fscreen, 0, 0, screenwidth, screenheight, null);
	}

	public void graphicsupdate() {
		globalsize = particlesize / grainnum;
		graphics.setColor(Color.black);
		graphics.fillRect(0, 0, screenwidth, screenheight);
		particlecount = finishedgrains.size();
		for (int i = 0; i < grains.size(); i++) {
			double radius = grains.get(i).rad;
			if (radius > 0.5) {
				graphics.setColor(Color.white);
			} else {
				graphics.setColor(
						new Color((int) (2 * radius * 255), (int) (2 * radius * 255), (int) (2 * radius * 255)));
			}
			graphics.drawOval((int) (grains.get(i).pos.x - radius), (int) (grains.get(i).pos.y - radius),
					(int) (radius * 2) - 1, (int) (radius * 2) - 1);

		}
		for (int i = 0; i < acid.size(); i++) {
			double radius = acid.get(i).rad;
			if (radius > 0.5) {
				graphics.setColor(Color.green);
			} else {
				graphics.setColor(new Color(0, (int) (2 * radius * 255), 0));
			}
			graphics.drawOval((int) (acid.get(i).pos.x - radius), (int) (acid.get(i).pos.y - radius),
					(int) (radius * 2) - 1, (int) (radius * 2) - 1);

		}
		for (int i = 0; i < finishedgrains.size(); i++) {
			Grain a = finishedgrains.get(i);
			double radius = a.rad;
			graphics.setColor(a.displayedcolor);
			for (int x = -1; x <= 1; x++) {
				for (int y = -1; y <= 1; y++) {

					graphics.fillOval((int) (a.pos.x - radius) + screenwidth * x,
							(int) (a.pos.y - radius) + screenheight * y, (int) (radius * 2) - 1,
							(int) (radius * 2) - 1);
					graphics.drawOval((int) (a.pos.x - radius) + screenwidth * x,
							(int) (a.pos.y - radius) + screenheight * y, (int) (radius * 2) - 1,
							(int) (radius * 2) - 1);
				}
			}
		}
		if (to_stick != null) {
			to_stick.rad += 0.1;
			double radius = to_stick.rad;
			graphics.setColor(Color.getHSBColor((float) (particlecount * colorchange), 1, 1));

			graphics.fillOval((int) (to_stick.pos.x - radius), (int) (to_stick.pos.y - radius), (int) (radius * 2),
					(int) (radius * 2));

			graphics.setColor(Color.getHSBColor(0.5f + (float) ((particlesize / radius) * colorchange), 1, 1));

			graphics.drawOval((int) (to_stick.pos.x - radius), (int) (to_stick.pos.y - radius), (int) (radius * 2),
					(int) (radius * 2));
		}
		layerer.setComposite(AlphaComposite.getInstance(AlphaComposite.SRC_OVER, blurriness));
		layerer.drawImage(screen, 0, 0, null);

		copier.clearRect(0, 0, screenwidth, screenheight);
		copier.drawImage(comblayers, 0, 0, null);
		if (overlayshown) {
			copier.setColor(Color.green);
			copier.setFont(new Font("arial", Font.PLAIN, 20));
			copier.drawString("particle count:" + String.valueOf(particlecount), 0, 20);
			copier.drawString("fps:" + String.valueOf(fps), 0, 40);
		}

	}

	public void contentupdate() {
		globalsize = particlesize / grainnum;
		particlecount = finishedgrains.size();
		if (toclear) {

			grains.clear();
			acid.clear();
			toclear = false;
		}
		if (dispersing && grains.size() < screenparticles) {
			grains.add(new Grain(new Point2D(dispersalpoint.x, dispersalpoint.y), globalsize, 0));
		}
		if (dispersing && acid.size() < acidlevel) {
			acid.add(new Grain(new Point2D(dispersalpoint.x, dispersalpoint.y), acidsize, 0));
		}

		for (Grain a : grains) {
			if (a.pos.x < 0) {
				a.pos.x += screenwidth;
			}
			if (a.pos.x > screenwidth) {
				a.pos.x -= screenwidth;
			}
			if (a.pos.y < 0) {
				a.pos.y += screenheight;
			}
			if (a.pos.y > screenheight) {
				a.pos.y -= screenheight;
			}
		}
		for (Grain a : acid) {
			if (a.pos.x < 0) {
				a.pos.x += screenwidth;
			}
			if (a.pos.x > screenwidth) {
				a.pos.x -= screenwidth;
			}
			if (a.pos.y < 0) {
				a.pos.y += screenheight;
			}
			if (a.pos.y > screenheight) {
				a.pos.y -= screenheight;
			}
		}
		for (int i = 0; i < grains.size(); i++) {
			for (Grain a : finishedgrains) {
				if (a.istouching(grains.get(i))) {
					if (Math.random() < stickiness) {

						// Point2D dif = Point2D.add(a.pos,
						// grains.get(i).pos.scale(-1));
						Grain sticker = grains.get(i);
						// grains.set(i,new
						// Grain(Point2D.add(a.pos,dif.scale((grains.get(i).rad+a.rad)*-1.0001/dif.dist())),
						// globalsize, 0));
						// if ((dif.angle() + Math.PI) % (Math.PI / 1.5) <
						// Math.PI / 48) {
						grainnum += sizechange;
						sticker.col = finishedgrains.get(finishedgrains.size() - 1).col + 1;
						sticker.displayedcolor = Color.getHSBColor((float) (a.col * colorchange), 1, 1);
						finishedgrains.add(sticker);
						grains.remove(i);

						// }
						i = 0;
						break;
					}
				}
			}
		}
		for (int i = 0; i < acid.size(); i++) {
			for (int j = 0; j < finishedgrains.size(); j++) {
				if (finishedgrains.get(j).istouching(acid.get(i))) {
					if (Math.random() < dissolvechance) {
						finishedgrains.remove(j);
					}
					acid.remove(i);
					j = 0;
					i = 0;
					break;
				}
			}
		}
		for (Grain b : grains) {
			b.rad = globalsize;
		}
		for (Grain a : grains) {
			a.update();
		}
		for (Grain a : acid) {
			a.update();
		}
	}

	public void run() {
		threadnum++;
		if (threadnum == 1) {
			for (double d = -Math.PI; d < Math.PI; d += Math.PI / 256) {
				double scale = Math.min(screenwidth, screenheight) / 2;
				double xdif = Math.sin(d) * scale;
				double ydif = Math.cos(d) * scale;
				Grain a = new Grain(screenwidth * 0.5 + xdif, screenheight * 0.5 + ydif, particlesize, 0);
				a.displayedcolor = Color.red;
				finishedgrains.add(a);
			}
			while (true) {
				try {
					Thread.sleep(1);
				} catch (InterruptedException e) {
				}

				graphicsupdate();
				repaint();
				timemilis++;

			}
		}
		if (threadnum == 2) {
			while (true) {
				contentupdate();
				for (int i = 0; i < 1000; i++) {
					wait[i]++;
				}
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
			dispersing = !dispersing;
		}
		if (e.getKeyCode() == KeyEvent.VK_ENTER) {
			toclear = true;
		}
		if (e.getKeyCode() == KeyEvent.VK_F3) {
			overlayshown = !overlayshown;
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

	public class Grain {
		Point2D pos;
		Point2D prevpos;
		double rad;
		double angle;
		int col;
		Color displayedcolor;

		public Grain(Point2D in, double radius, int color) {
			rad = radius;
			pos = in;
			prevpos = new Point2D(pos.x, pos.y);
			angle = Math.random() * Math.PI * 2;
			col = color;
		}

		public Grain(double x, double y, double radius, int color) {
			rad = radius;
			pos = new Point2D(x, y);
			prevpos = new Point2D(pos.x, pos.y);
			angle = Math.random() * Math.PI * 2;
		}

		public void update() {
			angle += (Math.random()-0.5)*Math.PI;
			pos.x+=Math.sin(angle);
			pos.y+=Math.cos(angle);
		}

		public boolean istouching(Grain comp) {
			return Point2D.add(pos.scale(-1), comp.pos).dist() < rad + comp.rad
					|| Point2D.add(pos.scale(-1), new Point2D(comp.pos.x + screenwidth, comp.pos.y)).dist() < rad
							+ comp.rad
					|| Point2D.add(pos.scale(-1), new Point2D(comp.pos.x - screenwidth, comp.pos.y)).dist() < rad
							+ comp.rad
					|| Point2D.add(pos.scale(-1), new Point2D(comp.pos.x, comp.pos.y + screenheight)).dist() < rad
							+ comp.rad
					|| Point2D.add(pos.scale(-1), new Point2D(comp.pos.x, comp.pos.y - screenheight)).dist() < rad
							+ comp.rad;
		}
	}

	public void mouseDragged(MouseEvent e) {
		if (SwingUtilities.isLeftMouseButton(e)) {
			dispersalpoint = new Point2D(e.getX(), e.getY());
		}

	}

	public void mouseMoved(MouseEvent e) {
		// TODO Auto-generated method stub

	}

	public void mouseClicked(MouseEvent e) {
		// TODO Auto-generated method stub

	}

	public void mousePressed(MouseEvent e) {
		if (e.getButton() == 1) {
			dispersalpoint = new Point2D(e.getX(), e.getY());
		}

	}

	public void mouseReleased(MouseEvent e) {
		if (e.getButton() == 3) {
			finishedgrains.add(to_stick);
			to_stick = null;
		}

	}

	public void mouseEntered(MouseEvent e) {
		// TODO Auto-generated method stub

	}

	public void mouseExited(MouseEvent e) {
		// TODO Auto-generated method stub

	}
}
