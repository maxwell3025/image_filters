package filters;

import java.awt.Color;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import java.awt.event.MouseMotionListener;
import java.awt.image.BufferedImage;

import javax.swing.JFrame;

import perlin.Function;
import perlin.Perlin3D;
import vectors.Point3D;

public class anim4 extends JFrame implements Runnable, KeyListener, MouseMotionListener, MouseListener {
	/**
	 * 
	 */
	private static final long serialVersionUID = 1645563191076831704L;
	BufferedImage towrite;
	BufferedImage[] screen;
	BufferedImage effectlayer;
	Graphics2D[] graphics;
	Graphics2D copier;
	BufferedImage finalscreen;
	Graphics2D applier;
	Point3D[][][] perlinframe;
	int screenwidth;
	int screenheight;
	int screendepth;
	int arraywidth;
	int arrayheight;
	int arraydepth;
	int particlecount;
	int xshift = 0;
	int yshift = 0;
	double xgap;
	double ygap;
	double zgap;
	boolean spawning = false;
	int timemilis;
	int cycleperiod;
	int mousecurx = 0;
	int mousecury = 0;
	Function f = new Function(){
		public double calculate(double in){
			return 0.5-Math.cos(in*Math.PI)*0.5;
		}
	};

	public static void main(String[] args) {
		anim4 a = new anim4(1920, 1080, 512, 6, 4, 8, 1920, 1080, 512);
		new Thread(a).start();

	}

	public anim4(int width, int height, int depth, int arwidth, int arheight, int ardepth, int truewidth,
			int trueheight, int truedepth) {

		setDefaultCloseOperation(3);
		setResizable(false);
		screenwidth = width;
		screenheight = height;
		screendepth = depth;
		arraywidth = arwidth;
		arrayheight = arheight;
		arraydepth = ardepth;
		cycleperiod = truedepth;
		addKeyListener(this);
		addMouseMotionListener(this);
		addMouseListener(this);
		xgap = (double) screenwidth / arraywidth;
		ygap = (double) screenheight / arrayheight;
		zgap = (double) screendepth / arraydepth;
		setSize(truewidth, trueheight);
		setLocationRelativeTo(null);
		screen = new BufferedImage[depth];
		graphics = new Graphics2D[depth];
		for (int i = 0; i < depth; i++) {
			screen[i] = new BufferedImage(width, height, BufferedImage.TYPE_INT_ARGB);
			graphics[i] = screen[i].createGraphics();
		}
		towrite = new BufferedImage(width, height, BufferedImage.TYPE_INT_ARGB);
		effectlayer = new BufferedImage(getWidth(), getHeight(), BufferedImage.TYPE_INT_ARGB);
		copier = effectlayer.createGraphics();
		finalscreen = new BufferedImage(getWidth(), getHeight(), BufferedImage.TYPE_INT_ARGB);
		applier = effectlayer.createGraphics();
		perlinframe = Perlin3D.randomArray(arwidth, arheight, ardepth, 1);
		setVisible(true);
	}

	public synchronized void paint(Graphics g) {
		g.drawImage(finalscreen, 0, 0, getWidth(), getHeight(), null);
	}

	public void update() {
		int swidth = getWidth();
		int sheight = getHeight();
		BufferedImage blank = screen[(timemilis % cycleperiod) * screendepth / cycleperiod];
		BufferedImage current = new BufferedImage(getWidth(), getHeight(), BufferedImage.TYPE_INT_ARGB);
		current.createGraphics().drawImage(blank, 0, 0, swidth, sheight, null);
		copier.drawImage(current, 0, 0, xshift, yshift, swidth - xshift, sheight - yshift, swidth, sheight, null);
		copier.drawImage(current, xshift, 0, swidth, yshift, 0, sheight - yshift, swidth - xshift, sheight, null);
		copier.drawImage(current, 0, yshift, xshift, sheight, swidth - xshift, 0, swidth, sheight - yshift, null);
		copier.drawImage(current, xshift, yshift, swidth, sheight, 0, 0, swidth - xshift, sheight - yshift, null);
		applier.drawImage(effectlayer,0,0, null);
	}

	public void run() {
		for (int z = 0; z < screendepth; z++) {
			Graphics2D drawer = graphics[z];
			for (int x = 0; x < screenwidth; x++) {
				for (int y = 0; y < screenheight; y++) {
					double temp = Perlin3D.CalcPerlin3D(perlinframe, x / xgap, y / ygap, z / zgap, f);
					int shade = (int) (((temp * 0.5) + 0.5) * 255);
					drawer.setColor(new Color(clip255(shade), clip255(shade), clip255(shade)));
					drawer.drawRect(x, y, 0, 0);
				}
			}
		}

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
		if (in < 128) {
			return 0;
		}
		else{
			return 255;
		}
	}

	public void keyTyped(KeyEvent e) {

	}

	public void keyPressed(KeyEvent e) {
		if (e.getKeyCode() == KeyEvent.VK_ESCAPE) {
			System.exit(3);
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
		xshift += e.getX() - mousecurx;
		yshift += e.getY() - mousecury;
		mousecurx = e.getX();
		mousecury = e.getY();
		xshift = Perlin3D.rem(xshift, getWidth());
		yshift = Perlin3D.rem(yshift, getHeight());
	}

	public void mouseMoved(MouseEvent e) {

	}

	public void mouseClicked(MouseEvent e) {
		// TODO Auto-generated method stub

	}

	public void mousePressed(MouseEvent e) {
		mousecurx = e.getX();
		mousecury = e.getY();
	}

	public void mouseReleased(MouseEvent e) {
		// TODO Auto-generated method stub

	}

	public void mouseEntered(MouseEvent e) {
		// TODO Auto-generated method stub

	}

	public void mouseExited(MouseEvent e) {
		// TODO Auto-generated method stub

	}

}
