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
import java.io.IOException;
import java.util.Arrays;

import javax.imageio.ImageIO;
import javax.swing.JFileChooser;
import javax.swing.JFrame;
import javax.swing.JPanel;

public class anim9 extends JPanel
		implements Runnable, KeyListener, MouseListener, MouseMotionListener, MouseWheelListener {
	/**
	 * 
	 */
	private static final long serialVersionUID = 9020477412984598531L;
	// content constants
	public final static double speed = 1;
	public double f = 0.029;
	public double k = 0.057;
	public double diffusionspeed = 0.25;
	// panel
	JFrame Panel = new JFrame();
	boolean onbeginning = true;
	BufferedImage start;
	// screen layers
	BufferedImage screen;
	BufferedImage fscreen;
	Graphics2D graphics;
	Graphics2D copier;
	// screen dimensions
	int screenwidth;
	int screenheight;
	int screenarea;
	int trueheight;
	int truewidth;
	// thread things
	int threadnum = 0;
	int timemilis;
	// 2 chemicals
	double[] a;
	double[] b;
	double[] newa;
	double[] newb;
	// fps values
	int fps = 0;
	int[] wait = new int[1000];
	boolean[] isHeld = new boolean[4];
	int ax = 0;
	int ay = 0;
	int mousex;
	int mousey;

	public static void main(String[] args) {
		// initializing the animation
		anim9 a = new anim9(720,480, 1080, 720);
		// staring all the threads
		new Thread(a).start();
		new Thread(a).start();
		new Thread(a).start();
		new Thread(a).start();
		new Thread(a).start();
		new Thread(a).start();
	}

	public anim9(int width, int height, int swidth, int sheight) {
		// setting values
		screenwidth = width;
		screenheight = height;
		truewidth = swidth;
		trueheight = sheight;
		screenarea = screenwidth * screenheight;
		// initializing graphics layers
		screen = new BufferedImage(width, height, BufferedImage.TYPE_INT_ARGB);
		fscreen = new BufferedImage(truewidth, trueheight, BufferedImage.TYPE_INT_ARGB);
		graphics = screen.createGraphics();
		copier = fscreen.createGraphics();
		copier.setRenderingHint(RenderingHints.KEY_INTERPOLATION, RenderingHints.VALUE_INTERPOLATION_BILINEAR);
		// setting up program values
		a = new double[screenarea];
		b = new double[screenarea];
		newa = new double[screenarea];
		newb = new double[screenarea];
		Arrays.fill(a, 1);
		Arrays.fill(b, 0);
		Arrays.fill(newa, 1);
		Arrays.fill(newb, 0);
		// setting up the window
		try {
			start = ImageIO.read(this.getClass().getClassLoader().getResource("filters/menu.png"));
		} catch (IOException e) {
		}
		Panel.add(this);
		Panel.addKeyListener(this);
		Panel.addMouseListener(this);
		Panel.addMouseMotionListener(this);
		Panel.addMouseWheelListener(this);
		Panel.setSize(truewidth + 6, trueheight + 36);
		Panel.setLocationRelativeTo(null);
		Panel.setDefaultCloseOperation(3);
		Panel.setResizable(false);
		Panel.setVisible(true);
	}

	// same thing as seed but doesn't add b but destroys it
	public void clear(int xp, int yp) {
		for (int x = -10; x < 10; x++) {
			for (int y = -10; y < 10; y++) {
				if (x * x + y * y < 100) {
					try {
						b[(x + xp) + (y + yp) * screenwidth] = 0;
						a[(x + xp) + (y + yp) * screenwidth] = 0;
						newb[(x + xp) + (y + yp) * screenwidth] = 0;
						newa[(x + xp) + (y + yp) * screenwidth] = 0;
					} catch (ArrayIndexOutOfBoundsException e) {

					}
				}
			}
		}
	}

	// adds a radius 10 ball of b
	public void createseed(int xp, int yp) {
		for (int x = -10; x < 10; x++) {
			for (int y = -10; y < 10; y++) {
				if (x * x + y * y < 100) {
					try {
						b[(x + xp) + (y + yp) * screenwidth] = 1;
						a[(x + xp) + (y + yp) * screenwidth] = 0;
					} catch (ArrayIndexOutOfBoundsException e) {

					}
				}
			}
		}
	}

	// gets value at array
	public double get(int x, int y, double[] screen) {
		return screen[clip(x, screenwidth) + clip(y, screenheight) * screenwidth];
	}

	public synchronized void paint(Graphics g) {
		// draws final image to screen
		g.drawImage(fscreen, 0, 0, null);
	}

	// graphical update
	public void graphicsupdate() {
		for (int i = 0, x = 0, y = 0; i < screenarea; i++, x = i % screenwidth, y = i / screenwidth) {
			// generates shade and applies it to image
			Color todraw = Color.getHSBColor((float) b[i],1,  1);
			screen.setRGB(x, y, todraw.getRGB());;
		}

		// finalizes the image
		copier.drawImage(screen, 0, 0, truewidth, trueheight, null);
		// writes stats
		copier.setColor(Color.BLACK);
		copier.setFont(new Font("arial", Font.PLAIN, 20));
		copier.drawString("fps:" + String.valueOf(fps), 0, 15);
		copier.drawString("feed rate:" + new StringBuffer(String.valueOf(f) + "00000").substring(0, 5), 0, 35);
		copier.drawString("kill rate:" + new StringBuffer(String.valueOf(k) + "00000").substring(0, 5), 0, 55);
		copier.drawString(
				"diffusion speed:" + new StringBuffer(String.valueOf(diffusionspeed) + "00000").substring(0, 5), 0, 75);
		// shows menu if on menu
		if (onbeginning) {
			copier.drawImage(start, 0, 0, truewidth, trueheight, null);
		}
	}

	public void contentupdate() {

		for (int i = 0, x = 0, y = 0; i < screenarea; i++, x = i % screenwidth, y = i / screenwidth) {
			// crescent world optional
			// double k = 0.045 + ((double) x / screenwidth) * 0.025;
			// double f = 0.01 + ((double) y / screenheight) * 0.09;
			// calculates laplacian for each point
			double lapa = 0;
			double lapb = 0;
			lapa -= get(x, y, a);
			lapa += 0.2 * (get(x - 1, y, a) + get(x, y - 1, a) + get(x + 1, y, a) + get(x, y + 1, a));
			lapa += 0.05 * (get(x + 1, y + 1, a) + get(x + 1, y - 1, a) + get(x - 1, y + 1, a) + get(x - 1, y - 1, a));
			lapb -= get(x, y, b);;
			lapb += 0.2 * (get(x - 1, y, b) + get(x, y - 1, b) + get(x + 1, y, b) + get(x, y + 1, b));
			lapb += 0.05 * (get(x + 1, y + 1, b) + get(x + 1, y - 1, b) + get(x - 1, y + 1, b) + get(x - 1, y - 1, b));
			// does the operation
			newa[i] = a[i] + ((lapa * diffusionspeed) - (a[i] * b[i] * b[i]) + ((1 - a[i]) * f)) * speed;
			newb[i] = b[i] + ((lapb * 0.5 * diffusionspeed) + (a[i] * b[i] * b[i]) - ((k + f) * b[i])) * speed;
		}
		// copies new grid
		a = newa;
		b = newb;
	}

	public void run() {
		threadnum++;
		// graphical update thrad
		if (threadnum == 1) {
			while (true) {
				try {
					Thread.sleep(1);
				} catch (InterruptedException e) {
				}

				graphicsupdate();
				repaint();

			}
		}
		// fps tracking thread
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
				timemilis++;
			}

		}
		// calculation threads
		if (threadnum == 3) {

			while (true) {
				try {
					Thread.sleep(1);
				} catch (InterruptedException e) {
				}
				contentupdate();
				for (int i = 0; i < 1000; i++) {
					wait[i]++;
				}
			}
		}
		if (threadnum == 4) {

			while (true) {
				try {
					Thread.sleep(1);
				} catch (InterruptedException e) {
				}
				contentupdate();
			}
		}
		if (threadnum == 5) {

			while (true) {
				try {
					Thread.sleep(1);
				} catch (InterruptedException e) {
				}
				contentupdate();
			}
		}
		if (threadnum == 6) {

			while (true) {
				try {
					Thread.sleep(1);
				} catch (InterruptedException e) {
				}
				contentupdate();
			}
		}
	}

	// clips within valid color values
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

	// pseudo mod function
	public int clip(int in, int bounds) {
		if (in < 0) {
			return bounds + in;
		}
		if (in >= bounds) {
			return in - bounds;
		}
		return in;
	}

	public void keyTyped(KeyEvent e) {
	}

	// screenshots
	public void keyPressed(KeyEvent e) {
		if (e.getKeyCode() == KeyEvent.VK_F2) {
			BufferedImage selection = new BufferedImage(screenwidth, screenheight, BufferedImage.TYPE_INT_ARGB);
			selection.createGraphics().drawImage(fscreen, 0, 0, null);
			JFileChooser chooser = new JFileChooser();
			chooser.showOpenDialog(null);
			try {
				ImageIO.write(selection, "png", chooser.getSelectedFile());
			} catch (IOException e1) {
			}
		}

		if (e.getKeyCode() == KeyEvent.VK_E) {
			Arrays.fill(b, 0);
			Arrays.fill(a, 1);
			Arrays.fill(newb, 0);
			Arrays.fill(newa, 1);
		}
		if (e.getKeyCode() == KeyEvent.VK_SPACE) {
			onbeginning = false;
		}
	}

	public void keyReleased(KeyEvent e) {
	}

	public void mouseClicked(MouseEvent e) {
	}

	// for clearing and seeding, it sets their booleans to true and does each
	// action
	public void mousePressed(MouseEvent e) {
		isHeld[e.getButton()] = true;
		if (e.getButton() == 1) {
			createseed((e.getX() - 3) * screenwidth / truewidth, (e.getY() - 32) * screenheight / trueheight);
		}
		if (e.getButton() == 3) {
			clear((e.getX() - 3) * screenwidth / truewidth, (e.getY() - 32) * screenheight / trueheight);
		}
	}

	// sets values to false
	public void mouseReleased(MouseEvent e) {
		isHeld[e.getButton()] = false;
	}

	public void mouseEntered(MouseEvent e) {
	}

	public void mouseExited(MouseEvent e) {
	}

	// does same thing as mousePressed
	public void mouseDragged(MouseEvent e) {
		if (isHeld[1]) {
			createseed((e.getX() - 3) * screenwidth / truewidth, (e.getY() - 32) * screenheight / trueheight);
		}
		if (isHeld[3]) {
			clear((e.getX() - 3) * screenwidth / truewidth, (e.getY() - 32) * screenheight / trueheight);
		}
	}

	// tracks mouse pos
	public void mouseMoved(MouseEvent e) {
		mousex = e.getX() - 3;
		mousey = e.getY() - 32;
	}

	// detects where mouse is and what to change in accordance
	public void mouseWheelMoved(MouseWheelEvent e) {
		double change = e.getWheelRotation() * -0.001;
		if ((e.getX() < 200 && e.getX() > 03) && (e.getY() < 108 && e.getY() > 48)) {
			if (e.getY() < 68) {
				f += change;
			} else if (e.getY() > 88) {
				diffusionspeed += change;
			} else {
				k += change;
			}
		}
	}
}
