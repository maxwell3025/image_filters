package filters;

import java.awt.Color;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;
import java.awt.image.BufferedImage;
import java.util.ArrayList;
import java.util.List;

import javax.swing.JFrame;

import perlin.Perlin2D;
import vectors.Point2D;

public class anim1 extends JFrame implements Runnable, KeyListener {
	/**
	 * 
	 */
	private static final long serialVersionUID = -8887439612958820973L;
	BufferedImage screen;
	BufferedImage fscreen;
	BufferedImage background;
	Graphics2D drawer;
	Graphics2D copier;
	Point2D[][] perlinframe;
	int screenwidth;
	int screenheight;
	int arraywidth;
	int arrayheight;
	double xgap;
	double ygap;
	boolean spawning = false;
	List<particle> particles = new ArrayList<particle>();
	int timemilis;

	public static void main(String[] args) {
		anim1 a = new anim1(720, 480, 15, 10);
		new Thread(a).start();
	}

	public anim1(int width, int height, int arwidth, int arheight) {
		
		setDefaultCloseOperation(3);
		setResizable(false);
		screenwidth = width;
		screenheight = height;
		arraywidth = arwidth;
		arrayheight = arheight;
		addKeyListener(this);
		xgap = (double) screenwidth / arraywidth;
		ygap = (double) screenheight / arrayheight;
		setSize(screenwidth, screenheight);
		screen = new BufferedImage(width, height, BufferedImage.TYPE_INT_ARGB);
		fscreen = new BufferedImage(width, height, BufferedImage.TYPE_INT_ARGB);
		background = new BufferedImage(width, height, BufferedImage.TYPE_INT_ARGB);
		drawer = screen.createGraphics();
		copier = fscreen.createGraphics();
		perlinframe = Perlin2D.randomArray(arwidth + 1, arheight + 1, 1);
		setVisible(true);
	}

	public synchronized void paint(Graphics g) {
		g.drawImage(fscreen, 0, 0, screenwidth, screenheight, null);
	}

	public void update() {
		drawer.clearRect(0, 0, screenwidth, screenheight);
		drawer.drawImage(background, 0, 0, null);

		for (particle a : particles) {
			a.update();
			a.pos.x=rem(a.pos.x, screenwidth-0.2)+0.1;
			a.pos.y=rem(a.pos.y, screenheight-0.2)+0.1;
			double xdif=0;
			double ydif=0;
			try{
			xdif = (Perlin2D.CalcPerlin2D(perlinframe, (a.pos.x + 0.1) / xgap, a.pos.y / ygap, 1)
					- Perlin2D.CalcPerlin2D(perlinframe, (a.pos.x - 0.1) / xgap, a.pos.y / ygap, 1));
			ydif = (Perlin2D.CalcPerlin2D(perlinframe, a.pos.x / xgap, (a.pos.y + 0.1) / ygap, 1)
					- Perlin2D.CalcPerlin2D(perlinframe, a.pos.x / xgap, (a.pos.y - 0.1) / ygap, 1))+0.001;
		
				
			}catch(ArrayIndexOutOfBoundsException e){
				System.out.println(a.pos.x);
				System.out.println(a.pos.y);
				System.exit(0);
			}
			Point2D vchange = new Point2D(-ydif,xdif);
			a.vel = Point2D.add(vchange, a.vel);
			int radius = Math.max((int) (a.vel.dist() * 10), 1);
			radius = 3;
			drawer.setColor(Color.white);
			drawer.fillOval((int) a.pos.x - radius, (int) a.pos.y - radius, radius * 2 - 1, radius * 2);

		}
		copier.drawImage(screen, 0, 0, null);
	}

	public void run() {

		
		Graphics2D renderer = background.createGraphics();
		for (int x = 0; x < screenwidth; x++) {
			for (int y = 0; y < screenheight; y++) {
				double curval = Perlin2D.CalcPerlin2D(perlinframe, x / xgap, y / ygap, 1);
				renderer.setColor(Color.getHSBColor((float) ((1 + curval) * 1.5), 1, 0.5f));
				
				renderer.drawRect(x, y, 0, 0);
			}
		}
		while (true) {
			try {
				Thread.sleep(1);
			} catch (InterruptedException e) {
			}
			timemilis++;
			if (spawning) {
				particles.add(new particle(new Point2D(Math.random() * screenwidth, Math.random() * screenheight)));
			}

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
		if (e.getKeyCode() == KeyEvent.VK_SPACE) {
			spawning = !spawning;
		}
		if (e.getKeyCode() == KeyEvent.VK_ENTER) {
			for (particle a : particles) {
				a.vel = Point2D.add(new Point2D(Math.random() - 0.5, Math.random() - 0.5), a.vel);
			}
		}
	}

	public void keyReleased(KeyEvent e) {

	}
	public static double rem(double a,double b){
		double downscaled = a/b;
		double out = (downscaled-Math.floor(downscaled))*b;
		if(out<0){
			return 0;
		}if(out>b){
			return b;
		}
		return out;
	}

}
